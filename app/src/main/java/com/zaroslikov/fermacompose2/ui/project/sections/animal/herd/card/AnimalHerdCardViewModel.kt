package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.toDomain
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.toUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toAnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toPregnancyProgress
import com.zaroslikov.fermacompose2.utils.PregnancyReminderScheduler
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalHerdCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalHerdRepository: AnimalHerdRepository,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,
    private val pregnancyRepository: AnimalPregnancyRepository,
    private val pregnancyReminderScheduler: PregnancyReminderScheduler,
    private val resourceProvider: ResourceProvider
) : BaseViewModel2<AnimalHerdCardState, AnimalHerdCardIntent, AnimalHerdCardReduce>(
    AnimalHerdCardState(), AnimalHerdCardReduce()
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalHerdCardDestination.itemIdPT])
    private val herdId: Long = checkNotNull(savedStateHandle[AnimalHerdCardDestination.itemId])

    init {
        loadData()
    }

    fun onIntent(intent: AnimalHerdCardIntent) {
        when (intent) {
            is AnimalHerdCardIntent.NoteChanged -> updateNote(intent.value)
            AnimalHerdCardIntent.DepersonalizeConfirmed -> depersonalize()
            AnimalHerdCardIntent.DisbandConfirmed -> disband()
            else -> sendIntent(intent)
        }
    }

    /** Сборная группа, её неархивные члены, их вес и продукция — реактивно. */
    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            val settings = settingsRepository.getSettings(itemIdPT).first()
            combine(
                animalHerdRepository.getHerd(herdId),
                animalHerdRepository.getHerdMembers(herdId),
                pregnancyRepository.getActivePregnanciesByProject(itemIdPT),
                animalHerdRepository.getHerdWeights(herdId),
                animalHerdRepository.getHerdProducts(herdId)
            ) { herd, members, pregnancies, weights, products ->
                val badges = pregnancies.toPregnancyProgress()
                val all = members.map { it.toAnimalListUi(resourceProvider).copy(pregnancyProgress = badges[it.id]) }
                val active = all.filter { !it.isArchive }
                // Расформирована или не осталось неархивных членов — в списке её больше нет
                if (herd == null || active.isEmpty()) null
                else AnimalHerdCardIntent.Loaded(
                    herd = herd.toUi(active),
                    isArchive = isArchive,
                    weight = herdWeight(active, weights, settings.weightSuffix),
                    products = herdProducts(products, settings),
                    // Продукция архивных членов (проданных, обезличенных) тоже учитывается
                    productsByMember = herdProductsByMember(all, products, settings)
                )
            }.collectLatest { loaded ->
                if (loaded == null) sendIntent(AnimalHerdCardIntent.Closed) else sendIntent(loaded)
            }
        }
    }

    private fun updateNote(note: String) {
        sendIntent(AnimalHerdCardIntent.NoteChanged(note))
        viewModelScope.launch {
            animalHerdRepository.updateHerd(getState().herd.toDomain())
        }
    }

    /**
     * Операция над сборной группой: блокирует повтор, ловит ошибки (нарушение инварианта,
     * сбой БД) и показывает снекбар. Отмену корутины не глотает.
     */
    private fun runOperation(operation: suspend () -> Unit) {
        if (getState().isOperationRunning) return
        sendIntent(AnimalHerdCardIntent.OperationRunningChanged(true))
        viewModelScope.launch {
            try {
                operation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_error))
            } finally {
                sendIntent(AnimalHerdCardIntent.OperationRunningChanged(false))
            }
        }
    }

    private fun depersonalize() {
        val state = getState()
        val individuals = state.herd.members.filter { it.sex != null }
        runOperation {
            // Активные беременности DAO прерывает в той же транзакции — их напоминания
            // снимаем после успешной операции
            val activePregnancyIds = individuals.mapNotNull { pregnancyRepository.getActivePregnancyId(it.id) }
            val count = animalHerdRepository.depersonalizeHerd(
                herdId = herdId,
                date = dateToday(),
                groupNote = resourceProvider.getString(R.string.animal_group_note_group_depersonalized)
                    .format(individuals.joinToString { it.name }),
                memberNote = resourceProvider.getString(R.string.animal_group_note_member_depersonalized)
                    .format(state.herd.name)
            )
            activePregnancyIds.forEach(pregnancyReminderScheduler::cancel)
            sendIntent(AnimalHerdCardIntent.OpenDepersonalizeDialog(false))
            showMessage(
                resourceProvider.getQuantityString(R.plurals.snackbar_animal_group_depersonalized, count, count)
            )
        }
    }

    private fun disband() {
        runOperation {
            animalHerdRepository.disbandHerd(herdId)
            sendIntent(AnimalHerdCardIntent.OpenDisbandDialog(false))
            showMessage(resourceProvider.getString(R.string.snackbar_animal_group_disbanded))
            sendIntent(AnimalHerdCardIntent.Closed)
        }
    }
}

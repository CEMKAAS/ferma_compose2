package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toAnimalListUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.toPregnancyProgress
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.StartFormEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.resolveSire
import com.zaroslikov.fermacompose2.utils.PregnancyReminderScheduler
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.mapperToBrieflyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class AnimalCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val addRepository: AddRepository,
    private val settingsRepository: SettingsRepository,
    private val projectRepository: ProjectRepository,
    private val animalHerdRepository: AnimalHerdRepository,
    private val pregnancyRepository: AnimalPregnancyRepository,
    private val pregnancyReminderScheduler: PregnancyReminderScheduler,
    private val resourceProvider: ResourceProvider
) : BaseViewModel2<AnimalCardState, AnimalCardIntent, AnimalCardReduce>(
    AnimalCardState(), AnimalCardReduce()
) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemId])

    init {
        viewModelScope.launch {
            loadData()
        }
        loadGroupData()
        loadPregnancyData()
    }

    fun onIntent(intent: AnimalCardIntent) {
        when (intent) {
            AnimalCardIntent.ArchiveAnimalPressed -> updateArchive()
            is AnimalCardIntent.ArchivePregnancyConfirmed -> archivePregnant(intent.interrupt)
            is AnimalCardIntent.NoteChanged -> updateNote(intent.value)
            AnimalCardIntent.SplitConfirmed -> splitFromCounter()
            AnimalCardIntent.LeaveConfirmed -> leaveGroup()
            is AnimalCardIntent.OpenPedigreeSheet -> openPedigreeSheet(intent)
            is AnimalCardIntent.PregnancyStartEvent ->
                if (intent.event == StartFormEvent.Confirm) confirmPregnancyStart() else sendIntent(intent)

            else -> sendIntent(intent)
        }
    }

    /** Сводка (счётчик, сборная группа) и имена проекта — реактивно. */
    private fun loadGroupData() {
        viewModelScope.launch {
            combine(
                animalRepository.getAnimalWithGroup(itemId),
                animalRepository.getAnimalNames(itemIdPT)
            ) { summary, names -> summary to names }
                .collectLatest { (summary, names) ->
                    updateState { state ->
                        state.copy(
                            summary = summary?.toAnimalListUi(resourceProvider),
                            projectAnimalNames = names
                        )
                    }
                }
        }
    }

    /** Беременность (только у самок-млекопитающих), родители и потомство — реактивно. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadPregnancyData() {
        viewModelScope.launch {
            val parents = animalRepository.getAnimal(itemId)
                .map { it.motherId to it.fatherId }
                .distinctUntilChanged()
                .flatMapLatest { (motherId, fatherId) ->
                    combine(
                        motherId?.let { pregnancyRepository.getAnimalOrNull(it) } ?: flowOf(null),
                        fatherId?.let { pregnancyRepository.getAnimalOrNull(it) } ?: flowOf(null)
                    ) { mother, father -> mother to father }
                }
            // Беременности читаем только у самок-млекопитающих (в т. ч. архивных — ради истории)
            val applicable = animalRepository.getAnimal(itemId)
                .map { if (PregnancyRules.isApplicable(it)) it.species else null }
                .distinctUntilChanged()
            launch {
                // Самцы того же вида — для листа «Случка» в карточке
                applicable.flatMapLatest { species ->
                    if (species != null) pregnancyRepository.getSireCandidates(itemIdPT, species, itemId)
                    else flowOf(emptyList())
                }.collectLatest { sires -> updateState { it.copy(sireCandidates = sires) } }
            }
            val pregnancies = applicable
                .map { it != null }
                .distinctUntilChanged()
                .flatMapLatest { isApplicable ->
                    if (isApplicable) pregnancyRepository.getPregnancies(itemId) else flowOf(emptyList())
                }
            combine(
                pregnancies,
                pregnancyRepository.getChildren(itemId),
                parents
            ) { all, children, (mother, father) ->
                val active = all.firstOrNull { it.status == PregnancyStatus.ACTIVE }
                PregnancyCardData(
                    active = active,
                    historyCount = all.count { it.id != active?.id },
                    children = children,
                    mother = mother,
                    father = father
                )
            }.collectLatest { data ->
                val active = data.active
                val norms = active?.let { PregnancyNorms.of(it.species) }
                val mating = active?.let { PregnancyDates.parseOrNull(it.matingDate) }
                updateState { state ->
                    state.copy(
                        isPregnancyLoaded = true,
                        activePregnancy = active,
                        pregnancyNorms = norms,
                        pregnancyProgress = if (norms != null && mating != null)
                            PregnancyCalculator.progress(norms, mating, LocalDate.now()) else null,
                        pregnancyHistoryCount = data.historyCount,
                        children = data.children,
                        mother = data.mother,
                        father = data.father
                    )
                }
            }
        }
    }

    /** Данные животного читаем, только пока лист «Мать»/«Отец»/«Потомок» открыт. */
    private var pedigreeJob: Job? = null

    private fun openPedigreeSheet(intent: AnimalCardIntent.OpenPedigreeSheet) {
        pedigreeJob?.cancel()
        pedigreeJob = null
        sendIntent(intent)
        val state = getState()
        val sheet = state.pedigreeSheet ?: return
        val animalId = state.pedigreeSheetAnimalId(sheet)
        if (animalId == null) {
            // Чужой отец или удалённая мать — в хозяйстве читать нечего, лист покажет имя
            sendIntent(AnimalCardIntent.PedigreeSheetLoaded(null, 0))
            return
        }
        pedigreeJob = viewModelScope.launch {
            combine(
                pregnancyRepository.getAnimalOrNull(animalId),
                pregnancyRepository.getChildren(animalId)
            ) { relative, children -> AnimalCardIntent.PedigreeSheetLoaded(relative, children.size) }
                .collectLatest { sendIntent(it) }
        }
    }

    /** Случка из карточки — те же правила и напоминания, что на экране беременности. */
    private fun confirmPregnancyStart() {
        val state = getState()
        if (!state.canConfirmPregnancyStart) return
        val form = state.pregnancyStart
        val (sireId, sireName) = form.resolveSire(state.sireCandidates) ?: return
        sendIntent(AnimalCardIntent.PregnancySavingChanged(true))
        viewModelScope.launch {
            try {
                val id = pregnancyRepository.startPregnancy(
                    DomainAnimalPregnancy(
                        animalId = itemId,
                        species = state.animal.species,
                        sireId = sireId,
                        sireName = sireName,
                        matingDate = form.matingDate,
                        status = PregnancyStatus.ACTIVE,
                        note = form.note.trim(),
                        idPT = itemIdPT
                    )
                )
                sendIntent(AnimalCardIntent.PregnancyStartEvent(StartFormEvent.Dismiss))
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_started))
                pregnancyReminderScheduler.schedule(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_error))
            } finally {
                sendIntent(AnimalCardIntent.PregnancySavingChanged(false))
            }
        }
    }

    /**
     * Выполняет операцию над группой: блокирует повтор, ловит ошибки (нарушение инварианта,
     * сбой БД) и показывает снекбар. Отмену корутины не глотает.
     */
    private fun runGroupOperation(operation: suspend () -> Unit) {
        if (getState().isGroupOperationRunning) return
        updateState { it.copy(isGroupOperationRunning = true) }
        viewModelScope.launch {
            try {
                operation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_error))
            } finally {
                updateState { it.copy(isGroupOperationRunning = false) }
            }
        }
    }

    private fun splitFromCounter() {
        val state = getState()
        if (!state.canConfirmSplit) return
        val names = state.splitNames
        runGroupOperation {
            animalHerdRepository.splitFromCounter(
                groupId = itemId,
                names = names,
                sex = state.split.sex,
                date = dateToday(),
                groupNote = resourceProvider.getString(R.string.animal_group_note_group_split)
                    .format(names.joinToString()),
                memberNote = resourceProvider.getString(R.string.animal_group_note_member_split)
                    .format(state.animal.name)
            )
            sendIntent(AnimalCardIntent.OpenSplitDialog(false))
            showMessage(
                resourceProvider.getQuantityString(R.plurals.snackbar_animal_group_split, names.size, names.size)
            )
        }
    }

    private fun leaveGroup() {
        val name = getState().animal.name
        runGroupOperation {
            animalHerdRepository.removeMember(itemId)
            sendIntent(AnimalCardIntent.OpenLeaveDialog(false))
            showMessage(resourceProvider.getString(R.string.snackbar_animal_group_left).format(name))
        }
    }

    private suspend fun loadData() {
        val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalSizeRepository.getSizeAnimalLimit(itemId),
            animalVaccinationRepository.getVaccinationAnimalLimit(itemId),
            animalWeightRepository.getWeightAnimalLimit(itemId),
            addRepository.getProductAnimal(itemId),
            settingsRepository.getSettings(itemIdPT)
        ) { list ->
            val productList = productsGetFromAnimalList(
                list[5] as List<DomainAnimalCountSuffix>,
                list[6] as DomainSettings
            )

            LoadDataAnimalCard(
                list[0] as DomainAnimalTable,
                list[1] as DomainAnimalCount?, list[2] as DomainAnimalSize?,
                list[3] as DomainAnimalVaccination?, list[4] as DomainAnimalWeight?,
                productList,
                list[6] as DomainSettings
            )
        }.collectLatest { data ->
            updateState { animal ->
                animal.copy(
                    isLoading = false,
                    animal = data.animal,
                    countAnimal = data.count,
                    size = data.size,
                    vaccination = data.vacc,
                    weight = data.weight,
                    itemId = itemId,
                    itemIdPT = itemIdPT,
                    productList = data.productList,
                    settings = data.settings,
                    isArchive = isArchive || data.animal.archive
                )
            }
        }
    }

    private fun updateNote(note: String) {
        viewModelScope.launch {
            sendIntent(AnimalCardIntent.NoteChanged(note))
            animalRepository.updateAnimalTable(getState().animal)
        }
    }

    private fun updateArchive() {
        // Беременную самку сначала спрашиваем: прервать беременность или только в архив
        if (getState().activePregnancy != null) {
            sendIntent(AnimalCardIntent.OpenArchivePregnancyDialog(true))
            return
        }
        viewModelScope.launch { archiveAnimal() }
    }

    private suspend fun archiveAnimal() {
        sendIntent(AnimalCardIntent.ArchiveAnimalPressed)
        animalRepository.updateAnimalTable(getState().animal)
    }

    /** [interrupt] — гибель, забой или продажа самки: беременность прерывается, напоминания снимаются. */
    private fun archivePregnant(interrupt: Boolean) {
        val active = getState().activePregnancy
        sendIntent(AnimalCardIntent.ArchivePregnancyConfirmed(interrupt))
        viewModelScope.launch {
            try {
                if (interrupt && active != null) {
                    // Прерывание и архивация — одной транзакцией
                    pregnancyRepository.interruptAndArchive(
                        pregnancyId = active.id,
                        reason = PregnancyInterruptReason.FEMALE_LOST,
                        date = dateToday(),
                        note = active.note,
                        animalId = itemId
                    )
                    pregnancyReminderScheduler.cancel(active.id)
                    sendIntent(AnimalCardIntent.ArchiveAnimalPressed)
                } else {
                    archiveAnimal()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_error))
            }
        }
    }

    private fun productsGetFromAnimalList(
        productList: List<DomainAnimalCountSuffix>,
        domainSettings: DomainSettings
    ): List<BrieflyItem> {
        return productList
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings = domainSettings)
            }
    }
}
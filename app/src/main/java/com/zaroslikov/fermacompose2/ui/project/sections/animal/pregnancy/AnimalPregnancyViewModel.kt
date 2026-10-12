package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.animal.pregnancy.LitterMode
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyBirth
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.PregnancyReminderScheduler
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimalPregnancyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val pregnancyRepository: AnimalPregnancyRepository,
    private val projectRepository: ProjectRepository,
    private val reminderScheduler: PregnancyReminderScheduler,
    private val resourceProvider: ResourceProvider
) : BaseViewModel2<AnimalPregnancyState, AnimalPregnancyIntent, AnimalPregnancyReduce>(
    AnimalPregnancyState(), AnimalPregnancyReduce()
) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalPregnancyDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalPregnancyDestination.itemId])

    init {
        updateState {
            it.copy(
                itemIdPT = itemIdPT,
                itemId = itemId,
                groupNameTemplate = resourceProvider.getString(R.string.pregnancy_litter_group_default)
            )
        }
        loadData()
    }

    fun onIntent(intent: AnimalPregnancyIntent) {
        when (intent) {
            AnimalPregnancyIntent.StartConfirmed -> confirmStart()
            AnimalPregnancyIntent.BirthConfirmed -> confirmBirth()
            AnimalPregnancyIntent.InterruptConfirmed -> confirmInterrupt()
            AnimalPregnancyIntent.EditNoteConfirmed -> confirmEditNote()
            AnimalPregnancyIntent.DeleteConfirmed -> confirmDelete()
            AnimalPregnancyIntent.DayNoteConfirmed -> confirmDayNote()
            AnimalPregnancyIntent.DayNoteDeleted -> deleteDayNote()
            is AnimalPregnancyIntent.OpenSireSheet -> openSireSheet(intent)
            else -> sendIntent(intent)
        }
    }

    /** Данные самца читаем, только пока лист «Отец» открыт. */
    private var sireJob: Job? = null

    private fun openSireSheet(intent: AnimalPregnancyIntent.OpenSireSheet) {
        sireJob?.cancel()
        sireJob = null
        sendIntent(intent)
        if (!intent.value) return
        val sireId = getState().active?.sireId
        if (sireId == null) {
            // Чужой производитель — в хозяйстве его нет, читать нечего
            sendIntent(AnimalPregnancyIntent.SireDetailsLoaded(null, 0))
            return
        }
        sireJob = viewModelScope.launch {
            combine(
                pregnancyRepository.getAnimalOrNull(sireId),
                pregnancyRepository.getChildren(sireId)
            ) { sire, children -> AnimalPregnancyIntent.SireDetailsLoaded(sire, children.size) }
                .collectLatest { sendIntent(it) }
        }
    }

    private fun loadData() {
        val animalFlow = animalRepository.getAnimal(itemId)
        val sireFlow = animalFlow
            .map { it.species }
            .distinctUntilChanged()
            .flatMapLatest { species -> pregnancyRepository.getSireCandidates(itemIdPT, species, itemId) }
        viewModelScope.launch {
            // combine принимает не больше пяти потоков — беременности и заметки идут парой
            val pregnanciesFlow = combine(
                pregnancyRepository.getPregnancies(itemId),
                pregnancyRepository.getNotesByAnimal(itemId)
            ) { pregnancies, notes -> pregnancies to notes }
            combine(
                animalFlow,
                pregnanciesFlow,
                animalRepository.getAnimalNames(itemIdPT),
                sireFlow,
                projectRepository.getIsArchiveProject(itemIdPT)
            ) { animal, (pregnancies, notes), names, sires, isProjectArchive ->
                AnimalPregnancyIntent.RefreshData(
                    animal = animal,
                    pregnancies = pregnancies,
                    dayNotes = notes,
                    projectAnimalNames = names,
                    sireCandidates = sires,
                    isProjectArchive = isProjectArchive,
                    today = LocalDate.now()
                )
            }.collectLatest { sendIntent(it) }
        }
    }

    /**
     * Выполняет операцию над беременностью: блокирует повторное нажатие, ловит нарушение правил
     * (IllegalStateException из транзакции) и сбои БД, показывает снекбар. Отмену не глотает.
     */
    private fun runOperation(operation: suspend () -> Unit) {
        if (getState().isSaving) return
        sendIntent(AnimalPregnancyIntent.SavingChanged(true))
        viewModelScope.launch {
            try {
                operation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_error))
            } finally {
                sendIntent(AnimalPregnancyIntent.SavingChanged(false))
            }
        }
    }

    private fun confirmStart() {
        val state = getState()
        if (!state.canConfirmStart) return
        val form = state.start
        val (sireId, sireName) = form.resolveSire(
            state.sireCandidates, state.editingSireId, state.editingSireName
        ) ?: return
        val editingId = form.editingId
        if (editingId == null) {
            runOperation {
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
                sendIntent(AnimalPregnancyIntent.OpenStart(false))
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_started))
                reminderScheduler.schedule(id)
            }
        } else {
            val current = state.pregnancyById[editingId] ?: return
            val updated = if (current.status == PregnancyStatus.ACTIVE)
                current.copy(
                    matingDate = form.matingDate,
                    sireId = sireId,
                    sireName = sireName,
                    note = form.note.trim()
                )
            else current.copy(note = form.note.trim())
            // Напоминания зависят только от даты случки: правка самца/заметки их не трогает
            val matingChanged = current.status == PregnancyStatus.ACTIVE &&
                    updated.matingDate != current.matingDate
            runOperation {
                pregnancyRepository.updatePregnancy(updated)
                sendIntent(AnimalPregnancyIntent.OpenStart(false))
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_updated))
                if (matingChanged) reminderScheduler.schedule(current.id)
            }
        }
    }

    private fun confirmBirth() {
        val state = getState()
        if (!state.canConfirmBirth) return
        val active = state.active ?: return
        val form = state.birth
        val female = state.bornFemale ?: return
        val male = state.bornMale ?: return
        val stillborn = state.bornStillborn ?: return
        val alive = female + male
        val birth = PregnancyBirth(
            pregnancyId = active.id,
            date = form.date,
            female = female,
            male = male,
            stillborn = stillborn,
            mode = form.mode,
            names = if (form.mode == LitterMode.INDIVIDUALS) state.birthNames else emptyList(),
            groupName = form.groupName.trim(),
            countNote = resourceProvider.getString(R.string.pregnancy_count_note).format(state.animal.name),
            note = form.note.trim()
        )
        runOperation {
            pregnancyRepository.registerBirth(birth)
            reminderScheduler.cancel(active.id)
            sendIntent(AnimalPregnancyIntent.OpenBirth(false))
            showMessage(
                if (alive == 0) resourceProvider.getString(R.string.pregnancy_snackbar_birth_none)
                else resourceProvider.getQuantityString(R.plurals.pregnancy_snackbar_birth, alive, alive)
            )
            // Приплод и родословная видны в карточке матери — возвращаемся туда
            navigateTo(UiEvent.NavigateBack)
        }
    }

    private fun confirmInterrupt() {
        val state = getState()
        if (!state.canConfirmInterrupt) return
        val active = state.active ?: return
        val form = state.interrupt
        val reason = form.reason ?: return
        runOperation {
            pregnancyRepository.interrupt(active.id, form.date, reason, form.note.trim())
            reminderScheduler.cancel(active.id)
            sendIntent(AnimalPregnancyIntent.OpenInterrupt(false))
            showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_interrupted))
        }
    }

    private fun confirmEditNote() {
        val state = getState()
        val (id, note) = state.editNote ?: return
        if (state.isReadOnly) return
        val current = state.pregnancyById[id] ?: return
        runOperation {
            pregnancyRepository.updatePregnancy(current.copy(note = note.trim()))
            sendIntent(AnimalPregnancyIntent.OpenEditNote(null))
            showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_updated))
        }
    }

    private fun confirmDayNote() {
        val state = getState()
        if (!state.canConfirmDayNote) return
        val form = state.dayNote
        val note = DomainAnimalPregnancyNote(
            id = form.editingId ?: 0,
            pregnancyId = form.pregnancyId,
            date = form.date,
            time = form.time.ifBlank { null },
            note = form.text.trim()
        )
        runOperation {
            if (form.editingId == null) pregnancyRepository.insertNote(note)
            else pregnancyRepository.updateNote(note)
            sendIntent(AnimalPregnancyIntent.OpenDayNote(false))
            showMessage(
                resourceProvider.getString(
                    if (form.editingId == null) R.string.pregnancy_day_note_snackbar_added
                    else R.string.pregnancy_snackbar_updated
                )
            )
        }
    }

    private fun deleteDayNote() {
        val state = getState()
        val id = state.dayNote.editingId ?: return
        // Удаляем только после подтверждения
        if (state.isReadOnly || !state.dayNote.isDeleteConfirm) return
        runOperation {
            pregnancyRepository.deleteNote(id)
            sendIntent(AnimalPregnancyIntent.OpenDayNote(false))
            showMessage(resourceProvider.getString(R.string.pregnancy_day_note_snackbar_deleted))
        }
    }

    private fun confirmDelete() {
        val state = getState()
        val id = state.deleteId ?: return
        if (state.isReadOnly) return
        val wasActive = state.pregnancyById[id]?.status == PregnancyStatus.ACTIVE
        runOperation {
            pregnancyRepository.deletePregnancy(id)
            if (wasActive) reminderScheduler.cancel(id)
            sendIntent(AnimalPregnancyIntent.OpenDelete(null))
            sendIntent(AnimalPregnancyIntent.OpenStart(false))
            sendIntent(AnimalPregnancyIntent.OpenEditNote(null))
            showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_deleted))
        }
    }
}

package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalPregnancyReduce : BaseReducer<AnimalPregnancyState, AnimalPregnancyIntent>() {
    override fun reducer(
        state: AnimalPregnancyState,
        intent: AnimalPregnancyIntent
    ): AnimalPregnancyState {
        return when (intent) {
            is AnimalPregnancyIntent.RefreshData -> state.refresh(intent)
            is AnimalPregnancyIntent.SavingChanged -> state.copy(isSaving = intent.value)

            is AnimalPregnancyIntent.OpenStart -> state.openStart(intent.value)
            is AnimalPregnancyIntent.OpenEdit -> state.openEdit(intent.id)
            is AnimalPregnancyIntent.StartFormChanged -> state.copy(start = state.start.reduce(intent.event))

            is AnimalPregnancyIntent.OpenSireSheet -> state.copy(
                isSireSheetOpen = intent.value && state.active != null,
                sireDetails = null,
                sireChildrenCount = 0,
                isSireDetailsLoaded = false
            )

            is AnimalPregnancyIntent.SireDetailsLoaded -> state.copy(
                sireDetails = intent.animal,
                sireChildrenCount = intent.childrenCount,
                isSireDetailsLoaded = true
            )

            is AnimalPregnancyIntent.OpenBirth -> state.openBirth(intent.value)
            is AnimalPregnancyIntent.BirthDateChanged -> state.copy(
                birth = state.birth.copy(
                    date = intent.value,
                    groupName = if (state.birth.isGroupNameEdited) state.birth.groupName
                    else state.defaultGroupName(intent.value)
                )
            )

            is AnimalPregnancyIntent.FemaleChanged -> state.copy(birth = state.birth.copy(female = intent.value))
            is AnimalPregnancyIntent.MaleChanged -> state.copy(birth = state.birth.copy(male = intent.value))
            is AnimalPregnancyIntent.StillbornChanged -> state.copy(birth = state.birth.copy(stillborn = intent.value))
            is AnimalPregnancyIntent.LitterModeChanged -> state.copy(birth = state.birth.copy(mode = intent.value))
            is AnimalPregnancyIntent.PrefixChanged -> state.copy(birth = state.birth.copy(prefix = intent.value))
            is AnimalPregnancyIntent.GroupNameChanged -> state.copy(
                birth = state.birth.copy(groupName = intent.value, isGroupNameEdited = true)
            )

            is AnimalPregnancyIntent.BirthNoteChanged -> state.copy(birth = state.birth.copy(note = intent.value))

            is AnimalPregnancyIntent.OpenInterrupt -> state.openInterrupt(intent.value)
            is AnimalPregnancyIntent.InterruptDateChanged -> state.copy(interrupt = state.interrupt.copy(date = intent.value))
            is AnimalPregnancyIntent.ReasonChanged -> state.copy(interrupt = state.interrupt.copy(reason = intent.value))
            is AnimalPregnancyIntent.InterruptNoteChanged -> state.copy(interrupt = state.interrupt.copy(note = intent.value))

            is AnimalPregnancyIntent.OpenEditNote -> state.copy(
                editNote = intent.id?.let { id -> state.pregnancyById[id]?.let { it.id to it.note } }
            )

            is AnimalPregnancyIntent.EditNoteChanged -> state.copy(
                editNote = state.editNote?.copy(second = intent.value)
            )

            is AnimalPregnancyIntent.OpenDelete -> state.copy(deleteId = intent.id)

            is AnimalPregnancyIntent.OpenDayNote -> state.openDayNote(intent.value)
            is AnimalPregnancyIntent.OpenEditDayNote -> state.openEditDayNote(intent.id)
            is AnimalPregnancyIntent.DayNoteDateChanged -> state.copy(dayNote = state.dayNote.copy(date = intent.value))
            is AnimalPregnancyIntent.DayNoteTimeChanged -> state.copy(dayNote = state.dayNote.copy(time = intent.value))
            is AnimalPregnancyIntent.DayNoteTextChanged -> state.copy(dayNote = state.dayNote.copy(text = intent.value))
            is AnimalPregnancyIntent.OpenDayNoteDelete -> state.copy(
                dayNote = state.dayNote.copy(
                    isDeleteConfirm = intent.value && state.dayNote.editingId != null
                )
            )

            AnimalPregnancyIntent.DayNoteConfirmed,
            AnimalPregnancyIntent.DayNoteDeleted,
            AnimalPregnancyIntent.StartConfirmed,
            AnimalPregnancyIntent.BirthConfirmed,
            AnimalPregnancyIntent.InterruptConfirmed,
            AnimalPregnancyIntent.EditNoteConfirmed,
            AnimalPregnancyIntent.DeleteConfirmed -> state
        }
    }

    private fun AnimalPregnancyState.refresh(intent: AnimalPregnancyIntent.RefreshData): AnimalPregnancyState {
        val active = intent.pregnancies.firstOrNull { it.status == PregnancyStatus.ACTIVE }
        val norms = PregnancyNorms.of(active?.species ?: intent.animal.species)
        val progress = if (active != null && norms != null)
            PregnancyDates.parseOrNull(active.matingDate)?.let {
                PregnancyCalculator.progress(norms, it, intent.today)
            }
        else null
        // Запись, открытая в листе, исчезла (удалена в другом месте) — закрываем лист
        val ids = intent.pregnancies.map { it.id }.toSet()
        val noteIds = intent.dayNotes.map { it.id }.toSet()
        // Заметку удалили в другом месте или её беременность пропала — закрываем лист
        val isDayNoteGone = dayNote.isOpen && (dayNote.pregnancyId !in ids ||
                (dayNote.editingId != null && dayNote.editingId !in noteIds))
        return copy(
            dayNotes = intent.dayNotes.groupBy { it.pregnancyId },
            dayNote = if (isDayNoteGone) DayNoteForm() else dayNote,
            isLoading = false,
            today = intent.today,
            animal = intent.animal,
            norms = norms,
            active = active,
            progress = progress,
            history = intent.pregnancies.filter { it.id != active?.id },
            sireCandidates = intent.sireCandidates,
            projectAnimalNames = intent.projectAnimalNames,
            isReadOnly = intent.isProjectArchive,
            startBlock = PregnancyRules.startBlock(intent.animal, active != null),
            birth = if (active == null) BirthForm() else birth,
            interrupt = if (active == null) InterruptForm() else interrupt,
            start = if (start.editingId != null && start.editingId !in ids) StartForm() else start,
            editNote = editNote?.takeIf { it.first in ids },
            deleteId = deleteId?.takeIf { it in ids },
            isSireSheetOpen = isSireSheetOpen && active != null
        )
    }

    private fun AnimalPregnancyState.openStart(open: Boolean): AnimalPregnancyState {
        if (!open) return copy(start = StartForm())
        if (!canStart) return this
        return copy(start = newStartForm(today))
    }

    private fun AnimalPregnancyState.openEdit(id: Long): AnimalPregnancyState {
        if (!canEdit) return this
        val p = pregnancyById[id] ?: return this
        val mode = when {
            p.sireId != null -> SireMode.FARM
            !p.sireName.isNullOrBlank() -> SireMode.EXTERNAL
            else -> SireMode.NONE
        }
        return copy(
            start = StartForm(
                isOpen = true,
                editingId = p.id,
                matingDate = p.matingDate,
                sireMode = mode,
                sireId = p.sireId,
                sireName = if (mode == SireMode.EXTERNAL) p.sireName.orEmpty() else "",
                note = p.note
            )
        )
    }

    private fun AnimalPregnancyState.openBirth(open: Boolean): AnimalPregnancyState {
        if (!open) return copy(birth = BirthForm())
        val active = active ?: return this
        val norms = norms ?: return this
        if (!canBirth) return this
        val date = PregnancyDates.format(today)
        return copy(
            birth = BirthForm(
                isOpen = true,
                date = date,
                prefix = norms.terms.youngSingular,
                groupName = defaultGroupName(date),
                note = active.note
            )
        )
    }

    private fun AnimalPregnancyState.openInterrupt(open: Boolean): AnimalPregnancyState {
        if (!open) return copy(interrupt = InterruptForm())
        val active = active ?: return this
        if (!canInterrupt) return this
        return copy(
            interrupt = InterruptForm(
                isOpen = true,
                date = PregnancyDates.format(today),
                note = active.note
            )
        )
    }

    /** Новая заметка — к активной беременности, за сегодня. */
    private fun AnimalPregnancyState.openDayNote(open: Boolean): AnimalPregnancyState {
        if (!open) return copy(dayNote = DayNoteForm())
        val active = active ?: return this
        if (!canAddDayNote) return this
        return copy(
            dayNote = DayNoteForm(
                isOpen = true,
                pregnancyId = active.id,
                date = PregnancyDates.format(today)
            )
        )
    }

    private fun AnimalPregnancyState.openEditDayNote(id: Long): AnimalPregnancyState {
        if (!canEdit) return this
        val note = dayNotes.values.firstNotNullOfOrNull { list -> list.firstOrNull { it.id == id } }
            ?: return this
        return copy(
            dayNote = DayNoteForm(
                isOpen = true,
                editingId = note.id,
                pregnancyId = note.pregnancyId,
                date = note.date,
                time = note.time.orEmpty(),
                text = note.note
            )
        )
    }

    /** «Крольчата (Маруся, 06.10.2026)». */
    private fun AnimalPregnancyState.defaultGroupName(date: String): String {
        val young = norms?.terms?.youngPlural ?: animal.type
        return if (groupNameTemplate.isBlank()) young
        else groupNameTemplate.format(young, animal.name, date)
    }
}

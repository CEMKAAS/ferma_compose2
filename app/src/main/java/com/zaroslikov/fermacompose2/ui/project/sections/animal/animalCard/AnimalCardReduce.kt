package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard


import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.StartForm
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.StartFormEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.newStartForm
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.reduce
import java.time.LocalDate

class AnimalCardReduce : BaseReducer<AnimalCardState, AnimalCardIntent>() {
    override fun reducer(
        state: AnimalCardState,
        intent: AnimalCardIntent
    ): AnimalCardState {
        return when (intent) {
            is AnimalCardIntent.NoteChanged -> state.updateNote(intent.value)
            is AnimalCardIntent.OpenArchiveDialogClicked -> state.updateOpenArchiveDialog(intent.value)
            is AnimalCardIntent.ArchiveAnimalPressed -> state.updateArchive()
            is AnimalCardIntent.OpenArchivePregnancyDialog ->
                state.copy(isOpenArchivePregnancyDialog = intent.value)

            is AnimalCardIntent.ArchivePregnancyConfirmed -> state.copy(isOpenArchivePregnancyDialog = false)

            AnimalCardIntent.OpenPregnancyStart ->
                if (state.canStartPregnancy) state.copy(pregnancyStart = newStartForm(LocalDate.now()))
                else state

            is AnimalCardIntent.PregnancyStartEvent -> state.copy(
                pregnancyStart = if (intent.event == StartFormEvent.Dismiss) StartForm()
                else state.pregnancyStart.reduce(intent.event)
            )

            is AnimalCardIntent.PregnancySavingChanged -> state.copy(isPregnancySaving = intent.value)

            is AnimalCardIntent.OpenPedigreeSheet -> state.copy(
                pedigreeSheet = intent.sheet?.takeIf { state.canOpen(it) },
                pedigreeSheetAnimal = null,
                pedigreeSheetChildrenCount = 0,
                isPedigreeSheetLoaded = false
            )

            is AnimalCardIntent.PedigreeSheetLoaded -> state.copy(
                pedigreeSheetAnimal = intent.animal,
                pedigreeSheetChildrenCount = intent.childrenCount,
                isPedigreeSheetLoaded = true
            )

            is AnimalCardIntent.OpenSplitDialog -> state.copy(
                split = if (intent.value) SplitDialogState(
                    isOpen = true,
                    prefix = state.animal.type.ifBlank { state.animal.name }
                ) else SplitDialogState()
            )

            is AnimalCardIntent.SplitCountChanged -> state.copy(split = state.split.copy(count = intent.value))
            is AnimalCardIntent.SplitPrefixChanged -> state.copy(split = state.split.copy(prefix = intent.value))
            is AnimalCardIntent.SplitSexChanged -> state.copy(split = state.split.copy(sex = intent.value))

            is AnimalCardIntent.OpenLeaveDialog -> state.copy(isOpenLeaveDialog = intent.value)

            AnimalCardIntent.SplitConfirmed,
            AnimalCardIntent.LeaveConfirmed -> state
        }
    }

    private fun AnimalCardState.updateNote(note: String): AnimalCardState {
        return copy(
            animal = animal.copy(
                note = note
            )
        )
    }

    private fun AnimalCardState.updateOpenArchiveDialog(isOpenArchiveDialog: Boolean): AnimalCardState {
        return copy(
            isOpenArchiveDialog = isOpenArchiveDialog
        )
    }

    private fun AnimalCardState.updateArchive(): AnimalCardState {
        return copy(
            animal = animal.copy(
                archive = true
            )
        )
    }
}

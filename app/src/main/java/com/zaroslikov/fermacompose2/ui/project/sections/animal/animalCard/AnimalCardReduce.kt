package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard


import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalCardReduce : BaseReducer<AnimalCardState, AnimalCardIntent>() {
    override fun reducer(
        state: AnimalCardState,
        intent: AnimalCardIntent
    ): AnimalCardState {
        return when (intent) {
            is AnimalCardIntent.NoteChanged -> state.updateNote(intent.value)
            is AnimalCardIntent.OpenArchiveDialogClicked -> state.updateOpenArchiveDialog(intent.value)
            is AnimalCardIntent.ArchiveAnimalPressed -> state.updateArchive()
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

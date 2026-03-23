package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AnimalCardIntent : BaseIntent {
    data class NoteChanged(val value: String) : AnimalCardIntent()
    data class OpenArchiveDialogClicked(val value: Boolean) : AnimalCardIntent()
    data object ArchiveAnimalPressed : AnimalCardIntent()
}

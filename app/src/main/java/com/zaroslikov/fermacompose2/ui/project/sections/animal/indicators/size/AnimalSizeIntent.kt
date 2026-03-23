package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AnimalSizeIntent : BaseIntent {
    data class OpenDialogClicked(
        val isEntry: Boolean,
        val domainAnimalSize: AnimalSizeUi? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalSizeIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: CurrentAnimalSize,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalSizeIntent()

    data class SizeChanged(val value: String) : AnimalSizeIntent()
    data class SuffixClicked(val value: Suffix) : AnimalSizeIntent()
    data class DateClicked(val value: String) : AnimalSizeIntent()
    data class NoteChanged(val value: String) : AnimalSizeIntent()
    data object InsertPressed : AnimalSizeIntent()
    data object UpdatePressed : AnimalSizeIntent()
    data class DeletePressed(val value: Long) : AnimalSizeIntent()
}
package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent


sealed class AnimalWeightIntent : BaseIntent {

    data class OpenDialogClicked(
        val isEntry: Boolean,
        val animalWeightUi: AnimalWeightUi? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalWeightIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: CurrentAnimalWeight,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalWeightIntent()


    data class WeightChanged(val value: String) : AnimalWeightIntent()
    data class SuffixClicked(val value: Suffix) : AnimalWeightIntent()
    data class DateClicked(val value: String) : AnimalWeightIntent()
    data class NoteChanged(val value: String) : AnimalWeightIntent()
    data object InsertPressed : AnimalWeightIntent()
    data object UpdatePressed : AnimalWeightIntent()
    data class DeletePressed(val value: Long) : AnimalWeightIntent()
}
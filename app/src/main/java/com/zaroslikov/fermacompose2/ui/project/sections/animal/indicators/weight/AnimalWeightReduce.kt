package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalWeightReduce : BaseReducer<AnimalWeightState, AnimalWeightIntent>() {
    override fun reducer(
        state: AnimalWeightState,
        intent: AnimalWeightIntent
    ): AnimalWeightState {
        return when (intent) {
            is AnimalWeightIntent.RefreshEntryBottomSheetState -> state.updateRefreshEntry(
                intent.isOpen,
                intent.state, intent.isSaveStateForBottomSheet
            ).updateValid()
            is AnimalWeightIntent.WeightChanged -> state.updateWeight(intent.value).updateValid()
            is AnimalWeightIntent.DateClicked -> state.updateDate(intent.value)
            is AnimalWeightIntent.NoteChanged -> state.updateNote(intent.value)
            is AnimalWeightIntent.SuffixClicked -> state.updateSuffix(intent.value)
            else -> state
        }
    }

    private fun AnimalWeightState.updateRefreshEntry(
        isOpenEntryBottomSheet: Boolean,
        currentAnimalWeight: CurrentAnimalWeight,
        isSaveStateForEntry: Boolean
    ): AnimalWeightState {
        return copy(
            isOpenEntryBottomSheet = isOpenEntryBottomSheet,
            currentProduct = currentAnimalWeight,
            isSaveStateForEntry = isSaveStateForEntry
        )
    }

    private fun AnimalWeightState.updateValid(): AnimalWeightState {
        val baseValid = currentProduct.weight.isNotBlank()
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AnimalWeightState.updateSuffix(suffix: Suffix): AnimalWeightState {
        return copy(
            currentProduct = currentProduct.copy(
                suffix = suffix
            )
        )
    }

    private fun AnimalWeightState.updateWeight(weight: String): AnimalWeightState {
        return copy(
            currentProduct = currentProduct.copy(
                weight = weight,
                error = currentProduct.error.copy(
                    isErrorWeight = weight.isBlank()
                )
            )
        )
    }

    private fun AnimalWeightState.updateDate(date: String): AnimalWeightState {
        return copy(
            currentProduct = currentProduct.copy(
                date = date
            )
        )
    }

    private fun AnimalWeightState.updateNote(note: String): AnimalWeightState {
        return copy(
            currentProduct = currentProduct.copy(
                note = note
            )
        )
    }
}
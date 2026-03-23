package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalSizeReduce : BaseReducer<AnimalSizeState, AnimalSizeIntent>() {
    override fun reducer(
        state: AnimalSizeState,
        intent: AnimalSizeIntent
    ): AnimalSizeState {
        return when (intent) {
            is AnimalSizeIntent.RefreshEntryBottomSheetState -> state.updateRefreshEntry(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet
            ).updateValid()

            is AnimalSizeIntent.SizeChanged -> state.updateSize(intent.value).updateValid()
            is AnimalSizeIntent.DateClicked -> state.updateDate(intent.value)
            is AnimalSizeIntent.NoteChanged -> state.updateNote(intent.value)
            is AnimalSizeIntent.SuffixClicked -> state.updateSuffix(intent.value)
            else -> state
        }
    }

    private fun AnimalSizeState.updateRefreshEntry(
        isOpenEntryBottomSheet: Boolean,
        currentAnimalSize: CurrentAnimalSize,
        isSaveStateForEntry: Boolean
    ): AnimalSizeState {
        return copy(
            isOpenEntryBottomSheet = isOpenEntryBottomSheet,
            currentProduct = currentAnimalSize,
            isSaveStateForEntry = isSaveStateForEntry
        )
    }

    private fun AnimalSizeState.updateValid(): AnimalSizeState {
        val baseValid = currentProduct.size.isNotBlank()
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AnimalSizeState.updateSuffix(suffix: Suffix): AnimalSizeState {
        return copy(
            currentProduct = currentProduct.copy(
                suffix = suffix
            )
        )
    }

    private fun AnimalSizeState.updateSize(size: String): AnimalSizeState {
        return copy(
            currentProduct = currentProduct.copy(
                size = size,
                error = currentProduct.error.copy(
                    isErrorSize = size.isBlank()
                )
            )
        )
    }

    private fun AnimalSizeState.updateDate(date: String): AnimalSizeState {
        return copy(
            currentProduct = currentProduct.copy(
                date = date
            )
        )
    }

    private fun AnimalSizeState.updateNote(note: String): AnimalSizeState {
        return copy(
            currentProduct = currentProduct.copy(
                note = note
            )
        )
    }
}
package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.formatNumber


class AnimalVaccinationReduce : BaseReducer<AnimalVaccinationState, AnimalVaccinationIntent>() {
    override fun reducer(
        state: AnimalVaccinationState,
        intent: AnimalVaccinationIntent
    ): AnimalVaccinationState {
        return when (intent) {
            is AnimalVaccinationIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                intent.isOpen, intent.state, intent.isSaveStateForBottomSheet
            ).updateValid()


            is AnimalVaccinationIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(
                intent.value
            )

            is AnimalVaccinationIntent.VaccinationChanged ->
                state.updateVaccination(intent.value).updateValid()

            is AnimalVaccinationIntent.CountChanged ->
                state.updateCount(intent.value).updatePriceAll().updateValid()

            is AnimalVaccinationIntent.PriceChanged ->
                state.updatePrice(intent.value).updatePriceAll()

            is AnimalVaccinationIntent.AutoPriceClicked ->
                state.updateAutoPrice(intent.value).updatePriceAll()

            is AnimalVaccinationIntent.DateClicked -> state.updateDate(intent.value)
            is AnimalVaccinationIntent.DateNextClicked -> state.updateDateNext(intent.value)
            is AnimalVaccinationIntent.NoteChanged -> state.updateNote(intent.value)
            is AnimalVaccinationIntent.DateFactoryClicked -> state.updateIsDateFactory(intent.value)
            else -> state
        }
    }

    private fun AnimalVaccinationState.updateOpenBottomSheetDelete(id: Long?): AnimalVaccinationState {
        return if (id == null)
            copy(
                isOpenBottomSheetDelete = false,
                deleteVaccination  = null
            )
        else {
            val domain = vaccinationList.find { it.id == id }
            copy(
                isOpenBottomSheetDelete = domain?.let { true } ?: false,
                deleteVaccination = domain
                )
        }
    }

    private fun AnimalVaccinationState.updateValid(): AnimalVaccinationState {
        val baseValid =
            currentProduct.vaccination.isNotBlank() && currentProduct.countVaccination.isNotBlank()
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AnimalVaccinationState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        vaccination: Vaccination,
        isSaveStateForBottomSheet: Boolean
    ): AnimalVaccinationState {
        return copy(
            isOpenDialog = isOpenEntryBottomSheet,
            currentProduct = vaccination,
            isSaveStateForBottomSheet = isSaveStateForBottomSheet
        )
    }

    private fun AnimalVaccinationState.updateVaccination(vaccination: String): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                vaccination = vaccination,
                error = currentProduct.error.copy(
                    isErrorVaccination = vaccination.isBlank()
                )
            )
        )
    }

    private fun AnimalVaccinationState.updateDate(date: String): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                date = date
            )
        )
    }

    private fun AnimalVaccinationState.updateDateNext(dateNext: String): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                nextDate = dateNext
            )
        )
    }

    private fun AnimalVaccinationState.updateNote(note: String): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                note = note
            )
        )
    }

    private fun AnimalVaccinationState.updateCount(count: String): AnimalVaccinationState {
        val isAnimalGroup = currentProduct.isAnimalGroup
        return copy(
            currentProduct = currentProduct.copy(
                countVaccination = count,
                error = currentProduct.error.copy(
                    isErrorCount = if (isAnimalGroup) currentProduct.countVaccination.isBlank() else false,
                    isErrorCountZero = if (isAnimalGroup) isAnimalCountZero(currentProduct.countVaccination) else false,
                )
            )
        )
    }

    private fun AnimalVaccinationState.updatePrice(price: String): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                price = price
            )
        )
    }

    private fun AnimalVaccinationState.updateAutoPrice(isAutoPrice: Boolean): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                isAutoCalculate = isAutoPrice
            )
        )
    }

    private fun AnimalVaccinationState.updatePriceAll(): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                priceAll = if (currentProduct.isAutoCalculate)
                    (currentProduct.price.toConvertZeroDouble() * currentProduct.countVaccination.toConvertZeroDouble()).formatNumber()
                else "0"
            )
        )
    }

    private fun AnimalVaccinationState.updateIsDateFactory(isDateFactory: Boolean): AnimalVaccinationState {
        return copy(
            currentProduct = currentProduct.copy(
                isDateFactory = isDateFactory,
            )
        )
    }
}

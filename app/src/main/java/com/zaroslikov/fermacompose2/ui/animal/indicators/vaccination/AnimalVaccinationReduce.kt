/*
package com.zaroslikov.fermacompose2.ui.animal.indicators.vaccination

import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AnimalVaccinationReduce : BaseReducer<AnimalVaccinationState, AnimalVaccinationIntent>() {
    override fun reduce(
        state: AnimalVaccinationState,
        intent: AnimalVaccinationIntent
    ): AnimalVaccinationState {
        fun updatePriceAll() : AnimalVaccinationState {
           return state.copy(
                currentProduct = state.currentProduct.copy(
                    priceAll = if (state.currentProduct.isAutoCalculate)
                        (state.currentProduct.price.toConvertZeroDouble() * state.currentProduct.countVaccination.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        }

        fun updateOpenDialog(
            isEntry: Boolean,
            domainAnimal: AnimalVaccinationExpensesDomain?
        ) {
            viewModelScope.launch {
                    state.copy(
                        isOpenDialog = true,
                        currentProduct = Vaccination(idAnimal = itemId, isEntry = isEntry)
                    )
                domainAnimal?.let {
                    val expensesVaccination =
                        expensesRepository.getItemExpensesForVaccination(domainAnimal.id).firstOrNull()


                        state.copy(
                            currentProduct = state.currentProduct.toUiMap22(
                                domainAnimal,
                                expensesVaccination?.id
                            )
                        )
                    }
                }
            }


        return when (intent) {
            is AnimalVaccinationIntent.OpenDialogClicked -> updateOpenDialog(
                intent.isEntry,
                intent.domainAnimalVaccination
            )

            AnimalVaccinationIntent.EndDialogClicked -> state.copy(
                isOpenDialog = false, currentProduct = Vaccination()
            )

            is AnimalVaccinationIntent.CountChanged -> {
                state.copy(
                    currentProduct = state.currentProduct.copy(countVaccination = intent.value)
                )
                updatePriceAll()
            }

            is AnimalVaccinationIntent.PriceChanged -> {
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        price = intent.value
                    )
                )
                updatePriceAll()
            }

            is AnimalVaccinationIntent.AutoPriceClicked -> {
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        isAutoCalculate = intent.value
                    )
                )
                updatePriceAll()
            }

            is AnimalVaccinationIntent.VaccinationChanged -> state.copy(
                currentProduct = state.currentProduct.copy(vaccination = intent.value)
            )

            is AnimalVaccinationIntent.DateClicked -> state.copy(
                currentProduct = state.currentProduct.copy(date = intent.value)
            )

            is AnimalVaccinationIntent.DateNextClicked -> state.copy(
                currentProduct = state.currentProduct.copy(nextDate = intent.value)
            )

            is AnimalVaccinationIntent.NoteChanged -> state.copy(
                currentProduct = state.currentProduct.copy(note = intent.value)
            )

            AnimalVaccinationIntent.InsertPressed -> insert()
            AnimalVaccinationIntent.UpdatePressed -> update()
            is AnimalVaccinationIntent.DeletePressed -> delete(intent.value)
            is AnimalVaccinationIntent.DateFactoryClicked -> state.copy(
                currentProduct = state.currentProduct.copy(isDateFactory = intent.value)
            )

        } as AnimalVaccinationState
    }
}*/

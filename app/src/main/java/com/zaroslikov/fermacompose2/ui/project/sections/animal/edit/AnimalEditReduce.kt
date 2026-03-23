package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalEditReduce : BaseReducer<AnimalEditState, AnimalEditIntent>() {
    override fun reducer(
        state: AnimalEditState,
        intent: AnimalEditIntent
    ): AnimalEditState {
        return when (intent) {
            is AnimalEditIntent.TitleChanged -> state.updateName(intent.value).updateValid()
            is AnimalEditIntent.TypeChanged -> state.updateType(intent.value).updateValid()
            is AnimalEditIntent.DateClicked -> state.updateDate(intent.value)
            is AnimalEditIntent.DateFactoryClicked -> state.updateIsDateFactory(intent.value)
            is AnimalEditIntent.DateFactoryChanged -> state.updateDateFactory(intent.value)
            is AnimalEditIntent.FoodDayChanged -> state.updateFoodDay(intent.value)
            is AnimalEditIntent.FoodDaySuffixClicked -> state.updateFoodSuffix(intent.value)
            is AnimalEditIntent.SexClicked -> state.updateSex(intent.value)
            is AnimalEditIntent.LoadDate -> state.updateLoadData(intent.value).updateValid()
            is AnimalEditIntent.LoadingChanged -> state.copy(isLoading = intent.value)
            else -> state
        }
    }


    private fun AnimalEditState.updateLoadData(value: AnimalUi): AnimalEditState {
        return copy(
            currentProduct = value
        )
    }

    private fun AnimalEditState.updateValid(): AnimalEditState {
        val isErrorTitle = currentProduct.name.isNotBlank()
        val isErrorType = currentProduct.type.isNotBlank()
        val baseValid = isErrorTitle && isErrorType
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AnimalEditState.updateName(name: String): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                name = name,
                error = currentProduct.error.copy(
                    isErrorTitle = name.isBlank()
                )
            )
        )
    }

    private fun AnimalEditState.updateIsDateFactory(isDateFactory: Boolean): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                isDateFactory = isDateFactory
            )
        )
    }

    private fun AnimalEditState.updateDateFactory(dateFactory: String): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                dateFactory = dateFactory
            )
        )
    }

    private fun AnimalEditState.updateFoodDay(foodDay: String): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                foodDay = foodDay
            )
        )
    }

    private fun AnimalEditState.updateFoodSuffix(foodDaySuffix: Suffix): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                foodDaySuffix = foodDaySuffix
            )
        )
    }

    private fun AnimalEditState.updateSex(sex: Boolean): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                sex = sex
            )
        )
    }

    private fun AnimalEditState.updateType(type: String): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                type = type,
                error = currentProduct.error.copy(
                    isErrorType = type.isBlank(),
                )
            )
        )
    }

    private fun AnimalEditState.updateDate(date: String): AnimalEditState {
        return copy(
            currentProduct = currentProduct.copy(
                dateBorn = date,
                dateFactory = if (currentProduct.isDateFactory) date else ""
            )
        )
    }
}
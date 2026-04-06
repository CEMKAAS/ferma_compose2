package com.zaroslikov.fermacompose2.ui.start.profile

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class ProfileReduce : BaseReducer<ProfileState, ProfileIntent>() {
    override fun reducer(
        state: ProfileState,
        intent: ProfileIntent
    ): ProfileState {
        return when (intent) {
            is ProfileIntent.ChoiceCurrency -> state.updateCurrentCurrency(intent.value)
            is ProfileIntent.LoadingChanged -> state.updateLoading(intent.value)
            is ProfileIntent.EditModeClick -> state.updateEditMode()
            is ProfileIntent.NameChanged -> state.updateName(intent.value)
        }
    }

    private fun ProfileState.updateEditMode(): ProfileState {
        return copy(
            isEditMode = !isEditMode
        )
    }

    private fun ProfileState.updateName(name: String): ProfileState {
        return copy(
            name = name
        )
    }

    private fun ProfileState.updateLoading(isLoading: Boolean): ProfileState {
        return copy(
            isLoading = isLoading
        )
    }

    private fun ProfileState.updateCurrentCurrency(currencySuffix: Suffix): ProfileState {
        return copy(
            currentFinanceCurrency = currencyList.first { currencySuffix == it.priceSuffix },
            currencyList = currencyList.map { item ->
                if (item.priceSuffix == currencySuffix) item.copy(isSelected = true)
                else item.copy(isSelected = false)
            },
        )
    }
}
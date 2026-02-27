package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class FirstScreenReducer : BaseReducer<FirstState, FirstIntent>() {
    override fun reducer(
        state: FirstState,
        intent: FirstIntent
    ): FirstState {
        return when (intent) {
            is FirstIntent.LoadingClicked -> state.copy(isLoading = intent.value)
            else -> state
        }
    }
}
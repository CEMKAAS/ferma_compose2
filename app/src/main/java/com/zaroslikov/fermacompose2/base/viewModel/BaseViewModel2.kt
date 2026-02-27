package com.zaroslikov.fermacompose2.base.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel2<STATE : BaseState, INTENT : BaseIntent, REDUCER : BaseReducer<STATE, INTENT>>(
    initialState: STATE, private val reducer: REDUCER
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    protected fun updateState(update: (STATE) -> STATE) {
        _state.update(update)
    }

    fun sendIntent(intent: INTENT) {
        val newState = reducer.reducer(_state.value, intent)
        _state.value = newState
    }

    protected fun getState(): STATE {
        return _state.value
    }

    private val _navigation = MutableSharedFlow<UiEvent>()
    val navigation = _navigation.asSharedFlow()

    protected val mutableEventFlow: MutableSharedFlow<UiEvent>
        get() = _navigation

    protected fun navigateTo(event: UiEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }

    protected fun showMessage(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }
}
package com.zaroslikov.fermacompose2.base.viewModel

import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class EntryNewViewModel<STATE : EntryNewState, INTENT : BaseIntent>(
    initialState: STATE
) : BaseViewModel<STATE, INTENT>(initialState) {

    protected abstract fun insert()
    protected abstract fun update()
    protected abstract fun delete(id: Long)
    abstract fun onIntent(intent: INTENT)


    protected abstract fun validation()
    protected fun isError(): Boolean {
        validation()
        return state.value.currentProduct.hasAnyError
    }
}
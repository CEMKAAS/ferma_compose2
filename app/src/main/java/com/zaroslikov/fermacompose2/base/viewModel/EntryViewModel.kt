package com.zaroslikov.fermacompose2.base.viewModel

import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.base.BaseIntent
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class EntryViewModel<STATE : BaseState, INTENT : BaseIntent >(
    initialState: STATE
) : BaseViewModel<STATE, INTENT>(initialState) {

    protected abstract fun insert()
    protected abstract fun update()
    protected abstract fun delete()
}
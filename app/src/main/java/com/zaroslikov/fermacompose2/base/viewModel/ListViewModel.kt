package com.zaroslikov.fermacompose2.base.viewModel

import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.base.state.ListState

abstract class ListViewModel<STATE : ListState, INTENT : BaseIntent>(
    initialState: STATE
) : BaseViewModel<STATE, INTENT>(initialState) {

}
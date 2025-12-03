package com.zaroslikov.fermacompose2.base.reduce

import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.state.BaseState

abstract class BaseReducer<T : BaseState, R : BaseIntent>() {
    abstract fun reduce(state: T, intent: R): T
}
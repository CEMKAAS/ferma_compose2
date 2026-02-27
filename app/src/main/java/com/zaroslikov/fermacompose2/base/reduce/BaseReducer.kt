package com.zaroslikov.fermacompose2.base.reduce

import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.state.BaseState

abstract class BaseReducer<STATE : BaseState, INTENT : BaseIntent>() {

    abstract fun reducer(state: STATE, intent: INTENT): STATE
}
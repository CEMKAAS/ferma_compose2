package com.zaroslikov.fermacompose2.base.reduce

import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.intent.BaseIntent2
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData

abstract class BaseReducer<STATE : BaseState, INTENT : BaseIntent>() {

    abstract fun reducer(state: STATE, intent: INTENT): STATE
}

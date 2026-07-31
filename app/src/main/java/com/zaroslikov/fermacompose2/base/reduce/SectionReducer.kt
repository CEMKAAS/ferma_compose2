package com.zaroslikov.fermacompose2.base.reduce

import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplatesState

abstract class SectionReducer<STATE : BaseState, INTENT : BaseIntent> :
    BaseReducer<STATE, INTENT>() {

    abstract fun qrReducer(state: STATE, intent: QrCodeIntent): STATE
    abstract fun templateReducer(state: STATE, intent: TemplateIntent): STATE
}

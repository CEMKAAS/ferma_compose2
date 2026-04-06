package com.zaroslikov.fermacompose2.ui.start.profile

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class ProfileIntent : BaseIntent {

    data object EditModeClick : ProfileIntent()
    data class NameChanged(val value: String) : ProfileIntent()

    data class LoadingChanged(val value: Boolean) : ProfileIntent()
    data class ChoiceCurrency(val value: Suffix) : ProfileIntent()
}
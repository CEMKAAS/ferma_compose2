package com.zaroslikov.crm.ui.base

import androidx.annotation.StringRes

/** Одноразовые события экрана. Снекбар — свой `SnackbarHostState` в `Scaffold` каждого экрана. */
sealed interface CrmUiEvent {
    data object NavigateBack : CrmUiEvent
    data class Message(@StringRes val resId: Int) : CrmUiEvent
}

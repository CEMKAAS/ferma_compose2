package com.zaroslikov.todo.ui.base

import androidx.annotation.StringRes

/** Одноразовые события экрана. Снекбар — свой `SnackbarHostState` в `Scaffold` каждого экрана. */
sealed interface TodoUiEvent {
    data object NavigateBack : TodoUiEvent
    data class Message(@StringRes val resId: Int, val onUndo: (() -> Unit)? = null) : TodoUiEvent
}

package com.zaroslikov.assistant.ui.chat

import com.zaroslikov.assistant.speech.SpeechState

/** Чистый переход состояния экрана чата. Побочные эффекты — во ViewModel. */
fun assistantChatReduce(state: AssistantChatState, intent: AssistantChatIntent): AssistantChatState = when (intent) {
    is AssistantChatIntent.Loaded -> state.copy(
        isLoading = false,
        projectTitle = intent.projectTitle,
        autoConfirm = intent.autoConfirm,
        speechAvailable = intent.speechAvailable
    )

    is AssistantChatIntent.InputChanged -> state.copy(input = intent.value.take(AssistantChatViewModel.MAX_INPUT_LENGTH))
    is AssistantChatIntent.Send -> if (intent.text == null) state.copy(input = "") else state
    is AssistantChatIntent.MessagesChanged -> state.copy(messages = intent.messages)
    is AssistantChatIntent.Thinking -> state.copy(isThinking = intent.value)

    is AssistantChatIntent.SpeechChanged -> state.copy(speech = intent.state)
    AssistantChatIntent.MicClicked, AssistantChatIntent.MicPermissionDenied, AssistantChatIntent.StopListening -> state

    is AssistantChatIntent.ConfirmCard, is AssistantChatIntent.CancelCard, is AssistantChatIntent.UndoCard -> state
    is AssistantChatIntent.EditCard -> state

    is AssistantChatIntent.EditFormChanged -> state.copy(editForm = intent.form)
    AssistantChatIntent.EditFormDismissed -> state.copy(editForm = null)
    AssistantChatIntent.EditFormSaved -> state

    is AssistantChatIntent.MenuToggled -> state.copy(showMenu = intent.show)
    is AssistantChatIntent.AutoConfirmChanged -> state.copy(autoConfirm = intent.value, showMenu = false)
    is AssistantChatIntent.ClearDialogToggled -> state.copy(showClearDialog = intent.show, showMenu = false)
    AssistantChatIntent.ClearChat -> state.copy(showClearDialog = false, speech = SpeechState.Idle)
}

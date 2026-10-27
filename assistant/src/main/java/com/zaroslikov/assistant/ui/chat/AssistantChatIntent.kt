package com.zaroslikov.assistant.ui.chat

import com.zaroslikov.assistant.model.ChatMessage
import com.zaroslikov.assistant.speech.SpeechState

sealed interface AssistantChatIntent {
    data class Loaded(val projectTitle: String, val autoConfirm: Boolean, val speechAvailable: Boolean) : AssistantChatIntent
    data class InputChanged(val value: String) : AssistantChatIntent

    /** Отправить текст из поля ввода (или подсказку-чип). */
    data class Send(val text: String? = null) : AssistantChatIntent

    data class MessagesChanged(val messages: List<ChatMessage>) : AssistantChatIntent
    data class Thinking(val value: Boolean) : AssistantChatIntent

    // Голос
    data object MicClicked : AssistantChatIntent

    /** Экран ушёл в фон — прекратить запись с микрофона. */
    data object StopListening : AssistantChatIntent
    data object MicPermissionDenied : AssistantChatIntent
    data class SpeechChanged(val state: SpeechState) : AssistantChatIntent

    // Карточка действия
    data class ConfirmCard(val messageId: Long) : AssistantChatIntent
    data class CancelCard(val messageId: Long) : AssistantChatIntent
    data class UndoCard(val messageId: Long) : AssistantChatIntent
    data class EditCard(val messageId: Long) : AssistantChatIntent

    // Шторка правки
    data class EditFormChanged(val form: ActionEditForm) : AssistantChatIntent
    data object EditFormDismissed : AssistantChatIntent
    data object EditFormSaved : AssistantChatIntent

    // Меню
    data class MenuToggled(val show: Boolean) : AssistantChatIntent
    data class AutoConfirmChanged(val value: Boolean) : AssistantChatIntent
    data class ClearDialogToggled(val show: Boolean) : AssistantChatIntent
    data object ClearChat : AssistantChatIntent
}

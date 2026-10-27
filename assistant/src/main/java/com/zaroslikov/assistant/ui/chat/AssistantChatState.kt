package com.zaroslikov.assistant.ui.chat

import com.zaroslikov.assistant.model.ChatMessage
import com.zaroslikov.assistant.nlu.ParsedCommand
import com.zaroslikov.assistant.speech.SpeechState

/** Поля шторки «Изменить» для карточки действия. Все значения — строки полей ввода. */
data class ActionEditForm(
    val messageId: Long,
    val command: ParsedCommand.Write,
    val title: String = "",
    val count: String = "",
    /** Единица количества; для животных — всегда головы. */
    val unitSuffix: com.zaroslikov.domain.models.enums.Suffix = com.zaroslikov.domain.models.enums.Suffix.PIECES,
    val unitLabel: String = "",
    val price: String = "",
    /** Для продажи/расхода: true — [price] это цена за единицу, false — сумма. */
    val priceIsPerUnit: Boolean = true,
    val buyer: String = "",
    val category: String = "",
    val animalName: String = "",
    val animalType: String = "",
    val date: String = "",
    val note: String = ""
)

data class AssistantChatState(
    val isLoading: Boolean = true,
    val projectTitle: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    /** Помощник «думает» (запрос к БД) — индикатор печати. */
    val isThinking: Boolean = false,
    val speech: SpeechState = SpeechState.Idle,
    val speechAvailable: Boolean = true,
    /** Записывать без карточки подтверждения. */
    val autoConfirm: Boolean = false,
    val editForm: ActionEditForm? = null,
    val showMenu: Boolean = false,
    val showClearDialog: Boolean = false
) {
    /** Есть карточка, ожидающая подтверждения — тогда «да/нет» голосом относятся к ней. */
    val pendingMessageId: Long?
        get() = messages.lastOrNull { it.action?.status == com.zaroslikov.assistant.model.ActionStatus.PENDING }?.id
}

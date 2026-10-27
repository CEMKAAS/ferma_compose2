package com.zaroslikov.assistant.model

import com.zaroslikov.assistant.nlu.ParsedCommand

enum class ChatAuthor { USER, ASSISTANT }

/** Строка карточки: подпись → значение («Количество» → «10 л»). */
data class CardRow(val label: String, val value: String)

/** Статус карточки действия. */
enum class ActionStatus {
    /** Ждёт подтверждения пользователя. */
    PENDING,
    /** Идёт запись в БД — кнопки заблокированы, повторное нажатие игнорируется. */
    SAVING,
    /** Записано в БД; можно отменить ([SavedRecord]). */
    SAVED,
    /** Идёт удаление записи. */
    UNDOING,
    /** Отменено пользователем. */
    CANCELLED,
    /** Запись удалена кнопкой «Отменить». */
    UNDONE
}

/** Что именно записали — чтобы «Отменить» удалил ровно это. */
data class SavedRecord(
    val addId: Long? = null,
    val saleId: Long? = null,
    val expenseId: Long? = null,
    val animalId: Long? = null
) {
    /** Ни одного id — отменять нечего. */
    val isEmpty: Boolean get() = addId == null && saleId == null && expenseId == null && animalId == null
}

/** Карточка команды записи в чате: распознанные поля + кнопки «Записать / Изменить / Отмена». */
data class ActionCard(
    val command: ParsedCommand.Write,
    val title: String,
    val rows: List<CardRow>,
    val status: ActionStatus = ActionStatus.PENDING,
    val saved: SavedRecord? = null
)

/** Карточка ответа аналитики. */
data class AnalysisCard(
    val title: String,
    val subtitle: String? = null,
    val rows: List<CardRow>,
    /** Доп. список, например топ покупателей или остатки по продуктам. */
    val listTitle: String? = null,
    val list: List<CardRow> = emptyList()
)

data class ChatMessage(
    val id: Long,
    val author: ChatAuthor,
    val text: String,
    val action: ActionCard? = null,
    val analysis: AnalysisCard? = null,
    /** Чипы-подсказки под сообщением помощника. */
    val suggestions: List<String> = emptyList(),
    /** Сообщение-ошибка (микрофон, БД) — рисуется приглушённо. */
    val isError: Boolean = false
)

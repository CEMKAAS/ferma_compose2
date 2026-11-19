package com.zaroslikov.todo.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TodoRepeatPeriod(val code: String) {
    @SerialName("none") NONE("none"),
    @SerialName("day") DAY("day"),
    @SerialName("week") WEEK("week"),
    @SerialName("month") MONTH("month"),
    @SerialName("year") YEAR("year");

    companion object {
        fun fromCode(code: String): TodoRepeatPeriod = entries.firstOrNull { it.code == code } ?: NONE
    }
}

/** Без срока нет повтора; окончание не может предшествовать первому сроку. */
fun DomainTodoTask.normalizeRepeat(): DomainTodoTask {
    val due = parseTodoDate(dueDate)
    if (due == null || repeatPeriod == TodoRepeatPeriod.NONE)
        return copy(repeatPeriod = TodoRepeatPeriod.NONE, repeatEndDate = null)
    val end = parseTodoDate(repeatEndDate)
    return copy(repeatEndDate = end?.let { if (it.isBefore(due)) due.toString() else it.toString() })
}

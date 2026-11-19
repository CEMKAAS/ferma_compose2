package com.zaroslikov.todo.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Вид экрана списка: строки с подсписками ([LIST]) или подсписки столбцами с карточками ([COLUMNS]). */
@Serializable(with = TodoListViewModeSerializer::class)
enum class TodoListViewMode(val code: String) {
    LIST("list"),
    COLUMNS("columns");

    companion object {
        fun fromCode(code: String?): TodoListViewMode =
            entries.firstOrNull { it.code == code } ?: LIST
    }
}

/** Неизвестный код читается как [TodoListViewMode.LIST], а не валит импорт. */
object TodoListViewModeSerializer : KSerializer<TodoListViewMode> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.todo.domain.model.TodoListViewMode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TodoListViewMode) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): TodoListViewMode = TodoListViewMode.fromCode(decoder.decodeString())
}

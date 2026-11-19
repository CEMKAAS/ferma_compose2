package com.zaroslikov.todo.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Приоритет задачи. В БД и бэкап уходит только стабильный [code]. */
@Serializable(with = TodoPrioritySerializer::class)
enum class TodoPriority(val code: String) {
    NONE("none"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    companion object {
        fun fromCode(code: String): TodoPriority =
            entries.firstOrNull { it.code == code } ?: NONE
    }
}

/** Неизвестный код (бэкап из новой версии) читается как [TodoPriority.NONE], а не валит импорт. */
object TodoPrioritySerializer : KSerializer<TodoPriority> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.todo.domain.model.TodoPriority", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TodoPriority) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): TodoPriority = TodoPriority.fromCode(decoder.decodeString())
}

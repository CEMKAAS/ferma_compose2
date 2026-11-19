package com.zaroslikov.todo.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Тип связи задач. [RELATES] — симметричная «Связана с». [BLOCKS] — направленная:
 * `task_id` блокирует `linked_task_id`, со стороны второй задачи — «Заблокирована задачей».
 */
@Serializable(with = TodoLinkTypeSerializer::class)
enum class TodoLinkType(val code: String) {
    RELATES("relates"),
    BLOCKS("blocks");

    companion object {
        fun fromCode(code: String): TodoLinkType =
            entries.firstOrNull { it.code == code } ?: RELATES
    }
}

/** Неизвестный код читается как [TodoLinkType.RELATES], а не валит импорт. */
object TodoLinkTypeSerializer : KSerializer<TodoLinkType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.todo.domain.model.TodoLinkType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TodoLinkType) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): TodoLinkType = TodoLinkType.fromCode(decoder.decodeString())
}

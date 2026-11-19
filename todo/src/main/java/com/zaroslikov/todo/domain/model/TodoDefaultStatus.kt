package com.zaroslikov.todo.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Статусы, которые создаются для каждого проекта автоматически. У пользовательского статуса
 * кода нет (`default_code = null`). [position] — порядок при первом создании.
 */
enum class TodoDefaultStatus(val code: String, val position: Int, val isDone: Boolean) {
    PLANNED("planned", 0, false),
    IN_PROGRESS("in_progress", 1, false),
    DONE("done", 2, true);

    companion object {
        /** Неизвестный код (из новой версии) — пользовательский статус, а не ошибка. */
        fun fromCode(code: String?): TodoDefaultStatus? =
            entries.firstOrNull { it.code == code }
    }
}

/** Nullable-код статуса для бэкапа: `null` и неизвестный код читаются как пользовательский статус. */
@OptIn(ExperimentalSerializationApi::class)
object TodoDefaultStatusSerializer : KSerializer<TodoDefaultStatus?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.todo.domain.model.TodoDefaultStatus", PrimitiveKind.STRING).nullable

    override fun serialize(encoder: Encoder, value: TodoDefaultStatus?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.code)
    }

    override fun deserialize(decoder: Decoder): TodoDefaultStatus? =
        if (decoder.decodeNotNullMark()) TodoDefaultStatus.fromCode(decoder.decodeString())
        else decoder.decodeNull()
}

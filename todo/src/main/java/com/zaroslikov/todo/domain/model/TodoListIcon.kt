package com.zaroslikov.todo.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Значок списка. В БД, бэкапе — только стабильный [code], никогда не id ресурса: ресурсы
 * перенумеровываются на каждой сборке. В картинку код превращается только в UI
 * (`ui/components/TodoListIconVisuals.kt`). Коды не менять — только дописывать новые.
 */
@Serializable(with = TodoListIconSerializer::class)
enum class TodoListIcon(val code: String) {
    LIST("list"),
    ANIMALS("animals"),
    PLANTS("plants"),
    FLOWERS("flowers"),
    MACHINERY("machinery"),
    EGGS("eggs"),
    SHOPPING("shopping"),
    REPAIR("repair"),
    HOME("home"),
    WATER("water"),
    MONEY("money"),
    CALENDAR("calendar");

    companion object {
        val DEFAULT = LIST

        /** Неизвестный код (бэкап из новой версии, порча) — значок по умолчанию. */
        fun fromCode(code: String?): TodoListIcon = entries.firstOrNull { it.code == code } ?: DEFAULT
    }
}

/** Неизвестный код читается как [TodoListIcon.LIST], а не валит импорт. */
object TodoListIconSerializer : KSerializer<TodoListIcon> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.todo.domain.model.TodoListIcon", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TodoListIcon) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): TodoListIcon = TodoListIcon.fromCode(decoder.decodeString())
}

package com.zaroslikov.crm.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Тип взаимодействия с клиентом. В БД и бэкап уходит только стабильный [code]. */
@Serializable(with = CrmInteractionTypeSerializer::class)
enum class CrmInteractionType(val code: String) {
    CALL("call"),
    MEETING("meeting"),
    MESSAGE("message"),
    NOTE("note");

    companion object {
        fun fromCode(code: String): CrmInteractionType =
            entries.firstOrNull { it.code == code } ?: NOTE
    }
}

/** Неизвестный код (бэкап из новой версии) читается как [CrmInteractionType.NOTE], а не валит импорт. */
object CrmInteractionTypeSerializer : KSerializer<CrmInteractionType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.crm.domain.model.CrmInteractionType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CrmInteractionType) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): CrmInteractionType = CrmInteractionType.fromCode(decoder.decodeString())
}

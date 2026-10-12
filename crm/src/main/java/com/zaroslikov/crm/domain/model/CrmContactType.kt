package com.zaroslikov.crm.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Тип контакта клиента. В БД и бэкап уходит только стабильный [code]. */
@Serializable(with = CrmContactTypeSerializer::class)
enum class CrmContactType(val code: String) {
    PHONE("phone"),
    EMAIL("email"),
    TELEGRAM("telegram"),
    WHATSAPP("whatsapp"),
    VK("vk"),
    ADDRESS("address"),
    OTHER("other");

    companion object {
        fun fromCode(code: String): CrmContactType =
            entries.firstOrNull { it.code == code } ?: OTHER
    }
}

/** Неизвестный код (бэкап из новой версии) читается как [CrmContactType.OTHER], а не валит импорт. */
object CrmContactTypeSerializer : KSerializer<CrmContactType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.crm.domain.model.CrmContactType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CrmContactType) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): CrmContactType = CrmContactType.fromCode(decoder.decodeString())
}

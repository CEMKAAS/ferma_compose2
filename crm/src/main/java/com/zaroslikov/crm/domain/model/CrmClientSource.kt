package com.zaroslikov.crm.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Откуда появился клиент. В БД и бэкап уходит только стабильный [code]. */
@Serializable(with = CrmClientSourceSerializer::class)
enum class CrmClientSource(val code: String) {
    SALES("sales"),
    MANUAL("manual");

    companion object {
        fun fromCode(code: String): CrmClientSource =
            entries.firstOrNull { it.code == code } ?: MANUAL
    }
}

/** Неизвестный код (бэкап из новой версии) читается как [CrmClientSource.MANUAL], а не валит импорт. */
object CrmClientSourceSerializer : KSerializer<CrmClientSource> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.crm.domain.model.CrmClientSource", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CrmClientSource) = encoder.encodeString(value.code)

    override fun deserialize(decoder: Decoder): CrmClientSource = CrmClientSource.fromCode(decoder.decodeString())
}

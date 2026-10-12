package com.zaroslikov.domain.models.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Статус беременности. Хранится кодом [code]; коды стабильны и не переиспользуются. */
enum class PregnancyStatus(val code: Int) {
    ACTIVE(0),
    BIRTH(1),
    INTERRUPTED(2);

    companion object {
        /** Неизвестный код (бэкап более новой версии) — [INTERRUPTED]: не порождает напоминаний. */
        fun fromCode(code: Int): PregnancyStatus = entries.firstOrNull { it.code == code } ?: INTERRUPTED
    }
}

/** Статус числом [PregnancyStatus.code]: бэкапы не зависят от имён констант. */
object PregnancyStatusAsCodeSerializer : KSerializer<PregnancyStatus> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.domain.models.enums.PregnancyStatus", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: PregnancyStatus) = encoder.encodeInt(value.code)

    override fun deserialize(decoder: Decoder): PregnancyStatus = PregnancyStatus.fromCode(decoder.decodeInt())
}

package com.zaroslikov.domain.models.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Причина прерывания беременности. Хранится кодом [code]; коды стабильны и не переиспользуются. */
enum class PregnancyInterruptReason(val code: Int) {
    /** Не подтвердилась (пустая). */
    NOT_CONFIRMED(0),
    /** Аборт / выкидыш. */
    ABORTION(1),
    /** Рассасывание (только кролики). */
    RESORPTION(2),
    /** Болезнь. */
    DISEASE(3),
    /** Гибель / забой / продажа самки. */
    FEMALE_LOST(4),
    /** Ошибка ввода. */
    INPUT_ERROR(5),
    /** Другое (+ комментарий в note). */
    OTHER(6);

    companion object {
        fun fromCode(code: Int): PregnancyInterruptReason = entries.firstOrNull { it.code == code } ?: OTHER
    }
}

/** Причина числом [PregnancyInterruptReason.code] или `null`. */
object PregnancyInterruptReasonAsCodeSerializer : KSerializer<PregnancyInterruptReason?> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.zaroslikov.domain.models.enums.PregnancyInterruptReason", PrimitiveKind.INT)
            .nullable

    override fun serialize(encoder: Encoder, value: PregnancyInterruptReason?) {
        if (value == null) encoder.encodeNull() else encoder.encodeInt(value.code)
    }

    override fun deserialize(decoder: Decoder): PregnancyInterruptReason? =
        if (decoder.decodeNotNullMark()) {
            PregnancyInterruptReason.fromCode(decoder.decodeInt())
        } else {
            decoder.decodeNull()
        }
}

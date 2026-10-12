package com.zaroslikov.domain.models.enums

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PregnancyEnumsTest {

    @Test fun statusCodesUniqueAndStable() {
        assertEquals(PregnancyStatus.entries.size, PregnancyStatus.entries.map { it.code }.toSet().size)
        assertEquals(listOf(0, 1, 2), PregnancyStatus.entries.map { it.code })
    }

    @Test fun statusRoundTrip() = PregnancyStatus.entries.forEach {
        assertEquals(it, PregnancyStatus.fromCode(it.code))
    }

    @Test fun statusUnknownCodeIsInterrupted() = assertEquals(PregnancyStatus.INTERRUPTED, PregnancyStatus.fromCode(99))

    @Test fun reasonCodesUniqueAndStable() {
        assertEquals(
            PregnancyInterruptReason.entries.size,
            PregnancyInterruptReason.entries.map { it.code }.toSet().size
        )
        assertEquals((0..6).toList(), PregnancyInterruptReason.entries.map { it.code })
    }

    @Test fun reasonRoundTrip() = PregnancyInterruptReason.entries.forEach {
        assertEquals(it, PregnancyInterruptReason.fromCode(it.code))
    }

    @Test fun reasonUnknownCodeIsOther() =
        assertEquals(PregnancyInterruptReason.OTHER, PregnancyInterruptReason.fromCode(-1))

    @Serializable
    private data class Holder(
        @Serializable(with = PregnancyStatusAsCodeSerializer::class) val status: PregnancyStatus,
        @Serializable(with = PregnancyInterruptReasonAsCodeSerializer::class) val reason: PregnancyInterruptReason?
    )

    @Test fun serializedAsCodes() {
        val json = Json.encodeToString(Holder.serializer(), Holder(PregnancyStatus.BIRTH, PregnancyInterruptReason.DISEASE))
        assertEquals("""{"status":1,"reason":3}""", json)
        assertEquals(Holder(PregnancyStatus.BIRTH, PregnancyInterruptReason.DISEASE), Json.decodeFromString(Holder.serializer(), json))
        val nullReason = Holder(PregnancyStatus.ACTIVE, null)
        assertEquals(nullReason, Json.decodeFromString(Holder.serializer(), Json.encodeToString(Holder.serializer(), nullReason)))
    }
}

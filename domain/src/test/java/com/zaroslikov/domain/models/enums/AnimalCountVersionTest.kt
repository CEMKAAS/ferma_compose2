package com.zaroslikov.domain.models.enums

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimalCountVersionTest {
    @Test fun codesAreUnique() =
        assertEquals(AnimalCountVersion.entries.size, AnimalCountVersion.entries.map { it.code }.toSet().size)

    @Test fun fromCodeRoundTrip() =
        AnimalCountVersion.entries.forEach { assertEquals(it, AnimalCountVersion.fromCode(it.code)) }

    @Test fun transferCodesAndSigns() {
        assertEquals(6, AnimalCountVersion.TRANSFER_IN.code)
        assertEquals(7, AnimalCountVersion.TRANSFER_OUT.code)
        assertEquals(1, AnimalCountVersion.TRANSFER_IN.sign)
        assertEquals(-1, AnimalCountVersion.TRANSFER_OUT.sign)
    }

    /** Служебные записи (только чтение): перегруппировка и вход/выход из сборной группы. */
    @Test fun onlyServiceRecordsAreTransfer() =
        AnimalCountVersion.entries.forEach {
            assertEquals(it in SERVICE, it.isTransfer)
        }

    @Test fun herdMembershipCodesAndZeroSign() {
        assertEquals(8, AnimalCountVersion.HERD_JOIN.code)
        assertEquals(9, AnimalCountVersion.HERD_LEAVE.code)
        assertEquals(0, AnimalCountVersion.HERD_JOIN.sign)
        assertEquals(0, AnimalCountVersion.HERD_LEAVE.sign)
    }

    private companion object {
        val SERVICE = setOf(
            AnimalCountVersion.TRANSFER_IN,
            AnimalCountVersion.TRANSFER_OUT,
            AnimalCountVersion.HERD_JOIN,
            AnimalCountVersion.HERD_LEAVE
        )
    }

    @Test fun signs() {
        listOf(AnimalCountVersion.SALE, AnimalCountVersion.KILL, AnimalCountVersion.WRITE_OFF)
            .forEach { assertTrue(it.sign < 0) }
        listOf(AnimalCountVersion.EXPENSES, AnimalCountVersion.ADD, AnimalCountVersion.INCUBATOR)
            .forEach { assertTrue(it.sign > 0) }
        assertFalse(AnimalCountVersion.SALE.isTransfer)
    }
}

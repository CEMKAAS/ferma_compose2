package com.zaroslikov.crm

import com.zaroslikov.crm.util.crmCompact
import com.zaroslikov.crm.util.crmFormat
import com.zaroslikov.crm.util.crmMoney
import com.zaroslikov.crm.util.daysBetween
import com.zaroslikov.crm.util.isoToDisplay
import com.zaroslikov.crm.util.isoToMillis
import com.zaroslikov.crm.util.millisToIso
import com.zaroslikov.crm.util.monthShortRu
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CrmFormatTest {

    private val nbsp = ' '

    @Test
    fun crmFormat_groupsThousandsAndTrimsZeros() {
        assertEquals("0", 0.0.crmFormat())
        assertEquals("15", 15.0.crmFormat())
        assertEquals("1${nbsp}234", 1234.0.crmFormat())
        assertEquals("12${nbsp}345,5", 12345.5.crmFormat())
        assertEquals("1${nbsp}000${nbsp}000,25", 1_000_000.25.crmFormat())
        assertEquals("-2${nbsp}500", (-2500.0).crmFormat())
    }

    @Test
    fun crmFormat_roundsToTwoDecimals() {
        assertEquals("3,33", (10.0 / 3).crmFormat())
        assertEquals("0", (-0.001).crmFormat())
        assertEquals("42", 42.crmFormat())
    }

    @Test
    fun crmCompact_shortensLargeValues() {
        assertEquals("950", 950.0.crmCompact())
        assertEquals("12,5 тыс", 12_500.0.crmCompact())
        assertEquals("1,2 млн", 1_200_000.0.crmCompact())
    }

    @Test
    fun isoToDisplay_convertsAndKeepsGarbage() {
        assertEquals("05.03.2026", isoToDisplay("2026-03-05"))
        assertEquals("not a date", isoToDisplay("not a date"))
    }

    @Test
    fun daysBetween_countsCalendarDays() {
        assertEquals(31L, daysBetween("2026-01-01", "2026-02-01"))
        assertEquals(-1L, daysBetween("2026-01-02", "2026-01-01"))
        assertNull(daysBetween("bad", "2026-01-01"))
    }

    @Test
    fun millis_roundTrip() {
        val millis = isoToMillis("2026-10-12")!!
        assertEquals("2026-10-12", millisToIso(millis))
        assertNull(isoToMillis(""))
    }

    @Test
    fun monthNames() {
        assertEquals("янв", monthShortRu(1))
        assertEquals("дек", monthShortRu(12))
        assertEquals("май", monthShortRu(5))
        assertEquals("", monthShortRu(13))
    }

    @Test
    fun crmMoney_appendsCurrency() {
        assertEquals("1${nbsp}500 ₽", 1500.0.crmMoney("₽"))
        assertEquals("1${nbsp}500", 1500.0.crmMoney(""))
    }
}

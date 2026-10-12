package com.zaroslikov.crm

import com.zaroslikov.crm.domain.model.CrmClientActivity
import com.zaroslikov.crm.util.crmClientActivity
import org.junit.Assert.assertEquals
import org.junit.Test

class CrmClientActivityTest {

    private val today = "2026-10-12"

    @Test
    fun noSales_whenNoPurchaseDates() {
        assertEquals(CrmClientActivity.NO_SALES, crmClientActivity(null, null, today))
    }

    @Test
    fun new_whenFirstPurchaseWithin30Days() {
        assertEquals(CrmClientActivity.NEW, crmClientActivity("2026-10-01", "2026-10-10", today))
        // Граница: ровно 30 дней — ещё новый
        assertEquals(CrmClientActivity.NEW, crmClientActivity("2026-09-12", "2026-09-12", today))
    }

    @Test
    fun active_whenBoughtWithin90Days() {
        assertEquals(CrmClientActivity.ACTIVE, crmClientActivity("2026-01-01", "2026-09-01", today))
        // Граница: ровно 90 дней — ещё активный
        assertEquals(CrmClientActivity.ACTIVE, crmClientActivity("2026-01-01", "2026-07-14", today))
    }

    @Test
    fun inactive_whenLastPurchaseOlderThan90Days() {
        assertEquals(CrmClientActivity.INACTIVE, crmClientActivity("2026-01-01", "2026-07-13", today))
    }

    @Test
    fun missingFirstDate_usesLastDate() {
        assertEquals(CrmClientActivity.NEW, crmClientActivity(null, "2026-10-01", today))
        assertEquals(CrmClientActivity.ACTIVE, crmClientActivity(null, "2026-08-01", today))
    }
}

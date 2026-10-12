package com.zaroslikov.data.room.mapper.dto.crm

import com.zaroslikov.data.room.dto.crm.CrmBuyerProductDto
import com.zaroslikov.data.room.dto.crm.CrmBuyerStatsDto
import com.zaroslikov.data.room.dto.crm.CrmMonthRevenueDto
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerProduct
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import com.zaroslikov.domain.models.enums.Suffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CrmMapperTest {

    @Test
    fun `статистика покупателя переносится без искажений`() {
        val result = CrmBuyerStatsDto("Иван", 1500.5, 3, "2024-01-05", "2024-03-10")
            .toDomainCrmBuyerStats()
        assertEquals(DomainCrmBuyerStats("Иван", 1500.5, 3, "2024-01-05", "2024-03-10"), result)
    }

    @Test
    fun `пустые даты статистики остаются null`() {
        val result = CrmBuyerStatsDto("Иван", 0.0, 0, null, null).toDomainCrmBuyerStats()
        assertNull(result.firstDate)
        assertNull(result.lastDate)
    }

    @Test
    fun `товар покупателя переносится вместе с единицей измерения`() {
        val result = CrmBuyerProductDto("Яйца", 30.0, Suffix.PIECES, 900.0).toDomainCrmBuyerProduct()
        assertEquals(DomainCrmBuyerProduct("Яйца", 30.0, Suffix.PIECES, 900.0), result)
    }

    @Test
    fun `выручка за месяц переносится без искажений`() {
        val result = CrmMonthRevenueDto(2024, 2, 1200.0, 4).toDomainCrmMonthRevenue()
        assertEquals(DomainCrmMonthRevenue(2024, 2, 1200.0, 4), result)
    }
}

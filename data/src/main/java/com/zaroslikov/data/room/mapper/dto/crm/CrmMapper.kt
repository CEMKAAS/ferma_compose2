package com.zaroslikov.data.room.mapper.dto.crm

import com.zaroslikov.data.room.dto.crm.CrmBuyerProductDto
import com.zaroslikov.data.room.dto.crm.CrmBuyerStatsDto
import com.zaroslikov.data.room.dto.crm.CrmMonthRevenueDto
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerProduct
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue

fun CrmBuyerStatsDto.toDomainCrmBuyerStats(): DomainCrmBuyerStats {
    return DomainCrmBuyerStats(
        buyer = this.buyer,
        total = this.total,
        orders = this.orders,
        firstDate = this.firstDate,
        lastDate = this.lastDate,
    )
}

fun CrmBuyerProductDto.toDomainCrmBuyerProduct(): DomainCrmBuyerProduct {
    return DomainCrmBuyerProduct(
        title = this.title,
        count = this.count,
        suffix = this.suffix,
        total = this.total,
    )
}

fun CrmMonthRevenueDto.toDomainCrmMonthRevenue(): DomainCrmMonthRevenue {
    return DomainCrmMonthRevenue(
        year = this.year,
        month = this.month,
        total = this.total,
        orders = this.orders,
    )
}

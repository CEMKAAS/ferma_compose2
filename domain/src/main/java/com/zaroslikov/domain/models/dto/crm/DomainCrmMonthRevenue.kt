package com.zaroslikov.domain.models.dto.crm

/** Выручка за месяц. */
data class DomainCrmMonthRevenue(
    val year: Int,
    val month: Int,
    val total: Double,
    val orders: Int,
)

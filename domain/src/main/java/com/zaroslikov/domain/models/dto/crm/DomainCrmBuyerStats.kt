package com.zaroslikov.domain.models.dto.crm

/** Агрегат продаж по покупателю. Даты в формате yyyy-MM-dd. */
data class DomainCrmBuyerStats(
    val buyer: String,
    val total: Double,
    val orders: Int,
    val firstDate: String?,
    val lastDate: String?,
)

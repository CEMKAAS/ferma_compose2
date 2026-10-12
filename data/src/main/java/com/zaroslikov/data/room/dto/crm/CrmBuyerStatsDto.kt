package com.zaroslikov.data.room.dto.crm

data class CrmBuyerStatsDto(
    val buyer: String,
    val total: Double,
    val orders: Int,
    val firstDate: String?,
    val lastDate: String?,
)

package com.zaroslikov.data.room.dto.crm

data class CrmMonthRevenueDto(
    val year: Int,
    val month: Int,
    val total: Double,
    val orders: Int,
)

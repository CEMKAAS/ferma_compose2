package com.zaroslikov.domain.models

import com.zaroslikov.domain.models.enums.Suffix

data class DomainSaleTable(
    val id: Long = 0,
    val title: String,
    val count: Double,
    val countSuffix: Suffix,
    val price: Double,
    val priceAll: Double? = null,
    val day: Int,
    val month: Int,
    val year: Int,
    val category: String,
    val buyer: String? = null,
    val note: String,
    val idPT: Long,
    val animalId: Long? = null,
    val animalCountId: Long? = null,
)
package com.zaroslikov.domain.models.table


data class DomainWriteOffTable(
    val id: Long = 0,
    val title: String,
    val count: Double,
    val countSuffix: String,
    val price: Double? = null,
    val priceAll: Double? = null,
    val day: Int,
    val month: Int,
    val year: Int,
    val status: Boolean,
    val note: String,
    val idPT: Long,
    val animalCountId: Long? = null
)
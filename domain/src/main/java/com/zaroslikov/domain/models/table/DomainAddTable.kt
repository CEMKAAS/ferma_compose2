package com.zaroslikov.domain.models


data class DomainAddTable(
    val id: Long = 0,
    val title: String,
    val count: Double,
    val day: Int,
    val month: Int,
    val year: Int,
    val price: Double,
    val countSuffix: String,
    val category: String,
    val animalId: Long? = null,
    val note: String,
    val idPT: Long
)
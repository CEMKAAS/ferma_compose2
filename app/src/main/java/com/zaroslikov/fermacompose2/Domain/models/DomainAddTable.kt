package com.zaroslikov.fermacompose2.Domain.models


data class DomainAddTable(
    val id: Long,
    val title: String,
    val count: Double,
    val day: Int,
    val month: Int,
    val year: Int,
    val price: Double,
    val countSuffix: String,
    val category: String,
    val animalId: Long?,
    val note: String,
    val idPT: Long
)
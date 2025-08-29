package com.zaroslikov.domain.models.dto.add

data class DomainFastAddProduct(
    val title: String,
    val count: Double,
    val suffix: String,
    val category: String,
    val idAnimal: Long,
    val animalName: String
)


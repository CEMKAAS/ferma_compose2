package com.zaroslikov.domain.models.table

data class DomainAnimalWeight(
    val id: Long,
    val weight: String,
    val suffix: String,
    val date: String,
    val idAnimal: Long,
    val note: String
)

package com.zaroslikov.domain.models.table

data class DomainAnimalWeight(
    val id: Long = 0,
    val weight: String = "",
    val suffix: String = "",
    val date: String = "",
    val idAnimal: Long = 0,
    val note: String = ""
)

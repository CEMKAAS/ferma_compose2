package com.zaroslikov.domain.models.table

data class DomainAnimalVaccination(
    val id: Long = 0,
    val vaccination: String = "",
    val date: String = "",
    val nextVaccination: String = "",
    val idAnimal: Long = 0,
    val note: String = ""
)

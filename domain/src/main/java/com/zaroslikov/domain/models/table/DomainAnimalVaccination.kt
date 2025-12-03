package com.zaroslikov.domain.models.table

data class DomainAnimalVaccination(
    val id: Long = 0,
    val vaccination: String = "",
    val countVaccination: Int = 1,
    val date: String = "",
    val nextVaccination: String? = null,
    val idAnimal: Long = 0,
    val note: String = ""
)

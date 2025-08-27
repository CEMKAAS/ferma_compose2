package com.zaroslikov.domain.models


data class DomainVaccinationsTable(
    val id: Int = 0,
    val vaccination: String = "",
    val date: String = "",
    val nextVaccination: String = "",
    val idAnimal: Int = 0
)

package com.zaroslikov.fermacompose2.Domain.models


data class DomainVaccinationsTable(
    val id: Int = 0,
    val vaccination: String = "",
    val date: String = "",
    val nextVaccination: String = "",
    val idAnimal: Int = 0
)

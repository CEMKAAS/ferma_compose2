package com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable

data class DomainAnimalCount(
    val id: Long,
    val count: String,
    val suffix: String,
    val date: String,
    val note: String,
    val version: Int?,
    val idAnimal: Long
)
package com.zaroslikov.domain.models.table

data class DomainAnimalVaccination(
    val id: Long,
    val vaccination: String,
    val date: String,
    val nextVaccination: String,
    val idAnimal: Long,
    val note:String
)

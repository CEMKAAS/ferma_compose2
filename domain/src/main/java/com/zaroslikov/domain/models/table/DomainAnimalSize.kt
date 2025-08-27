package com.zaroslikov.domain.models.table

data class DomainAnimalSize(
    val id: Long,
    val size: String,
    val suffix: String,
    val date: String,
    val idAnimal: Int,
    val note: String
)

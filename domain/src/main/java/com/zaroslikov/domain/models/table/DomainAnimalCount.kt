package com.zaroslikov.domain.models.table

data class DomainAnimalCount(
    val id: Long = 0,
    val count: String = "",
    val suffix: String = "",
    val date: String = "",
    val note: String = "",
    val version: Int? = null,
    val idAnimal: Long = 0
)
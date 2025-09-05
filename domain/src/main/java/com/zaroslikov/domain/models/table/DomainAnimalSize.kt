package com.zaroslikov.domain.models.table

data class DomainAnimalSize(
    val id: Long = 0,
    val size: String = "",
    val suffix: String ="",
    val date: String ="",
    val idAnimal: Long = 0,
    val note: String = ""
)

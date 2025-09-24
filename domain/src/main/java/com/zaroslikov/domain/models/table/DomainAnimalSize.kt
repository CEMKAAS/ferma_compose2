package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalSize(
    val id: Long = 0,
    val size: String = "",
    val suffix: Suffix = Suffix.MILLIMETERS,
    val date: String ="",
    val idAnimal: Long = 0,
    val note: String = ""
)

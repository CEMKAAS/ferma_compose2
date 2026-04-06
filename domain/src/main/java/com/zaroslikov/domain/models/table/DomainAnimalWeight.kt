package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainAnimalWeight(
    val id: Long = 0,
    val weight: String = "",
    val suffix: Suffix = Suffix.KILOGRAM,
    val date: String = "",
    val idAnimal: Long = 0,
    val note: String = ""
)

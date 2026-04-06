package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainAnimalCount(
    val id: Long = 0,
    val count: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = "",
    val note: String = "",
    val version: AnimalCountVersion? = null,
    val idAnimal: Long = 0
)
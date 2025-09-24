package com.zaroslikov.domain.models.dto.animal

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalCountPrice(
    val id: Long,
    val count: String,
    val suffix: Suffix,
    val date: String,
    val animalId: Long,
    val note: String,
    val version: AnimalCountVersion?,
    val price: Double?,
    val buyer: String?,
    val tableId: Long?,
    val idPT: Long?
)

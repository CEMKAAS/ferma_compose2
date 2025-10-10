package com.zaroslikov.domain.models.dto.animal

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalCountPrice(
    val id: Long = 0,
    val count: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = "",
    val animalId: Long = 0,
    val note: String = "",
    val version: AnimalCountVersion? = AnimalCountVersion.ADD,
    val price: Double? = null,
    val priceAll: Double? = null,
    val buyer: String? = null,
    val tableId: Long? = null,
    val idPT: Long? = null,
)

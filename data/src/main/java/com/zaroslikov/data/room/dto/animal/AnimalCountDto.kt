package com.zaroslikov.data.room.dto.animal

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix

data class AnimalCountDto(
    val id: Long = 0,
    val count: String? = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = "",
    val note: String = "",
    val version: AnimalCountVersion? = null,
    val idAnimal: Long = 0
)
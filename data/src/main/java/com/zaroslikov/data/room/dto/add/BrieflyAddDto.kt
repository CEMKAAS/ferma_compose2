package com.zaroslikov.data.room.dto.add

import com.zaroslikov.domain.models.enums.Suffix


data class BrieflyAddDto(
    val title: String,
    val count :Double,
    val suffix: Suffix
)
package com.zaroslikov.domain.models.dto.incubator

import com.zaroslikov.domain.models.enums.TypeEgg

data class DomainTypeEggCount(
    val typeEgg: TypeEgg,
    val count: Int
)

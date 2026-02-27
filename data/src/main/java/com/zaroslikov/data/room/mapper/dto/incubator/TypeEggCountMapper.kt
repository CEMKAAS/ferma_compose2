package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.TypeEggCountDto
import com.zaroslikov.domain.models.dto.incubator.DomainTypeEggCount

fun TypeEggCountDto.toDomainTypeEggCount(): DomainTypeEggCount {
    return DomainTypeEggCount(typeEgg = this.typeEgg, count = this.count,)
}
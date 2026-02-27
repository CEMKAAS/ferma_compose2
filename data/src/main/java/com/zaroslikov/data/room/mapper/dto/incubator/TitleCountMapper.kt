package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.TitleCountDto
import com.zaroslikov.domain.models.dto.incubator.DomainTitleCount

fun TitleCountDto.toTitleCountDto(): DomainTitleCount {
    return DomainTitleCount(this.title, this.count)
}
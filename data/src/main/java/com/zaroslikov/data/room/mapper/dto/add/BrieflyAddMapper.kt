package com.zaroslikov.data.room.mapper.dto

import com.zaroslikov.data.room.dto.add.BrieflyAddDto
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain

fun BrieflyAddDto.toBrieflyAddDomain(): BrieflyAddDomain {
    return BrieflyAddDomain(
        title = this.title, count = this.count, suffix = this.suffix,
        rowCount = this.rowCount,
    )
}

fun BrieflyAddDomain.toBrieflyAddDto(): BrieflyAddDto {
    return BrieflyAddDto(
        count = this.count, title = this.title, suffix = this.suffix,
        rowCount = this.rowCount,
    )
}
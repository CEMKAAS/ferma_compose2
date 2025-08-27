package com.zaroslikov.data.room.mapper.dto

import com.zaroslikov.data.room.dto.add.TitleAndSuffixDto
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain

fun TitleAndSuffixDto.toTitleAndSuffixDomain(): TitleAndSuffixDomain {
    return TitleAndSuffixDomain(title = this.title, suffix = this.suffix,)
}

fun TitleAndSuffixDomain.toTitleAndSuffixDto(): TitleAndSuffixDto {
    return TitleAndSuffixDto(title = this.title, suffix = this.suffix,)
}
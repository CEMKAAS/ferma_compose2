package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.shared.TitleCountSuffixDto
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix

fun TitleCountSuffixDto.toDomainTitleCountSuffix(): DomainTitleCountSuffix {
    return DomainTitleCountSuffix(title = this.title, count = this.count, suffix = this.suffix,)
}
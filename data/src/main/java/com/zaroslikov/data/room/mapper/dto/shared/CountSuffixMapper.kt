package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.shared.CountSuffixDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix

fun CountSuffixDto.toDomainCountSuffix(): DomainCountSuffix {
    return DomainCountSuffix(count = this.count, suffix = this.suffix,)
}
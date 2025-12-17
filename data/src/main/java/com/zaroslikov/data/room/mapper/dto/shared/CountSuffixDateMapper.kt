package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.shared.CountSuffixDateDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffixDate

fun CountSuffixDateDto.toDomainCountSuffixDate(): DomainCountSuffixDate {
    return DomainCountSuffixDate(count = this.count, suffix = this.suffix, date = this.date,)
}
package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.add.AnimalCountSuffixDto
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix

fun AnimalCountSuffixDto.toDomainAnimalCountSuffix(): DomainAnimalCountSuffix {
    return DomainAnimalCountSuffix(
        title = this.title,
        type = this.type,
        count = this.count,
        suffix = this.suffix,
    )
}
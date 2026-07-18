package com.zaroslikov.data.room.mapper.dto.add

import com.zaroslikov.data.room.dto.add.AddItemDto
import com.zaroslikov.data.room.dto.add.AddItemDto2
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto2

fun DomainAddItemDto2.toAddItemDto(): AddItemDto2 {
    return AddItemDto2(
        id = this.id,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        day = this.day,
        month = this.month,
        year = this.year,
        category = this.category,
        nameAnimal = this.nameAnimal,
        note = this.note,
    )
}

fun AddItemDto2.toDomainAddItemDto(): DomainAddItemDto2 {
    return DomainAddItemDto2(
        id = this.id,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        day = this.day,
        month = this.month,
        year = this.year,
        category = this.category,
        nameAnimal = this.nameAnimal,
        note = this.note,
        animalCountId = this.animalCountId,
    )
}
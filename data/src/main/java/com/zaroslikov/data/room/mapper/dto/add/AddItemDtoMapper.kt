package com.zaroslikov.data.room.mapper.dto.add

import com.zaroslikov.data.room.dto.add.AddItemDto
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto

fun DomainAddItemDto.toAddItemDto(): AddItemDto {
    return AddItemDto(
        id = this.id,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        day = this.day,
        month = this.month,
        year = this.year,
        price = this.price,
        category = this.category,
        animalId = this.animalId,
        nameAnimal = this.nameAnimal,
        note = this.note,
        idPT = this.idPT,
        animalCountId = this.animalCountId,
    )
}

fun AddItemDto.toDomainAddItemDto(): DomainAddItemDto {
    return DomainAddItemDto(
        id = this.id,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        day = this.day,
        month = this.month,
        year = this.year,
        price = this.price,
        category = this.category,
        animalId = this.animalId,
        nameAnimal = this.nameAnimal,
        note = this.note,
        idPT = this.idPT,
        animalCountId = this.animalCountId,
    )
}
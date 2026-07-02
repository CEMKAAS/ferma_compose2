package com.zaroslikov.data.room.mapper.dto.add

import com.zaroslikov.data.room.dto.add.AddTemplateDto
import com.zaroslikov.domain.models.dto.add.DomainAddTemplateDto

fun AddTemplateDto.toDomainAddTemplateDto(): DomainAddTemplateDto {
    return DomainAddTemplateDto(
        id = this.id,
        nameTemplate = this.nameTemplate,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        price = this.price,
        category = this.category,
        animalId = this.animalId,
        nameAnimal = this.nameAnimal,
        note = this.note,
        idPT = this.idPT,
    )
}
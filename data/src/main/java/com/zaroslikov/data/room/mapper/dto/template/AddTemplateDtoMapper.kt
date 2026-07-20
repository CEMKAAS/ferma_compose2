package com.zaroslikov.data.room.mapper.dto.template

import com.zaroslikov.data.room.dto.template.AddTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto

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
        pin = this.isPinned,
        isMultiProject = this.isMultiProject
    )
}
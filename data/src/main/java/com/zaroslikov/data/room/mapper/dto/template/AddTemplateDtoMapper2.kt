package com.zaroslikov.data.room.mapper.dto.template

import com.zaroslikov.data.room.dto.template.AddTemplateDto
import com.zaroslikov.data.room.dto.template.AddTemplateDto2
import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto2

fun AddTemplateDto2.toDomainAddTemplateDto(): DomainAddTemplateDto2 {
    return DomainAddTemplateDto2(
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
        pin = this.pin,
        isMultiProject = this.isMultiProject
    )
}
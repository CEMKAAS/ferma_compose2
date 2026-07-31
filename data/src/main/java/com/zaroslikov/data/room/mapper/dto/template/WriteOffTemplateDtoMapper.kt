package com.zaroslikov.data.room.mapper.dto.template

import com.zaroslikov.data.room.dto.template.WriteOffTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainWriteOffTemplateDto

fun WriteOffTemplateDto.toDomainWriteOffTemplateDto(): DomainWriteOffTemplateDto {
    return DomainWriteOffTemplateDto(
        id = this.id,
        nameTemplate = this.nameTemplate,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        price = this.price,
        priceAll = this.priceAll,
        priceSuffix = this.priceSuffix,
        category = this.category,
        writeOffStatus = this.writeOffStatus,
        note = this.note,
        idPT = this.idPT,
        isPinned = this.isPinned,
        isMultiProject = this.isMultiProject,
    )
}
package com.zaroslikov.data.room.mapper.dto.template

import com.zaroslikov.data.room.dto.template.SaleTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainSaleTemplateDto

fun SaleTemplateDto.toDomainSaleTemplateDto(): DomainSaleTemplateDto {
    return DomainSaleTemplateDto(
        id = id,
        nameTemplate = nameTemplate,
        title = title,
        count = count,
        countSuffix = countSuffix,
        price = price,
        priceAll = priceAll,
        priceSuffix = priceSuffix,
        category = category,
        buyer = buyer,
        note = note,
        idPT = idPT,
        isPinned = isPinned,
        isMultiProject = isMultiProject
    )
}
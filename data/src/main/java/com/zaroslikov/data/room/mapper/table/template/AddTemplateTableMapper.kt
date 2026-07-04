package com.zaroslikov.data.room.mapper.table.template

import com.zaroslikov.data.room.table.ferma.templateOne.TemplateTable
import com.zaroslikov.domain.models.table.template.DomainTemplateTable

fun DomainTemplateTable.toAddTemplateTable(): TemplateTable {
    return TemplateTable(
        id = this.id,
        templateType = this.templateType,
        nameTemplate = this.nameTemplate,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        price = this.price,
        priceSuffix = this.priceSuffix,
        category = this.category,
        animalId = this.animalId,
        note = this.note,
        idPT = this.idPT,
    )
}

fun TemplateTable.toDomainAddTemplateTable(): DomainTemplateTable {
    return DomainTemplateTable(
        id = this.id,
        templateType = this.templateType,
        nameTemplate = this.nameTemplate,
        title = this.title,
        count = this.count,
        countSuffix = this.countSuffix,
        price = this.price,
        priceSuffix = this.priceSuffix,
        category = this.category,
        animalId = this.animalId,
        note = this.note,
        idPT = this.idPT,
    )
}
package com.zaroslikov.data.room.mapper.table.template

import com.zaroslikov.data.room.table.ferma.templateOne.AddTemplateTable
import com.zaroslikov.domain.models.table.template.DomainAddTemplateTable

fun DomainAddTemplateTable.toAddTemplateTable(): AddTemplateTable {
    return AddTemplateTable(
        id = this.id,
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

fun AddTemplateTable.toDomainAddTemplateTable(): DomainAddTemplateTable {
    return DomainAddTemplateTable(
        id = this.id,
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
package com.zaroslikov.domain.models.table.template

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType

data class DomainTemplateTable(
    val id: Long = 0,
    val templateType: TemplateType,
    val nameTemplate: String,
    val title: String?,
    val count: Double?,
    val countSuffix: Suffix?,
    val price: Double?,
    val priceSuffix: Suffix?,
    val category: String?,
    val animalId: Long?,
    val note: String?,
    val idPT: Long
)

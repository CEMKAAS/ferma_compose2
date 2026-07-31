package com.zaroslikov.domain.models.table.template

import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import kotlinx.serialization.Serializable

@Serializable
data class DomainTemplateTable(
    val id: Long = 0,
    val templateType: TemplateType,
    val nameTemplate: String,
    val title: String?,
    val count: Double?,
    val countSuffix: Suffix?,
    val price: Double?,
    val priceAll: Double? = null,
    val priceSuffix: Suffix?,
    val isDate: Boolean,
    val category: String?,
    val animalId: Long? = null,
    val animalName: String? = null,
    val note: String?,
    val buyer: String? = null,
    val productOrigin: ProductOrigin? = null,
    val writeOffStatus: Boolean? = null,
    val isPinned: Boolean,
    val isMultiProjectTemplate: Boolean,
    val idPT: Long,
)

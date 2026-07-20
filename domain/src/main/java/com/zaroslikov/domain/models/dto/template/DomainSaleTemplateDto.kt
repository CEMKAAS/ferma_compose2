package com.zaroslikov.domain.models.dto.template

import com.zaroslikov.domain.models.enums.Suffix

data class DomainSaleTemplateDto(
    val id: Long = 0,
    val nameTemplate: String,
    val title: String? = "",
    val count: Double? = 0.0,
    val countSuffix: Suffix? = Suffix.KILOGRAM,
    val price: Double? = 0.0,
    val priceAll: Double? = null,
    val category: String?,
    val buyer: String? = null,
    val note: String? = "",
    val idPT: Long = 0,
    val isPinned: Boolean = false,
    val isMultiProject: Boolean = false
)
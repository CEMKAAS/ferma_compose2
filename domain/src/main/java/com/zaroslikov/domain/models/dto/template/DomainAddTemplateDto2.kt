package com.zaroslikov.domain.models.dto.template

import com.zaroslikov.domain.models.enums.Suffix

data class DomainAddTemplateDto2(
    val id: Long = 0,
    val nameTemplate: String,
    val title: String? = "",
    val count: Double? = 0.0,
    val countSuffix: Suffix? = Suffix.KILOGRAM,
    val price: Double? = 0.0,
    val category: String?,
    val animalId: Long? = null,
    val nameAnimal: String? = null,
    val note: String? = "",
    val idPT: Long = 0,
    val pin: Boolean = false,
    val isMultiProject: Boolean = false
)
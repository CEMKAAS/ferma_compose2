package com.zaroslikov.domain.models.DomainAnimalTable

import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalWithCount(
    val id: Long,
    val name: String,
    val type: String,
    val date: String,
    val dateFactory: String?,
    val group: Boolean,
    val sex: Boolean,
    val count: String?,
    val suffix: String?,
    val foodDay: Double,
    val foodDaySuffix: Suffix
)

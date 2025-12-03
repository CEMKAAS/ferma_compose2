package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix


data class DomainWriteOffTable(
    val id: Long = 0,
    val title: String,
    val count: Double,
    val countSuffix: Suffix,
    val price: Double? = null,
    val priceAll: Double? = null,
    val category: String = "",
    val day: Int,
    val month: Int,
    val year: Int,
    val status: Boolean,
    val note: String,
    val idPT: Long,
    val animalCountId: Long? = null
)
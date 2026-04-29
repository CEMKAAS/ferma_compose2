package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainWriteOffTable(
    val id: Long = 0,
    override val title: String,
    override val count: Double,
    override val countSuffix: Suffix,
    override val price: Double? = null,
    override val priceAll: Double? = null,
    val priceSuffix: Suffix? = null,
    val category: String = "",
    val day: Int,
    val month: Int,
    val year: Int,
    val status: Boolean,
    val note: String,
    val idPT: Long,
    val animalCountId: Long? = null
) : BaseProductSection
package com.zaroslikov.domain.models

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainSaleTable(
    val id: Long = 0,
    override val title: String,
    override val count: Double,
    override val countSuffix: Suffix,
    override val price: Double,
    override val priceAll: Double? = null,
    val priceSuffix: Suffix = Suffix.RUBLE,
    val day: Int,
    val month: Int,
    val year: Int,
    val category: String,
    val buyer: String? = null,
    val note: String,
    val idPT: Long,
    val animalId: Long? = null,
    val animalCountId: Long? = null,
) : BaseProductSection
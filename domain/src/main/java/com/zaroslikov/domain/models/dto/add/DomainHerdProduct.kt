package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix

/** Продукция, полученная от члена сборной группы. */
data class DomainHerdProduct(
    val animalId: Long,
    override val title: String,
    override val count: Double,
    override val countSuffix: Suffix,
    override val price: Double?,
    override val priceAll: Double? = null
) : BaseProductSection

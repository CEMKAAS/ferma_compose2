package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalCountSuffix(
    override val title: String,
    val type: String,

    override val count: Double,
    override val countSuffix: Suffix,

    override val price: Double?,
    override val priceAll: Double?
) : BaseProductSection

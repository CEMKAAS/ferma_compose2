package com.zaroslikov.domain.models.dto.finance

import com.zaroslikov.domain.models.dto.BaseProductSection
import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalWitchCountAndImage(
    override val title: String,
    val type: String,

    override val count: Double,
    override val countSuffix: Suffix,

    override val price: Double?,
    override val priceAll: Double?,
    val currentIcon: Int?,
    val imagePath: String?
) : BaseProductSection
package com.zaroslikov.domain.models.dto.crm

import com.zaroslikov.domain.models.enums.Suffix

/** Товар, купленный покупателем: суммарное количество и выручка. */
data class DomainCrmBuyerProduct(
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val total: Double,
)

package com.zaroslikov.domain.models.dto.sale

data class DomainBuyerPrice(
    val buyer: String,
    val price: String,
    val count: String,
    val suffix: String,
)
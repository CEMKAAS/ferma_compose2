package com.zaroslikov.data.room.mapper.dto.sale

import com.zaroslikov.data.room.dto.sale.BuyerPriceDto
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice

fun BuyerPriceDto.toDomainBuyerPrice(): DomainBuyerPrice {
    return DomainBuyerPrice(
        buyer = this.buyer,
        price = this.price,
        count = this.count,
        suffix = this.suffix,
    )
}
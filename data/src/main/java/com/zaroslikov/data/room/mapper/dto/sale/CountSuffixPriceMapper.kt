package com.zaroslikov.data.room.mapper.dto.sale

import com.zaroslikov.data.room.dto.sale.CountSuffixPriceDto
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPrice

fun CountSuffixPriceDto.toDomainCountSuffixPrice(): DomainCountSuffixPrice {
    return DomainCountSuffixPrice(count = this.count, suffix = this.suffix, price = this.price,)
}
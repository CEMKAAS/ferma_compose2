package com.zaroslikov.data.room.mapper.dto.sale

import com.zaroslikov.data.room.dto.sale.CountSuffixPriceDateDto
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate

fun CountSuffixPriceDateDto.toDomainCountSuffixPriceDate(): DomainCountSuffixPriceDate {
    return DomainCountSuffixPriceDate(
        count = this.count,
        suffix = this.suffix,
        date = this.date,
        price = this.price,
    )
}
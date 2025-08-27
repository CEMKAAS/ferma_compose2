package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.shared.TitleSuffixPriceDto
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice

fun TitleSuffixPriceDto.toDomainTitleSuffixPrice(): DomainTitleSuffixPrice {
    return DomainTitleSuffixPrice(title = this.title, suffix = this.suffix, price = this.price,)
}
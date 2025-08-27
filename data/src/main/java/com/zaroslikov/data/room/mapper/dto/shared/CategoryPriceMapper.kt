package com.zaroslikov.data.room.mapper.dto.shared

import com.zaroslikov.data.room.dto.shared.CategoryPriceDto
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice

fun CategoryPriceDto.toDomainCategoryPrice(): DomainCategoryPrice {
    return DomainCategoryPrice(category = this.category, price = this.price,)
}
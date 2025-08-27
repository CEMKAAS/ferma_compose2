package com.zaroslikov.data.room.mapper.dto.sale

import com.zaroslikov.data.room.dto.sale.TitleSaleDto
import com.zaroslikov.domain.models.dto.sale.TitleSaleDomain

fun TitleSaleDto.toTitleSaleDomain(): TitleSaleDomain {
    return TitleSaleDomain(title = this.title, suffix = this.suffix, category = this.category,)
}
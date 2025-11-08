package com.zaroslikov.data.room.mapper.dto.sale

import com.zaroslikov.data.room.dto.sale.BrieflySaleDto
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain

fun BrieflySaleDto.toBrieflySaleDomain(): BrieflySaleDomain {
    return BrieflySaleDomain(
        title = this.title,
        count = this.count,
        suffix = this.suffix,
        price = this.price,
        rowCount = this.rowCount
    )
}
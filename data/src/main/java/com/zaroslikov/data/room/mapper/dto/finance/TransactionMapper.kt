package com.zaroslikov.data.room.mapper.dto.finance

import com.zaroslikov.data.room.dto.finance.TransactionDto
import com.zaroslikov.domain.models.dto.finance.DomainTransaction

fun TransactionDto.toDomainTransaction(): DomainTransaction {
    return DomainTransaction(
        value = this.count,
        suffix = this.suffix,
        price = this.price,
        priceAll = this.priceAll,
        category = this.category,
        buyer = this.buyer,
        data = this.date,
        categoryFinance = this.categoryFinance,
        animal = this.animal,
    )
}
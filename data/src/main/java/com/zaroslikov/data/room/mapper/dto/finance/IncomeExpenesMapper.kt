package com.zaroslikov.data.room.mapper.dto.finance

import com.zaroslikov.data.room.dto.finance.IncomeExpensesDto
import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses

fun IncomeExpensesDto.toDomainIncomeExpenses(): DomainIncomeExpenses {
    return DomainIncomeExpenses(
        title = this.title,
        count = this.count,
        suffix = this.suffix,
        price = this.price,
        date = this.date,
        category = this.category
    )
}
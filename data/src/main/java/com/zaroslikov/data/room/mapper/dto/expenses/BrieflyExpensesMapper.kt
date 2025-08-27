package com.zaroslikov.data.room.mapper.dto.expenses

import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDto

fun BrieflyExpensesDomain.toBrieflyExpensesDto(): BrieflyExpensesDto {
    return BrieflyExpensesDto(
        price = this.price,
        count = this.count,
        title = this.title,
        suffix = this.suffix,
    )
}

fun BrieflyExpensesDto.toBrieflyExpensesDomain(): BrieflyExpensesDomain {
    return BrieflyExpensesDomain(
        title = this.title,
        count = this.count,
        price = this.price,
        suffix = this.suffix,
    )
}
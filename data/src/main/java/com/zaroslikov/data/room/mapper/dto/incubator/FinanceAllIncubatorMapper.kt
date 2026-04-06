package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.FinanceAllIncubator
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceAllIncubator

fun FinanceAllIncubator.toDomainFinanceAllIncubator(): DomainFinanceAllIncubator {
    return DomainFinanceAllIncubator(
        income = this.income,
        expenses = this.expenses,
        postedEgg = this.postedEgg,
        lossesEgg = this.lossesEgg,
    )
}
package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.FinanceIncubatorHistoryDto
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorHistory

fun FinanceIncubatorHistoryDto.toDomainFinanceIncubatorHistory(): DomainFinanceIncubatorHistory {
    return DomainFinanceIncubatorHistory(
        title = this.title,
        type = this.type,
        breed = this.breed,
        countEgg = this.countEgg,
        profit = this.profit,
        income = this.income,
        chicks = this.chicks,
        expenses = this.expenses,
        priceOneEgg = this.priceOneEgg,
        postedPrice = this.postedPrice,
        postedEgg = this.postedEgg,
        lossesPrice = this.lossesPrice,
        lossesEgg = this.lossesEgg,
    )
}
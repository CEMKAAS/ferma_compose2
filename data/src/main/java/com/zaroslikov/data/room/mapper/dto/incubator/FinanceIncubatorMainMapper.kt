package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.FinanceIncubatorMainDto
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorMain

fun FinanceIncubatorMainDto.toDomainFinanceIncubatorMain(): DomainFinanceIncubatorMain {
    return DomainFinanceIncubatorMain(
        profit = this.profit,
        income = this.income,
        chicks = this.chicks,
        expenses = this.expenses,
        incubator = this.incubator,
        eggsPrice = this.eggsPrice,
        postedPrice = this.postedPrice,
        lossesPrice = this.lossesPrice,
        postedEgg = this.postedEgg,
        lossesEgg = this.lossesEgg,
        averageEggPrice = this.averageEggPrice,
        averageChicksPrice = this.averageChicksPrice,
        costChicksPrice = this.costChicksPrice
    )
}
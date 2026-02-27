package com.zaroslikov.domain.models.dto.incubator

data class DomainFinanceIncubatorMain(
    val profit: Double = 0.0,
    val income: Double = 0.0,
    val chicks: Int = 0,
    val expenses: Double = 0.0,
    val incubator: Double = 0.0,
    val eggsPrice: Double = 0.0,
    val postedPrice: Double = 0.0,
    val lossesPrice: Double = 0.0,
    val postedEgg: Int = 0,
    val lossesEgg: Int = 0,
    val averageEggPrice: Double = 0.0,
    val averageChicksPrice: Double = 0.0,
    val costChicksPrice: Double = 0.0
)

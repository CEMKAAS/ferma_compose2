package com.zaroslikov.fermacompose2.Domain.models

import com.zaroslikov.fermacompose2.supportFun.dateTodayArray

data class DomainAddTable(
    val id: Long = 0,
    val title: String = "",
    val count: String = "",
    var day: Int = dateTodayArray()[0],
    val mount: Int = dateTodayArray()[1],
    val year: Int = dateTodayArray()[2],
    val priceAll: String = "",
    var suffix: String = "кг.", //TODO
    var category: String = "Без категории",//TODO
    var idAnimal: Long = 0,
    var animal: String = "",
    val note: String = "",
    val idPT: Long = 0,
)
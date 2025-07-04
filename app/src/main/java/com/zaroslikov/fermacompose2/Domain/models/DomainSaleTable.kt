package com.zaroslikov.fermacompose2.Domain.models

import com.zaroslikov.fermacompose2.supportFun.dateTodayArray

data class DomainSaleTable (
    val id: Long = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = dateTodayArray()[0],
    val mount: Int = dateTodayArray()[1],
    val year: Int = dateTodayArray()[2],
    val priceAll: String = "",
    var suffix: String = "",
    var category: String = "",
    var buyer: String = "",
    var note: String = "",
    val idPT: Long = 0,
    val animalCountId : Long? = null
)
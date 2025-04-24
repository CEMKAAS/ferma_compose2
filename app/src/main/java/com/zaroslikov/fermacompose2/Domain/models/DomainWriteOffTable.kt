package com.zaroslikov.fermacompose2.Domain.models

data class DomainWritOffTable(
    val id: Int = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = 0,  // день
    val mount: Int = 0, // месяц
    val year: Int = 0, // время
    val priceAll: String = "",
    val idPT: Int = 0,
    var suffix: String = "",
    val status: Int = 0,
    val note: String = ""
)

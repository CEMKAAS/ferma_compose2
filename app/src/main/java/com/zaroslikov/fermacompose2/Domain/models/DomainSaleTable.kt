package com.zaroslikov.fermacompose2.Domain.models

data class DomainSaleTable (
    val id: Long = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = 0,  // день
    val mount: Int = 0, // месяц
    val year: Int = 0, // время
    val priceAll: String = "",
    var suffix: String = "",
    var category: String = "",
    var buyer: String = "",
    var note: String = "",
    val idPT: Long = 0,
)
package com.zaroslikov.fermacompose2.Domain.models

data class DomainAddTable(
        val id: Int = 0,
        val title: String = "",
        val count: Double =0.0,
        val day: Int = 0,
        val mount: Int = 0,
        val year: Int = 0,
        val priceAll: Double = 0.0,
        var suffix: String = "",
        var category: String = "",
        var idAnimal: Long = 0,
        var animal: String = "",
        val note: String = "",
        val idPT: Int = 0,
    )

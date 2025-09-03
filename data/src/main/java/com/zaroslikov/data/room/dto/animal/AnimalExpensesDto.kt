package com.zaroslikov.data.room.dto.animal

data class AnimalExpensesDto(
    val id: Long,
    val name: String,
    val foodDay: Double,
    val foodDaySuffix: String,
    val countAnimal: Int,
    val idExpensesAnimal: Long,
    val ps: Boolean = false,
    val presentException: Double = 0.0,
)
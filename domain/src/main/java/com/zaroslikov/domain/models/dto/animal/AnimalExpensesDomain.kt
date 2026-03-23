package com.zaroslikov.data.room.dto.animal

import com.zaroslikov.domain.models.enums.Suffix

data class AnimalExpensesDomain(
    val id: Long,
    val name: String,
    val type: String,
    val foodDay: Double,
    val foodDaySuffix: Suffix,
    val countAnimal: Int,
    val idExpensesAnimal: Long,
    val ps: Boolean = false,
    val presentException: Double = 0.0,
)
package com.zaroslikov.domain.models.dto.animal

data class AnimalVaccinationExpensesDomain(
    val id: Long = 0,
    val vaccination: String = "",
    val countVaccination: Int = 1,
    val date: String = "",
    val nextDate: String? = null,
    val note: String = "",
    val idAnimal: Long = 0,
    //Expenses
    val price: Double? = null,
    val priceAll: Double? = null,
)

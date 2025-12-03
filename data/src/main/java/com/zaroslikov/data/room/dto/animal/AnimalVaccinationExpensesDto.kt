package com.zaroslikov.data.room.dto.animal

import androidx.room.ColumnInfo

data class AnimalVaccinationExpensesDto(
    val id: Long,
    val vaccination: String,
    @ColumnInfo(name = "count_vaccination")
    val countVaccination: Int,
    val date: String,
    @ColumnInfo(name = "next_vaccination")
    val nextDate: String?,
    val note: String,
    @ColumnInfo(name = "animal_id")
    val idAnimal: Long,
    //Expenses
    val price: Double?,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
)

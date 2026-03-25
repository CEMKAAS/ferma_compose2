package com.zaroslikov.domain.models

import com.zaroslikov.domain.models.enums.Suffix


data class DomainExpensesTable(
    val id: Long = 0,
    val title: String, // название
    val count: Double, // Кол-во
    val day: Int,  // день
    val month: Int, // месяц
    val year: Int, // время
    val price: Double,
    val priceAll: Double? = null,
    val countSuffix: Suffix,
    val category: String,
    val note: String,

    val isShowFood: Boolean, // Показывать на складе еду
    val feedFood: Double? = null, // Ежедневный расход еды
    val feedFoodSuffix: Suffix? = null, // Суффикс ежедневного расхода
    val countAnimal: Int? = null, // Кол-во животных
    val foodDesignedDay: Int? = null, // Кол-во дней
    val lastDayFood: String? = null, //Последний день еды

    val weight: Double? = null,
    val weightSuffix: Suffix? = null,
    val idPT: Long,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
)


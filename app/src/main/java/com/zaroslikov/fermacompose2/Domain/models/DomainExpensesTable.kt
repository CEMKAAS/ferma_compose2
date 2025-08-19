package com.zaroslikov.fermacompose2.Domain.models


data class DomainExpensesTable(
    val id: Long,
    val title: String, // название
    val count: Double , // Кол-во
    val day: Int,  // день
    val month: Int, // месяц
    val year: Int, // время
    val price: Double,
    val priceAll: Double?,
    val countSuffix: String,
    val category: String,
    val note: String,
    val isShowFood: Boolean, // Показывать на складе еду
    val isShowFoodHand: Boolean, // указать вручную
    val isShowWarehouse: Boolean, // Показывать на складе (Перекупство)
    val isShowAnimals: Boolean, // Связывает животных
    val feedFood: Double?, // Ежедневный расход еды
    val feedFoodSuffix: String?, // Суффикс ежедневного расхода
    val countAnimal: Int?, // Кол-во животных
    val foodDesignedDay: Int?, // Кол-во дней
    val lastDayFood: String?, //Последний день еды
    val weight: Double?,
    val weightSuffix: String?,
    val idPT: Long,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
)


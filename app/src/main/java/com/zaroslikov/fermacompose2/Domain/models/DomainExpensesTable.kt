package com.zaroslikov.fermacompose2.Domain.models

import android.util.Log
import com.zaroslikov.fermacompose2.supportFun.DataFourWeight
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray

data class DomainExpensesTable(
    val id: Long = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = dateTodayArray()[0],  // день
    val mount: Int = dateTodayArray()[1], // месяц
    val year: Int = dateTodayArray()[2], // время
    val priceAll: String = "", // кол-во товара
    var suffix: String = "",
    var category: String = "",
    val note: String = "",
    val showFood: Boolean = false, // Показывать на складе еду
    val showWarehouse: Boolean = false, // Показывать на складе (Перекупство)
    val showAnimals: Boolean = false, // Связывает животных
    val dailyExpensesFoodAndCount: Boolean = false, // указать вручную
    val dailyExpensesFood: String = "", // Ежедневный расход еды
    val dailyExpensesFoodSuffix: String = "", // Суффикс ежедневного расхода
    val countAnimal: String = "", // Кол-во животных
    val foodDesignedDay: String = "", // Кол-во дней
    val lastDayFood: String = "", //Последний день еды
    val idPT: Long = 0,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
)


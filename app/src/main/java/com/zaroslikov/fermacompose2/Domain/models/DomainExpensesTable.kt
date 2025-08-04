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
    val countAnimal: String = "", // Кол-во животных
    val foodDesignedDay: String = "", // Кол-во дней
    val lastDayFood: String = "", //Последний день еды
    val idPT: Long = 0,
    val animalId: Long? = null,
    val animalVaccinationId: Long? = null,
    val animalCountId: Long? = null,
    val error: Error = Error(),
    val weight: String = "",
    val weightSuffix: String = "",
    val isAutoWeight: Boolean = false
) {
    val hasAnyError: Boolean
        get() = error.hasAnyError(dailyExpensesFoodAndCount)

    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false,
        val isErrorDailyExpensesFood: Boolean = false,
        val isErrorCountAnimal: Boolean = false,
    ) {
        fun hasAnyError(dailyExpensesFoodAndCount: Boolean): Boolean {
            return if (dailyExpensesFoodAndCount) {
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorDailyExpensesFood || isErrorCountAnimal
            } else {
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
            }
        }
    }

    fun validate(): DomainExpensesTable {
        val error = Error(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/"),
            isErrorCount = count.isBlank(),
            isErrorPrice = priceAll.isBlank(),
            isErrorDailyExpensesFood = dailyExpensesFood.isBlank(),
            isErrorCountAnimal = countAnimal.isBlank(),
        )
        return this.copy(error = error)
    }

    fun validateTitle(): DomainExpensesTable {
        return this.copy(
            error = error.copy(
                isErrorTitle = title.isBlank(),
                isErrorSlash = title.contains("/")
            )
        )
    }

    fun validateCount(): DomainExpensesTable {
        Log.i("isError", "validateCount: ${count.isBlank()} ")
        return this.copy(error = error.copy(isErrorCount = count.isBlank()))
    }

    fun validatePrice(): DomainExpensesTable {
        return this.copy(
            error = error.copy(
                isErrorPrice = priceAll.isBlank()
            )
        )
    }

    fun validateDailyExpensesFood(): DomainExpensesTable {
        return this.copy(
            error = error.copy(
                isErrorDailyExpensesFood = dailyExpensesFood.isBlank()
            )
        )
    }

    fun validateCountAnimal(): DomainExpensesTable {
        return this.copy(
            error = error.copy(
                isErrorCountAnimal = countAnimal.isBlank()
            )
        )
    }
}

package com.zaroslikov.fermacompose2.Domain.models

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
    val showWarehouse: Boolean = true, // Показывать на складе
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
    val error: Error = Error()
) {
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false,
        val isErrorDailyExpensesFood: Boolean = false,
        val isErrorCountAnimal: Boolean = false,
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorDailyExpensesFood || isErrorCountAnimal
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
            error = Error(
                isErrorTitle = title.isBlank(),
                isErrorSlash = title.contains("/")
            )
        )
    }

    fun validateCount(): DomainExpensesTable {
        return this.copy(error = Error(isErrorCount = count.isBlank()))
    }

    fun validatePrice(): DomainExpensesTable {
        return this.copy(
            error = Error(
                isErrorPrice = priceAll.isBlank()
            )
        )
    }

    fun validateDailyExpensesFood(): DomainExpensesTable {
        return this.copy(
            error = Error(
                isErrorDailyExpensesFood = dailyExpensesFood.isBlank()
            )
        )
    }

    fun validateCountAnimal(): DomainExpensesTable {
        return this.copy(
            error = Error(
                isErrorCountAnimal = countAnimal.isBlank()
            )
        )
    }
}

package com.zaroslikov.fermacompose2.ui.sections.expenses

import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting

data class ExpensesEntryState(
    val weight: String = "",
    val weightSuffix: String = "",

    val countInWarehouse: DomainPairDataDoubleSting = DomainPairDataDoubleSting(),
    val feedFoodChip: String = "",
    val feedFoodChipSuffix: String = "",
    val countAnimalChip: String = "",

    val feedFoodInput: String = "",
    val feedFoodInputSuffix: String = "",
    val countAnimalInput: String = "",

    val daysFood: Int = 0,
    val dateEndFood: String = "",
    val isAutoWeight: Boolean = false, // Расчет веса
    val isAutoCalculate: Boolean = false, // Расчет цены
    val isIndicatorsValue: Boolean = false,
    val animalList2: List<AnimalExpensesList2> = emptyList(),
    val domainExpensesTable: DomainExpensesTable = DomainExpensesTable(),
    val error: ValidationError = ValidationError()
) {
    val hasAnyError: Boolean
        get() = error.hasAnyError(domainExpensesTable.dailyExpensesFoodAndCount)

    data class ValidationError(
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
}

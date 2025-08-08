package com.zaroslikov.fermacompose2.ui.sections.expenses

import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.dateToday

data class ExpensesEntryState(
    val title: String = "",
    val count: String = "",
    val date: String = dateToday(),
    val price: String = "",
    val priceAll: String = "",
    val countSuffix: String = "",
    val category: String = "",
    val note: String = "",
    val isShowFood: Boolean = false,
    val isShowFoodHand: Boolean = false,
    val isShowWarehouse: Boolean = false,
    val isShowAnimals: Boolean = false,

    val feedFoodChip: String = "",
    val feedFoodChipSuffix: String = "",
    val countAnimalChip: String = "",

    val feedFoodInput: String = "",
    val feedFoodInputSuffix: String = "",
    val countAnimalInput: String = "",

    val daysFood: Int = 0,
    val dateEndFood: String = "",

    val weight: String = "",
    val weightSuffix: String = "",
    val isAutoWeight: Boolean = false,
    val isAutoPrice: Boolean = false,


    val countInWarehouse: DomainPairDataDoubleSting = DomainPairDataDoubleSting(),

    val isIndicatorsValue: Boolean = false,
    val animalList2: List<AnimalExpensesList2> = emptyList(),
//    val domainExpensesTable: DomainExpensesTable = DomainExpensesTable(),
    val error: ValidationError = ValidationError()
) {
    val hasAnyError: Boolean
        get() = error.hasAnyError(isShowFoodHand)

    data class ValidationError(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false,
        val isErrorDailyExpensesFood: Boolean = false,
        val isErrorCountAnimal: Boolean = false,
    ) {
        fun hasAnyError(isShowFoodHand: Boolean): Boolean {
            return if (isShowFoodHand) {
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorDailyExpensesFood || isErrorCountAnimal
            } else {
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
            }
        }
    }
}

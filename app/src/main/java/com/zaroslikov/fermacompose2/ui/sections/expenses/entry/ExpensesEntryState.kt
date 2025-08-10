package com.zaroslikov.fermacompose2.ui.sections.expenses.entry

import android.util.Log
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import javax.inject.Inject


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

    val isEntry: Boolean = false,
    val isIndicatorsValue: Boolean = false,
    val animalList2: List<AnimalExpensesList2> = emptyList(),
    val error: ValidationError = ValidationError(),
) {
    val hasAnyError: Boolean
        get() = error.hasAnyError(isShowFood, isShowFoodHand, isShowAnimals)

    data class ValidationError(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false,
        val isErrorFood: Boolean = false,
        val isErrorAnimal : Boolean = false,
        val isErrorDailyExpensesFood: Boolean = false,
        val isErrorCountAnimal: Boolean = false,
    ) {
        fun hasAnyError(isShowFood: Boolean, isShowFoodHand: Boolean, isShowAnimals: Boolean): Boolean {
            Log.i("expenses", "isShowFood: $isShowFood")
            Log.i("expenses", "isShowFoodHand: $isShowFoodHand")
            Log.i("expenses", "isShowAnimals: $isShowAnimals")
            return when {
                isShowFoodHand ->
                    isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorDailyExpensesFood || isErrorCountAnimal

                isShowFood ->
                    isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorFood

                isShowAnimals->
                    isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorAnimal

                else -> isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
            }
        }
    }
}

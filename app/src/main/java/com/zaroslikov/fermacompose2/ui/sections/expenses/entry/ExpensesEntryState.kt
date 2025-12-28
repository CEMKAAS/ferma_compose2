package com.zaroslikov.fermacompose2.ui.sections.expenses.entry

import android.util.Log
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber


data class ExpensesEntryState(
    val title: String = "",
    val count: String = "",
    val date: String = dateToday(),
    val price: String = "",
    val priceAll: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val category: String = "",
    val note: String = "",
    val isShowFood: Boolean = false,
    val isShowFoodHand: Boolean = false,
    val isShowWarehouse: Boolean = false,
    val isShowAnimals: Boolean = false,

    val feedFoodChip: String = "",
    val feedFoodChipSuffix: Suffix = Suffix.GRAM,
    val countAnimalChip: String = "",

    val feedFoodInput: String = "",
    val feedFoodInputSuffix: Suffix = Suffix.GRAM,
    val countAnimalInput: String = "",

    val daysFood: Int = 0,
    val dateEndFood: String = "",

    val weight: String = "",
    val weightSuffix: Suffix = Suffix.KILOGRAM,
    val isAutoWeight: Boolean = false,
    val isAutoPrice: Boolean = false,

    val pickList: PickList = PickList(),
    val countInWarehouse: DomainCountSuffix = DomainCountSuffix(0.0, Suffix.PIECES),

    override val isEntry: Boolean = false,
    val isIndicatorsValue: Boolean = false,

    override val error: ValidationError = ValidationError(),
    override val navigate: UiEvent? = null,
    override val isLoading: Boolean = false
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError(isShowFood, isShowFoodHand, isShowAnimals)

    data class ValidationError(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false,
        val isErrorFood: Boolean = false,
        val isErrorAnimal: Boolean = false,
        val isErrorDailyExpensesFood: Boolean = false,
        val isErrorCountAnimal: Boolean = false,
    ) : BaseError {

        fun hasAnyError(
            isShowFood: Boolean,
            isShowFoodHand: Boolean,
            isShowAnimals: Boolean
        ): Boolean {
            Log.i("expenses", "isShowFood: $isShowFood")
            Log.i("expenses", "isShowFoodHand: $isShowFoodHand")
            Log.i("expenses", "isShowAnimals: $isShowAnimals")
            return when {
                isShowFoodHand ->
                    isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorDailyExpensesFood || isErrorCountAnimal

                isShowFood ->
                    isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorFood

                isShowAnimals ->
                    isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorAnimal

                else -> isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
            }
        }
    }

    data class PickList(
        val titleList: List<TitleAndSuffixDomain> = emptyList(),
        val categoryList: List<String> = emptyList(),
        val animalList2: List<AnimalExpensesDomain> = emptyList(),
    )
}

fun ExpensesEntryState.updateFromDomain(
    domain: DomainExpensesTable
): ExpensesEntryState {
    val useDaily = domain.isShowFoodHand
    val isIndicatorsValue =
        setOf(domain.animalId, domain.animalVaccinationId, domain.animalCountId)
            .any { it != null }

    Log.i("expenses", "updateFromDomain: $isIndicatorsValue")
    Log.i("expenses", "animalId: ${domain.animalId}")
    Log.i("expenses", "animalVaccinationId: ${domain.animalVaccinationId}")
    Log.i("expenses", "animalCountId: ${domain.animalCountId}")
    Log.i("expenses", "feedFood: ${domain.feedFood}")
    Log.i("expenses", "feedFood: ${domain.weight?.formatNumber(false)}")

    return copy(
        title = domain.title,
        count = domain.count.formatNumber(false),
        date = formatDateToString(
            domain.day,
            domain.month,
            domain.year
        ),
        isAutoPrice = domain.priceAll != null,
        price = domain.price.formatNumber(false),
        priceAll = domain.priceAll?.formatNumber() ?: "",
        countSuffix = domain.countSuffix,
        category = domain.category,
        note = domain.note,
        isShowFood = domain.isShowFood,
        isShowFoodHand = domain.isShowFoodHand,
        isShowWarehouse = domain.isShowWarehouse,
        isShowAnimals = domain.isShowAnimals,
        countAnimalChip = if (!useDaily) domain.countAnimal?.formatNumber() ?: "" else "",
        feedFoodChip = if (!useDaily) domain.feedFood?.formatNumber() ?: "" else "",
        feedFoodChipSuffix = if (!useDaily) domain.feedFoodSuffix
            ?: feedFoodChipSuffix else feedFoodChipSuffix,
        countAnimalInput = if (useDaily) domain.countAnimal?.formatNumber(false) ?: "" else "",
        feedFoodInput = if (useDaily) domain.feedFood?.formatNumber(false) ?: "" else "",
        feedFoodInputSuffix = if (useDaily) domain.feedFoodSuffix
            ?: feedFoodInputSuffix else feedFoodInputSuffix,
        daysFood = domain.foodDesignedDay ?: 0,
        dateEndFood = domain.lastDayFood ?: "",
        isAutoWeight = domain.weight != null,
        weight = domain.weight?.formatNumber(false) ?: "",
        weightSuffix = domain.weightSuffix ?: weightSuffix,
        isIndicatorsValue = isIndicatorsValue
    )
}

fun ExpensesEntryState.updateForSave(
    id: Long = 0,
    itemIdPT: Long
): DomainExpensesTable {
    val title2 =
        when {
            isShowFood -> Triple(countAnimalChip, feedFoodChip, feedFoodChipSuffix)
            isShowFoodHand -> Triple(countAnimalInput, feedFoodInput, feedFoodInputSuffix)
            else -> Triple(null, null, null)
        }
    val dateList = date.split(".")
    return DomainExpensesTable(
        id = id,
        title = title.trim(),
        count = count.toConvertDbDouble(),
        day = dateList[0].toInt(),
        month = dateList[1].toInt(),
        year = dateList[2].toInt(),
        price = price.toConvertDbDouble(),
        priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
        countSuffix = countSuffix,
        category = category.trim(),
        note = note.trim(),
        isShowFood = isShowFood,
        isShowFoodHand = isShowFoodHand,
        isShowWarehouse = isShowWarehouse,
        isShowAnimals = isShowAnimals,
        countAnimal = title2.first?.toConvertZeroString()?.toConvertDbOnlyInt(),
        feedFood = title2.second?.toConvertZeroString()?.toConvertDbDouble(),
        feedFoodSuffix = title2.third,
        foodDesignedDay = if (isShowFood || isShowFoodHand) daysFood else null,
        lastDayFood = if (isShowFood || isShowFoodHand) dateEndFood else null,
        weight = if (isAutoWeight) weight.toConvertDbDouble() else null,
        weightSuffix = if (isAutoWeight) weightSuffix else null,
        idPT = itemIdPT,
    )
}


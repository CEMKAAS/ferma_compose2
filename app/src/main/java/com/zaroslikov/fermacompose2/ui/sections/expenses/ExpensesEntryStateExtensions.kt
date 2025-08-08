package com.zaroslikov.fermacompose2.ui.sections.expenses


import android.util.Log
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun ExpensesEntryState.validate(): ExpensesEntryState {

    val (feedFood, countAnimal) = if (isShowFoodHand) {
        feedFoodInput to countAnimalInput
    } else feedFoodChip to countAnimalChip

    val newError = ExpensesEntryState.ValidationError(
        isErrorTitle = title.isBlank(),
        isErrorSlash = title.contains("/"),
        isErrorCount = count.isBlank(),
        isErrorPrice = price.isBlank(),
        isErrorDailyExpensesFood = feedFood.isBlank(),
        isErrorCountAnimal = countAnimal.isBlank()
    )
    return this.copy(error = newError)
}

fun ExpensesEntryState.updateFromDomain(domain: DomainExpensesTable): ExpensesEntryState {
    val useDaily = domain.isShowFoodHand
    return copy(
        title = domain.title,
        count = domain.count.toString(),
        date = formatDateToString(
            domain.day,
            domain.month,
            domain.year
        ),
        price = domain.price.toString(),
        priceAll = domain.priceAll.toString(),
        countSuffix = domain.countSuffix,
        category = domain.category,
        note = domain.note,
        isShowFood = domain.isShowFood,
        isShowFoodHand = domain.isShowFoodHand,
        isShowWarehouse = domain.isShowWarehouse,
        isShowAnimals = domain.isShowAnimals,
        countAnimalChip = if (!useDaily) domain.countAnimal.toString() else "",
        feedFoodChip = if (!useDaily) domain.feedFood.toString() else "",
        feedFoodChipSuffix = if (!useDaily) domain.feedFoodSuffix else "",
        countAnimalInput = if (useDaily) domain.countAnimal.toString() else "",
        feedFoodInput = if (useDaily) domain.feedFood.toString() else "",
        feedFoodInputSuffix = if (useDaily) domain.feedFoodSuffix else "",
        daysFood = domain.foodDesignedDay,
        dateEndFood = domain.lastDayFood,
        isAutoWeight = domain.isAutoWeight,
        isAutoPrice = domain.isAutoPrice,
        weight = domain.weight.toString(),
        weightSuffix = domain.weightSuffix
    )
}

fun ExpensesEntryState.updateTitle(title: String): ExpensesEntryState {
    return copy(
        title = title,
        error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/")
        )
    )
}

fun ExpensesEntryState.updateCount(count: String): ExpensesEntryState {
    return copy(
        count = count,
        error = error.copy(
            isErrorCount = count.isBlank()
        )
    ).updatePriceAll()
}

fun ExpensesEntryState.updateCountSuffix(suffix: String): ExpensesEntryState {
    return copy(
        countSuffix = suffix
    )
}

fun ExpensesEntryState.updateAutoWeight(isAutoWeight: Boolean): ExpensesEntryState {
    return copy(
        isAutoWeight = isAutoWeight
    )
}

fun ExpensesEntryState.updateWeight(weight: String): ExpensesEntryState {
    return copy(
        weight = weight
    )
}

fun ExpensesEntryState.updateWeightSuffix(weightSuffix: String): ExpensesEntryState {
    return copy(
        weightSuffix = weightSuffix
    )
}

fun ExpensesEntryState.updatePrice(price: String): ExpensesEntryState {
    return copy(
        price = price,
        error = error.copy(
            isErrorPrice = price.isBlank()
        )
    ).updatePriceAll()
}

fun ExpensesEntryState.updateAutoPrice(isAutoCalculate: Boolean): ExpensesEntryState {
    return copy(isAutoPrice = isAutoCalculate).updatePriceAll()
}

fun ExpensesEntryState.updatePriceAll(): ExpensesEntryState {
    return copy(
        priceAll = if (isAutoPrice) (price.toConvertZeroDouble() * count.toConvertZeroDouble()).toString() else ""
    )
}

fun ExpensesEntryState.updateCategory(category: String): ExpensesEntryState {
    return copy(
        category = category
    )
}

fun ExpensesEntryState.updateDate(date: String): ExpensesEntryState {
    return copy(
        date = date
    )
}

fun ExpensesEntryState.updateNote(note: String): ExpensesEntryState {
    return copy(note = note)
}

fun ExpensesEntryState.updateShowFood(showFood: Boolean): ExpensesEntryState {
    return copy(
        animalList2 = animalList2.map { it.copy(ps = false) },
        isShowFood = showFood,
        isShowWarehouse = showFood,
        isShowAnimals = false
    )
}

fun ExpensesEntryState.updateDailyExpensesFoodAndCount(dailyExpensesFoodAndCount: Boolean): ExpensesEntryState {
    return copy(
        isShowFoodHand = dailyExpensesFoodAndCount
    )
}

fun ExpensesEntryState.updateDailyExpensesFood(dailyExpensesFood: String): ExpensesEntryState {
    return copy(
        feedFoodInput = dailyExpensesFood,
        error = error.copy(
            isErrorDailyExpensesFood = dailyExpensesFood.isBlank()
        )
    )
}

fun ExpensesEntryState.updateDailyExpensesFoodSuffix(dailyExpensesFoodSuffix: String): ExpensesEntryState {
    return copy(
        feedFoodInputSuffix = dailyExpensesFoodSuffix,
    )
}

fun ExpensesEntryState.updateCountAnimal(countAnimal: String): ExpensesEntryState {
    return copy(
        countAnimalInput = countAnimal,
        error = error.copy(
            isErrorCountAnimal = countAnimal.isBlank()
        )
    )
}

fun ExpensesEntryState.toggleAnimalChipSelection(
    toggledAnimal: AnimalExpensesList2
): ExpensesEntryState {
    val newSelected = !toggledAnimal.ps

    // Обновляем список животных с пересчетом presentException
    val updatedAnimals = animalList2.map {
        if (it.id == toggledAnimal.id) {
            val convertedFood =
                it.foodDay.convertWeight(it.foodDaySuffix, feedFoodChipSuffix)
            val dailyFood = convertedFood * it.countAnimal
            it.copy(
                ps = newSelected,
                presentException = if (newSelected) (it.foodDay / dailyFood) * 100.0 else 0.0
            )
        } else it
    }

    // Сумма по выбранным животным
    val updatedCountAnimal = updatedAnimals
        .filter { it.ps }
        .sumOf { it.countAnimal }
        .toString()

    val updatedDailyFood = updatedAnimals
        .filter { it.ps }
        .sumOf {
            it.foodDay.convertWeight(it.foodDaySuffix, feedFoodChipSuffix) * it.countAnimal
        }
        .toString()

    return this.copy(
        animalList2 = updatedAnimals,
        feedFoodChip = updatedCountAnimal,
        countAnimalChip = updatedDailyFood
    )
}

fun ExpensesEntryState.updateShowWarehouse(showWarehouse: Boolean): ExpensesEntryState {
    return copy(
        isShowWarehouse = showWarehouse
    )
}

fun ExpensesEntryState.updateShowAnimal(showAnimal: Boolean): ExpensesEntryState {
    return copy(
        animalList2 = animalList2.map { it.copy(ps = false) },
        isShowAnimals = showAnimal
    )
}

// Хелпер: Перераспределить presentException для всех выбранных
fun ExpensesEntryState.redistributeSelectedShare(totalShare: Double = 100.0): ExpensesEntryState {
    val selected = animalList2.filter { it.ps }
    val equalShare = if (selected.isNotEmpty()) totalShare / selected.size else 0.0

    val updatedList = animalList2.map {
        if (it.ps) it.copy(presentException = equalShare) else it.copy(presentException = 0.0)
    }

    return this.copy(
        animalList2 = updatedList
    )
}


// Хелпер: Обновить ps у одного животного
fun ExpensesEntryState.toggleAnimalSelection(animalId: Int): ExpensesEntryState {
    val updatedList = animalList2.map {
        if (it.id == animalId) it.copy(ps = !it.ps) else it
    }

    return this.copy(
        animalList2 = updatedList
    ).redistributeSelectedShare()
}


// Хелпер: Обновить presentException у одного животного, перераспределить остаток
fun ExpensesEntryState.updateAnimalSlider(
    animalId: Int,
    newValue: Double,
    totalShare: Double = 100.0
): ExpensesEntryState {
    val selectedAnimals = animalList2.filter { it.ps }
    val remainingShare = (totalShare - newValue).coerceAtLeast(0.0)
    val othersCount = selectedAnimals.size - 1

    val updatedList = animalList2.map {
        when {
            it.id == animalId -> it.copy(presentException = newValue)
            it.ps -> {
                val redistributed = if (othersCount > 0) remainingShare / othersCount else 0.0
                it.copy(presentException = redistributed)
            }

            else -> it.copy(presentException = 0.0)
        }
    }

    return this.copy(animalList2 = updatedList)
}

fun ExpensesEntryState.updateSettingDay(): ExpensesEntryState {
    val dailyExpensesFoodAndCount = isShowFoodHand

    val countTable = count.toConvertZeroDouble()
    val weight = weight.toConvertZeroDouble()

    val countAnimal = if (dailyExpensesFoodAndCount)
        countAnimalInput.toConvertZeroDouble() else countAnimalChip.toConvertZeroDouble()
    val dailyExpensesFood = if (dailyExpensesFoodAndCount)
        feedFoodInput.toConvertZeroDouble() else feedFoodChip.toConvertZeroDouble()

    val countProduct =
        if (isAutoPrice) {
            (countTable * weight)
                .convertWeight(
                    weightSuffix,
                    feedFoodChipSuffix
                )
        } else countTable.convertWeight(
            countSuffix,
            feedFoodChipSuffix
        )

    val foodDay =
        if (dailyExpensesFoodAndCount) dailyExpensesFood
            .convertWeight(
                feedFoodInputSuffix,
                feedFoodChipSuffix
            )
        else dailyExpensesFood

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateLocal = LocalDate.parse(date, formatter)

    var days =
        (if (dailyExpensesFoodAndCount) countProduct / (countAnimal * foodDay)
        else countProduct / foodDay).toLong()

    if (days > 1000) days = 1000
    val newDate = dateLocal.plusDays(days)

    return this.copy(daysFood = days.toInt(), dateEndFood = newDate.format(formatter))
}


fun ExpensesEntryState.updateForSave(
    id: Long = 0,
    itemIdPT: Long
): DomainExpensesTable {
    val title2 = if (isShowFoodHand) {
        Triple(countAnimalInput, feedFoodInput, feedFoodInputSuffix)
    } else Triple(countAnimalChip, feedFoodChip, feedFoodChipSuffix)

    Log.i("expenses", "updateForSave: $isShowAnimals")
//    Log.i("expenses", "updateForSave: $isShowFoodHand")
    val dateList = date.split(".")
    return DomainExpensesTable(
        id = id,
        title = title.trim(),
        count = count.toConvertDbDouble(),
        day = dateList[0].toInt(),
        month = dateList[1].toInt(),
        year = dateList[2].toInt(),
        price = price.toConvertDbDouble(),
        priceAll = priceAll.toDoubleOrNull(),
        countSuffix = countSuffix,
        category = category,
        note = note,
        isShowFood = isShowFood,
        isShowFoodHand = isShowFoodHand,
        isShowWarehouse = isShowWarehouse,
        isShowAnimals = isShowAnimals,
        countAnimal = title2.first.toConvertZeroString().toConvertDbOnlyInt(),
        feedFood = title2.second.toConvertZeroString().toConvertDbDouble(),
        feedFoodSuffix = title2.third,
        foodDesignedDay = daysFood,
        lastDayFood = dateEndFood,
        weight = weight.toConvertDbDouble(),
        weightSuffix = weightSuffix,
        isAutoWeight = isAutoWeight,
        isAutoPrice = isAutoPrice,
        idPT = itemIdPT,
    )
}

fun ExpensesEntryState.updateForSaveAnimalList(): ExpensesEntryState {
    val animalList = if (isShowFoodHand && isShowFood) emptyList()
    else animalList2

    Log.i("expenses", "updateForSaveAnimalList:$animalList")
    Log.i("expenses", "updateForSaveAnimalList:$animalList2")

    return copy(animalList2 = animalList)
}
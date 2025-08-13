package com.zaroslikov.fermacompose2.ui.sections.expenses.entry


import android.util.Log
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun ExpensesEntryState.validate(): ExpensesEntryState {

    val (feedFood, countAnimal) = if (isShowFoodHand) {
        feedFoodInput.isBlank() to countAnimalInput.isBlank()
    } else false to false

    val isErrorFood =
        if (isShowFood && !isShowFoodHand) animalList2.filter { it.foodDay != 0.0 }
            .none { it.ps == true } else false

    val isErrorAnimal =
        if (isShowAnimals) animalList2.none { it.ps == true } else false


    val newError = ExpensesEntryState.ValidationError(
        isErrorTitle = title.isBlank(),
        isErrorSlash = title.contains("/"),
        isErrorCount = count.isBlank(),
        isErrorPrice = price.isBlank(),
        isErrorFood = isErrorFood,
        isErrorAnimal = isErrorAnimal,
        isErrorDailyExpensesFood = feedFood,
        isErrorCountAnimal = countAnimal
    )

    return this.copy(error = newError)
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

fun ExpensesEntryState.updateTitleAndSuffix(pair: Pair<String, String>,  suffixSet: Set<String>): ExpensesEntryState {
    return copy(
        title = pair.first,
        countSuffix = pair.second,
        isShowFood = if (pair.second !in suffixSet && !isAutoWeight) false else isShowFood,
        isShowFoodHand = if (pair.second !in suffixSet && !isAutoWeight) false else isShowFoodHand,
        error = error.copy(
            isErrorTitle = pair.first.isBlank(),
            isErrorSlash = pair.first.contains("/")
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



fun ExpensesEntryState.updateCountSuffix(
    suffix: String,
    suffixSet: Set<String>
): ExpensesEntryState {
    return copy(
        countSuffix = suffix,
        isAutoWeight = if (suffix in suffixSet) false else isAutoWeight,
        isShowFood = if (suffix !in suffixSet && !isAutoWeight) false else isShowFood,
        isShowFoodHand = if (suffix !in suffixSet && !isAutoWeight) false else isShowFoodHand
    )
}

fun ExpensesEntryState.updateAutoWeight(
    isAutoWeight: Boolean,
    suffixSet: Set<String>
): ExpensesEntryState {
    return copy(
        isAutoWeight = isAutoWeight,
        isShowFood = if (countSuffix !in suffixSet && !isAutoWeight) false else isShowFood,
        isShowFoodHand = if (countSuffix !in suffixSet && !isAutoWeight) false else isShowFoodHand
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
        priceAll = if (isAutoPrice) (price.toConvertZeroDouble() * count.toConvertZeroDouble()).formatNumber() else "0"
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
        isShowAnimals = false,
        error = error.copy(
            isErrorFood = false,
            isErrorAnimal = false
        )
    )
}

fun ExpensesEntryState.updateDailyExpensesFoodAndCount(dailyExpensesFoodAndCount: Boolean): ExpensesEntryState {
    return copy(
        isShowFoodHand = dailyExpensesFoodAndCount,
        error = error.copy(
            isErrorFood = false
        )
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

            Log.i("expenses", "foodDaySuffix: ${it.foodDaySuffix}")
            Log.i("expenses", "feedFoodChipSuffix: ${feedFoodChipSuffix}")
            val convertedFood =
                it.foodDay.convertWeight(it.foodDaySuffix, feedFoodChipSuffix)
            val dailyFood = convertedFood * it.countAnimal
            Log.i(
                "expenses", "convertedFood: $convertedFood"
            )
            Log.i(
                "expenses", "dailyFood: $dailyFood"
            )
            Log.i(
                "expenses",
                "toggleAnimalChipSelection: ${if (newSelected) (it.foodDay / dailyFood) * 100.0 else 0.0}"
            )
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
        feedFoodChip = updatedDailyFood,
        countAnimalChip = updatedCountAnimal,
        error = error.copy(isErrorFood = false)
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
        isShowFoodHand = false,
        isShowAnimals = showAnimal,
        error = error.copy(
            isErrorAnimal = false
        )
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
        animalList2 = updatedList,
        error = error.copy(
            isErrorAnimal = false
        )
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
        if (isAutoWeight) {
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

fun ExpensesEntryState.updateFromDomain(
    domain: DomainExpensesTable,
    suffix: String,
    countSuffix: String
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
        feedFoodChipSuffix = if (!useDaily) domain.feedFoodSuffix ?: suffix else suffix,
        countAnimalInput = if (useDaily) domain.countAnimal?.formatNumber(false) ?: "" else "",
        feedFoodInput = if (useDaily) domain.feedFood?.formatNumber(false) ?: "" else "",
        feedFoodInputSuffix = if (useDaily) domain.feedFoodSuffix ?: suffix else suffix,
        daysFood = domain.foodDesignedDay ?: 0,
        dateEndFood = domain.lastDayFood ?: "",
        isAutoWeight = domain.isAutoWeight,
        isAutoPrice = domain.isAutoPrice,
        weight = domain.weight?.formatNumber(false) ?: "",
        weightSuffix = domain.weightSuffix ?: countSuffix,
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
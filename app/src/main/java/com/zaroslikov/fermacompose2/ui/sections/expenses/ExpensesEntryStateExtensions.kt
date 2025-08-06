package com.zaroslikov.fermacompose2.ui.sections.expenses

import android.util.Log
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun ExpensesEntryState.validate(): ExpensesEntryState {

    val (dailyExpensesFood, countAnimal) = if (domainExpensesTable.dailyExpensesFoodAndCount) {
        feedFoodInput to countAnimalInput
    } else feedFoodChip to countAnimalChip

    val newError = ExpensesEntryState.ValidationError(
        isErrorTitle = domainExpensesTable.title.isBlank(),
        isErrorSlash = domainExpensesTable.title.contains("/"),
        isErrorCount = domainExpensesTable.count.isBlank(),
        isErrorPrice = domainExpensesTable.priceAll.isBlank(),
        isErrorDailyExpensesFood = dailyExpensesFood.isBlank(),
        isErrorCountAnimal = countAnimal.isBlank()
    )
    return this.copy(error = newError)
}

fun ExpensesEntryState.validateAll(domain: DomainExpensesTable): ExpensesEntryState {
    val error = ExpensesEntryState.ValidationError(
        isErrorTitle = domain.title.isBlank(),
        isErrorSlash = domain.title.contains("/"),
        isErrorCount = domain.count.isBlank(),
        isErrorPrice = domain.priceAll.isBlank(),
        isErrorDailyExpensesFood = domain.dailyExpensesFood.isBlank(),
        isErrorCountAnimal = domain.countAnimal.isBlank()
    )
    return ExpensesEntryState(domainExpensesTable = domain, error = error)
}

fun ExpensesEntryState.updateFromDomain(domain: DomainExpensesTable): ExpensesEntryState {
    val useDaily = domain.dailyExpensesFoodAndCount
    return copy(
        countAnimalInput = if (useDaily) domain.countAnimal else "",
        feedFoodInput = if (useDaily) domain.dailyExpensesFood else "",
        feedFoodInputSuffix = if (useDaily) domain.dailyExpensesFoodSuffix else "",
        countAnimalChip = if (useDaily) domain.countAnimal else "",
        feedFoodChip = if (useDaily) domain.dailyExpensesFood else "",
        feedFoodChipSuffix = if (useDaily) domain.dailyExpensesFoodSuffix else "",
        domainExpensesTable = domain
    )
}

fun ExpensesEntryState.updateTitle(title: String): ExpensesEntryState {
    return copy(
        domainExpensesTable = domainExpensesTable.copy(title = title),
        error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/")
        )
    )
}

fun ExpensesEntryState.updateCount(count: String): ExpensesEntryState {
    return copy(
        domainExpensesTable = domainExpensesTable.copy(count = count),
        error = error.copy(
            isErrorCount = count.isBlank()
        )
    )
}

fun ExpensesEntryState.updateSuffix(suffix: String): ExpensesEntryState {
    return copy(
        domainExpensesTable = domainExpensesTable.copy(suffix = suffix)
    )
}

fun ExpensesEntryState.updatePrice(price: String): ExpensesEntryState {
    return copy(
        domainExpensesTable = domainExpensesTable.copy(priceAll = price),
        error = error.copy(
            isErrorPrice = price.isBlank()
        )
    )
}

fun ExpensesEntryState.updateAutoCalculate(isAutoCalculate: Boolean): ExpensesEntryState {
    return copy(
        isAutoCalculate = isAutoCalculate,
    )
}

fun ExpensesEntryState.updateCategory(category: String): ExpensesEntryState {
    return copy(
        domainExpensesTable = domainExpensesTable.copy(category = category)
    )
}

fun ExpensesEntryState.updateDate(date: String): ExpensesEntryState {
    val dateList = date.split(".")
    return copy(
        domainExpensesTable = domainExpensesTable.copy(
            day = dateList[0].toInt(),
            mount = dateList[1].toInt(),
            year = dateList[2].toInt()
        )
    )
}

fun ExpensesEntryState.updateNote(note: String): ExpensesEntryState {
    return copy(
        domainExpensesTable = domainExpensesTable.copy(
            note = note
        )
    )
}

fun ExpensesEntryState.updateShowFood(showFood: Boolean): ExpensesEntryState {
    return copy(
        animalList2 = animalList2.map { it.copy(ps = false) },
        domainExpensesTable = domainExpensesTable.copy(
            showFood = showFood,
            showWarehouse = showFood,
            showAnimals = false
        ),
    )
}

fun ExpensesEntryState.updateDailyExpensesFoodAndCount(dailyExpensesFoodAndCount: Boolean): ExpensesEntryState {
    return copy(
//        animalList2 = animalList2.map { it.copy(ps = false) },
        domainExpensesTable = domainExpensesTable.copy(
            dailyExpensesFoodAndCount = dailyExpensesFoodAndCount,
//            dailyExpensesFood = "",
//            countAnimal = ""
        ),
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
        domainExpensesTable = domainExpensesTable.copy(
            showWarehouse = showWarehouse
        ),
    )
}

fun ExpensesEntryState.updateShowAnimal(showAnimal: Boolean): ExpensesEntryState {
    return copy(
        animalList2 = animalList2.map { it.copy(ps = false) },
        domainExpensesTable = domainExpensesTable.copy(
            showAnimals = showAnimal
        ),
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
    val date = formatDateToString(
        domainExpensesTable.day,
        domainExpensesTable.mount,
        domainExpensesTable.year
    )
    val dailyExpensesFoodAndCount =
        domainExpensesTable.dailyExpensesFoodAndCount

    val countTable = domainExpensesTable.count.toConvertZeroDouble()
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
            domainExpensesTable.suffix,
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


fun ExpensesEntryState.updateForSave(itemIdPT: Long): ExpensesEntryState {
    val title = if (domainExpensesTable.dailyExpensesFoodAndCount) {
        Triple(countAnimalInput, feedFoodInput, feedFoodInputSuffix)
    } else Triple(countAnimalChip, feedFoodChip, feedFoodChipSuffix)

    val price = if (isAutoCalculate) calculatePriceAll(
        domainExpensesTable.priceAll,
        domainExpensesTable.count
    ) else domainExpensesTable.priceAll

    val animalList = if (domainExpensesTable.dailyExpensesFoodAndCount){
        emptyList()
    } else{
        animalList2
    }
    Log.i("updateForSale", "updateForSave: $animalList")
    return copy(
        animalList2 = animalList,
        domainExpensesTable = domainExpensesTable.copy(
            countAnimal = title.first,
            dailyExpensesFood = title.second,
            dailyExpensesFoodSuffix = title.third,
            priceAll = price,
            idPT = itemIdPT
        )
    )
}
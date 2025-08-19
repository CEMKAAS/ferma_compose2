package com.zaroslikov.fermacompose2.ui.animal.entry

import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalCount
import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber


data class AnimalEntryState(
    val isEntry: Boolean = false,
    val title: String = "",
    val type: String = "",
    val sex: Boolean = true,
    val isAnimalGroup: Boolean = false, // true group

    val count: String = "",
    val countSuffix: String = "",

    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",

    val isDateFactory: Boolean = true,
    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),

    val foodDay: String = "",
    val foodDaySuffix: String = "",

    val note: String = "",

    val category: String = "",
    val typeList: List<String> = emptyList(),
    val archive: Boolean = false,
    val error: Error = Error()

) {
    val hasAnyError: Boolean
        get() = error.hasAnyError(isAnimalGroup)

    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorType: Boolean = false,
        val isErrorCount: Boolean = false,
    ) {
        fun hasAnyError(isAnimalGroup: Boolean): Boolean {
            return when {
                isAnimalGroup -> isErrorTitle || isErrorCount || isErrorType
                else -> isErrorTitle || isErrorType
            }
        }
    }
}


fun AnimalEntryState.validate(): AnimalEntryState {
    val error = error.copy(
        isErrorTitle = title.isBlank(),
        isErrorType = type.isBlank(),
        isErrorCount = count.isBlank(),
    )
    return copy(error = error)
}


fun AnimalEntryState.updateIsAnimalGroup(isAnimalGroup: Boolean): AnimalEntryState {
    return copy(
        isAnimalGroup = isAnimalGroup
    )
}

fun AnimalEntryState.updateTitle(title: String): AnimalEntryState {
    return copy(
        title = title,
        error = error.copy(
            isErrorTitle = title.isBlank(),
        )
    )
}

fun AnimalEntryState.updateType(type: String): AnimalEntryState {
    return copy(
        type = type,
        error = error.copy(
            isErrorType = type.isBlank(),
        )
    )
}


fun AnimalEntryState.updateCount(count: String): AnimalEntryState {
    return this.copy(
        count = count,
        error = error.copy(
            isErrorCount = count.isBlank()
        )
    ).updatePriceAll()
}

fun AnimalEntryState.updateSuffix(countSuffix: String): AnimalEntryState {
    return this.copy(
        countSuffix = countSuffix,
    )
}

fun AnimalEntryState.updatePrice(price: String): AnimalEntryState {
    return this.copy(
        price = price
    ).updatePriceAll()
}

fun AnimalEntryState.updateIsAutoPrice(isAutoPrice: Boolean): AnimalEntryState {
    return this.copy(
        isAutoPrice = isAutoPrice
    ).updatePriceAll()
}

fun AnimalEntryState.updatePriceAll(): AnimalEntryState {
    return copy(
        priceAll = if (isAutoPrice) (price.toConvertZeroDouble() * count.toConvertZeroDouble()).formatNumber() else "0"
    )
}

fun AnimalEntryState.updateIsDateFactory(isDateFactory: Boolean): AnimalEntryState {
    return copy(
        isDateFactory = isDateFactory
    )
}

fun AnimalEntryState.updateDateFactory(dateFactory: String): AnimalEntryState {
    return copy(
        dateFactory = dateFactory
    )
}

fun AnimalEntryState.updateFoodDay(foodDay: String): AnimalEntryState {
    return copy(
        foodDay = foodDay
    )
}

fun AnimalEntryState.updateFoodDaySuffix(foodDaySuffix: String): AnimalEntryState {
    return copy(
        foodDaySuffix = foodDaySuffix
    )
}


fun AnimalEntryState.updateDate(date: String): AnimalEntryState {
    return this.copy(
        dateBorn = date,
        dateFactory = if (isDateFactory) date else ""
    )
}

fun AnimalEntryState.updateNote(note: String): AnimalEntryState {
    return this.copy(note = note)
}

fun AnimalEntryState.updateSex(sex: Boolean): AnimalEntryState {
    return this.copy(sex = sex)
}

fun AnimalEntryState.updateList(typeList: List<String>): AnimalEntryState {
    return copy(typeList = typeList)
}

fun AnimalEntryState.updateFromDomain(domain: DomainAnimalTable): AnimalEntryState {
    return copy(
        title = domain.name,
        type = domain.type,
        isDateFactory = domain.dateFactory == null,
        dateBorn = domain.date,
        dateFactory = domain.dateFactory ?: domain.date,
        isAnimalGroup = domain.group,
        sex = domain.sex,
        note = domain.note,
        foodDay = domain.foodDay.formatNumber(),
        foodDaySuffix = domain.foodDaySuffix
    )
}

fun AnimalEntryState.saveAnimal(
    id: Long = 0,
    itemIdPT: Long
): DomainAnimalTable {
    return DomainAnimalTable(
        id = id,
        name = title,
        type = type,
        date = dateBorn,
        dateFactory = if (isDateFactory) null else dateFactory,
        group = isAnimalGroup,
        sex = sex,
        note = note,
        image = null,
        archive = archive,
        foodDay = if (foodDay.isBlank()) 0.0 else foodDay.toConvertDbDouble(),
        foodDaySuffix = foodDaySuffix,
        idPT = itemIdPT
    )
}


fun AnimalEntryState.updateForSave(
    idAnimal: Long,
    itemIdPT: Long
): Pair<DomainAnimalCount, DomainExpensesTable?> {
    val dateFactory2 = if (isDateFactory) dateBorn else dateFactory
    val dateList = dateFactory2.split(".")
    val countAnimal = if (count == "") "1" else count

    return DomainAnimalCount(
        id = 0,
        count = countAnimal,
        suffix = countSuffix,
        date = if (!isDateFactory) dateFactory else dateBorn,
        idAnimal = idAnimal,
        note = "",
        version = 1
    ) to
            if (price.isNotBlank())
                DomainExpensesTable(
                    id = 0,
                    title = title,
                    count = count.toConvertZeroDouble(),
                    day = dateList[0].toInt(),
                    month = dateList[0].toInt(),
                    year = dateList[0].toInt(),
                    price = price.toConvertDbDouble(),
                    priceAll = if (isAutoPrice && isAnimalGroup) priceAll.toConvertDbDouble() else null,
                    countSuffix = countSuffix,
                    category = category,
                    note = "",
                    isShowFood = false,
                    isShowFoodHand = false,
                    isShowWarehouse = false,
                    isShowAnimals = false,
                    feedFood = null,
                    feedFoodSuffix = null,
                    countAnimal = countAnimal.toInt(),
                    foodDesignedDay = null,
                    lastDayFood = null,
                    weight = null,
                    weightSuffix = null,
                    idPT = itemIdPT,
                    animalId = idAnimal,
                    animalVaccinationId = null,
                    animalCountId = null,
                ) else null

}
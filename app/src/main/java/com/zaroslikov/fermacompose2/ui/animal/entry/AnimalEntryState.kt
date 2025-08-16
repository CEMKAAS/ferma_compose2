package com.zaroslikov.fermacompose2.ui.animal.entry

import com.zaroslikov.fermacompose2.Domain.models.DomainWriteOffTable
import com.zaroslikov.fermacompose2.supportFun.PairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.SaleTitleData
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber


data class AnimalEntryState(
    val isEntry: Boolean = false,
    val title: String = "",
    val type: String = "",
    val sex: Boolean = false,
    val isAnimalGroup: Boolean = false, // true group

    val count: String = "",
    val countSuffix: String = "",

    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",

    val isDateFactory: Boolean = true,
    val dateBorn: String = "",
    val dateFactory: String = "",

    val foodDay: String = "",
    val foodDaySuffix: String = "",

    val note: String = "",

    val typeList: List<String> = emptyList(),
    val error: Error = Error()
) {
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorType: Boolean = false,
        val isErrorCount: Boolean = false,
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorType
    }
}


fun AnimalEntryState.validate(): AnimalEntryState {
    val error = error.copy(
        isErrorTitle = title.isBlank(),
        isErrorType = type.isBlank()
    )
    return copy(error = error)
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


fun AnimalEntryState.updateCountWarehouse(domainPairDataDoubleSting: List<PairDataDoubleSting>): AnimalEntryState {
    return this.copy(
        warehouseList = domainPairDataDoubleSting
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

fun AnimalEntryState.updateList(titleList: List<SaleTitleData>): AnimalEntryState {
    return copy(titleList = titleList)
}

fun AnimalEntryState.updateFromDomain(domain: DomainWriteOffTable): AnimalEntryState {
    return copy(
        title = domain.title,
        count = domain.count.formatNumber(false),
        countSuffix = domain.countSuffix,
        isAutoPrice = domain.priceAll != null,
        price = domain.price?.formatNumber(false) ?: "",
        priceAll = domain.priceAll?.formatNumber() ?: "",
        date = formatDateToString(
            domain.day,
            domain.month,
            domain.year
        ),
        status = domain.status,
        note = domain.note,
        animalCountId = domain.animalCountId,
    )
}

fun AnimalEntryState.updateForSave(
    id: Long = 0,
    itemIdPT: Long
): DomainWriteOffTable {
    val dateList = date.split(".")
    return DomainWriteOffTable(
        id = id,
        title = title.trim(),
        count = count.toConvertDbDouble(),
        countSuffix = countSuffix,
        price = if (price.isBlank()) null else price.toConvertDbDouble(),
        priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
        day = dateList[0].toInt(),
        month = dateList[1].toInt(),
        year = dateList[2].toInt(),
        note = note.trim(),
        status = status,
        idPT = itemIdPT,
        animalCountId = animalCountId,
    )
}
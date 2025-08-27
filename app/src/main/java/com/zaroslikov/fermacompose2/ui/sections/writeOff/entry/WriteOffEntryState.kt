package com.zaroslikov.fermacompose2.ui.sections.writeOff.entry

import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.data.room.dto.PairDataDoubleSting
import com.zaroslikov.data.room.dto.SaleTitleData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.text.trim

data class WriteOffEntryState(
    val title: String = "",
    val count: String = "",
    val countSuffix: String = "",
    val date: String = dateToday(),
    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",
    val status: Boolean = false,
    val note: String = "",
    val animalCountId: Long? = null,

    val isEntry: Boolean = false,
    val isIndicatorsValue: Boolean = false,
    val warehouseList: List<PairDataDoubleSting> = emptyList(),

    val titleList: List<SaleTitleData> = emptyList(),
    val error: Error = Error()
) {
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount
    }
}

fun WriteOffEntryState.validate(): WriteOffEntryState {
    val error = error.copy(
        isErrorTitle = title.isBlank(),
        isErrorSlash = title.contains("/"),
        isErrorCount = count.isBlank()
    )
    return copy(error = error)
}


fun WriteOffEntryState.updateTitleAndSuffix(title: String, suffix: String): WriteOffEntryState {
    return copy(
        title = title,
        countSuffix = suffix,
        error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/")
        )
    )
}

fun WriteOffEntryState.updateCount(count: String): WriteOffEntryState {
    return this.copy(
        count = count,
        error = error.copy(
            isErrorCount = count.isBlank()
        )
    ).updatePriceAll()
}

fun WriteOffEntryState.updateSuffix(countSuffix: String): WriteOffEntryState {
    return this.copy(
        countSuffix = countSuffix,
    )
}

fun WriteOffEntryState.updatePrice(price: String): WriteOffEntryState {
    return this.copy(
        price = price
    ).updatePriceAll()
}

fun WriteOffEntryState.updateIsAutoPrice(isAutoPrice: Boolean): WriteOffEntryState {
    return this.copy(
        isAutoPrice = isAutoPrice
    ).updatePriceAll()
}

fun WriteOffEntryState.updatePriceAll(): WriteOffEntryState {
    return copy(
        priceAll = if (isAutoPrice) (price.toConvertZeroDouble() * count.toConvertZeroDouble()).formatNumber() else "0"
    )
}

fun WriteOffEntryState.updateCountWarehouse(domainPairDataDoubleSting: List<PairDataDoubleSting>): WriteOffEntryState {
    return this.copy(
        warehouseList = domainPairDataDoubleSting
    )
}

fun WriteOffEntryState.updateDate(date: String): WriteOffEntryState {
    return this.copy(
        date = date,
    )
}

fun WriteOffEntryState.updateNote(note: String): WriteOffEntryState {
    return this.copy(note = note)
}

fun WriteOffEntryState.updateStatus(status: Boolean): WriteOffEntryState {
    return this.copy(status = status)
}

fun WriteOffEntryState.updateList(titleList: List<SaleTitleData>): WriteOffEntryState {
    return copy(titleList = titleList)
}

fun WriteOffEntryState.updateFromDomain(domain: DomainWriteOffTable): WriteOffEntryState {
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

fun WriteOffEntryState.updateForSave(
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

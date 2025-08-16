package com.zaroslikov.fermacompose2.ui.sections.sale.entry

import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.supportFun.PairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.SaleTitleData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.text.trim

data class SaleEntryState(
    val title: String = "",
    val count: String = "",
    val countSuffix: String = "",
    val date: String = dateToday(),
    val category: String = "",
    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",
    val buyer: String = "",

    val selectedAnimalIndex: Long = 0,
    val animalCountId: Long? = null,
    val animalId: Long? = null,
    val animal: String = "",
    val note: String = "",
    val isEntry: Boolean = false,
    val isIndicatorsValue: Boolean = false,
    val warehouseList: List<PairDataDoubleSting> = emptyList(),
    val titleList: List<SaleTitleData> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val buyerList: List<String> = emptyList(),
    val error: Error = Error()
) {
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false
    ) {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
    }
}

fun SaleEntryState.validate(): SaleEntryState {
    val error = error.copy(
        isErrorTitle = title.isBlank(),
        isErrorSlash = title.contains("/"),
        isErrorCount = count.isBlank(),
        isErrorPrice = priceAll.isBlank()
    )
    return this.copy(error = error)
}

fun SaleEntryState.updateTitle(title: String): SaleEntryState {
    return this.copy(
        title = title,
        error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/")
        )
    )
}

fun SaleEntryState.updateTitleAndSuffix(title: String, suffix: String): SaleEntryState {
    return copy(
        title = title,
        countSuffix = suffix,
        error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/")
        )
    )
}

fun SaleEntryState.updateCount(count: String): SaleEntryState {
    return this.copy(
        count = count,
        error = error.copy(
            isErrorCount = count.isBlank()
        )
    ).updatePriceAll()
}

fun SaleEntryState.updatePrice(price: String): SaleEntryState {
    return this.copy(
        price = price,
        error = error.copy(
            isErrorPrice = price.isBlank()
        )
    ).updatePriceAll()
}

fun SaleEntryState.updateIsAutoPrice(isAutoPrice: Boolean): SaleEntryState {
    return this.copy(
        isAutoPrice = isAutoPrice
    ).updatePriceAll()
}

fun SaleEntryState.updatePriceAll(): SaleEntryState {
    return copy(
        priceAll = if (isAutoPrice) (price.toConvertZeroDouble() * count.toConvertZeroDouble()).formatNumber() else "0"
    )
}

fun SaleEntryState.updateSuffix(countSuffix: String): SaleEntryState {
    return this.copy(
        countSuffix = countSuffix,
    )
}

fun SaleEntryState.updateCountWarehouse(domainPairDataDoubleSting: List<PairDataDoubleSting>): SaleEntryState {
    return this.copy(
        warehouseList = domainPairDataDoubleSting
    )
}

fun SaleEntryState.updateCategory(category: String): SaleEntryState {
    return this.copy(
        category = category,
    )
}

fun SaleEntryState.updateDate(date: String): SaleEntryState {
    return this.copy(
        date = date,
    )
}

fun SaleEntryState.updateBuyer(buyer: String): SaleEntryState {
    return this.copy(
        buyer = buyer
    )
}

fun SaleEntryState.updateBuyerClear(): SaleEntryState {
    return this.copy(
        buyer = ""
    )
}

fun SaleEntryState.updateNote(note: String): SaleEntryState {
    return this.copy(note = note)
}

fun SaleEntryState.updateList(
    titleList: List<SaleTitleData>,
    categoryList: List<String>,
    buyerList: List<String>
): SaleEntryState {
    return copy(
        titleList = titleList,
        categoryList = categoryList,
        buyerList = buyerList
    )
}

fun SaleEntryState.updateFromDomain(domain: DomainSaleTable): SaleEntryState {
    return copy(
        title = domain.title,
        count = domain.count.formatNumber(false),
        countSuffix = domain.countSuffix,
        isAutoPrice = domain.priceAll != null,
        price = domain.price.formatNumber(false),
        priceAll = domain.priceAll?.formatNumber() ?: "",
        date = formatDateToString(
            domain.day,
            domain.month,
            domain.year
        ),
        category = domain.category,
        buyer = domain.buyer ?: buyer,
        note = domain.note,
        animalId = domain.animalId,
        animalCountId = domain.animalCountId,
    )
}

fun SaleEntryState.updateForSave(
    id: Long = 0,
    itemIdPT: Long
): DomainSaleTable {
    val dateList = date.split(".")
    return DomainSaleTable(
        id = id,
        title = title.trim(),
        count = count.toConvertDbDouble(),
        countSuffix = countSuffix,
        price = price.toConvertDbDouble(),
        priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
        day = dateList[0].toInt(),
        month = dateList[1].toInt(),
        year = dateList[2].toInt(),
        category = category.trim(),
        note = note.trim(),
        buyer = if (buyer.isBlank()) null else buyer.trim(),
        idPT = itemIdPT,
        animalId = animalId,
        animalCountId = animalCountId
    )
}


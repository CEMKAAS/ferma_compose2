package com.zaroslikov.fermacompose2.ui.project.sections.sale.entry

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber
import kotlin.text.trim

data class SaleEntryState(
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
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
    val isIndicatorsValue: Boolean = false,
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val pickList: PickList = PickList(),
    override val isEntry: Boolean = false,
    override val error: Error = Error(),
    override val navigate: UiEvent? = null, override val isLoading: Boolean = false
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorPrice: Boolean = false
    ) : BaseError {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
    }

    data class PickList(
        val titleList: List<DomainTitleSuffixCategory> = emptyList(),
        val categoryList: List<String> = emptyList(),
        val buyerList: List<String> = emptyList(),
    )
}

fun SaleEntryState.updateFromDomain(domain: DomainSaleTable): SaleEntryState {
    val isIndicatorsValue =
        setOf(domain.animalId, domain.animalCountId)
            .any { it != null }

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
        isIndicatorsValue = isIndicatorsValue
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


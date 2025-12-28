package com.zaroslikov.fermacompose2.ui.sections.writeOff.entry

import com.zaroslikov.domain.models.table.DomainWriteOffTable
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

data class WriteOffEntryState(
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val date: String = dateToday(),
    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",
    val status: Boolean = false,
    val note: String = "",
    val animalCountId: Long? = null,
    override val isEntry: Boolean = false,
    val isIndicatorsValue: Boolean = false,
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val titleList: List<DomainTitleSuffixCategory> = emptyList(),
    override val error: Error = Error(),
    override val navigate: UiEvent? = null, override val isLoading: Boolean = false,
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
    ) : BaseError {

        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount
    }
}


fun WriteOffEntryState.updateFromDomain(domain: DomainWriteOffTable): WriteOffEntryState {
    val isIndicatorsValue =
        setOf(domain.animalCountId)
            .any { it != null }
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
        isIndicatorsValue = isIndicatorsValue
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

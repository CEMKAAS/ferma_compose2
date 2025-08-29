package com.zaroslikov.fermacompose2.ui.sections.writeOff.entry

import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.data.room.dto.PairDataDoubleSting
import com.zaroslikov.data.room.dto.SaleTitleData
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.write_off.TitleWriteOffDomain
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
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
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val titleList: List<TitleWriteOffDomain> = emptyList(),
    override val error: Error = Error()
) : EntryState() {
    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorCount: Boolean = false,
    ) : BaseError {
        override val hasAnyError: Boolean
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

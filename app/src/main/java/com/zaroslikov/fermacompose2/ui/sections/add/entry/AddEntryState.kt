package com.zaroslikov.fermacompose2.ui.sections.add.entry

import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.PairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.String

data class AddEntryState(
    val title: String = "",
    val count: String = "",
    val date: String = dateToday(),
    val countSuffix: String = "",
    val category: String = "",
    val selectedAnimalIndex: Long = 0,
    val animalId: Long? = null,
    val animal: String = "",
    val note: String = "",
    val isEntry: Boolean = false,
    val warehouseList: List<PairDataDoubleSting> = emptyList(),
    val titleList: List<PairData> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val animalList: List<TripleData> = emptyList(),
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

fun AddEntryState.toUiMap(domain: DomainAddTable): AddEntryState {
    return copy(
        title = domain.title,
        count = domain.count.formatNumber(false),
        date = formatDateToString(
            domain.day,
            domain.month,
            domain.year
        ),
        countSuffix = domain.countSuffix,
        category = domain.category,
        selectedAnimalIndex = domain.animalId ?: 0,
        animalId = domain.animalId,
        note = domain.note
    )
}

fun AddEntryState.toDomainMap(
    id: Long = 0,
    itemIdPT: Long
): DomainAddTable {
    val dateList = date.split(".")
    return DomainAddTable(
        id = id,
        title = title.trim(),
        count = count.toConvertDbDouble(),
        day = dateList[0].toInt(),
        month = dateList[1].toInt(),
        year = dateList[2].toInt(),
        countSuffix = countSuffix,
        category = category.trim(),
        animalId = animalId,
        note = note.trim(),
        price = 0.0,
        idPT = itemIdPT
    )
}



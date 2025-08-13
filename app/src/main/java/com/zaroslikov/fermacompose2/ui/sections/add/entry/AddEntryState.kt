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

fun AddEntryState.validate(): AddEntryState {
    val error = AddEntryState.Error(
        isErrorTitle = title.isBlank(),
        isErrorSlash = title.contains("/"),
        isErrorCount = count.isBlank(),
    )
    return this.copy(error = error)
}

fun AddEntryState.updateTitle(title: String): AddEntryState {
    return this.copy(
        title = title,
        error = error.copy(
            isErrorTitle = title.isBlank(),
            isErrorSlash = title.contains("/")
        )
    )
}

fun AddEntryState.updateTitleAndSuffix(pair: Pair<String, String>): AddEntryState {
    return copy(
        title = pair.first,
        countSuffix = pair.second,
        error = error.copy(
            isErrorTitle = pair.first.isBlank(),
            isErrorSlash = pair.first.contains("/")
        )
    )
}

fun AddEntryState.updateCount(count: String): AddEntryState {
    return this.copy(
        count = count,
        error = error.copy(
            isErrorCount = count.isBlank()
        )
    )
}

fun AddEntryState.updateSuffix(countSuffix: String): AddEntryState {
    return this.copy(
        countSuffix = countSuffix,
    )
}

fun AddEntryState.updateCountWarehouse(domainPairDataDoubleSting: List<PairDataDoubleSting>): AddEntryState {
    return this.copy(
        warehouseList = domainPairDataDoubleSting
    )
}

fun AddEntryState.updateCategory(category: String): AddEntryState {
    return this.copy(
        category = category,
    )
}

fun AddEntryState.updateDate(date: String): AddEntryState {
    return this.copy(
        date = date,
    )
}

fun AddEntryState.updateAnimal(animal: Pair<Long, String>): AddEntryState {
    return this.copy(
        selectedAnimalIndex = animal.first,
        animalId = animal.first,
        animal = animal.second
    )
}

fun AddEntryState.updateAnimalNameById(name: String): AddEntryState {
    return this.copy(
        animal = name
    )
}

fun AddEntryState.updateAnimalClear(animal: String): AddEntryState {
    return this.copy(
        animalId = 0,
        animal = animal
    )
}

fun AddEntryState.updateNote(note: String): AddEntryState {
    return this.copy(note = note)
}

fun AddEntryState.updateList(
    titleList: List<PairData>,
    categoryList: List<String>,
    animalList: List<TripleData>
): AddEntryState {
    return copy(
        titleList = titleList,
        categoryList = categoryList,
        animalList = animalList
    )
}

fun AddEntryState.updateFromDomain(domain: DomainAddTable): AddEntryState {
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

fun AddEntryState.updateForSave(
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
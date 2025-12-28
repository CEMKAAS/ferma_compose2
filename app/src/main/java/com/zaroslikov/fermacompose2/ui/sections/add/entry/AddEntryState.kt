package com.zaroslikov.fermacompose2.ui.sections.add.entry

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber
import kotlin.String

data class AddEntryState(
    val title: String = "",
    val count: String = "",
    val date: String = dateToday(),
    val countSuffix: Suffix = Suffix.PIECES,
    val category: String = "",
    val selectedAnimalIndex: Long = 0,
    val animalId: Long? = null,
    val animal: String = "",
    val note: String = "",
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
    ) : BaseError {
        val hasAnyError: Boolean
            get() = isErrorTitle || isErrorSlash || isErrorCount
    }

    data class PickList(
        val titleList: List<TitleAndSuffixDomain> = emptyList(),
        val categoryList: List<String> = emptyList(),
        val animalList: List<AnimalForAddDomain> = emptyList(),
    )
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



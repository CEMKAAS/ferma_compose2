package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalListState(
    val textSearch: String = "",
    val isArchive: Boolean = true,
    val idPT: Long = 0,
    val openBottomSheetEntry: Boolean = false,
    val list: List<DomainAnimalWithCount> = emptyList(),
    val archiveList: List<DomainAnimalWithCount> = emptyList(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = true,
    override val currentProduct: AnimalEntryState2 = AnimalEntryState2()
) : EntryNewState()

data class AnimalEntryState2(
    val title: String = "",
    val type: String = "",
    val sex: Boolean = true,
    val isAnimalGroup: Boolean = false, // true group
    val count: String = "",

    val countSuffix: Suffix = Suffix.PIECES,
    val isAutoPrice: Boolean = false,

    val price: String = "",
    val priceAll: String = "",
    val isDateFactory: Boolean = true,

    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),
    val foodDay: String = "",

    val foodDaySuffix: Suffix = Suffix.GRAM_DAY,
    val note: String = "",

    val category: String = "",

    val typeList: List<String> = emptyList(),
    val archive: Boolean = false,
    val error: ErrorEntryAnimal = ErrorEntryAnimal(),
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError(isAnimalGroup, isEntry = true)

    fun enabledButton(): Boolean {
        val isEnabled = when {
            isAnimalGroup -> title.isNotBlank() && count.isNotBlank() && type.isNotBlank() && !hasAnyError
            else -> title.isNotBlank() && type.isNotBlank() && !hasAnyError
        }
        return !isEnabled
    }
}


data class ErrorEntryAnimal(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorType: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError {
    fun hasAnyError(isAnimalGroup: Boolean, isEntry: Boolean): Boolean {
        return when {
            isAnimalGroup && isEntry -> isErrorTitle || isErrorCount || isErrorType
            else -> isErrorTitle || isErrorType
        }
    }
}



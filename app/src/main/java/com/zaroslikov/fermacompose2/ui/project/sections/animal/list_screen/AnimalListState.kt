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
    val searchList: List<DomainAnimalWithCount> = emptyList(),
    val searchArchiveList: List<DomainAnimalWithCount> = emptyList(),
    val isSaveStateForEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = true,
    override val currentProduct: AnimalEntryState2 = AnimalEntryState2()
) : EntryNewState()

data class AnimalEntryState2(
    val itemId: Long = 0,
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

    val typeList: List<String> = emptyList(),
    val archive: Boolean = false,
    val itemIdPT: Long = 0,
    val error: ErrorEntryAnimal = ErrorEntryAnimal(),
    override val hasAnyError: Boolean = false
) : BaseProduct()


data class ErrorEntryAnimal(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorType: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError



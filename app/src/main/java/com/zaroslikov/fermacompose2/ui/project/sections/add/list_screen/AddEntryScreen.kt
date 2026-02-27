package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import android.util.Log
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AddListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val openBottomSheetGroup: Boolean = false,
    val openBottomSheetEntry: Boolean = false,
    val currentBriefly: BrieflyAddDomain = BrieflyAddDomain(),
    val list: List<DomainAddTable> = emptyList(),
    val briefly: List<BrieflyAddDomain> = emptyList(),
    val listBriefly: List<DomainAddTable> = emptyList(),
    override val isEntry: Boolean = false,
    override val currentProduct: AddEntryState2 = AddEntryState2(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : EntryNewState()


data class AddEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val count: String = "",
    val date: String = dateToday(),
    val countSuffix: Suffix = Suffix.PIECES,
    val category: String = "",
    val selectedAnimalIndex: Long = 0,
    val animalId: Long? = null,
    val animal: String = "",
    val note: String = "",
    val itemIdPT: Long = 0,
    val isEntry: Boolean = true,
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val pickList: PickList = PickList(),
    val error: ErrorAdd = ErrorAdd(),
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled = title.isNotBlank() && count.isNotBlank() && !hasAnyError
        return isEnabled
    }
}

data class PickList(
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val animalList: List<AnimalForAddDomain> = emptyList(),
)

data class ErrorAdd(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorTitle || isErrorSlash || isErrorCount
}

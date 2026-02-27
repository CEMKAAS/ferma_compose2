package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WriteOffListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val openBottomSheetGroup: Boolean = false,
    val openBottomSheetEntry: Boolean = false,
    val currentBriefly: BrieflyWriteOffDomain = BrieflyWriteOffDomain(),
    val list: List<DomainWriteOffTable> = emptyList(),
    val briefly: List<BrieflyWriteOffDomain> = emptyList(),
    val listBriefly: List<DomainWriteOffTable> = emptyList(),
    override val isEntry: Boolean = false,
    override val currentProduct: WriteOffEntryState2 = WriteOffEntryState2(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val writeOffBoolean: Boolean = false,
) : EntryNewState()


data class WriteOffEntryState2(
    val itemId: Long = 0,
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
    val isEntry: Boolean = true,
    val isIndicatorsValue: Boolean = false,
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val titleList: List<DomainTitleSuffixCategory> = emptyList(),
    val itemIdPT: Long = 0,
    val error: ErrorWriteOff = ErrorWriteOff(),
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled =
            title.isNotBlank() && count.isNotBlank() && !hasAnyError
        return isEnabled
    }
}

data class ErrorWriteOff(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorTitle || isErrorSlash || isErrorCount
}
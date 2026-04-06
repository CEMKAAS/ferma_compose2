package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem

data class WriteOffListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val isOpenGroupBottomSheet: Boolean = false,
    val isOpenEntryBottomSheet: Boolean = false,
    val isOpenBottomSheetDetail: Boolean = false,
    val isSaveStateForBottomSheet: Boolean = false,

    val currentDetail: DomainWriteOffTable? = null,
    val currentBriefly: BrieflyItem? = null,

    val list: List<DomainWriteOffTable> = emptyList(),
    val briefly: List<BrieflyItem> = emptyList(),
    val listBriefly: List<DomainWriteOffTable> = emptyList(),
    val searchList: List<DomainWriteOffTable> = emptyList(),
    val searchBrieflyList: List<BrieflyItem> = emptyList(),

    val settings: DomainSettings = DomainSettings(),
    val writeOffBoolean: Boolean = false,
    override val isEntry: Boolean = false,
    override val currentProduct: WriteOffEntryState2 = WriteOffEntryState2(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : EntryNewState()

data class WriteOffEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val writeOffCategory: Category? = null,
    val date: String = dateToday(),
    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",
    val status: Boolean = false,
    val note: String = "",
    val animalCountId: Long? = null,
    val isEntry: Boolean = true,
    val isIndicatorsValue: Boolean = false,
    val pickList: PickWriteOffList = PickWriteOffList(),
    val itemIdPT: Long = 0,
    val error: ErrorWriteOff = ErrorWriteOff(),
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class PickWriteOffList(
    val titleList: List<DomainTitleSuffixCategory> = emptyList(),
    val warehouseList: List<DomainCountSuffix> = emptyList(),
)

data class ErrorWriteOff(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError

data class LoadDataWriteOffList(
    val addList: List<DomainWriteOffTable>,
    val briefly: List<BrieflyItem>,
    val titleList: List<TitleAndSuffixDomain>,
    val settings: DomainSettings
)
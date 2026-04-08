package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem

data class SaleListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val isOpenBottomSheetGroup: Boolean = false,
    val isOpenBottomSheetEntry: Boolean = false,
    val isOpenBottomSheetDetail: Boolean = false,
    val isSaveStateForBottomSheet: Boolean = false,

    val currentDetail: DomainSaleTable? = null,
    val currentBriefly: BrieflyItem? = null,

    val list: List<DomainSaleTable> = emptyList(),
    val briefly: List<BrieflyItem> = emptyList(),
    val listBriefly: List<DomainSaleTable> = emptyList(),
    val searchList: List<DomainSaleTable> = emptyList(),
    val searchBrieflyList: List<BrieflyItem> = emptyList(),

    val settings: DomainSettings = DomainSettings(),
    override val isEntry: Boolean = false,
    override val currentProduct: SaleEntryState2 = SaleEntryState2(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false
) : EntryNewState()

data class SaleEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val saleCategory: Category? = null,
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val date: String = dateToday(),
    val category: String = "",
    val price: String = "",
    val isAutoPrice: Boolean = false,
    val priceAll: String = "",
    val buyer: String = "",
    val selectedAnimalIndex: Long = 0,
    val animalCountId: Long? = null,
    val animalId: Long? = null,
    val animal: String = "",
    val note: String = "",
    val itemIdPT: Long = 0,
    val isIndicatorsValue: Boolean = false,
    val pickList: PickSaleList = PickSaleList(),
    val isEntry: Boolean = true,
    val error: ErrorSale = ErrorSale(),
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class PickSaleList(
    val titleList: List<DomainTitleSuffixCategory> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val buyerList: List<String> = emptyList(),
    val warehouseList: List<DomainCountSuffix> = emptyList(),
)

data class ErrorSale(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorPrice: Boolean = false
) : BaseError
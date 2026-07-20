package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BasePickList
import com.zaroslikov.fermacompose2.base.state.BaseProductState
import com.zaroslikov.fermacompose2.base.state.DetailNomenclatura
import com.zaroslikov.fermacompose2.base.state.MainList
import com.zaroslikov.fermacompose2.base.state.Product
import com.zaroslikov.fermacompose2.base.state.SearchState
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.base.state.UiState
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddBottomSheetState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddTemplateState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddTemplatesState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeWarning

data class SaleListState(
    val idPT: Long = 0,
    val productDetail: DomainSaleTable? = null,

    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isArchive: Boolean = false,
    override val ui: SaleUiState = SaleUiState(),
    override val mainList: SaleMainListState = SaleMainListState(),

    override val detailNomenclatura: SaleDetailNomenclaturaState = SaleDetailNomenclaturaState(),
    override val currentProduct: SaleProductState = SaleProductState(),
    override val searchState: SaleSearchState = SaleSearchState(),

    override val settings: DomainSettings = DomainSettings(),
    override val bottomSheetState: AddBottomSheetState = AddBottomSheetState(),
    override val templatesState: AddTemplatesState = AddTemplatesState(),
    override val qrCodeState: QrCodeData? = null,
    override val qrCodeWarning: QrCodeWarning = QrCodeWarning()
) : SectionState

data class SaleUiState(
    override val colors: List<Color> = listOf(blue_1, blue_2),
    override val iconRes: Int =R.drawable.icon_sale
) : UiState

data class SaleSearchState(
    override val searchQuery: String = "",
    val searchResults: List<DomainSaleTable> = emptyList(),
    val searchBrieflyResults: List<BrieflyItem> = emptyList(),
) : SearchState

data class SaleMainListState(
    val isGroupMode: Boolean = true,
    val items: List<DomainSaleTable> = emptyList(),
    val brieflyItems: List<BrieflyItem> = emptyList()
) : MainList

data class SaleDetailNomenclaturaState(
    val detail: BrieflyItem? = null,
    val productItems: List<DomainSaleTable> = emptyList(),
) : DetailNomenclatura

//Product State
data class SaleProductState(
    override val product: SaleProduct = SaleProduct(),
    override val errors: ErrorSale = ErrorSale(),
    override val pickList: PickSaleList = PickSaleList(),
    override val template: AddTemplateState = AddTemplateState()
) : BaseProductState

data class SaleProduct(
    override val itemId: Long = 0,
    override val title: String = "",
    override val date: String = dateToday(),
    override val count: String = "",
    override val countSuffix: Suffix = Suffix.PIECES,
    override val category: String = "",
    override val note: String = "",
    override val projectId: Long = 0,
    override val isEntry: Boolean = true,
    val productOrigin: ProductOrigin? = null,
    val price: String = "",
    val isAutoPrice: Boolean = false,
    val priceAll: String = "",
    val buyer: String = "",
    val selectedAnimalIndex: Long = 0,
    val animalCountId: Long? = null,
    val animalId: Long? = null,
    val animal: String = "",
    val isIndicatorsValue: Boolean = false,
) : Product

data class PickSaleList(
    val titles: List<DomainTitleSuffixCategory> = emptyList(),
    val categories: List<String> = emptyList(),
    val buyers: List<String> = emptyList(),
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val suffixList: List<Suffix> = suffixAllList
) : BasePickList

data class ErrorSale(
    val isErrorNameTemplate: Boolean = false,
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorPrice: Boolean = false,
    override val hasAnyError: Boolean = false
) : BaseError
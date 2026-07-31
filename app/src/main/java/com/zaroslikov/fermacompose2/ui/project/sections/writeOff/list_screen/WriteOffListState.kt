package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixPiecesList
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.BasePickList
import com.zaroslikov.fermacompose2.base.state.BaseProductState
import com.zaroslikov.fermacompose2.base.state.DetailNomenclatura
import com.zaroslikov.fermacompose2.base.state.MainList
import com.zaroslikov.fermacompose2.base.state.Product
import com.zaroslikov.fermacompose2.base.state.ProductError
import com.zaroslikov.fermacompose2.base.state.SearchState
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.base.state.UiState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.BottomSheetState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplatesState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeWarning
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2

data class WriteOffListState(
    val idPT: Long = 0,
    val productDetail: DomainWriteOffTable? = null,
    val isNotProduction: Boolean = false,

    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isArchive: Boolean = false,
    override val ui: WriteOffUiState = WriteOffUiState(),
    override val mainList: WriteOffMainListState = WriteOffMainListState(),

    override val detailNomenclatura: WriteOffDetailNomenclaturaState = WriteOffDetailNomenclaturaState(),
    override val currentProduct: WriteOffProductState = WriteOffProductState(),
    override val searchState: WriteOffSearchState = WriteOffSearchState(),

    override val settings: DomainSettings = DomainSettings(),
    override val bottomSheetState: BottomSheetState = BottomSheetState(),
    override val templatesState: TemplatesState = TemplatesState(),
    override val qrCodeState: QrCodeData? = null,
    override val qrCodeWarning: QrCodeWarning = QrCodeWarning()
) : SectionState

data class WriteOffUiState(
    override val colors: List<Color> = listOf(violet_1, violet_2),
    override val iconRes: Int = R.drawable.baseline_edit_note_24
) : UiState

data class WriteOffMainListState(
    override val isGroupMode: Boolean = true,
    val items: List<DomainWriteOffTable> = emptyList(),
    val brieflyItems: List<BrieflyItem> = emptyList()
) : MainList

data class WriteOffDetailNomenclaturaState(
    val detail: BrieflyItem? = null,
    val productItems: List<DomainWriteOffTable> = emptyList(),
) : DetailNomenclatura

data class WriteOffSearchState(
    override val searchQuery: String = "",
    val searchResults: List<DomainWriteOffTable> = emptyList(),
    val searchBrieflyResults: List<BrieflyItem> = emptyList(),
) : SearchState


//ProductState
data class WriteOffProductState(
    override val product: WriteOffProduct = WriteOffProduct(),
    override val pickList: WriteOffPickList = WriteOffPickList(),
    override val errors: WriteOffError = WriteOffError(),
    override val template: TemplateState = TemplateState()
) : BaseProductState


data class WriteOffProduct(
    override val itemId: Long = 0,
    override val title: String = "",
    override val count: String = "",
    override val countSuffix: Suffix = Suffix.PIECES,
    override val date: String = dateToday(),
    override val price: String = "",
    override val priceAll: String = "",
    val priceSuffix: Suffix = Suffix.RUBLE,
    override val isAutoPrice: Boolean = false,
    override val category: String = "",
    override val note: String = "",
    override val isEntry: Boolean = true,
    override val projectId: Long = 0,
    val status: Boolean = false,
    val animalCountId: Long? = null,
    val hasIndicators: Boolean = false,
    val productOrigin: ProductOrigin? = null
) : Product

data class WriteOffPickList(
    val titles: List<DomainTitleSuffixCategory> = emptyList(),
    override val categories: List<String> = emptyList(),
    override val warehouseList: List<DomainCountSuffix> = emptyList(),
    val suffixList: List<Suffix> = suffixPiecesList,
) : BasePickList

data class WriteOffError(
    val isErrorNameTemplate: Boolean = false,
    override val isErrorTitle: Boolean = false,
    override val isErrorSlash: Boolean = false,
    override val isErrorCount: Boolean = false,
    override val hasAnyError: Boolean = false
) : ProductError

data class LoadDataWriteOffList(
    val addList: List<DomainWriteOffTable>,
    val briefly: List<BrieflyItem>,
    val titleList: List<TitleAndSuffixDomain>,
    val settings: DomainSettings
)
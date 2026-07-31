package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import android.health.connect.datatypes.Device
import androidx.compose.ui.graphics.Color
import coil3.Bitmap
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto2
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.ActiveField
import com.zaroslikov.fermacompose2.base.state.BaseProductState
import com.zaroslikov.fermacompose2.base.state.BasePickList
import com.zaroslikov.fermacompose2.base.state.DetailNomenclatura
import com.zaroslikov.fermacompose2.base.state.MainList
import com.zaroslikov.fermacompose2.base.state.Product
import com.zaroslikov.fermacompose2.base.state.ProductError
import com.zaroslikov.fermacompose2.base.state.SearchState
import com.zaroslikov.fermacompose2.base.state.SectionState
import com.zaroslikov.fermacompose2.base.state.TemplateState
import com.zaroslikov.fermacompose2.base.state.UiState
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import kotlinx.serialization.Serializable

data class AddListState(
    val idPT: Long = 0,
    val productDetail: DomainAddItemDto2? = null,

    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val isArchive: Boolean = false,
    override val ui: AddUiState = AddUiState(),
    override val mainList: AddMainListState = AddMainListState(),

    override val detailNomenclatura: AddDetailNomenclaturaState = AddDetailNomenclaturaState(),
    override val currentProduct: AddProductState = AddProductState(),
    override val searchState: AddSearchState = AddSearchState(),

    override val settings: DomainSettings = DomainSettings(),
    override val bottomSheetState: BottomSheetState = BottomSheetState(),
    override val templatesState: TemplatesState = TemplatesState(),
    override val qrCodeState: QrCodeData? = null,
    override val qrCodeWarning: QrCodeWarning = QrCodeWarning()
) : SectionState

data class AddUiState(
    override val colors: List<Color> = listOf(price_green, green_shamrock),
    override val iconRes: Int = R.drawable.icon_add_product
) : UiState

data class TemplatesState(
    val templatesList: List<TemplateItem> = emptyList(),
    val templateToDelete: TemplateItem? = null
)


data class BottomSheetState(
    val isOpenGroup: Boolean = false,
    val isOpenEntry: Boolean = false,
    val isOpenDetail: Boolean = false,
    val isOpenProductDelete: Boolean = false,
    val isOpenTemplateDelete: Boolean = false,
    val isSaveStateForBottomSheet: Boolean = false,
    val isOpenTemplates: Boolean = false,
    val isOpenEntryInTemplate: Boolean = false,
    val isOpenWarningQrCode: Boolean = false,
    val isOpenScannerQrCode: Boolean = false,
    val isOpenCreateQrCode: Boolean = false
)

data class AddSearchState(
    override val searchQuery: String = "",
    val searchResults: List<DomainAddItemDto2> = emptyList(),
    val searchBrieflyResults: List<BrieflyItem> = emptyList(),
) : SearchState

data class AddMainListState(
    override val isGroupMode: Boolean = true,
    val items: List<DomainAddItemDto2> = emptyList(),
    val brieflyItems: List<BrieflyItem> = emptyList()
) : MainList

data class AddDetailNomenclaturaState(
    val detail: BrieflyItem? = null,
    val productItems: List<DomainAddItemDto2> = emptyList(),
) : DetailNomenclatura


//ProductState
data class AddProductState(
    override val product: AddProduct = AddProduct(),
    override val pickList: AddPickList = AddPickList(),
    override val errors: AddErrors = AddErrors(),
    override val template: com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateState = TemplateState()
) : BaseProductState

data class AddProduct(
    override val itemId: Long = 0,
    override val title: String = "",
    override val date: String = dateToday(),
    override val count: String = "",
    override val countSuffix: Suffix = Suffix.PIECES,
    override val category: String = "",
    override val note: String = "",
    override val projectId: Long = 0,
    override val isEntry: Boolean = true,
    val selectedAnimalIndex: Long = 0,
    val animalId: Long? = null,
    val animalName: String = "",
    val hasIndicators: Boolean = false,
    val animalCountId: Long? = null,
    override val price: String = "", // Заглушка на будующе
    override val priceAll: String = "",// Заглушка на будующе
    override val isAutoPrice: Boolean = false,// Заглушка на будующе
) : Product


data class TemplateState(
    override val name: String = "",
    override val isTemplate: Boolean = false,
    override val isTemplateEntry: Boolean = false,
    override val pin: Boolean = false,
    override val activeField: TemplateFieldsState = TemplateFieldsState()
) : TemplateState

data class TemplateFieldsState(
    override val isTitle: Boolean = false,
    override val isCount: Boolean = false,
    override val isSuffix: Boolean = false,
    override val isCategory: Boolean = false,
    override val isDate: Boolean = false,
    val isWriteOffStatus: Boolean = false,
    val isPrice: Boolean = false,
    val isPriceAll: Boolean = false,
    val isAnimal: Boolean = false,
    override val isNote: Boolean = false,
    val isBuyer: Boolean = false,
    override val isMultiProjectTemplate: Boolean = false
) : ActiveField

data class AddPickList(
    val titles: List<TitleAndSuffixDomain> = emptyList(),
    override val categories: List<String> = emptyList(),
    val animals: List<AnimalForAddDomain> = emptyList(),
    override val warehouseList: List<DomainCountSuffix> = emptyList(),
) : BasePickList

data class AddErrors(
    val isErrorNameTemplate: Boolean = false,
    override val isErrorTitle: Boolean = false,
    override val isErrorSlash: Boolean = false,
    override val isErrorCount: Boolean = false,
    override val hasAnyError: Boolean = false
) : ProductError


data class TemplateItem(
    val id: Long,
    val name: String,
    val description: String,
    val isMultiProject: Boolean,
    val isPinned: Boolean,
)

data class QrCodeData(
    val template: DomainTemplateTable,
    val qrCodeBitmap: Bitmap,
    val qrCodeWithLogoBitmap: Bitmap,
)

data class QrCodeWarning(
    val warningType: QrCodeWarningType = QrCodeWarningType.LOCAL,
    val templateBackup: DomainTemplateTable? = null
)


@Serializable
data class QrPayload(
    val templateType: TemplateType,
    val isMultiProjectTemplate: Boolean,
    val deviceId: String?,
    val itemId: Long,
    val idPT: Long,
    val backupData: DomainTemplateTable? = null
)
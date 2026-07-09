package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import coil3.Bitmap
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import kotlinx.serialization.Serializable

data class AddListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val openBottomSheetGroup: Boolean = false,
    val openBottomSheetEntry: Boolean = false,
    val isOpenBottomSheetDetail: Boolean = false,
    val isOpenProductDeleteBottomSheet: Boolean = false,
    val isOpenTemplateDeleteBottomSheet: Boolean = false,
    val isSaveStateForBottomSheet: Boolean = false,
    val isOpenTemplateBottomSheet: Boolean = false,
    val isOpenEntryInTemplateBottomSheet: Boolean = false,
    val isOpenWarningQrCodeBottomSheet: Boolean = false,
    val isOpenQrCodeBottomSheet: Boolean = false,
    val bitmap: Triple<DomainTemplateTable, Bitmap, Bitmap>? = null,

    val templateList: List<TemplateItem> = emptyList(),

    val currentDetail: DomainAddItemDto? = null,
    val templateDelete: TemplateItem? = null,
    val currentBriefly: BrieflyItem? = null,

    val list: List<DomainAddItemDto> = emptyList(),
    val briefly: List<BrieflyItem> = emptyList(),
    val listBriefly: List<DomainAddItemDto> = emptyList(),
    val searchList: List<DomainAddItemDto> = emptyList(),
    val searchBrieflyList: List<BrieflyItem> = emptyList(),

    val settings: DomainSettings = DomainSettings(),
    override val isEntry: Boolean = false,
    override val currentProduct: AddEntryState2 = AddEntryState2(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false
) : EntryNewState()


data class AddEntryState2(
    val itemId: Long = 0,
    val nameTemplate: String = "",
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
    val pickList: PickList = PickList(),
    val error: ErrorAdd = ErrorAdd(),
    val isIndicatorsValue: Boolean = false,
    val animalCountId: Long? = null,
    override val hasAnyError: Boolean = false,
    val isTemplate: Boolean = false,
    val isTemplateEntry: Boolean = false,
    val templateEntryState: TemplateEntryState = TemplateEntryState()
) : BaseProduct()


data class TemplateEntryState(
    val isTitle: Boolean = false,
    val isCount: Boolean = false,
    val isSuffix: Boolean = false,
    val isCategory: Boolean = false,
    val isAnimal: Boolean = false,
    val isNote: Boolean = false
)

data class TemplateItem(
    val id: Long,
    val nameTemplate: String,
    val description: String
)

data class PickList(
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val animalList: List<AnimalForAddDomain> = emptyList(),
    val warehouseList: List<DomainCountSuffix> = emptyList(),
)

data class ErrorAdd(
    val isErrorNameTemplate: Boolean = false,
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
) : BaseError


@Serializable
data class QrPayload(
    val templateType: TemplateType,
    val itemId: Long,
    val idPT: Long,
    val backupData: DomainTemplateTable? = null
)
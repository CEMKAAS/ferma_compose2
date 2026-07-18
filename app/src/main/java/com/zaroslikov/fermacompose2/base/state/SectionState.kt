package com.zaroslikov.fermacompose2.base.state



import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem

interface SectionState : BaseState {
    val currentProduct: BaseProductState
    val isArchive: Boolean
    val bottomSheetState: BottomSheetState
    val searchState: SearchState
    val settings: DomainSettings
    val templatesState: TemplatesState
    val mainList: MainList
    val detailNomenclatura: DetailNomenclatura
    val qrCodeState: QrCodeState?
    val qrCodeWarning: QrCodeWarningState
    val ui: UiState

}

interface BottomSheetState {
    val isOpenGroup: Boolean
    val isOpenEntry: Boolean
    val isOpenDetail: Boolean
    val isOpenProductDelete: Boolean
    val isOpenTemplateDelete: Boolean
    val isSaveStateForBottomSheet: Boolean
    val isOpenTemplates: Boolean
    val isOpenEntryInTemplate: Boolean
    val isOpenWarningQrCode: Boolean
    val isOpenScannerQrCode: Boolean
    val isOpenCreateQrCode: Boolean
}

interface SearchState {
    val searchQuery: String
}

interface Support {}

interface MainList

interface DetailNomenclatura

interface QrCodeState

interface QrCodeWarningState

interface UiState {
    val colors: List<Color>
    val iconRes: Int
}


interface TemplatesState {
    val templatesList: List<TemplateItem>
    val templateToDelete: TemplateItem?
}
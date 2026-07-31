package com.zaroslikov.fermacompose2.base.state


import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.BottomSheetState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeWarning
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplatesState

interface SectionState : BaseState {
    val currentProduct: BaseProductState
    val isArchive: Boolean
    val searchState: SearchState
    val ui: UiState
    val mainList: MainList
    val detailNomenclatura: DetailNomenclatura
    val settings: DomainSettings
    val bottomSheetState: BottomSheetState
    val templatesState: TemplatesState
    val qrCodeState: QrCodeData?
    val qrCodeWarning: QrCodeWarning

}


interface SearchState {
    val searchQuery: String
}

interface MainList {
    val isGroupMode: Boolean
}

interface DetailNomenclatura


interface UiState {
    val colors: List<Color>
    val iconRes: Int
}
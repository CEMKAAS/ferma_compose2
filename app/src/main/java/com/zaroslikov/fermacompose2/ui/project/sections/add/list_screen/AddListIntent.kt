package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import com.zaroslikov.domain.models.dto.add.DomainAddItemDto2
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem

sealed class AddListIntent : BaseIntent {

    data class LoadData(
        val itemIdPT: Long,
        val addList: List<DomainAddItemDto2>,
        val briefly: List<BrieflyItem>,
        val settings: DomainSettings,
        val isLoading: Boolean,
        val isArchive: Boolean
    ) : AddListIntent()

    data class LoadDataForDetailNomenclatura(val title: String? = null) : AddListIntent()
    data class OpenBottomSheetGroup(
        val isOpen: Boolean,
        val detail: BrieflyItem? = null,
        val productItems: List<DomainAddItemDto2> = emptyList()
    ) : AddListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val id: Long? = null,
        val isSaveStateForBottomSheet: Boolean = false,
    ) : AddListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: AddProductState,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) : AddListIntent()

    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : AddListIntent()

    data class OpenBottomSheetDetail(
        val value: Long? = null
    ) : AddListIntent()

    data class OpenBottomSheetDelete(val value: Long? = null) : AddListIntent()

    data class GroupClicked(val value: Boolean) : AddListIntent()
    data class TitleChanged(val value: String) : AddListIntent()
    data class TitleAndSuffix(val pair: Pair<String, Suffix>) : AddListIntent()
    data class CountChanged(val value: String) : AddListIntent()
    data class SuffixClicked(val value: Suffix) : AddListIntent()
    data class CategoryChanged(val value: String) : AddListIntent()
    data class Date(val value: String) : AddListIntent()
    data class Animal(val animal: Pair<Long, String>) : AddListIntent()
    data class AnimalNameById(val value: String) : AddListIntent()
    data class AnimalClear(val value: String) : AddListIntent()
    data class NoteChanged(val value: String) : AddListIntent()
    data class SearchChanged(val value: String) : AddListIntent()

    data object Insert : AddListIntent()
    data object Update : AddListIntent()
    data object Delete : AddListIntent()


    //Template
    data class NameTemplateChanged(val value: String) : AddListIntent()
    data class TitleTemplateChanged(val value: Boolean) : AddListIntent()
    data class CountTemplateChanged(val value: Boolean) : AddListIntent()
    data class SuffixTemplateClicked(val value: Boolean) : AddListIntent()
    data class CategoryTemplateChanged(val value: Boolean) : AddListIntent()
    data class DateTemplateChanged(val value: Boolean) : AddListIntent()
    data class AnimalTemplateChanged(val value: Boolean) : AddListIntent()
    data class NoteTemplateChanged(val value: Boolean) : AddListIntent()
    data class MultiProjectTemplateChanged(val value: Boolean) : AddListIntent()

    data class OpenTemplateBottomSheetClick(
        val value: Boolean,
        val toUiMap23: AddProductState = AddProductState()
    ) : AddListIntent()
}
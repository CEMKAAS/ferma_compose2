package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AddListIntent : BaseIntent {
    data class OpenBottomSheetGroup(val title: String? = null) : AddListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val id: Long? = null,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) : AddListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: AddEntryState2,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) : AddListIntent()

    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : AddListIntent()

    data class OpenBottomSheetDetail(
        val value: Long? = null
    ) : AddListIntent()

    data class OpenBottomSheetDelete(val value: Long? = null) : AddListIntent()
    data class OpenTemplateDeleteBottomSheet(val value: Long? = null) : AddListIntent()

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
    data object InsertTemplate : AddListIntent()
    data object UpdateTemplate : AddListIntent()
    data object DeleteTemplate : AddListIntent()

    //Template
    data class NameTemplateChanged(val value: String) : AddListIntent()
    data class TitleTemplateChanged(val value: Boolean) : AddListIntent()
    data class CountTemplateChanged(val value: Boolean) : AddListIntent()
    data class SuffixTemplateClicked(val value: Boolean) : AddListIntent()
    data class CategoryTemplateChanged(val value: Boolean) : AddListIntent()
    data class AnimalTemplateChanged(val value: Boolean) : AddListIntent()
    data class NoteTemplateChanged(val value: Boolean) : AddListIntent()

    data class OpenPatternsBottomSheetClick(val value: Boolean) : AddListIntent()
    data class LoadDataForTemplate(val value: List<TemplateItem>) : AddListIntent()
    data class OpenQrCodeBottomSheetClick(
        val value: Boolean,
        val bitmap: Triple<DomainTemplateTable, Bitmap, Bitmap>? = null
    ) :
        AddListIntent()

    data class CreateQrCodeClick(val value: Long) : AddListIntent()
    data class CreateQrCodeImageClick(val value: ImageBitmap) : AddListIntent()
    data class OpenTemplateBottomSheetClick(
        val value: Boolean,
        val toUiMap23: AddEntryState2 = AddEntryState2()
    ) : AddListIntent()

    data class OpenWarningQrCodeBottomSheetClick(
        val value: Boolean,
    ) : AddListIntent()

    data class LoadDataForTemplateBottomSheetClick(val value: Long) : AddListIntent()
}
package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddProductState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen.SaleListIntent

sealed class WriteOffListIntent : BaseIntent {
    data class LoadDataForDetailNomenclatura(val value: String?) : WriteOffListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val id: Long? = null,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) : WriteOffListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: WriteOffProductState,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) : WriteOffListIntent()

    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : WriteOffListIntent()

    data class OpenBottomSheetDetail(val value: Long? = null) : WriteOffListIntent()
    data class OpenBottomSheetDelete(val value: Long? = null) : WriteOffListIntent()

    data class TitleAndSuffix(
        val title: String, val suffix: Suffix,
        val writeOffProductOrigin: ProductOrigin
    ) : WriteOffListIntent()

    data class SuffixClicked(val value: Suffix) : WriteOffListIntent()
    data class CountChanged(val value: String) : WriteOffListIntent()
    data class PriceChanged(val value: String) : WriteOffListIntent()
    data class AutoPriceClicked(val value: Boolean) : WriteOffListIntent()
    data class CategoryChanged(val value: String) : WriteOffListIntent()
    data class DateClicked(val value: String) : WriteOffListIntent()
    data class NoteChanged(val value: String) : WriteOffListIntent()
    data class StatusClicked(val value: Boolean) : WriteOffListIntent()
    data object Insert : WriteOffListIntent()
    data object Update : WriteOffListIntent()
    data object Delete : WriteOffListIntent()

    data class CountWarehouse(val value: List<DomainCountSuffix>) : WriteOffListIntent()
    data class GroupClicked(val value: Boolean) : WriteOffListIntent()
    data class SearchChanged(val value: String) : WriteOffListIntent()

    //Template
    data class NameTemplateChanged(val value: String) : WriteOffListIntent()
    data class WriteOffStatusClicked(val value: Boolean) : WriteOffListIntent()
    data class TitleTemplateChanged(val value: Boolean) : WriteOffListIntent()
    data class CountTemplateChanged(val value: Boolean) : WriteOffListIntent()
    data class SuffixTemplateClicked(val value: Boolean) : WriteOffListIntent()
    data class DateTemplateChanged(val value: Boolean) : WriteOffListIntent()
    data class PriceTemplateClicked(val value: Boolean) : WriteOffListIntent()
    data class CategoryTemplateChanged(val value: Boolean) : WriteOffListIntent()
    data class NoteTemplateChanged(val value: Boolean) : WriteOffListIntent()
    data class MultiProjectTemplateChanged(val value: Boolean) : WriteOffListIntent()

    data class OpenTemplateBottomSheetClick(
        val value: Boolean,
        val toUiMap23: WriteOffProductState = WriteOffProductState()
    ) : WriteOffListIntent()
}
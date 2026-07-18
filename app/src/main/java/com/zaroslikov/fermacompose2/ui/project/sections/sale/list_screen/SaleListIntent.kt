package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.intent.BaseIntent2
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddProductState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem

sealed class SaleListIntent : BaseIntent {
    data class OpenBottomSheetGroup(val value: String?) : SaleListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val item: DomainSaleTable? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : SaleListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: SaleProductState,
        val isSaveStateForBottomSheet: Boolean = false
    ) : SaleListIntent()

    data class OpenBottomSheetDetail(
        val value: Long? = null
    ) : SaleListIntent()

    data class OpenBottomSheetDelete(val value: Long? = null) : SaleListIntent()

    data class TitleChanged(val value: String) : SaleListIntent()
    data class TitleAndSuffixClicked(
        val title: String,
        val suffix: Suffix,
        val productOrigin: ProductOrigin
    ) : SaleListIntent()

    data class CountChanged(val value: String) : SaleListIntent()
    data class SuffixClicked(val value: Suffix) : SaleListIntent()
    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : SaleListIntent()
    data class PriceChanged(val value: String) : SaleListIntent()
    data class AutoPriceClicked(val value: Boolean) : SaleListIntent()
    data class CategoryChanged(val value: String) : SaleListIntent()
    data class DateClicked(val value: String) : SaleListIntent()
    data class BuyerChanged(val value: String) : SaleListIntent()

    data class NoteChanged(val value: String) : SaleListIntent()
    data class GroupClicked(val value: Boolean) : SaleListIntent()
    data class SearchChanged(val value: String) : SaleListIntent()
    data object Insert : SaleListIntent()
    data object Update : SaleListIntent()
    data object Delete : SaleListIntent()


    //Template
    data class NameTemplateChanged(val value: String) : SaleListIntent()
    data class TitleTemplateChanged(val value: Boolean) : SaleListIntent()
    data class CountTemplateChanged(val value: Boolean) : SaleListIntent()
    data class SuffixTemplateClicked(val value: Boolean) : SaleListIntent()
    data class PriceTemplateClicked(val value: Boolean) : SaleListIntent()
    data class CategoryTemplateChanged(val value: Boolean) : SaleListIntent()
    data class BuyerTemplateChanged(val value: Boolean) : SaleListIntent()
    data class NoteTemplateChanged(val value: Boolean) : SaleListIntent()
    data class MultiProjectTemplateChanged(val value: Boolean) : SaleListIntent()

    data class OpenTemplateDeleteBottomSheet(val value: Long? = null) : SaleListIntent()
    data class OpenPatternsBottomSheetClick(val value: Boolean) : SaleListIntent()
    data class LoadDataForTemplate(val value: List<TemplateItem>) : SaleListIntent()
    data class OpenQrCodeBottomSheetClick(
        val value: Boolean,
        val qrCode: QrCodeData? = null
    ) :
        SaleListIntent()

    data class CreateQrCodeClick(val value: Long) : SaleListIntent()
    data class OpenTemplateBottomSheetClick(
        val value: Boolean,
        val toUiMap23: AddProductState = AddProductState()
    ) : SaleListIntent()

    data class OpenWarningQrCodeBottomSheetClick(
        val value: Boolean,
        val qrCodeWarningType: QrCodeWarningType = QrCodeWarningType.LOCAL,
        val backupData: DomainTemplateTable? = null
    ) : SaleListIntent()

    data class LoadDataForTemplateBottomSheetClick(val value: Long) : SaleListIntent()

    data class OpenScannerQrCodeBottomSheetClick(val value: Boolean) : SaleListIntent()
    data class RecoverClick(val value: DomainTemplateTable) : SaleListIntent()
    data class QrDetected(val value: String) : SaleListIntent()
    data class SetPinOfTemplateClick(val value: Pair<Boolean, Long>) : SaleListIntent()

    data object InsertTemplate : SaleListIntent()
    data object UpdateTemplate : SaleListIntent()
    data object DeleteTemplate : SaleListIntent()
}
package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class SaleListIntent : BaseIntent {
    data class OpenBottomSheetGroup(
        val value: Boolean,
        val currentBriefly: BrieflySaleDomain = BrieflySaleDomain()
    ) : SaleListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val item: DomainSaleTable? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : SaleListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: SaleEntryState2,
        val isSaveStateForBottomSheet: Boolean = false
    ) : SaleListIntent()

    data class TitleChanged(val value: String) : SaleListIntent()
    data class TitleAndSuffixClicked(
        val title: String,
        val suffix: Suffix,
        val category: Category
    ) : SaleListIntent()

    data class CountChanged(val value: String) : SaleListIntent()
    data class SuffixClicked(val value: Suffix) : SaleListIntent()
    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : SaleListIntent()
    data class PriceChanged(val value: String) : SaleListIntent()
    data class AutoPriceClicked(val value: Boolean) : SaleListIntent()
    data class CategoryChanged(val value: String) : SaleListIntent()
    data class DateClicked(val value: String) : SaleListIntent()
    data class BuyerChanged(val value: String) : SaleListIntent()
    data object BuyerClearClicked : SaleListIntent()

    data class NoteChanged(val value: String) : SaleListIntent()
    data class GroupClicked(val value: Boolean) : SaleListIntent()
    data class SearchChanged(val value: String) : SaleListIntent()
    data object Insert : SaleListIntent()
    data object Update : SaleListIntent()
    data class Delete(val value: Long) : SaleListIntent()
}
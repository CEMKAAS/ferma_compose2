package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class WriteOffListIntent : BaseIntent {
    data class OpenBottomSheetGroup(
        val value: Boolean,
        val currentBriefly: BrieflyWriteOffDomain = BrieflyWriteOffDomain()
    ) : WriteOffListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val item: DomainWriteOffTable? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : WriteOffListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: WriteOffEntryState2,
        val isSaveStateForBottomSheet: Boolean = false
    ) : WriteOffListIntent()

    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : WriteOffListIntent()
    data class TitleAndSuffix(
        val title: String, val suffix: Suffix,
        val writeOffCategory: Category
    ) : WriteOffListIntent()

    data class CountChanged(val value: String) : WriteOffListIntent()
    data class PriceChanged(val value: String) : WriteOffListIntent()
    data class AutoPriceClicked(val value: Boolean) : WriteOffListIntent()
    data class DateClicked(val value: String) : WriteOffListIntent()
    data class NoteChanged(val value: String) : WriteOffListIntent()
    data class StatusClicked(val value: Boolean) : WriteOffListIntent()
    data object Insert : WriteOffListIntent()
    data object Update : WriteOffListIntent()
    data class Delete(val value: Long) : WriteOffListIntent()

    data class CountWarehouse(val value: List<DomainCountSuffix>) : WriteOffListIntent()
    data class GroupClicked(val value: Boolean) : WriteOffListIntent()
    data class SearchChanged(val value: String) : WriteOffListIntent()
}
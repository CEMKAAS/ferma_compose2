package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AddListIntent : BaseIntent {
    data class OpenBottomSheetGroup(val title: String? = null) : AddListIntent()

    data class OpenBottomSheetEntry(
        val isOpen: Boolean,
        val state: DomainAddItemDto? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AddListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: AddEntryState2,
        val isSaveStateForBottomSheet: Boolean = false
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
    data object  Delete : AddListIntent()
}
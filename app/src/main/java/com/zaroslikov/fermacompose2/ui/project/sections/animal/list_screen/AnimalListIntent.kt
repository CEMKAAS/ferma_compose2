package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AnimalListIntent : BaseIntent {
    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainAnimalTable? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: AnimalEntryState2,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalListIntent()

    data class AnimalGroupClicked(val value: Boolean) : AnimalListIntent()
    data class TitleChanged(val value: String) : AnimalListIntent()
    data class TypeChanged(val value: String) : AnimalListIntent()
    data class CountChanged(val value: String) : AnimalListIntent()
    data class SuffixClicked(val value: Suffix) : AnimalListIntent()
    data class SexClicked(val value: Boolean) : AnimalListIntent()
    data class PriceChanged(val value: String) : AnimalListIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalListIntent()
    data class DateClicked(val value: String) : AnimalListIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalListIntent()
    data class DateFactoryChanged(val value: String) : AnimalListIntent()
    data class FoodDayChanged(val value: String) : AnimalListIntent()
    data class FoodDaySuffixClicked(val value: Suffix) : AnimalListIntent()
    data class NoteChanged(val value: String) : AnimalListIntent()

    data object Insert : AnimalListIntent()
    data class Archive(val id: Long, val isArchive: Boolean) : AnimalListIntent()
    data class Delete(val value: Long) : AnimalListIntent()

    data class ArchiveClicked(val value: Boolean) : AnimalListIntent()
    data class SearchChanged(val value: String) : AnimalListIntent()
}
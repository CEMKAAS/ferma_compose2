package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem

sealed class ExpensesListIntent : BaseIntent {
    data class OpenBottomSheetGroup(val title: String? = null) : ExpensesListIntent()

    data class OpenEntryBottomSheetByItem(
        val isOpen: Boolean,
        val id: Long? = null,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) : ExpensesListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: ExpensesProductState,
        val isSaveStateForBottomSheet: Boolean = false,
        val isTemplate: Boolean = false
    ) :
        ExpensesListIntent()

    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>) : ExpensesListIntent()

    data class OpenBottomSheetDetail(val value: Long? = null) : ExpensesListIntent()
    data class OpenBottomSheetDelete(val value: Long? = null) : ExpensesListIntent()

    data class FoodClicked(val value: Boolean) : ExpensesListIntent()
    data class PercentClicked(val value: Boolean) : ExpensesListIntent()
    data object EquallyClicked : ExpensesListIntent()

    data class TitleChanged(val value: String) : ExpensesListIntent()
    data class TitleAndSuffixClicked(val title: String, val suffix: Suffix) :
        ExpensesListIntent()

    data class CountChanged(val value: String) : ExpensesListIntent()
    data class SuffixClicked(val value: Suffix) : ExpensesListIntent()

    data class AutoWeightClicked(val value: Boolean) : ExpensesListIntent()
    data class WeightChanged(val value: String) : ExpensesListIntent()
    data class WeightSuffixChanged(val value: Suffix) : ExpensesListIntent()

    data class AutoPriceClicked(val value: Boolean) : ExpensesListIntent()
    data class PriceChanged(val value: String) : ExpensesListIntent()

    data class CategoryChanged(val value: String) : ExpensesListIntent()
    data class DateClicked(val value: String) : ExpensesListIntent()
    data class NoteChanged(val value: String) : ExpensesListIntent()

    data class AnimalChipByIdFoodClicked(val value: Long) : ExpensesListIntent()
    data class AnimalChipByIdClicked(val value: Long) : ExpensesListIntent()
    data class AnimalSliderClicked(val animal: Long, val newValue: Double) :
        ExpensesListIntent()

    data class AnimalValueChanged(val animal: Long, val newValue: String) :
        ExpensesListIntent()

    data class GroupClicked(val value: Boolean) : ExpensesListIntent()
    data class SearchChanged(val value: String) : ExpensesListIntent()
    data object Insert : ExpensesListIntent()
    data object Update : ExpensesListIntent()
    data object Delete : ExpensesListIntent()


    //Template
    data class NameTemplateChanged(val value: String) : ExpensesListIntent()
    data class TitleTemplateChanged(val value: Boolean) : ExpensesListIntent()
    data class CountTemplateChanged(val value: Boolean) : ExpensesListIntent()
    data class SuffixTemplateClicked(val value: Boolean) : ExpensesListIntent()
    data class DateTemplateChanged(val value: Boolean) : ExpensesListIntent()
    data class PriceTemplateClicked(val value: Boolean) : ExpensesListIntent()
    data class CategoryTemplateChanged(val value: Boolean) : ExpensesListIntent()
    data class NoteTemplateChanged(val value: Boolean) : ExpensesListIntent()
    data class MultiProjectTemplateChanged(val value: Boolean) : ExpensesListIntent()

    data class OpenTemplateBottomSheetClick(
        val value: Boolean,
        val toUiMap23: ExpensesProductState = ExpensesProductState()
    ) : ExpensesListIntent()
}
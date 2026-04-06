package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent

sealed class ExpensesListIntent : BaseIntent {
    data class OpenBottomSheetGroup(val title: String? = null) : ExpensesListIntent()

    data class OpenEntryBottomSheetByItem(
        val value: Boolean,
        val item: ExpensesTableUi? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : ExpensesListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: ExpensesEntryState2,
        val isSaveStateForBottomSheet: Boolean = false
    ) :
        ExpensesListIntent()
    data class RefreshWarehouseCount(val value: List<DomainCountSuffix>): ExpensesListIntent()

    data class OpenBottomSheetDetail(
        val value: Long? = null
    ) :  ExpensesListIntent()


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
    data class Delete(val value: Long) : ExpensesListIntent()
}
package com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen

import android.util.Log
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


data class ExpensesListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val openBottomSheetGroup: Boolean = false,
    val openBottomSheetEntry: Boolean = false,
    val currentBriefly: BrieflyExpensesDomain = BrieflyExpensesDomain(),
    val list: List<DomainExpensesTable> = emptyList(),
    val briefly: List<BrieflyExpensesDomain> = emptyList(),
    val listBriefly: List<DomainExpensesTable> = emptyList(),
    override val isEntry: Boolean = false,
    override val currentProduct: ExpensesEntryState2 = ExpensesEntryState2(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : EntryNewState()


data class ExpensesEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val count: String = "",
    val date: String = dateToday(),
    val price: String = "",
    val priceAll: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val category: String = "",
    val note: String = "",
    val isShowFood: Boolean = false,
    val isShowFoodHand: Boolean = false,
    val isShowWarehouse: Boolean = false,
    val isShowAnimals: Boolean = false,

    val feedFoodChip: String = "",
    val feedFoodChipSuffix: Suffix = Suffix.GRAM,
    val countAnimalChip: String = "",

    val feedFoodInput: String = "",
    val feedFoodInputSuffix: Suffix = Suffix.GRAM,
    val countAnimalInput: String = "",

    val daysFood: Int = 0,
    val dateEndFood: String = "",

    val weight: String = "",
    val weightSuffix: Suffix = Suffix.KILOGRAM,
    val isAutoWeight: Boolean = false,
    val isAutoPrice: Boolean = false,

    val pickList: PickExpensesList = PickExpensesList(),
    val countInWarehouse: DomainCountSuffix = DomainCountSuffix(0.0, Suffix.PIECES),

    val isEntry: Boolean = true,
    val isIndicatorsValue: Boolean = false,
    val itemIdPT: Long = 0,
    val error: ErrorExpenses = ErrorExpenses(),
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError(isShowFood, isShowFoodHand, isShowAnimals)

    fun enabledButton(): Boolean {
        val isEnabled =
            title.isNotBlank() && count.isNotBlank() && price.isNotBlank() && !hasAnyError
        return !isEnabled
    }
}

data class PickExpensesList(
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val animalList2: List<AnimalExpensesDomain> = emptyList(),
)

data class ErrorExpenses(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorPrice: Boolean = false,
    val isErrorFood: Boolean = false,
    val isErrorAnimal: Boolean = false,
    val isErrorDailyExpensesFood: Boolean = false,
    val isErrorCountAnimal: Boolean = false,
) : BaseError {

    fun hasAnyError(
        isShowFood: Boolean,
        isShowFoodHand: Boolean,
        isShowAnimals: Boolean
    ): Boolean {
        Log.i("expenses", "isShowFood: $isShowFood")
        Log.i("expenses", "isShowFoodHand: $isShowFoodHand")
        Log.i("expenses", "isShowAnimals: $isShowAnimals")
        return when {
            isShowFoodHand ->
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorDailyExpensesFood || isErrorCountAnimal

            isShowFood ->
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorFood

            isShowAnimals ->
                isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice || isErrorAnimal

            else -> isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
        }
    }


}


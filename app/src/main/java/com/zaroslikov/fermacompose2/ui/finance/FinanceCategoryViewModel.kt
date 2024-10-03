package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.sale.SaleEditDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleEditViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleTableUiState
import com.zaroslikov.fermacompose2.ui.sale.toSaleTableUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class FinanceCategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArg])
    val itemCategory: String =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgTwo])
    val itemBoolean: Boolean =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgThree])
    val dateBegin: String =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgFour])
    val dateEnd: String =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgFive])


    val financeCategoryUiState: StateFlow<FinanceCategoryState> = if (itemBoolean) {
        itemsRepository.getProductListCategoryIncomeCurrentMonth(
            itemId,
            dateBegin, dateEnd,
            itemCategory
        )
            .map { FinanceCategoryState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FinanceCategoryState()
            )
    } else {
        itemsRepository.getProductLisCategoryExpensesCurrentMonth(
            itemId,
            dateBegin, dateEnd,
            itemCategory
        )
            .map { FinanceCategoryState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FinanceCategoryState()
            )
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Ui State for HomeScreen
 */
data class FinanceCategoryState(val itemList: List<Fin> = listOf())



package com.zaroslikov.fermacompose2.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.TimeZone

class FinanceIncomeExpensesViewModel (
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArg])
    val itemBoolean: Boolean =
        checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArgTwo])


    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val month = calendar[Calendar.MONTH] + 1
    val year = calendar[Calendar.YEAR]

    val financeCategoryIEState: StateFlow<FinanceCatState> = if (itemBoolean) {
        itemsRepository.getIncomeCategoryAllList(
            itemId
        )
            .map { FinanceCatState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FinanceCatState()
            )
    } else {
        itemsRepository.getExpensesCategoryAllList(
            itemId
        )
            .map { FinanceCatState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FinanceCatState()
            )
    }


    val financeProductIEState: StateFlow<FinanceCategoryState> = if (itemBoolean) {
        itemsRepository.getIncomeAllList(
            itemId
        )
            .map { FinanceCategoryState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FinanceCategoryState()
            )
    } else {
        itemsRepository.getExpensesAllList(
            itemId
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
data class FinanceCatState(val itemList: List<Fin> = listOf())

package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.sale.toSaleTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class FinanceMountViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[FinanceDestination.itemIdArg])

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val month = calendar[Calendar.MONTH] + 1
    val year = calendar[Calendar.YEAR]

    var incomeMountUiState by mutableDoubleStateOf(0.00)
        private set

    var expensesMountUiState by mutableDoubleStateOf(0.00)
        private set

    init {
        viewModelScope.launch {
            incomeMountUiState = itemsRepository.getIncomeMount(itemId, month, year)
                .filterNotNull()
                .first().toDouble()
        }
        viewModelScope.launch {
            expensesMountUiState = itemsRepository.getExpensesMount(itemId, month, year)
                .filterNotNull()
                .first().toDouble()
        }
    }

    val incomeCategoryUiState: StateFlow<IncomeCategoryUiState> =
        itemsRepository.getCategoryIncomeCurrentMonth(itemId,month,year).map {IncomeCategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncomeCategoryUiState()
            )

    val expensesCategoryUiState: StateFlow<IncomeCategoryUiState> =
        itemsRepository.getCategoryExpensesCurrentMonth(itemId,month,year).map { IncomeCategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncomeCategoryUiState()
            )


//    suspend fun saveItem() {
//        itemsRepository.updateSale(itemUiState.toSaleTable())
//    }
//
//    suspend fun deleteItem() {
//        itemsRepository.deleteSale(itemUiState.toSaleTable())
//    }
//



    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}
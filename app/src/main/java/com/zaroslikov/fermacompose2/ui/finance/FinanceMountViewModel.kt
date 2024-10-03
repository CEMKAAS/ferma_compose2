package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import java.text.SimpleDateFormat
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


    private fun calBegin(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year, month, 1)
        return calendar.timeInMillis
    }

    private fun calEnd(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        return calendar.timeInMillis
    }

    var dateBegin by mutableLongStateOf(0)
        private set
    var dateEnd by mutableLongStateOf(0)
        private set


    var incomeMountUiState by mutableDoubleStateOf(0.00)
        private set

    var expensesMountUiState by mutableDoubleStateOf(0.00)
        private set

    var ownNeedMonthUiState by mutableDoubleStateOf(0.00)
        private set

    var scrapMonthUiState by mutableDoubleStateOf(0.00)
        private set


    var incomeCategoryUiState: StateFlow<IncomeCategoryUiState>
    var expensesCategoryUiState: StateFlow<IncomeCategoryUiState>


    init {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val begin = format.format(calBegin())
        val end = format.format(calEnd())

        dateBegin = calBegin()
        dateEnd = calEnd()

        viewModelScope.launch {
            incomeMountUiState = itemsRepository.getIncomeMount(itemId, begin, end)
                .filterNotNull()
                .first().toDouble()

            expensesMountUiState = itemsRepository.getExpensesMount(itemId, begin, end)
                .filterNotNull()
                .first().toDouble()

            ownNeedMonthUiState = itemsRepository.getOwnNeedMonth(itemId, begin, end)
                .filterNotNull()
                .first().toDouble()

            scrapMonthUiState = itemsRepository.getScrapMonth(itemId, begin, end)
                .filterNotNull()
                .first().toDouble()
        }

        incomeCategoryUiState =
            itemsRepository.getCategoryIncomeCurrentMonth(itemId, begin, end)
                .map { IncomeCategoryUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = IncomeCategoryUiState()
                )

        expensesCategoryUiState =
            itemsRepository.getCategoryExpensesCurrentMonth(itemId, begin, end)
                .map { IncomeCategoryUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = IncomeCategoryUiState()
                )

    }


    fun updateDateBegin(dateString: Long) {
        dateBegin = dateString
    }

    fun updateDateEnd(dateString: Long) {
        dateEnd = dateString
    }


    fun upAnalisis() {

        val format = SimpleDateFormat("yyyy-MM-dd")
        val begin = format.format(dateBegin)
        val end = format.format(dateEnd)

        viewModelScope.launch {
            incomeMountUiState = itemsRepository.getIncomeMount(itemId, begin, end)
                .filterNotNull()
                .first().toDouble()

            expensesMountUiState = itemsRepository.getExpensesMount(itemId, begin, end)
                .filterNotNull()
                .first().toDouble()
        }

        incomeCategoryUiState =
            itemsRepository.getCategoryIncomeCurrentMonth(itemId, begin, end)
                .map { IncomeCategoryUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = IncomeCategoryUiState()
                )

        expensesCategoryUiState =
            itemsRepository.getCategoryExpensesCurrentMonth(itemId, begin, end)
                .map { IncomeCategoryUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = IncomeCategoryUiState()
                )
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

//    suspend fun saveItem() {
//        itemsRepository.updateSale(itemUiState.toSaleTable())
//    }
//
//    suspend fun deleteItem() {
//        itemsRepository.deleteSale(itemUiState.toSaleTable())
//    }
//





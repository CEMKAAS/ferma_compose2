//package com.zaroslikov.fermacompose2.ui.finance
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableDoubleStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQLPair
//import com.zaroslikov.fermacompose2.supportFun.firstDayOfMonth
//import com.zaroslikov.fermacompose2.supportFun.todayOfMonth
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.filterNotNull
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//
//
//class FinanceMountViewModel(
//    savedStateHandle: SavedStateHandle,
//    private val itemsRepository: ItemsRepository
//) : ViewModel() {
//
//    val itemId: Int = checkNotNull(savedStateHandle[FinanceDestination.itemIdArg])
//
//    var incomeMountUiState by mutableDoubleStateOf(0.00)
//        private set
//
//    var expensesMountUiState by mutableDoubleStateOf(0.00)
//        private set
//
//    var ownNeedMonthUiState by mutableDoubleStateOf(0.00)
//        private set
//
//    var scrapMonthUiState by mutableDoubleStateOf(0.00)
//        private set
//
//    var incomeCategoryUiState: StateFlow<IncomeCategoryUiState>
//    var expensesCategoryUiState: StateFlow<IncomeCategoryUiState>
//
//    var dateBegin by mutableStateOf(firstDayOfMonth())
//        private set
//    var dateEnd by mutableStateOf(todayOfMonth())
//        private set
//
//    init {
//        viewModelScope.launch {
//            incomeMountUiState =
//                itemsRepository.getIncomeMount(itemId, dateBegin.first, dateEnd.first)
//                    .filterNotNull()
//                    .first().toDouble()
//
//            expensesMountUiState =
//                itemsRepository.getExpensesMount(itemId, dateBegin.first, dateEnd.first)
//                    .filterNotNull()
//                    .first().toDouble()
//
//            ownNeedMonthUiState =
//                itemsRepository.getOwnNeedMonth(itemId, dateBegin.first, dateEnd.first)
//                    .filterNotNull()
//                    .first().toDouble()
//
//            scrapMonthUiState =
//                itemsRepository.getScrapMonth(itemId, dateBegin.first, dateEnd.first)
//                    .filterNotNull()
//                    .first().toDouble()
//        }
//
//        incomeCategoryUiState =
//            itemsRepository.getCategoryIncomeCurrentMonth(itemId, dateBegin.first, dateEnd.first)
//                .map { IncomeCategoryUiState(it) }
//                .stateIn(
//                    scope = viewModelScope,
//                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                    initialValue = IncomeCategoryUiState()
//                )
//
//        expensesCategoryUiState =
//            itemsRepository.getCategoryExpensesCurrentMonth(itemId, dateBegin.first, dateEnd.first)
//                .map { IncomeCategoryUiState(it) }
//                .stateIn(
//                    scope = viewModelScope,
//                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                    initialValue = IncomeCategoryUiState()
//                )
//
//    }
//
//
//    fun updateDateBegin(dateString: Long) {
//        dateBegin = dateLongToStringSQLPair(dateString)
//    }
//
//    fun updateDateEnd(dateString: Long) {
//        dateEnd = dateLongToStringSQLPair(dateString)
//    }
//
//
//    fun upAnalisis() {
//        viewModelScope.launch {
//            incomeMountUiState =
//                itemsRepository.getIncomeMount(itemId, dateBegin.first, dateEnd.first)
//                    .filterNotNull()
//                    .first().toDouble()
//
//            expensesMountUiState =
//                itemsRepository.getExpensesMount(itemId, dateBegin.first, dateEnd.first)
//                    .filterNotNull()
//                    .first().toDouble()
//        }
//
//        incomeCategoryUiState =
//            itemsRepository.getCategoryIncomeCurrentMonth(itemId, dateBegin.first, dateEnd.first)
//                .map { IncomeCategoryUiState(it) }
//                .stateIn(
//                    scope = viewModelScope,
//                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                    initialValue = IncomeCategoryUiState()
//                )
//
//        expensesCategoryUiState =
//            itemsRepository.getCategoryExpensesCurrentMonth(itemId, dateBegin.first, dateEnd.first)
//                .map { IncomeCategoryUiState(it) }
//                .stateIn(
//                    scope = viewModelScope,
//                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                    initialValue = IncomeCategoryUiState()
//                )
//    }
//
//
//    companion object {
//        private const val TIMEOUT_MILLIS = 5_000L
//    }
//
//}
//
//
//
//

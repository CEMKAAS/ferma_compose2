package com.zaroslikov.fermacompose2.ui.finance.month

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQLPair
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceMonthViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository,
    private val resourceProvider: ResourceProvider
) : ListViewModel<FinanceMonthState, FinanceMonthIntent>(FinanceMonthState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[FinanceMonthDestination.itemIdPT])

    init {
        loadData()
    }

    fun onIntent(intent: FinanceMonthIntent) {
        when (intent) {
            is FinanceMonthIntent.OpenCalendarDialogClicked -> updateOpenCalendarDialog(intent.value)
            is FinanceMonthIntent.CurrentPeriodClicked -> updateCurrentPeriod(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val dateBegin = getState().dateBegin.first
            val dateEnd = getState().dateEnd.first
            val incomeMonth =
                saleRepository.getIncomeMount(itemId, dateBegin, dateEnd)
                    .filterNotNull()
                    .first().toDouble()

            val expensesMonth =
                expensesRepository.getExpensesMount(itemId, dateBegin, dateEnd)
                    .filterNotNull()
                    .first().toDouble()

            val ownNeedMonth =
                writeOffRepository.getOwnNeedMonth(itemId, dateBegin, dateEnd)
                    .filterNotNull()
                    .first().toDouble()

            val scrapMonth =
                writeOffRepository.getScrapMonth(itemId, dateBegin, dateEnd)
                    .filterNotNull()
                    .first().toDouble()

            val incomeCategory =
                saleRepository.getCategoryIncomeCurrentMonth(itemId, dateBegin, dateEnd).first()

            val expensesCategory =
                expensesRepository.getCategoryExpensesCurrentMonth(itemId, dateBegin, dateEnd)
                    .first()

            updateState {
                it.copy(
                    isLoading = false,
                    currentPeriod = resourceProvider.getString(R.string.support_text_now_month),
                    incomeMonth = incomeMonth,
                    expensesMonth = expensesMonth,
                    ownNeedMonth = ownNeedMonth,
                    scrapMonth = scrapMonth,
                    incomeCategory = incomeCategory,
                    expensesCategory = expensesCategory
                )
            }
        }
    }

    private fun updateOpenCalendarDialog(isOpenDialog: Boolean) {
        updateState { it.copy(isOpenCalendarDialog = isOpenDialog) }
    }

    private fun updateCurrentPeriod(currentPeriod: Pair<Long?, Long?>) {
        val dateBegin =
            currentPeriod.first?.let { dateLongToStringSQLPair(it) } ?: getState().dateBegin
        val dateEnd = currentPeriod.first?.let { dateLongToStringSQLPair(it) } ?: getState().dateEnd
        updateState {
            it.copy(
                dateBegin = dateBegin,
                dateEnd = dateEnd,
                currentPeriod = "${dateLongToString(dateBegin.second)} - ${dateLongToString(dateEnd.second)}"
            )
        }
        loadData()
    }
}

sealed class FinanceMonthIntent : BaseIntent {
    data class OpenCalendarDialogClicked(val value: Boolean) : FinanceMonthIntent()
    data class CurrentPeriodClicked(val value: Pair<Long?, Long?>) : FinanceMonthIntent()
}


package com.zaroslikov.fermacompose2.ui.new_year

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.finance.AnalysisAddAnimalAllTimeUiState
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTime
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTimeUiState
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.FinUiState
import com.zaroslikov.fermacompose2.ui.finance.toFinUiState
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


class NewYearViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
) : ViewModel() {

  val itemBoolean: Boolean = checkNotNull(savedStateHandle[NewYearDestination.itemIdArg])
   val itemIdPT: Int = checkNotNull(savedStateHandle[NewYearDestination.itemIdArgTwo])

    private fun calBegin(): Pair<String, String> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)

        return Pair("$year-01-01", "$year-12-31")
    }

    var dateBegin by mutableStateOf(calBegin().first)
        private set
    var dateEnd by mutableStateOf(calBegin().second)
        private set

    var expensesProject by mutableDoubleStateOf(0.0)
        private set
    var saleProject by mutableDoubleStateOf(0.0)
        private set
    var writeOffOwnNeedsProject by mutableDoubleStateOf(0.0)
        private set
    var writeOffScrapProject by mutableDoubleStateOf(0.0)
        private set
    var countAnimalProject by mutableDoubleStateOf(0.00)
        private set

    var countIncubator by mutableIntStateOf(0)
        private set
    var eggInIncubator by mutableIntStateOf(0)
        private set
    var chikenInIncubator by mutableIntStateOf(0)
        private set
    var typeIncubator by mutableStateOf("")
        private set

    var bestProject by mutableStateOf(FinUiState())
        private set


    init {
        viewModelScope.launch {
            if (itemBoolean) {
                expensesProject =
                    itemsRepository.getAnalysisExpensesNewYearProject(itemIdPT, dateBegin, dateEnd)
                        .filterNotNull()
                        .first().toDouble()

                saleProject =
                    itemsRepository.getAnalysisSaleNewYearProject(itemIdPT, dateBegin, dateEnd)
                        .filterNotNull()
                        .first().toDouble()

                writeOffOwnNeedsProject = itemsRepository.getAnalysisWriteOffOwnNeedsNewYearProject(
                    itemIdPT,
                    dateBegin,
                    dateEnd
                ).filterNotNull().first().toDouble()

                writeOffScrapProject =
                    itemsRepository.getAnalysisWriteOffScrapNewYearProject(
                        itemIdPT,
                        dateBegin,
                        dateEnd
                    )
                        .filterNotNull()
                        .first().toDouble()

                countAnimalProject =
                    itemsRepository.getAnalysisCountAnimalNewYearProject(
                        itemIdPT,
                        dateBegin,
                        dateEnd
                    )
                        .filterNotNull()
                        .first().toDouble()
            } else {
                expensesProject =
                    itemsRepository.getAnalysisExpensesNewYear(dateBegin, dateEnd)
                        .filterNotNull()
                        .first().toDouble()

                saleProject =
                    itemsRepository.getAnalysisSaleNewYear(dateBegin, dateEnd)
                        .filterNotNull()
                        .first().toDouble()

                writeOffOwnNeedsProject = itemsRepository.getAnalysisWriteOffOwnNeedsNewYear(
                    dateBegin,
                    dateEnd
                ).filterNotNull().first().toDouble()

                writeOffScrapProject =
                    itemsRepository.getAnalysisWriteOffScrapNewYear(
                        dateBegin,
                        dateEnd
                    ).filterNotNull().first().toDouble()

                countAnimalProject =
                    itemsRepository.getAnalysisCountAnimalNewYear(
                        dateBegin,
                        dateEnd
                    ).filterNotNull().first().toDouble()

                countIncubator =
                    itemsRepository.getIncubatorCountNewYear(dateBegin, dateEnd).filterNotNull()
                        .first().toInt()
                eggInIncubator =
                    itemsRepository.getEggInIncubatorNewYear(dateBegin, dateEnd).filterNotNull()
                        .first().toInt()
                chikenInIncubator =
                    itemsRepository.getChikenInIncubatorNewYear(dateBegin, dateEnd).filterNotNull()
                        .first().toInt()
                typeIncubator =
                    itemsRepository.getTypeIncubatorNewYear(dateBegin, dateEnd).filterNotNull()
                        .first().toString()

                bestProject =
                    itemsRepository.getBestProjectNewYear(dateBegin, dateEnd).filterNotNull()
                        .first().toFinUiState()
            }
        }
    }


    var bestSale: StateFlow<AnalysisSaleBuyerAllTimeUiState> = if (itemBoolean) {
        itemsRepository.getAnalysisSaleProductNewYearProject(itemIdPT, dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    } else {
        itemsRepository.getAnalysisSaleProductNewYear(dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    }

    var bestBuyer: StateFlow<AnalysisSaleBuyerAllTimeUiState> = if (itemBoolean) {
        itemsRepository.getAnalysisSaleBuyerNewYearProject(itemIdPT, dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    } else {
        itemsRepository.getAnalysisSaleBuyerNewYear(dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    }

    var bestExpenses: StateFlow<AnalysisSaleBuyerAllTimeUiState> = if (itemBoolean) {
        itemsRepository.getAnalysisExpensesProductNewYearProject(itemIdPT, dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    } else {
        itemsRepository.getAnalysisExpensesProductNewYear(dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    }


    var bestAdd: StateFlow<AnalysisSaleBuyerAllTimeUiState> = if (itemBoolean) {
        itemsRepository.getAnalysisAddProductNewYearProject(itemIdPT, dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    } else {
        itemsRepository.getAnalysisAddProductNewYear(dateBegin, dateEnd)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
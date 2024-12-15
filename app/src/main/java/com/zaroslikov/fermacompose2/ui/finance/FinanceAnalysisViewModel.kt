package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
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

class FinanceAnalysisViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArg])
    val name: String = checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArgTwo])

    private fun calBegin(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        calendar.set(year, month, 1)
        return calendar.timeInMillis
    }

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    var dateBegin by mutableLongStateOf(calBegin())
        private set
    var dateEnd by mutableLongStateOf(calendar.timeInMillis)
        private set


    var analysisAddAllTime by mutableStateOf(FinUiState())
        private set
    var analysisSaleAllTime by mutableStateOf(FinUiState())
        private set
    var analysisWriteOffAllTime by mutableStateOf(FinUiState())
        private set
    var analysisWriteOffOwnNeedsAllTime by mutableStateOf(FinUiState())
        private set
    var analysisWriteOffScrapAllTime by mutableStateOf(FinUiState())
        private set
    var analysisSaleSoldAllTime by mutableDoubleStateOf(0.00)
        private set
    var analysisWriteOffOwnNeedsMoneyAllTime by mutableDoubleStateOf(0.00)
        private set
    var analysisWriteOffScrapMoneyAllTime by mutableDoubleStateOf(0.00)
        private set
    var analysisAddAverageValueAllTime by mutableStateOf(FinUiState())
        private set

    init {

        viewModelScope.launch {
            analysisAddAllTime =
                itemsRepository.getAnalysisAddAllTime(itemId, name)
                    .filterNotNull()
                    .first().toFinUiState()

            analysisSaleAllTime = itemsRepository.getAnalysisSaleAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisWriteOffAllTime = itemsRepository.getAnalysisWriteOffAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisWriteOffOwnNeedsAllTime =
                itemsRepository.getAnalysisWriteOffOwnNeedsAllTime(itemId, name)
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffScrapAllTime =
                itemsRepository.getAnalysisWriteOffScrapAllTime(itemId, name)
                    .filterNotNull()
                    .first().toFinUiState()
            analysisSaleSoldAllTime = itemsRepository.getAnalysisSaleSoldAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()
            analysisWriteOffOwnNeedsMoneyAllTime =
                itemsRepository.getAnalysisWriteOffOwnNeedsMoneyAllTime(itemId, name)
                    .filterNotNull()
                    .first().toDouble()
            analysisWriteOffScrapMoneyAllTime =
                itemsRepository.getAnalysisWriteOffScrapMoneyAllTime(itemId, name)
                    .filterNotNull()
                    .first().toDouble()
            analysisAddAverageValueAllTime =
                itemsRepository.getAnalysisAddAverageValueAllTime(itemId, name)
                    .filterNotNull()
                    .first().toFinUiState()
        }
    }


    var analysisAddAnimalAllTimeState: StateFlow<AnalysisAddAnimalAllTimeUiState> =
        itemsRepository.getAnalysisAddAnimalAllTime(itemId, name)
            .map { AnalysisAddAnimalAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisAddAnimalAllTimeUiState()
            )

    var analysisSaleBuyerAllTimeState: StateFlow<AnalysisSaleBuyerAllTimeUiState> =
        itemsRepository.getAnalysisSaleBuyerAllTime(itemId, name)
            .map { AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )

    var analysisCostPriceTimeState: StateFlow<AnalysisAddAnimalAllTimeUiState2> =
        itemsRepository.getAnalysisCostPriceAllTime(itemId, name)
            .map { AnalysisAddAnimalAllTimeUiState2(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisAddAnimalAllTimeUiState2()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
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
            analysisAddAllTime =
                itemsRepository.getAnalysisAddAllTimeRange(itemId, name, begin, end)
                    .filterNotNull().first().toFinUiState()
            analysisSaleAllTime =
                itemsRepository.getAnalysisSaleAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffAllTime =
                itemsRepository.getAnalysisWriteOffAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffOwnNeedsAllTime =
                itemsRepository.getAnalysisWriteOffOwnNeedsAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffScrapAllTime =
                itemsRepository.getAnalysisWriteOffScrapAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toFinUiState()
            analysisSaleSoldAllTime =
                itemsRepository.getAnalysisSaleSoldAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toDouble()
            analysisWriteOffOwnNeedsMoneyAllTime =
                itemsRepository.getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toDouble()
            analysisWriteOffScrapMoneyAllTime =
                itemsRepository.getAnalysisWriteOffScrapMoneyAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toDouble()
            analysisAddAverageValueAllTime =
                itemsRepository.getAnalysisAddAverageValueAllTimeRange(itemId, name, begin, end)
                    .filterNotNull()
                    .first().toFinUiState()
        }

        analysisAddAnimalAllTimeState =
            itemsRepository.getAnalysisAddAnimalAllTimeRange(itemId, name, begin, end)
                .map { AnalysisAddAnimalAllTimeUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnalysisAddAnimalAllTimeUiState()
                )

        analysisSaleBuyerAllTimeState =
            itemsRepository.getAnalysisSaleBuyerAllTimeRange(itemId, name, begin, end)
                .map { AnalysisSaleBuyerAllTimeUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnalysisSaleBuyerAllTimeUiState()
                )

        analysisCostPriceTimeState =
            itemsRepository.getAnalysisCostPriceAllTimeRange(itemId, name, begin, end)
                .map { AnalysisAddAnimalAllTimeUiState2(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnalysisAddAnimalAllTimeUiState2()
                )

    }
}


data class AnalysisAddAnimalAllTimeUiState(val itemList: List<AnimalTitSuff> = listOf())

data class AnalysisAddAnimalAllTimeUiState2(val itemList: List<Fin> = listOf())
data class AnalysisSaleBuyerAllTimeUiState(val itemList: List<AnalysisSaleBuyerAllTime> = listOf())

fun Fin.toFinUiState(
): FinUiState = FinUiState(
    title ?: "", priceAll
)

data class AnalysisSaleBuyerAllTime(
    val buyer: String,
    val resultPrice: Double,
    val resultCount: Double,
    val suffix: String
)

package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.ui.animal.AnimalCoutUiStateLimit
import com.zaroslikov.fermacompose2.ui.animal.AnimalEditUiState
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceAnalysisViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArg])
    val name: String = checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArgTwo])

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
            analysisAddAllTime = itemsRepository.getAnalysisAddAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisSaleAllTime= itemsRepository.getAnalysisSaleAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisWriteOffAllTime = itemsRepository.getAnalysisWriteOffAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisWriteOffOwnNeedsAllTime = itemsRepository.getAnalysisWriteOffOwnNeedsAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisWriteOffScrapAllTime = itemsRepository.getAnalysisWriteOffScrapAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
            analysisSaleSoldAllTime = itemsRepository.getAnalysisSaleSoldAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()
            analysisWriteOffOwnNeedsMoneyAllTime = itemsRepository.getAnalysisWriteOffOwnNeedsMoneyAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()
            analysisWriteOffScrapMoneyAllTime= itemsRepository.getAnalysisWriteOffScrapMoneyAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()
            analysisAddAverageValueAllTime = itemsRepository.getAnalysisAddAverageValueAllTime(itemId, name)
                .filterNotNull()
                .first().toFinUiState()
        }
    }


    val analysisAddAnimalAllTimeState: StateFlow<AnalysisAddAnimalAllTimeUiState> =
        itemsRepository.getAnalysisAddAnimalAllTime(itemId, name).map { AnalysisAddAnimalAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisAddAnimalAllTimeUiState()
            )

    val analysisSaleBuyerAllTimeState: StateFlow<AnalysisSaleBuyerAllTimeUiState> =
        itemsRepository.getAnalysisSaleBuyerAllTime(itemId, name).map {AnalysisSaleBuyerAllTimeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnalysisSaleBuyerAllTimeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

data class AnalysisAddAnimalAllTimeUiState(val itemList: List<AnimalTitSuff> = listOf())
data class AnalysisSaleBuyerAllTimeUiState(val itemList: List<AnalysisSaleBuyerAllTime> = listOf())

fun Fin.toFinUiState(
): FinUiState = FinUiState(
    title, priceAll
)

data class AnalysisSaleBuyerAllTime(
    val buyer: String,
    val resultPrice: Double,
    val resultCount: Double,
    val suffix: String
)
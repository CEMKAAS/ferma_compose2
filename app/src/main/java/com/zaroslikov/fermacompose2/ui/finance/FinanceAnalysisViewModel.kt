package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.animal.AnimalCoutUiStateLimit
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

    val itemId: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArg])
    val name: String = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArg])
    val suffix: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArg])


    var analysisAddAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisSaleAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisWriteOffAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisWriteOffOwnNeedsAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisWriteOffScrapAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisSaleSoldAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisWriteOffOwnNeedsMoneyAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisWriteOffScrapMoneyAllTime by mutableDoubleStateOf(0.00)
        private set

    var analysisAddAverageValueAllTime by mutableDoubleStateOf(0.00)
        private set

    init {
        viewModelScope.launch {
            analysisAddAllTime = itemsRepository.getAnalysisAddAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()

            analysisSaleAllTime= itemsRepository.getAnalysisSaleAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()

            analysisWriteOffAllTime = itemsRepository.getAnalysisWriteOffAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()

            analysisWriteOffOwnNeedsAllTime = itemsRepository.getAnalysisWriteOffOwnNeedsAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()

            analysisWriteOffScrapAllTime = itemsRepository.getAnalysisWriteOffScrapAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()

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
                .first().toDouble()
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

data class AnalysisSaleBuyerAllTime(
    val buyer: String,
    val resultPrice: Double,
    val resultCount: Double,
    val suffix: String
)
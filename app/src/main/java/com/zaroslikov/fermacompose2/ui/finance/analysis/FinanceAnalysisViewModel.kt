package com.zaroslikov.fermacompose2.ui.finance.analysis
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQLPair
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceAnalysisViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val writeOffRepository: WriteOffRepository,
    private val resourceProvider: ResourceProvider
) : ListViewModel<FinanceAnalysisState, FinanceAnalysisIntent>(FinanceAnalysisState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArg])
    private val name: String =
        checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArgTwo])

    init {
        loadData()
    }

    fun onIntent(intent: FinanceAnalysisIntent) {
        when (intent) {
            is FinanceAnalysisIntent.CurrentPeriodClicked -> updateCurrentPeriod(intent.value)
            is FinanceAnalysisIntent.OpenCalendarDialogClicked -> updateOpenCalendarDialog(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val productAdd = addRepository.getAnalysisAddAllTime(itemId, name)
                .filterNotNull()
                .first()
            val productSale = saleRepository.getAnalysisSaleAllTime(itemId, name)
                .filterNotNull()
                .first()
            val productWriteOff = writeOffRepository.getAnalysisWriteOffAllTime(itemId, name)
                .filterNotNull()
                .first()
            val productOnwNeeds =
                writeOffRepository.getAnalysisWriteOffOwnNeedsAllTime(itemId, name)
                    .filterNotNull()
                    .first()
            val productScrap = writeOffRepository.getAnalysisWriteOffScrapAllTime(itemId, name)
                .filterNotNull()
                .first()
            val productSaleSold = saleRepository.getAnalysisSaleSoldAllTime(itemId, name)
                .filterNotNull()
                .first().toDouble()
            val productWriteOffOnwNeedMoney =
                writeOffRepository.getAnalysisWriteOffOwnNeedsMoneyAllTime(itemId, name)
                    .filterNotNull()
                    .first().toDouble()
            val productWriteOffScrapMoney =
                writeOffRepository.getAnalysisWriteOffScrapMoneyAllTime(itemId, name)
                    .filterNotNull()
                    .first().toDouble()
            val productAverage = addRepository.getAnalysisAddAverageValueAllTime(itemId, name)
                .filterNotNull()
                .first()
//           var analysisAddAnimalAllTimeState= itemsRepository.getAnalysisAddAnimalAllTime(itemId, name)
//            var analysisCostPriceTimeState = writeOffRepository.getAnalysisCostPriceAllTime(itemId, name)
            val saleBuyerList = saleRepository.getAnalysisSaleBuyerAllTime(itemId, name).first()

            updateState {
                it.copy(
                    isLoading = false,
                    productAdd = productAdd,
                    productSale = productSale,
                    productWriteOff = productWriteOff,
                    productOnwNeeds = productOnwNeeds,
                    productScrap = productScrap,
                    productSaleSold = productSaleSold,
                    productWriteOffOnwNeedMoney = productWriteOffOnwNeedMoney,
                    productWriteOffScrapMoney = productWriteOffScrapMoney,
                    productAverage = productAverage,
                    saleBuyerList = saleBuyerList,
                    currentPeriod = resourceProvider.getString(R.string.support_text_all_time)
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
    /*fun upAnalisis() {
        viewModelScope.launch {
            analysisAddAllTime =
                itemsRepository.getAnalysisAddAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull().first().toFinUiState()
            analysisSaleAllTime =
                itemsRepository.getAnalysisSaleAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffAllTime =
                itemsRepository.getAnalysisWriteOffAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffOwnNeedsAllTime =
                itemsRepository.getAnalysisWriteOffOwnNeedsAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toFinUiState()
            analysisWriteOffScrapAllTime =
                itemsRepository.getAnalysisWriteOffScrapAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toFinUiState()
            analysisSaleSoldAllTime =
                itemsRepository.getAnalysisSaleSoldAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toDouble()
            analysisWriteOffOwnNeedsMoneyAllTime =
                itemsRepository.getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toDouble()
            analysisWriteOffScrapMoneyAllTime =
                itemsRepository.getAnalysisWriteOffScrapMoneyAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toDouble()
            analysisAddAverageValueAllTime =
                itemsRepository.getAnalysisAddAverageValueAllTimeRange(
                    itemId,
                    name,
                    dateBegin.first,
                    dateEnd.first
                )
                    .filterNotNull()
                    .first().toFinUiState()
        }

        analysisAddAnimalAllTimeState =
            itemsRepository.getAnalysisAddAnimalAllTimeRange(
                itemId,
                name,
                dateBegin.first,
                dateEnd.first
            )
                .map { AnalysisAddAnimalAllTimeUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnalysisAddAnimalAllTimeUiState()
                )

        analysisSaleBuyerAllTimeState =
            itemsRepository.getAnalysisSaleBuyerAllTimeRange(
                itemId,
                name,
                dateBegin.first,
                dateEnd.first
            )
                .map { AnalysisSaleBuyerAllTimeUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnalysisSaleBuyerAllTimeUiState()
                )

        analysisCostPriceTimeState =
            itemsRepository.getAnalysisCostPriceAllTimeRange(
                itemId,
                name,
                dateBegin.first,
                dateEnd.first
            )
                .map { AnalysisAddAnimalAllTimeUiState2(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnalysisAddAnimalAllTimeUiState2()
                )

    }*/
}

//data class AnalysisSaleBuyerAllTime(
//    val buyer: String,
//    val resultPrice: Double,
//    val resultCount: Double,
//    val suffix: String
//)

sealed class FinanceAnalysisIntent : BaseIntent {
    data class OpenCalendarDialogClicked(val value: Boolean) : FinanceAnalysisIntent()
    data class CurrentPeriodClicked(val value: Pair<Long?, Long?>) : FinanceAnalysisIntent()
}
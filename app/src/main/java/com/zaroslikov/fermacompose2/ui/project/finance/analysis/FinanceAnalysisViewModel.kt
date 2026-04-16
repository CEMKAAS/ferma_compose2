package com.zaroslikov.fermacompose2.ui.project.finance.analysis

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix
import com.zaroslikov.domain.models.dto.finance.DomainAnimalWitchCountAndImage
import com.zaroslikov.domain.models.dto.finance.DomainTransaction
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.DomainChartPoint
import com.zaroslikov.fermacompose2.supportFun.chartFilter
import com.zaroslikov.fermacompose2.supportFun.conversation2
import com.zaroslikov.fermacompose2.supportFun.conversation22
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQLPair
import com.zaroslikov.fermacompose2.supportFun.datePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.first

@HiltViewModel
class FinanceAnalysisViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val writeOffRepository: WriteOffRepository,
    private val financeRepository: FinanceRepository,
    private val settingsRepository: SettingsRepository
) : ListViewModel<FinanceAnalysisState, FinanceAnalysisIntent>(FinanceAnalysisState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArg])
    private val titleProduct: String =
        checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArgTwo])
    private val baseSuffix: Suffix =
        Suffix.valueOf(checkNotNull(savedStateHandle[FinanceAnalysisDestination.itemIdArgTree]))

    init {
        loadData()
    }

    fun onIntent(intent: FinanceAnalysisIntent) {
        when (intent) {
            is FinanceAnalysisIntent.CurrentPeriodClicked -> updateCurrentPeriod(intent.value)
            is FinanceAnalysisIntent.OpenCalendarDialogClicked -> updateOpenCalendarDialog(intent.value)
            is FinanceAnalysisIntent.FilterDateClicked -> updateFilterDate(intent.value)
            is FinanceAnalysisIntent.CharSelectionClicked -> updateCharSelection(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val state = getState()
            val (start, end) = datePeriod(
                state.dateFilter.filterDate,
                state.dateFilter.dateBegin.first,
                state.dateFilter.dateEnd.first
            )
            combine(
                financeRepository.getAnalysisTransactionList(itemId, titleProduct, start, end),
                saleRepository.getAnalysisSaleBuyerRangeList(itemId, titleProduct, start, end),
                addRepository.getAnalysisAddAnimalRangeList(itemId, titleProduct, start, end),
                addRepository.getAnalysisAddRangeList(itemId, titleProduct, start, end),
                writeOffRepository.getAnalysisOwnNeedsScrapRangeList(
                    itemId,
                    titleProduct,
                    true,
                    start,
                    end
                ),
                writeOffRepository.getAnalysisOwnNeedsScrapRangeList(
                    itemId,
                    titleProduct,
                    false,
                    start,
                    end
                ),
                saleRepository.getAnalysisSaleRangeList(itemId, titleProduct, start, end),
                settingsRepository.getSettings(itemId)
            ) { values: Array<Any?> ->
                buildUiState(
                    transactionList = values[0] as List<DomainTransaction>,
                    buyersList = values[1] as List<DomainBuyerPrice>,
                    animalProducerList = values[2] as List<DomainAnimalWitchCountAndImage>,
                    productList = values[3] as List<DomainCountSuffixPriceDate>,
                    totalOwnNeed = values[4] as List<DomainCountSuffixPriceDate>,
                    totalScrap = values[5] as List<DomainCountSuffixPriceDate>,
                    saleProduct = values[6] as List<DomainCountSuffixPriceDate>,
                    settings = values[7] as DomainSettings,
                    start = start,
                    end = end,
                )
            }.collect { newState ->
                updateState {
                    it.copy(
                        isLoading = false,
                        titleProduct = titleProduct,
                        buyers = newState.buyers,
                        animalProducer = newState.animalProducer,
                        totalPrice = newState.totalPrice,
                        countProduct = newState.countProduct,
                        financeAnalysis = newState.financeAnalysis,
                        stock = newState.stock,
                        averagePrice = newState.averagePrice,
                        realizedPrice = newState.realizedPrice,
                        potentialBalance = newState.potentialBalance,
                        soldLost = newState.soldLost,
                        transactionList = newState.transactionList,
                        charFilter = newState.charFilter,
                        baseSuffix = baseSuffix
                    )
                }
            }

        }
    }

    private fun buildUiState(
        transactionList: List<DomainTransaction>,
        buyersList: List<DomainBuyerPrice>,
        animalProducerList: List<DomainAnimalWitchCountAndImage>,
        productList: List<DomainCountSuffixPriceDate>,
        totalOwnNeed: List<DomainCountSuffixPriceDate>,
        totalScrap: List<DomainCountSuffixPriceDate>,
        saleProduct: List<DomainCountSuffixPriceDate>,
        settings: DomainSettings,
        start: String,
        end: String,
    ): AnalysisUiCalculatedState {
        val topBuyer = topBuyersList(buyersList, settings)
        val animalProducerList = animalProducerList(animalProducerList, settings)
        val char = char(productList, saleProduct, totalOwnNeed, totalScrap, settings, start, end)

        val transactionList2 =
            transactionList.filter { it.suffix.conversation4(settings) == baseSuffix }

        Log.i("analysis", "buildUiState: $baseSuffix")

        val totalCountProduct =
            productList.filter { it.suffix.conversation4(settings) == baseSuffix }.sumOf {
                it.count.conversation22(it.suffix, baseSuffix, settings)
            } //TODO

        val financeSale =
            financeAnalysis(
                saleProduct,
                settings.currencySuffix,
                FinanceAnalysisEnum.SALE,
                settings
            )
        val financeOwnNeed =
            financeAnalysis(
                totalOwnNeed,
                settings.currencySuffix,
                FinanceAnalysisEnum.OWN_NEED,
                settings
            )
        val financeScrap =
            financeAnalysis(
                totalScrap,
                settings.currencySuffix,
                FinanceAnalysisEnum.SCRAP,
                settings
            )

        val totalStock = // TODO
            totalCountProduct - financeSale.totalCount - financeOwnNeed.totalCount - financeScrap.totalCount
        val realizedPrice = financeSale.totalPrice + financeOwnNeed.totalPrice


        val averagePurchasePrice = financeSale.averagePrice.takeIf { it > 0 }
            ?: financeOwnNeed.averagePrice.takeIf { it > 0 }
            ?: financeScrap.averagePrice.takeIf { it > 0 }
            ?: 0.0

        val totalPriceStock = totalStock * averagePurchasePrice // TODO

        val financeStock = FinanceAnalysis(
            totalCount = totalStock,
            suffixCount = baseSuffix,
            totalPrice = totalPriceStock,
            suffixPrice = settings.currencySuffix,
            averagePrice = averagePurchasePrice,
            financeAnalysis = FinanceAnalysisEnum.STOCK,
            percentDouble = 0.0,
            percentFloat = 0f
        )

        val financeAnalysisList =
            listOf(financeSale, financeOwnNeed, financeScrap, financeStock)

        val totalAllAnimals2 = financeAnalysisList.sumOf { it.totalCount }
        val resultAnimalProducer2 = financeAnalysisList.map { animal -> //TODO
            val percent =
                if (totalAllAnimals2 == 0.0) 0.0
                else (animal.totalCount / totalAllAnimals2)

            animal.copy(
                percentDouble = percent * 100.0,
                percentFloat = percent.toFloat()
            )
        }

        val totalPrice = financeAnalysisList.sumOf { it.totalPrice } //TODO

        return AnalysisUiCalculatedState(
            buyers = topBuyer,
            animalProducer = animalProducerList,
            charFilter = char,
            financeAnalysis = resultAnimalProducer2,
            totalPrice = totalPrice,
            countProduct = totalCountProduct,
            realizedPrice = realizedPrice,
            potentialBalance = totalPriceStock,
            soldLost = financeScrap.totalPrice,
            stock = totalStock,
            transactionList = transactionList2,
            averagePrice = resultAnimalProducer2[0].averagePrice,
        )
    }

    private fun animalProducerList(
        animalProducerList: List<DomainAnimalWitchCountAndImage>,
        domainSettings: DomainSettings
    ): List<AnimalProducer> {
        val groupedAnimalProducerList = animalProducerList
            .filter { it.countSuffix.conversation4(domainSettings) == baseSuffix }
            .groupBy { it.title } // название животного
            .map { (animalName, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation22(
                        suffix = it.countSuffix,
                        baseSuffix = baseSuffix,
                        settings = domainSettings
                    )
                }
                AnimalProducer(
                    name = animalName,
                    type = items.first().type, // тип одинаковый внутри группы
                    count = totalCount,
                    suffix = baseSuffix,
                    percentDouble = 0.0, // временно, заполним позже
                    percentFloat = 0f,
                    imagePath = items.first().imagePath,
                    currentIcon = items.first().currentIcon ?: R.drawable.baseline_pets_24,
                )
            }
        val totalAllAnimals = groupedAnimalProducerList.sumOf { it.count }
        return groupedAnimalProducerList.map { animal ->
            val percent =
                if (totalAllAnimals == 0.0) 0.0
                else (animal.count / totalAllAnimals)
            animal.copy(
                percentDouble = percent * 100.0,
                percentFloat = percent.toFloat()
            )
        }
    }

    private fun topBuyersList(
        buyersList: List<DomainBuyerPrice>,
        domainSettings: DomainSettings
    ): List<Buyer> {
        return buyersList
            .filter { it.suffix.conversation4(domainSettings) == baseSuffix }
            .groupBy { it.buyer }
            .map { (buyerName, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation22(
                        suffix = it.suffix, baseSuffix, settings = domainSettings
                    )
                }
                val totalPrice = items.sumOf { it.price }
                Buyer(
                    buyer = buyerName,
                    count = totalCount,
                    suffix = baseSuffix,
                    price = totalPrice,
                    priceSuffix = Suffix.RUBLE,
                    countTransaction = items.size
                )
            }
    }

    private fun char(
        productList: List<DomainCountSuffixPriceDate>,
        saleProduct: List<DomainCountSuffixPriceDate>,
        totalOwnNeed: List<DomainCountSuffixPriceDate>,
        totalScrap: List<DomainCountSuffixPriceDate>,
        domainSettings: DomainSettings,
        start: String,
        end: String
    ): List<Pair<List<DomainChartPoint>, FinanceAnalysisEnum>> {

        val chartFilterStock = chartFilter(
            productList, getState().dateFilter.filterDate, start,
            end, baseSuffix, domainSettings
        )
        val chartFilterSale = chartFilter(
            saleProduct, getState().dateFilter.filterDate, start,
            end, baseSuffix, domainSettings
        )
        val chartFilterOwnNeed = chartFilter(
            totalOwnNeed, getState().dateFilter.filterDate, start,
            end, baseSuffix, domainSettings
        )
        val chartFilterScrap = chartFilter(
            totalScrap, getState().dateFilter.filterDate, start,
            end, baseSuffix, domainSettings
        )

        return listOf(
            chartFilterStock to FinanceAnalysisEnum.STOCK,
            chartFilterSale to FinanceAnalysisEnum.SALE,
            chartFilterOwnNeed to FinanceAnalysisEnum.OWN_NEED,
            chartFilterScrap to FinanceAnalysisEnum.SCRAP
        )
    }

    private fun financeAnalysis(
        list: List<DomainCountSuffixPriceDate>,
        suffixPrice: Suffix,
        financeAnalysisEnum: FinanceAnalysisEnum,
        settings: DomainSettings
    ): FinanceAnalysis {
        val totalProduct2 =
            list.filter { it.suffix.conversation4(settings) == baseSuffix }.sumOf {
                it.count.conversation22(it.suffix, baseSuffix, settings)
            }
        val totalSum =
            list.filter { it.suffix.conversation4(settings) == baseSuffix }.sumOf { it.price }
        val averagePrice = if (totalSum == 0.0) 0.0 else totalSum / totalProduct2

        return FinanceAnalysis(
            totalCount = totalProduct2,
            suffixCount = baseSuffix,
            totalPrice = totalSum,
            suffixPrice = suffixPrice,
            averagePrice = averagePrice,
            financeAnalysis = financeAnalysisEnum,
            percentDouble = 0.0,
            percentFloat = 0f
        )
    }

    private fun updateCharSelection(filterAnalysisEnum: FinanceAnalysisEnum) {
        updateState {
            it.copy(
                charSelection = filterAnalysisEnum
            )
        }
    }

    private fun updateFilterDate(filterDate: FilterDate) {
        if (filterDate == FilterDate.PERIOD) updateOpenCalendarDialog(true)
        else start(filterDate)
    }

    private fun start(filterDate: FilterDate) {
        updateState {
            it.copy(dateFilter = it.dateFilter.copy(filterDate = filterDate))
        }
        loadData()
    }

    private fun updateOpenCalendarDialog(isOpenDialog: Boolean) {
        updateState {
            it.copy(
                dateFilter = it.dateFilter.copy(
                    isOpenCalendarDialog = isOpenDialog
                )
            )
        }
    }

    private fun updateCurrentPeriod(currentPeriod: Pair<Long?, Long?>) {
        val dateBegin =
            currentPeriod.first?.let { dateLongToStringSQLPair(it) }
                ?: getState().dateFilter.dateBegin
        val dateEnd = currentPeriod.second?.let { dateLongToStringSQLPair(it) }
            ?: getState().dateFilter.dateEnd
        updateState {
            it.copy(
                dateFilter = it.dateFilter.copy(
                    dateBegin = dateBegin,
                    dateEnd = dateEnd,
                    currentPeriod = "${dateLongToString(dateBegin.second)} - " +
                            dateLongToString(dateEnd.second)
                )
            )
        }
        start(filterDate = FilterDate.PERIOD)
    }
}

sealed class FinanceAnalysisIntent : BaseIntent {
    data class FilterDateClicked(val value: FilterDate) : FinanceAnalysisIntent()
    data class OpenCalendarDialogClicked(val value: Boolean) : FinanceAnalysisIntent()
    data class CurrentPeriodClicked(val value: Pair<Long?, Long?>) : FinanceAnalysisIntent()
    data class CharSelectionClicked(val value: FinanceAnalysisEnum) : FinanceAnalysisIntent()
}

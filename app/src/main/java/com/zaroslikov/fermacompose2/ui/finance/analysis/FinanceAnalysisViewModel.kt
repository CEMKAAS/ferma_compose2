package com.zaroslikov.fermacompose2.ui.finance.analysis

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPrice
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.conversation2
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQLPair
import com.zaroslikov.fermacompose2.supportFun.datePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceAnalysisViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val writeOffRepository: WriteOffRepository,
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
        }
    }


    private fun loadData(
        suffixPrice: Suffix = Suffix.RUBLE
    ) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val (start, end) = datePeriod(
                getState().dateFilter.filterDate,
                getState().dateFilter.dateBegin.first,
                getState().dateFilter.dateEnd.first
            )
            val productList = addRepository.getAnalysisAllTimeRangeList(
                itemId,
                titleProduct,
                start,
                end
            ).first()

            val totalCountProduct =
                productList.sumOf {
                    it.count.conversation2(it.suffix, baseSuffix, baseSuffix)
                }

            val buyersList = saleRepository.getAnalysisSaleBuyerRangeList(
                itemId,
                titleProduct,
                start,
                end
            ).first()

            val topBuyersList = buyersList
                .groupBy { it.buyer }
                .map { (buyerName, items) ->
                    val totalCount = items.sumOf {
                        it.count.conversation2(
                            suffix = it.suffix, baseSuffix, baseSuffix
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

            val animalProducerList = addRepository.getAnalysisAddAnimalRangeList(
                itemId,
                titleProduct,
                start,
                end
            ).first()

            val groupedAnimalProducerList = animalProducerList
                .groupBy { it.title } // название животного
                .map { (animalName, items) ->
                    val totalCount = items.sumOf {
                        it.count.conversation2(
                            suffix = it.suffix,
                            baseSuffix = baseSuffix,
                            settingsSuffix = baseSuffix
                        )
                    }

                    AnimalProducer(
                        name = animalName,
                        type = items.first().type, // тип одинаковый внутри группы
                        count = totalCount,
                        suffix = baseSuffix,
                        percentDouble = 0.0, // временно, заполним позже
                        percentFloat = 0f,
                    )
                }
            val totalAllAnimals = groupedAnimalProducerList.sumOf { it.count }
            val resultAnimalProducerList = groupedAnimalProducerList.map { animal ->
                val percent =
                    if (totalAllAnimals == 0.0) 0.0
                    else (animal.count / totalAllAnimals)
                animal.copy(
                    percentDouble = percent * 100.0,
                    percentFloat = percent.toFloat()
                )
            }

            val totalOwnNeed =
                writeOffRepository.getAnalysisOwnNeedsScrapRangeList(
                    itemId,
                    titleProduct,
                    true,
                    start,
                    end
                ).first()

            val totalScrap =
                writeOffRepository.getAnalysisOwnNeedsScrapRangeList(
                    itemId,
                    titleProduct,
                    false,
                    start,
                    end
                ).first()

            val saleProduct =
                saleRepository.getAnalysisSaleRangeList(
                    itemId,
                    titleProduct,
                    start,
                    end
                ).first()

            val financeSale = financeAnalysis(saleProduct, suffixPrice, FinanceAnalysisEnum.SALE)
            val financeOwnNeed =
                financeAnalysis(totalOwnNeed, suffixPrice, FinanceAnalysisEnum.OWN_NEED)
            val financeScrap = financeAnalysis(totalScrap, suffixPrice, FinanceAnalysisEnum.SCRAP)

            val totalStock =
                totalCountProduct - financeSale.totalCount - financeOwnNeed.totalCount - financeScrap.totalCount
            val realizedPrice = financeSale.totalPrice + financeOwnNeed.totalPrice

            val averagePurchasePrice = financeSale.averagePrice.takeIf { it > 0 }
                ?: financeOwnNeed.averagePrice.takeIf { it > 0 }
                ?: financeScrap.averagePrice.takeIf { it > 0 }
                ?: 0.0

            val totalPriceStock = totalStock * averagePurchasePrice

            val financeStock = FinanceAnalysis(
                totalCount = totalStock,
                suffixCount = baseSuffix,
                totalPrice = totalPriceStock,
                suffixPrice = suffixPrice,
                averagePrice = averagePurchasePrice,
                financeAnalysis = FinanceAnalysisEnum.STOCK,
                percentDouble = 0.0,
                percentFloat = 0f
            )

            val financeAnalysisList =
                listOf(financeSale, financeOwnNeed, financeScrap, financeStock)

            val totalAllAnimals2 = financeAnalysisList.sumOf { it.totalCount }
            val resultAnimalProducer2 = financeAnalysisList.map { animal ->
                val percent =
                    if (totalAllAnimals2 == 0.0) 0.0
                    else (animal.totalCount / totalAllAnimals2)

                animal.copy(
                    percentDouble = percent * 100.0,
                    percentFloat = percent.toFloat()
                )
            }

            val totalPrice = financeAnalysisList.sumOf { it.totalPrice }

            updateState {
                it.copy(
                    isLoading = false,
                    titleProduct = titleProduct,
                    baseSuffix = baseSuffix,
                    buyers = topBuyersList,
                    totalPrice = totalPrice,
                    countProduct = totalCountProduct,
                    animalProducer = resultAnimalProducerList,
                    financeAnalysis = resultAnimalProducer2,
                    stock = totalStock,
                    averagePrice = resultAnimalProducer2[0].averagePrice,
                    realizedPrice = realizedPrice,
                    potentialBalance = totalPriceStock,
                    soldLost = financeScrap.totalPrice
                )
            }
        }
    }

    private fun financeAnalysis(
        list: List<DomainCountSuffixPrice>,
        suffixPrice: Suffix,
        financeAnalysisEnum: FinanceAnalysisEnum
    ): FinanceAnalysis {
        val totalProduct2 =
            list.sumOf {
                it.count.conversation2(it.suffix, baseSuffix, baseSuffix)
            }
        val totalSum = list.sumOf { it.price }
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
        val dateEnd = currentPeriod.first?.let { dateLongToStringSQLPair(it) }
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
}
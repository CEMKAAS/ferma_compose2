@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.finance.analysis

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.finance.DomainTransaction
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarNewFilter
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.DomainChartPoint
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toColorIconBorderSecond
import com.zaroslikov.fermacompose2.supportFun.toColorIconSecond
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.toTransactionDrawRes
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.ButtonForGroupButtons
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.CategoryBorderCard
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.DateRangePickerModal
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconCircle
import com.zaroslikov.fermacompose2.ui.elements.IconCircleText
import com.zaroslikov.fermacompose2.ui.elements.IconFinance
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.WhiteTenCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.text_36
import com.zaroslikov.fermacompose2.ui.elements.сompositions.CircleShape
import com.zaroslikov.fermacompose2.ui.elements.сompositions.CustomLineChart
import com.zaroslikov.fermacompose2.ui.elements.сompositions.FilterDateElement
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import com.zaroslikov.fermacompose2.ui.elements.сompositions.Slider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.SliderGradient
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.monthToResString2
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.white


object FinanceAnalysisDestination : NavigationDestination {
    override val route = "financeAnalysis"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemProduct"
    const val itemIdArgTree = "itemSuffix"
    val routeWithArgs =
        "$route/{$itemIdArg}/{$itemIdArgTwo}/{$itemIdArgTree}"
}

@Composable
fun FinanceAnalysisProduct(
    navigateBack: () -> Unit,
    viewModel: FinanceAnalysisViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNewFilter(
                title = stringResource(R.string.analysis_screen_title_s, state.titleProduct),
                navigateBack = navigateBack,
                scrollBehavior = scrollBehavior,
                content = {
                    FilterDateElement(
                        value = state.dateFilter.filterDate,
                        currentPeriod = state.dateFilter.currentPeriod,
                        onValueChange = {
                            viewModel.onIntent(FinanceAnalysisIntent.FilterDateClicked(it))
                        }
                    )
                }
            )
        }) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            FinanceAnalysisContainer(
                modifier = Modifier
                    .modifierScreen(innerPadding),
                state = state,
                onCharSelectionClick = {
                    viewModel.onIntent(FinanceAnalysisIntent.CharSelectionClicked(it))
                }
            )
        if (state.dateFilter.isOpenCalendarDialog)
            DateRangePickerModal(
                dateBegin = state.dateFilter.dateBegin.second,
                dateEnd = state.dateFilter.dateEnd.second,
                onDateRangeSelected = {
                    viewModel.onIntent(FinanceAnalysisIntent.CurrentPeriodClicked(it))
                },
                onDismiss = {
                    viewModel.onIntent(FinanceAnalysisIntent.OpenCalendarDialogClicked(false))
                }
            )
    }
}

@Composable
fun FinanceAnalysisContainer(
    modifier: Modifier,
    state: FinanceAnalysisState,
    onCharSelectionClick: (FinanceAnalysisEnum) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        AnalysisMainCard(
            currentBalance = state.countProduct,
            stock = state.stock,
            suffix = state.baseSuffix,
            averagePrice = state.averagePrice,
            priceSuffix = Suffix.RUBLE
        )
        FinanceAnalysisCard(
            financeCategory2 = state.financeAnalysis,
            totalPrice = state.totalPrice,
            suffixPrice = Suffix.RUBLE,
            realizedPrice = state.realizedPrice,
            potentialBalance = state.potentialBalance,
            soldLost = state.soldLost
        )
        ProductDistribution(state.financeAnalysis)
        AnimalProducers(state.animalProducer)
        TopBuyers(state.buyers)
        Chart(
            state.charFilter,
            charSelection = state.charSelection,
            onClick = onCharSelectionClick,
            suffix = state.baseSuffix
        )
        TransactionSection(R.string.analysis_screen_history_transaction, state.transactionList)
    }
}

@Composable
private fun AnalysisMainCard(
    currentBalance: Double,
    stock: Double,
    suffix: Suffix,
    averagePrice: Double,
    priceSuffix: Suffix
) {
    BigColorCard(
        glowColor = price_green,
        colors = listOf(price_green, green_2),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconFinance(
                icon = R.drawable.icon_add_product,
                color = Color(0x20FFFFFF)
            )
            Text(
                text = stringResource(R.string.analysis_screen_total_produced),
                style = text_14,
                color = Color(0x80FFFFFF)
            )
        }
        Text(
            text = "${currentBalance.formatNumber()} ${stringResource(suffix.toResId())}",
            style = text_36,
            color = white
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WhiteTenCard(
                modifier = Modifier.weight(1f),
                titleRes = R.string.analysis_screen_in_stock,
                value = stock,
                suffix = suffix
            )
            WhiteTenCard(
                modifier = Modifier.weight(1f),
                titleRes = R.string.analysis_screen_average_price,
                value = averagePrice,
                suffix = priceSuffix
            )
        }
    }
}


@Composable
private fun FinanceAnalysisCard(
    totalPrice: Double,
    realizedPrice: Double,
    potentialBalance: Double,
    soldLost: Double,
    suffixPrice: Suffix,
    financeCategory2: List<FinanceAnalysis>
) {
    CardNewWithTitle(
        titleRes = R.string.analysis_screen_finance_analysis
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.analysis_screen_total_price_product),
                style = text_14,
                color = marengo
            )
            Text(
                "${totalPrice.formatNumber()} ${stringResource(suffixPrice.toResId())}",
                style = text_16,
                color = black_2
            )
        }
        HorizontalDivider(thickness = 1.dp, color = gray_6)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            financeCategory2.forEach {
                FinanceAnalysisRowCard(
                    totalCount = it.totalCount,
                    suffixCount = it.suffixCount,
                    totalPrice = it.totalPrice,
                    suffixPrice = it.suffixPrice,
                    averagePrice = it.averagePrice,
                    percent = it.percentDouble,
                    financeAnalysis = it.financeAnalysis
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = gray_6)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val financeRow = listOf(
                Triple(
                    R.string.analysis_screen_realized_product, realizedPrice,
                    FinanceAnalysisEnum.SALE.textColor
                ),
                Triple(
                    R.string.analysis_screen_potential_balance, potentialBalance,
                    FinanceAnalysisEnum.STOCK.textColor
                ),
                Triple(
                    R.string.analysis_screen_sold_lost_profits, soldLost,
                    FinanceAnalysisEnum.SCRAP.textColor
                )
            )
            financeRow.forEach {
                FinanceAnalysisRow(
                    title = it.first,
                    count = it.second,
                    suffixPrice = suffixPrice,
                    textColor = it.third,
                )
            }
        }
    }
}

@Composable
private fun FinanceAnalysisRowCard(
    totalCount: Double,
    suffixCount: Suffix,
    totalPrice: Double,
    suffixPrice: Suffix,
    averagePrice: Double,
    percent: Double,
    financeAnalysis: FinanceAnalysisEnum
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = financeAnalysis.colorBackground
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconCircle(
                    icon = financeAnalysis.icon,
                    colorBackground = financeAnalysis.iconBackground
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(stringResource(financeAnalysis.title), style = text_14, color = black_2)
                    Text(
                        totalCount.formatNumber() +
                                " ${stringResource(suffixCount.toResId())}" + " • " +
                                "${percent.formatNumber()}%",
                        style = text_12,
                        color = gray_7
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${financeAnalysis.sign}${totalPrice.formatNumber()} " +
                            stringResource(suffixPrice.toResId()),
                    style = text_16,
                    color = financeAnalysis.textColor
                )
                Text(
                    text = "${averagePrice.formatNumber()} " +
                            stringResource(suffixPrice.toResId()) + "/" + stringResource(suffixCount.toResId()),
                    style = text_12,
                    color = gray_7
                )
            }
        }
    }
}

@Composable
private fun FinanceAnalysisRow(
    @StringRes title: Int,
    count: Double,
    suffixPrice: Suffix,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(title), style = text_14, color = marengo)
        Text(
            text = "${count.formatNumber()} " +
                    stringResource(suffixPrice.toResId()),
            style = text_16,
            color = textColor
        )
    }
}

@Composable
private fun ProductDistribution(
    financeAnalysis: List<FinanceAnalysis>
) {
    CardNewWithTitle(
        titleRes = R.string.analysis_screen_product_distribution
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            financeAnalysis.forEach {
                FinanceCategorySlider(
                    count = it.totalCount,
                    suffix = it.suffixCount,
                    percentDouble = it.percentDouble,
                    percentFloat = it.percentFloat,
                    financeAnalysisEnum = it.financeAnalysis,
                )
            }
        }
    }
}

@Composable
private fun FinanceCategorySlider(
    count: Double,
    suffix: Suffix,
    percentDouble: Double,
    percentFloat: Float,
    financeAnalysisEnum: FinanceAnalysisEnum
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircleShape(financeAnalysisEnum.iconBackground)
                Text(stringResource(financeAnalysisEnum.title), style = text_14, color = dark)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${count.formatNumber()} ${stringResource(suffix.toResId())}",
                    style = text_14, color = black_2
                )
                TextMiniCard(
                    "${percentDouble.formatNumber()}%",
                    textColor = financeAnalysisEnum.colorTextPercent,
                    backgroundColor = financeAnalysisEnum.colorBackground
                )
            }
        }
        Slider(
            percentFloat = percentFloat,
            color = financeAnalysisEnum.iconBackground
        )
    }
}

@Composable
private fun AnimalProducers(
    animalProducer: List<AnimalProducer>
) {
    CardNewWithTitle(
        titleRes = R.string.analysis_screen_animal_producers
    ) {
        animalProducer.forEach {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimalProducerSlider(
                    name = it.name,
                    type = it.type,
                    count = it.count,
                    suffix = it.suffix,
                    percentDouble = it.percentDouble,
                    percentFloat = it.percentFloat
                )
            }

        }
    }
}

@Composable
private fun AnimalProducerSlider(
    name: String,
    type: String,
    count: Double,
    suffix: Suffix,
    percentDouble: Double,
    percentFloat: Float,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                IconAnimal(sex = true, size = 40.dp)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(name, style = text_14, color = black_2)
                    Text(type, style = text_12, color = gray_7)
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text("$count ${stringResource(suffix.toResId())}", style = text_14, color = black_2)
                TextMiniCard(
                    "${percentDouble.formatNumber()}%",
                    textColor = FinanceAnalysisEnum.SALE.colorTextPercent,
                    backgroundColor = FinanceAnalysisEnum.SALE.colorBackground
                )
            }
        }
        SliderGradient(
            percentFloat = percentFloat,
            colors = listOf(green_6, green_shamrock)
        )
    }
}

@Composable
private fun TopBuyers(
    buyersList: List<Buyer>
) {
    CardNewWithTitle(
        titleRes = R.string.analysis_screen_byers
    ) {
        buyersList.forEachIndexed { index, buyer ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BuyerCard(
                    number = index + 1,
                    buyer = buyer.buyer,
                    count = buyer.count,
                    suffix = buyer.suffix,
                    price = buyer.price,
                    priceSuffix = buyer.priceSuffix,
                    countTransaction = buyer.countTransaction
                )
            }
        }
    }
}

@Composable
private fun BuyerCard(
    number: Int,
    buyer: String,
    count: Double,
    suffix: Suffix,
    price: Double,
    priceSuffix: Suffix,
    countTransaction: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = ghostly_white
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconCircleText(
                    number = number.toString(),
                    colorBackground = violet_5,
                    colorText = violet_6,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(buyer, style = text_12, color = black_2)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CountColorCard(count, suffix, suffix.toColorList())
                        Text(
                            "$countTransaction ${stringResource(R.string.analysis_screen_buy)}",
                            style = text_12,
                            color = gray_7
                        )
                    }
                }
            }
            Text(
                text = "${price.formatNumber()} " +
                        stringResource(priceSuffix.toResId()),
                style = text_14,
                color = price_green
            )
        }
    }
}

@Composable
private fun Chart(
    chartFilter: List<Pair<List<DomainChartPoint>, FinanceAnalysisEnum>>,
    charSelection: FinanceAnalysisEnum,
    suffix: Suffix,
    onClick: (FinanceAnalysisEnum) -> Unit
) {
    val valuesList = chartFilter
        .find { it.second == charSelection }?.first
        ?.map { it.value } ?: emptyList()
    val labelsList = chartFilter
        .find { it.second == charSelection }?.first
        ?.map { it.date.toString() } ?: emptyList()

    CardNewWithTitle(
        titleRes = charSelection.titleChar
    ) {
        CustomLineChart(
            values = valuesList,
            labels = labelsList,
            lineColor = charSelection.iconBackground,
            pointColor = charSelection.iconBackground,
            fillColor = charSelection.iconBackground.copy(alpha = 0.25f),
            suffix = stringResource(suffix.toResId()),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(bottom = 20.dp)
        )
        GroupButton {
            FinanceAnalysisEnum.entries.forEach {
                val triple = if (it == charSelection) Triple(it.iconBackground, white, 1.dp)
                else Triple(Color.Transparent, marengo, 0.dp)

                ButtonForGroupButtons(
                    text = it.titleButton,
                    backgroundColor = triple.first,
                    textColor = triple.second,
                    shadowElevation = triple.third
                ) { onClick(it) }
            }
        }
    }
}

@Composable
private fun TransactionSection(
    @StringRes title: Int,
    transactionList: List<DomainTransaction>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(title),
                style = text_16,
                color = black_2
            )
            TextMiniCard(
                "${transactionList.size} " +
                        stringResource(R.string.analysis_screen_entries),
                textColor = green_9,
                backgroundColor = price_green_2
            )
        }
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            transactionList.forEach {
                TransitionCard(
                    count = it.value,
                    suffix = it.suffix,
                    category = it.category,
                    price = it.price,
                    date = it.data,
                    priceAll = it.priceAll,
                    priceSuffix = Suffix.RUBLE,
                    buyer = it.buyer,
                    animal = it.animal,
                    categoryFinance = it.categoryFinance
                )
            }
        }
    }
}

@Composable
private fun TransitionCard(
    count: Double,
    suffix: Suffix,
    price: Double?,
    priceAll: Double? = null,
    priceSuffix: Suffix = Suffix.PIECES,
    category: String? = null,
    animal: String? = null,
    date: String = dateToday(),
    buyer: String? = null,
    categoryFinance: FinanceCategory
) {
    val priceFinal = priceAll ?: price
    val categoryNoNull = category ?: stringResource(R.string.support_text_no_category)
    val (colorPrice, positive) = when (categoryFinance) {
        FinanceCategory.SALE -> price_green to "+"
        FinanceCategory.OWN_NEED -> orang_2 to ""
        else -> error_base to "-"
    }
    val dateList = date.split("-")
    CardFieldNew(
        padding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTransaction2(
                icon = categoryFinance.toTransactionDrawRes(),
                color = categoryFinance.toColorIconBorderSecond(),
                colorIcon = categoryFinance.toColorIconSecond()
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryBorderCard(categoryNoNull)
                        CountColorCard(count, suffix, suffix.toColorList())
                    }
                    priceFinal?.let {
                        Text(
                            text = "$positive${it.formatNumber()} " +
                                    stringResource(priceSuffix.toResId()),
                            style = text_18,
                            color = colorPrice
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    animal?.let {
                        IconAndTextNew(
                            iconRes = R.drawable.baseline_pets_24,
                            valueString = it,
                            iconColor = grey
                        )
                    }
                    price?.let {
                        IconAndTextNew(
                            iconRes = R.drawable.baseline_currency_ruble_24,
                            valueString = "${it.formatNumber()} " +
                                    "${stringResource(priceSuffix.toResId())}/" +
                                    stringResource(suffix.toResId()),
                            iconColor = grey
                        )
                    }
                    buyer?.let {
                        IconAndTextNew(
                            iconRes = R.drawable.baseline_person_24,
                            valueString = it,
                            iconColor = grey
                        )
                    }
                    IconAndTextNew(
                        iconRes = R.drawable.baseline_calendar_month_24,
                        valueString = "${dateList[2]} ${stringResource(monthToResString2(dateList[1].toInt()))} ${dateList[0]}",
                        iconColor = grey
                    )
                }
            }
        }
    }
}
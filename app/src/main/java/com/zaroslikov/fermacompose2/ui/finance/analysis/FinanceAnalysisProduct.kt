package com.zaroslikov.fermacompose2.ui.finance.analysis

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarCalendar
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.DateRangePickerModal
import com.zaroslikov.fermacompose2.ui.elements.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.finance.month.FinanceMonthIntent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import io.appmetrica.analytics.AppMetrica


object FinanceAnalysisDestination : NavigationDestination {
    override val route = "financeAnalysis"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemProduct"
    val routeWithArgs =
        "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun FinanceAnalysisProduct(
    navigateBack: () -> Unit,
    viewModel: FinanceAnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBarCalendar(
            title = state.currentPeriod,
            true,
            navigateUp = navigateBack,
            settingUp = {
                viewModel.onIntent(FinanceAnalysisIntent.OpenCalendarDialogClicked(true))
                AppMetrica.reportEvent("Диапазон Анализ")
            }
        )
    }) { innerPadding ->
        FinanceAnalysisContainer(
            modifier = Modifier
                .modifierScreen(innerPadding),
            text = state.currentPeriod,
            analysisAddAllTime = state.productAdd,
            analysisSaleAllTime = state.productSale,
            analysisWriteOffAllTime = state.productWriteOff,
            analysisWriteOffOwnNeedsAllTime = state.productOnwNeeds,
            analysisWriteOffScrapAllTime = state.productScrap,
            analysisSaleSoldAllTime = state.productSaleSold,
            analysisWriteOffOwnNeedsMoneyAllTime = state.productWriteOffOnwNeedMoney,
            analysisWriteOffScrapMoneyAllTime = state.productWriteOffScrapMoney,
            analysisAddAverageValueAllTime = state.productAverage,
            analysisSaleBuyerAllTimeState = state.saleBuyerList,
//            analysisAddAnimalAllTimeState = state.analysisAddAnimalAllTimeState.collectAsState().value.itemList,
//            analysisCostPriceAllTimeState = viewModel.analysisCostPriceTimeState.collectAsState().value.itemList,
        )
        if (state.isOpenCalendarDialog)
            DateRangePickerModal(
                dateBegin = state.dateBegin.second,
                dateEnd = state.dateEnd.second,
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
    text: String,
    analysisAddAllTime: DomainCountSuffix,
    analysisSaleAllTime: DomainTitleSuffixPrice,
    analysisWriteOffAllTime: DomainCountSuffix,
    analysisWriteOffOwnNeedsAllTime: DomainCountSuffix,
    analysisWriteOffScrapAllTime: DomainCountSuffix,
    analysisSaleSoldAllTime: Double,
    analysisWriteOffOwnNeedsMoneyAllTime: Double,
    analysisWriteOffScrapMoneyAllTime: Double,
    analysisAddAverageValueAllTime: DomainCountSuffix,
    analysisSaleBuyerAllTimeState: List<DomainBuyerPrice>,
//    analysisAddAnimalAllTimeState: List<AnimalTitSuff>,
//    analysisCostPriceAllTimeState: List<Fin>
) {
    val noTitleString = stringResource(R.string.analysis_screen_no_title)
    Column(modifier = modifier) {
        AnalysisCardOne(
            text = text,
            analysisAddAllTime = analysisAddAllTime,
            analysisSaleAllTime = analysisSaleAllTime,
            analysisWriteOffAllTime = analysisWriteOffAllTime,
            analysisWriteOffOwnNeedsAllTime = analysisWriteOffOwnNeedsAllTime,
            analysisWriteOffScrapAllTime = analysisWriteOffScrapAllTime,
            analysisSaleSoldAllTime = analysisSaleSoldAllTime,
        )
        AnalysisCardTwo(
            text = text,
            analysisSaleSoldAllTime = analysisSaleSoldAllTime,
            analysisWriteOffOwnNeedsMoneyAllTime = analysisWriteOffOwnNeedsMoneyAllTime,
            analysisWriteOffScrapMoneyAllTime = analysisWriteOffScrapMoneyAllTime,
        )
        /*PullOutCard(
            intRes = R.string.analysis_screen_byers,
            list = analysisSaleBuyerAllTimeState,
            intTitleText = R.string.alert_dialog_info_title_byers_analysis,
            intText = R.string.alert_dialog_info_text_byers_analysis
        ) { analysisSaleBuyerAllTimeState ->
            Pair(
                if (analysisSaleBuyerAllTimeState.buyer == "") noTitleString else analysisSaleBuyerAllTimeState.buyer,
                stringResource(
                    R.string.card_count_briefly_s,
                    "${analysisSaleBuyerAllTimeState.count.formatNumber()} ${analysisSaleBuyerAllTimeState.suffix}",
                    analysisSaleBuyerAllTimeState.price.formatNumber(),
                    stringResource(R.string.currency_ruble)
                )
            )
        }*/
        /*PullOutCard(
           intRes = R.string.analysis_screen_animals,
           list = analysisAddAnimalAllTimeState,
           intTitleText = R.string.alert_dialog_info_title_animals_analysis,
           intText = R.string.alert_dialog_info_text_animals_analysis
       ) {
           Pair(
               if (it.title == "") noTitleString else it.title,
               "${it.priceAll.formatNumber()} ${it.suffix}"
           )
       }*/
        /*PullOutCard(
            intRes = R.string.analysis_screen_cost_price,
            list = analysisCostPriceAllTimeState,
            intTitleText = R.string.alert_dialog_info_title_cost_price,
            intText = R.string.alert_dialog_info_text_cost_price
        ) { analysisCostPriceAllTimeState ->
            Pair(
                if (analysisCostPriceAllTimeState.title == "" || analysisCostPriceAllTimeState.title == null) noTitleString else
                    stringResource(
                        R.string.analysis_screen_cost_price_s,
                        analysisCostPriceAllTimeState.title
                    ),
                stringResource(
                    R.string.card_ruble_s,
                    analysisCostPriceAllTimeState.priceAll.formatNumber()
                )
            )
        }*/
    }
}

@Composable
fun AnalysisCardOne(
    text: String,
    analysisAddAllTime: DomainCountSuffix,
    analysisSaleAllTime: DomainTitleSuffixPrice,
    analysisWriteOffAllTime: DomainCountSuffix,
    analysisWriteOffOwnNeedsAllTime: DomainCountSuffix,
    analysisWriteOffScrapAllTime: DomainCountSuffix,
    analysisSaleSoldAllTime: Double,
) {
    CardField {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.analysis_screen_product_information, text),
                style = textBold_18
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_product_add),
                value = "${analysisAddAllTime.count.formatNumber()} ${analysisAddAllTime.suffix}"
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_product_sale),
                value = "${analysisSaleAllTime.price.formatNumber()} ${analysisSaleAllTime.suffix}"
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_product_write_off),
                value = "${analysisWriteOffAllTime.count.formatNumber()} ${analysisWriteOffAllTime.suffix}"
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_product_own_needs),
                value = "${analysisWriteOffOwnNeedsAllTime.count.formatNumber()} ${analysisWriteOffOwnNeedsAllTime.suffix}",
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_product_until),
                value = "${analysisWriteOffScrapAllTime.count.formatNumber()} ${analysisWriteOffScrapAllTime.suffix}",
            )
            if (text == stringResource(R.string.support_text_all_time))
                TextAndIconRow(
                    title = stringResource(R.string.analysis_screen_product_warehouse),
                    value = "${(analysisAddAllTime.count - analysisSaleAllTime.price - analysisWriteOffAllTime.count).formatNumber()} ${analysisAddAllTime.suffix}"
                )
            /*
            Text(
                text = "В среднем за текущий год: ${formatter(analysisAddAverageValueAllTime.priceAll)} ${analysisAddAverageValueAllTime.title} за день",
                modifier = modifierText
            )
           */
        }
    }
}

@Composable
fun AnalysisCardTwo(
    text: String,
    analysisSaleSoldAllTime: Double,
    analysisWriteOffOwnNeedsMoneyAllTime: Double,
    analysisWriteOffScrapMoneyAllTime: Double,
) {
    var openAlertDialogSaved by remember { mutableStateOf(false) }
    var openAlertDialogLost by remember { mutableStateOf(false) }

    if (openAlertDialogSaved) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialogSaved = !openAlertDialogSaved },
            intTitleText = R.string.alert_dialog_info_title_saved,
            intText = R.string.alert_dialog_info_text_saved,
        )
    }
    if (openAlertDialogLost) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialogLost = !openAlertDialogLost },
            intTitleText = R.string.alert_dialog_info_title_lost,
            intText = R.string.alert_dialog_info_text_lost,
        )
    }
    CardField {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.analysis_screen_finance_information, text),
                style = textBold_18
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_income),
                value = stringResource(
                    R.string.card_ruble_s,
                    analysisSaleSoldAllTime.formatNumber()
                )
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_saved),
                value = stringResource(
                    R.string.card_ruble_s,
                    analysisWriteOffOwnNeedsMoneyAllTime.formatNumber()
                ),
                isShowIcon = true,
                onClick = { openAlertDialogSaved = !openAlertDialogSaved }
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_lost),
                value = stringResource(
                    R.string.card_ruble_s,
                    analysisWriteOffScrapMoneyAllTime.formatNumber()
                ),
                isShowIcon = true,
                onClick = { openAlertDialogLost = !openAlertDialogLost }
            )
            TextAndIconRow(
                title = stringResource(R.string.analysis_screen_total),
                value = stringResource(
                    R.string.card_ruble_s,
                    (analysisSaleSoldAllTime + analysisWriteOffOwnNeedsMoneyAllTime - analysisWriteOffScrapMoneyAllTime).formatNumber()
                )
            )
        }
    }
}


@Composable
fun <T> PullOutCard(
    modifier: Modifier = Modifier,
    @StringRes intRes: Int,
    @StringRes intTitleText: Int,
    @StringRes intText: Int,
    list: List<T>,
    itemToString: @Composable (T) -> Pair<String?, String>
) {
    var expanded by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    var openAlertDialog by remember { mutableStateOf(false) }
    if (openAlertDialog) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialog = false },
            intTitleText = intTitleText,
            intText = intText
        )
    }

    CardField(
        modifier = modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextAndIconRow(
                intRes = intRes,
                titleStyle = textBold_18,
                isShowIcon = true,
                onClick = { openAlertDialog = !openAlertDialog },
                isShowValue = false,
                value = ""
            )
            if (list.isNotEmpty()) {
                for (i in list.indices) {
                    TextAndIconRow(
                        title = "${i + 1}. ${itemToString(list[i]).first}",
                        value = itemToString(list[i]).second
                    )
                    if (i == 2 && !expanded)
                        break
                }
            } else Text(text = stringResource(R.string.analysis_screen_no))
        }
        if (list.size > 3)
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painterResource(if (expanded ) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down ),
                    contentDescription = "Показать меню"
                )
            }
    }
}





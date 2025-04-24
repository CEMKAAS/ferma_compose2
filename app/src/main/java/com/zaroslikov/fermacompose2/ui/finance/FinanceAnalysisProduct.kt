package com.zaroslikov.fermacompose2.ui.finance

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarCalendar
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.DateRangePickerModal
import com.zaroslikov.fermacompose2.ui.composeElement.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_18
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
    viewModel: FinanceAnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var openCalendarDialog by remember { mutableStateOf(false) }
    val nowMonth = stringResource(R.string.support_text_all_time)
    var text by remember { mutableStateOf(nowMonth) }


    if (openCalendarDialog) {
        DateRangePickerModal(
            onDateRangeSelected = {
                Pair(it.first?.let { it1 -> viewModel.updateDateBegin(it1) },
                    it.second?.let { it1 -> viewModel.updateDateEnd(it1) })
            },
            onDismiss = { openCalendarDialog = false },
            dateBegin = viewModel.dateBegin.second,
            dateEnd = viewModel.dateEnd.second,
            upAnalysis = {
                viewModel.upAnalisis()
                text =
                    "${dateLongToString(viewModel.dateBegin.second)} - ${dateLongToString(viewModel.dateEnd.second)}"

            }
        )
    }

    Scaffold(topBar = {
        TopAppBarCalendar(
            title = viewModel.name,
            true,
            navigateUp = navigateBack,
            settingUp = {
                openCalendarDialog = true
                AppMetrica.reportEvent("Диапазон Анализ")
            }
        )
    }) { innerPadding ->
        FinanceAnalysisContainer(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            text = text,
            analysisAddAllTime = viewModel.analysisAddAllTime,
            analysisSaleAllTime = viewModel.analysisSaleAllTime,
            analysisWriteOffAllTime = viewModel.analysisWriteOffAllTime,
            analysisWriteOffOwnNeedsAllTime = viewModel.analysisWriteOffOwnNeedsAllTime,
            analysisWriteOffScrapAllTime = viewModel.analysisWriteOffScrapAllTime,
            analysisSaleSoldAllTime = viewModel.analysisSaleSoldAllTime,
            analysisWriteOffOwnNeedsMoneyAllTime = viewModel.analysisWriteOffOwnNeedsMoneyAllTime,
            analysisWriteOffScrapMoneyAllTime = viewModel.analysisWriteOffScrapMoneyAllTime,
            analysisAddAverageValueAllTime = viewModel.analysisAddAverageValueAllTime,
            analysisAddAnimalAllTimeState = viewModel.analysisAddAnimalAllTimeState.collectAsState().value.itemList,
            analysisSaleBuyerAllTimeState = viewModel.analysisSaleBuyerAllTimeState.collectAsState().value.itemList,
            analysisCostPriceAllTimeState = viewModel.analysisCostPriceTimeState.collectAsState().value.itemList,
        )
    }
}

@Composable
fun FinanceAnalysisContainer(
    modifier: Modifier,
    text: String,
    analysisAddAllTime: FinUiState,
    analysisSaleAllTime: FinUiState,
    analysisWriteOffAllTime: FinUiState,
    analysisWriteOffOwnNeedsAllTime: FinUiState,
    analysisWriteOffScrapAllTime: FinUiState,
    analysisSaleSoldAllTime: Double,
    analysisWriteOffOwnNeedsMoneyAllTime: Double,
    analysisWriteOffScrapMoneyAllTime: Double,
    analysisAddAverageValueAllTime: FinUiState,
    analysisAddAnimalAllTimeState: List<AnimalTitSuff>,
    analysisSaleBuyerAllTimeState: List<AnalysisSaleBuyerAllTime>,
    analysisCostPriceAllTimeState: List<Fin>
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

        PullOutCard(
            intRes = R.string.analysis_screen_animals,
            list = analysisAddAnimalAllTimeState,
            intTitleText = R.string.alert_dialog_info_title_animals_analysis,
            intText = R.string.alert_dialog_info_text_animals_analysis
        ) {
            Pair(
                if (it.title == "") noTitleString else it.title,
                "${it.priceAll.formatNumber()} ${it.suffix}"
            )
        }

        PullOutCard(
            intRes = R.string.analysis_screen_byers,
            list = analysisSaleBuyerAllTimeState,
            intTitleText = R.string.alert_dialog_info_title_byers_analysis,
            intText = R.string.alert_dialog_info_text_byers_analysis
        ) { analysisSaleBuyerAllTimeState ->
            Pair(
                if (analysisSaleBuyerAllTimeState.buyer == "") noTitleString else analysisSaleBuyerAllTimeState.buyer,
                stringResource(
                    R.string.card_count_briefly_s,
                    "${analysisSaleBuyerAllTimeState.resultCount.formatNumber()} ${analysisSaleBuyerAllTimeState.suffix}",
                    analysisSaleBuyerAllTimeState.resultPrice.formatNumber(),
                    stringResource(R.string.currency_ruble)
                )
            )
        }

        PullOutCard(
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
        }
    }
}

@Composable
fun AnalysisCardOne(
    text: String,
    analysisAddAllTime: FinUiState,
    analysisSaleAllTime: FinUiState,
    analysisWriteOffAllTime: FinUiState,
    analysisWriteOffOwnNeedsAllTime: FinUiState,
    analysisWriteOffScrapAllTime: FinUiState,
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
                title = stringResource(
                    R.string.analysis_screen_product_add,
                ),
                value = "${analysisAddAllTime.priceAll.formatNumber()} ${analysisAddAllTime.title}"
            )
            TextAndIconRow(
                title = stringResource(
                    R.string.analysis_screen_product_sale,
                ),
                value = "${analysisSaleAllTime.priceAll.formatNumber()} ${analysisSaleAllTime.title}"
            )
            TextAndIconRow(
                title = stringResource(
                    R.string.analysis_screen_product_write_off,
                ),
                value = "${analysisWriteOffAllTime.priceAll.formatNumber()} ${analysisWriteOffAllTime.title}"
            )
            TextAndIconRow(
                title = stringResource(
                    R.string.analysis_screen_product_own_needs,
                ),
                value = "${analysisWriteOffOwnNeedsAllTime.priceAll.formatNumber()} ${analysisWriteOffOwnNeedsAllTime.title}",
            )
            TextAndIconRow(
                title = stringResource(
                    R.string.analysis_screen_product_until
                ),
                value = "${analysisWriteOffScrapAllTime.priceAll.formatNumber()} ${analysisWriteOffScrapAllTime.title}",
            )
            if (text == stringResource(R.string.support_text_all_time)) {
                TextAndIconRow(
                    title = stringResource(
                        R.string.analysis_screen_product_warehouse,
                    ),
                    value = "${(analysisAddAllTime.priceAll - analysisSaleAllTime.priceAll - analysisWriteOffAllTime.priceAll).formatNumber()} ${analysisAddAllTime.title}"
                )
            }
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
                title = stringResource(
                    R.string.analysis_screen_income
                ),
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
        if (list.size > 3) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Показать меню"
                )
            }
        }
    }
}





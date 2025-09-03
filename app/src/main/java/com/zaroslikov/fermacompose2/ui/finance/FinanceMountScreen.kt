/*
package com.zaroslikov.fermacompose2.ui.finance

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQL
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardFinance
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceOutlined
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceOutlinedRow
import com.zaroslikov.fermacompose2.ui.elements.DateRangePickerModal
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import io.appmetrica.analytics.AppMetrica


object FinanceMountDestination : NavigationDestination {
    override val route = "FinanceMount"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceMountScreen(
    navigateBack: () -> Unit,
    navigateToCategory: (FinanceCategoryDataNav) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceMountViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val incomeRow by viewModel.incomeCategoryUiState.collectAsState()
    val expensesRow by viewModel.expensesCategoryUiState.collectAsState()

    val nowMonth = stringResource(R.string.support_text_now_month)

    var text by remember { mutableStateOf(nowMonth) }
    var openCalendarDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBarBack(
                title = text,
                navigateUp = navigateBack,
                calendarClick = {
                    openCalendarDialog = true
                    AppMetrica.reportEvent("Финансы Месяц Диапазон")
                }
            )
        }
    ) { innerPadding ->
        FinanceMountBody(
            incomeMount = viewModel.incomeMountUiState,
            expensesMount = viewModel.expensesMountUiState,
            ownNeedsMonth = viewModel.ownNeedMonthUiState,
            scrapMonth = viewModel.scrapMonthUiState,
            incomeRow = incomeRow.itemList,
            expensesRow = expensesRow.itemList,
            navigateToCategory = {
                println(
                    FinanceCategoryDataNav(
                        it.first,
                        it.second,
                        it.third,
                        dateLongToStringSQL(viewModel.dateBegin.second),
                        dateLongToStringSQL(viewModel.dateEnd.second)
                    )
                )
                navigateToCategory(
                    FinanceCategoryDataNav(
                        it.first,
                        it.second,
                        it.third,
                        dateLongToStringSQL(viewModel.dateBegin.second),
                        dateLongToStringSQL(viewModel.dateEnd.second)
                    )
                )
            },
            text = text,
            idPT = viewModel.itemId,
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
        )
    }
}

@Composable
private fun FinanceMountBody(
    modifier: Modifier = Modifier,
    incomeMount: Double,
    expensesMount: Double,
    ownNeedsMonth: Double,
    scrapMonth: Double,
    incomeRow: List<Fin>,
    expensesRow: List<Fin>,
    navigateToCategory: (Triple<Int, String, Boolean>) -> Unit,
    text: String,
    idPT: Int,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {

        CardFinanceOutlinedRow(
            oneIntRes = R.string.card_income,
            twoIntRes = R.string.card_expenditure,
            oneValue = incomeMount,
            twoValue = expensesMount
        )
        CardFinanceOutlinedRow(
            oneIntRes = R.string.card_own_need,
            twoIntRes = R.string.card_scrap,
            oneValue = ownNeedsMonth,
            twoValue = scrapMonth
        )
        CardFinanceOutlinedRow(
            oneIntRes = R.string.card_all_income,
            twoIntRes = R.string.card_all_expenditure,
            oneValue = incomeMount + ownNeedsMonth,
            twoValue = expensesMount + scrapMonth
        )
        CardFinanceOutlined(
            titleRes = R.string.card_all,
            value = incomeMount + ownNeedsMonth - expensesMount - scrapMonth,
        )

        LazyRowMount(
            list = incomeRow,
            navigateToCategory = {
                navigateToCategory(
                    Triple(
                        it.first,
                        it.second,
                        true
                    )
                )
            },
            idPT = idPT,
            month = text,
            heading = R.string.support_text_income_category,
            headingNull = R.string.support_text_no_income_category
        )

        LazyRowMount(
            list = expensesRow,
            navigateToCategory = {
                navigateToCategory(
                    Triple(
                        it.first,
                        it.second,
                        false
                    )
                )
            },
            idPT = idPT,
            month = text,
            heading = R.string.support_text_expenses_category,
            headingNull = R.string.support_text_no_expenses_category,
        )
    }
}


@Composable
fun LazyRowMount(
    list: List<Fin>,
    month: String,
    navigateToCategory: (Pair<Int, String>) -> Unit,
    idPT: Int,
    @StringRes heading: Int,
    @StringRes headingNull: Int
) {
    val noTitleString = stringResource(R.string.analysis_screen_no_title)
    Text(
        text = stringResource(if (list.isNotEmpty()) heading else headingNull, month),
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        style = textBold_16
    )
    if (list.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = list) {
                CardFinance(
                    onClick = {
                        navigateToCategory(
                            Pair(
                                idPT,
                                if (it.title == "" || it.title == null) noTitleString else it.title
                            )
                        )
                    },
                    titleRes = it.title ?: "",
                    value = it.priceAll
                )
            }
        }
    }
}


*/

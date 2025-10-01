@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.finance.month

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardFinance
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceOutlined
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceOutlinedRow
import com.zaroslikov.fermacompose2.ui.elements.DateRangePickerModal
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import io.appmetrica.analytics.AppMetrica


object FinanceMonthDestination : NavigationDestination {
    override val route = "FinanceMount"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun FinanceMonthScreen(
    navigateBack: () -> Unit,
//    navigateToCategory: (FinanceCategoryDataNav) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceMonthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBarBack(
                title = state.currentPeriod,
                navigateUp = navigateBack,
                calendarClick = {
                    viewModel.onIntent(FinanceMonthIntent.OpenCalendarDialogClicked(true))
                    AppMetrica.reportEvent("Финансы Месяц Диапазон")
                }
            )
        }
    ) { innerPadding ->
        FinanceMountBody(
            modifier = Modifier
                .modifierScreenLazy(innerPadding),
            incomeMount = state.incomeMonth,
            expensesMount = state.expensesMonth,
            ownNeedsMonth = state.ownNeedMonth,
            scrapMonth = state.scrapMonth,
            incomeRow = state.incomeCategory,
            expensesRow = state.expensesCategory,
            currentPeriod = state.currentPeriod,
            navigateToCategory = {
//                navigateToCategory(
//                    FinanceCategoryDataNav(
//                        it.first,
//                        it.second,
//                        it.third,
//                        dateLongToStringSQL(viewModel.dateBegin.second),
//                        dateLongToStringSQL(viewModel.dateEnd.second)
//                    )
//                )
            },
        )
        if (state.isOpenCalendarDialog)
            DateRangePickerModal(
                dateBegin = state.dateBegin.second,
                dateEnd = state.dateEnd.second,
                onDateRangeSelected = {
                    viewModel.onIntent(FinanceMonthIntent.CurrentPeriodClicked(it))
                },
                onDismiss = { viewModel.onIntent(FinanceMonthIntent.OpenCalendarDialogClicked(false)) },
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
    incomeRow: List<DomainCategoryPrice>,
    expensesRow: List<DomainCategoryPrice>,
    navigateToCategory: (Pair<String, Boolean>) -> Unit,
    currentPeriod: String
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
            navigateToCategory = { navigateToCategory(it to true) },
            currentPeriod = currentPeriod,
            heading = R.string.support_text_income_category,
            headingNull = R.string.support_text_no_income_category
        )
        LazyRowMount(
            list = expensesRow,
            navigateToCategory = { navigateToCategory(it to false) },
            currentPeriod = currentPeriod,
            heading = R.string.support_text_expenses_category,
            headingNull = R.string.support_text_no_expenses_category,
        )
    }
}

@Composable
private fun LazyRowMount(
    list: List<DomainCategoryPrice>,
    currentPeriod: String,
    navigateToCategory: (String) -> Unit,
    @StringRes heading: Int,
    @StringRes headingNull: Int
) {
    val noTitleString = stringResource(R.string.analysis_screen_no_title)
    Text(
        text = stringResource(if (list.isNotEmpty()) heading else headingNull, currentPeriod),
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(vertical = 8.dp),
        style = textBold_16
    )
    if (list.isNotEmpty())
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = list) {
                CardFinance(
                    onClick = {
                        navigateToCategory(
                            if (it.category == "") noTitleString else it.category
                        )
                    },
                    titleRes = it.category,
                    value = it.price.toDouble()
                )
            }
        }
}
package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarNew
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_10
import com.zaroslikov.fermacompose2.blue_11
import com.zaroslikov.fermacompose2.blue_12
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_7
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_10
import com.zaroslikov.fermacompose2.red_7
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.IconFinance
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.text_36
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.monthToResString2
import com.zaroslikov.fermacompose2.ui.monthToResString3
import com.zaroslikov.fermacompose2.white
import java.time.LocalDate

object FinanceDestination : NavigationDestination {
    override val route = "finance"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    navigateToIncomeExpenses: (Pair<Long, FinanceCategory>) -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val idProject = state.idPT

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNew(
                title = stringResource(R.string.finance_screen_title),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            FinanceBody(
                modifier = Modifier
                    .modifierScreen(innerPadding),
                currentBalance = state.currentBalance,
                income = state.income,
                expenses = state.expenses,
                ownNeed = state.ownNeed,
                scrap = state.scrap,
                profit = state.profit,
                incomeMount = state.incomeMount,
                expensesMount = state.expensesMount,
                incomeExpensesList = state.domainIncomeExpenseList,
                suffix = state.currencySuffix,
                navigate = { navigateToIncomeExpenses(idProject to it) },
            )
    }
}

@Composable
private fun FinanceBody(
    modifier: Modifier = Modifier,
    currentBalance: Double,
    income: Double,
    expenses: Double,
    ownNeed: Double,
    scrap: Double,
    profit: Double,
    incomeMount: Double,
    expensesMount: Double,
    incomeExpensesList: List<DomainIncomeExpenses>,
    suffix: Suffix,
    navigate: (FinanceCategory) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CurrentBalance(
            currentBalance = currentBalance,
            expensesMount = expensesMount,
            incomeMount = incomeMount,
            suffix = suffix,
            onDetailClick = { navigate(FinanceCategory.PROFIT) }
        )
        IncomeExpensesCards(
            income = income,
            expenses = expenses,
            suffix = suffix
        ) { navigate(it) }
        WriteOffFinanceCards(
            scrap = scrap,
            ownNeed = ownNeed,
            suffix = suffix
        ) { navigate(it) }
        TransactionList(incomeExpensesList, suffix)
    }
}

@Composable
private fun CurrentBalance(
    currentBalance: Double,
    incomeMount: Double,
    expensesMount: Double,
    suffix: Suffix,
    onDetailClick: (() -> Unit)? = null
) {
    val positive = currentBalance >= 0
    val month = LocalDate.now().monthValue
    val currentBalanceMount = incomeMount - expensesMount
    val positiveMount = currentBalanceMount > 0

    BigColorCard(
        glowColor = blue_11,
        colors = listOf(blue_10, blue_11, blue_12),
        borderStroke = BorderStroke(1.dp, Color.Black),
        onDetailClick = onDetailClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconFinance(
                        icon = R.drawable.baseline_currency_ruble_24,
                        color = Color(0x20FFFFFF)
                    )
                    Text(
                        text = stringResource(R.string.support_text_current_balance),
                        style = text_14,
                        color = Color(0x70FFFFFF)
                    )
                }
                CardClips(
                    colorBackground = if (positive) Color(0x2000C950) else Color(0x20F17B7B),
                    colorBorder = if (positive) Color(0x3000C950) else Color(0x30F17B7B),
                    colorText = if (positive) Color(0xFF7BF1A8) else Color(0xFFF17B7B),
                    colorIcon = if (positive) Color(0xFF7BF1A8) else Color(0xFFF17B7B),
                    icon = if (positive) R.drawable.icon_arrow_up else R.drawable.icon_arrow_down,
                    value = stringResource(if (positive) R.string.finance_height else R.string.finance_recession),
                )
            }
            Text(
                text = "${currentBalance.formatNumber()} ${stringResource(suffix.toResId())}",
                style = text_36,
                color = white
            )
            Text(
                text = stringResource(R.string.support_text_current_balance_),
                style = text_14,
                color = Color(0x60FFFFFF)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HorizontalDivider(thickness = 1.dp, color = Color(0x10FFFFFF))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        text = stringResource(monthToResString3(month)),
                        style = text_12,
                        color = Color(0x50FFFFFF)
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        text = stringResource(R.string.finance_income),
                        style = text_12,
                        color = Color(0x50FFFFFF),
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        text = stringResource(R.string.finance_expenses),
                        style = text_12,
                        color = Color(0x50FFFFFF)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        text = (if (positiveMount) "+" else "-") +
                                currentBalanceMount.formatNumber() +
                                " ${stringResource(suffix.toResId())}",
                        style = text_14,
                        color = white,
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        text = incomeMount.formatNumber() +
                                " ${stringResource(suffix.toResId())}",
                        style = text_14,
                        color = green_7,
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        text = expensesMount.formatNumber() +
                                " ${stringResource(suffix.toResId())}",
                        style = text_14,
                        color = red_10,
                    )
                }
            }
        }
    }
}

@Composable
private fun IncomeExpensesCards(
    income: Double,
    expenses: Double,
    suffix: Suffix,
    navigate: (FinanceCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardFinanceNew(
            modifier = Modifier.weight(1f),
            onClick = { navigate(FinanceCategory.SALE) },
            titleRes = R.string.card_income,
            value = income,
            icon = R.drawable.icon_arrow_up,
            colors = listOf(green_6, green_shamrock),
            suffix = suffix,
            titleRes2 = R.string.finance_income_sup
        )
        CardFinanceNew(
            modifier = Modifier.weight(1f),
            onClick = { navigate(FinanceCategory.EXPENSES) },
            titleRes = R.string.card_expenditure,
            value = expenses,
            icon = R.drawable.icon_arrow_down,
            colors = listOf(Color(0xFFFB2C36), red_7),
            suffix = suffix,
            titleRes2 = R.string.finance_expenses_sup
        )
    }
}

@Composable
private fun WriteOffFinanceCards(
    ownNeed: Double,
    scrap: Double,
    suffix: Suffix,
    navigation: (FinanceCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardFinanceNew(
            modifier = Modifier.weight(1f),
            onClick = { navigation(FinanceCategory.OWN_NEED) },
            titleRes = R.string.card_own_need,
            value = ownNeed,
            icon = R.drawable.outline_savings_24,
            colors = listOf(Color(0xFFFE9A00), Color(0xFFFF6900)),
            suffix = suffix,
            titleRes2 = R.string.finance_onw_need
        )
        CardFinanceNew(
            modifier = Modifier.weight(1f),
            onClick = { navigation(FinanceCategory.SCRAP) },
            titleRes = R.string.card_scrap,
            value = scrap,
            icon = R.drawable.baseline_delete_24,
            colors = listOf(Color(0xFF4A5565), Color(0xFF364153)),
            suffix = suffix,
            titleRes2 = R.string.finance_scrap
        )
    }
}

@Composable
private fun TransactionList(
    incomeExpensesList: List<DomainIncomeExpenses>,
    suffixCurrency: Suffix,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (incomeExpensesList.isNotEmpty()) {
            Text(
                text = stringResource(R.string.support_text_transactions_now_month),
                style = text_18,
                color = black_2
            )
            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                incomeExpensesList.forEach {
                    TransactionFinanceCard(
                        title = it.title,
                        count = it.count,
                        suffix = it.suffix,
                        category = it.category,
                        price = it.price,
                        suffixCurrency = suffixCurrency,
                        date = it.date,
                        positive = it.price > 0,

                        )
                }
            }
        } else Text(
            text = stringResource(R.string.no_item_finance),
            style = textBold_16,
            modifier = Modifier,
        )
    }
}


@Composable
fun TransactionFinanceCard(
    title: String,
    count: Double? = null,
    suffix: Suffix = Suffix.PIECES,
    category: FinanceCategory,
    price: Double,
    suffixCurrency: Suffix,
    date: String? = null,
    positive: Boolean,
    onDetailClick: () -> Unit = {}
) {
    CardFieldNew(
        padding = PaddingValues(16.dp),
        onClick = onDetailClick,
        contentRow = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title,
                            style = text_16,
                            color = black_2
                        )
                        count?.let {
                            CountColorCard(
                                count = it,
                                suffix = suffix,
                                colorCard = suffix.toColorList()
                            )
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${price.formatNumber()} ${stringResource(suffixCurrency.toResId())}",
                        textAlign = TextAlign.Center,
                        style = textBold_16,
                        color = if (positive) price_green else error_base
                    )
                    date?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.baseline_calendar_month_24),
                                modifier = Modifier.size(12.dp),
                                contentDescription = null,
                                tint = gray_7
                            )
                            val dateList = it.split(".")
                            Text(
                                text = "${dateList[0]} ${stringResource(monthToResString2(dateList[1].toInt()))}",
                                style = text_14,
                                color = gray_7
                            )
                        }
                    }
                }
            }
        }
    )
}
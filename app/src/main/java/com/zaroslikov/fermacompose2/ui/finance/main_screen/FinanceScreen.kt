package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CardFinance
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceOutlined
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceOutlinedRow
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.TextAndIcon
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_28
import com.zaroslikov.fermacompose2.ui.elements.text_10
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.theme.errorLight
import com.zaroslikov.fermacompose2.ui.theme.tertiaryLight
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica

object FinanceDestination : NavigationDestination {
    override val route = "Finance"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToFinaceMount: (Int) -> Unit,
    navigateToIncomeExpenses: (Pair<Int, Boolean>) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val idProject = state.idPT

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = navigateToStart,
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                2,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarNavigation(
                    title = R.string.finance_screen_title,
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            if (state.isLoading)
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
            else
                FinanceBody(
                    modifier = modifier
                        .modifierScreen(innerPadding),
                    currentBalance = state.currentBalance,
                    income = state.income,
                    expenses = state.expenses,
                    ownNeed = state.ownNeed,
                    scrap = state.scrap,
                    incomeMount = state.incomeMount,
                    expensesMount = state.expensesMount,
                    incomeExpensesList = state.domainIncomeExpenseList,
                    navigateToFinaceMount = navigateToFinaceMount,
                    navigateToIncomeExpenses = navigateToIncomeExpenses,
                    idPT = idProject
                )
        }
    }
}

@Composable
private fun FinanceBody(
    currentBalance: Double,
    income: Double,
    expenses: Double,
    ownNeed: Double,
    scrap: Double,
    incomeMount: Double,
    expensesMount: Double,
    incomeExpensesList: List<DomainIncomeExpenses>,
    navigateToFinaceMount: (Int) -> Unit,
    navigateToIncomeExpenses: (Pair<Int, Boolean>) -> Unit,
    idPT: Long,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.card_ruble_s, currentBalance.formatNumber()),
            textAlign = TextAlign.Start,
            style = textBold_28,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
        )
        Text(
            text = stringResource(R.string.support_text_current_balance),
            textAlign = TextAlign.Start,
            style = text_10,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .padding(2.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardFinance(
                modifier = Modifier.weight(1f),
                onClick = {
                    navigateToIncomeExpenses(Pair(idPT, true))
                    AppMetrica.reportEvent("Финансы Доход")
                },
                titleRes = R.string.card_income,
                value = income
            )
            CardFinance(
                modifier = Modifier.weight(1f),
                onClick = {
                    navigateToIncomeExpenses(Pair(idPT, true))
                    AppMetrica.reportEvent("Финансы Доход")
                },
                titleRes = R.string.card_expenditure,
                value = expenses
            )
        }

        if (expanded) {
            CardFinanceOutlinedRow(
                modifier = Modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
                oneIntRes = R.string.card_own_need,
                twoIntRes = R.string.card_scrap,
                oneValue = ownNeed,
                twoValue = scrap
            )
            CardFinanceOutlinedRow(
                modifier = Modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
                oneIntRes = R.string.card_all_income,
                twoIntRes = R.string.card_all_expenditure,
                oneValue = ownNeed + income,
                twoValue = scrap + expenses
            )
            CardFinanceOutlined(
                modifier = Modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
                titleRes = R.string.card_all,
                value = (ownNeed + income) - (scrap + expenses),
            )
        }
        CardField(
            modifier = Modifier
                .clickable {
                    expanded = !expanded
                    AppMetrica.reportEvent("Финансы Подробно")
                },
            horizontalArrangement = Arrangement.Center
        ) {
            TextAndIcon(
                intRes = if (expanded) R.string.widget_briefly else R.string.widget_detail,
                iconRes = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            )
        }
        CardField(
            modifier = Modifier.clickable {
                navigateToFinaceMount(idPT)
                AppMetrica.reportEvent("Финансы Месяц")
            },
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.card_now_month),
                    style = textBold_16
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.card_ruble_s, incomeMount.formatNumber()),
                        style = textBold_16
                    )
                    Text(
                        text = stringResource(R.string.card_ruble_s, expensesMount.formatNumber()),
                        style = textBold_16
                    )
                }
            }
        }

        if (incomeExpensesList.isNotEmpty()) {
            Text(
                text = stringResource(R.string.support_text_transactions_now_month),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .padding(2.dp),
                style = textBold_16
            )
            LazyColumn {
                items(items = incomeExpensesList) {
                    TransactionRow(it)
                }
            }
        } else Text(
            text = stringResource(R.string.no_item_finance),
            style = textBold_16,
            modifier = modifier,
        )
    }
}

@Composable
fun TransactionRow(
    incomeExpensesDetails: IncomeExpensesDetails
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${incomeExpensesDetails.title} - ${formatter(incomeExpensesDetails.count)} ${incomeExpensesDetails.suffix}",
                style = textBold_14
            )
            Text(
                text = incomeExpensesDetails.date,
                style = text_12,
            )
        }
        Text(
            text = stringResource(
                R.string.card_ruble_s,
                incomeExpensesDetails.priceAll.formatNumber()
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            style = textBold_16,
            color = if (!incomeExpensesDetails.priceAll.toString()
                    .contains("-")
            ) tertiaryLight else errorLight
        )
    }
    HorizontalDivider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 5.dp)
    )
}


data class Fin(
    val title: String?, // может быть категория, название или суффикс
    val priceAll: Double
)

data class FinUiState(
    val title: String = "",
    val priceAll: Double = 0.0
)

data class FinanceCategoryDataNav(
    val idPT: Int,
    val category: String,
    val incomeBoolean: Boolean,
    val dateBegin: String,
    val dateEnd: String
)
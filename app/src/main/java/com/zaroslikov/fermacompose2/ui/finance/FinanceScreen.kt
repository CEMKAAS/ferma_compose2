package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.theme.errorLight
import com.zaroslikov.fermacompose2.ui.theme.tertiaryLight
import io.appmetrica.analytics.AppMetrica

object FinanceDestination : NavigationDestination {
    override val route = "Finance"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToFinaceMount: (Int) -> Unit,
    navigateToIncomeExpenses: (FinanceIncomeExpensesData) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: FinanceViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    val currentBalance = viewModel.currentBalanceUiState
    val income = viewModel.incomeUiState
    val expenses = viewModel.expensesUiState
    val incomeMount = viewModel.incomeMountUiState
    val expensesMount = viewModel.expensesMountUiState

    val incomeExpensesList by viewModel.incomeExpensesUiState.collectAsState()

    val idProject = viewModel.itemId

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
                TopAppBarFerma(
                    title = "Мои Финансы",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            FinanceBody(
                currentBalance = currentBalance,
                income = income,
                expenses = expenses,
                ownNeed = viewModel.ownNeedUiState,
                scrap = viewModel.scrapUiState,
                incomeMount = incomeMount,
                expensesMount = expensesMount,
                incomeExpensesList = incomeExpensesList.itemList,
                navigateToFinaceMount = navigateToFinaceMount,
                navigateToIncomeExpenses = navigateToIncomeExpenses,
                idPT = idProject,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceBody(
    currentBalance: Double,
    income: Double,
    expenses: Double,
    ownNeed: Double,
    scrap: Double,
    incomeMount: Double,
    expensesMount: Double,
    incomeExpensesList: List<IncomeExpensesDetails>,
    navigateToFinaceMount: (Int) -> Unit,
    navigateToIncomeExpenses: (FinanceIncomeExpensesData) -> Unit,
    idPT: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(contentPadding)
            .padding(8.dp),
    ) {
        Text(
            text = "${formatter(currentBalance)} ₽",
            textAlign = TextAlign.Start,
            fontSize = 28.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Текущий баланс",
            textAlign = TextAlign.Start,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .padding(2.dp),
        )
        Row {
            Card(
                onClick = {
                    navigateToIncomeExpenses(FinanceIncomeExpensesData(idPT, true))
                    AppMetrica.reportEvent("Финансы Доход")
                },
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(0.5f),
            ) {
                Text(
                    text = "Доход",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${formatter(income)} ₽",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Card(
                onClick = {
                    navigateToIncomeExpenses(FinanceIncomeExpensesData(idPT, false))
                    AppMetrica.reportEvent("Финансы Расход")
                },
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(1f)
            ) {
                Text(
                    text = "Расход",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${formatter(expenses)} ₽",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )

            }
        }

        var expanded by remember { mutableStateOf(false) }
        val extraPadding by animateDpAsState(
            if (expanded) 2.dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ), label = ""
        )

        if (expanded) {
            Row {
                CardFinace(
                    textTitle = "Сэкономлено",
                    textCount = "${formatter(ownNeed)} ₽",
                    float = 0.5f,
                    animate = extraPadding
                )
                CardFinace(
                    textTitle = "Потеряно", textCount = "${formatter(scrap)} ₽", float = 1f,
                    animate = extraPadding
                )
            }
            Row {
                CardFinace(
                    textTitle = "Итог доходов",
                    textCount = "${formatter(ownNeed + income)} ₽",
                    float = 0.5f,
                    animate = extraPadding
                )
                CardFinace(
                    textTitle = "Итог расходов",
                    textCount = "${formatter(scrap + expenses)} ₽",
                    float = 1f,
                    animate = extraPadding
                )
            }
            CardFinace(
                textTitle = "Итого",
                textCount = "${formatter((ownNeed + income) - (scrap + expenses))} ₽",
                float = 1f,
                animate = extraPadding
            )
        }

        Card(
            onClick = {
                expanded = !expanded
                AppMetrica.reportEvent("Финансы Подробно")
            },
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (expanded) "Кратко" else "Подробнее",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Показать меню"
                    )
                }
            }
        }


        Card(
            onClick = {
                navigateToFinaceMount(idPT)
                AppMetrica.reportEvent("Финансы Месяц")
            },
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Текущий месяц",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                fontWeight = FontWeight.SemiBold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "${formatter(incomeMount)} ₽",
//                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
//                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${formatter(expensesMount)} ₽",
//                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
//                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }

        }

        if (incomeExpensesList.isNotEmpty()) {
            Text(
                text = "Транзакции в текущем месяце:",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .padding(2.dp),
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn {
                items(items = incomeExpensesList) { it ->
                    TransactionRow(it)
                }
            }
        } else {
            Text(
                text = stringResource(R.string.no_item_finance),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        }

    }
}

@Composable
fun TransactionRow(
    incomeExpensesDetails: IncomeExpensesDetails
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(
                text = "${incomeExpensesDetails.title} - ${formatter(incomeExpensesDetails.count)} ${incomeExpensesDetails.suffix}",
                modifier = Modifier
                    .wrapContentSize()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = incomeExpensesDetails.date,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 1.dp, horizontal = 6.dp)
            )
        }
        if (!incomeExpensesDetails.priceAll.toString().contains("-")) {
            Text(
                text = "${formatter(incomeExpensesDetails.priceAll)} ₽",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = tertiaryLight
            )
        } else {
            Text(
                text = "${formatter(incomeExpensesDetails.priceAll)} ₽",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = errorLight
            )
        }

    }
    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(5.dp))
}


@Composable
fun CardFinace(
    textTitle: String,
    textCount: String,
    float: Float,
    animate: Dp = 0.dp
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(2.dp, Color(0xFFE0E3D4)),
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(float)
            .padding(bottom = animate.coerceAtLeast(0.dp))
    ) {
        Text(
            text = textTitle,
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = textCount,
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun FinancePrewie() {
//    FinanceBody(
//        currentBalance = 155.0,
//        income = 22.0,
//        expenses = 33.0,
//        expensesMount = 0.0,
//        incomeMount = 0.0,
//        incomeExpensesList = arrayListOf(
//            IncomeExpensesDetails(
//                "Govno",
//                55.0,
//                "ED",
//                88.0,
//                1,
//                2,
//                1996
//            ), IncomeExpensesDetails("Govno", 55.0, "ED", 88.0, 1, 2, 1996)
//        ),
//        navigateToFinaceMount = {},
//        navigateToIncomeExpenses = {},
//        idPT = 1
//    )
//}

@Composable
fun RowCardFin(column1: String, column2: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = column1,
//                    textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier
//                        .fillMaxWidth()
                .padding(2.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = column2,
//                    textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier
//                        .fillMaxWidth()
                .padding(2.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}


data class Fin(
    val title: String?, // может быть категория, название или суффикс
    val priceAll: Double
)

data class FinUiState(
    val title: String = "",
    val priceAll: Double = 0.0
)

data class FinanceCategoryData(
    val idPT: Int,
    val category: String,
    val incomeBoolean: Boolean,
)

data class FinanceCategoryDataNav(
    val idPT: Int,
    val category: String,
    val incomeBoolean: Boolean,
    val dateBegin: String,
    val dateEnd: String
)


data class FinanceIncomeExpensesData(
    val idPT: Int,
    val incomeBoolean: Boolean
)

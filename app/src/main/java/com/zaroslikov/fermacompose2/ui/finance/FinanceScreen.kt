package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet

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
    navigateToCategory: (FinanceCategoryData) -> Unit,
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

    val incomeRow by viewModel.incomeCategoryUiState.collectAsState()
    val expensesRow by viewModel.expensesCategoryUiState.collectAsState()
    val incomeExpensesList by viewModel.incomeExpensesUiState.collectAsState()

    val idProject = viewModel.itemId
    val showBottomSheetFilter = remember { mutableStateOf(false) }

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
            },
//            bottomBar = {
//                Banner(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                )
//            }
        ) { innerPadding ->
            FinanceBody(
                currentBalance = currentBalance,
                income = income,
                expenses = expenses,
                incomeRow = incomeRow.itemList,
                expensesRow = expensesRow.itemList,
                incomeExpensesList = incomeExpensesList.itemList,
                navigateToCategory = navigateToCategory,
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
    incomeRow: List<Fin>,
    expensesRow: List<Fin>,
    incomeExpensesList: List<IncomeExpensesDetails>,
    navigateToCategory: (FinanceCategoryData) -> Unit,
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
            text = "$currentBalance ₽",
            textAlign = TextAlign.Start,
            fontSize = 25.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Текущий баланс",
            textAlign = TextAlign.Start,
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
        Row {
            Card(
                onClick = { navigateToIncomeExpenses(FinanceIncomeExpensesData(idPT, true)) },
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
                    text = "$income ₽",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Card(
                onClick = { navigateToIncomeExpenses(FinanceIncomeExpensesData(idPT, false)) },
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
                    text = "$expenses ₽",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    fontWeight = FontWeight.SemiBold
                )

            }
        }

        if (expensesRow.isNotEmpty() && incomeRow.isEmpty()) {
            Text(
                text = "А где же доходы в этом месяце?",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                fontWeight = FontWeight.SemiBold
            )
        } else if (incomeRow.isNotEmpty()) {
            Text(
                text = "Доходы в текущем месяце",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                fontWeight = FontWeight.SemiBold
            )
            LazyRow {
                items(items = incomeRow) {
                    CardRow(it, modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateToCategory(
                                FinanceCategoryData(
                                    idPT,
                                    it.category,
                                    true
                                )
                            )
                        })
                }
            }
        } else {
            Text(
                text = "Доходов в этом месяце нет :(",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                fontWeight = FontWeight.SemiBold
            )
        }

        if (expensesRow.isNotEmpty()) {
            Text(
                text = "Расходы в текущем месяце",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                fontWeight = FontWeight.SemiBold
            )
            LazyRow {
                items(items = expensesRow) {
                    CardRow(it, modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            navigateToCategory(
                                FinanceCategoryData(
                                    idPT,
                                    it.category,
                                    false
                                )
                            )
                        })
                }
            }
        } else {
            Text(
                text = "Расходов в этом месяце нет (Это хорошо)",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                fontWeight = FontWeight.SemiBold
            )
        }


        Text(
            text = "Транзакции в текущем месяце",
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            fontWeight = FontWeight.SemiBold
        )

        if (incomeExpensesList.isNotEmpty()) {
            LazyColumn {
                items(items = incomeExpensesList) { it ->
                    TransactionRow(it)
                }
            }
        }else{
            Text(
                text = stringResource(R.string.no_item_finance),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRow(
    fin: Fin,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Text(
            text = fin.category, textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
        )
        Text(
            text = "${fin.priceAll} ₽", textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
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
                text = incomeExpensesDetails.title,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = "${incomeExpensesDetails.day}. ${incomeExpensesDetails.mount}.${incomeExpensesDetails.year}",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(vertical = 3.dp, horizontal = 6.dp)
            )
        }
        if (!incomeExpensesDetails.priceAll.toString().contains("-")) {
            Text(
                text = "${incomeExpensesDetails.priceAll} ₽",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.Green
            )
        } else {
            Text(
                text = "${incomeExpensesDetails.priceAll} ₽",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.Red
            )
        }

    }
    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(5.dp))
}

//@Preview(showBackground = true)
//@Composable
//fun FinancePrewie() {
//    FinanceBody(
//        currentBalance = 155.0,
//        income = 22.0,
//        expenses = 33.0,
//        incomeRow = arrayListOf(),
//        expensesRow = arrayListOf(),
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
//        navigateToCategory = {},
//        navigateToIncomeExpenses = {},
//        idPT = 1
//    )
//}

data class Fin(
    val category: String,
    val priceAll: Double
)

data class FinTit(
    val Title: String,
    val priceAll: Double
)

data class FinanceCategoryData(
    val idPT: Int,
    val category: String,
    val incomeBoolean: Boolean
)

data class FinanceIncomeExpensesData(
    val idPT: Int,
    val incomeBoolean: Boolean
)

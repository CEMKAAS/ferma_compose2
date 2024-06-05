package com.zaroslikov.fermacompose2.ui.finance

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
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import java.sql.Driver

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
    navigateToCategory: (navigateId) -> Unit,
    navigateToIncomeExpenses: (Int) -> Unit,
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
    //val idProject = viewModel.itemId
    val showBottomSheetFilter = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = navigateToStart,
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                2,//ToDo 3
                "1"
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
                    showBottomFilter = showBottomSheetFilter, //todo на фильтр
                    filterSheet = true,
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            FinanceBody(
                currentBalance = currentBalance,
                income = income,
                expenses = expenses,
                incomeRow = incomeRow.itemList,
                expensesRow = expensesRow.itemList,
                incomeExpensesList = incomeExpensesList.itemList,
                onItemClick = navigateToItemUpdate,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding,
                showBottomFilter = showBottomSheetFilter,

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
    onItemClick: (navigateId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBottomFilter: MutableState<Boolean>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(contentPadding),
    ) {

        Text(
            text = "$currentBalance ₽",
            textAlign = TextAlign.Start,
            fontSize = 25.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Текущий баланс",
            textAlign = TextAlign.Start,
            fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            onClick = { /*TODO*/ }, modifier = Modifier.padding(0.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Доход",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$income ₽",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }

        Card(
            onClick = { /*TODO*/ }, modifier = Modifier.padding(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Расход",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$expenses ₽",
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "Доходы в текущем месяце",
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            fontWeight = FontWeight.SemiBold
        )
        LazyRow {
            items(items = incomeRow) {
                CardRow(it)
            }
        }

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
                CardRow(it)
            }
        }

        Text(
            text = "Транзакции в текущем месяце",
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn {
            items(items = incomeExpensesList) {
                TransactionRow(it)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRow(
    fin: Fin
) {
    Card(onClick = { /*TODO*/ }, modifier = Modifier.padding(5.dp)) {
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
        Divider(color = Color.DarkGray, thickness = 2.dp, modifier = Modifier.padding(5.dp))
    }
}

//@Preview
//@Composable
//fun FinancePrewie() {
//    FinanceScreen(
//        navigateToStart = { /*TODO*/ },
//        navigateToModalSheet = {},
//        navigateToItemUpdate = {},
//        navigateToItem = {},
//        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    )
//}

data class Fin(
    val category: String,
    val priceAll: Double
)
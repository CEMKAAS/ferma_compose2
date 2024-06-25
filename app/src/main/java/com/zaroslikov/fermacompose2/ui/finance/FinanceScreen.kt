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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import java.sql.Driver

object FinanceArhivDestination : NavigationDestination {
    override val route = "FinanceArhiv"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceArhivScreen(
    navigateToStart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val currentBalance = viewModel.currentBalanceUiState
    val income = viewModel.incomeUiState
    val expenses = viewModel.expensesUiState

    val idProject = viewModel.itemId

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarEdit(
                title = "",
                navigateUp = navigateToStart
            )
        }
    ) { innerPadding ->
        FinanceBody(
            currentBalance = currentBalance,
            income = income,
            expenses = expenses,
            navigateToIncomeExpenses = navigateToIncomeExpenses,
            idPT = idProject,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceBody(
    currentBalance: Double,
    income: Double,
    expenses: Double,
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

        Card {
            Text(text = "sds")
        }


    }
}

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
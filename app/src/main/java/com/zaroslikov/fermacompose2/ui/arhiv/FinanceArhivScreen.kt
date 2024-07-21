package com.zaroslikov.fermacompose2.ui.arhiv

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.animal.AnimalCard
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

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
    navigateToBack: () -> Unit,
    navigateToStart: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToIncomeExpenses: (FinanceIncomeExpensesData) -> Unit,
    viewModel: FinanceArhivViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val project = viewModel.projectState
    val animalUiState by viewModel.animalUiState.collectAsState()
    val currentBalance = viewModel.currentBalanceUiState
    val income = viewModel.incomeUiState
    val expenses = viewModel.expensesUiState

    val idProject = viewModel.itemId

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarEdit(
                title = project.titleProject,
                navigateUp = navigateToBack
            )
        },
//        bottomBar = {
//            Banner(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            )
//        }
    ) { innerPadding ->
        FinanceBody(
            currentBalance = currentBalance,
            income = income,
            expenses = expenses,
            navigateToIncomeExpenses = navigateToIncomeExpenses,
            idPT = idProject,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentPadding = innerPadding,
            animalList = animalUiState.itemList,
            projectState = project,
            onValueChange = viewModel::updateUiState,
            unarchive = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateToStart()
                }
            },
            deleteRoom = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateToStart()
                }
            }
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
    animalList: List<AnimalTable>,
    projectState: IncubatorProjectEditState,
    onValueChange: (IncubatorProjectEditState) -> Unit = {},
    unarchive: () -> Unit,
    deleteRoom: () -> Unit,
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
            text = "Баланс",
            textAlign = TextAlign.Start,
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
        FinanceCard(
            projectTable = projectState, modifier = Modifier
                .padding(5.dp)
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

        animalList.forEach {
            AnimalCard(
                animalTable = it, modifier = Modifier
                    .padding(8.dp)
            )
        }

        OutlinedButton(
            onClick = {
                onValueChange(projectState.copy(arhive = "0"))
                unarchive()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_unarchive_24),
                contentDescription = " Разархивировать"
            )
            Text(text = " Разархивировать")
        }

        OutlinedButton(
            onClick = deleteRoom,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = "Удалить"
            )
            Text(text = " Удалить ")
        }

    }
}

@Composable
fun FinanceCard(projectTable: IncubatorProjectEditState, modifier: Modifier) {

    val modifierText = Modifier
        .wrapContentSize()
        .padding(vertical = 3.dp, horizontal = 6.dp)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Text(
            text = "Данные",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Text(text = "Дата начала проекта: ${projectTable.data}", modifier = modifierText)
        Text(text = "Дата окончания проекта: ${projectTable.dateEnd}", modifier = modifierText)
    }
}


data class FinTit(
    val Title: String,
    val priceAll: Double
)

data class FinanceIncomeExpensesData(
    val idPT: Int,
    val incomeBoolean: Boolean
)
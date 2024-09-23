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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSampleNoLimit
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.dateLong
import com.zaroslikov.fermacompose2.ui.start.formatter
import java.util.Calendar

object FinanceMountDestination : NavigationDestination {
    override val route = "FinanceMount"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceMountScreen(
    navigateBack: () -> Unit,
    navigateToCategory: (FinanceCategoryData) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceMountViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val incomeMount = viewModel.incomeMountUiState
    val expensesMount = viewModel.expensesMountUiState
    val currentBalance = incomeMount - expensesMount

    val incomeRow by viewModel.incomeCategoryUiState.collectAsState()
    val expensesRow by viewModel.expensesCategoryUiState.collectAsState()

    val idProject = viewModel.itemId

    val selectMonthBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarEdit(title = "Текущий месяц", navigateUp = navigateBack)
        }
    ) { innerPadding ->
        FinanceMountBody(
            currentBalance = currentBalance,
            incomeRow = incomeRow.itemList,
            expensesRow = expensesRow.itemList,
            navigateToCategory = navigateToCategory,
            idPT = idProject,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceMountBody(
    currentBalance: Double,
    incomeRow: List<Fin>,
    expensesRow: List<Fin>,
    navigateToCategory: (FinanceCategoryData) -> Unit,
    idPT: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(contentPadding)
            .padding(8.dp),
    ) {

        val calendar = Calendar.getInstance()

        Card {
            Text(
                text = "${SetMount(calendar[Calendar.MONTH] + 1)} ${calendar[Calendar.YEAR]}",
                textAlign = TextAlign.Start,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            )
        }

        Text(
            text = "${formatter(currentBalance)} ₽",
            textAlign = TextAlign.Start,
            fontSize = 28.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Текущий доход",
            textAlign = TextAlign.Start,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
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
                    CardMountRow(it, modifier = Modifier
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
        Text(
            text = "${formatter(currentBalance)} ₽",
            textAlign = TextAlign.Start,
            fontSize = 28.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Расход за месяц",
            textAlign = TextAlign.Start,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
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
                    CardMountRow(it, modifier = Modifier
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardMountRow(
    fin: Fin,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Text(
            text = fin.category, textAlign = TextAlign.Start,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
        )
        Text(
            text = "${formatter(fin.priceAll)} ₽", textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMonthBottomSheet(
    mountList: List<String>,
    editBottomSheet: MutableState<Boolean>,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = { editBottomSheet.value = false },
        sheetState = sheetState
    ) {

        val calendar = Calendar.getInstance()

        LazyColumn {
            items(mountList) {
                Card {
                    Text(
                        text = "${SetMount(calendar[Calendar.MONTH] + 1)} ${calendar[Calendar.YEAR]}",
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                    )
                }
            }
        }
    }
}

fun SetMount(mount: Int): String {
    return when (mount) {
        1 -> "Январь"
        2 -> "Февраль"
        3 -> "Март"
        4 -> "Апрель"
        5 -> "Май"
        6 -> "Июнь"
        7 -> "Июль"
        8 -> "Август"
        9 -> "Сентябрь"
        10 -> "Октябрь"
        11 -> "Ноябрь"
        12 -> "Декабрь"
        else -> {
            "Январь"
        }
    }

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

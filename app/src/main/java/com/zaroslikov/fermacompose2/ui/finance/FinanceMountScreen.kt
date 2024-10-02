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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
            incomeMount = viewModel.incomeMountUiState,
            expensesMount = viewModel.expensesMountUiState,
            incomeRow = incomeRow.itemList,
            expensesRow = expensesRow.itemList,
            navigateToCategory = navigateToCategory,
            idPT = idProject,
            modifier = modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceMountBody(
    incomeMount: Double,
    expensesMount: Double,
    incomeRow: List<Fin>,
    expensesRow: List<Fin>,
    navigateToCategory: (FinanceCategoryData) -> Unit,
    idPT: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

//        val calendar = Calendar.getInstance()
//        Card {
//            Text(
//                text = "${SetMount(calendar[Calendar.MONTH] + 1)} ${calendar[Calendar.YEAR]}",
//                textAlign = TextAlign.Start,
//                fontSize = 15.sp,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(5.dp),
//            )
//        }

        val modifierCard = Modifier
            .fillMaxWidth()
            .padding(8.dp)

        val modifierHeading = Modifier
            .wrapContentSize()
            .padding(6.dp)

        val modifierText = Modifier
            .wrapContentSize()
            .padding(vertical = 3.dp, horizontal = 6.dp)


        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Данные:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Доход: ${formatter(incomeMount)} ₽",
                modifier = modifierText
            )
            Text(
                text = "Расход: ${formatter(expensesMount)} ₽",
                modifier = modifierText
            )
            Text(
                text = "Прибыль: ${formatter(incomeMount - expensesMount)} ₽",
                modifier = modifierText
            )

        }

        LazyRowMount(
            list = incomeRow,
            navigateToCategory = navigateToCategory,
            idPT = idPT,
            heading = "Доходы по категории в текущем месяце",
            headingNull = "Доходов в этом месяце не было :("
        )

        LazyRowMount(
            list = expensesRow,
            navigateToCategory = navigateToCategory,
            idPT = idPT,
            heading = "Расходы по категории в текущем месяце",
            headingNull = "Расходов в этом месяце не было нет (Это хорошо)"
        )
    }
}


@Composable
fun LazyRowMount(
    list: List<Fin>,
    navigateToCategory: (FinanceCategoryData) -> Unit,
    idPT: Int,
    heading: String,
    headingNull: String
) {
    if (list.isNotEmpty()) {
        Text(
            text = heading,
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            fontWeight = FontWeight.SemiBold
        )
        LazyRow {
            items(items = list) {
                CardMountRow(it, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateToCategory(
                            FinanceCategoryData(
                                idPT,
                                "${if (it.title == "") "Не указано " else it.title}",
                                true
                            )
                        )
                    })
            }
        }
    } else {
        Text(
            text = headingNull,
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            fontWeight = FontWeight.SemiBold
        )
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
            text = fin.title ?: "Не указан", textAlign = TextAlign.Start,
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


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SelectMonthBottomSheet(
//    mountList: List<String>,
//    editBottomSheet: MutableState<Boolean>,
//    sheetState: SheetState
//) {
//    ModalBottomSheet(
//        onDismissRequest = { editBottomSheet.value = false },
//        sheetState = sheetState
//    ) {
//
//        val calendar = Calendar.getInstance()
//
//        LazyColumn {
//            items(mountList) {
//                Card {
//                    Text(
//                        text = "${SetMount(calendar[Calendar.MONTH] + 1)} ${calendar[Calendar.YEAR]}",
//                        textAlign = TextAlign.Start,
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),
//                    )
//                }
//            }
//        }
//    }
//}




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

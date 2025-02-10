package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.zaroslikov.fermacompose2.TopAppBarCalendar
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSampleNoLimit
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.dateLong
import com.zaroslikov.fermacompose2.ui.start.formatter
import io.appmetrica.analytics.AppMetrica
import java.text.SimpleDateFormat
import java.util.Calendar
import androidx.compose.material3.OutlinedCard as OutlinedCard1

object FinanceMountDestination : NavigationDestination {
    override val route = "FinanceMount"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceMountScreen(
    navigateBack: () -> Unit,
    navigateToCategory: (FinanceCategoryDataNav) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceMountViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val incomeRow by viewModel.incomeCategoryUiState.collectAsState()
    val expensesRow by viewModel.expensesCategoryUiState.collectAsState()

    val idProject = viewModel.itemId
    val format = SimpleDateFormat("dd.MM.yyyy")
    val formatSQL = SimpleDateFormat("yyyy-MM-dd")
    var openCalendarDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("Текущий месяц") }

    if (openCalendarDialog) {
        DateRangePickerModal(
            onDateRangeSelected = {
                Pair(it.first?.let { it1 -> viewModel.updateDateBegin(it1) },
                    it.second?.let { it1 -> viewModel.updateDateEnd(it1) })
            },
            onDismiss = { openCalendarDialog = false },
            dateBegin = viewModel.dateBegin,
            dateEnd = viewModel.dateEnd,
            upAnalisis = {
                viewModel.upAnalisis()

                text = "${format.format(viewModel.dateBegin)} - ${format.format(viewModel.dateEnd)}"
            }
        )
    }


    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarCalendar(
                title = text,
                true,
                navigateUp = navigateBack,
                settingUp = {
                    openCalendarDialog = true
                    AppMetrica.reportEvent("Финансы Месяц Диапазон")
                }
            )
        }
    ) { innerPadding ->
        FinanceMountBody(
            incomeMount = viewModel.incomeMountUiState,
            expensesMount = viewModel.expensesMountUiState,
            ownNeedsMonth = viewModel.ownNeedMonthUiState,
            scrapMonth = viewModel.scrapMonthUiState,
            incomeRow = incomeRow.itemList,
            expensesRow = expensesRow.itemList,
            navigateToCategory = {
                navigateToCategory(
                    FinanceCategoryDataNav(
                        it.idPT,
                        it.category,
                        it.incomeBoolean,
                        formatSQL.format(viewModel.dateBegin),
                        formatSQL.format(viewModel.dateEnd)
                    )
                )
            },
            text = text,
            idPT = idProject,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun FinanceMountBody(
    incomeMount: Double,
    expensesMount: Double,
    ownNeedsMonth: Double,
    scrapMonth: Double,
    incomeRow: List<Fin>,
    expensesRow: List<Fin>,
    navigateToCategory: (FinanceCategoryData) -> Unit,
    text: String,
    idPT: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(contentPadding)
            .padding(5.dp)
    ) {

        Row {
            CardFinace(
                textTitle = "Доход",
                textCount = "${formatter(incomeMount)} ₽",
                float = 0.5f
            )
            CardFinace(
                textTitle = "Расход", textCount = "${formatter(expensesMount)} ₽", float = 1f
            )
        }
        Row {
            CardFinace(
                textTitle = "Сэкономлено",
                textCount = "${formatter(ownNeedsMonth)} ₽",
                float = 0.5f
            )
            CardFinace(
                textTitle = "Потери", textCount = "${formatter(scrapMonth)} ₽", float = 1f
            )
        }

        Row {
            CardFinace(
                textTitle = "Итого доходов",
                textCount = "${formatter(incomeMount + ownNeedsMonth)} ₽",
                float = 0.5f
            )
            CardFinace(
                textTitle = "Итого расходов",
                textCount = "${formatter(expensesMount + scrapMonth)} ₽",
                float = 1f
            )
        }


        CardFinace(
            textTitle = "Итого",
            textCount = "${formatter(incomeMount + ownNeedsMonth - expensesMount - scrapMonth)} ₽",
            float = 1f
        )

        LazyRowMount(
            list = incomeRow,
            navigateToCategory = {
                navigateToCategory(
                    FinanceCategoryData(
                        it.first,
                        it.second,
                        true
                    )
                )
            },
            idPT = idPT,
            heading = "Доходы по категориям за $text",
            headingNull = "Доходов за $text не было :("
        )

        LazyRowMount(
            list = expensesRow,
            navigateToCategory = {
                navigateToCategory(
                    FinanceCategoryData(
                        it.first,
                        it.second,
                        false
                    )
                )
            },
            idPT = idPT,
            heading = "Расходы по категориям за $text",
            headingNull = "Расходов за $text не было (Это хорошо)"
        )
    }
}


@Composable
fun LazyRowMount(
    list: List<Fin>,
    navigateToCategory: (Pair<Int, String>) -> Unit,
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
                .padding(8.dp),
            fontWeight = FontWeight.SemiBold
        )
        LazyRow {
            items(items = list) {
                CardMountRow(it, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateToCategory(
                            Pair(
                                idPT,
                                "${if (it.title == "") "Не указано " else it.title}"
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
                .padding(8.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}


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

package com.zaroslikov.fermacompose2.ui.warehouse


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFermaWarehouse
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffTableInsert
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.TimeZone

object WarehouseDestination : NavigationDestination {
    override val route = "warehouse"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigationToAnalysis: (AnalysisNav) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    navigationToNewYear: (Pair<Boolean, Int>) -> Unit,
//    isFirstStart:Boolean,
//    isFirstEnd: () -> Unit,
    viewModel: WarehouseViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

//    AlterDialigStart(
//        isFirstStart = isFirstStart,
//        dialogTitle = "Хозяйство",
//        dialogText = "Склад — ключевой элемент проекта, где хранится произведенная и приобретенная продукция. Здесь можно получить детальную информацию о каждом продукте." +
//                " Слева расположены меню проекта. Рекомендуем начать с добавления животных в разделе «Мои Животные» для корректной работы расчетов, затем добавлять свою продукцию на склад в разделе \"Моя Продукция\"." +
//                "\nДополнительная информация доступна в нашей группе ВКонтакте." +
//                "\nУдачи.",
////        isFirstEndConfig = isFirstEnd
//    )
    val context = LocalContext.current
    val homeUiState by viewModel.homeUiState.collectAsState()
    val homeFoodUiState by viewModel.homeFoodUiState.collectAsState()
    val homeExpensesUiState by viewModel.homeExpensesUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val fastAddUiState by viewModel.fastAddUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val idProject = viewModel.itemId

    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = {
//                    isFirstEnd()
                    navigateToStart()
                },
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                1,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarFermaWarehouse(
                    title = "Мой Склад",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    navigateToEdit = { navigateToEdit(idProject) },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            if (isLoading) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                WarehouseBody(
                    itemList = homeUiState.itemList,
                    modifier = modifier.fillMaxSize(),
                    contentPadding = innerPadding,
                    navigationToAnalysis = {
                        navigationToAnalysis(AnalysisNav(idProject = idProject, name = it))
                        AppMetrica.reportEvent("Анализ через склад")
                    },
                    itemExpensesList = homeExpensesUiState.itemList,
                    itemFoodList = homeFoodUiState.itemList,
                    writeOffButton = {
                        coroutineScope.launch {
                            viewModel.saveItem(
                                WriteOffTable(
                                    id = it.first.id,
                                    title = it.first.title,
                                    count = it.first.count,
                                    day = it.first.day,
                                    mount = it.first.mount,
                                    year = it.first.year,
                                    priceAll = it.first.priceAll,
                                    suffix = it.first.suffix,
                                    status = it.first.status,
                                    idPT = idProject,
                                    note = it.first.note
                                ), it.second
                            )
                        }
                        Toast.makeText(
                            context,
                            "Списано: ${it.first.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    navigationToNewYaer = { navigationToNewYear(Pair(true, idProject)) },
                    itemFastAddList = fastAddUiState.itemList,
                    fastAddButton = {
                        coroutineScope.launch {
                            viewModel.saveFastAddItem(
                                it
                            )
                        }
                        Toast.makeText(
                            context,
                            "Добавлено: ${it.title} ${it.disc} ${it.suffix}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun WarehouseBody(
    itemList: List<WarehouseData>,
    itemFoodList: List<ExpensesTable>,
    itemExpensesList: List<WarehouseData>,
    itemFastAddList: List<FastAdd>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationToAnalysis: (String) -> Unit,
    writeOffButton: (Pair<WriteOffTableInsert, ExpensesTable>) -> Unit,
    navigationToNewYaer: () -> Unit,
    fastAddButton: (FastAdd) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemList.isEmpty() && itemFoodList.isEmpty() && itemExpensesList.isEmpty()) {
            Column(
                modifier = modifier
                    .padding(contentPadding)
                    .padding(15.dp)
            ) {
                Text(
                    text = "Добро пожаловать на Склад!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "В этом разделе указано сколько Вашего товара сейчас на складе (учитываются данные при добавлении, продажи и списании), если колличество Вашего Товара ушло в минус, оно отображаться в этом Разделе не будет!",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "Сейчас склад пустой:(\nДобавьте товар на склад в разделе \"Моя Продукция\" или \"Мои Покупки\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                )
            }
        } else {
            WarehouseInventoryList(
                itemList = itemList,
                itemExpensesList = itemExpensesList,
                itemFoodList = itemFoodList,
                contentPadding = contentPadding,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_small)
                ),
                navigationToAnalysis = navigationToAnalysis,
                writeOffButton = writeOffButton,
                navigationToNewYear = navigationToNewYaer,
                itemFastAddList = itemFastAddList,
                fastAddButton = fastAddButton
            )
        }

    }
}

@Composable
private fun WarehouseInventoryList(
    itemList: List<WarehouseData>,
    itemFoodList: List<ExpensesTable>,
    itemExpensesList: List<WarehouseData>,
    itemFastAddList: List<FastAdd>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (String) -> Unit,
    navigationToNewYear: () -> Unit,
    writeOffButton: (Pair<WriteOffTableInsert, ExpensesTable>) -> Unit,
    fastAddButton: (FastAdd) -> Unit
) {
    var fastAddBoolean by rememberSaveable { mutableStateOf(true) }
    var productBoolean by rememberSaveable { mutableStateOf(true) }
    var foodBoolean by rememberSaveable { mutableStateOf(true) }
    var expensesTable by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(contentPadding)
    ) {
        if (itemFastAddList.isNotEmpty()) {
            TextButtonWarehouse(
                boolean = fastAddBoolean,
                onClick = { fastAddBoolean = !fastAddBoolean },
                title = "Быстрое добавление",
            )
            LazyRow(verticalAlignment = Alignment.CenterVertically) {
                if (fastAddBoolean) {
                    items(items = itemFastAddList) { item ->
                        FastAddCard(
                            fastAdd = item,
                            onClick = { fastAddButton(item) },
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = modifier
        ) {
            if (newYearBoolean()) {
                item {
                    Button(
                        onClick = {
                            navigationToNewYear()
                            AppMetrica.reportEvent("Итоги года по проекту")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 8.dp)
                    ) {
                        Text(text = "Итоги года по проекту!")
                    }
                }
            }


            if (itemList.isNotEmpty()) {
                item {
                    TextButtonWarehouse(
                        boolean = productBoolean,
                        onClick = { productBoolean = !productBoolean },
                        title = "Продукция"
                    )
                }
                if (productBoolean) {
                    items(items = itemList) { item ->
                        WarehouseProductCard(
                            warehouseProduct = item,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navigationToAnalysis(item.Title.replace("/", "-"))
                                }
                        )
                    }
                }
            }

            if (itemFoodList.isNotEmpty()) {
                item {
                    TextButtonWarehouse(
                        boolean = foodBoolean,
                        onClick = { foodBoolean = !foodBoolean },
                        title = "Корм"
                    )
                }
                if (foodBoolean) {
                    items(items = itemFoodList) { item ->
                        WarehouseFoodCard(
                            warehouseProduct = item,
                            modifier = Modifier
                                .padding(8.dp),
                            writeOffButton = writeOffButton
                        )
                    }
                }
            }
            if (itemExpensesList.isNotEmpty()) {
                item {
                    TextButtonWarehouse(
                        boolean = expensesTable,
                        onClick = { expensesTable = !expensesTable },
                        title = "Купленный товар"
                    )
                }
                if (expensesTable) {
                    items(items = itemExpensesList) { item ->
                        WarehouseExpensesCard(
                            warehouseProduct = item,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WarehouseProductCard(
    warehouseProduct: WarehouseData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
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
                    text = warehouseProduct.Title,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Text(
                text = "${formatter(warehouseProduct.ResultCount)} ${warehouseProduct.suffix}",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun WarehouseFoodCard(
    warehouseProduct: ExpensesTable,
    modifier: Modifier = Modifier,
    writeOffButton: (Pair<WriteOffTableInsert, ExpensesTable>) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {


        val startDate =
            LocalDate.of(warehouseProduct.year, warehouseProduct.mount, warehouseProduct.day)
        val daysToAdd = warehouseProduct.foodDesignedDay
        val endDate = startDate.plusDays(daysToAdd.toLong())
        val currentDate = LocalDate.now()


        if (currentDate >= endDate) println("Конечная дата уже наступила.")

        val totalDays = ChronoUnit.DAYS.between(startDate, endDate)
        val remainingDays = ChronoUnit.DAYS.between(currentDate, endDate)
        val percentageRemaining = (remainingDays.toDouble() / totalDays * 100).coerceIn(0.0, 100.0)

        val endFood = currentDate >= endDate

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "${warehouseProduct.title} - ${if (endFood) "закончился" else "хватит на $remainingDays суток"}",
                modifier = Modifier
                    .wrapContentSize()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            if (endFood) {
                val calendar = Calendar.getInstance()
                Button(onClick = {
                    writeOffButton(
                        Pair(
                            WriteOffTableInsert(
                                id = 0,
                                title = warehouseProduct.title,
                                count = warehouseProduct.count,
                                day = calendar.get(Calendar.DAY_OF_MONTH),
                                mount = calendar.get(Calendar.MONTH),
                                year = calendar.get(Calendar.YEAR),
                                status = 0,
                                priceAll = 0.0,
                                suffix = warehouseProduct.suffix,
                                note = ""
                            ), warehouseProduct
                        )
                    )
                }) {
                    Text(text = "Cписать")
                }
            }
        }

        if (!endFood) {
            LinearProgressIndicator(
                progress = {
                    (percentageRemaining / 100.0).toFloat()
                },
                color = if (percentageRemaining <= 20) MaterialTheme.colorScheme.error else ProgressIndicatorDefaults.linearColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }
    }
}


@Composable
fun WarehouseExpensesCard(
    warehouseProduct: WarehouseData,
    modifier: Modifier = Modifier
) {


//    OutlinedCard(
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//        ),
//        border = BorderStroke(4.dp, CardDefaults.cardColors().containerColor),
//        modifier = modifier
//    )

        Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    )
        {
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
                    text = warehouseProduct.Title,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            Text(
                text = "${formatter(warehouseProduct.ResultCount)} ${warehouseProduct.suffix}",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun TextButtonWarehouse(
    onClick: () -> Unit,
    boolean: Boolean,
    title: String,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                thickness = 1.dp, modifier = Modifier
                    .fillMaxWidth(0.30f)
            )
            Icon(
                if (boolean) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Показать меню",
                modifier = Modifier
                    .padding(start = 3.dp)
                    .fillMaxWidth(0.1f)
            )
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .fillMaxWidth(0.5f)
            )
            HorizontalDivider(
                thickness = 1.dp, modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
fun FastAddCard(
    fastAdd: FastAdd,
    onClick: () -> Unit = {}
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    if (selected) {
        LaunchedEffect(Unit) {
            delay(2000) // Задержка в 2000 миллисекунд (2 секунды)
            selected = false 
        }
    }
    Spacer(modifier = Modifier.width(8.dp))
    FilterChip(
        selected = selected,
        label = {
            Column(
                modifier = Modifier.padding(3.dp)
            ) {
                Text(
                    text = fastAdd.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${formatter(fastAdd.disc)} ${fastAdd.suffix}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                if (fastAdd.category == "" || fastAdd.category == "Без категории")
                    else Text(
                        text = fastAdd.category,
                        modifier = Modifier
                            .padding(top = 2.dp),
                        fontSize = 10.sp
                    )
                
                if (fastAdd.animal != "") {
                    Text(
                        text = fastAdd.animal,
                        fontSize = 10.sp
                    )
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add,
                contentDescription = "Done icon",
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
        onClick = {
            onClick()
            selected = true
        }
    )
}


fun newYearBoolean(): Boolean {
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)
    val year = calendar.get(Calendar.YEAR)
    return formattedDate == "30.12.$year" || formattedDate == "31.12.$year"
}

data class WarehouseData(
    val Title: String,
    val ResultCount: Double,
    val suffix: String
)

data class AnalysisNav(
    val idProject: Int,
    val name: String
)


//@Preview
//@Composable
//fun FastPrewie() {
////    FastAddCard(fastAdd = FastAdd("Молоко", 10.0, "Шт.", "Без категории", 0, "Несушка", 5))
//    TextButtonWarehouse(onClick = {},true, "Быстрое добавление")
//}
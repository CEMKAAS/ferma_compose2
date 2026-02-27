package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_15
import com.zaroslikov.fermacompose2.blue_16
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderShowAllButton
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseIntent
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseViewModel
import com.zaroslikov.fermacompose2.white

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
    navigateToEdit: (Long) -> Unit,
    navigateToNote: (Long) -> Unit,
    navigationToNewYear: (Pair<Boolean, Int>) -> Unit,
    navigationToAnalysis: (Triple<Long, String, Suffix>) -> Unit,
    viewModel: WarehouseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.warehouse_screen_title,
                scrollBehavior = scrollBehavior,
                onNavigateBackClick = navigateToStart,
                onSettingsClick = { navigateToEdit(state.idPT) },
                onNoteClick = { navigateToNote(state.idPT) }
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            if (state.productList.isEmpty() && state.expensesList.isEmpty() && state.fastAddList.isEmpty())
                EmptyBookmark(
                    modifier = Modifier.padding(innerPadding),
                    iconRes = R.drawable.baseline_warehouse_24,
                    title = R.string.warehouse_screen_welcome,
                    supportText = R.string.warehouse_screen_welcome_support_text,
                    supportSecondText = R.string.warehouse_screen_welcome_support_second_text,
                    iconColor = blue_4,
                    backgroundColor = blue_3,
                )
            else
                WarehouseContainer(
                    modifier = Modifier.modifierScreen(innerPadding),
                    state = state,
                    onFastAddClick = { viewModel.onIntent(WarehouseIntent.FastAddClicked(it)) },
                    onShowFastAddClick = { viewModel.onIntent(WarehouseIntent.ShowFastAddClicked(it)) },
                    onAnalysisNavClick = {
                        navigationToAnalysis(Triple(state.idPT, it.first, it.second))
                    }
                )
        /*WarehouseBody(
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
                            countSuffix = it.first.countSuffix,
                            status = it.first.status,
                            idPT = idProject.toLong(),
                            note = it.first.note,
                            price = null
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
            idProject = idProject,
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
        )*/
    }
}

@Composable
private fun WarehouseContainer(
    modifier: Modifier = Modifier,
    state: WarehouseState,
    onAnalysisNavClick: (Pair<String, Suffix>) -> Unit,
    onShowFastAddClick: (Boolean) -> Unit,
    onFastAddClick: (DomainFastAddProduct) -> Unit
) {
    Column(
        modifier = modifier
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        FastAdd(
            state.isShowFastAddProduct, state.fastAddList,
            { onShowFastAddClick(it) }) { onFastAddClick(it) }
        if (state.productList.isEmpty() && state.expensesList.isEmpty())
            Box(
                modifier = Modifier
                    .weight(1f)              // ← занимает всё оставшееся место
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center // ← центр
            ) {
                EmptyBookmark(
                    iconRes = R.drawable.icon_open_box,
                    title = R.string.warehouse_screen_warehouse_empty,
                    supportText = R.string.warehouse_screen_warehouse_empty_support_text,
                    supportSecondText = R.string.warehouse_screen_warehouse_empty_support_second_text,
                    iconColor = blue_4,
                    backgroundColor = blue_3,
                )
            }
        else {
            if (state.productList.isNotEmpty())
                WarehouseSection(
                    titleRes = R.string.add_screen_title2,
                    iconRes = R.drawable.icon_add_product,
                    list = state.productList,
                    textColor = green_2,
                    borderColor = green_1,
                    iconColor = green_shamrock,
                    backgroundMiniColor = green_g_1,
                ) { item ->
                    ProductCard(
                        title = item.title,
                        value = item.count,
                        suffix = item.suffix
                    ) { onAnalysisNavClick(item.title to item.suffix) }
                }
            if (state.expensesList.isNotEmpty())
                WarehouseSection(
                    titleRes = R.string.warehouse_screen_products,
                    iconRes = R.drawable.icon_expenses,
                    list = state.expensesList,
                    textColor = blue_8,
                    borderColor = blue_16,
                    iconColor = blue_1,
                    backgroundMiniColor = blue_3,
                ) { item ->
                    ProductCard(
                        title = item.title,
                        value = item.count,
                        suffix = item.suffix
                    ) { }
                }
        }
    }
}

@Composable
private fun ProductCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Double,
    suffix: Suffix,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ghostly_white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = text_14, color = black_2)
            Text(
                "${value.formatNumber()} " + stringResource(suffix.toResId()),
                style = text_14,
                color = black_2
            )
        }
    }
}

@Composable
private fun <T> WarehouseSection(
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int,
    list: List<T>,
    iconColor: Color,
    backgroundMiniColor: Color,
    textColor: Color,
    borderColor: Color,
    itemCard: @Composable (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    CardFieldNew {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(iconRes), contentDescription = null, tint = iconColor)
                    Text(stringResource(titleRes), style = text_16, color = black_2)
                }
                TextMiniCard(
                    "${list.size} " +
                            stringResource(R.string.warehouse_screen_positions),
                    textColor = textColor,
                    backgroundColor = backgroundMiniColor
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (list.isNotEmpty()) {
                    for (i in list.indices) {
                        itemCard(list[i])
                        if (i == 4 && !expanded)
                            break
                    }
                }
            }
            /*AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
            visible = isManyCount
            ) {*/
            if (list.size >= 4)
                BorderShowAllButton(
                    listSize = list.size,
                    textColor = textColor,
                    borderColor = borderColor,
                    isShowMore = expanded
                ) { expanded = !expanded }
        }
    }
}


@Composable
private fun FastAdd(
    isShowFastAddProduct: Boolean,
    list: List<DomainFastAddProduct>,
    onShowClick: (Boolean) -> Unit,
    onClick: (DomainFastAddProduct) -> Unit
) {
    val icon =
        if (isShowFastAddProduct) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down

    Card(
        onClick = { onShowClick(!isShowFastAddProduct) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = blue_3
        ),
        border = BorderStroke(
            width = 1.dp,
            color = blue_9
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_electric_bolt_24),
                        contentDescription = null,
                        tint = blue_1
                    )
                    Text(
                        stringResource(R.string.warehouse_screen_fast_add),
                        style = text_16,
                        color = blue_14
                    )
                }
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = blue_1
                )
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isShowFastAddProduct
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    list.forEach {
                        FastAddCard(
                            title = it.title,
                            count = it.count,
                            suffix = it.suffix,
                            category = it.category,
                            animal = it.animalName,
                            onClick = { onClick(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FastAddCard(
    title: String,
    count: Double,
    suffix: Suffix,
    animal: String?,
    category: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = white
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    icon = R.drawable.icon_add,
                    colorIcon = blue_1,
                    color = blue_13,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(title, style = text_14, color = black_2)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        animal?.let {
                            TextMiniCard(
                                it,
                                textColor = blue_15,
                                backgroundColor = green_g_3
                            )
                        }
                        if (animal != null && category != null) Text(
                            "•",
                            style = text_12,
                            color = grey
                        )
                        category?.let {
                            Text(it, style = text_12, color = marengo)
                        }
                    }
                }
            }
            Text(
                "${count.formatNumber()} " + stringResource(suffix.toResId()),
                style = text_14,
                color = black_2
            )
        }

    }
}
/*
@Composable
private fun WarehouseBody(
    itemList: List<WarehouseData>,
    itemFoodList: List<ExpensesTable>,
    itemExpensesList: List<WarehouseData>,
    itemFastAddList: List<FastAdd>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationToAnalysis: (String) -> Unit,
    idProject: Int,
    writeOffButton: (Pair<WriteOffTable, ExpensesTable>) -> Unit,
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
                fastAddButton = fastAddButton,
                idProject = idProject
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
    idProject: Int,
    navigationToAnalysis: (String) -> Unit,
    navigationToNewYear: () -> Unit,
    writeOffButton: (Pair<WriteOffTable, ExpensesTable>) -> Unit,
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
                            idProject = idProject,
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
    idProject: Int,
    modifier: Modifier = Modifier,
    writeOffButton: (Pair<WriteOffTable, ExpensesTable>) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {


        val startDate =
            LocalDate.of(warehouseProduct.year, warehouseProduct.mount, warehouseProduct.day)
        val daysToAdd = warehouseProduct.foodDesignedDay
        val endDate = startDate.plusDays(daysToAdd!!.toLong())
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
                            WriteOffTable(
                                id = 0,
                                title = warehouseProduct.title,
                                count = warehouseProduct.count,
                                day = calendar.get(Calendar.DAY_OF_MONTH),
                                mount = calendar.get(Calendar.MONTH),
                                year = calendar.get(Calendar.YEAR),
                                status = false,
                                priceAll = 0.0,
                                note = "",
                                idPT = idProject.toLong(),
                                countSuffix = warehouseProduct.countSuffix,
                                price = TODO()
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
*/


////    OutlinedCard(
////        colors = CardDefaults.cardColors(
////            containerColor = MaterialTheme.colorScheme.surface,
////        ),
////        border = BorderStroke(4.dp, CardDefaults.cardColors().containerColor),
////        modifier = modifier
////    )
//
//    Card(
//        modifier = modifier,
//        elevation = CardDefaults.cardElevation(2.dp),
//        colors = CardDefaults.cardColors()
//    )
//    {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Column(
//                modifier = Modifier.fillMaxWidth(0.7f)
//            ) {
//                Text(
//                    text = warehouseProduct.Title,
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .padding(6.dp),
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp
//                )
//            }
//            Text(
//                text = "${formatter(warehouseProduct.ResultCount)} ${warehouseProduct.suffix}",
//                textAlign = TextAlign.End,
//                modifier = Modifier
//                    .padding(6.dp)
//                    .fillMaxWidth(1f),
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 18.sp
//            )
//        }
//    }
//}
//
@Composable
fun TextButtonWarehouse(
    onClick: () -> Unit,
    boolean: Boolean,
    title: String = "",
    @StringRes intRes: Int = R.string.support_text_count_warehouse_s
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.weight(1f) // линия слева занимает свободное место
            )
            Icon(
                painterResource(if (boolean) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
                contentDescription = "Показать меню",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = stringResource(intRes),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 10.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.weight(1f) // линия справа занимает свободное место
            )
        }
    }
}


//@Composable
//fun FastAddCard(
//    fastAdd: FastAdd,
//    onClick: () -> Unit = {}
//) {
//    var selected by rememberSaveable { mutableStateOf(false) }
//    if (selected) {
//        LaunchedEffect(Unit) {
//            delay(2000) // Задержка в 2000 миллисекунд (2 секунды)
//            selected = false
//        }
//    }
//    Spacer(modifier = Modifier.width(8.dp))
//    FilterChip(
//        selected = selected,
//        label = {
//            Column(
//
//        }
//}   modifier = Modifier.padding(3.dp)
//            ) {
//                Text(
//                    text = fastAdd.title,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 14.sp
//                )
//                Text(
//                    text = "${formatter(fastAdd.disc)} ${fastAdd.suffix}",
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 12.sp
//                )
//                if (fastAdd.category == "" || fastAdd.category == "Без категории")
//                else Text(
//                    text = fastAdd.category,
//                    modifier = Modifier
//                        .padding(top = 2.dp),
//                    fontSize = 10.sp
//                )
//
//                if (fastAdd.animal != "") {
//                    Text(
//                        text = fastAdd.animal,
//                        fontSize = 10.sp
//                    )
//                }
//            }
//        },
//        leadingIcon = {
//            Icon(
//                imageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add,
//                contentDescription = "Done icon",
//                modifier = Modifier.size(FilterChipDefaults.IconSize)
//            )
//        },
//        onClick = {
//            onClick()
//            selected = true
//        }
//    )
//}
//
//
//fun newYearBoolean(): Boolean {
//    val format = SimpleDateFormat("dd.MM.yyyy")
//    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//    val formattedDate: String = format.format(calendar.timeInMillis)
//    val year = calendar.get(Calendar.YEAR)
//    return formattedDate == "30.12.$year" || formattedDate == "31.12.$year"
//}
//
//data class WarehouseData(
//    val Title: String,
//    val ResultCount: Double,
//    val suffix: String
//)
//
//data class AnalysisNav(
//    val idProject: Int,
//    val name: String
//)
//
//
////@Preview
////@Composable
////fun FastPrewie() {
//////    FastAddCard(fastAdd = FastAdd("Молоко", 10.0, "Шт.", "Без категории", 0, "Несушка", 5))
////    TextButtonWarehouse(onClick = {},true, "Быстрое добавление")
////}
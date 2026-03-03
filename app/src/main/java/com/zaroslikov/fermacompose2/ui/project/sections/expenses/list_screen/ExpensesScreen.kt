@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody

object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    /* modifier: Modifier = Modifier,
     navigateToStart: () -> Unit,
     navigateToModalSheet: (DrawerNavigation) -> Unit,
     navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
     navigateToItemAdd: (Long) -> Unit,
     drawerState: DrawerState,*/
    viewModel: ExpensesViewModel = hiltViewModel()
    /* state: AddListState,
     onIntent: (AddListIntent) -> Unit*/
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(orang_1, orang_2)
    val primeColor = orang_1
    val idProject = state.idPT

    val query = state.textSearch.trim().lowercase()
    val searchList = if (query.isBlank() && !state.isGroup) state.list
    else
        state.list.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.note.lowercase().contains(query) ||
                    item.category.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    stringResource(item.countSuffix.toResId()).lowercase().contains(query)
            "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                .contains(query) ||
                    (item.priceAll ?: item.price).toString().lowercase().contains(query)
        }

    val searchList2 = if (query.isBlank() && state.isGroup) state.briefly
    else
        state.briefly.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    (item.price).toString().lowercase().contains(query)
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = {
                    viewModel.onIntent(ExpensesListIntent.SearchChanged(it))
                },
                onClick = { viewModel.onIntent(ExpensesListIntent.GroupClicked(it)) }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(ExpensesListIntent.OpenBottomSheetEntry(true))
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            ExpensesContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                color = primeColor,
                searchText = state.textSearch,
                itemList = state.list,
                searchList = searchList,
                brieflyList = searchList2,
                onInsertClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetEntry(true)
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetGroup(true, it)
                    )
                },
                details = state.isGroup,
            )
            if (state.openBottomSheetEntry)
                ExpensesEntryBottomSheet(
                    modifier = Modifier,
                    state = state.currentProduct,
                    colors = colors,
                    onIntent = viewModel::onIntent
                )
            if (state.openBottomSheetGroup)
                BrieflyBottomSheetExpenses(
                    color = primeColor,
                    list = state.listBriefly,
                    titleProduct = state.currentBriefly.title,
                    count = state.currentBriefly.count,
                    suffix = state.currentBriefly.suffix,
                    countEntry = state.currentBriefly.rowCount,
                    onDismissRequest = {
                        viewModel.onIntent(
                            ExpensesListIntent.OpenBottomSheetGroup(
                                false
                            )
                        )
                    },
                    onEditClick = {
                        viewModel.onIntent(
                            ExpensesListIntent.OpenBottomSheetEntry(true, it)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete(it)) },
                )
        }
    }
}


@Composable
fun ExpensesContainer(
    modifier: Modifier = Modifier,
    details: Boolean,
    color: Color = blue_1,
    searchText: String,
    itemList: List<DomainExpensesTable>,
    searchList: List<DomainExpensesTable>,
    brieflyList: List<BrieflyExpensesDomain>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainExpensesTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflyExpensesDomain) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailsClick = onDetailsClick,
        detailCard = { index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = Suffix.RUBLE,
                category = item.category,
                note = item.note,
                animal = "Murka",
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                onClick = { },
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                titleProduct = item.title,
                count = item.count,
                suffix = item.suffix,
                price = item.price,
                priceSuffix = Suffix.RUBLE,
                countEntry = item.rowCount,
                color = color,
                colorSecondary = Color(0xFFFFF7ED),
                onClick = { onDetailsClick(item) },
                icon = R.drawable.icon_sale,
            )
        },
        titleRes = R.string.message_no_data_title_expenses,
        messageRes = R.string.message_no_data_message_expenses,
        details = details,
        iconRes = R.drawable.icon_sale,
        iconColor = orang_1,
        backgroundColor = orang_3
    )
}


@Composable
fun BrieflyBottomSheetExpenses(
    list: List<DomainExpensesTable>,
    color: Color = blue_1,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    onEditClick: (DomainExpensesTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    BrieflyBottomSheetUniversal(
        list = list,
        titleProduct = titleProduct,
        count = count,
        suffix = suffix,
        countEntry = countEntry,
        onDismissRequest = onDismissRequest,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        itemCard = { product ->
            DetailProductCardNew(
                modifier = Modifier,
                isCardField = false,
                count = product.count,
                suffix = product.countSuffix,
                price = product.price,
                category = product.category,
                note = product.note,
                animal = "Mas",
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                onClick = {},
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) }
            )
        }
    )
}


/*

@Composable
private fun ExpensesBody(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel,
    itemList: List<DomainExpensesTable>,
    brieflyList: List<BrieflyExpensesDomain>,
    onItemClick: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: () -> Unit
) {
    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            brieflyList = brieflyList,
            viewModel = viewModel,
            onItemClick = { onItemClick(Pair(it.idPT, it.id)) },
            modifier = modifier
        )
    else MessageNoData(
        modifier = modifier,
        onClick = navigateToItemAdd,
        titleRes = R.string.message_no_date_title_sale,
        messageRes = R.string.message_no_date_message_sale,
        supportRes = R.string.message_no_date_support_text_sale,
        buttonRes = R.string.button_sale_message_no_data
    )
}

@Composable
private fun InventoryList(
    viewModel: ExpensesViewModel,
    itemList: List<DomainExpensesTable>,
    brieflyList: List<BrieflyExpensesDomain>,
    onItemClick: (DomainExpensesTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var details by rememberSaveable { mutableStateOf(true) }

    val extraPadding by animateDpAsState(
        if (details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )
    val extraPaddingResd by animateDpAsState(
        if (!details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            TextButtonWarehouse(
                boolean = details,
                onClick = { details = !details },
                intRes = if (details) R.string.widget_briefly else R.string.widget_detail
            )
        }
        if (details) {
            items(items = itemList, key = { it.id }) { item ->
                ExpensesCard(
                    expensesTable = item,
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(bottom = extraPadding.coerceAtLeast(0.dp))
                )
            }
        } else {
            items(items = brieflyList) { item ->
                BrieflyPriceCard(
                    viewModel = viewModel,
                    product = item,
                    onItemClick = onItemClick,
                    modifier = Modifier
                        .padding(bottom = extraPaddingResd.coerceAtLeast(0.dp))
                )
            }
        }
    }
}

@Composable
fun BrieflyPriceCard(
    viewModel: ExpensesViewModel,
    product: BrieflyExpensesDomain,
    onItemClick: (DomainExpensesTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    CardField(modifier = modifier.clickable {
        expanded = !expanded
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                TextLine(
                    modifier = Modifier.padding(start = 3.dp, bottom = 5.dp),
                    valueString = product.title,
                    textStyle = textBold_20
                )
                IconAndText(
                    iconRes = R.drawable.baseline_shopping_basket_24,
                    valueString = stringResource(
                        R.string.card_count_briefly_s,
                        "${product.count.formatNumber()} ${product.suffix}",
                        product.price.formatNumber(),
                        stringResource(R.string.currency_ruble),
                    ),
                )
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painterResource(if (expanded) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
                    contentDescription = "Показать меню"
                )
            }
        }
    }
    if (expanded) {
        val products = viewModel.getDetailsName(product.title).collectAsState(initial = emptyList())
        products.value.forEach {
            ExpensesCard(
                expensesTable = it,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = 0.95f
                    }
                    .clickable { onItemClick(it) }
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp)))
        }
    }
}


@Composable
fun ExpensesCard(
    expensesTable: DomainExpensesTable,
    modifier: Modifier = Modifier
) {
    val price = expensesTable.priceAll ?: expensesTable.price
    CardField(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(start = 3.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = expensesTable.title,
                        style = textBold_20,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (expensesTable.animalId != null)
                        Icon(
                            painter = painterResource(R.drawable.baseline_pets_24),
                            contentDescription = stringResource(R.string.is_empty)
                        )
                }
                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateBuilder(
                        expensesTable.day,
                        expensesTable.month,
                        expensesTable.year
                    )
                )
                expensesTable.category.takeUnless { it == "Без категории" || it.isEmpty() }
                    ?.let { category ->
                        IconAndText(
                            iconRes = R.drawable.baseline_format_list_bulleted_24,
                            valueString = category
                        )
                    }
                if (expensesTable.note != "")
                    IconAndText(
                        iconRes = R.drawable.baseline_sticky_note_2_24,
                        valueString = expensesTable.note
                    )
            }
            Text(
                text = stringResource(
                    R.string.card_count_s,
                    "${expensesTable.count.formatNumber()} ${expensesTable.countSuffix}",
                    price.formatNumber(),
                    stringResource(R.string.currency_ruble),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                style = textBold_20
            )
        }
    }
}*/

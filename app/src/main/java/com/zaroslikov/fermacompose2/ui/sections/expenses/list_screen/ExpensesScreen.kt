package com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.water.BrieflyItemPrice
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CircularProgress
import com.zaroslikov.fermacompose2.ui.composeElement.FloatButton
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndText
import com.zaroslikov.fermacompose2.ui.composeElement.MessageNoData
import com.zaroslikov.fermacompose2.ui.composeElement.TextLine
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_20
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse

object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: (Int) -> Unit,
    drawerState: DrawerState,
    viewModel: ExpensesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val brieflyUiState by viewModel.brieflyUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val idProject = viewModel.itemId
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = navigateToStart,
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                5,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarNavigation(
                    title = R.string.expenses_screen_title,
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatButton { navigateToItemAdd(idProject) }
            }
        ) { innerPadding ->
            if (isLoading) {
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
            } else {
                ExpensesBody(
                    modifier = Modifier
                        .modifierScreenLazy(innerPadding),
                    itemList = homeUiState.itemList,
                    brieflyList = brieflyUiState.itemList,
                    viewModel = viewModel,
                    onItemClick = navigateToItemUpdate,
                    navigateToItemAdd = { navigateToItemAdd(idProject) }
                )
            }
        }
    }
}

@Composable
private fun ExpensesBody(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel,
    itemList: List<ExpensesTable>,
    brieflyList: List<BrieflyItemPrice>,
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
    itemList: List<ExpensesTable>,
    brieflyList: List<BrieflyItemPrice>,
    onItemClick: (ExpensesTable) -> Unit,
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
    product: BrieflyItemPrice,
    onItemClick: (ExpensesTable) -> Unit,
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
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
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
    expensesTable: ExpensesTable,
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
                        expensesTable.mount,
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
}

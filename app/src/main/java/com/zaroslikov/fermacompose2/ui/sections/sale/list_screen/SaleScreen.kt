package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse

object SaleDestination : NavigationDestination {
    override val route = "Sale"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    modifier: Modifier = Modifier,
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: (Long) -> Unit,
    drawerState: DrawerState,
    viewModel: SaleViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val idProject = state.idPT

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = navigateToStart,
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                4,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarNavigation(
                    title = R.string.sale_screen_title,
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = { FloatButton { navigateToItemAdd(idProject) } }
        ) { innerPadding ->
            if (state.isLoading) {
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
            } else {
                SaleBody(
                    modifier = Modifier
                        .modifierScreenLazy(innerPadding),
                    viewModel = viewModel,
                    itemList = state.list,
                    brieflyList = state.briefly,
                    onItemClick = navigateToItemUpdate,
                    navigateToItemAdd = { navigateToItemAdd(idProject) }
                )
            }
        }
    }
}


@Composable
fun SaleBody(
    modifier: Modifier = Modifier,
    viewModel: SaleViewModel,
    itemList: List<DomainSaleTable>,
    brieflyList: List<BrieflySaleDomain>,
    onItemClick: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: () -> Unit
) {
    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            brieflyList = brieflyList,
            viewModel = viewModel,
            onItemClick = { onItemClick(it.idPT to it.id) },
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
    viewModel: SaleViewModel,
    itemList: List<DomainSaleTable>,
    brieflyList: List<BrieflySaleDomain>,
    onItemClick: (DomainSaleTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var details by rememberSaveable { mutableStateOf(true) }

    val extraPadding by animateDpAsState(
        if (details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val extraPaddingResd by animateDpAsState(
        if (!details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
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
                SaleProductCard(
                    saleTable = item,
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(bottom = extraPadding.coerceAtLeast(0.dp))
                )
            }
        } else {
            items(items = brieflyList) { item ->
                BrieflyPriceCard(
                    product = item,
                    viewModel = viewModel,
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
    viewModel: SaleViewModel,
    product: BrieflySaleDomain,
    onItemClick: (DomainSaleTable) -> Unit,
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
            Column(modifier = Modifier.weight(0.7f)) {
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
                    )
                )

            }
            IconButton(onClick = {
                expanded = !expanded
            }) {
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
            SaleProductCard(
                saleTable = it,
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
fun SaleProductCard(
    saleTable: DomainSaleTable,
    modifier: Modifier = Modifier
) {
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
                Text(
                    modifier = Modifier.padding(start = 3.dp, bottom = 10.dp),
                    text = saleTable.title,
                    style = textBold_20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateBuilder(
                        saleTable.day,
                        saleTable.month,
                        saleTable.year
                    )
                )
                saleTable.category.takeUnless { it == "Без категории" || it.isEmpty() }
                    ?.let { category ->
                        IconAndText(
                            iconRes = R.drawable.baseline_format_list_bulleted_24,
                            valueString = category
                        )
                    }
                saleTable.buyer.takeUnless { it == "Неизвестный" }
                    ?.let { buyer ->
                        IconAndText(
                            iconRes = R.drawable.baseline_person_24,
                            valueString = buyer
                        )
                    }
                if (saleTable.note != "")
                    IconAndText(
                        iconRes = R.drawable.baseline_sticky_note_2_24,
                        valueString = saleTable.note
                    )
            }
            Text(
                text = stringResource(
                    R.string.card_count_briefly_s,
                    "${saleTable.count.formatNumber()} ${saleTable.countSuffix}",
                    saleTable.price.formatNumber(),
                    stringResource(R.string.currency_ruble)
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
package com.zaroslikov.fermacompose2.ui.sale

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.water.BrieflyItemPrice
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.home.AddProductCard
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter
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
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (navigateId) -> Unit,
    navigateToItem: (Int) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: SaleViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.saleUiState.collectAsState()
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
                4,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarFerma(
                    title = "Мои Продажи",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigateToItem(idProject) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.item_entry_title) // TODO Преименовать
                    )
                }
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
                SaleBody(
                    viewModel = viewModel,
                    itemList = homeUiState.itemList,
                    brieflyList = brieflyUiState.itemList,
                    onItemClick = navigateToItemUpdate,
                    modifier = modifier.fillMaxSize(),
                    contentPadding = innerPadding,
                    navigateToItemAdd = { navigateToItem(idProject) }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaleBody(
    viewModel: SaleViewModel,
    itemList: List<SaleTable>,
    brieflyList: List<BrieflyItemPrice>,
    onItemClick: (navigateId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigateToItemAdd: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemList.isEmpty()) {
            Column(
                modifier = modifier
                    .padding(contentPadding)
                    .padding(15.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Text(
                    text = "Добро пожаловать в раздел \"Мои Продажи!\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "В этом разделе Вы можете добавлять товары, которые продаете с Вашей фермы! Каждому товару можно назначить цену, кол-во, категорию и покупателя.",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "Сейчас нет продаж:(\nНажмите + чтобы добавить\nили",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                )
                Button(
                    onClick = navigateToItemAdd, modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Text(text = "Добавить Продажу!")
                }
            }
        } else {
            InventoryList(
                itemList = itemList,
                brieflyList = brieflyList,
                onItemClick = { onItemClick(navigateId(it.id, it.idPT)) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun InventoryList(
    viewModel: SaleViewModel,
    itemList: List<SaleTable>,
    brieflyList: List<BrieflyItemPrice>,
    onItemClick: (SaleTable) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var details by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            TextButtonWarehouse(
                boolean = details,
                onClick = { details = !details },
                title = if (details) "Кратко" else "Подробно"
            )
        }
        if (details) {
            items(items = itemList, key = { it.id }) { item ->
                SaleProductCard(saleTable = item,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onItemClick(item) })
            }
        } else {
            items(items = brieflyList) { item ->
                BrieflyPriceCard(
                    product = item,
                    modifier = Modifier
                        .padding(8.dp),
                    viewModel = viewModel,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun BrieflyPriceCard(
    viewModel: SaleViewModel,
    product: BrieflyItemPrice,
    onItemClick: (SaleTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.clickable {
            expanded = !expanded
        },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    text = product.title,
                    modifier = Modifier
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${formatter(product.count)} ${product.suffix} " +
                            "за ${formatter(product.price)} ₽",
                    modifier = Modifier
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
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
            SaleProductCard(saleTable = it,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onItemClick(it) })
        }
    }
}



@Composable
fun SaleProductCard(
    saleTable: SaleTable,
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
                    text = saleTable.title,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                if (saleTable.category == "Без категории" || saleTable.category == "") {

                } else {
                    Text(
                        text = "Категория: ${saleTable.category}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }

                if (saleTable.buyer == "Неизвестный" || saleTable.buyer == "") {

                } else {
                    Text(
                        text = "Покупатель: ${saleTable.buyer}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                if (saleTable.note != "") {
                    Text(
                        text = "Примечание: ${saleTable.note}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                Text(
                    text = "Дата: ${
                        String.format(
                            "%02d.%02d.%d",
                            saleTable.day,
                            saleTable.mount,
                            saleTable.year
                        )
                    }",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 3.dp, horizontal = 6.dp)
                )
            }

            Text(
                text = "${formatter(saleTable.count)} ${saleTable.suffix}\n за \n${
                    formatter(
                        saleTable.priceAll
                    )
                } ₽",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}



data class navigateId(
    val id: Int,
    val idPT: Int
)

data class navigateId2(
    val id: Long,
    val idPT: Long
)
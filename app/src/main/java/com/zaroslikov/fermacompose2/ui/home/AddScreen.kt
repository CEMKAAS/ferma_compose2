package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.water.BrieflyItemCount
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.add.ChoiseProjectDestination
import com.zaroslikov.fermacompose2.ui.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.AnalysisNav
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse
import io.appmetrica.analytics.AppMetrica

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (navigateId) -> Unit,
    navigateToItemAdd: (Int) -> Unit,
    navigationToAnalysis: (AnalysisNav) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    isFirstStart: Boolean,
    viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val brieflyUiState by viewModel.brieflyUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val idProject = viewModel.itemId
    val coroutineScope = rememberCoroutineScope()

//    val list2 = viewModel.items.value.toMutableList()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navigateToStart = navigateToStart,
                navigateToModalSheet = navigateToModalSheet,
                drawerState = drawerState,
                3,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarFerma(
                    title = "Моя Продукция",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigateToItemAdd(idProject) },
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
            },
        ) { innerPadding ->
            AddBody(
                itemList = homeUiState.itemList,
                brieflyList = brieflyUiState.itemList,
                viewModel = viewModel,
                onItemClick = navigateToItemUpdate,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding,
                navigateToItemAdd = { navigateToItemAdd(idProject) },
                navigationToAnalysis = {
                    navigationToAnalysis(AnalysisNav(idProject = idProject, name = it))
                    AppMetrica.reportEvent("Анализ через склад")
                },
            )
        }
    }
}


@Composable
private fun AddBody(
    viewModel: AddViewModel,
    itemList: List<AddTable>,
    brieflyList: List<BrieflyItemCount>,
    onItemClick: (navigateId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigateToItemAdd: () -> Unit,
    navigationToAnalysis: (String) -> Unit,
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
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "Добро пожаловать в раздел \"Мои Товары!\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "В этом разделе Вы можете добавлять товары, которые поступают с вашей фермы! Каждому товару можно назначить кол-во, категорию и животное, если оно занесено в разделе \"Мои Животные\"",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "Сейчас нет товаров:(\nНажмите + чтобы добавить\nили",
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
                    Text(text = "Добавить Продукцию!")
                }
            }
        } else {
            InventoryList(
                itemList = itemList,
                brieflyList = brieflyList,
                viewModel = viewModel,
                onItemClick = { onItemClick(navigateId(it.id, it.idPT)) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                navigationToAnalysis = navigationToAnalysis
            )
        }
    }
}


@Composable
private fun InventoryList(
    itemList: List<AddTable>,
    brieflyList: List<BrieflyItemCount>,
    viewModel: AddViewModel,
    onItemClick: (AddTable) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (String) -> Unit,
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
                title = if (details) "Показать кратко" else "Показать подробно"
            )
        }

        if (details) {
            items(items = itemList, key = { it.id }) { item ->
                AddProductCard(addProduct = item,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onItemClick(item) })
            }
        } else {

            items(items = brieflyList) { item ->
                BrieflyCountCard(
                    product = item,
                    modifier = Modifier
                        .padding(8.dp),
                    viewModel = viewModel,
                    contentPadding = contentPadding,
                    onItemClick = onItemClick,
                    navigationToAnalysis = navigationToAnalysis
                )
            }
        }
    }
}

@Composable
fun BrieflyCountCard(
    viewModel: AddViewModel,
    product: BrieflyItemCount,
    onItemClick: (AddTable) -> Unit,
    navigationToAnalysis: (String) -> Unit ={},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    val itemList = viewModel.detailsName(product.title).collectAsState()

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
            IconButton(onClick = { navigationToAnalysis(product.title)}) {
                Icon(
                    painterResource(id = R.drawable.baseline_analytics_24),
                    contentDescription = "Анализ",
                    modifier = Modifier.fillMaxWidth(0.1f)
                )
            }
            Text(
                text = product.title,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "${formatter(product.count)} ${product.suffix}",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(0.2f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Показать меню",
                    modifier = Modifier
                        .fillMaxWidth(1f)
                )
            }
        }
    }
    if (expanded) {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding
        ) {
            items(items = itemList.value.itemList, key = { it.id }) { item ->
                AddProductCard(addProduct = item,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onItemClick(item) })
            }
        }
    }
}


@Composable
fun AddProductCard(
    addProduct: AddTable,
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
                    text = addProduct.title,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                if (addProduct.category == "Без категории" || addProduct.category == "") {


                } else {
                    Text(
                        text = "Категория: ${addProduct.category}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }

                if (addProduct.animal != "") {
                    Text(
                        text = "Животное: ${addProduct.animal}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                if (addProduct.note != "") {
                    Text(
                        text = "Примечание: ${addProduct.note}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                Text(
                    text = "Дата: ${
                        String.format(
                            "%02d.%02d.%d",
                            addProduct.day,
                            addProduct.mount,
                            addProduct.year
                        )
                    }",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 3.dp, horizontal = 6.dp)
                )
            }
            Text(
                text = "${formatter(addProduct.count)} ${addProduct.suffix}",
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

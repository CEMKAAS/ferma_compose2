package com.zaroslikov.fermacompose2.ui.writeOff

import androidx.compose.foundation.Image
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.water.BrieflyItemCount
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.home.AddProductCard
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.home.BrieflyCountCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse

object WriteOffDestination : NavigationDestination {
    override val route = "WriteOff"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteOffScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (navigateId) -> Unit,
    navigateToItem: (Int) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: WriteOffViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.writeOffUiState.collectAsState()
    val titleUiState by viewModel.titleUiState.collectAsState()
    val brieflyUiState by viewModel.brieflyUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val writeOffBoolean = titleUiState.titleList.isNotEmpty()
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
                6,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarFerma(
                    title = "Мои Списания",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (writeOffBoolean) {
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
                WriteOffBody(
                    viewModel = viewModel,
                    writeOffBoolean = writeOffBoolean,
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

@Composable
private fun WriteOffBody(
    viewModel: WriteOffViewModel,
    writeOffBoolean: Boolean,
    itemList: List<WriteOffTable>,
    brieflyList: List<BrieflyItemCount>,
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
            if (!writeOffBoolean) {
                Column(
                    modifier = modifier
                        .padding(contentPadding)
                        .padding(15.dp)
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {
                    Text(
                        text = "Добро пожаловать в раздел \"Мои Списания!\"",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        fontSize = 20.sp,
                    )
                    Text(
                        text = "В этом разделе вы можете оформить списание продукции или товара, который был поврежден или который вы решили оставить для личного использования. Для каждого списанного товара можно указать количество, цену и причину списания (для собственных нужд или утилизация).",
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        fontSize = 20.sp,
                    )
                    Text(
                        text = "Добавьте товар в разделе \"Мои Товар\" для списания",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 20.sp,
                    )
                }
            } else {
                Column(modifier = modifier
                    .padding(contentPadding)
                    .padding(15.dp)) {
                    Text(
                        text = "Добро пожаловать в раздел \"Мои Списания!\"",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        fontSize = 20.sp,
                    )
                    Text(
                        text = "В этом разделе вы можете оформить списание продукции или товара, который был поврежден или который вы решили оставить для личного использования. Для каждого списанного товара можно указать количество, цену и причину списания (для собственных нужд или утилизация).",
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        fontSize = 20.sp,
                    )
                    Text(
                        text = "Нет списаний:(\nНажмите + чтобы добавить\nили",
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
                        Text(text = "Добавить Списания!")
                    }
                }
            }
        } else {
            WriteOffInventoryList(
                viewModel = viewModel,
                itemList = itemList,
                brieflyList = brieflyList,
                onItemClick = { onItemClick(navigateId(it.id, it.idPT)) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun WriteOffInventoryList(
    viewModel: WriteOffViewModel,
    itemList: List<WriteOffTable>,
    brieflyList: List<BrieflyItemCount>,
    onItemClick: (WriteOffTable) -> Unit,
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
                WriteOffProductCard(writeOffTable = item,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onItemClick(item) })
            }
        } else {
            items(items = brieflyList) { item ->
                BrieflyCountCard(
                    viewModel = viewModel,
                    product = item,
                    modifier = Modifier
                        .padding(8.dp),
                    onItemClick = onItemClick)
            }
        }
    }
}


@Composable
fun BrieflyCountCard(
    viewModel: WriteOffViewModel,
    product: BrieflyItemCount,
    onItemClick: (WriteOffTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

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
                    text = "${formatter(product.count)} ${product.suffix}",
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
            WriteOffProductCard(writeOffTable = it,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onItemClick(it) })
        }
    }
}


@Composable
fun WriteOffProductCard(
    writeOffTable: WriteOffTable,
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

            val image = if (writeOffTable.status == 0) {
                R.drawable.baseline_cottage_24
            } else {
                R.drawable.baseline_delete_24
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "delete",
                        modifier = Modifier
                            .padding(6.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text(
                            text = writeOffTable.title,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(6.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        if (writeOffTable.note != "") {
                            Text(
                                text = "Примечание: ${writeOffTable.note}",
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(vertical = 3.dp, horizontal = 6.dp)
                            )
                        }
                        Text(
                            text = "Дата: ${
                                String.format(
                                    "%02d.%02d.%d",
                                    writeOffTable.day,
                                    writeOffTable.mount,
                                    writeOffTable.year
                                )
                            }",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(vertical = 3.dp, horizontal = 6.dp)
                        )
                    }
                }
            }
            Text(
                text = "${formatter(writeOffTable.count)} ${writeOffTable.suffix}",
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


//@Preview()
//@Composable
//fun Card() {
//    SaleProductCard(
//        saleTable = SaleTable(
//            0,
//            "Мясо Коровы",
//            150.50,
//            25,
//            12,
//            2025,
//            "0",
//            "кг",
//            "Животноводство",
//            "Борька",
//            "Тетя Надя",
//            1
//        )
//    )
//}

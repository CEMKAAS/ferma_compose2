package com.zaroslikov.fermacompose2.ui.expenses

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter

object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (navigateId) -> Unit,
    navigateToItem: (Int) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val idProject = viewModel.itemId

    val coroutineScope = rememberCoroutineScope()

    val showBottomSheetFilter = remember { mutableStateOf(false) }

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
                TopAppBarFerma(
                    title = "Мои Покупки",
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
            ExpensesBody(
                itemList = homeUiState.itemList,
                onItemClick = navigateToItemUpdate,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding,
                showBottomFilter = showBottomSheetFilter,
                navigateToItemAdd = { navigateToItem(idProject) }
            )
        }
    }
}

@Composable
private fun ExpensesBody(
    itemList: List<ExpensesTable>,
    onItemClick: (navigateId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBottomFilter: MutableState<Boolean>,
    navigateToItemAdd:() ->Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemList.isEmpty()) {
            Column(modifier = modifier
                .padding(contentPadding)
                .padding(15.dp).verticalScroll(rememberScrollState())) {
                Text(
                    text = "Добро пожаловать в раздел \"Мои Покупки!\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "В этом разделе Вы можете добавлять покупки, которые покупаете для Вашей фермы и не только! Каждой покупки можно назначить кол-во, цену и категорию.",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    fontSize = 20.sp,
                )
                Text(
                    text = "Сейчас нет покупок:(\nНажмите + чтобы добавить\nили",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                )
                Button(
                    onClick = navigateToItemAdd, modifier = Modifier
                        .padding(bottom = 20.dp)

                ) {
                    Text(text = "Добавить Покупку!")
                }
            }
        } else {
            ExpensesList(
                itemList = itemList,
                onItemClick = { onItemClick(navigateId(it.id, it.idPT)) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun ExpensesList(
    itemList: List<ExpensesTable>,
    onItemClick: (ExpensesTable) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = itemList, key = { it.id }) { item ->
            ExpensesCard(expensesTable = item,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
fun ExpensesCard(
    expensesTable: ExpensesTable,
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
                    text = expensesTable.title,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                if (expensesTable.category == "Без категории" || expensesTable.category == "") {

                } else{
                    Text(
                        text = "Категория: ${expensesTable.category}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                if (expensesTable.note != "") {
                    Text(
                        text = "Примечание: ${expensesTable.note}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                Text(
                    text = "Дата: ${
                        String.format(
                            "%02d.%02d.%d",
                            expensesTable.day,
                            expensesTable.mount,
                            expensesTable.year
                        )
                    }",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 3.dp, horizontal = 6.dp)
                )
            }
            Text(
                text = "${formatter(expensesTable.count)} ${expensesTable.suffix}\n за\n${formatter(expensesTable.priceAll)} ₽",
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
//    AddProductCard(
//        addProduct = AddTable(
//            0,
//            "Мясо Коровы",
//            150.50,
//            25,
//            12,
//            2025,
//            "0",
//            1,
//            "кг",
//            "Животноводство",
//            "Борька"
//        )
//    )
//}

//
//data class navigateId(
//    val id: Int,
//    val idPT: Int
//
//)
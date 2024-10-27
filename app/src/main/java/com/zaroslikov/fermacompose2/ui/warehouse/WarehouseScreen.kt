package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFermaWarehouse
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.home.AddTableInsert
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.formatter

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
    viewModel: WarehouseViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
                1,//ToDo 3
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
            WarehouseBody(
                itemList = homeUiState.itemList,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding,
                showBottomFilter = showBottomSheetFilter,
                navigationToAnalysis = {
                    navigationToAnalysis(
                        AnalysisNav(idProject = idProject, name = it)
                    )

                }
            )
        }
    }
}

@Composable
private fun WarehouseBody(
    itemList: List<WarehouseData>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBottomFilter: MutableState<Boolean>,
    navigationToAnalysis: (String) -> Unit
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
                    text = "Сейчас склад пустой:(\nДобавьте товар в разделе \"Мои Товары\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                )
            }
        } else {
            WarehouseInventoryList(
                itemList = itemList,
                contentPadding = contentPadding,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_small)
                ),
                navigationToAnalysis = navigationToAnalysis
            )
        }

        if (showBottomFilter.value) {
//            FilterProductSheet(
//                showBottom = showBottomFilter
//            )
        }

    }
}

@Composable
private fun WarehouseInventoryList(
    itemList: List<WarehouseData>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            Text(
                text = "Сейчас на складе:",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
        items(items = itemList) { item ->
            if (!item.ResultCount.toString().contains("-")) {
                WarehouseProductCard(
                    warehouseProduct = item,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navigationToAnalysis(item.Title.replace("/", "-"))}
                )
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


data class WarehouseData(
    val Title: String,
    val ResultCount: Double,
    val suffix: String
)

data class AnalysisNav(
    val idProject: Int,
    val name: String
)
package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.water.BrieflyItemCount
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CircularProgress
import com.zaroslikov.fermacompose2.ui.composeElement.FloatButton
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndText
import com.zaroslikov.fermacompose2.ui.composeElement.MessageNoData
import com.zaroslikov.fermacompose2.ui.composeElement.TitleAndText
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.theme.baseline
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
    viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
                3,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarNavigation(
                    title = R.string.add_screen_title,
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = { FloatButton { navigateToItemAdd(idProject) } }

        ) { innerPadding ->

            if (isLoading)
                CircularProgress(
                    modifier = modifier,
                    paddingValues = innerPadding
                )
            else
                AddContainer(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = innerPadding,
                    itemList = homeUiState.itemList,
                    brieflyList = brieflyUiState.itemList,
                    viewModel = viewModel,
                    onItemClick = navigateToItemUpdate,
                    navigateToItemAdd = { navigateToItemAdd(idProject) },
                    navigationToAnalysis = {
                        navigationToAnalysis(AnalysisNav(idProject = idProject, name = it))
                        AppMetrica.reportEvent("Анализ через Продукцию")
                    }
                )

        }
    }
}


@Composable
fun AddContainer(
    viewModel: AddViewModel,
    itemList: List<AddTable>,
    brieflyList: List<BrieflyItemCount>,
    onItemClick: (navigateId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    navigateToItemAdd: () -> Unit,
    navigationToAnalysis: (String) -> Unit,
) {

    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            brieflyList = brieflyList,
            viewModel = viewModel,
            onItemClick = { onItemClick(navigateId(it.id, it.idPT)) },
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
            navigationToAnalysis = navigationToAnalysis
        )
    else MessageNoData(
        modifier = modifier,
        paddingValues = contentPadding,
        onClick = navigateToItemAdd
    )

}


@Composable
fun InventoryList(
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
                title = if (details) "Кратко" else "Подробно"
            )
        }

        if (details) {
            items(items = itemList, key = { it.id }) { item ->
                AddProductCard(addProduct = item,
                    modifier = Modifier
                        .clickable { onItemClick(item) })
            }
        } else {
            items(items = brieflyList) { item ->
                BrieflyCountCard(
                    product = item,
                    modifier = Modifier,
                    viewModel = viewModel,
                    onItemClick = onItemClick,
                    navigationToAnalysis = navigationToAnalysis,
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
    navigationToAnalysis: (String) -> Unit = {},
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
                    .fillMaxWidth(0.75f)
            ) {
                Text(
                    text = product.title,
                    modifier = Modifier
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${formatter(product.count)} ${product.suffix}",
                    modifier = Modifier
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            IconButton(onClick = { navigationToAnalysis(product.title) }) {
                Icon(
                    painterResource(id = R.drawable.baseline_analytics_24),
                    contentDescription = "Анализ",
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
            AddProductCard(addProduct = it,
                modifier = Modifier
                    .clickable { onItemClick(it) })
        }
    }
}


@Composable
fun AddProductCard(
    addProduct: AddTable,
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
                modifier = Modifier.fillMaxWidth().weight(0.7f)
            ) {

                Text(
                    text = addProduct.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,

                )

                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateBuilder(
                        addProduct.day,
                        addProduct.mount,
                        addProduct.year
                    )
                )

                addProduct.category.takeUnless { it == "Без категории" || it.isEmpty() }
                    ?.let { category ->
                        IconAndText(
                            iconRes = R.drawable.baseline_format_list_bulleted_24,
                            valueString = category
                        )
                    }

                if (addProduct.animal != "")
                    IconAndText(
                        iconRes = R.drawable.baseline_pets_24,
                        valueString = addProduct.animal
                    )


                if (addProduct.note != "")
                    IconAndText(
                        iconRes = R.drawable.baseline_sticky_note_2_24,
                        valueString = addProduct.note
                    )


            }

            Text(
                text = "${addProduct.count.formatNumber()} ${addProduct.suffix}",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth().weight(0.3f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Preview
@Composable
fun AddCard() {
    AddProductCard(
        addProduct = AddTable(
            0,
            "ds",
            320.0,
            1,
            2,
            3,
            234.0,
            "шт.",
            "2321",
            1,
            "Боря",
            "231",
            1
        ), modifier = Modifier
    )
}
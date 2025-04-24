package com.zaroslikov.fermacompose2.ui.home

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.zaroslikov.fermacompose2.ui.composeElement.TextLine
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_20
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
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
    modifier: Modifier = Modifier,
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (Pair<Int, Int>) -> Unit,
    navigateToItemAdd: (Int) -> Unit,
    navigationToAnalysis: (AnalysisNav) -> Unit,
    drawerState: DrawerState,
    viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val idProject = viewModel.itemId

    val homeUiState by viewModel.homeUiState.collectAsState()
    val brieflyUiState by viewModel.brieflyUiState.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
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
                    modifier = modifier.padding(innerPadding),
                )
            else
                AddContainer(
                    modifier = modifier
                        .modifierScreen(innerPadding),
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
    modifier: Modifier = Modifier,
    viewModel: AddViewModel,
    itemList: List<AddTable>,
    brieflyList: List<BrieflyItemCount>,
    onItemClick: (Pair<Int, Int>) -> Unit,
    navigateToItemAdd: () -> Unit,
    navigationToAnalysis: (String) -> Unit,
) {

    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            brieflyList = brieflyList,
            viewModel = viewModel,
            onItemClick = { onItemClick(Pair(it.id, it.idPT)) },
            modifier = modifier,
            navigationToAnalysis = navigationToAnalysis,

            )
    else MessageNoData(
        modifier = modifier,
        onClick = navigateToItemAdd,
        titleRes = R.string.message_no_date_title_add,
        messageRes = R.string.message_no_date_message_add,
        supportRes = R.string.message_no_date_support_text_add,
        buttonRes = R.string.button_add_message_no_data
    )

}


@Composable
fun InventoryList(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel,
    itemList: List<AddTable>,
    brieflyList: List<BrieflyItemCount>,
    onItemClick: (AddTable) -> Unit,
    navigationToAnalysis: (String) -> Unit,
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

        if (details)
            items(items = itemList, key = { it.id }) { item ->
                AddProductCard(addProduct = item,
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(bottom = extraPadding.coerceAtLeast(0.dp)))
            }
        else
            items(items = brieflyList) { item ->
                BrieflyCountCard(
                    product = item,
                    viewModel = viewModel,
                    onItemClick = onItemClick,
                    navigationToAnalysis = navigationToAnalysis,
                    modifier = Modifier
                        .padding(bottom = extraPaddingResd.coerceAtLeast(0.dp))
                )
            }
    }
}

@Composable
fun BrieflyCountCard(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel,
    product: BrieflyItemCount,
    onItemClick: (AddTable) -> Unit,
    navigationToAnalysis: (String) -> Unit = {},

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
                    valueString = "${product.count.formatNumber()} ${product.suffix}",
                )
            }

            IconButton(onClick = { navigationToAnalysis(product.title) }) {
                Icon(
                    painterResource(id = R.drawable.baseline_analytics_24),
                    contentDescription = "Анализ",
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
            AddProductCard(addProduct = it,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = 0.95f
                    }
                    .clickable { onItemClick(it) }
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {

                Text(
                    modifier = Modifier.padding(start = 3.dp, bottom = 10.dp),
                    text = addProduct.title,
                    style = textBold_20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                style = textBold_20
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
            "Яйцоbvcbbvcbbvcbcbvcbbcvbcvbc",
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
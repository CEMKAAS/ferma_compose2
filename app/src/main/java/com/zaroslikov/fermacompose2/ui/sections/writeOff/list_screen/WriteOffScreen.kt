package com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.supportFun.getImageWriteOff
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.FloatButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse

//import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse

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
    navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: (Long) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: WriteOffViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val idProject = state.idPT
    val writeOffBoolean = state.writeOffBoolean

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
                6,
                idProject.toString()
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarNavigation(
                    title = R.string.write_off_screen_title,
                    scope = coroutineScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (writeOffBoolean) FloatButton { navigateToItemAdd(idProject) }
            }
        ) { innerPadding ->
            if (state.isLoading)
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
            else
                WriteOffBody(
                    modifier = modifier.modifierScreenLazy(innerPadding),
                    viewModel = viewModel,
                    itemList = state.list,
                    brieflyList = state.listBriefly,
                    onItemClick = navigateToItemUpdate,
                    navigateToItemAdd = { navigateToItemAdd(idProject) },
                    writeOffBoolean = writeOffBoolean,
                )

        }
    }
}

@Composable
private fun WriteOffBody(
    modifier: Modifier = Modifier,
    viewModel: WriteOffViewModel,
    itemList: List<DomainWriteOffTable>,
    brieflyList: List<BrieflyWriteOffDomain>,
    onItemClick: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: () -> Unit,
    writeOffBoolean: Boolean,
) {
    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            brieflyList = brieflyList,
            viewModel = viewModel,
            onItemClick = { onItemClick(Pair(it.idPT, it.id)) },
            modifier = modifier
        )
    else
        MessageNoData(
            modifier = modifier,
            onClick = if (writeOffBoolean) navigateToItemAdd else null,
            titleRes = R.string.message_no_date_title_write_off,
            messageRes = R.string.message_no_date_message_write_off,
            supportRes = if (writeOffBoolean) R.string.message_no_date_support_write_off else R.string.message_no_date_support_no_write_off,
            buttonRes = R.string.button_sale_message_no_data
        )
}

@Composable
private fun InventoryList(
    viewModel: WriteOffViewModel,
    itemList: List<DomainWriteOffTable>,
    brieflyList: List<BrieflyWriteOffDomain>,
    onItemClick: (DomainWriteOffTable) -> Unit,
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

        if (details)
            items(items = itemList, key = { it.id }) { item ->
                WriteOffProductCard(
                    writeOffTable = item,
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(bottom = extraPadding.coerceAtLeast(0.dp))
                )
            }
        else
            items(items = brieflyList) { item ->
                BrieflyCountCard(
                    viewModel = viewModel,
                    product = item,
                    onItemClick = onItemClick,
                    modifier = Modifier
                        .padding(bottom = extraPaddingResd.coerceAtLeast(0.dp)),
                )
            }
    }
}


@Composable
fun BrieflyCountCard(
    viewModel: WriteOffViewModel,
    product: BrieflyWriteOffDomain,
    onItemClick: (DomainWriteOffTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    CardField(modifier = modifier.clickable {
        expanded = !expanded
    }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
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
            WriteOffProductCard(
                writeOffTable = it,
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
fun WriteOffProductCard(
    writeOffTable: DomainWriteOffTable,
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
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(
                    modifier = Modifier.padding(start = 3.dp, bottom = 10.dp),
                    text = writeOffTable.title,
                    style = textBold_20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateBuilder(
                        writeOffTable.day,
                        writeOffTable.month,
                        writeOffTable.year
                    )
                )
                if (writeOffTable.note != "") {
                    IconAndText(
                        iconRes = R.drawable.baseline_sticky_note_2_24,
                        valueString = writeOffTable.note
                    )
                }
            }
            Row(
                modifier = Modifier.weight(0.4f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = getImageWriteOff(writeOffTable.status)),
                    contentDescription = "delete"
                )
                Text(
                    text = "${formatter(writeOffTable.count)} ${writeOffTable.countSuffix}",
                    textAlign = TextAlign.Center,
                    style = textBold_20
                )
            }
        }
    }
}


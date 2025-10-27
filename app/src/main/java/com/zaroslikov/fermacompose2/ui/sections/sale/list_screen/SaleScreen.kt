package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew2
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigation
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.AddListIntent
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.BrieflyBottomSheetAdd
import com.zaroslikov.fermacompose2.ui.start.DrawerNavigation
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import com.zaroslikov.fermacompose2.ui.start.dateBuilder
import com.zaroslikov.fermacompose2.ui.start.formatNumber

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
            floatingActionButton = {
                NeonGlowFab {
//                    viewModel.onIntent(AddListIntent.OpenBottomSheetEntry(true))
                }
            }
        ) { innerPadding ->
            if (state.isLoading)
                CircularProgress(
                    modifier = modifier.padding(innerPadding),
                )
            else
                Column {  }
               /* SaleContainer(
                    modifier = modifier
                        .modifierScreenLazy(innerPadding),
                    searchText = state.textSearch,
                    itemList = state.list,
                    searchList = searchList,
                    brieflyList = searchList2,
                    onInsertClick = {
                        viewModel.onIntent(
                            AddListIntent.OpenBottomSheetEntry(true)
                        )
                    },
                    onEditClick = {
                        viewModel.onIntent(
                            AddListIntent.OpenBottomSheetEntry(true, it)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(AddListIntent.Delete(it)) },
                    onSearchChange = { viewModel.onIntent(AddListIntent.SearchChanged(it)) },
                    onDetailsClick = {
                        viewModel.onIntent(
                            AddListIntent.OpenBottomSheetGroup(true, it)
                        )
                    }
                )*/
/*
            if (state.openBottomSheetGroup)
                BrieflyBottomSheetAdd(
                    list = state.list,
                    titleProduct = state.currentBriefly.title,
                    count = state.currentBriefly.count,
                    suffix = state.currentBriefly.suffix,
                    countEntry = state.currentBriefly.rowCount,
                    onDismissRequest = { viewModel.onIntent(AddListIntent.OpenBottomSheetGroup(false)) },
                    onEditClick = {
                        viewModel.onIntent(
                            AddListIntent.OpenBottomSheetEntry(true, it)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(AddListIntent.Delete(it)) },
                )*/
        }
    }
}

/*
@Composable
fun SaleContainer(
    modifier: Modifier = Modifier,
    searchText: String,
    itemList: List<DomainSaleTable>,
    searchList: List<DomainSaleTable>,
    brieflyList: List<BrieflySaleDomain>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainSaleTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onSearchChange: (String) -> Unit,
    onDetailsClick: (BrieflySaleDomain) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        searchText = searchText,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onSearchChange = onSearchChange,
        onDetailsClick = onDetailsClick,
        detailCard = { item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                category = item.category,
                note = item.note,
                animal = "Murka",
                color = green_shamrock,
                day = item.day,
                month = item.month,
                year = item.year,
                onClick = { },
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                titleProduct = item.title,
                count = item.count,
                suffix = item.suffix,
                countEntry = item.rowCount,
                color = green_shamrock,
                colorSecondary = alabaster,
                onClick = { onDetailsClick(item) }
            )
        },
        titleRes = R.string.message_no_date_title_sale,
        messageRes = R.string.message_no_date_message_sale,
        supportRes = R.string.message_no_date_support_text_sale,
        buttonRes = R.string.button_sale_message_no_data
    )
}
*/


@Composable
fun BrieflyBottomSheetSale(
    list: List<DomainSaleTable>,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    onEditClick: (DomainSaleTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    BrieflyBottomSheetUniversal(
        list = list,
        titleProduct = titleProduct,
        count = count,
        suffix = suffix,
        countEntry = countEntry,
        onDismissRequest = onDismissRequest,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        itemCard = { product ->
            DetailProductCardNew2(
                modifier = Modifier,
                count = product.count,
                suffix = product.countSuffix,
                category = product.category,
                note = product.note,
                animal = "Mas",
                color = green_shamrock,
                day = product.day,
                month = product.month,
                year = product.year,
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) }
            )
        }
    )
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.white

object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    /* modifier: Modifier = Modifier,
     navigateToStart: () -> Unit,
     navigateToModalSheet: (DrawerNavigation) -> Unit,
     navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
     navigateToItemAdd: (Long) -> Unit,
     drawerState: DrawerState,*/
    viewModel: ExpensesViewModel = hiltViewModel()
    /* state: AddListState,
     onIntent: (AddListIntent) -> Unit*/
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(orang_1, orang_2)
    val primeColor = orang_1

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = {
                    viewModel.onIntent(ExpensesListIntent.SearchChanged(it))
                },
                onClick = { viewModel.onIntent(ExpensesListIntent.GroupClicked(it)) }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(ExpensesListIntent.OpenEntryBottomSheetByItem(true))
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            ExpensesContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                color = primeColor,
                itemList = state.list,
                searchList = state.searchList,
                brieflyList = state.searchBrieflyList,
                onEditClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenEntryBottomSheetByItem(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetGroup(true, it)
                    )
                },
                details = state.isGroup,
                priceSuffix = state.priceSuffix
            )
            if (state.isOpenEntryBottomSheet)
                ExpensesEntryBottomSheet(
                    state = state.currentProduct,
                    priceSuffix = state.priceSuffix,
                    colors = colors,
                    onIntent = viewModel::onIntent
                )
            if (state.isOpenGroupBottomSheet)
                BrieflyBottomSheetExpenses(
                    color = primeColor,
                    list = state.brieflyList,
                    titleProduct = state.currentBriefly.title,
                    count = state.currentBriefly.count,
                    suffix = state.currentBriefly.suffix,
                    countEntry = state.currentBriefly.rowCount,
                    priceSuffix = state.priceSuffix,
                    onDismissRequest = {
                        viewModel.onIntent(
                            ExpensesListIntent.OpenBottomSheetGroup(false)
                        )
                    },
                    onEditClick = {
                        viewModel.onIntent(
                            ExpensesListIntent.OpenEntryBottomSheetByItem(true, it)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete(it)) },
                )
        }
    }
}


@Composable
fun ExpensesContainer(
    modifier: Modifier = Modifier,
    details: Boolean,
    color: Color = blue_1,
    priceSuffix: Suffix,
    itemList: List<ExpensesTableUi>,
    searchList: List<ExpensesTableUi>,
    brieflyList: List<BrieflyExpensesDomain>,
    onEditClick: (ExpensesTableUi) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflyExpensesDomain) -> Unit
) {

    InventoryBody(
        modifier = modifier,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        detailCard = { index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = priceSuffix,
                category = item.category,
                note = item.note,
                animal = "Murka",
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                food = item.food,
                colors = item.colors,
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
                price = item.price,
                priceSuffix = priceSuffix,
                countEntry = item.rowCount,
                color = color,
                colorSecondary = Color(0xFFFFF7ED),
                onClick = { onDetailsClick(item) },
                icon = R.drawable.icon_sale,
            )
        },
        titleRes = R.string.message_no_data_title_expenses,
        messageRes = R.string.message_no_data_message_expenses,
        details = details,
        iconRes = R.drawable.icon_sale,
        iconColor = orang_1,
        backgroundColor = orang_3
    )
}


@Composable
fun BrieflyBottomSheetExpenses(
    list: List<ExpensesTableUi>,
    color: Color = blue_1,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    priceSuffix: Suffix,
    onEditClick: (ExpensesTableUi) -> Unit,
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
            DetailProductCardNew(
                modifier = Modifier,
                isCardField = false,
                count = product.count,
                suffix = product.countSuffix,
                price = product.price,
                priceSuffix = priceSuffix,
                category = product.category,
                note = product.note,
                animal = "Mas",
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                food = product.food,
                colors = product.colors,
                onClick = {},
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) }
            )
        }
    )
}
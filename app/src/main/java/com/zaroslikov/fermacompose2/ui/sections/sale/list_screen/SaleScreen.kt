package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.monthToResString

object SaleDestination : NavigationDestination {
    override val route = "Sale"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    /*modifier: Modifier = Modifier,
    navigateToStart: () -> Unit,
    navigateToModalSheet: (DrawerNavigation) -> Unit,
    navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: (Long) -> Unit,
    drawerState: DrawerState,*/
    /* state: AddListState,
     onIntent: (AddListIntent) -> Unit,*/
    viewModel: SaleViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(blue_1, blue_2)
    val idProject = state.idPT

    val query = state.textSearch.trim().lowercase()

    val searchList = if (query.isBlank() && !state.isGroup) state.list
    else
        state.list.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.note.lowercase().contains(query) ||
                    item.category.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    stringResource(item.countSuffix.toResId()).lowercase().contains(query)
                    "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                        .contains(query) ||
                    item.buyer?.lowercase()?.contains(query) == true ||
                    (item.priceAll ?: item.price).toString().lowercase().contains(query)
        }

    val searchList2 = if (query.isBlank() && state.isGroup) state.briefly
    else
        state.briefly.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    (item.price).toString().lowercase().contains(query)
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isGroup,
                onClick = { viewModel.onIntent(SaleListIntent.GroupClicked(it)) },
                onValueChange = { viewModel.onIntent(SaleListIntent.SearchChanged(it)) }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(SaleListIntent.OpenBottomSheetEntry(true))
            }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            SaleContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                details = state.isGroup,
                searchText = state.textSearch,
                itemList = state.list,
                searchList = searchList,
                brieflyList = searchList2,
                onInsertClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(true)
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.Delete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetGroup(true, it)
                    )
                }
            )
        if (state.openBottomSheetEntry)
            SaleEntryBottomSheet(
                modifier = Modifier,
                state = state.currentProduct,
                colors = colors,
                onIntent = viewModel::onIntent
            )
        if (state.openBottomSheetGroup)
            BrieflyBottomSheetSale(
                list = state.listBriefly,
                titleProduct = state.currentBriefly.title,
                count = state.currentBriefly.count,
                suffix = state.currentBriefly.suffix,
                countEntry = state.currentBriefly.rowCount,
                onDismissRequest = { viewModel.onIntent(SaleListIntent.OpenBottomSheetGroup(false)) },
                onEditClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.Delete(it)) },
            )
    }
}

@Composable
private fun SaleContainer(
    modifier: Modifier = Modifier,
    details: Boolean,
    color: Color = blue_1,
    searchText: String,
    itemList: List<DomainSaleTable>,
    searchList: List<DomainSaleTable>,
    brieflyList: List<BrieflySaleDomain>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainSaleTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflySaleDomain) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        details = details,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailsClick = onDetailsClick,
        detailCard = {index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = Suffix.RUBLE,
                category = item.category,
                note = item.note,
                animal = "Murka",
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                buyer = item.buyer,
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
                priceSuffix = Suffix.RUBLE,
                countEntry = item.rowCount,
                color = color,
                colorSecondary = Color(0xFFEFF6FF),
                onClick = { onDetailsClick(item) },
                icon = R.drawable.icon_sale,
            )
        },
        titleRes = R.string.message_no_date_title_sale,
        messageRes = R.string.message_no_date_message_sale,
        supportRes = R.string.message_no_date_support_text_sale,
        buttonRes = R.string.button_sale_message_no_data
    )
}


@Composable
private fun BrieflyBottomSheetSale(
    list: List<DomainSaleTable>,
    color: Color = blue_1,
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
            DetailProductCardNew(
                modifier = Modifier,
                isCardField = false,
                count = product.count,
                suffix = product.countSuffix,
                price = product.price,
                category = product.category,
                note = product.note,
                buyer = product.buyer,
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                onClick = {},
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) },
            )
        }
    )
}


@Composable
private fun SaleEntryBottomSheet(
    modifier: Modifier,
    colors: List<Color>,
    state: SaleEntryState2,
    onIntent: (SaleListIntent) -> Unit
) {
    EntryBottomSheet(
        modifier = Modifier,
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(
                SaleListIntent.OpenBottomSheetEntry(false)
            )
        },
        onInsertClick = { onIntent(SaleListIntent.Insert) },
        onUpdateClick = { onIntent(SaleListIntent.Update) }
    ) {
        OutlinedTextTitleSaleNew(
            value = state.title,
            onValueChange = {
                onIntent(SaleListIntent.TitleChanged(it))
                //TODO Обновление кол-во на складе
            },
            onValueChoice = {
                onIntent(SaleListIntent.TitleAndSuffixClicked(it.title, it.suffix))
                /* updateCountWarehouse(it.title, it.category)*/
            },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue,
        )
        OutlinedTextCountNew(
            value = state.count,
            onValueChange = {
                onIntent(SaleListIntent.CountChanged(it))
            },
            onSuffixChange = { onIntent(SaleListIntent.SuffixClicked(it)) },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product,
//                    isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
//                    warehouseList = state.warehouseList
        )
        WarehouseCountCard(
            title = state.title,
            warehouseList = state.warehouseList
        )

        OutlinedTextCategoryNew(
            value = state.category,
            onValueChange = { onIntent(SaleListIntent.CategoryChanged(it)) },
            titleList = state.pickList.categoryList,
        )
        OutlinedTextBuyerNew(
            value = state.buyer,
            onValueChange = {
                onIntent(SaleListIntent.BuyerChanged(it))
            },
            onTrailingChance = {
                onIntent(SaleListIntent.BuyerClearClicked)
            },
            list = state.pickList.buyerList,
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(SaleListIntent.NoteChanged(it)) },
        )
    }
}
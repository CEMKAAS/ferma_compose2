package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import androidx.annotation.DrawableRes
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
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.ui.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.white

object SaleDestination : NavigationDestination {
    override val route = "Sale"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    viewModel: SaleViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(blue_1, blue_2)
    val iconRes = R.drawable.icon_expenses

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = { viewModel.onIntent(SaleListIntent.SearchChanged(it)) },
                onClick = { viewModel.onIntent(SaleListIntent.GroupClicked(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchive)
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
                iconRes = iconRes,
                details = state.isGroup,
                itemList = state.list,
                searchList = state.searchList,
                brieflyList = state.briefly,
                searchBrieflyList = state.searchBrieflyList,
                isArchive = state.isArchive,
                onDetailsCardClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDetail(it)) },
                onEditClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(SaleListIntent.OpenBottomSheetGroup(it))
                }
            )
        if (state.isOpenBottomSheetEntry)
            SaleEntryBottomSheet(
                colors = colors,
                priceSuffix = state.settings.currencySuffix,
                state = state.currentProduct,
                onIntent = viewModel::onIntent
            )
        if (state.isOpenBottomSheetGroup)
            BrieflyBottomSheetSale(
                list = state.listBriefly,
                onDismissRequest = { viewModel.onIntent(SaleListIntent.OpenBottomSheetGroup(null)) },
                onEditClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(it)) },
                iconRes = iconRes,
                color = colors.first(),
                isArchive = state.isArchive,
                state = state.currentBriefly,
            )
        if (state.isOpenBottomSheetDetail)
            SaleDetailBottomSheet(
                state = state.currentDetail,
                colors = colors,
                onIntent = viewModel::onIntent,
                isArchive = state.isArchive
            )
        if (state.isOpenBottomSheetDelete)
            WarningDeleteSaleBottomSheet(
                onDismissRequest = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(null)) },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.Delete) },
                state = state.currentDetail,
                color = colors.first(),
                priceSuffix = state.settings.currencySuffix
            )
    }
}

@Composable
private fun SaleDetailBottomSheet(
    state: DomainSaleTable?,
    colors: List<Color>,
    onIntent: (SaleListIntent) -> Unit,
    isArchive: Boolean
) {
    state?.let {
        val monthText = stringResource(id = monthToResString(state.month))
        val date = dateBuilder(state.day, monthText, state.year)
        DetailSectionBottomSheet(
            title = state.title,
            count = state.count,
            countSuffix = state.countSuffix,
            price = state.price,
            priceAll = state.priceAll,
            category = state.category,
            buyer = state.buyer,
            date = date,
            note = state.note,
            iconColor = blue_1,
            animalCountId = state.animalCountId,
            boxColor = Color(0xFFEFF6FF),
            colors = colors,
            isArchive = isArchive,
            onUpdateClick = { onIntent(SaleListIntent.OpenBottomSheetEntry(true, state)) },
            onDeleteClick = { onIntent(SaleListIntent.OpenBottomSheetDelete(state.id)) },
            onDismissRequest = { onIntent(SaleListIntent.OpenBottomSheetDetail(null)) },
        )
    }
}

@Composable
private fun WarningDeleteSaleBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    state: DomainSaleTable?,
    color: Color,
    priceSuffix: Suffix
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_sale,
        supportRes = R.string.base_section_delete_support_sale,
        textButtonRes = R.string.base_section_button_delete_sale
    ) {
        state?.let { product ->
            DetailProductCardNew(
                title = product.title,
                count = product.count,
                suffix = product.countSuffix,
                price = product.priceAll ?: product.price,
                priceSuffix = priceSuffix,
                note = product.note,
                buyer = product.buyer,
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                animalCountId = product.animalCountId,
                typeProduct = product.animalCountId?.let { TypeProduct.ANIMAL },
                isArchive = true,
                isCardField = false
            )
        }
    }
}

@Composable
private fun SaleContainer(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    details: Boolean,
    color: Color = blue_1,
    isArchive: Boolean,
    itemList: List<DomainSaleTable>,
    searchList: List<DomainSaleTable>,
    brieflyList: List<BrieflyItem>,
    searchBrieflyList: List<BrieflyItem>,
    onDetailsCardClick: (Long) -> Unit,
    onEditClick: (DomainSaleTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        details = details,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList, searchBrieflyList = searchBrieflyList,
        detailCard = { index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = Suffix.RUBLE,
                note = item.note,
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                buyer = item.buyer,
                animalCountId = item.animalCountId,
                typeProduct = item.animalCountId?.let { TypeProduct.ANIMAL },
                isArchive = isArchive,
                onClick = { onDetailsCardClick(item.id) },
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                title = item.title,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                icon = iconRes,
                color = color,
                colorSecondary = Color(0xFFEFF6FF),
                onClick = { onDetailsClick(item.title) },
                price = item.price,
                pieces = item.pieces,
                rowCount = item.rowCount,
            )
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_sale,
            message = R.string.message_no_data_message_sale,
            icon = iconRes
        ),
        iconColor = blue_1,
        backgroundColor = blue_3, isArchive = isArchive
    )
}

@Composable
private fun BrieflyBottomSheetSale(
    @DrawableRes iconRes: Int,
    list: List<DomainSaleTable>,
    color: Color = blue_1,
    state: BrieflyItem?,
    isArchive: Boolean,
    onEditClick: (DomainSaleTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    state?.let { currentBriefly ->
        BrieflyBottomSheetUniversal(
            list = list,
            title = currentBriefly.title,
            price = currentBriefly.price,
            weight = currentBriefly.weight,
            linear = currentBriefly.linear,
            volume = currentBriefly.volume,
            pieces = currentBriefly.pieces,
            iconRes = iconRes,
            onDismissRequest = onDismissRequest,
            itemCard = { product ->
                DetailProductCardNew(
                    isCardField = false,
                    count = product.count,
                    suffix = product.countSuffix,
                    price = product.priceAll ?: product.price,
                    note = product.note,
                    buyer = product.buyer,
                    color = color,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    animalCountId = product.animalCountId,
                    isArchive = isArchive,
                    typeProduct = product.animalCountId?.let { TypeProduct.ANIMAL },
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product) },
                )
            }
        )
    }
}

@Composable
private fun SaleEntryBottomSheet(
    colors: List<Color>,
    priceSuffix: Suffix,
    state: SaleEntryState2,
    onIntent: (SaleListIntent) -> Unit
) {
    EntryBottomSheet(
        titleEntryRes = R.string.sale_screen_title_entry,
        titleEditRes = R.string.sale_screen_title_edit,
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                SaleListIntent.OpenBottomSheetEntry(
                    isOpen = false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = { onIntent(SaleListIntent.OpenBottomSheetEntry(false)) },
        onInsertClick = { onIntent(SaleListIntent.Insert) },
        onUpdateClick = { onIntent(SaleListIntent.Update) }
    ) {
        OutlinedTextTitleSaleNew(
            value = state.title,
            onValueChange = {
                onIntent(SaleListIntent.TitleChanged(it))
            },
            onValueChoice = {
                onIntent(SaleListIntent.TitleAndSuffixClicked(it.title, it.suffix, it.category))
            },
            category = state.saleCategory,
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
            enabled = !state.isIndicatorsValue,
        )
        if (!state.isIndicatorsValue)
            WarehouseCountCard(
                title = state.title,
                warehouseList = state.pickList.warehouseList
            )
        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(SaleListIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isError = state.error.isErrorPrice,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(SaleListIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            count = state.count,
            countSuffix = state.countSuffix,
            priceSuffix = priceSuffix,
        )
        OutlinedTextCategoryNew(
            value = state.category,
            onValueChange = { onIntent(SaleListIntent.CategoryChanged(it)) },
            titleList = state.pickList.categoryList,
        )
        if (!state.isIndicatorsValue)
            OutlinedTextDateNew(
                value = state.date,
                onValueChange = { onIntent(SaleListIntent.DateClicked(it)) }
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
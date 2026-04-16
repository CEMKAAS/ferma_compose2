package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixWeightDayList
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.ui.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedWriteOffStatus
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.white

object WriteOffDestination : NavigationDestination {
    override val route = "WriteOff"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteOffScreen(viewModel: WriteOffViewModel = hiltViewModel()) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(violet_1, violet_2)
    val primeColor = violet_1
    val priceSuffix = state.settings.currencySuffix
    val writeOffBoolean = state.writeOffBoolean
    val iconRes = R.drawable.baseline_edit_note_24

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = { viewModel.onIntent(WriteOffListIntent.SearchChanged(it)) },
                onClick = { viewModel.onIntent(WriteOffListIntent.GroupClicked(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchive && writeOffBoolean) NeonGlowFab(colors = colors) {
                viewModel.onIntent(WriteOffListIntent.OpenBottomSheetEntry(true))
            }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else WriteOffContainer(
            modifier = Modifier
                .modifierScreenLazy(innerPadding),
            itemList = state.list,
            searchList = state.searchList,
            brieflyList = state.briefly,
            searchBrieflyList = state.searchBrieflyList,
            isArchive = state.isArchive,
            onDetailsCardClick = { viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDetail(it)) },
            onEditClick = {
                viewModel.onIntent(
                    WriteOffListIntent.OpenBottomSheetEntry(true, it)
                )
            },
            onDeleteClick = { viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDelete(it)) },
            onDetailsClick = {
                viewModel.onIntent(
                    WriteOffListIntent.OpenBottomSheetGroup(it)
                )
            },
            color = primeColor,
            details = state.isGroup,
            priceSuffix = priceSuffix,
            iconRes = iconRes,
            writeOffBoolean = writeOffBoolean
        )
        if (state.isOpenEntryBottomSheet)
            WriteOffEntryBottomSheet(
                colors = colors,
                priceSuffix = priceSuffix,
                state = state.currentProduct,
                onIntent = viewModel::onIntent,
            )
        if (state.isOpenGroupBottomSheet)
            BrieflyBottomSheetWriteOff(
                iconRes = iconRes,
                color = primeColor,
                state = state.currentBriefly,
                list = state.listBriefly,
                priceSuffix = priceSuffix,
                isArchive = state.isArchive,
                onDismissRequest = {
                    viewModel.onIntent(WriteOffListIntent.OpenBottomSheetGroup(null))
                },
                onEditClick = {
                    viewModel.onIntent(
                        WriteOffListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDelete(it)) },
            )
        if (state.isOpenBottomSheetDetail)
            WriteOffDetailBottomSheet(
                state = state.currentDetail,
                colors = colors,
                onIntent = viewModel::onIntent,
                isArchive = state.isArchive
            )
        if (state.isOpenBottomSheetDelete)
            WarningDeleteWriteOffBottomSheet(
                state = state.currentDetail,
                color = colors.first(),
                priceSuffix = priceSuffix,
                onDismissRequest = {
                    viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDelete(null))
                },
                onDeleteClick = { viewModel.onIntent(WriteOffListIntent.Delete) }
            )
    }
}

@Composable
private fun WriteOffDetailBottomSheet(
    state: DomainWriteOffTable?,
    colors: List<Color>,
    isArchive: Boolean,
    onIntent: (WriteOffListIntent) -> Unit
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
            category = state.category, //TODO
            date = date,
            note = state.note,
            statusWriteOff = state.status,
            animalCountId = state.animalCountId,
            iconColor = violet_1,
            boxColor = Color(0xFFFAF5FF),
            colors = colors,
            isArchive = isArchive,
            onUpdateClick = { onIntent(WriteOffListIntent.OpenBottomSheetEntry(true, state)) },
            onDeleteClick = { onIntent(WriteOffListIntent.OpenBottomSheetDelete(state.id)) },
            onDismissRequest = { onIntent(WriteOffListIntent.OpenBottomSheetDetail(null)) }
        )
    }
}

@Composable
private fun WarningDeleteWriteOffBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    state: DomainWriteOffTable?,
    color: Color,
    priceSuffix: Suffix
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_write_off,
        supportRes = R.string.base_section_delete_support_write_off,
        textButtonRes = R.string.base_section_button_delete_write_off
    ) {
        state?.let { product ->
            DetailProductCardNew(
                title = product.title,
                count = product.count,
                suffix = product.countSuffix,
                price = product.priceAll ?: product.price,
                statusWriteOff = product.status,
                note = product.note,
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                priceSuffix = priceSuffix,
                animalCountId = product.animalCountId,
                isArchive = true,
                isCardField = false,
            )
        }
    }
}

@Composable
private fun WriteOffContainer(
    modifier: Modifier = Modifier,
    @StringRes iconRes: Int,
    color: Color,
    details: Boolean,
    priceSuffix: Suffix,
    isArchive: Boolean,
    itemList: List<DomainWriteOffTable>,
    searchList: List<DomainWriteOffTable>,
    brieflyList: List<BrieflyItem>,
    searchBrieflyList: List<BrieflyItem>,
    onEditClick: (DomainWriteOffTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit,
    onDetailsCardClick: (Long) -> Unit,
    writeOffBoolean: Boolean
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
                priceSuffix = priceSuffix,
                statusWriteOff = item.status,
                note = item.note,
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                typeProduct = item.animalCountId?.let { TypeProduct.ANIMAL },
                animalCountId = item.animalCountId,
                isArchive = isArchive,
                onClick = { onDetailsCardClick(item.id) },
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                color = color,
                title = item.title,
                price = item.price,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                pieces = item.pieces,
                rowCount = item.rowCount,
                icon = iconRes,
                colorSecondary = violet_3,
                onClick = { onDetailsClick(item.title) })
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_write_off,
            message = R.string.message_no_data_message_write_off,
            support = if (writeOffBoolean) null else R.string.message_no_data_message_write_off_support,
            icon = iconRes
        ),
        iconColor = violet_1,
        backgroundColor = violet_3,
        isArchive = isArchive
    )
}


@Composable
private fun BrieflyBottomSheetWriteOff(
    @DrawableRes iconRes: Int,
    list: List<DomainWriteOffTable>,
    state: BrieflyItem?,
    color: Color,
    isArchive: Boolean,
    priceSuffix: Suffix,
    onDismissRequest: () -> Unit,
    onEditClick: (DomainWriteOffTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
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
                    /*category = product.category,*/
                    statusWriteOff = product.status,
                    note = product.note,
                    color = color,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    priceSuffix = priceSuffix,
                    animalCountId = product.animalCountId,
                    isArchive = isArchive,
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product) }
                )
            }
        )
    }
}


@Composable
private fun WriteOffEntryBottomSheet(
    colors: List<Color>,
    state: WriteOffEntryState2,
    onIntent: (WriteOffListIntent) -> Unit,
    priceSuffix: Suffix
) {
    EntryBottomSheet(
        titleEntryRes = R.string.write_off_screen_title_entry,
        titleEditRes = R.string.write_off_screen_title_edit,
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                WriteOffListIntent.OpenBottomSheetEntry(
                    isOpen = false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = { onIntent(WriteOffListIntent.OpenBottomSheetEntry(false)) },
        onInsertClick = { onIntent(WriteOffListIntent.Insert) },
        onUpdateClick = { onIntent(WriteOffListIntent.Update) }) {
        OutlinedWriteOffStatus(
            value = state.status,
            onValueChange = { onIntent(WriteOffListIntent.StatusClicked(it)) }
        )
        OutlinedTextTitleSaleNew(
            value = state.title,
            onValueChoice = {
                onIntent(WriteOffListIntent.TitleAndSuffix(it.title, it.suffix, it.category))
            },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue,
            isMore = true,
            category = state.writeOffCategory
        )
        OutlinedTextCountNew(
            value = state.count,
            onValueChange = {
                onIntent(WriteOffListIntent.CountChanged(it))
            },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_write_off,
            suffixList = suffixAllList,
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
                onIntent(WriteOffListIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            supportTextRes = R.string.support_text_price_write_off_all,
            supportTextResAutoCal = R.string.support_text_price_write_off_one,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(WriteOffListIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            count = state.count,
            countSuffix = state.countSuffix,
            priceSuffix = priceSuffix,
        )
        if (!state.isIndicatorsValue)
            OutlinedTextDateNew(
                value = state.date,
                onValueChange = {
                    onIntent(WriteOffListIntent.DateClicked(it))
                }
            )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = {
                onIntent(WriteOffListIntent.NoteChanged(it))
            }
        )
    }
}


package com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen

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
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixWeightDayList
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.EntryBottomSheet
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
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.start.monthToResString
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2

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
    val idProject = state.idPT

    val writeOffBoolean = state.writeOffBoolean

    val query = state.textSearch.trim().lowercase()

    val searchList = if (query.isBlank() && !state.isGroup) state.list
    else
        state.list.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.note.lowercase().contains(query) ||
                    /*  item.category.lowercase().contains(query) ||*/
                    item.count.toString().lowercase().contains(query) ||
                    stringResource(item.countSuffix.toResId()).lowercase().contains(query)
            "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                .contains(query) ||
                    (item.priceAll ?: item.price).toString().lowercase().contains(query)
        }

    val searchList2 = if (query.isBlank() && state.isGroup) state.briefly
    else
        state.briefly.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) /*||
                    (item.price).toString().lowercase().contains(query)*/
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isGroup,
                onClick = { viewModel.onIntent(WriteOffListIntent.GroupClicked(it)) },
                onValueChange = { viewModel.onIntent(WriteOffListIntent.SearchChanged(it)) }
            )
        },
        floatingActionButton = {
            if (writeOffBoolean) NeonGlowFab(colors = colors) {
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
            searchList = searchList,
            brieflyList = searchList2,
            onInsertClick = {
                viewModel.onIntent(
                    WriteOffListIntent.OpenBottomSheetEntry(true)
                )
            },
            onEditClick = {
                viewModel.onIntent(
                    WriteOffListIntent.OpenBottomSheetEntry(true, it)
                )
            },
            onDeleteClick = { viewModel.onIntent(WriteOffListIntent.Delete(it)) },
            onDetailsClick = {
                viewModel.onIntent(
                    WriteOffListIntent.OpenBottomSheetGroup(true, it)
                )
            },
            color = primeColor,
            details = state.isGroup
        )
        if (state.openBottomSheetEntry)
            WriteOffEntryBottomSheet(
                modifier = Modifier,
                state = state.currentProduct,
                colors = colors,
                onIntent = viewModel::onIntent
            )
        if (state.openBottomSheetGroup)
            BrieflyBottomSheetWriteOff(
                color = primeColor,
                list = state.listBriefly,
                titleProduct = state.currentBriefly.title,
                count = state.currentBriefly.count,
                suffix = state.currentBriefly.suffix,
                countEntry = state.currentBriefly.rowCount,
                onDismissRequest = {
                    viewModel.onIntent(
                        WriteOffListIntent.OpenBottomSheetGroup(
                            false
                        )
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        WriteOffListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(WriteOffListIntent.Delete(it)) },
            )
    }
}

@Composable
private fun WriteOffContainer(
    modifier: Modifier = Modifier,
    color: Color,
    details: Boolean,
    itemList: List<DomainWriteOffTable>,
    searchList: List<DomainWriteOffTable>,
    brieflyList: List<BrieflyWriteOffDomain>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainWriteOffTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflyWriteOffDomain) -> Unit
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
        detailCard = { item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = Suffix.RUBLE,
                /*category = item.category,*/
                statusWriteOff = item.status,
                note = item.note,
                animal = "Murka",
                color = color,
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
                price = item.price,
                priceSuffix = Suffix.RUBLE,
                countEntry = item.rowCount,
                color = color,
                colorSecondary = Color(0xFFFAF5FF),
                onClick = { onDetailsClick(item) },
                icon = R.drawable.icon_trash,
            )
        },
        titleRes = R.string.message_no_date_title_write_off,
        messageRes = R.string.message_no_date_message_write_off,
        supportRes = /*if (writeOffBoolean) R.string.message_no_date_support_write_off else*/ R.string.message_no_date_support_no_write_off,
        buttonRes = R.string.button_sale_message_no_data
    )
}


@Composable
private fun BrieflyBottomSheetWriteOff(
    list: List<DomainWriteOffTable>,
    color: Color,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    onEditClick: (DomainWriteOffTable) -> Unit,
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
                /*category = product.category,*/
                statusWriteOff = product.status,
                note = product.note,
                animal = "Mas",
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                onClick = {},
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) }
            )
        }
    )
}


@Composable
private fun WriteOffEntryBottomSheet(
    modifier: Modifier,
    colors: List<Color>,
    state: WriteOffEntryState2,
    onIntent: (WriteOffListIntent) -> Unit
) {
    EntryBottomSheet(
        modifier = Modifier,
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(
                WriteOffListIntent.OpenBottomSheetEntry(false)
            )
        },
        onInsertClick = { onIntent(WriteOffListIntent.Insert) },
        onUpdateClick = { onIntent(WriteOffListIntent.Update) }
    ) {
        OutlinedWriteOffStatus(
            value = state.status,
            onValueChange = { onIntent(WriteOffListIntent.StatusClicked(it)) }
        )
        OutlinedTextTitleSaleNew(
            value = state.title,
            onValueChoice = {
                onIntent(WriteOffListIntent.TitleAndSuffix(it.title, it.suffix))
                /* updateCountWarehouse(it.title, it.category)*/
            },
            titleList = state.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue,
            isMore = true
        )
        OutlinedTextCountNew(
            value = state.count,
            onValueChange = {
                onIntent(WriteOffListIntent.CountChanged(it))
            },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_write_off,
            suffixList = suffixWeightDayList
            /*isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
            warehouseList = state.warehouseList,*/
        )
        WarehouseCountCard(
            title = state.title,
            warehouseList = state.warehouseList
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
            priceSuffix = Suffix.RUBLE,
        )
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


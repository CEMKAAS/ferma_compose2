@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.add.list_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.supportFun.toColor
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.sections.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.start.monthToResString

/*object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    /*    modifier: Modifier = Modifier,
        navigateToStart: () -> Unit,
        navigateToModalSheet: (DrawerNavigation) -> Unit,
        navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
        navigateToItemAdd: (Long) -> Unit,
        navigationToAnalysis: (Pair<Long, String>) -> Unit,
        drawerState: DrawerState,*/
    /*state: AddListState,
    onIntent: (AddListIntent) -> Unit,*/
    viewModel: AddViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(Color(0xFF009966), Color(0xFF00A63E))
    val idProject = state.idPT

    val query = state.textSearch.trim().lowercase()

    val searchList = if (query.isBlank() && !state.isGroup) state.list
    else
        state.list.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.note.lowercase().contains(query) ||
                    item.category.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    stringResource(item.countSuffix.toResId()).lowercase().contains(query) ||
                    "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                        .contains(query)
        }


    val searchList2 = if (query.isBlank() && state.isGroup) state.briefly
    else
        state.briefly.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query)
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = { viewModel.onIntent(AddListIntent.SearchChanged(it)) },
                onClick = { viewModel.onIntent(AddListIntent.GroupClicked(it)) }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(AddListIntent.OpenBottomSheetEntry(true))
            }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AddContainer2(
                modifier = Modifier
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
                onDetailsClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetGroup(true, it)
                    )
                },
                details = state.isGroup
            )
        if (state.openBottomSheetEntry)
            AddEntryBottomSheet(
                modifier = Modifier,
                state = state.currentProduct,
                colors = colors,
                onIntent = viewModel::onIntent
            )
        if (state.openBottomSheetGroup)
            BrieflyBottomSheetAdd(
                list = state.listBriefly,
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
            )
    }
}


@Composable
fun AddContainer2(
    modifier: Modifier = Modifier,
    details: Boolean,
    searchText: String,
    itemList: List<DomainAddTable>,
    searchList: List<DomainAddTable>,
    brieflyList: List<BrieflyAddDomain>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainAddTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflyAddDomain) -> Unit
) {
    InventoryBody(
        modifier = modifier,
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
                icon = R.drawable.icon_add_product,
                color = green_shamrock,
                colorSecondary = alabaster,
                onClick = { onDetailsClick(item) },
            )
        },
        titleRes = R.string.message_no_date_title_add,
        messageRes = R.string.message_no_date_message_add,
        supportRes = R.string.message_no_date_support_text_add,
        buttonRes = R.string.button_add_message_no_data,
        details = details
    )
}

@Composable
fun BrieflyBottomSheetAdd(
    list: List<DomainAddTable>,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    onEditClick: (DomainAddTable) -> Unit,
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
                category = product.category,
                note = product.note,
                animal = "Mas",
                color = green_shamrock,
                day = product.day,
                month = product.month,
                year = product.year,
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) },
                onClick = { }
            )
        }
    )
}

@Composable
fun AddEntryBottomSheet(
    modifier: Modifier,
    state: AddEntryState2,
    colors: List<Color>,
    onIntent: (AddListIntent) -> Unit
) {
    EntryBottomSheet(
        modifier = Modifier,
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(
                AddListIntent.OpenBottomSheetEntry(false)
            )
        },
        onInsertClick = { onIntent(AddListIntent.Insert) },
        onUpdateClick = { onIntent(AddListIntent.Update) },
    ) {
        OutlinedTextTitleAddNew(
            value = state.title,
            onValueChange = { onIntent(AddListIntent.TitleChanged(it)) },
            onValueChangeSuffix = { onIntent(AddListIntent.TitleAndSuffix(it)) },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            drawableRes = R.drawable.icon_add_product
        )
        OutlinedTextCountNew(
            value = state.count,
            onValueChange = {
                onIntent(AddListIntent.CountChanged(it))
            },
            onSuffixChange = { onIntent(AddListIntent.SuffixClicked(it)) },
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
            onValueChange = { onIntent(AddListIntent.CategoryChanged(it)) },
            titleList = state.pickList.categoryList,
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AddListIntent.Date(it)) }
        )
        if (state.pickList.animalList.isNotEmpty())
            OutlinedTextAnimalNew(
                value = state.animal,
                onValueChange = { onIntent(AddListIntent.Animal(it)) },
                selectedAnimalIndex = state.selectedAnimalIndex,
                onClickClear = { onIntent(AddListIntent.AnimalClear(it)) },
                animalList = state.pickList.animalList,
            )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AddListIntent.NoteChanged(it)) },
        )
    }
}


enum class Page {
    WRITE_OFF, SALE, ADD, EXPENSES, ANIMAL
}
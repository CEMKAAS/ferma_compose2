@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
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
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen.ExpensesListIntent

/*object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    /* navigationToAnalysis: (Pair<Long, String>) -> Unit */
    viewModel: AddViewModel = hiltViewModel(),
    navigationToAnalysis: (Triple<Long, String, Suffix>) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(price_green, green_shamrock)
    val idProject = state.idPT

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
                itemList = state.list,
                searchList = state.searchList,
                brieflyList = state.searchBrieflyList,
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
                onAnalysisClick = { navigationToAnalysis(Triple(idProject, it.first, it.second)) }
            )
    }
}


@Composable
fun AddContainer2(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<DomainAddTable>,
    searchList: List<DomainAddTable>,
    brieflyList: List<BrieflyAddDomain>,
    onEditClick: (DomainAddTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflyAddDomain) -> Unit
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
        titleRes = R.string.message_no_data_title_add,
        messageRes = R.string.message_no_data_message_add,
        details = details,
        iconRes = R.drawable.icon_add_product,
        iconColor = green_shamrock,
        backgroundColor = green_g_1
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
    onAnalysisClick: (Pair<String, Suffix>) -> Unit = {},
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
        onAnalysisClick = { onAnalysisClick(titleProduct to suffix) },
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
    state: AddEntryState2,
    colors: List<Color>,
    onIntent: (AddListIntent) -> Unit
) {
    EntryBottomSheet(
        titleEntryRes = R.string.add_screen_title_entry,
        titleEditRes = R.string.add_screen_title_edit,
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                AddListIntent.OpenBottomSheetEntry(
                    isOpen = false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AddListIntent.OpenBottomSheetEntry(false))
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
        )
        WarehouseCountCard(
            title = state.title,
            warehouseList = state.pickList.warehouseList
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
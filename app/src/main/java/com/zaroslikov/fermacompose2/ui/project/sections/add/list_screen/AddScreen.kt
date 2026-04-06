@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.ui.dateBuilder
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
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.white

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
    val iconRes = R.drawable.icon_add_product

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
                iconRes = iconRes,
                itemList = state.list,
                searchList = state.searchList,
                brieflyList = state.searchBrieflyList,
                details = state.isGroup,
                onDetailsCardClick = { viewModel.onIntent(AddListIntent.OpenBottomSheetDetail(it)) },
                onEditClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(AddListIntent.Delete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetGroup(it)
                    )
                })
        if (state.openBottomSheetEntry)
            AddEntryBottomSheet(
                state = state.currentProduct,
                colors = colors,
                onIntent = viewModel::onIntent
            )
        if (state.openBottomSheetGroup)
            BrieflyBottomSheetAdd(
                iconRes = iconRes,
                list = state.listBriefly,
                state = state.currentBriefly,
                onDismissRequest = { viewModel.onIntent(AddListIntent.OpenBottomSheetGroup(null)) },
                onEditClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(AddListIntent.Delete(it)) },
                onAnalysisClick = { navigationToAnalysis(Triple(idProject, it.first, it.second)) }
            )
        if (state.isOpenBottomSheetDetail)
            AddDetailBottomSheet(
                state = state.currentDetail,
                colors = colors,
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
private fun AddDetailBottomSheet(
    state: DomainAddItemDto?,
    colors: List<Color>,
    onIntent: (AddListIntent) -> Unit
) {
    state?.let {
        val monthText = stringResource(id = monthToResString(state.month))
        val date = dateBuilder(state.day, monthText, state.year)
        DetailSectionBottomSheet(
            title = state.title,
            count = state.count,
            countSuffix = state.countSuffix,
            category = state.category,
            date = date,
            animal = state.nameAnimal,
            note = state.note,
            animalId = state.animalCountId,
            iconColor = green_shamrock,
            boxColor = alabaster,
            colors = colors,
            onUpdateClick = { onIntent(AddListIntent.OpenBottomSheetEntry(true, state)) },
            onDeleteClick = { onIntent(AddListIntent.Delete(state.id)) },
            onDismissRequest = { onIntent(AddListIntent.OpenBottomSheetDetail(null)) },
        )
    }
}


@Composable
fun AddContainer2(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    details: Boolean,
    itemList: List<DomainAddItemDto>,
    searchList: List<DomainAddItemDto>,
    brieflyList: List<BrieflyItem>,
    onDetailsCardClick: (Long) -> Unit,
    onEditClick: (DomainAddItemDto) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit
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
                animal = item.nameAnimal,
                color = green_shamrock,
                day = item.day,
                month = item.month,
                year = item.year,
                colors = item.animalCountId?.let { listOf(white, red_15) },
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
                color = green_shamrock,
                colorSecondary = alabaster,
                onClick = { onDetailsClick(item.title) },
                price = item.price,
                pieces = item.pieces,
                rowCount = item.rowCount,
            )
        },
        titleRes = R.string.message_no_data_title_add,
        messageRes = R.string.message_no_data_message_add,
        details = details,
        iconRes = iconRes,
        iconColor = green_shamrock,
        backgroundColor = green_g_1
    )
}

@Composable
fun BrieflyBottomSheetAdd(
    @DrawableRes iconRes: Int,
    list: List<DomainAddItemDto>,
    state: BrieflyItem?,
    onEditClick: (DomainAddItemDto) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
    onAnalysisClick: (Pair<String, Suffix>) -> Unit = {},
) {
    state?.let { currentBriefly ->
        BrieflyBottomSheetUniversal(
            list = list,
            iconRes = iconRes,
            title = currentBriefly.title,
            price = null,
            weight = currentBriefly.weight,
            linear = currentBriefly.linear,
            volume = currentBriefly.volume,
            pieces = currentBriefly.pieces,
            onDismissRequest = onDismissRequest,
            onAnalysisClick = { onAnalysisClick(currentBriefly.title to it) }, //TODO
            itemCard = { product ->
                DetailProductCardNew(
                    modifier = Modifier,
                    isCardField = false,
                    count = product.count,
                    suffix = product.countSuffix,
                    category = product.category,
                    note = product.note,
                    animal = product.nameAnimal,
                    color = green_shamrock,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product) },
                    onClick = { }
                )
            })
    }
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
        if (!state.isIndicatorsValue)
            WarehouseCountCard(
                title = state.title,
                warehouseList = state.pickList.warehouseList
            )
        if (!state.isIndicatorsValue)
            OutlinedTextCategoryNew(
                value = state.category,
                onValueChange = { onIntent(AddListIntent.CategoryChanged(it)) },
                titleList = state.pickList.categoryList,
            )
        if (!state.isIndicatorsValue)
            OutlinedTextDateNew(
                value = state.date,
                onValueChange = { onIntent(AddListIntent.Date(it)) }
            )
        if (!state.isIndicatorsValue)
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
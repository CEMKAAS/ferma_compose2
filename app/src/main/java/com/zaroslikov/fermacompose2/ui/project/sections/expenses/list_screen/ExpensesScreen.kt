@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen


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
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody

object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(orang_1, orang_2)
    val primeColor = orang_1
    val iconRes = R.drawable.icon_expenses

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = {
                    viewModel.onIntent(ExpensesListIntent.SearchChanged(it))
                },
                onClick = { viewModel.onIntent(ExpensesListIntent.GroupClicked(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchive)
                NeonGlowFab(colors = colors) {
                    viewModel.onIntent(ExpensesListIntent.OpenEntryBottomSheetByItem(true))
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            ExpensesContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                color = primeColor,
                iconRes = iconRes,
                itemList = state.list,
                searchList = state.searchList,
                brieflyList = state.briefly,
                searchBrieflyList = state.searchBrieflyList,
                priceSuffix = state.settings.currencySuffix,
                details = state.isGroup,
                isArchive = state.isArchive,
                onDetailsCardClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetDetail(
                            it
                        )
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenEntryBottomSheetByItem(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(ExpensesListIntent.OpenBottomSheetDelete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetGroup(it)
                    )
                }
            )

        if (state.isOpenEntryBottomSheet)
            ExpensesEntryBottomSheet(
                colors = colors,
                state = state.currentProduct,
                priceSuffix = state.settings.currencySuffix,
                onIntent = viewModel::onIntent
            )
        if (state.isOpenGroupBottomSheet)
            BrieflyBottomSheetExpenses(
                color = primeColor,
                iconRes = iconRes,
                list = state.brieflyList,
                state = state.currentBriefly,
                priceSuffix = state.settings.currencySuffix,
                isArchive = state.isArchive,
                onDismissRequest = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetGroup(null)
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenEntryBottomSheetByItem(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(ExpensesListIntent.OpenBottomSheetDelete(it)) },
            )
    }
    if (state.isOpenBottomSheetDetail)
        ExpensesDetailBottomSheet(
            state = state.currentDetail,
            priceSuffix = state.settings.currencySuffix,
            colors = colors,
            onIntent = viewModel::onIntent,
            isArchive = state.isArchive
        )
    if (state.isOpenBottomSheetDelete)
        WarningDeleteExpensesBottomSheet(
            onDismissRequest = { viewModel.onIntent(ExpensesListIntent.OpenBottomSheetDelete(null)) },
            onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete) },
            state = state.currentDetail,
            color = colors.first(),
            priceSuffix = state.settings.currencySuffix
        )
}


@Composable
private fun ExpensesDetailBottomSheet(
    state: ExpensesTableUi?,
    priceSuffix: Suffix,
    colors: List<Color>,
    isArchive: Boolean,
    onIntent: (ExpensesListIntent) -> Unit
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
            priceSuffix = priceSuffix,
            category = state.category,
            date = date,
            note = state.note,
            food = state.food,
            animalVaccinationId = state.animalVaccinationId,
            animalCountId = state.animalCountId,
            iconColor = orang_1,
            boxColor = Color(0xFFFFF7ED),
            colors = colors,
            onUpdateClick = {
                onIntent(
                    ExpensesListIntent.OpenEntryBottomSheetByItem(
                        true,
                        state
                    )
                )
            },
            isArchive = isArchive,
            onDeleteClick = { onIntent(ExpensesListIntent.OpenBottomSheetDelete(state.id)) },
            onDismissRequest = { onIntent(ExpensesListIntent.OpenBottomSheetDetail(null)) },
        )
    }
}


@Composable
private fun WarningDeleteExpensesBottomSheet(
    state: ExpensesTableUi?,
    color: Color,
    priceSuffix: Suffix,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_expenses,
        supportRes = R.string.base_section_delete_support_expenses,
        textButtonRes = R.string.base_section_button_delete_expenses
    ) {
        state?.let { product ->
            DetailProductCardNew(
                title = product.title,
                count = product.count,
                suffix = product.countSuffix,
                price = product.priceAll ?: product.price,
                priceSuffix = priceSuffix,
                category = product.category ?: stringResource(R.string.support_text_no_category),
                note = product.note,
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                food = product.food,
                typeProduct = product.typeProduct,
                animalCountId = product.animalCountId,
                animalVaccinationId = product.animalVaccinationId,
                isArchive = true,
                isCardField = false
            )
        }
    }
}

@Composable
fun ExpensesContainer(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    details: Boolean,
    color: Color = blue_1,
    priceSuffix: Suffix,
    itemList: List<ExpensesTableUi>,
    searchList: List<ExpensesTableUi>,
    brieflyList: List<BrieflyItem>,
    searchBrieflyList: List<BrieflyItem>,
    onDetailsCardClick: (Long) -> Unit,
    onEditClick: (ExpensesTableUi) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit,
    isArchive: Boolean
) {
    InventoryBody(
        modifier = modifier,
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
                category = item.category,
                note = item.note,
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                food = item.food,
                typeProduct = item.typeProduct,
                animalCountId = item.animalCountId,
                animalVaccinationId = item.animalVaccinationId,
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
                colorSecondary = Color(0xFFFFF7ED),
                icon = iconRes,
                title = item.title,
                price = item.price,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                pieces = item.pieces,
                rowCount = item.rowCount,
                onClick = { onDetailsClick(item.title) },
            )
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_expenses,
            message = R.string.message_no_data_message_expenses,
            icon = iconRes
        ),
        details = details,
        iconColor = orang_1,
        backgroundColor = orang_3, isArchive = isArchive
    )
}


@Composable
fun BrieflyBottomSheetExpenses(
    @DrawableRes iconRes: Int,
    list: List<ExpensesTableUi>,
    state: BrieflyItem?,
    color: Color = blue_1,
    priceSuffix: Suffix,
    onEditClick: (ExpensesTableUi) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
    isArchive: Boolean,
) {
    state?.let { currentBriefly ->
        BrieflyBottomSheetUniversal(
            list = list,
            iconRes = iconRes,
            title = currentBriefly.title,
            price = currentBriefly.price,
            weight = currentBriefly.weight,
            linear = currentBriefly.linear,
            volume = currentBriefly.volume,
            pieces = currentBriefly.pieces,
            onDismissRequest = onDismissRequest,
            itemCard = { product ->
                DetailProductCardNew(
                    isCardField = false,
                    count = product.count,
                    suffix = product.countSuffix,
                    price = product.priceAll ?: product.price,
                    priceSuffix = priceSuffix,
                    category = product.category,
                    note = product.note,
                    color = color,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    food = product.food,
                    typeProduct = product.typeProduct,
                    isArchive = isArchive,
                    animalCountId = product.animalCountId,
                    animalVaccinationId = product.animalVaccinationId,
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product) }
                )
            }
        )
    }
}
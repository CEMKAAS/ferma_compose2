@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.sale.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


object SaleEntryDestination : NavigationDestination {
    override val route = "SaleEntry"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun SaleEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SaleEntryViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.navigation
    val state = viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.sale_screen_title,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        SaleEntryContainerProduct(
            modifier = Modifier.modifierScreen(innerPadding),
            state = state.value,
            updateCountWarehouse = viewModel::updateWarehouseUiState,
            onIntent = viewModel::onIntent
        )
    }
}

@Composable
fun SaleEntryContainerProduct(
    modifier: Modifier,
    state: SaleEntryState,
    updateCountWarehouse: (String, Category) -> Unit,
    onIntent: (SaleEntryIntent) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextTitleSale(
            value = state.title,
            onValueChange = {
                onIntent(SaleEntryIntent.TitleChanged(it))
                //TODO Обновление кол-во на складе
            },
            onValueChoice = {
                onIntent(SaleEntryIntent.TitleAndSuffixClicked(it.title, it.suffix))
                updateCountWarehouse(it.title, it.category)
            },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash
        )
        OutlinedTextCount2(
            value = state.count,
            onValueChange = {
                onIntent(SaleEntryIntent.CountChanged(it))
            },
            onSuffixChange = {
                onIntent(SaleEntryIntent.SuffixClicked(it))
            },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_sale,
            isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
            warehouseList = state.warehouseList,
        )
        OutlinedPriceInput(
            price = state.price,
            onPriceChange = {
                onIntent(SaleEntryIntent.PriceChanged(it))
            },
            count = state.priceAll,
            isError = state.error.isErrorPrice,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(SaleEntryIntent.AutoPriceClicked(it))
            },
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
            isManyCount = true,
            isNecessarily = true
        )
        OutlinedTextCategory(
            value = state.category,
            onValueChange = {
                onIntent(SaleEntryIntent.CategoryChanged(it))
            },
            titleList = state.pickList.categoryList,
        )
        OutlinedTextDateEdit(
            value = state.date,
            onValueChange = {
                onIntent(SaleEntryIntent.DateClicked(it))
            }
        )
        OutlinedTextBuyer(
            value = state.buyer,
            onValueChange = {
                onIntent(SaleEntryIntent.BuyerChanged(it))
            },
            onTrailingChance = {
                onIntent(SaleEntryIntent.BuyerClearClicked)
            },
            list = state.pickList.buyerList,
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = {
                onIntent(SaleEntryIntent.NoteChanged(it))
            },
        )
        ButtonPanel(
            isEntry = state.isEntry,
            entryButton = R.string.button_sale,
            onClickInsert = { onIntent(SaleEntryIntent.Insert) },
            onClickUpdate = { onIntent(SaleEntryIntent.Update) },
            onClickDelete = { onIntent(SaleEntryIntent.Delete) }
        )
    }
}

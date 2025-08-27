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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.Category
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
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
    val eventFlow = viewModel.eventFlow
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
            state = viewModel.saleUiState,
            onValueChange = viewModel::updateUiState,
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
            updateCountWarehouse = viewModel::updateWarehouseUiState
        )
    }
}

@Composable
fun SaleEntryContainerProduct(
    modifier: Modifier,
    state: SaleEntryState,
    updateCountWarehouse: (String, Category) -> Unit,
    onValueChange: (SaleEntryState) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Column(modifier = modifier) {
        OutlinedTextTitleSale(
            value = state.title,
            onValueChange = {
                onValueChange(state.updateTitle(it))
                //TODO Обновление кол-во на складе
            },
            onValueChoice = {
                onValueChange(state.updateTitleAndSuffix(it.first, it.second))
                updateCountWarehouse(it.first, it.third)
            },
            titleList = state.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash
        )
        OutlinedTextCount2(
            value = state.count,
            onValueChange = {
                onValueChange(state.updateCount(it))
            },
            onSuffixChange = { onValueChange(state.updateSuffix(it)) },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_sale,
            isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
            warehouseList = state.warehouseList,
        )
        OutlinedPriceInput(
            price = state.price,
            onPriceChange = {
                onValueChange(state.updatePrice(it))
            },
            count = state.priceAll,
            isError = state.error.isErrorPrice,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = { onValueChange(state.updateIsAutoPrice(it)) },
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
            isManyCount = true,
            isNecessarily = true
        )
        OutlinedTextCategory(
            value = state.category,
            onValueChange = { onValueChange(state.updateCategory(it)) },
            titleList = state.categoryList,
        )
        OutlinedTextDateEdit(
            value = state.date,
            onValueChange = { onValueChange(state.updateDate(it)) }
        )
        OutlinedTextBuyer(
            value = state.buyer,
            onValueChange = { onValueChange(state.updateBuyer(it)) },
            onTrailingChance = { onValueChange(state.updateBuyerClear()) },
            list = state.buyerList,
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onValueChange(state.updateNote(it)) },
        )
        ButtonPanel(
            state = state,
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}

@Composable
private fun ButtonPanel(
    state: SaleEntryState,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    if (state.isEntry)
        ButtonStandart(
            intRes = R.string.button_sale,
            onClick = {
                focusManager.clearFocus()
                onClickInsert()
            }
        )
    else {
        ButtonRefresh {
            focusManager.clearFocus()
            onClickUpdate()
        }
        ButtonDelete { onClickDelete() }
    }
}

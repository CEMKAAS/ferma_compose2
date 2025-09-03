@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.writeOff.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.elements.RadioButtonWriteOff
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


object WriteOffEntryDestination : NavigationDestination {
    override val route = "WriteOffEntry"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}


@Composable
fun WriteOffEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: WriteOffEntryViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.navigation
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                intRes = R.string.write_off_screen_title,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        WriteOffEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding),
            state = state,
            onIntent = viewModel::onIntent,
            updateCountWarehouse = viewModel::updateWarehouseUiState
        )
    }
}


@Composable
fun WriteOffEntryContainerProduct(
    modifier: Modifier,
    state: WriteOffEntryState,
    onIntent: (WriteOffIntent) -> Unit,
    updateCountWarehouse: (String, Category) -> Unit,
) {
    Column(modifier = modifier) {
        OutlinedTextTitleSale(
            value = state.title,
            onValueChoice = {
                onIntent(WriteOffIntent.TitleAndSuffix(it.title, it.suffix))
                updateCountWarehouse(it.title, it.category)
            },
            titleList = state.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
        )
        OutlinedTextCount2(
            value = state.count,
            onValueChange = {
                onIntent(WriteOffIntent.CountChanged(it))
            },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_write_off,
            isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
            warehouseList = state.warehouseList,
        )
        OutlinedPriceInput(
            price = state.price,
            onPriceChange = {
                onIntent(WriteOffIntent.PriceChanged(it))
            },
            count = state.priceAll,
            supportTextRes = R.string.support_text_price_write_off_all,
            supportTextResAutoCal = R.string.support_text_price_write_off_one,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(WriteOffIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
        )
        OutlinedTextDateEdit(
            value = state.date,
            onValueChange = {
                onIntent(WriteOffIntent.DateClicked(it))
            }
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = {
                onIntent(WriteOffIntent.NoteChanged(it))
            },
        )
        RadioButtonWriteOff(
            state = state.status,
            onStateSelect = {
                onIntent(WriteOffIntent.StatusClicked(it))
            }
        )
        ButtonPanel(
            isEntry = state.isEntry,
            entryButton = R.string.button_add,
            onClickInsert = { onIntent(WriteOffIntent.Insert) },
            onClickUpdate = { onIntent(WriteOffIntent.Update) },
            onClickDelete = { onIntent(WriteOffIntent.Delete) }
        )
    }
}




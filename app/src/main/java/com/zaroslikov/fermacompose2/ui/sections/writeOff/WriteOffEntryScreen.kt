@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.writeOff

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.Domain.models.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.RadioButtonWriteOff
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
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
    val eventFlow = viewModel.eventFlow
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.write_off_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        WriteOffEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding),
            titleList = viewModel.titleUiState.collectAsState().value.list,
            domainWriteOffTable = viewModel.writeOffUiState,
            isEntry = viewModel.isEntry,
            isIndicationValue = viewModel.isIndicatorsValue,
            isAutoCalculate = viewModel.isAutoCalculate,
            onValueChange = viewModel::updateUiState,
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
            countWarehouse = viewModel.itemUiState,
            updateCountWarehouse = viewModel::updateWarehouseUiState
        )
    }
}


@Composable
fun WriteOffEntryContainerProduct(
    modifier: Modifier,
    domainWriteOffTable: DomainWriteOffTable,
    isEntry: Boolean,
    isIndicationValue: Boolean,
    isAutoCalculate: MutableState<Boolean>,
    titleList: List<PairData>,
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (Pair<String, Boolean>) -> Unit,
    onValueChange: (DomainWriteOffTable) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var date by rememberSaveable {
        mutableStateOf(
            formatDateToString(
                domainWriteOffTable.day,
                domainWriteOffTable.mount,
                domainWriteOffTable.year
            )
        )
    }
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier) {
//        OutlinedTextTitleSale(
//            value = domainWriteOffTable.title,
//            onValueChoice = {
//                selectedItemIndex = it.first
//                onValueChange(domainWriteOffTable.copy(title = it.second).validateTitle())
//                updateCountWarehouse(
//                    Pair(
//                        it.second,
//                        titleList[selectedItemIndex].second == "Моя Продукция"
//                    )
//                )
//            },
////            selectedItemIndex = selectedItemIndex,
//            titleList = titleList,
//            isErrorTitle = domainWriteOffTable.error.isErrorTitle,
//            isErrorSlash = domainWriteOffTable.error.isErrorSlash,
//            enable = !isIndicationValue,
//            readOnly = isIndicationValue,
//        )
        OutlinedTextCount(
            value = domainWriteOffTable.count,
            onValueChange = {
                onValueChange(domainWriteOffTable.copy(count = it).validateCount())
            },
            onSuffixChange = { onValueChange(domainWriteOffTable.copy(suffix = it)) },
            isError = domainWriteOffTable.error.isErrorCount,
            suffix = domainWriteOffTable.suffix,
            intResSup = R.string.support_text_count_product_write_off,
            countWarehouse = countWarehouse.first.toString(),
            countWarehouseSuffix = countWarehouse.second,
        )
        OutlinedPriceInput(
            price = domainWriteOffTable.priceAll,
            onPriceChange = {
                onValueChange(domainWriteOffTable.copy(priceAll = it))
            },
            count = domainWriteOffTable.count,
            supportTextRes = R.string.support_text_price_write_off,
            isAutoCalculate = isAutoCalculate.value,
            onAutoCalculate = { isAutoCalculate.value = it },
            isManyCount = true,
        )
        OutlinedTextDateEdit(
            value = date,
            onValueChange = {
                date = it
                val dateList = it.split(".")
                onValueChange(
                    domainWriteOffTable.copy(
                        day = dateList[0].toInt(),
                        mount = dateList[1].toInt(),
                        year = dateList[2].toInt()
                    )
                )
            }
        )
        OutlinedTextNote(
            value = domainWriteOffTable.note,
            onValueChange = { onValueChange(domainWriteOffTable.copy(note = it)) },
        )
        RadioButtonWriteOff(
            state = domainWriteOffTable.status,
            onStateSelect = { onValueChange(domainWriteOffTable.copy(status = it)) }
        )
        ButtonPanel(
            isEntry = isEntry,
            focusManager = focusManager,
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}

@Composable
private fun ButtonPanel(
    isEntry: Boolean,
    focusManager: FocusManager,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    if (isEntry)
        ButtonStandart(
            intRes = R.string.button_add,
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



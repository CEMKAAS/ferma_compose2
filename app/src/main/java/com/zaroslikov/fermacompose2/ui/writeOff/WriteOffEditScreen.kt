@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.writeOff

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.Domain.models.DomainWritOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.RadioButtonWriteOff
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import kotlinx.coroutines.launch

object WriteOffEditDestination : NavigationDestination {
    override val route = "WriteOffEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemAddId"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}


@Composable
fun WriteOffEditProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: WriteOffEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val titleUiState by viewModel.titleUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.write_off_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->

        WriteOffEditContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.list,
            writeOffTable = viewModel.itemUiState,
            onValueChange = viewModel::updateUiState,
            countWarehouse = viewModel.countWarehouseUiState,
            updateCountWarehouse = {
                viewModel.updateCountWarehouseUiState(it)
            },
            onClickSave = {
                coroutineScope.launch {
                    viewModel.saveItem()
                }
                onNavigateUp()
            },
            onClickDelete = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                }
                onNavigateUp()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteOffEditContainerProduct(
    modifier: Modifier,
    writeOffTable: DomainWritOffTable,
    titleList: List<PairData>,
    onValueChange: (DomainWritOffTable) -> Unit = {},
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (Pair<String, Boolean>) -> Unit,
    onClickSave: () -> Unit,
    onClickDelete: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableIntStateOf(0) }

    val state by remember { mutableStateOf(writeOffTable.status == 0) }
    var date = formatDateToString(writeOffTable.day, writeOffTable.mount, writeOffTable.year)

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }


    val toastText = stringResource(
        R.string.toast_refresh_s,
        "${writeOffTable.title} ${writeOffTable.count} ${writeOffTable.suffix}",
    )
    Column(modifier = modifier) {
        OutlinedTextTitleSale(
            value = writeOffTable.title,
            onValueChoice = {
                onValueChange(writeOffTable.copy(title = it.second))
                selectedItemIndex = it.first
                updateCountWarehouse(
                    Pair(
                        it.second,
                        titleList[selectedItemIndex].second == "Моя Продукция"
                    )
                )
            },
            selectedItemIndex = selectedItemIndex,
            titleList = titleList,
            readOnly = true,
            focusManager = focusManager
        )
        OutlinedTextCount(
            value = writeOffTable.count,
            onValueChange = {
                onValueChange(
                    writeOffTable.copy(
                        count = it.toConvertDb()
                    )
                )
                isErrorCount = it.isError()
            },
            onClick = { onValueChange(writeOffTable.copy(suffix = it)) },
            isError = isErrorCount,
            suffix = writeOffTable.suffix,
            intResSup = R.string.support_text_count_product_write_off,
            countWarehouse = countWarehouse.first,
            countWarehouseSuffix = countWarehouse.second,
            focusManager = focusManager
        )
        OutlinedTextPrice(
            value = writeOffTable.priceAll,
            onValueChange = {
                onValueChange(
                    writeOffTable.copy(
                        priceAll = it
                    )
                )
            },
            intSupportText = R.string.support_text_price_write_off,
            focusManager = focusManager
        )
        OutlinedTextDateEdit (
            value = date,
            onValueChange = {
                date = it
                val dateList = it.split(".")
                onValueChange(
                    writeOffTable.copy(
                        day = dateList[0].toInt(),
                        mount = dateList[1].toInt(),
                        year = dateList[2].toInt()
                    )
                )
            }
        )
        OutlinedTextNote(
            value = writeOffTable.note,
            onValueChange = { onValueChange(writeOffTable.copy(note = it)) },
            focusManager = focusManager
        )
        CardField {
            RadioButtonWriteOff(
                state = state,
                onStateSelect = {
                    onValueChange(
                        writeOffTable.copy(
                            status = if (it) R.drawable.baseline_cottage_24 else R.drawable.baseline_delete_24
                        )
                    )
                }
            )
        }
        ButtonRefresh {
            if (isErrorAdd(title = writeOffTable.title, count = writeOffTable.count,
                    isErrorTitle = { isErrorTitle = it },
                    isErrorCount = { isErrorCount = it })
            ) {
                focusManager.clearFocus()
                onClickSave()
                toastShort(
                    context = context,
                    text = toastText
                )
            }
        }
        ButtonDelete { onClickDelete() }
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.writeOff

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDatePickerState
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
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.supportFun.metricaWriteOff
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.RadioButtonWriteOff
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import kotlinx.coroutines.launch
import java.time.Instant


object WriteOffEntryDestination : NavigationDestination {
    override val route = "WriteOffEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun WriteOffEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: WriteOffEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val titleUiState by viewModel.titleUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.write_off_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        WriteOffEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.animalList,
            saveInRoomSale = {
                coroutineScope.launch {
                    viewModel.saveItem(it)
                }
                onNavigateUp()
            },
            idProject = viewModel.itemId,
            countWarehouse = viewModel.itemUiState,
            updateCountWarehouse = {
                viewModel.updateUiState(it)
            }
        )
    }
}


@Composable
fun WriteOffEntryContainerProduct(
    modifier: Modifier,
    titleList: List<PairData>,
    saveInRoomSale: (WriteOffTable) -> Unit,
    idProject: Int, // TODO convet to Long
    countWarehouse: Double,
    updateCountWarehouse: (Pair<String, Boolean>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var title by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("") }
    var state1 by rememberSaveable { mutableStateOf(true) }
    var suffix by rememberSaveable { mutableStateOf("Шт.") }
    var priceAll by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var date by remember { mutableStateOf(dateToday()) }
    var openDialog by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val toastText = stringResource(R.string.toast_write_off_s, "$title $count $suffix")

    //Дата
    val state = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    if (openDialog) {
        DatePickerDialogSample(state, date) {
            date = it
            openDialog = false
        }
    }

    Column(modifier = modifier) {

        OutlinedTextTitleSale(
            value = title,
            onValueChoice = {
                selectedItemIndex = it.first
                title = it.second
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
            value = count,
            onValueChange = {
                count = it.toConvertDb()
                isErrorCount = it.isError()
            },
            onClick = { suffix = it },
            isError = isErrorCount,
            suffix = suffix,
            intResSup = R.string.support_text_count_product_write_off,
            countWarehouse = countWarehouse,
            focusManager = focusManager
        )
        OutlinedTextPrice(
            value = priceAll,
            onValueChange = {
                priceAll = it.toConvertDb()
            },
            intSupportText = R.string.support_text_price_write_off,
            focusManager = focusManager
        )
        OutlinedTextDate(
            value = date,
            onValueChange = { openDialog = !openDialog }
        )
        OutlinedTextNote(
            value = note,
            onValueChange = { note = it },
            focusManager = focusManager
        )
        CardField {
            RadioButtonWriteOff(
                state = state1,
                onStateSelect = { state1 = it }
            )
        }
        ButtonStandart(
            intRes = R.string.button_write_off,
            onClick = {
                if (isErrorAdd(title = title, count = count,
                        isErrorTitle = { isErrorTitle = it },
                        isErrorCount = { isErrorCount = it })
                ) {
                    focusManager.clearFocus()
                    val formattedDateList = date.split(".")
                    saveInRoomSale(
                        WriteOffTable(
                            id = 0,
                            title = title,
                            count = count.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                .toDouble(),
                            day = formattedDateList[0].toInt(),
                            mount = formattedDateList[1].toInt(),
                            year = formattedDateList[2].toInt(),
                            suffix = suffix,
                            priceAll = if (priceAll == "") 0.0 else priceAll.replace(
                                Regex("[^\\d.]"),
                                ""
                            ).replace(",", ".")
                                .toDouble(),
                            status = if (state1) 0 else 1,
                            note = note,
                            idPT = idProject
                        )
                    )
                    toastShort(
                        context = context,
                        text = toastText
                    )
                    metricaWriteOff(
                        title, count, suffix, priceAll, note, state1
                    )
                }
            }
        )
    }
}



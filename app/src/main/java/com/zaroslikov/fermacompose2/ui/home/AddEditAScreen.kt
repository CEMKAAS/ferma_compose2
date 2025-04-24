@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.home


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
import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch

object AddEditDestination : NavigationDestination {
    override val route = "AddEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemAddId"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun AddEditProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AddEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    val animalUiState by viewModel.animalUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.add_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->

        AddEditContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.list,
            categoryList = categoryUiState.list,
            animalList = animalUiState.animalList,
            addTable = viewModel.itemUiState,
            countWarehouseUiState = viewModel.countWarehouseUiState,
            updateCountWarehouse = {
                viewModel.updatecountWarehouseUiState(it)
            },
            onValueChange = viewModel::updateUiState,
            saveInRoomAdd = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    onNavigateUp()
                }
            },
            deleteAdd = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    onNavigateUp()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContainerProduct(
    modifier: Modifier,
    addTable: DomainAddTable,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<TripleData>,
    countWarehouseUiState: Double,
    updateCountWarehouse: (String) -> Unit = {},
    onValueChange: (DomainAddTable) -> Unit = {},
    saveInRoomAdd: () -> Unit,
    deleteAdd: () -> Unit
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val toastText = stringResource(
        R.string.toast_add_s,
        "${addTable.title} ${addTable.count} ${addTable.suffix}"
    )

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val selectedAnimalIndex by remember { mutableIntStateOf(0) }

    //date
    var openDialog by remember { mutableStateOf(false) }
    var date = formatDateToString(addTable.day, addTable.mount, addTable.year)

    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = formatDateToLong(date)
        )
        DatePickerDialogSample(datePickerState, date) {
            date = it
            openDialog = !openDialog

            val dateList = date.split(".")
            onValueChange(
                addTable.copy(
                    day = dateList[0].toInt(),
                    mount = dateList[1].toInt(),
                    year = dateList[2].toInt()
                )
            )
        }
    }

    Column(modifier = modifier) {

        OutlinedTextTitleAdd(
            value = addTable.title,
            onValueChange = {
                onValueChange(addTable.copy(title = it.trim()))
                isErrorTitle = it.isError()
                isErrorSlash = it.isErrorSlash()
                updateCountWarehouse(it)
            },
            titleList = titleList,
            isErrorTitle = isErrorTitle,
            isErrorSlash = isErrorSlash,
            focusManager = focusManager
        )

        OutlinedTextCount(
            value = addTable.count.formatNumber(),
            onValueChange = {
                onValueChange(addTable.copy(count = it.toConvertDb().toDouble()))
                isErrorCount = it.isError()
            },
            onClick = { onValueChange(addTable.copy(suffix = it)) },
            isError = isErrorCount,
            suffix = addTable.suffix,
            countWarehouse = addTable.count,
            intResSup = R.string.support_text_count_product,
            focusManager = focusManager
        )

        OutlinedTextCategory(
            value = addTable.category,
            onValueChange = { onValueChange(addTable.copy(category = it.trim())) },
            titleList = categoryList,
            focusManager = focusManager
        )

        OutlinedTextDate(
            value = date,
            onValueChange = { openDialog = !openDialog }
        )

        if (animalList.isNotEmpty()) {
            OutlinedTextAnimal(
                value = addTable.animal,
                onValueChange = {
                    onValueChange(
                        addTable.copy(
                            idAnimal = it.first.toLong(),
                            animal = animalList[selectedAnimalIndex].second
                        )
                    )
                },
                selectedAnimalIndex = selectedAnimalIndex,
                onClickClear = {
                    onValueChange(
                        addTable.copy(
                            idAnimal = 0,
                            animal = ""
                        )
                    )
                },
                animalList = animalList,
                focusManager = focusManager
            )
        }

        OutlinedTextNote(
            value = addTable.note,
            onValueChange = {
                onValueChange(addTable.copy(note = it))
            },
            focusManager = focusManager
        )

        ButtonRefresh {
            if (isErrorAdd(title = addTable.title, count = addTable.count.toString(),
                    isErrorTitle = { isErrorTitle = it },
                    isErrorCount = { isErrorCount = it },
                    isErrorSlash = { isErrorSlash = it })
            ) {
                focusManager.clearFocus()
                saveInRoomAdd()
                toastShort(
                    context = context,
                    text = toastText
                )
            }
        }
        ButtonDelete { deleteAdd() }
    }
}
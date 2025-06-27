@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


object AddEntryDestination : NavigationDestination {
    override val route = "AddEntry"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}


@Composable
fun AddEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AddEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val eventFlow = viewModel.eventFlow
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarBack(intRes = R.string.add_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        AddEntryContainerProduct(
            modifier = Modifier.modifierScreen(innerPadding),
            addTable = viewModel.addUiState,
            isEntry = viewModel.isEntry,
            titleList = viewModel.titleUiState.collectAsState().value.list,
            categoryList = viewModel.categoryUiState.collectAsState().value.list,
            animalList = viewModel.animalUiState.collectAsState().value.list,
            onValueChange = viewModel::updateUiState,
            countWarehouse = viewModel.itemUiState,
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
            updateCountWarehouse = viewModel::updateWarehouseUiState
        )
    }
}

@Composable
fun AddEntryContainerProduct(
    modifier: Modifier,
    addTable: DomainAddTable,
    isEntry: Boolean,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<TripleData>,
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (String) -> Unit,
    onValueChange: (DomainAddTable) -> Unit,
    onClickInsert: (String) -> Unit,
    onClickUpdate: (String) -> Unit,
    onClickDelete: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val textSnackbar = (if (isEntry) stringResource(
        R.string.toast_add_s
    ) else stringResource(R.string.toast_refresh_s)).format(
        addTable.title, addTable.count, addTable.suffix
    )
    val textSnackbarDelete = stringResource(R.string.toast_delete_s).format(
        addTable.title, addTable.count, addTable.suffix
    )

    var date by rememberSaveable {
        mutableStateOf(
            formatDateToString(
                addTable.day,
                addTable.mount,
                addTable.year
            )
        )
    }
    val selectedAnimalIndex by rememberSaveable { mutableIntStateOf(0) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextTitleAdd(
            value = addTable.title,
            onValueChange = {
                onValueChange(addTable.copy(title = it))
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
            value = addTable.count,
            onValueChange = {
                onValueChange(addTable.copy(count = it))
                isErrorCount = it.isError()
            },
            onClick = { onValueChange(addTable.copy(suffix = it)) },
            isError = isErrorCount,
            suffix = addTable.suffix,
            intResSup = R.string.support_text_count_product,
            countWarehouse = countWarehouse.first,
            countWarehouseSuffix = countWarehouse.second,
            focusManager = focusManager
        )
        OutlinedTextCategory(
            value = addTable.category,
            onValueChange = { onValueChange(addTable.copy(category = it)) },
            titleList = categoryList,
            focusManager = focusManager
        )
        OutlinedTextDateEdit(
            value = date,
            onValueChange = {
                date = it
                val dateList = it.split(".")
                onValueChange(
                    addTable.copy(
                        day = dateList[0].toInt(),
                        mount = dateList[1].toInt(),
                        year = dateList[2].toInt()
                    )
                )
            }
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
                            animal = it
                        )
                    )
                },
                animalList = animalList,
                focusManager = focusManager
            )
        }
        OutlinedTextNote(
            value = addTable.note,
            onValueChange = { onValueChange(addTable.copy(note = it)) },
            focusManager = focusManager
        )
        ButtonPanel(
            title = addTable.title,
            count = addTable.count,
            isEntry = isEntry,
            focusManager = focusManager,
            isErrorTitle = { isErrorTitle = it },
            isErrorCount = { isErrorCount = it },
            isErrorSlash = { isErrorSlash = it },
            onClickInsert = { onClickInsert(textSnackbar) },
            onClickUpdate = { onClickUpdate(textSnackbar) },
            onClickDelete = { onClickDelete(textSnackbarDelete) }
        )
    }
}

@Composable
private fun ButtonPanel(
    title: String,
    count: String,
    isEntry: Boolean,
    focusManager: FocusManager,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val context = LocalContext.current

    if (isEntry)
        ButtonStandart(
            intRes = R.string.button_add,
            onClick = {
                onClickButton(
                    title = title,
                    count = count,
                    focusManager = focusManager,
                    isErrorTitle = isErrorTitle,
                    isErrorCount = isErrorCount,
                    isErrorSlash = isErrorSlash,
                    onClick = onClickInsert
                )
            }
        )
    else {
        ButtonRefresh {
            onClickButton(
                title = title,
                count = count,
                focusManager = focusManager,
                isErrorTitle = isErrorTitle,
                isErrorCount = isErrorCount,
                isErrorSlash = isErrorSlash,
                onClick = onClickUpdate
            )
        }
        ButtonDelete { onClickDelete() }
    }
}

private fun onClickButton(
    title: String,
    count: String,
    focusManager: FocusManager,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    if (isErrorAdd(
            title = title, count = count,
            isErrorTitle = { isErrorTitle(it) },
            isErrorCount = { isErrorCount(it) },
            isErrorSlash = { isErrorSlash(it) })
    ) {
        focusManager.clearFocus()
        onClick()
    }
}

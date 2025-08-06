@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.add

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
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
    viewModel: AddEntryViewModel = hiltViewModel()
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
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
            countWarehouse = viewModel.itemUiState,
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
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

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

    Column(modifier = modifier) {
        OutlinedTextTitleAdd(
            value = addTable.title,
            onValueChange = {
                onValueChange(addTable.copy(title = it).validateTitle())
                updateCountWarehouse(it)
            },
            titleList = titleList,
            isErrorTitle = addTable.error.isErrorTitle,
            isErrorSlash = addTable.error.isErrorSlash,
        )
        OutlinedTextCount(
            value = addTable.count,
            onValueChange = {
                onValueChange(addTable.copy(count = it).validateCount())
            },
            onSuffixChange = { onValueChange(addTable.copy(suffix = it)) },
            isError = addTable.error.isErrorCount,
            suffix = addTable.suffix,
            intResSup = R.string.support_text_count_product,
            countWarehouse = countWarehouse.first,
            countWarehouseSuffix = countWarehouse.second,
        )
        OutlinedTextCategory(
            value = addTable.category,
            onValueChange = { onValueChange(addTable.copy(category = it)) },
            titleList = categoryList,
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

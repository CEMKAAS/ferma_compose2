@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.add.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd2
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
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: AddEntryViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.eventFlow
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.add_screen_title,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        AddEntryContainerProduct(
            modifier = modifier.modifierScreen(innerPadding),
            state = state,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
fun AddEntryContainerProduct(
    modifier: Modifier,
    state: AddEntryState,
    onIntent: (AddEntryIntent) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextTitleAdd2(
            value = state.title,
            onValueChange = { onIntent(AddEntryIntent.TitleChanged(it)) },
            onValueChangeSuffix = { onIntent(AddEntryIntent.TitleAndSuffix(it)) },
            titleList = state.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
        )
        OutlinedTextCount2(
            value = state.count,
            onValueChange = {
                onIntent(AddEntryIntent.CountChanged(it))
            },
            onSuffixChange = { onIntent(AddEntryIntent.Suffix(it)) },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product,
            isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
            warehouseList = state.warehouseList
        )
        OutlinedTextCategory(
            value = state.category,
            onValueChange = { onIntent(AddEntryIntent.CategoryChanged(it)) },
            titleList = state.categoryList,
        )
        OutlinedTextDateEdit(
            value = state.date,
            onValueChange = { onIntent(AddEntryIntent.Date(it)) }
        )
        if (state.animalList.isNotEmpty()) {
            OutlinedTextAnimal(
                value = state.animal,
                onValueChange = { onIntent(AddEntryIntent.Animal(it)) },
                selectedAnimalIndex = state.selectedAnimalIndex,
                onClickClear = { onIntent(AddEntryIntent.AnimalClear(it)) },
                animalList = state.animalList,
            )
        }
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onIntent(AddEntryIntent.NoteChanged(it)) },
        )
        ButtonPanel(
            state = state,
            onClickInsert = { onIntent(AddEntryIntent.Insert) },
            onClickUpdate = { onIntent(AddEntryIntent.Update) },
            onClickDelete = { onIntent(AddEntryIntent.Delete) }
        )
    }
}

@Composable
private fun ButtonPanel(
    state: AddEntryState,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    if (state.isEntry)
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

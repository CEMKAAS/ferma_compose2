@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.add.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
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
    val eventFlow = viewModel.navigation
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextTitleAddNew(
            value = state.title,
            onValueChange = { onIntent(AddEntryIntent.TitleChanged(it)) },
            onValueChangeSuffix = { onIntent(AddEntryIntent.TitleAndSuffix(it)) },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            drawableRes = R.drawable.icon_add_product
        )
        OutlinedTextCountNew(
            value = state.count,
            onValueChange = {
                onIntent(AddEntryIntent.CountChanged(it))
            },
            onSuffixChange = { onIntent(AddEntryIntent.SuffixClicked(it)) },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product,
//            isWarehouseShow = state.title.isNotBlank() && state.warehouseList.isNotEmpty(),
//            warehouseList = state.warehouseList
        )
        OutlinedTextCategoryNew(
            value = state.category,
            onValueChange = { onIntent(AddEntryIntent.CategoryChanged(it)) },
            titleList = state.pickList.categoryList,
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AddEntryIntent.Date(it)) }
        )
        if (state.pickList.animalList.isNotEmpty()) {
            OutlinedTextAnimalNew(
                value = state.animal,
                onValueChange = { onIntent(AddEntryIntent.Animal(it)) },
                selectedAnimalIndex = state.selectedAnimalIndex,
                onClickClear = { onIntent(AddEntryIntent.AnimalClear(it)) },
                animalList = state.pickList.animalList,
            )
        }
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AddEntryIntent.NoteChanged(it)) },
        )
        /*ButtonPanelNew(
            modifier = Modifier,
            isEntry = true,
            entryButton = R.string.button_add,
            onClickInsert = { onIntent(AddEntryIntent.Insert) },
            onClickClose = { TODO() },
            enable = false
        )*/
        /* ButtonPanel(
             isEntry = state.isEntry,
             entryButton = R.string.button_add,
             onClickInsert = { onIntent(AddEntryIntent.Insert) },
             onClickUpdate = { onIntent(AddEntryIntent.Update) },
             onClickDelete = { onIntent(AddEntryIntent.Delete) }
         )*/
    }
}



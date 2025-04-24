@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorSale
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.ErrorSupportTextSlash
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.toOutlinedText
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object NoteEditDestination : NavigationDestination {
    override val route = "NoteEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun NoteEditProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.note_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->

        AddEditContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            noteTable = viewModel.itemUiState,
            onValueChange = viewModel::updateUiState,
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

@Composable
fun AddEditContainerProduct(
    modifier: Modifier,
    noteTable: DomainNoteTable,
    onValueChange: (DomainNoteTable) -> Unit = {},
    onClickSave: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    val toastText = stringResource(
        R.string.toast_refresh_note_s,
        noteTable.title
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = noteTable.title,
            onValueChange = {
                onValueChange(noteTable.copy(title = it, date = dateToday()))
                isErrorTitle = it.isError()
            },
            label = { Text(text = stringResource(R.string.outlined_text_title)) },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isErrorTitle,
                    intRes = R.string.support_text_title,
                    intResError = R.string.error_no_title,
                )
            },
            modifier = Modifier.toOutlinedText(),
            isError = isErrorTitle,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = keyboardActionsDown(focusManager)
        )
        OutlinedTextField(
            value = noteTable.note,
            onValueChange = {
                onValueChange(
                    noteTable.copy(
                        note = it, date = dateToday()
                    )
                )
            },
            label = { Text(stringResource(R.string.outlined_text_note_note)) },
            modifier = Modifier
                .toOutlinedText(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        ButtonRefresh {
            if (!isErrorTitle) {
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
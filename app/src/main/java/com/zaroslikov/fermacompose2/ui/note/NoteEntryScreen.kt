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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.metricaNote
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.ErrorSupportTextSlash
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.toOutlinedText
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


object NoteEntryDestination : NavigationDestination {
    override val route = "NoteEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun NoteEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: NoteEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.note_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        NoteEntryContainer(
            modifier = Modifier
                .modifierScreen(innerPadding),
            idProject = viewModel.itemId,
            saveInRoomAdd = {
                coroutineScope.launch { viewModel.saveItem(it) }
                onNavigateUp()
            }
        )
    }
}

@Composable
fun NoteEntryContainer(
    modifier: Modifier,
    idProject: Long,
    saveInRoomAdd: (NoteTable) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
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
            value = note,
            onValueChange = {
                note = it
            },
            label = { Text(stringResource(R.string.outlined_text_note_note)) },
            modifier = Modifier
                .toOutlinedText(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        ButtonStandart(
            intRes = R.string.button_note,
            onClick = {
                if (!isErrorTitle) {
                    saveInRoomAdd(
                        NoteTable(
                            title = title,
                            note = note,
                            date = dateToday(),
                            idPT = idProject
                        )
                    )
                    metricaNote(title)
                }
            }
        )
    }
}

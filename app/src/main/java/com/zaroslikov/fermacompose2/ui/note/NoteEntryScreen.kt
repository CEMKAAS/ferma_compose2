package com.zaroslikov.fermacompose2.ui.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
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
    val idProject = viewModel.itemId
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Мои Заметки", navigateUp = navigateBack)
        }
    ) { innerPadding ->

        NoteEntryContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            saveInRoomAdd = {
                coroutineScope.launch {
                    viewModel.saveItem(
                        NoteTable(
                            id = it.id,
                            title = it.title,
                            note = it.note,
                            date = it.date,
                            idPT = idProject,
                        )
                    )
                    onNavigateUp()
                }
            },
        )
    }
}

@Composable
fun NoteEntryContainer(
    modifier: Modifier,
    saveInRoomAdd: (NoteTableInsert) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = title == ""
        return !(isErrorTitle)
    }

    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val date: String = format.format(calendar.timeInMillis)

    Column(modifier = modifier) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                validateTitle(title)
            },
            label = { Text(text = "Заголовок") },
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        text = "Не указан заголовок",
                        color = MaterialTheme.colorScheme.error
                    )
                } else Text("Укажите заголовок")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            isError = isErrorTitle,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            })
        )

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
            },
            label = { Text("Заметка") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        Button(
            onClick = {
                if (errorBoolean()) {
                    saveInRoomAdd(
                        NoteTableInsert(
                            title = title,
                            note = note,
                            date = date
                        )
                    )
                    val eventParameters: MutableMap<String, Any> = HashMap()
                    eventParameters["Заголовок"] = title
                    eventParameters["Заметка"] = note
                    AppMetrica.reportEvent("Заметки", eventParameters);
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            Text(text = "Добавить")
        }
    }
}

data class NoteTableInsert(
    val id: Long = 0,
    val title: String,
    val note: String,
    val date: String
)


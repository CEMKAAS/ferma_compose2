package com.zaroslikov.fermacompose2.ui.incubator

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.incubator.TimePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


object IncubatorProjectEditDestination : NavigationDestination {
    override val route = "IncubatorProjectEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun IncubatorProjectEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateStart: () -> Unit,
    viewModel: IncubatorProjectEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = "Инкубатор",
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorEditDayContainer(
            project = viewModel.projectState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            onValueChange = viewModel::updateUiState,
            saveInRoomAdd = {
                if (it) {
                    coroutineScope.launch {
                        viewModel.saveItem()
//                        Toast.makeText(
//                            context,
//                            "Обновлено: ${viewModel.itemUiState.title} ${viewModel.itemUiState.count} ${viewModel.itemUiState.suffix} за ${viewModel.itemUiState.priceAll} ₽",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        onNavigateUp()
                    }
                }
            },
            deleteRoom = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateStart()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorEditDayContainer(
    project: IncubatorProjectEditState,
    modifier: Modifier = Modifier,
    onValueChange: (IncubatorProjectEditState) -> Unit = {},
    saveInRoomAdd: (Boolean) -> Unit,
    deleteRoom: () -> Unit,
) {
    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (openDialog) {
        DatePickerDialogSample(datePickerState, project.data) { date ->
            onValueChange(project.copy(data = date))
            openDialog = false
        }
    }

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateCount(text: String) {
        isErrorCount = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = project.titleProject == ""
        isErrorCount = project.eggAll == ""
        return !(isErrorTitle || isErrorCount)
    }

    val showDialogTime1 = remember { mutableStateOf(false) }
    val showDialogTime2 = remember { mutableStateOf(false) }
    val showDialogTime3 = remember { mutableStateOf(false) }

    if (showDialogTime1.value) {
        onValueChange(
            project.copy(
                time1 = TimePickerEdit(
                    project.time1,
                    showDialog = showDialogTime1
                )
            )
        )
    }
    if (showDialogTime2.value) {
        onValueChange(
            project.copy(
                time1 = TimePickerEdit(
                    time = project.time2,
                    showDialog = showDialogTime2
                )
            )
        )
    }
    if (showDialogTime3.value) {
        onValueChange(
            project.copy(
                time1 = TimePickerEdit(
                    time = project.time3,
                    showDialog = showDialogTime3
                )
            )
        )
    }

    Column(modifier = modifier.padding(5.dp, 5.dp)) {

        OutlinedTextField(
            value = project.titleProject,
            onValueChange = {
                onValueChange(project.copy(titleProject = it))
                validateTitle(it)
            },
            label = { Text("Инкубатор") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        text = "Не указано название",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите название инкубатора")
                }
            },
            isError = isErrorTitle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )

        OutlinedTextField(
            value = project.eggAll,
            onValueChange = {
                onValueChange(project.copy(eggAll = it))
                validateCount(project.eggAll)
            },
            label = { Text("Количество") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isErrorCount) {
                    Text(
                        text = "Не указано кол-во яиц",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите кол-во яий, которых заложили в инкубатор")
                }
            },
            suffix = { Text(text = "Шт.") },
            isError = isErrorCount,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )

        OutlinedTextField(
            value = project.data,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата") },
            supportingText = {
                Text("Выберите дату ")
            },
            trailingIcon = {
                IconButton(onClick = { openDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_month_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    openDialog = true
                }
                .padding(bottom = 2.dp),
        )


        OutlinedTextField(
            value = project.time1,
            onValueChange = {
            },
            readOnly = true,
            label = { Text("Уведомление 1") },
            supportingText = {
                Text("Укажите время уведомления")
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialogTime1.value = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialogTime1.value = true
                }
                .padding(bottom = 2.dp)
        )

        OutlinedTextField(
            value = project.time2,
            onValueChange = {},
            readOnly = true,
            label = { Text("Уведомление 2") },
            supportingText = {
                Text("Укажите время уведомления")
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialogTime2.value = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialogTime2.value = true
                }
                .padding(bottom = 2.dp)
        )

        OutlinedTextField(
            value = project.time3,
            onValueChange = { },
            readOnly = true,
            label = { Text("Уведомление 3") },
            supportingText = {
                Text("Укажите время уведомления")
            },
            trailingIcon = {
                IconButton(onClick = {
                    showDialogTime3.value = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialogTime3.value = true
                }
                .padding(bottom = 2.dp)
        )

        Button(
            onClick = { saveInRoomAdd(errorBoolean()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_create_24),
                contentDescription = " Обновить "
            )
            Text(text = " Обновить ")
        }

        OutlinedButton(
            onClick = deleteRoom,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = "Удалить"
            )
            Text(text = " Удалить ")
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerEdit(time: String, showDialog: MutableState<Boolean>): String {
    var time1 = time
    val timsa = time.split(":").toTypedArray()
    val timeState = rememberTimePickerState(
        initialHour = timsa[0].toInt(),
        initialMinute = timsa[1].toInt()
    )
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.TimePicker(state = timeState)
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    showDialog.value = false
                    time1 = "${timeState.hour}:${timeState.minute}0"

                }) {
                    Text(text = "Принять")
                }

                TextButton(onClick = {
                    showDialog.value = false
                }) {
                    Text(text = "Назад")
                }
            }
        }
    }
    return time1
}
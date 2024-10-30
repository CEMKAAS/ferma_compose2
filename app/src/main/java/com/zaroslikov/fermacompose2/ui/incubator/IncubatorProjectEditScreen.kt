package com.zaroslikov.fermacompose2.ui.incubator

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.add.incubator.TimeOutlinedTextField
import com.zaroslikov.fermacompose2.ui.start.add.incubator.TimePicker
import com.zaroslikov.fermacompose2.ui.start.dateLong
import io.appmetrica.analytics.AppMetrica
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
    val countTime = rememberSaveable { mutableIntStateOf(0) }
   val project = viewModel.projectState

    if (project.time1 == "") {
        countTime.intValue = 0
    } else if (project.time2 == "") {
        countTime.intValue = 1
    } else if (project.time3 == "") {
        countTime.intValue = 2
    } else{
        countTime.intValue = 3
    }

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = "Инкубатор",
                navigateUp = navigateBack,
            )
        }
    ) { innerPadding ->

        IncubatorEditDayContainer(
            project = project,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            onValueChange = viewModel::updateUiState,
            saveInRoomAdd = {
                if (it) {
                    coroutineScope.launch {
                        viewModel.saveItem(countTime.intValue)
                        val eventParameters: MutableMap<String, Any> = HashMap()
                        eventParameters["Имя"] = project.titleProject
                        eventParameters["Тип"] = project.type
                        eventParameters["Кол-во"] = project.eggAll
                        eventParameters["АвтоОхл"] = project.airing
                        eventParameters["АвтоПрев"] = project.over
                        eventParameters["Увед1"] =project.time1
                        eventParameters["Увед2"] = project.time2
                        eventParameters["Увед3"] = project.time3
                        AppMetrica.reportEvent("Incubator Edit", eventParameters)
                        onNavigateUp()
                    }
                }
            },
            deleteRoom = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateStart()
                }
            },
            countTime = countTime
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
    countTime: MutableIntState
) {
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current




    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = dateLong(project.data)
    )

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

    var showDialogTime1 by remember { mutableStateOf(false) }
    var showDialogTime2 by remember { mutableStateOf(false) }
    var showDialogTime3 by remember { mutableStateOf(false) }

    if (showDialogTime1) {
        TimePicker(
            time =  if (project.time1 == "") "08:00" else project.time1,
            showDialog = {
                onValueChange(project.copy(time1 = it))
                showDialogTime1 = false
            }
        )
    }
    if (showDialogTime2) {
        TimePicker(
            time =  if (project.time2 == "") "12:00" else project.time2,
            showDialog = {
                onValueChange(project.copy(time2 = it))
                showDialogTime2 = false
            }
        )
    }
    if (showDialogTime3) {
        TimePicker(
            time =  if (project.time3 == "") "18:00" else project.time3,
            showDialog = {
                onValueChange(project.copy(time3 = it))
                showDialogTime3 = false
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
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
                onValueChange(
                    project.copy(
                        eggAll = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                    )
                )
                validateCount(project.eggAll)
            },
            label = { Text("Количество") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            supportingText = {
                if (isErrorCount) {
                    Text(
                        text = "Не указано кол-во яиц",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите кол-во яиц, которых заложили в инкубатор")
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
                .padding(bottom = 10.dp),
        )

        if (countTime.intValue > 0) {
            TimeOutlinedTextField(
                time =  if (project.time1 == "") "08:00" else project.time1,
                count = 1,
                countTime = countTime,
                showDialog = { showDialogTime1 = true },
            )
        }

        if (countTime.intValue > 1) {
            TimeOutlinedTextField(
                time = if (project.time2 == "") "12:00" else project.time2,
                count = 2,
                countTime = countTime,
                showDialog = { showDialogTime2 = true },
            )
        }

        if (countTime.intValue > 2) {
            TimeOutlinedTextField(
                time =  if (project.time3 == "") "18:00" else project.time3,
                count = 3,
                countTime = countTime,
                showDialog = { showDialogTime3 = true },
            )
        }

        if (countTime.intValue < 3) {
            OutlinedButton(
                onClick = { countTime.intValue++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 20.dp)
            ) {
                Text(text = "Добавить напоминание")
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Удалить",
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

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

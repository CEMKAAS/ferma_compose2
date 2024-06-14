package com.zaroslikov.fermacompose2.ui.start.add.incubator

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object AddIncubatorDestination : NavigationDestination {
    override val route = "AddIncubator"
    override val titleRes = R.string.app_name

}
@Composable
fun AddIncubator(
    navigateBack: () -> Unit,
    navigateContinue: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarStart(title = "Мое Хозяйство", true, navigateUp = navigateBack)
        },
    ) { innerPadding ->
        AddIncubatorContainer(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            navigateContinue = navigateContinue
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncubatorContainer(modifier: Modifier, navigateContinue: () -> Unit) {

    val typeBirdsList = arrayListOf("Курица", "Гуси", "Перепела", "Индюки", "Утки")

    var title by rememberSaveable { mutableStateOf("") }
    var typeBirds by rememberSaveable { mutableStateOf(typeBirdsList[0]) }
    var count by rememberSaveable { mutableStateOf("") }
    var time1 by rememberSaveable { mutableStateOf("08:00") }
    var time2 by rememberSaveable { mutableStateOf("12:00") }
    var time3 by rememberSaveable { mutableStateOf("18:00") }

    var expandedTypeBirds by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val checkedStateAiring = remember { mutableStateOf(false) }
    val checkedStateOver = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableStateOf(0) }

    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var date1 by remember { mutableStateOf(formattedDate) }

    if (openDialog) {
        DatePickerDialogSample(datePickerState, date1) { date ->
            date1 = date
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
        isErrorTitle = title == ""
        isErrorCount = count == ""
        return !(isErrorTitle || isErrorCount)
    }

    var showDialog by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color.LightGray.copy(alpha = .3f))
                    .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timeState)
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = "Принять")
                    }
                    TextButton(onClick = {
                        showDialog = false
                        timeState.hour
                         timeState.minute
                    }) {
                        Text(text = "Назад")
                    }
                }
            }
        }
    }

    Column(modifier = modifier.padding(5.dp, 5.dp)) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                validateTitle(title)
            },
            label = { Text("Инкубатор №1") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите название инкубатора")
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

        Box {
            ExposedDropdownMenuBox(
                expanded = expandedTypeBirds,
                onExpandedChange = { expandedTypeBirds = !expandedTypeBirds },
            ) {
                OutlinedTextField(
                    value = typeBirds,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeBirds) },
                    label = { Text(text = "Тип птицы") },
                    supportingText = {
                        if (isErrorTitle) {
                            Text(
                                text = "Не выбран тип яйца",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Выберите тип птицы")
                        }
                    },
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedTypeBirds,
                    onDismissRequest = { expandedTypeBirds = false }
                ) {
                    typeBirdsList.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                )
                            },
                            onClick = {
                                selectedItemIndex = index
                                expandedTypeBirds = false
                                typeBirds = typeBirdsList[selectedItemIndex]
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = count,
            onValueChange = {
                count = it
                validateCount(count)
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
            value = date1,
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
            value = time1,
            onValueChange = {

            },
            readOnly = true,
            label = { Text("Уведомление 1") },
            supportingText = {
                Text("Укажите время уведомления")
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true
                    time1 = "${timeState.hour}:${timeState.minute}"
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_month_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialog = true
                }
                .padding(bottom = 2.dp)
        )

        OutlinedTextField(
            value = time2,
            onValueChange = { time2 = "${timeState.hour} : ${timeState.minute}"},
            readOnly = true,
            label = { Text("Уведомление 2") },
            supportingText = {
                Text("Укажите время уведомления")
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true
                    time2 = "${timeState.hour}:${timeState.minute}"
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_month_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialog = true
                }
                .padding(bottom = 2.dp)
        )

        OutlinedTextField(
            value = time3,
            onValueChange = { time3 = "${timeState.hour} : ${timeState.minute}"},
            readOnly = true,
            label = { Text("Уведомление 3") },
            supportingText = {
                Text("Укажите время уведомления")
            },
            trailingIcon = {
                IconButton(onClick = { showDialog = true
                    time3 = "${timeState.hour}:${timeState.minute}"}) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_month_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDialog = true
                }
                .padding(bottom = 2.dp)
        )


        Row(
            Modifier
                .selectableGroup()
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedStateAiring.value,
                onCheckedChange = { checkedStateAiring.value = it }
            )
            Text(text = "Авто охлаждение")
            Checkbox(
                checked = checkedStateOver.value,
                onCheckedChange = { checkedStateOver.value = it }
            )
            Text(text = "Авто переворот")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(onClick = {
                if (errorBoolean()) {
                    navigateContinue()
                }
            }) {
                Text(text = "Далее")
                //TODO Изображение
            }
        }
    }
}
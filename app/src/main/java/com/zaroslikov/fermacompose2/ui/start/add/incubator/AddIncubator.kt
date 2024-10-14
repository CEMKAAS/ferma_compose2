package com.zaroslikov.fermacompose2.ui.start.add.incubator

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object AddIncubatorDestination : NavigationDestination {
    override val route = "AddIncubator"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AddIncubator(
    navigateBack: () -> Unit
    viewModel: AddIncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var shouldShowTwo by rememberSaveable { mutableStateOf(true) }

    if (shouldShowTwo) AddIncubatorContainerOne(navigateBack = navigateBack, navigateContinue = false) else AddIncubatorTwoContainer()

}

@Composable
fun AddIncubatorContainerOne(
    navigateBack: () -> Unit,
    navigateContinue: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Инкубатор", navigateUp = navigateBack)
        }
    ) { innerPadding ->

        AddIncubatorContainer(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            navigateContinue = navigateContinue,
            number = viewModel.countProject()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncubatorContainer(
    modifier: Modifier,
    navigateContinue: (AddIncubatorList) -> Unit,
    number: Int
) {
    val typeBirdsList = arrayListOf("Курицы", "Гуси", "Перепела", "Индюки", "Утки")

    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    var title by remember { mutableStateOf("Мой Инкубатор") }
    var typeBirds by rememberSaveable { mutableStateOf(typeBirdsList[0]) }
    var count by rememberSaveable { mutableStateOf("0") }
    var date1 by remember { mutableStateOf(formattedDate) }
    val time1 = rememberSaveable { mutableStateOf("08:00") }
    val time2 = rememberSaveable { mutableStateOf("12:00") }
    val time3 = rememberSaveable { mutableStateOf("18:00") }

    var expandedTypeBirds by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val checkedStateAiring = remember { mutableStateOf(false) }
    val checkedStateOver = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableStateOf(0) }

    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = calendar.timeInMillis
    )


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

    val showDialogTime1 = remember { mutableStateOf(false) }
    val showDialogTime2 = remember { mutableStateOf(false) }
    val showDialogTime3 = remember { mutableStateOf(false) }

    if (showDialogTime1.value) {
        TimePicker(time = time1, showDialog = showDialogTime1)
    }
    if (showDialogTime2.value) {
        TimePicker(time = time2, showDialog = showDialogTime2)
    }
    if (showDialogTime3.value) {
        TimePicker(time = time3, showDialog = showDialogTime3)
    }

    Column(modifier = modifier.padding(5.dp, 5.dp)) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                validateTitle(it)
            },
            label = { Text("Название") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
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
                        Text("Выберите тип птицы")
                    },
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
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
                count = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                validateCount(it)
            },
            label = { Text("Количество") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
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
                .padding(bottom = 10.dp),
        )
//
//
//        OutlinedTextField(
//            value = time1.value,
//            onValueChange = {
//            },
//            readOnly = true,
//            label = { Text("Уведомление 1") },
//            supportingText = {
//                Text("Укажите время уведомления")
//            },
//            trailingIcon = {
//                IconButton(onClick = {
//                    showDialogTime1.value = true
//                }) {
//                    Icon(
//                        painter = painterResource(R.drawable.baseline_access_time_24),
//                        contentDescription = "Показать меню"
//                    )
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable {
//                    showDialogTime1.value = true
//                }
//                .padding(bottom = 2.dp)
//        )
//
//        OutlinedTextField(
//            value = time2.value,
//            onValueChange = {},
//            readOnly = true,
//            label = { Text("Уведомление 2") },
//            supportingText = {
//                Text("Укажите время уведомления")
//            },
//            trailingIcon = {
//                IconButton(onClick = {
//                    showDialogTime2.value = true
//                }) {
//                    Icon(
//                        painter = painterResource(R.drawable.baseline_access_time_24),
//                        contentDescription = "Показать меню"
//                    )
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable {
//                    showDialogTime2.value = true
//                }
//                .padding(bottom = 2.dp)
//        )
//
//        OutlinedTextField(
//            value = time3.value,
//            onValueChange = { },
//            readOnly = true,
//            label = { Text("Уведомление 3") },
//            supportingText = {
//                Text("Укажите время уведомления")
//            },
//            trailingIcon = {
//                IconButton(onClick = {
//                    showDialogTime2.value = true
//                }) {
//                    Icon(
//                        painter = painterResource(R.drawable.baseline_access_time_24),
//                        contentDescription = "Показать меню"
//                    )
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable {
//                    showDialogTime2.value = true
//                }
//                .padding(bottom = 2.dp)
//        )


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

            Button(
                onClick = {

                    if (errorBoolean()) {
                        navigateContinue(
                            AddIncubatorList(
                                title = title,
                                typeBirds = typeBirds,
                                count = count,
                                date1 = date1,
                                time1 = time1.value,
                                time2 = time2.value,
                                time3 = time3.value,
                                checkedStateAiring = checkedStateAiring.value,
                                checkedStateOver = checkedStateOver.value
                            )
                        )
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(text = "Далее")
            }
        }
    }
}

@Parcelize
data class AddIncubatorList(
    val title: String,
    val typeBirds: String,
    val count: String,
    val date1: String,
    val time1: String,
    val time2: String,
    val time3: String,
    val checkedStateAiring: Boolean,
    val checkedStateOver: Boolean
) : Parcelable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(time: MutableState<String>, showDialog: MutableState<Boolean>) {
    val timsa = time.value.split(":").toTypedArray()
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
            TimePicker(state = timeState)
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    showDialog.value = false
                    time.value = "${timeState.hour}:${timeState.minute}0"
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
}

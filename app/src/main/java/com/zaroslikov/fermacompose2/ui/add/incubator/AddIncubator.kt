package com.zaroslikov.fermacompose2.ui.add.incubator

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.ui.window.Dialog
import com.zaroslikov.fermacompose2.AlterDialigStart
import com.zaroslikov.fermacompose2.ui.add.getByteArrayFromDrawable
import com.zaroslikov.fermacompose2.ui.start.formatterTime
import io.appmetrica.analytics.AppMetrica

object AddIncubatorDestination : NavigationDestination {
    override val route = "AddIncubator"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AddIncubator(
    navigateBack: () -> Unit,
    viewModel: AddIncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateContinue: () -> Unit,
    isFirstStart :Boolean,
) {
    var shouldShowTwo by rememberSaveable { mutableStateOf(true) }
    var openEndDialog by rememberSaveable { mutableStateOf(false) }
    var listBoolean by rememberSaveable { mutableStateOf(false) }
    val countTime = rememberSaveable { mutableIntStateOf(0) }

    val incubator = viewModel.incubatorUiState
    val context = LocalContext.current

//   Доставка из ахива
    viewModel.incubatorFromArchive5(incubator.type)
    val projectList = viewModel.items2.value

    val list = setAutoIncubator(
        setIncubator(incubator.type),
        incubator.airing,
        incubator.over
    )
    val list2 = viewModel.items.value.toMutableList()

    if (openEndDialog) {
        ArhivIncubatorChoice(
            openDialog = {
                listBoolean = false
                openEndDialog = false
                shouldShowTwo = false
            },
            projectList = projectList,
            incubatorArh = {
                viewModel.incubatorFromArchive4(it)
            },
            incubatorArh2 = {
                listBoolean = true
                openEndDialog = false
                shouldShowTwo = false
            }
        )
    }

    if (shouldShowTwo)
        AddIncubatorContainerOne(
            navigateBack = navigateBack,
            navigateContinue = {
                if (projectList.isEmpty()) shouldShowTwo = false else openEndDialog = true
            },
            incubator = incubator,
            onUpdate = viewModel::updateUiState,
            countTime = countTime,
            isFirstStart
        )
    else AddIncubatorContainerTwo(
        name = incubator.titleProject,
        navigateBack = {
            listBoolean = false
            shouldShowTwo = true
        },
        navigateContinue = {
            viewModel.updateUiState(incubator.copy(imageData = getByteArray(context,viewModel.incubatorUiState.type)))
            viewModel.saveProject(it, countTime.intValue)
            val eventParameters: MutableMap<String, Any> = HashMap()
            eventParameters["Имя"] = incubator.titleProject
            eventParameters["Тип"] = incubator.type
            eventParameters["Кол-во"] = incubator.eggAll
            eventParameters["АвтоОхл"] = incubator.airing
            eventParameters["АвтоПрев"] = incubator.over
            eventParameters["Увед1"] = incubator.time1
            eventParameters["Увед2"] = incubator.time2
            eventParameters["Увед3"] = incubator.time3
            AppMetrica.reportEvent("Incubator", eventParameters);
            navigateContinue()
        },
        list = if (listBoolean) list2 else list,
        isFirstStart
    )
}

@Composable
fun AddIncubatorContainerOne(
    navigateBack: () -> Unit,
    navigateContinue: () -> Unit,
    incubator: IncubatorProjectEditState,
    onUpdate: (IncubatorProjectEditState) -> Unit = {},
    countTime: MutableIntState,
    isFirstStart :Boolean,
) {
    AlterDialigStart(
        isFirstStart = isFirstStart,
        dialogTitle = "Установка проекта",
        dialogText = "Давайте настроим Ваш первый инкубатор!\n" +
                "Для начала укажите его название, вид птицы, количество. После этого сможете перейти в меню для детальной настройки каждого дня.\n" +
                "Удачи!",
        textAppMetrica = "Установка инкубатора"
    )
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
            incubator, onUpdate,
            countTime = countTime
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncubatorContainer(
    modifier: Modifier,
    navigateContinue: () -> Unit,
    incubator: IncubatorProjectEditState,
    onUpdate: (IncubatorProjectEditState) -> Unit = {},
    countTime: MutableIntState
) {
    val typeBirdsList = arrayListOf("Курицы", "Гуси", "Перепела", "Индюки", "Утки")

    //Календарь
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val context = LocalContext.current

    var expandedTypeBirds by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableStateOf(0) }

    //Дата
    var openDialog by remember { mutableStateOf(false) }


    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = calendar.timeInMillis
        )
        DatePickerDialogSample(datePickerState, incubator.data) { date ->
            onUpdate(incubator.copy(data = date))
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
        isErrorTitle = incubator.titleProject == ""
        isErrorCount = incubator.eggAll == ""
        return !(isErrorTitle || isErrorCount)
    }

    var showDialogTime1 by remember { mutableStateOf(false) }
    var showDialogTime2 by remember { mutableStateOf(false) }
    var showDialogTime3 by remember { mutableStateOf(false) }

    if (showDialogTime1) {
        TimePicker(time = incubator.time1, showDialog = {
            onUpdate(incubator.copy(time1 = it))
            showDialogTime1 = false
        })
    }
    if (showDialogTime2) {
        TimePicker(time = incubator.time2, showDialog = {
            onUpdate(incubator.copy(time2 = it))
            showDialogTime2 = false
        })
    }
    if (showDialogTime3) {
        TimePicker(time = incubator.time3, showDialog = {
            onUpdate(incubator.copy(time3 = it))
            showDialogTime3 = false
        })
    }

    Column(modifier = modifier.padding(5.dp, 5.dp)) {

        OutlinedTextField(
            value = incubator.titleProject,
            onValueChange = {
                onUpdate(incubator.copy(titleProject = it))
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
            })
        )

        Box {
            ExposedDropdownMenuBox(
                expanded = expandedTypeBirds,
                onExpandedChange = { expandedTypeBirds = !expandedTypeBirds },
            ) {
                OutlinedTextField(
                    value = incubator.type,
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
                                onUpdate(incubator.copy(type = typeBirdsList[selectedItemIndex]))
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = incubator.eggAll,
            onValueChange = {
                onUpdate(
                    incubator.copy(
                        eggAll = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                    )
                )
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
            value = incubator.data,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата") },
            supportingText = {
                Text("Выберите дату ")
            },
            trailingIcon = {
                IconButton(onClick = {
                    openDialog = true

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
                    openDialog = true
                }
                .padding(bottom = 10.dp),
        )

        if (countTime.intValue > 0) {
            TimeOutlinedTextField(
                time = incubator.time1,
                count = 1,
                countTime = countTime,
                showDialog = { showDialogTime1 = true },
            )
        }

        if (countTime.intValue > 1) {
            TimeOutlinedTextField(
                time = incubator.time2,
                count = 2,
                countTime = countTime,
                showDialog = { showDialogTime2 = true },
            )
        }

        if (countTime.intValue > 2) {
            TimeOutlinedTextField(
                time = incubator.time3,
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

        Row(
            Modifier
                .selectableGroup()
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = incubator.airing,
                onCheckedChange = { onUpdate(incubator.copy(airing = it)) }
            )
            Text(text = "Авто охлаждение")
            Checkbox(
                checked = incubator.over,
                onCheckedChange = { onUpdate(incubator.copy(over = it)) }
            )
            Text(text = "Авто переворот")
        }

        Button(
            onClick = { if (errorBoolean()) navigateContinue() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(text = "Далее")
        }
    }
}


@Composable
fun TimeOutlinedTextField(
    time: String,
    count: Int,
    countTime: MutableIntState,
    showDialog: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = time,
            onValueChange = {},
            readOnly = true,
            label = { Text("Уведомление $count") },
            supportingText = {
                Text("Укажите время для уведомления $count")
            },
            trailingIcon = {
                IconButton(onClick = showDialog) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth(if (countTime.intValue < count + 1) 0.9f else 1f)
                .clickable { showDialog() }
                .padding(bottom = 2.dp)
        )
        if (countTime.intValue < count + 1) {
            IconButton(
                onClick = {
                    countTime.intValue--
                }, modifier = Modifier
                    .padding(bottom = 13.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Удалить")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(time: String, showDialog: (String) -> Unit) {

    val timsa = time.split(":")
    val timeState = rememberTimePickerState(
        initialHour = timsa[0].toInt(),
        initialMinute = timsa[1].toInt()
    )
    Dialog(
        onDismissRequest = { showDialog(time) }
    ) {
        Card(
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
                        showDialog(formatterTime(timeState.hour, timeState.minute))
                    }) {
                        Text(text = "Принять")
                    }

                    TextButton(onClick = {
                        showDialog(time)
                    }) {
                        Text(text = "Назад")
                    }
                }
            }
        }
    }
}

fun getByteArray(context : Context, type: String): ByteArray {
    return when (type) {
        "Курицы" -> getByteArrayFromDrawable(context, R.drawable.chicken)
        "Гуси" -> getByteArrayFromDrawable(context, R.drawable.external_goose_birds_icongeek26_outline_icongeek26)
        "Перепела" -> getByteArrayFromDrawable(context, R.drawable.quail)
        "Утки" -> getByteArrayFromDrawable(context, R.drawable.duck)
        "Индюки" -> getByteArrayFromDrawable(context, R.drawable.turkeycock)
        else -> {
            getByteArrayFromDrawable(context, R.drawable.chicken)
        }
    }
}

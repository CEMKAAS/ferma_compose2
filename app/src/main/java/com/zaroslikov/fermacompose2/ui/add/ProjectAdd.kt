package com.zaroslikov.fermacompose2.ui.add

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.AlterDialigStart
import com.zaroslikov.fermacompose2.MainActivity
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.incubator.AlertDialogExample
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.TimeZone


object ProjectAddDestination : NavigationDestination {
    override val route = "ProjectAdd"
    override val titleRes = R.string.app_name
}

@Composable
fun AddProject(
    navigateBack: () -> Unit,
    navigateToStart: () -> Unit,
    isFirstStart: Boolean,
    isFirstEnd: () -> Unit,
    viewModel: ProjectAddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    AlterDialigStart(
        isFirstStart = isFirstStart,
        dialogTitle = "Установка проекта",
        dialogText = "Придумайте оригинальное название для вашего проекта, например: \"Козоводство\" или \"Кролиководство\". Укажите дату начала проекта и нажмите \"Начать\".",
        isFirstEnd = isFirstEnd
    )

    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Проект", navigateUp = navigateBack)
        }
    ) { innerPadding ->
        AddProjectContainer(
            number = viewModel.countProject(),
            modifier = Modifier.padding(innerPadding),
            navigateToStart = {
                coroutineScope.launch {
                    viewModel.insertTable(it)
                    navigateToStart()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectContainer(
    number: Int,
    modifier: Modifier,
    navigateToStart: (ProjectTable) -> Unit,
) {

    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    //Дата
    var openDialog by remember { mutableStateOf(false) }

    val state = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = calendar.timeInMillis
    )

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }


    //Текст
    var name by remember {
        mutableStateOf("")
    }
    var date1 by remember { mutableStateOf(formattedDate) }

    if (openDialog) {
        DatePickerDialogSample(state, date1) { date ->
            date1 = date
            openDialog = false
        }
    }
    fun errorBoolean(): Boolean {
        isErrorTitle = name == ""
        return !(isErrorTitle)
    }



    Column(modifier = modifier.padding(5.dp, 5.dp)) {

        //Картинка
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//    val launcher =
//        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
//            imageUri = uri
//        }
//
//    val context = LocalContext.current
//    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            imageUri?.let {
//                if (Build.VERSION.SDK_INT < 28) {
//                    bitmap.value = MediaStore.Images
//                        .Media.getBitmap(context.contentResolver, it)
//                } else {
//                    val source = ImageDecoder.createSource(context.contentResolver, it)
//                    bitmap.value = ImageDecoder.decodeBitmap(source)
//                }
//            }
//
//            if (imageUri == null) {
//                Image(
//                    painter = painterResource(R.drawable.baseline_add_photo_alternate_24),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(125.dp)
//                        .clickable { launcher.launch("image/*") }
//                )
//            } else {
//                bitmap.value?.let { btm ->
//                    Image(
//                        bitmap = btm.asImageBitmap(),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .size(125.dp)
//                            .clickable { launcher.launch("image/*") }
//                    )
//                }
//            }


        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                validateTitle(it)
            },
            label = { Text("Название") },
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        text = "Не указано название проекта",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите название проекта")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            isError = isErrorTitle,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            )
        )

        OutlinedTextField(
            value = date1,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата") },
            supportingText = {
                Text("Выберите дату начала проекта")
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
                .padding(bottom = 2.dp)
                .clickable {
                    openDialog = true
                }
        )

        Button(
            onClick = {
                if (errorBoolean()) {

                    navigateToStart(
                        ProjectTable(
                            id = 0,
                            titleProject = name,
                            type = "",
                            data = date1,
                            eggAll = "",
                            eggAllEND = "",
                            airing = "",
                            over = "",
                            arhive = "0",
                            dateEnd = date1,
                            time1 = "",
                            time2 = "",
                            time3 = "",
                            mode = 1
                        )
                    )
                    val eventParameters: MutableMap<String, Any> = HashMap()
                    eventParameters["Имя"] = name
                    AppMetrica.reportEvent("Project", eventParameters);
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        )
        {
            Text(text = "Начать")
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePickerDialogSample(
//    datePickerState: DatePickerState,
//    dateToday: String,
//    onDateSelected: (String) -> Unit
//) {
//    DatePickerDialog(
//        onDismissRequest = {
//            onDateSelected(dateToday)
//        },
//        confirmButton = {
//            TextButton(
//                onClick = {
//                    val format = SimpleDateFormat("dd.MM.yyyy")
//                    val formattedDate: String =
//                        format.format(datePickerState.selectedDateMillis)
//                    onDateSelected(formattedDate)
//                },
//            ) { Text("Выбрать") }
//        },
//        dismissButton = {
//            TextButton(
//                onClick = {
//                    onDateSelected(dateToday)
//                }
//            ) { Text("Назад") }
//        }
//    ) {
//        DatePicker(state = datePickerState)
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSample(
    datePickerState: DatePickerState,
    dateToday: String,
    onDateSelected: (String) -> Unit
) {
    DatePickerDialog(
        onDismissRequest = {
            onDateSelected(dateToday)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val format = SimpleDateFormat("dd.MM.yyyy")
                    val formattedDate: String =
                        format.format(datePickerState.selectedDateMillis)
                    onDateSelected(formattedDate)
                },
            ) { Text("Выбрать") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDateSelected(dateToday)
                }
            ) { Text("Назад") }
        }
    ) {
        DatePicker(state = datePickerState, dateFormatter = DatePickerDefaults.dateFormatter())
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSampleNoLimit(
    datePickerState: DatePickerState,
    dateToday: String,
    onDateSelected: (String) -> Unit
) {
    DatePickerDialog(
        onDismissRequest = {
            onDateSelected(dateToday)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val format = SimpleDateFormat("dd.MM.yyyy")
                    val formattedDate: String =
                        format.format(datePickerState.selectedDateMillis)
                    onDateSelected(formattedDate)
                },
            ) { Text("Выбрать") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDateSelected(dateToday)
                }
            ) { Text("Назад") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun AddProjectPrewie() {
//    AddProjectContainer(
//        modifier = Modifier.fillMaxSize(),
//        navController = rememberNavController(),
//        scope = rememberCoroutineScope()
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}
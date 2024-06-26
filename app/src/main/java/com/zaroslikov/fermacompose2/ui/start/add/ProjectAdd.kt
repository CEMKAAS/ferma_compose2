package com.zaroslikov.fermacompose2.ui.start.add

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.StartDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
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
    viewModel: ProjectAddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Проект", navigateUp = navigateBack)
        },
    ) { innerPadding ->
        AddProjectContainer(
            number = viewModel.countProject,
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
    val datePickerState = rememberDatePickerState()
    datePickerState.setSelection(calendar.timeInMillis) //todo хмм
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }
    //Текст
    var name by remember { mutableStateOf("Мое Хозяйство №${
        number + 1}") }
    var date1 by remember { mutableStateOf(formattedDate) }

    if (openDialog) {
        DatePickerDialogSample(datePickerState, date1) { date ->
            date1 = date
            openDialog = false
        }
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
               if (!isErrorTitle) {
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
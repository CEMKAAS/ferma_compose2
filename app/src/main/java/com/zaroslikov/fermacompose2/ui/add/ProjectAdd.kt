package com.zaroslikov.fermacompose2.ui.add


import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.AlterDialigStart
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
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
    viewModel: ProjectAddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    AlterDialigStart(
        isFirstStart = isFirstStart,
        dialogTitle = "Установка проекта",
        dialogText = "Придумайте оригинальное название для вашего проекта, например: \"Козоводство\" или \"Кролиководство\". Укажите дату начала проекта и нажмите \"Начать\".",
        textAppMetrica = "Установка проекта"
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
    var name by remember { mutableStateOf("") }
    val bitmap by remember { mutableStateOf<ByteArray?>(null) }

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

    val imageResources = listOf(
        R.drawable.baseline_cruelty_free_24,
        R.drawable.chicken,
        R.drawable.livestock,
        R.drawable.baseline_warehouse_24,
        R.drawable.ic_ovos2
    ).map { painterResource(it) }.toMutableList()


    //Картинка
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Column(modifier = modifier.padding(5.dp, 5.dp)) {
        Text(
            text = "Выберите фото проекта:",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp
        )

        ImageLazyRow(
            images = imageResources,
            onImageSelected = { index ->
//                viewModel.selectImage(index)
            },
            onAddImageClicked = {
                // Логика для открытия галереи и выбора изображения
                launcher.launch("image/*")

                imageResources.add( imageUri)
            }
        )


//        LazyRow {
//            items(imageResource.size) { index ->
//                val image = imageResource[index]
//                ImageCircle(image, isSelected = selectedImageIndex == index) {
//                    selectedImageIndex = index
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//            }
//        }

//        //Картинка
//        var imageUri by remember { mutableStateOf<Uri?>(null) }
//        val launcher =
//            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
//                imageUri = uri
//            }
//
//        val context = LocalContext.current
//        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
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
//        }


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
                            mode = 1,
//                            imageData = bitmap
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


@Composable
fun ImageLazyRow(
    images: MutableList<Painter>,
    onImageSelected: (Int?) -> Unit = {},
    onAddImageClicked: () -> Unit
) {
    val selectedImageIndex by remember { mutableStateOf<Int?>(null) }

    LazyRow(modifier = Modifier.padding(16.dp)) {
        items(images.size) { index ->
            val image = images[index]
            SelectableImageWithIcon(
                image = image,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_cruelty_free_24),
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp)
                    )
                },
                isSelected = selectedImageIndex == index
            ) {
                if (index == images.size - 1) {
//                    selectedImageIndex = index
                    onAddImageClicked()
                } else {
//                    selectedImageIndex = index
//                    onImageSelected(index)
                }
            }


            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun SelectableImageWithIcon(
    image: Painter,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color.Green else Color.Transparent,
                    shape = CircleShape
                )
        )
        if (isSelected) {
            icon() // Отображаем значок, если элемент выбран
        }
    }
}


@Composable
fun ImageCircle(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(shape = CircleShape)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color.Green else Color.Black,
                    shape = CircleShape
                )
                .padding(end = 5.dp)
        )
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Показать меню",
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp),
                tint = Color.Green
            )
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
package com.zaroslikov.fermacompose2.ui.add


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.zaroslikov.fermacompose2.AlterDialigStart
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonCustom
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextFieldCustom
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarEdit
import com.zaroslikov.fermacompose2.navigate.NavigationDestination
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
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

    val coroutineScope = rememberCoroutineScope()

    AlterDialigStart(
        isFirstStart = isFirstStart,
        dialogTitle = stringResource(R.string.project_add_screen_install_project),
        dialogText = stringResource(R.string.project_add_screen_info),
        textAppMetrica = stringResource(R.string.project_add_screen_install_project)
    )

    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = stringResource(R.string.project_add_screen_create_project),
                navigateUp = navigateBack
            )
        }

    ) { innerPadding ->
        AddProjectContainer(
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
    var name by rememberSaveable{ mutableStateOf("") }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    //Текст

    val context = LocalContext.current

    var date1 by remember { mutableStateOf(formattedDate) }

    fun errorBoolean(): Boolean {
        isErrorTitle = name == ""
        return (!isErrorTitle)
    }

    val imageResources = listOf(
        R.drawable.livestock,
        R.drawable.icons_chicken_s,
        R.drawable.icons_goat,
        R.drawable.icons_cow,
        R.drawable.icons_pig,
        R.drawable.icons_sheep,
        R.drawable.icons_hourse,
        R.drawable.icons_rabbit,
        R.drawable.icons_farming_pets,
        R.drawable.icons_pets,
        R.drawable.icons_plant,
        R.drawable.icons_farming_1,
        R.drawable.icons_farming_2,
        R.drawable.baseline_add_photo_alternate_24
    )

    //Картинка
    var byteArray by remember { mutableStateOf(byteArrayOf()) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            byteArray = (uriToByteArray(context, imageUri) ?: getByteArrayFromDrawable(
                context,
                imageResources[0]
            ))
        }



    Column(modifier = modifier.padding(5.dp, 5.dp)) {
        Text(
            text = "Выберите фото проекта:",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )

        ImageLazyRow(
            images = imageResources,
            imageUri = imageUri,
            onImageSelected = {
                byteArray = getByteArrayFromDrawable(context, imageResources[it])
            },
            onAddImageClicked = {
                launcher.launch("image/*")
            },
            selectedImage = 0
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                validateTitle(name)
            },
            label = { Text(text = "Товар") },
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        text = "Не указано имя товара",
                        color = MaterialTheme.colorScheme.error
                    )
                } else Text("Введите или выберите товар")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            isError = isErrorTitle,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            )
        )


//
//        OutlinedTextFieldCustom(
//            value = name,
//            onValueChange = {
//                name = it
//                validateTitle(name)
//            },
//            label = stringResource(R.string.project_add_screen_name_project),
//            supportingText = stringResource(R.string.project_add_screen_name_project),
//            isError = isErrorTitle,
//            errorText = stringResource(R.string.project_add_screen_error_name_project),
//            modifier = Modifier
//                .padding(bottom = 2.dp),
//            keyboardOptions = KeyboardOptions(
//                imeAction = ImeAction.Next,
//                capitalization = KeyboardCapitalization.Sentences
//            )
//        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                validateTitle(name)
            },
            label = { Text(stringResource(R.string.project_add_screen_name_project)) },
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        text = stringResource(R.string.project_add_screen_error_name_project),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(stringResource(R.string.project_add_screen_name_project))
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            isError = isErrorTitle,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            )
        )


        OutlinedTextFieldCustom(
            value = date1,
            label = stringResource(R.string.project_add_screen_date),
            supportingText = stringResource(R.string.project_add_screen_project_creation_date),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { openDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_month_24),
                        contentDescription = "Показать меню"
                    )
                }
            },
            modifier = Modifier
                .padding(bottom = 2.dp)
                .clickable {
                    openDialog = true
                }
        )

        ButtonCustom(
            modifier = Modifier,
            intStringRes = R.string.button_begin,
            isError = errorBoolean(),
            onClick = {
                navigateToStart(
                    ProjectTable(
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
                        imageData = if (byteArray.contentEquals(byteArrayOf()))
                            getByteArrayFromDrawable(context, imageResources[0])
                        else byteArray
                    )
                )
                val eventParameters: MutableMap<String, Any> = HashMap()
                eventParameters["Имя"] = name
                AppMetrica.reportEvent("Project", eventParameters);
            }
        )

    }
    if (openDialog) {
        DatePickerDialogSample(state, date1) { date ->
            date1 = date
            openDialog = false
        }
    }
}


@Composable
fun ImageLazyRow(
    images: List<Int>,
    onImageSelected: (Int) -> Unit,
    onAddImageClicked: () -> Unit,
    imageUri: Uri?,
    selectedImage: Int = -1
) {
    var selectedImageIndex by remember { mutableStateOf(selectedImage) }

    val painter = rememberAsyncImagePainter(model = imageUri)

    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {

        items(images.size) { index ->
            val image = images[index]
            SelectableImageWithIcon(
                image = if (index == images.size - 1 && imageUri == null) painterResource(image)
                else if (index == images.size - 1 && imageUri != null) painter
                else painterResource(image),
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                },
                isSelected = selectedImageIndex == index
            ) {
                selectedImageIndex = index
                if (index == images.size - 1) onAddImageClicked() else onImageSelected(index)
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
            .wrapContentSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 3.dp else 2.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    ),
                    shape = CircleShape
                )
        ) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.75f)
                    .align(Alignment.Center)
            )
        }
        if (isSelected) {
            icon() // Отображаем значок, если элемент выбран
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


@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}

fun getByteArrayFromDrawable(context: Context, drawableResId: Int): ByteArray {
    // Загружаем ресурс как Bitmap
    val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)

    // Конвертируем Bitmap в ByteArray
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 15, stream)
    return stream.toByteArray()
}

fun uriToByteArray(context: Context, uri: Uri?): ByteArray? {
    if (uri == null) return null

    // Загружаем Bitmap из URI
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    // Преобразуем Bitmap в ByteArray
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}
package com.zaroslikov.fermacompose2.ui.add


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconGradient
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.elements.сompositions.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.white
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
    viewModel: ProjectAddViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Создание проекта", navigateUp = navigateBack)
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
    navigateToStart: (DomainProjectTable) -> Unit,
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
    val context = LocalContext.current

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
                        DomainProjectTable(
                            title = name,
                            date = date1,
                            dateEnd = date1,
                            mode = true,
                            /*imageData = if (byteArray.contentEquals(byteArrayOf()))
                                getByteArrayFromDrawable(context, imageResources[0])
                            else byteArray*/
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
                        painterResource(R.drawable.icon_check),
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



//
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
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun saveImageToInternalStorage(
    context: Context,
    bytes: ByteArray
): String {
    val file = File(
        context.filesDir,
        "project_${System.currentTimeMillis()}.jpg"
    )

    file.writeBytes(bytes)

    return file.absolutePath
}

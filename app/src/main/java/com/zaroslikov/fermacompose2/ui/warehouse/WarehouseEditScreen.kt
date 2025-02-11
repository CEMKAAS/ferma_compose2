package com.zaroslikov.fermacompose2.ui.warehouse

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.ImageLazyRow
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.add.getByteArrayFromDrawable
import com.zaroslikov.fermacompose2.ui.add.uriToByteArray
import com.zaroslikov.fermacompose2.ui.start.dateLong
import kotlinx.coroutines.launch
import java.util.Calendar


object WarehouseEditDestination : NavigationDestination {
    override val route = "ProjectAdd"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "${WarehouseEditDestination.route}/{$itemIdArg}"
}


@Composable
fun WarehouseEditScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    navigateToStart: () -> Unit,
    viewModel: WarehouseEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Редактировать Проект", navigateUp = navigateBack)
        }
    ) { innerPadding ->
        WarehouseEditContainer(project = viewModel.projectState,
            modifier = Modifier.padding(innerPadding),
            onValueChange = viewModel::updateUiState,
            saveInRoom = {
                if (!it) {
                    coroutineScope.launch {
                        viewModel.saveItem()
                        navigateUp()
                    }
                }
            },
            arhivInRoom = {
                if (!it) {
                    coroutineScope.launch {
                        viewModel.saveItem()
                        navigateToStart()
                    }
                }
            },
            deleteInRoom = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateToStart()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseEditContainer(
    project: IncubatorProjectEditState,
    modifier: Modifier,
    saveInRoom: (Boolean) -> Unit,
    arhivInRoom: (Boolean) -> Unit,
    deleteInRoom: () -> Unit,
    onValueChange: (IncubatorProjectEditState) -> Unit = {},
) {
    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = dateLong(project.data)
    )

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    if (openDialog) {
        DatePickerDialogSample(datePickerState, project.data) { date ->
            onValueChange(project.copy(data = date))
            openDialog = false
        }
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
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            onValueChange(project.copy(imageData = uriToByteArray(context,imageUri)
                ?: getByteArrayFromDrawable(context, R.drawable.livestock)))
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
                imageUri = imageUri,
                onImageSelected = {
                    onValueChange(project.copy(imageData = getByteArrayFromDrawable(context, imageResources[it])))
                },
                onAddImageClicked = {
                    launcher.launch("image/*")
                }
            )

        OutlinedTextField(
            value = project.titleProject,
            onValueChange = {
                onValueChange(project.copy(titleProject = it))
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
                imeAction = ImeAction.Next, capitalization = KeyboardCapitalization.Sentences
            )
        )

        OutlinedTextField(value = project.data,
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
                })

        Button(
            onClick = { saveInRoom(isErrorTitle) }, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_create_24),
                contentDescription = " Обновить "
            )
            Text(text = " Обновить ")
        }

        OutlinedButton(
            onClick = {
                onValueChange(project.copy(arhive = "1"))
                arhivInRoom(isErrorTitle)
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_archive_24),
                contentDescription = "В Архив"
            )
            Text(text = " В Архив")
        }

        OutlinedButton(
            onClick = { deleteInRoom() }, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = "Удалить"
            )
            Text(text = " Удалить ")
        }
    }
}

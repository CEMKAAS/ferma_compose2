package com.zaroslikov.fermacompose2.ui.warehouse

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import kotlinx.coroutines.launch


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
        },
//        bottomBar = {
//            Banner(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            )
//        }
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
    val datePickerState = rememberDatePickerState()

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

    Column(modifier = modifier.padding(5.dp, 5.dp)) {

        OutlinedTextField(value = project.titleProject,
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
            onClick = {

                saveInRoom(isErrorTitle)

            }, modifier = Modifier
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
            onClick = {deleteInRoom()}, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = "Удалить"
            )
            Text(text = " Удалить ")
        }
    }
}

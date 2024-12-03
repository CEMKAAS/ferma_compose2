package com.zaroslikov.fermacompose2.ui.animal

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.zaroslikov.fermacompose2.ui.start.dateLong
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


object AnimalEditDestination : NavigationDestination {
    override val route = "animalEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AnimalEditProduct(
    navigateBack: () -> Unit,
    navigateEdit: () -> Unit,
    navigateDelete: (String) -> Unit,
    viewModel: AnimalEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val animalEditUiState = viewModel.animaEditUiState
    val typeEditUiState = viewModel.typeUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Редактировать данные", navigateUp = navigateBack)
        }
    ) { innerPadding ->
        AnimalEditContainer(
            animalEditUiState = animalEditUiState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            typeList = typeEditUiState.value.titleList,
            onValueChange = viewModel::updateUiState,
            saveInRoomSale = {
                if (it) {
                    coroutineScope.launch {
                        viewModel.saveItem()
                        navigateEdit()
                    }
                }
            },
            deleteInRoom = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateDelete(it)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEditContainer(
    animalEditUiState: AnimalEditUiState,
    modifier: Modifier,
    typeList: List<String>,
    onValueChange: (AnimalEditUiState) -> Unit = {},
    saveInRoomSale: (Boolean) -> Unit,
    deleteInRoom: (String) -> Unit
) {
    val sexList = arrayListOf("Мужской", "Женский")

    var expandedSex by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorType by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var selectedItemIndex by remember { mutableStateOf(0) }

    var openDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = dateLong(animalEditUiState.data)
    )

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateType(text: String) {
        isErrorType = text == ""
    }
    fun validatePrice(text: String) {
        isErrorPrice = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = animalEditUiState.name == ""
        isErrorType = animalEditUiState.type == ""
        isErrorPrice = animalEditUiState.price == ""
        return !(isErrorTitle || isErrorType || isErrorPrice)
    }


    Column(modifier = modifier) {

        OutlinedTextField(
            value = animalEditUiState.name,
            onValueChange = {
                onValueChange(animalEditUiState.copy(name = it))
                validateTitle(it)
            },
            label = { Text(text = "Название") },
            supportingText = {
                if (isErrorTitle) {
                    Text(
                        text = "Не указана кличка животного или имя группы",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите кличку животного или группы")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            isError = isErrorTitle,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
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
                expanded = expandedType,
                onExpandedChange = {
                    expandedType = !expandedType
                }
            ) {
                OutlinedTextField(
                    value = animalEditUiState.type,
                    onValueChange = {
                        onValueChange(animalEditUiState.copy(type = it))
                        validateType(it)
                    },
                    label = { Text(text = "Тип") },
                    supportingText = {
                        if (isErrorType) {
                            Text(
                                text = "Не указан или не выбран тип животного",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Укажите или выберите тип животного")
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    isError = isErrorType,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }
                    )
                )
                val filteredOptions =
                    typeList.filter { it.contains(animalEditUiState.type, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = {
//                            expandedCat = false
                            // We shouldn't hide the menu when the user enters/removes any character
                        }
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    onValueChange(animalEditUiState.copy(type = item))
                                    expandedType = false
                                }
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = animalEditUiState.price,
            onValueChange = {
                onValueChange(animalEditUiState.copy(price = it.replace(Regex("[^\\d.]"), "").replace(",", ".")))
                validatePrice(it)
            },
            label = { Text("Стоимость") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            supportingText = {
                if (isErrorPrice) {
                    Text(
                        text = "Не указана стоимость животного!",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите за сколько купили животного")
                }
            },
            suffix = { Text(text = "₽") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }),
            isError = isErrorPrice
        )

        if (openDialog) {
            DatePickerDialogSample(datePickerState, animalEditUiState.data) { date ->
                openDialog = false
                onValueChange(
                    animalEditUiState.copy(
                        data = date
                    )
                )
            }
        }

        OutlinedTextField(
            value = animalEditUiState.data,
            readOnly = true,
            onValueChange = {},
            label = { Text("Дата рождения или завода") },
            supportingText = {
                Text("Выберите дату")
            },
            trailingIcon = {
                IconButton(onClick = { openDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_month_24),
                        contentDescription = "Показать Календарь"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    openDialog = true
                }
                .padding(vertical = 10.dp)
        )

        Text(
            text = "Вносим данные по группе или одному животному?",
            modifier = Modifier.padding(start = 6.dp)
        )

        Row(
            Modifier
                .selectableGroup()
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = animalEditUiState.groop,
                    onClick = {
                        onValueChange(
                            animalEditUiState.copy(
                                groop = true
                            )
                        )
                    },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" }
                )
                Text(text = "Группа")
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = !animalEditUiState.groop,
                    onClick = {
                        onValueChange(
                            animalEditUiState.copy(
                                groop = false
                            )
                        )
                    },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" },
                )
                Text(text = "Один")
            }
        }

        if (!animalEditUiState.groop) {
            Box {
                ExposedDropdownMenuBox(
                    expanded = expandedSex,
                    onExpandedChange = { expandedSex = !expandedSex },
                ) {
                    OutlinedTextField(
                        value = animalEditUiState.sex,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSex) },
                        label = { Text(text = "Пол") },
                        supportingText = {
                            Text("Выберите пол животного")
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
                        expanded = expandedSex,
                        onDismissRequest = { expandedSex = false }
                    ) {
                        sexList.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                    )
                                },
                                onClick = {
                                    selectedItemIndex = index
                                    onValueChange(
                                        animalEditUiState.copy(
                                            sex = sexList[selectedItemIndex]
                                        )
                                    )
                                    expandedSex = false
                                }
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = animalEditUiState.note,
            onValueChange = {
                onValueChange(animalEditUiState.copy(note = it))
            },
            label = { Text(text = "Примечание") },
            supportingText = {
                Text("Здесь может быть важная информация")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )

        Button(
            onClick = { saveInRoomSale(errorBoolean())},
            modifier = Modifier
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
            onClick = { deleteInRoom(animalEditUiState.idPT.toString()) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = "Удалить"
            )
            Text(text = " Удалить ")
        }
    }
}

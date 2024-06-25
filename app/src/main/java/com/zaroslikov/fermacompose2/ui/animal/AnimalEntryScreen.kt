package com.zaroslikov.fermacompose2.ui.animal

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
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
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryViewModel
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesTableInsert
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object AnimalEntryDestination : NavigationDestination {
    override val route = "animalEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AnimalEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AnimalEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val typeList by viewModel.typeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Добавить Животного", navigateUp = navigateBack)
        }
    ) { innerPadding ->
        AnimalEntryContainer(
            idPT = viewModel.itemId,
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            typeList = typeList.titleList,
            saveInRoomSale = {
                coroutineScope.launch {
                    viewModel.saveItem(
                        animalTable = it.animalTable,
                        animalCountTable = it.animalCountTable,
                        animalWeightTable = it.animalWeightTable,
                        animalSizeTable = it.animalSizeTable
                    )
                    onNavigateUp()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalEntryContainer(
    idPT: Int,
    modifier: Modifier,
    typeList: List<String>,
    saveInRoomSale: (AnimalEntryRoom) -> Unit
) {
    val sexList = arrayListOf("Мужской", "Женский")

    var title by remember { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf(sexList[0]) }
    var state by remember { mutableStateOf(true) }//выбор группы
    var weight by remember { mutableStateOf("10") }
    var size by remember { mutableStateOf("0") }
    var count by remember { mutableStateOf("1") }
    var note by remember { mutableStateOf("") }


    var expandedSex by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorType by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableStateOf(0) }


    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateType(text: String) {
        isErrorType = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = title == ""
        isErrorType = count == ""
        return !(isErrorTitle || isErrorType)
    }

    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    var date1 by remember { mutableStateOf(formattedDate) }

    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    datePickerState.setSelection(calendar.timeInMillis)

    if (openDialog) {
        DatePickerDialogSample(datePickerState, date1) { date ->
            date1 = date
            openDialog = false
        }
    }


    Column(modifier = modifier) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                validateTitle(title)
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
                .padding(bottom = 2.dp),
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
                    value = type,
                    onValueChange = {
                        type = it
                        validateType(type)
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
                        .padding(bottom = 2.dp),
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
                    typeList.filter { it.contains(type, ignoreCase = true) }
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
                                    type = item
                                    expandedType = false
                                }
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = date1,
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
                },
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
                    selected = state,
                    onClick = { state = true },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" }
                )
                Text(text = "Группа")
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = !state,
                    onClick = { state = false },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" },
                )
                Text(text = "Один")
            }
        }

        if (!state) {
            Box {
                ExposedDropdownMenuBox(
                    expanded = expandedSex,
                    onExpandedChange = { expandedSex = !expandedSex },
                ) {
                    OutlinedTextField(
                        value = sex,
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
                            .padding(bottom = 2.dp)
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
                                    expandedSex = false
                                    sex = sexList[selectedItemIndex]
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = it
                },
                label = { Text(text = "Вес") },
                supportingText = {
                    Text("Укажите вес животного")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                suffix = { Text(text = "кг.") },
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
                )
            )

            OutlinedTextField(
                value = size,
                onValueChange = {
                    size = it
                },
                label = { Text(text = "Размер") },
                supportingText = {
                    Text("Укажите размер Вашего животного")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
                )
            )
        } else {

            OutlinedTextField(
                value = count,
                onValueChange = {
                    count = it
                },
                label = { Text(text = "Колличество") },
                supportingText = {
                    Text("Укажите кол-во Вашей группы")
                },
                suffix = { Text(text = "кг.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
                )
            )
        }

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
            },
            label = { Text(text = "Примечание") },
            supportingText = {
                Text("Здесь может быть важная информация")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (errorBoolean()) {
                        saveInRoomSale(
                            AnimalEntryRoom(
                                AnimalTable(
                                    id = 0,
                                    name = title,
                                    type = type,
                                    data = date1,
                                    groop = state,
                                    sex = sex,
                                    note = note,
                                    image = "0",
                                    arhiv = false,
                                    idPT = idPT
                                ),
                                AnimalCountTable(
                                    id = 0,
                                    count = count,
                                    date = date1,
                                    idAnimal = idPT
                                ),
                                AnimalWeightTable(
                                    id = 0,
                                    weight = weight,
                                    date = date1,
                                    idAnimal = idPT
                                ),
                                AnimalSizeTable(
                                    id = 0,
                                    size = size,
                                    date = date1,
                                    idAnimal = idPT
                                )
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ) {
                Text(text = "Добавить")
            }
        }
    }
}

data class AnimalEntryRoom(
    val animalTable: AnimalTable,
    val animalCountTable: AnimalCountTable,
    val animalWeightTable: AnimalWeightTable,
    val animalSizeTable: AnimalSizeTable
)
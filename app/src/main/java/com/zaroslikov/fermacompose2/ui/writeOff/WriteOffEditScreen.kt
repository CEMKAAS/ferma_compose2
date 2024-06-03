package com.zaroslikov.fermacompose2.ui.writeOff

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleEditViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleTableUiState
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import kotlinx.coroutines.launch

object WriteOffEditDestination : NavigationDestination {
    override val route = "WriteOffEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemAddId"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}


@Composable
fun WriteOffEditProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: WriteOffEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current

    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    val animalUiState by viewModel.animalUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Изменить Списание", navigateUp = navigateBack)
        }
    ) { innerPadding ->

        WriteOffEditContainerProduct(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp),
            titleList = titleUiState.titleList,
            categoryList = categoryUiState.categoryList,
            animalList = animalUiState.animalList,
            writeOffTable = viewModel.itemUiState,
            onValueChange = viewModel::updateUiState,
            saveInRoomAdd = {
                if (it) {
                    coroutineScope.launch {
                        viewModel.saveItem()
                        Toast.makeText(
                            context,
                            "Обновлено: ${viewModel.itemUiState.title} ${viewModel.itemUiState.count} ${viewModel.itemUiState.suffix} за ${viewModel.itemUiState.priceAll} ₽",
                            Toast.LENGTH_SHORT
                        ).show()
                        onNavigateUp()
                    }
                }
            },
            deleteAdd = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    onNavigateUp()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteOffEditContainerProduct(
    modifier: Modifier,
    writeOffTable: WriteOffTableUiState,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<String>,
    onValueChange: (WriteOffTableUiState) -> Unit = {},
    saveInRoomAdd: (Boolean) -> Unit,
    deleteAdd: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var expandedSuf by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedAni by remember { mutableStateOf(false) }

    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var formattedDate = "${writeOffTable.day}.${writeOffTable.mount}.${writeOffTable.year}"

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableStateOf(0) }


    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateCount(text: String) {
        isErrorCount = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = writeOffTable.title == ""
        isErrorCount = writeOffTable.count == ""
        return !(isErrorTitle || isErrorCount)
    }


    Column(modifier = modifier) {
        Box {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = writeOffTable.title,
                    onValueChange = {
                        onValueChange(writeOffTable.copy(title = it))
                        validateTitle(it)
                    },
                    label = { Text(text = "Товар") },
                    supportingText = {
                        if (isErrorTitle) {
                            Text(
                                text = "Не указано имя товара",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Выберите или укажите товар")
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    isError = isErrorTitle,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }
                    )
                )

                val filteredOptions =
                    titleList.filter { it.contains(writeOffTable.title, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
//                            expanded = false
                        }
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    onValueChange(writeOffTable.copy(title = item))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Box {
            OutlinedTextField(
                value = writeOffTable.count,
                onValueChange = {
                    onValueChange(writeOffTable.copy(count = it))
                    validateCount(it)
                },
                label = { Text("Количество") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указано кол-во товара",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите кол-во товара, которое хотите сохранить на склад")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { expandedSuf = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Показать меню")
                    }
                },
                suffix = {
                    Text(text = writeOffTable.suffix)
                },
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
            DropdownMenu(
                expanded = expandedSuf,
                onDismissRequest = { expandedSuf = false },
                //todo чтобы был слева
            ) {
                DropdownMenuItem(
                    onClick = {
                        onValueChange(writeOffTable.copy(suffix = "Шт."))
                    },
                    text = { Text("Шт.") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(writeOffTable.copy(suffix = "Кг."))
                    },
                    text = { Text("Кг.") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(writeOffTable.copy(suffix = "Л."))
                    },
                    text = { Text("Л.") }
                )
            }

        }

        OutlinedTextField(
            value = writeOffTable.priceAll,
            onValueChange = {
                onValueChange(writeOffTable.copy(priceAll = it))
            },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите цену за списанный товар")
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
            }
            )
        )

        Box {
            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = {
                    expandedCat = !expandedCat
                }
            ) {
                OutlinedTextField(
                    value = writeOffTable.category,
                    onValueChange = { onValueChange(writeOffTable.copy(category = it)) },
                    label = { Text("Категория") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    supportingText = {
                        Text("Укажите или выберите категорию в которую хотите отнести товар")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }
                    )
                )

                val filteredOptions =
                    categoryList.filter { it.contains(writeOffTable.category, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = {
//                            expandedCat = false
                            // We shouldn't hide the menu when the user enters/removes any character
                        }
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    onValueChange(writeOffTable.copy(category = item))
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (animalList.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = expandedAni,
                onExpandedChange = { expandedAni = !expandedAni },
            ) {
                OutlinedTextField(
                    value = writeOffTable.animal,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAni) },
                    supportingText = {
                        Text("Выберите животное, которое принесло товар")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedAni,
                    onDismissRequest = { expandedAni = false }
                ) {
                    animalList.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                )
                            },
                            onClick = {
                                selectedItemIndex = index
                                expandedAni = false
                                onValueChange(writeOffTable.copy(animal = animalList[selectedItemIndex]))
                            }
                        )
                    }
                }
            }
        }


        if (openDialog) {
            DatePickerDialogSample(datePickerState, formattedDate) { date ->
                formattedDate = date
                openDialog = false
                val formattedDateList = formattedDate.split(".")
                onValueChange(
                    writeOffTable.copy(
                        day = formattedDateList[0].toInt(),
                        mount = formattedDateList[1].toInt(),
                        year = formattedDateList[2].toInt()
                    )
                )
            }
        }

        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата") },
            supportingText = {
                Text("Выберите дату ")
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
                .clickable {
                    openDialog = true
                },
        )

        Button(
            onClick = { saveInRoomAdd(errorBoolean()) },
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
            onClick = deleteAdd,
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
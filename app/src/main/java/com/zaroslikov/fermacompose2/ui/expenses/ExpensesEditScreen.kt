package com.zaroslikov.fermacompose2.ui.expenses

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.dateLong
import kotlinx.coroutines.launch

object ExpensesEditDestination : NavigationDestination {
    override val route = "ExpensesEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemAddId"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}


@Composable
fun ExpensesEditProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: ExpensesEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current

    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
//    val animalUiState2 by viewModel.animalUiState2.collectAsState()

    viewModel.itemlist2()
    val projectList = viewModel.items.value
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Изменить Покупку", navigateUp = navigateBack)
        }
    ) { innerPadding ->

        ExpensesEditContainerProduct(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            expensesTable = viewModel.itemUiState,
            titleList = titleUiState.titleList,
            categoryList = categoryUiState.categoryList,
//            animalList = animalUiState2.animalList,
            list = projectList,
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
            },
            sd = viewModel::updateUiState2
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpensesEditContainerProduct(
    modifier: Modifier,
    expensesTable: ExpensesTableUiState,
    titleList: List<String>,
    categoryList: List<String>,
//    animalList: List<AnimalExpensesList2>,
    list: List<AnimalExpensesList2>,
    onValueChange: (ExpensesTableUiState) -> Unit = {},
    saveInRoomAdd: (Boolean) -> Unit,
    deleteAdd: () -> Unit,
    sd: (AnimalExpensesList2) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var expandedSuf by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    var openDialog by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateCount(text: String) {
        isErrorCount = text == ""
    }

    fun validatePrice(text: String) {
        isErrorCount = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = expensesTable.title == ""
        isErrorCount = expensesTable.count == ""
        isErrorPrice = expensesTable.priceAll == ""
        return !(isErrorTitle || isErrorCount || isErrorPrice)
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
                    value = expensesTable.title,
                    onValueChange = {
                        onValueChange(expensesTable.copy(title = it))
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
                            Text("Введите или выберите товар")
                        }
                    },
                    modifier = Modifier
                        .menuAnchor()
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

                val filteredOptions =
                    titleList.filter { it.contains(expensesTable.title, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {}
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    onValueChange(expensesTable.copy(title = item))
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
                value = expensesTable.count,
                onValueChange = {
                    onValueChange(
                        expensesTable.copy(
                            count = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                        )
                    )
                    validateCount(it)
                },
                label = { Text("Количество") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
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
                    Text(text = expensesTable.suffix)
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
                        onValueChange(expensesTable.copy(suffix = "Шт."))
                    },
                    text = { Text("Шт.") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(expensesTable.copy(suffix = "Кг."))
                    },
                    text = { Text("Кг.") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(expensesTable.copy(suffix = "Л."))
                    },
                    text = { Text("Л.") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(expensesTable.copy(suffix = "м3"))
                    },
                    text = { Text("м3") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(expensesTable.copy(suffix = "Тн."))
                    },
                    text = { Text("Тн.") }
                )
                DropdownMenuItem(
                    onClick = {
                        onValueChange(expensesTable.copy(suffix = "М."))
                    },
                    text = { Text("М.") }
                )
            }
        }

        OutlinedTextField(
            value = expensesTable.priceAll,
            onValueChange = {
                onValueChange(
                    expensesTable.copy(
                        priceAll = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                    )
                )
                validatePrice(expensesTable.priceAll)
            },
            label = { Text("Стоимость") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            supportingText = {
                if (isErrorPrice) {
                    Text(
                        text = "Не указана стоимость за товар!",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите стоимость за купленный товар")
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
            }
            ),
            isError = isErrorPrice
        )

        Box {
            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = {
                    expandedCat = !expandedCat
                }
            ) {
                OutlinedTextField(
                    value = expensesTable.category,
                    onValueChange = { onValueChange(expensesTable.copy(category = it)) },
                    label = { Text("Категория") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 10.dp),
                    supportingText = {
                        Text("Укажите или выберите категорию в которую хотите отнести товар")
                    },
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
                    categoryList.filter { it.contains(expensesTable.category, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = {}
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    onValueChange(expensesTable.copy(category = item))
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }
            }
        }

        var formattedDate = String.format(
            "%02d.%02d.%d",
            expensesTable.day,
            expensesTable.mount,
            expensesTable.year
        )

        if (openDialog) {
            val datePickerState = rememberDatePickerState(
                selectableDates = PastOrPresentSelectableDates,
                initialSelectedDateMillis = dateLong(formattedDate)
            )
            DatePickerDialogSample(datePickerState, formattedDate) { date ->
                formattedDate = date
                openDialog = false
                val formattedDateList = formattedDate.split(".")
                onValueChange(
                    expensesTable.copy(
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
                }
                .padding(bottom = 10.dp),
        )

        OutlinedTextField(
            value = expensesTable.note,
            onValueChange = {
                onValueChange(
                    expensesTable.copy(
                        note = it
                    )
                )
            },
            label = { Text("Примечание") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            supportingText = {
                Text("Здесь может быть важная информация")

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


        Card {
            Column(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Text(
                    text = "Доп. настройки",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 10.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = expensesTable.food,
                        onCheckedChange = {
                            onValueChange(
                                expensesTable.copy(
                                    food = it,
                                    showWarehouse = it
                                )
                            )
                        },
                        enabled = expensesTable.count != ""
                    )
                    Text(text = "Корм")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = expensesTable.showWarehouse,
                        onCheckedChange = { onValueChange(expensesTable.copy(showWarehouse = it)) },
                        enabled = if (expensesTable.count == "") {
                            false
                        } else if (expensesTable.food) {
                            false
                        } else true
                    )
                    Text(text = "Отображать на складе")
                }
            }

            if (expensesTable.food && (expensesTable.count != "")) {

//                Text(
//                    text = "${if (expensesTable.title == "") "Корма" else expensesTable.title} хватит на $day",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 14.dp)
//                        .padding(top = 5.dp)
//                )


                Text(
                    text = "Выбрать животных для рассчета ежедневного расхода",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 5.dp)
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 5.dp)
                ) {
//                    val itemList = remember { mutableStateListOf(list.toTypedArray()) }


//                    if (itemList.isNotEmpty()) {

                    list.forEachIndexed { index, it ->
//                        if (list.isNotEmpty()) {
//                            selected = list.contains(it.id.toLong())
//                        }
                        var sp by rememberSaveable { mutableStateOf(it.ps) }
                        FilterChip(
                            onClick = {
                                sp = !sp
//                                if (selected) {
//                                    foodDay2 += it.foodDay
//                                    countAnimal2 += it.countAnimal
//                                } else {
//                                    foodDay2 -= it.foodDay
//                                    countAnimal2 -= it.countAnimal
//                                    if (foodDay2 < 0) foodDay2 = 0
//                                    if (countAnimal2 < 0) countAnimal2 = 0
//                                }
//                                foodDay = foodDay2.toString()
//                                countAnimal = countAnimal2.toString()
//                                day = settingDay(date1, count.toDouble(), foodDay2, countAnimal2).second
                            },
                            label = {
                                Text(it.name)
                            },
                            selected = sp,
                            leadingIcon = if (sp) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
                            modifier = Modifier.padding(4.dp)

                        )
                    }
                }
            }

            Text(
                text = "Указать ежедневный расход в ручную",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(top = 5.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                    OutlinedTextField(
//                        value = foodDay,
//                        onValueChange = {
//                            foodDay = it.replace(Regex("[^\\d.]"), "")
//                            foodDay2 = 0
//                            countAnimal2 = 0
//                            foodDay2 = if (foodDay == "") 0 else foodDay.toInt()
//                            countAnimal2 = if (countAnimal == "") 0 else countAnimal.toInt()
//                            day = settingDay(date1, count.toDouble(), foodDay2, countAnimal2).second
//                        },
//                        label = { Text("Расход") },
//                        modifier = Modifier
//                            .fillMaxWidth(0.5f)
//                            .padding(4.dp),
//                        suffix = { Text(text = suffix) },
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Next
//                        ),
//                        keyboardActions = KeyboardActions(onNext = {
//                            focusManager.moveFocus(
//                                FocusDirection.Down
//                            )
//                        }
//                        )
//                    )

//                    OutlinedTextField(
//                        value = countAnimal,
//                        onValueChange = {
//                            countAnimal = it.replace(Regex("[^\\d.]"), "")
//                            countAnimal2 = 0
//                            foodDay2 = 0
//                            foodDay2 = if (foodDay == "") 0 else foodDay.toInt()
//                            countAnimal2 = if (countAnimal == "") 0 else countAnimal.toInt()
//                            day = settingDay(date1, count.toDouble(), foodDay2, countAnimal2).second
//                        },
//                        label = { Text("Кол-во голов") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(4.dp)
//                            .padding(bottom = 10.dp),
//                        suffix = { Text(text = "Шт.") },
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Next
//                        ),
//                        keyboardActions = KeyboardActions(onNext = {
//                            focusManager.moveFocus(
//                                FocusDirection.Down
//                            )
//                        }
//                        )
//                    )
            }
        }



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

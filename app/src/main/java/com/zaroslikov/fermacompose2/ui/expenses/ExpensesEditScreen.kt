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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.zaroslikov.fermacompose2.ui.start.formatter
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
    val animalList = mutableListOf<AnimalExpensesList2>()
    val projectList = viewModel.items.value
    animalList.addAll(projectList)
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
            animalList = animalList,
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
    animalList: MutableList<AnimalExpensesList2>,
    onValueChange: (ExpensesTableUiState) -> Unit = {},
    saveInRoomAdd: (Boolean) -> Unit,
    deleteAdd: () -> Unit,
    sd: (AnimalExpensesList2) -> Unit = {},
) {

    // Для расчета

    var countAnimal by remember { mutableIntStateOf(0) } //кол-во животных
    var foodDesignedDay by remember { mutableDoubleStateOf(expensesTable.dailyExpensesFood) }

    var foodDesignedDayUI by remember { mutableStateOf(Pair<Int, String>(0, "")) }
    var dailyExpensesFoodTotal by remember { mutableDoubleStateOf(0.0) } // Общий ежедневный расход

    countAnimal = expensesTable.countAnimal
    dailyExpensesFoodTotal = expensesTable.dailyExpensesFood

    // Ошибки
    var expanded by remember { mutableStateOf(false) }
    var expandedSuf by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    var openDialog by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorDailyExpensesFood by rememberSaveable { mutableStateOf(false) }
    var isErrorСountAnimalUI by rememberSaveable { mutableStateOf(false) }


    val focusManager = LocalFocusManager.current

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateCount(text: String) {
        if (text == "") {
            isErrorCount = true
            onValueChange(
                expensesTable.copy(
                    showFood = false,
                    showAnimals = false,
                    dailyExpensesFoodAndCount = false,
                    dailyExpensesFood = 0.0,
                    countAnimal = 0
                )
            )
        }
    }

    fun validatePrice(text: String) {
        if (text == "") {
            isErrorPrice = true
            onValueChange(expensesTable.copy(showAnimals = false))
        }
    }

    fun validateDailyExpensesFood(text: String) {
        isErrorDailyExpensesFood = text == ""
    }

    fun validateСountAnimalUI(text: String) {
        isErrorСountAnimalUI = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = expensesTable.title == ""
        isErrorCount = expensesTable.count == ""
        isErrorPrice = expensesTable.priceAll == ""
        isErrorDailyExpensesFood = expensesTable.dailyExpensesFood.toString() == ""
        isErrorСountAnimalUI = expensesTable.countAnimal.toString() == ""

        return if (expensesTable.dailyExpensesFoodAndCount) {
            !(isErrorTitle || isErrorCount || isErrorPrice || isErrorСountAnimalUI || isErrorDailyExpensesFood)
        } else !(isErrorTitle || isErrorCount || isErrorPrice)
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

        // ВЫКЛЮЧАТЕЛИ
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
                        checked = expensesTable.showFood,
                        onCheckedChange = {
                            onValueChange(
                                expensesTable.copy(
                                    showFood = it,
                                    showWarehouse = it,
                                    showAnimals = if (it) false else false
                                )
                            )
                        },
                        enabled = expensesTable.count != ""
                    )
                    Text(text = "Корм")

                    if (expensesTable.showFood && (expensesTable.count != "")) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 5.dp)
                        ) {
                            Checkbox(
                                checked = expensesTable.dailyExpensesFoodAndCount,
                                onCheckedChange = {
                                    onValueChange(expensesTable.copy(dailyExpensesFoodAndCount = it))
//                                    dailyExpensesFoodTotal = 0.0
//                                    countAnimal = 0
                                    foodDesignedDayUI = settingDay(
                                        formattedDate,
                                        expensesTable.count.toDouble(),
                                        expensesTable.dailyExpensesFood,
                                        expensesTable.countAnimal
//                                        if (expensesTable.dailyExpensesFood == "") 0.0 else dailyExpensesFoodUI.toDouble(),
//                                        if (expensesTable.countAnimal == "") 0 else countAnimalUI.toInt()
                                    )

                                }
                            )
                            Text(text = "Указать вручную")
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = expensesTable.showWarehouse,
                        onCheckedChange = { onValueChange(expensesTable.copy(showWarehouse = it)) },
                        enabled = if (expensesTable.count == "") {
                            false
                        } else if (expensesTable.showFood) {
                            false
                        } else true
                    )
                    Text(text = "Отображать на складе")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = expensesTable.showAnimals,
                        onCheckedChange = {
                            onValueChange(expensesTable.copy(showAnimals = it))
                            animalListClean(animalList)
                        },
                        enabled = if (expensesTable.count == "" || expensesTable.priceAll == "") {
                            false
                        } else if (expensesTable.showFood) {
                            false
                        } else true
                    )
                    Text(text = "Распределить расходы по животным")
                }
            }


            // КОРМА
            if (expensesTable.showFood && (expensesTable.count != "")) {
                Text(
                    text = "${if (expensesTable.title == "") "Корма" else expensesTable.title} хватит на ${if (foodDesignedDayUI.first >= 1000) "более" else ""} ${foodDesignedDayUI.first} суток до ${foodDesignedDayUI.second}\n" +
//                            "Ежедневный расход составляет - ${if (expensesTable.dailyExpensesFoodAndCount) dailyExpensesFoodUI else dailyExpensesFoodTotal} ${expensesTable.suffix}\n" +
//                            "Кол-во голов -  ${if (expensesTable.dailyExpensesFoodAndCount) countAnimalUI else countAnimal} шт. ",
                            "Ежедневный расход составляет - ${if (expensesTable.dailyExpensesFoodAndCount) expensesTable.dailyExpensesFood else dailyExpensesFoodTotal} ${expensesTable.suffix}\n" +
                            "Кол-во голов -  ${if (expensesTable.dailyExpensesFoodAndCount) expensesTable.countAnimal else countAnimal} шт. ",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 5.dp)
                )

                if (!expensesTable.dailyExpensesFoodAndCount) {
                    Text(
                        text = "Выберите животных для рассчета длительности корма",
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
                        if (animalList.isNotEmpty()) {
                            animalList.forEach {
                                var selected by remember { mutableStateOf(it.ps) }

                                FilterChip(
                                    onClick = {
                                        selected = !selected
                                        if (selected) {
                                            countAnimal += it.countAnimal
                                            dailyExpensesFoodTotal += (it.foodDay * it.countAnimal)
                                            it.presentException = (it.foodDay/dailyExpensesFoodTotal) * 100.0
                                        } else {
                                            countAnimal -= it.countAnimal
                                            dailyExpensesFoodTotal -= (it.foodDay * it.countAnimal)
                                        }
                                        it.ps = selected
                                        foodDesignedDayUI = settingDay(
                                            formattedDate,
                                            expensesTable.count.toDouble(),
                                            dailyExpensesFoodTotal
                                        )
                                    },
                                    label = { Text(it.name) },
                                    selected = if (expensesTable.dailyExpensesFoodAndCount) {
                                        selected = false
                                        false
                                    } else selected,
                                    leadingIcon = if (selected) {
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
                        } else {
                            Text(text = "Нет животных")
                        }
                    }
                }

                if (expensesTable.dailyExpensesFoodAndCount) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = expensesTable.dailyExpensesFood.toString(),
                            onValueChange = {
                                onValueChange(expensesTable.copy(dailyExpensesFood = it.toDouble()))
//                                dailyExpensesFoodUI = it.replace(Regex("[^\\d.]"), "")
                                validateDailyExpensesFood(it)
                                foodDesignedDayUI = settingDay(
                                    formattedDate,
                                    expensesTable.count.toDouble(),
                                    expensesTable.dailyExpensesFood,
                                    expensesTable.countAnimal
//                                    if (expensesTable.dailyExpensesFood == "") 0.0 else dailyExpensesFoodUI.toDouble(),
//                                    if (expensesTable.countAnimal == "") 0 else countAnimalUI.toInt()
                                )

                            },
                            isError = isErrorDailyExpensesFood,
                            supportingText = {
                                if (isErrorDailyExpensesFood) {
                                    Text(
                                        text = "Нет значений!",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else Text("Укажите ежедневный расход")
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(4.dp),
                            suffix = { Text(text = expensesTable.suffix) },
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

                        OutlinedTextField(
                            value = expensesTable.countAnimal.toString(),
                            onValueChange = {
                                onValueChange(expensesTable.copy(countAnimal = it.toInt()))
//                                countAnimalUI = it.replace(Regex("[^\\d.]"), "")
                                validateСountAnimalUI(it)
                                foodDesignedDayUI = settingDay(
                                    formattedDate,
                                    expensesTable.count.toDouble(),
                                    expensesTable.dailyExpensesFood,
                                    expensesTable.countAnimal
                                )
//                                    if (expensesTable.dailyExpensesFood == "") 0.0 else dailyExpensesFoodUI.toDouble(),
//                                    if (expensesTable.countAnimal == "") 0 else countAnimalUI.toInt()
//                                )
                            },
                            isError = isErrorСountAnimalUI,
                            supportingText = {
                                if (isErrorСountAnimalUI) {
                                    Text(
                                        text = "Нет значений!",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else Text("Укажите кол-во животных")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .padding(bottom = 10.dp),
                            suffix = { Text(text = "Шт.") },
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
                    }
                }
            }

            //РАСПРЕДЕЛЕНИЕ РАСХОДОВ
            if (expensesTable.showAnimals) {
                val totalFood by remember { mutableFloatStateOf(100f) }

                Column {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        if (animalList.isNotEmpty()) {

                            animalList.forEachIndexed { index, animal ->
                                var selected by remember { mutableStateOf(animal.ps) }
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        selected = !selected

//                                        if (selected) animal.ps = true
//                                        else animal.ps = false
                                        animalList.forEach {
                                            animalList[index].presentException =
                                                (totalFood / animalList.size).toDouble()
                                        }
                                    },
                                    label = { Text(animal.name) },
                                    leadingIcon = if (selected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = "Done icon",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else null,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                )
                            }
                        } else {
                            Text(text = "Нет животных")
                        }
                    }

                    // Отображаем слайдеры для каждого выбранного животного
                    animalList.forEachIndexed { index, animal ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 5.dp)
                        ) {

                            Text(
                                text = "" +
//                                        "${animalList.find { it.id.toLong() == animal.key }?.name}: " +
                                        "${
                                            formatter(
                                                (animal.presentException ?: 1.0) * expensesTable.count.toDouble() / 100.0
                                            )
                                        } " +
//                                        "$suffix /" +
                                        " ${formatter((animal.presentException ?: 1.0) * expensesTable.priceAll.toDouble() / 100.0)} ₽" +
                                        " -  ${formatter(animal.presentException ?: 1.0)}%",
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                            )

                            Slider(
                                value = (animal.presentException ?: 0).toFloat(),
                                onValueChange = {
                                    animal.presentException = it.toDouble()
                                    // Пересчитываем значения для остальных животных
                                    val remainingFood = totalFood - it
                                    val otherAnimalsCount = animalList.size - 1

                                    animalList.forEachIndexed { indexA, otherAnimal ->
                                        if (indexA != index) {
                                            animalList[indexA].presentException =
                                                (remainingFood / otherAnimalsCount).toDouble()
                                        }
                                    }
                                },
                                valueRange = 0f..totalFood,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp, vertical = 2.5.dp)
                            )
                        }
                    }
                }
            }
        }


        //ПОВТОР
//        Card {
//            Column(
//                Modifier
//                    .selectableGroup()
//                    .fillMaxWidth()
//                    .padding(10.dp),
//            ) {
//                Text(
//                    text = "Доп. настройки",
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 14.dp)
//                        .padding(top = 10.dp)
//                )
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Checkbox(
//                        checked = expensesTable.showFood,
//                        onCheckedChange = {
//                            onValueChange(
//                                expensesTable.copy(
//                                    showFood = it,
//                                    showWarehouse = it
//                                )
//                            )
//                        },
//                        enabled = expensesTable.count != ""
//                    )
//                    Text(text = "Корм")
//                }
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Checkbox(
//                        checked = expensesTable.showWarehouse,
//                        onCheckedChange = { onValueChange(expensesTable.copy(showWarehouse = it)) },
//                        enabled = if (expensesTable.count == "") {
//                            false
//                        } else if (expensesTable.showFood) {
//                            false
//                        } else true
//                    )
//                    Text(text = "Отображать на складе")
//                }
//            }
//
//            if (expensesTable.showFood && (expensesTable.count != "")) {

//                Text(
//                    text = "${if (expensesTable.title == "") "Корма" else expensesTable.title} хватит на $day",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 14.dp)
//                        .padding(top = 5.dp)
//                )


//                Text(
//                    text = "Выбрать животных для рассчета ежедневного расхода",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 14.dp)
//                        .padding(top = 5.dp)
//                )
//                FlowRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 14.dp)
//                        .padding(top = 5.dp)
//                ) {
//                    val itemList = remember { mutableStateListOf(list.toTypedArray()) }


//                    if (itemList.isNotEmpty()) {

//                    list.forEachIndexed { index, it ->
//                        if (list.isNotEmpty()) {
//                            selected = list.contains(it.id.toLong())
//                        }
//                        var sp by rememberSaveable { mutableStateOf(it.ps) }
//                        FilterChip(
//                            onClick = {
//                                sp = !sp
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
//                            },
//                            label = {
//                                Text(it.name)
//                            },
//                            selected = sp,
//                            leadingIcon = if (sp) {
//                                {
//                                    Icon(
//                                        imageVector = Icons.Filled.Done,
//                                        contentDescription = "Done icon",
//                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
//                                    )
//                                }
//                            } else null,
//                            modifier = Modifier.padding(4.dp)
//
//                        )
//                    }
//                }
//            }
//
//            Text(
//                text = "Указать ежедневный расход в ручную",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 14.dp)
//                    .padding(top = 5.dp)
//            )
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
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
//            }
//        }


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


fun animalListClean(list: MutableList<AnimalExpensesList2>) {
    list.forEach {
        it.ps = false
    }
}
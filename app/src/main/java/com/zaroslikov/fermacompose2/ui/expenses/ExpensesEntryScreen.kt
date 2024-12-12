package com.zaroslikov.fermacompose2.ui.expenses

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object ExpensesEntryDestination : NavigationDestination {
    override val route = "ExpensesEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun ExpensesEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: ExpensesEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    val animalUiState by viewModel.animalUiState.collectAsState()

    val idProject = viewModel.itemId

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Добавить Покупку", navigateUp = navigateBack)
        }
    ) { innerPadding ->
        ExpensesEntryContainerProduct(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.titleList,
            categoryList = categoryUiState.categoryList,
            animalList = animalUiState.animalList,
            saveInRoomSale = {
                coroutineScope.launch {
                    viewModel.saveItem(
                        ExpensesTable(
                            id = it.first.id,
                            title = it.first.title,
                            count = it.first.count,
                            day = it.first.day,
                            mount = it.first.mount,
                            year = it.first.year,
                            priceAll = it.first.priceAll,
                            suffix = it.first.suffix,
                            category = it.first.category,
                            idPT = idProject,
                            note = it.first.note,
                            showFood = it.first.showFood,
                            showWarehouse = it.first.showWarehouse,
                            showAnimals = it.first.showAnimals,
                            dailyExpensesFood = it.first.dailyExpensesFood,
                            countAnimal = it.first.countAnimal,
                            foodDesignedDay = it.first.foodDesignedDay,
                            lastDayFood = it.first.lastDayFood
                        ), it.second
                    )
                    Toast.makeText(
                        context,
                        "Куплено: ${it.first.title} ${it.first.count} ${it.first.suffix} за ${it.first.priceAll} ₽",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateUp()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpensesEntryContainerProduct(
    modifier: Modifier,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<AnimalExpensesList>,
    saveInRoomSale: (Pair<ExpensesTableInsert, MutableMap<Long, Double>>) -> Unit
) {
    var title by remember { mutableStateOf("") } // Имя
    var count by rememberSaveable { mutableStateOf("") } // кол-во
    var category by remember { mutableStateOf("Без категории") } // Категория
    var suffix by remember { mutableStateOf("Шт.") } // Единица измерения
    var priceAll by remember { mutableStateOf("") } // Цена за все
    var note by remember { mutableStateOf("") } // Примечание
    var showFoodUI by remember { mutableStateOf(false) } // Корм
    var showWarehouseUI by remember { mutableStateOf(false) } // Показать на складе
    var showAnimalsUI by remember { mutableStateOf(false) } // Расход на животных
    var dailyExpensesFoodUI by remember { mutableStateOf("") } //Ежедневный расход
    var countAnimalUI by remember { mutableStateOf("") } // Кол-во животных
    var foodDesignedDayUI by remember { mutableStateOf(Pair<Int, String>(0, "")) } // Кол-во дней
    var dailyExpensesFoodTotal by remember { mutableStateOf(0.0) } // Общий ежедневный расход
    var setDailyExpensesFoodAndCountUI by remember { mutableStateOf(false) } // Уставновить ежедневный расход

    // Для расчета
    var countAnimal by remember { mutableStateOf(0) }

    // Ошибки
    var expanded by remember { mutableStateOf(false) }
    var expandedSuf by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorDailyExpensesFood by rememberSaveable { mutableStateOf(false) }
    var isErrorСountAnimalUI by rememberSaveable { mutableStateOf(false) }

    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateCount(text: String) {
        if (text == "") {
            isErrorCount = true
            showFoodUI = false
            showAnimalsUI = false
        }
    }

    fun validatePrice(text: String) {
        if (text == "") {
            isErrorPrice = true
            showAnimalsUI = false
        }
    }

    fun validateDailyExpensesFood(text: String) {
        isErrorDailyExpensesFood = text == ""
    }

    fun validateСountAnimalUI(text: String) {
        isErrorСountAnimalUI = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorTitle = title == ""
        isErrorCount = count == ""
        isErrorPrice = priceAll == ""

        return !(isErrorTitle || isErrorCount || isErrorPrice)
    }

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

    var date1 by remember { mutableStateOf(formattedDate) }

    if (openDialog) {
        DatePickerDialogSample(state, date1) { date ->
            date1 = date
            openDialog = false
        }
    }

    // Прочее
    if (count == "") {
        showFoodUI = false
        showWarehouseUI = false
    }
    var selectedFilters2 = remember { mutableStateMapOf<Long, Double>() }
    val focusManager = LocalFocusManager.current

    // Начало дизайна
    Column(modifier = modifier) {
        Box {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        validateTitle(title)
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
                    titleList.filter { it.contains(title, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {}
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    title = item
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
                value = count,
                onValueChange = {
                    count = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                    validateCount(count)

                    if (showFoodUI && !setDailyExpensesFoodAndCountUI) {
                        foodDesignedDayUI = settingDay(
                            date1,
                            if (count == "") 0.0 else count.toDouble(),
                            dailyExpensesFoodTotal
                        )
                    }

                    if (showFoodUI && setDailyExpensesFoodAndCountUI) {
                        foodDesignedDayUI = settingDay(
                            date1,
                            if (count == "") 0.0 else count.toDouble(),
                            if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
                            if (countAnimalUI == "") 0 else countAnimalUI.toInt()
                        )
                    }
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
                    Text(text = suffix)
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
                    onClick = { suffix = "Шт." },
                    text = { Text("Шт.") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "Кг." },
                    text = { Text("Кг.") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "Л." },
                    text = { Text("Л.") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "м3" },
                    text = { Text("м3") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "Тн." },
                    text = { Text("Тн.") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "М." },
                    text = { Text("М.") }
                )
            }
        }

        OutlinedTextField(
            value = priceAll,
            onValueChange = {
                priceAll = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                validatePrice(priceAll)
            },
            label = { Text("Стоимость") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            isError = isErrorPrice,
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
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 10.dp),
                    trailingIcon = {
                        IconButton(onClick = { category = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Стереть")
                        }
                    },
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
                    categoryList.filter { it.contains(category, ignoreCase = true) }
                if (filteredOptions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = {}
                    ) {
                        filteredOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    category = item
                                    expandedCat = false
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
            readOnly = true,
            label = { Text("Дата") },
            supportingText = {
                Text("Выберите дату")
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

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
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
                capitalization = KeyboardCapitalization.Sentences
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
                        checked = showFoodUI,
                        onCheckedChange = {
                            showFoodUI = it
                            showWarehouseUI = it
                            if (showFoodUI) showAnimalsUI = false
                            selectedFilters2.clear()
                        },
                        enabled = count != ""
                    )
                    Text(text = "Корм")

                    if (showFoodUI && (count != "")) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 5.dp)
                        ) {
                            Checkbox(
                                checked = setDailyExpensesFoodAndCountUI,
                                onCheckedChange = {
                                    setDailyExpensesFoodAndCountUI = !setDailyExpensesFoodAndCountUI
                                    dailyExpensesFoodTotal = 0.0
                                    countAnimal = 0
                                    foodDesignedDayUI = settingDay(
                                        date1,
                                        count.toDouble(),
                                        if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
                                        if (countAnimalUI == "") 0 else countAnimalUI.toInt()
                                    )
                                    selectedFilters2.clear()

                                }
                            )
                            Text(text = "Указать вручную")
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showWarehouseUI,
                        onCheckedChange = { showWarehouseUI = it },
                        enabled = if (count == "") {
                            false
                        } else if (showFoodUI) {
                            false
                        } else true
                    )
                    Text(text = "Отображать на складе")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showAnimalsUI,
                        onCheckedChange = {
                            showAnimalsUI = it
                            selectedFilters2.clear()
                        },
                        enabled = if (count == "" || priceAll == "") {
                            false
                        } else if (showFoodUI) {
                            false
                        } else true
                    )
                    Text(text = "Распределить расходы")
                }
            }


            // КОРМА
            if (showFoodUI && (count != "")) {
                Text(
                    text = "${if (title == "") "Корма" else title} хватит на ${if (foodDesignedDayUI.first >= 1000) "более" else ""} ${foodDesignedDayUI.first} суток до ${foodDesignedDayUI.second}\n" +
                            "Ежедневный расход составляет - ${if (setDailyExpensesFoodAndCountUI) dailyExpensesFoodUI else dailyExpensesFoodTotal} $suffix\n" +
                            "Кол-во голов -  ${if (setDailyExpensesFoodAndCountUI) countAnimalUI else countAnimal} шт. ",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 5.dp)
                )

                if (!setDailyExpensesFoodAndCountUI) {
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
                                var selected by remember { mutableStateOf(false) }

                                FilterChip(
                                    onClick = {
                                        selected = !selected
                                        if (selected) {
                                            countAnimal += it.countAnimal
                                            dailyExpensesFoodTotal += (it.foodDay * it.countAnimal)
                                            selectedFilters2.set(
                                                it.id.toLong(),
                                                ((it.foodDay / dailyExpensesFoodTotal) * 100.0)
                                            )
                                        } else {
                                            countAnimal -= it.countAnimal
                                            dailyExpensesFoodTotal -= (it.foodDay * it.countAnimal)
                                            selectedFilters2.remove(it.id.toLong())
                                        }
                                        foodDesignedDayUI = settingDay(
                                            date1,
                                            count.toDouble(),
                                            dailyExpensesFoodTotal
                                        )
                                    },
                                    label = { Text(it.name) },
                                    selected = if (setDailyExpensesFoodAndCountUI) {
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

                if (setDailyExpensesFoodAndCountUI) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = dailyExpensesFoodUI,
                            onValueChange = {
                                dailyExpensesFoodUI = it.replace(Regex("[^\\d.]"), "")
                                validateDailyExpensesFood(it)
                                foodDesignedDayUI = settingDay(
                                    date1,
                                    count.toDouble(),
                                    if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
                                    if (countAnimalUI == "") 0 else countAnimalUI.toInt()
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
                            suffix = { Text(text = suffix) },
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
                            value = countAnimalUI,
                            onValueChange = {
                                countAnimalUI = it.replace(Regex("[^\\d.]"), "")
                                validateСountAnimalUI(it)
                                foodDesignedDayUI = settingDay(
                                    date1,
                                    count.toDouble(),
                                    if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
                                    if (countAnimalUI == "") 0 else countAnimalUI.toInt()
                                )
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
            if (showAnimalsUI) {
                val totalFood by remember { mutableFloatStateOf(100f) }

                Column {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .padding(top = 5.dp)
                    ) {
                        if (animalList.isNotEmpty()) {
                            animalList.forEach { animal ->
                                var selected by remember { mutableStateOf(false) }
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        selected = !selected
                                        if (selected) selectedFilters2.set(animal.id.toLong(), 0.0)
                                        else selectedFilters2.remove(animal.id.toLong())
                                        selectedFilters2.forEach {
                                            selectedFilters2[it.key] =
                                                (totalFood / selectedFilters2.size).toDouble()
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
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        } else {
                            Text(text = "Нет животных")
                        }
                    }

                    // Отображаем слайдеры для каждого выбранного животного
                    selectedFilters2.forEach { animal ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 10.dp)
                        ) {

                            Text(
                                text = "${animal.key}: ${formatter(animal.value * count.toDouble() / 100.0)} $suffix /" +
                                        " ${formatter(animal.value * priceAll.toDouble() / 100.0)} ₽" +
                                        " -  ${formatter(animal.value)}%",
                                fontSize = 20.sp
                            )

                            Slider(
                                value = animal.value.toFloat(),
                                onValueChange = {
                                    selectedFilters2[animal.key] = it.toDouble()
                                    // Пересчитываем значения для остальных животных
                                    val remainingFood = totalFood - it
                                    val otherAnimalsCount = selectedFilters2.size - 1

                                    selectedFilters2.forEach { otherAnimal ->
                                        if (otherAnimal.key != animal.key) {
                                            selectedFilters2[otherAnimal.key] =
                                                (remainingFood / otherAnimalsCount).toDouble()

                                        }
                                    }
                                },
                                valueRange = 0f..totalFood
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (errorBoolean()) {
                    val formattedDateList = date1.split(".")
                    saveInRoomSale(
                        Pair(
                            ExpensesTableInsert(
                                id = 0,
                                title = title,
                                count = count.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                    .toDouble(),
                                day = formattedDateList[0].toInt(),
                                mount = formattedDateList[1].toInt(),
                                year = formattedDateList[2].toInt(),
                                suffix = suffix,
                                category = category,
                                priceAll = priceAll.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                    .toDouble(),
                                note = note,
                                showFood = showFoodUI,
                                showWarehouse = showWarehouseUI,
                                showAnimals = showAnimalsUI,
                                dailyExpensesFood = if (setDailyExpensesFoodAndCountUI) dailyExpensesFoodUI.toDouble() else dailyExpensesFoodTotal,
                                countAnimal = if (setDailyExpensesFoodAndCountUI) countAnimalUI.toInt() else countAnimal,
                                foodDesignedDay = foodDesignedDayUI.first,
                                lastDayFood = foodDesignedDayUI.second
                            ),
                            selectedFilters2
                        )
                    )
                    val eventParameters: MutableMap<String, Any> = HashMap()
                    eventParameters["Имя"] = title
                    eventParameters["Кол-во"] = "$title $count $suffix $priceAll₽"
                    eventParameters["Категория"] = category
                    eventParameters["Примечание"] = note
                    AppMetrica.reportEvent("Expenses Products", eventParameters)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
        ) {
            Text(text = "Купить")
        }
    }
}

fun settingDay(
    date: String,
    count: Double,
    foodDay: Double = 0.0,
    countAnimal: Int = 0
): Pair<Int, String> {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateLocal = LocalDate.parse(date, formatter)
    var days = (count / (foodDay * countAnimal.toDouble())).toLong()
    if (days > 1000) days = 1000
    val newDate = dateLocal.plusDays(days)
    return Pair(
        days.toInt(),
        newDate.format(formatter)
    )
}

fun settingDay(
    date: String,
    count: Double,
    dailyExpensesFoodTotal: Double
): Pair<Int, String> {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateLocal = LocalDate.parse(date, formatter)
    var days = (count / (dailyExpensesFoodTotal)).toLong()
    if (days > 1000) days = 1000
    val newDate = dateLocal.plusDays(days)
    return Pair(
        days.toInt(),
        newDate.format(formatter)
    )
}


data class ExpensesTableInsert(
    var id: Int,
    var title: String,
    var count: Double,
    var day: Int,
    var mount: Int,
    var year: Int,
    var priceAll: Double,
    var suffix: String,
    var category: String,
    var note: String,
    val showFood: Boolean, // Показывать на складе иду
    val showWarehouse: Boolean, // Показывать на складе
    val showAnimals: Boolean, // Связывает животных
    val dailyExpensesFood: Double, // Ежедневный расход еды
    val countAnimal: Int, // Кол-во животных
    val foodDesignedDay: Int, // Кол-во дней
    val lastDayFood: String //Последний день еды
)
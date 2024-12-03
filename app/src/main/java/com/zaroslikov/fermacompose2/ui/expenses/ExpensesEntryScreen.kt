package com.zaroslikov.fermacompose2.ui.expenses

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
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
                            id = it.id,
                            title = it.title,
                            count = it.count,
                            day = it.day,
                            mount = it.mount,
                            year = it.year,
                            priceAll = it.priceAll,
                            suffix = it.suffix,
                            category = it.category,
                            idPT = idProject,
                            note = it.note
                        )

                    )
                    Toast.makeText(
                        context,
                        "Куплено: ${it.title} ${it.count} ${it.suffix} за ${it.priceAll} ₽",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateUp()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesEntryContainerProduct(
    modifier: Modifier,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<AnimalExpensesList>,
    saveInRoomSale: (ExpensesTableInsert) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("") }
    var category by remember { mutableStateOf("Без категории") }
    var suffix by remember { mutableStateOf("Шт.") }
    var priceAll by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var countAnimal2 by remember { mutableIntStateOf(0) }
    var foodDay2 by remember { mutableIntStateOf(0) }

    var countAnimal by remember { mutableStateOf("") }
    var foodDay by remember { mutableStateOf("") }

    var food by remember { mutableStateOf(false) }
    var showWarehouse by remember { mutableStateOf(false) }
    var day by remember { mutableStateOf(1) }

    var expanded by remember { mutableStateOf(false) }
    var expandedSuf by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableStateOf(0) }


    fun validateTitle(text: String) {
        isErrorTitle = text == ""
    }

    fun validateCount(text: String) {
        isErrorCount = text == ""
    }

    fun validatePrice(text: String) {
        isErrorPrice = text == ""
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
    var date2 by remember { mutableStateOf(formattedDate) }

    if (openDialog) {
        DatePickerDialogSample(state, date1) { date ->
            date1 = date
            openDialog = false
        }
    }

    if (openDialog) {
        DatePickerDialogSample(state, date1) { date ->
            date1 = date
            openDialog = false
        }
        foodDay2 = settingFood(date1, date2, count.toDouble(), countAnimal2)
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
//                    date2 = settingDay(date1, count.toDouble(), foodDay2, countAnimal2)
                    //TODO Расчет Корма
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
                        checked = food,
                        onCheckedChange = { food = it }
                    )
                    Text(text = "Корм")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showWarehouse,
                        onCheckedChange = { showWarehouse = it }
                    )
                    Text(text = "Отображать на складе")
                }

            }
        }

        if (food) {
            Text(text = "Выберите животных или укажите ежедневный расход в ручную")

            animalList.forEach {
                var selected by remember { mutableStateOf(false) }

                FilterChip(
                    onClick = {
                        selected = !selected
                        if (selected) {
                            foodDay2 += it.foodDay
                            countAnimal2 += it.countAnimal
                        } else {
                            foodDay2 -= it.foodDay
                            countAnimal2 -= it.countAnimal
                        }
                        foodDay = foodDay2.toString()
                        countAnimal = countAnimal2.toString()
//                        date2 = settingDay(date1, count.toDouble(), foodDay2, countAnimal2)
                    },
                    label = {
                        Text(it.name)
                    },
                    selected = selected,
                    leadingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
            Row {

                OutlinedTextField(
                    value = foodDay,
                    onValueChange = {
                        foodDay = it.replace(Regex("[^\\d.]"), "")
//                        date2 = settingDay(date1, count.toDouble(), foodDay2, countAnimal2)
                    },
                    label = { Text("Расход") },
                    modifier = Modifier
//                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    suffix = { Text(text = "г") },
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
                    value = countAnimal,
                    onValueChange = {
                        countAnimal = it.replace(Regex("[^\\d.]"), "")
//                        date2 = settingDay(date1, count.toDouble(), foodDay2, countAnimal2)
                    },
                    label = { Text("Кол-во голов") },
                    modifier = Modifier
//                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    suffix = { Text(text = "шт.") },
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




        OutlinedTextField(
            value = date1,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата окончания кормов") },
            supportingText = {
                Text("Выберите дату сами или введите вручную")
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (errorBoolean()) {
                        val formattedDateList = date1.split(".")
                        saveInRoomSale(
                            ExpensesTableInsert(
                                id = 0,
                                title = title,
                                count = count.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                    .toDouble(),
                                formattedDateList[0].toInt(),
                                formattedDateList[1].toInt(),
                                formattedDateList[2].toInt(),
                                suffix = suffix,
                                category = category,
                                priceAll = priceAll.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                    .toDouble(),
                                note = note
                            )
                        )
                        val eventParameters: MutableMap<String, Any> = HashMap()
                        eventParameters["Имя"] = title
                        eventParameters["Кол-во"] = "$title $count $suffix $priceAll₽"
                        eventParameters["Категория"] = category
                        eventParameters["Примечание"] = note
                        AppMetrica.reportEvent("Expenses Products", eventParameters);
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
}

fun settingDay(date: String, count: Double, foodDay: Int, countAnimal: Int): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateLocal = LocalDate.parse(date, formatter)
    val days = (50 / ((200 / 1000) * 2)).toLong()
    val newDate = dateLocal.plusDays(days)
    return newDate.format(formatter)
}

fun settingFood(date1: String, date2: String, count: Double, countAnimal: Int): Int {
    val format = SimpleDateFormat("dd.MM.yyyy")
    val dateBefore: Date = format.parse(date1)
    val dateAfter: Date = format.parse(date2)
    val day = (TimeUnit.DAYS.convert(
        dateAfter.time - dateBefore.time,
        TimeUnit.MILLISECONDS
    )).toDouble()
    return ((count * 1000) / (day * countAnimal.toDouble())).toInt()
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
    var note: String
)
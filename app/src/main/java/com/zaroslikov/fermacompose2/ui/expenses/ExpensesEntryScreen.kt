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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorExpenses
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.metricaExpenses
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
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
    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    val animalUiState by viewModel.animalUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.expenses_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        ExpensesEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.list,
            categoryList = categoryUiState.list,
            animalList = animalUiState.animalList,
            idProject = viewModel.itemId,
            saveInRoomSale = {
                coroutineScope.launch {
                    viewModel.saveItem(it.first, it.second)
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
    idProject: Int, // TODO convet to Long
    saveInRoomSale: (Pair<ExpensesTable, MutableMap<Long, Double>>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var title by remember { mutableStateOf("") } // Имя
    var count by rememberSaveable { mutableStateOf("") } // кол-во
    var category by remember { mutableStateOf("Без категории") } // Категория
    var suffix by remember { mutableStateOf("Шт.") } // Единица измерения
    var priceAll by remember { mutableStateOf("") } // Цена за все
    var date by remember { mutableStateOf(dateToday()) }
    var note by remember { mutableStateOf("") } // Примечание
    var showFoodUI by remember { mutableStateOf(false) } // Корм
    var showWarehouseUI by remember { mutableStateOf(false) } // Показать на складе
    var showAnimalsUI by remember { mutableStateOf(false) } // Расход на животных
    var dailyExpensesFoodUI by remember { mutableStateOf("") } //Ежедневный расход
    var countAnimalUI by remember { mutableStateOf("") } // Кол-во животных
    var foodDesignedDayUI by remember { mutableStateOf(Pair<Int, String>(0, "")) } // Кол-во дней
    var dailyExpensesFoodTotal by remember { mutableStateOf(0.0) } // Общий ежедневный расход
    var setDailyExpensesFoodAndCountUI by remember { mutableStateOf(false) } // Уставновить ежедневный расход

    val toastText = stringResource(
        R.string.toast_expenses_s_s,
        "$title $count $suffix",
        priceAll,
        stringResource(R.string.currency_ruble)
    )

    // Для расчета
    var countAnimal by remember { mutableStateOf(0) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorDailyExpensesFood by rememberSaveable { mutableStateOf(false) }
    var isErrorCountAnimalUI by rememberSaveable { mutableStateOf(false) }

    fun validateCount(text: String) {
        if (text == "") {
            isErrorCount = true
            showFoodUI = false
            showAnimalsUI = false
            dailyExpensesFoodTotal = 0.0
            countAnimal = 0
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

    fun validateCountAnimalUI(text: String) {
        isErrorCountAnimalUI = text == ""
    }

    //Дата
    var openDialog by remember { mutableStateOf(false) }
    val state = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    if (openDialog) {
        DatePickerDialogSample(state, date) {
            date = it
            openDialog = false
        }
    }

    // Прочее
    if (count == "") {
        showFoodUI = false
        showWarehouseUI = false
    }

    var selectedFilters2 = remember { mutableStateMapOf<Long, Double>() }


    //Подсказки
    var openAlertFood by remember { mutableStateOf(false) }
    var openAlertWarehouse by remember { mutableStateOf(false) }
    var openAlertAnimal by remember { mutableStateOf(false) }

    if (openAlertFood) {
        AlertDialogInfo(
            onConfirmation = { openAlertFood = false },
            intText = R.string.alert_dialog_info_title_food,
            intTitleText = R.string.alert_dialog_info_title_food
        )
    }

    if (openAlertWarehouse) {
        AlertDialogInfo(
            onConfirmation = { openAlertWarehouse = false },
            intText = R.string.alert_dialog_info_title_show_warehouse,
            intTitleText = R.string.alert_dialog_info_text_show_warehouse
        )
    }

    if (openAlertAnimal) {
        AlertDialogInfo(
            onConfirmation = { openAlertAnimal = false },
            intText = R.string.alert_dialog_info_title_animals_expenses,
            intTitleText = R.string.alert_dialog_info_title_animals_expenses
        )
    }


    // Начало дизайна
    OutlinedTextTitleAdd(
        value = title,
        onValueChange = {
            title = it.trim()
            isErrorTitle = it.isError()
            isErrorSlash = it.isErrorSlash()
//            updateCountWarehouse(title) TODO
        },
        titleList = titleList,
        isErrorTitle = isErrorTitle,
        isErrorSlash = isErrorSlash,
        focusManager = focusManager
    )

    OutlinedTextCount(
        value = count,
        onValueChange = {
            count = it.toConvertDb()
            isErrorCount = it.isError()

            if (showFoodUI && !setDailyExpensesFoodAndCountUI) {
                foodDesignedDayUI = settingDay(
                    date,
                    if (count == "") 0.0 else count.toDouble(),
                    dailyExpensesFoodTotal
                )
            }

            if (showFoodUI && setDailyExpensesFoodAndCountUI) {
                foodDesignedDayUI = settingDay(
                    date,
                    if (count == "") 0.0 else count.toDouble(),
                    if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
                    if (countAnimalUI == "") 0 else countAnimalUI.toInt()
                )
            }
        },
        onClick = { suffix = it },
        isError = isErrorCount,
        suffix = suffix,
//        countWarehouse = countWarehouse, TODO
        focusManager = focusManager
    )

    OutlinedTextPrice(
        value = priceAll,
        onValueChange = {
            priceAll = it.toConvertDb()
            isErrorPrice = it.isError()
        },
        isError = isErrorPrice,
        focusManager = focusManager
    )

    OutlinedTextCategory(
        value = category,
        onValueChange = { category = it.trim() },
        titleList = categoryList,
        focusManager = focusManager
    )

    OutlinedTextDate(
        value = date,
        onValueChange = { openDialog = !openDialog }
    )

    OutlinedTextNote(
        value = note,
        onValueChange = { note = it },
        focusManager = focusManager
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

                IconButton(onClick = { openAlertFood = !openAlertFood }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Показать меню"
                    )
                }

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
                                    date,
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
                IconButton(onClick = { openAlertWarehouse = !openAlertWarehouse }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Показать меню"
                    )
                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

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
                Text(
                    text = "Распределить расходы по животным",
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                IconButton(onClick = { openAlertAnimal = !openAlertAnimal }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Показать меню"
                    )
                }
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
                    text = "Выберите животных для рассчета длительности корма:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .padding(top = 5.dp),
                    textDecoration = TextDecoration.Underline
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
                                        date,
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
                                date,
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
                            validateCountAnimalUI(it)
                            foodDesignedDayUI = settingDay(
                                date,
                                count.toDouble(),
                                if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
                                if (countAnimalUI == "") 0 else countAnimalUI.toInt()
                            )
                        },
                        isError = isErrorCountAnimalUI,
                        supportingText = {
                            if (isErrorCountAnimalUI) {
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
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                },
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
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
                            .padding(top = 5.dp)
                    ) {

                        Text(
                            text = "${animalList.find { it.id.toLong() == animal.key }?.name}: ${
                                formatter(
                                    animal.value * count.toDouble() / 100.0
                                )
                            } $suffix /" +
                                    " ${formatter(animal.value * priceAll.toDouble() / 100.0)} ₽" +
                                    " -  ${formatter(animal.value)}%",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
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
                            valueRange = 0f..totalFood,
                            steps = 99,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp, vertical = 2.5.dp)
                        )
                    }
                }
            }
        }
    }

    ButtonStandart(
        intRes = R.string.button_expenses,
        onClick = {
            if (isErrorExpenses(
                    title = title,
                    count = count,
                    price = priceAll,
                    dailyExpensesFoodUI = dailyExpensesFoodUI,
                    countAnimalUI = countAnimalUI,
                    setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
                    isErrorTitle = { isErrorTitle = it },
                    isErrorCount = { isErrorCount = it },
                    isErrorSlash = { isErrorSlash = it },
                    isErrorPrice = { isErrorPrice = it },
                    isErrorDailyExpensesFood = { isErrorDailyExpensesFood = it },
                    isErrorCountAnimalUI = { isErrorCountAnimalUI = it }
                )
            ) {
                val formattedDateList = date.split(".")
                saveInRoomSale(
                    Pair(
                        ExpensesTable(
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
                            dailyExpensesFoodAndCount = setDailyExpensesFoodAndCountUI,
                            dailyExpensesFood = if (setDailyExpensesFoodAndCountUI) dailyExpensesFoodUI.toDouble() else dailyExpensesFoodTotal,
                            countAnimal = if (setDailyExpensesFoodAndCountUI) countAnimalUI.toInt() else countAnimal,
                            foodDesignedDay = foodDesignedDayUI.first,
                            lastDayFood = foodDesignedDayUI.second,
                            idPT = idProject
                        ),
                        selectedFilters2
                    )
                )
                toastShort(
                    context = context,
                    text = toastText
                )
                metricaExpenses(
                    title,
                    count,
                    suffix,
                    priceAll,
                    category,
                    showFoodUI,
                    showWarehouseUI,
                    showAnimalsUI,
                    note
                )
            }
        }
    )
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

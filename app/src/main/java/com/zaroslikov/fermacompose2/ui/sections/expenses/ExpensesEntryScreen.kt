@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.enableCheckBoxExpenses
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorExpenses
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsClear
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsRight
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.metricaExpenses
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.ErrorSupportTextSlash
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TextFoodExpenses
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_20
import com.zaroslikov.fermacompose2.ui.start.formatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
            intTitleText = R.string.alert_dialog_info_text_food
        )
    }

    if (openAlertWarehouse) {
        AlertDialogInfo(
            onConfirmation = { openAlertWarehouse = false },
            intText = R.string.alert_dialog_info_text_show_warehouse,
            intTitleText = R.string.alert_dialog_info_title_show_warehouse
        )
    }

    if (openAlertAnimal) {
        AlertDialogInfo(
            onConfirmation = { openAlertAnimal = false },
            intText = R.string.alert_dialog_info_text_animals_expenses,
            intTitleText = R.string.alert_dialog_info_title_animals_expenses
        )
    }

    Column(modifier = modifier) {
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
            intResSup = R.string.support_text_count_product_expenses,
//        countWarehouse = countWarehouse, TODO
            focusManager = focusManager
        )
        OutlinedTextPrice(
            value = priceAll,
            onValueChange = {
                priceAll = it
                isErrorPrice = it.isError()
            },
            isError = isErrorPrice,
            intSupportText = R.string.support_text_price_expenses,
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
            onValueChange = { date = it}
        )
        OutlinedTextNote(
            value = note,
            onValueChange = { note = it },
            focusManager = focusManager
        )
        // ВЫКЛЮЧАТЕЛИ
        CardField() {
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.card_extra_set),
                    style = textBold_20
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CheckboxTextIcon(
                        checked = showFoodUI,
                        onCheckedChange = {
                            showFoodUI = it
                            showWarehouseUI = it
                            if (showFoodUI) showAnimalsUI = false
                            selectedFilters2.clear()
                        },
                        enabled = count != "",
                        intTitle = R.string.checkbox_food,
                        onClick = {
                            openAlertFood = !openAlertFood
                        }
                    )
                    if (showFoodUI && (count != "")) {
                        CheckboxTextIcon(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 5.dp),
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
                            },
                            intTitle = R.string.checkbox_food
                        )
                    }
                }

                CheckboxTextIcon(
                    checked = showWarehouseUI,
                    onCheckedChange = { showWarehouseUI = it },
                    enabled = enableCheckBoxExpenses(
                        value = count,
                        boolean = showFoodUI
                    ),
                    intTitle = R.string.checkbox_show_warehouse,
                    onClick = { openAlertWarehouse = !openAlertWarehouse }
                )

                CheckboxTextIcon(
                    checked = showAnimalsUI,
                    onCheckedChange = {
                        showAnimalsUI = it
                        selectedFilters2.clear()
                    },
                    enabled = enableCheckBoxExpenses(
                        value = count,
                        valueTwo = priceAll,
                        boolean = showFoodUI
                    ),
                    intTitle = R.string.checkbox_animals_expenses,
                    onClick = { openAlertAnimal = !openAlertAnimal }
                )

                // КОРМА
                if (showFoodUI && (count != "")) {

                    TextFoodExpenses(
                        title = title,
                        foodDesignedDayUI = foodDesignedDayUI,
                        setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
                        dailyExpensesFoodUI = dailyExpensesFoodUI,
                        dailyExpensesFoodTotal = dailyExpensesFoodTotal,
                        suffix = suffix,
                        countAnimalUI = countAnimalUI,
                        countAnimal = countAnimal
                    )

                    if (!setDailyExpensesFoodAndCountUI) {
                        Text(
                            text = stringResource(R.string.support_text_choice_animal),
                            textDecoration = TextDecoration.Underline
                        )
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
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
                            } else Text(text = stringResource(R.string.support_text_no_animal))
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
                                    ErrorSupportTextSlash(
                                        isError = isErrorDailyExpensesFood,
                                        intRes = R.string.support_text_daily_expenses,
                                        intResError = R.string.error_no_count_animals //TODO text refresh
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(4.dp),
                                suffix = { Text(text = suffix) },
                                keyboardOptions = keyboardOptionsNextNumber(),
                                keyboardActions = keyboardActionsRight(focusManager)
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
                                    ErrorSupportTextSlash(
                                        isError = isErrorCountAnimalUI,
                                        intRes = R.string.support_text_count_animals,
                                        intResError = R.string.error_no_count_animals
                                    )
                                },
                                modifier = Modifier,
                                suffix = { Text(text = stringResource(R.string.suffix_pieces)) },
                                keyboardOptions = keyboardOptionsNextNumber(),
                                keyboardActions = keyboardActionsClear(focusManager)
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
                                            if (selected) selectedFilters2.set(
                                                animal.id.toLong(),
                                                0.0
                                            )
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
                            } else Text(text = stringResource(R.string.support_text_no_animal))
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
                                idPT = idProject.toLong()
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

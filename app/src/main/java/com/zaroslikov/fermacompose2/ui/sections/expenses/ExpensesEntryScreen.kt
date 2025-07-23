@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.zaroslikov.fermacompose2.ui.sections.expenses

import android.widget.Switch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorExpenses
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TextFoodExpenses
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_20
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object ExpensesEntryDestination : NavigationDestination {
    override val route = "ExpensesEntry"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}


@Composable
fun ExpensesEntryProduct(
    navigateBack: () -> Unit,
    viewModel: ExpensesEntryViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.eventFlow
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.expenses_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        ExpensesEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding),
            domainExpensesTable = viewModel.expensesUiState,
            isEntry = viewModel.isEntry,
            isIndicationValue = viewModel.isIndicatorsValue,
            isAutoCalculate = viewModel.isAutoCalculate,
            titleList = viewModel.titleUiState.collectAsState().value.list,
            categoryList = viewModel.categoryUiState.collectAsState().value.list,
            animalList = viewModel.animalUiState.collectAsState().value.animalList,
            countWarehouse = viewModel.itemUiState,
            updateCountWarehouse = viewModel::updateWarehouseUiState,
            onValueChange = viewModel::updateUiState,
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpensesEntryContainerProduct(
    modifier: Modifier,
    domainExpensesTable: DomainExpensesTable,
    isEntry: Boolean,
    isIndicationValue: Boolean,
    isAutoCalculate: MutableState<Boolean>,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<AnimalExpensesList>,
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (String) -> Unit,
    onValueChange: (DomainExpensesTable) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var date by rememberSaveable {
        mutableStateOf(
            formatDateToString(
                domainExpensesTable.day,
                domainExpensesTable.mount,
                domainExpensesTable.year
            )
        )
    }

//    var title by remember { mutableStateOf("") } // Имя
//    var count by rememberSaveable { mutableStateOf("") } // кол-во
//    var category by remember { mutableStateOf("Без категории") } // Категория
//    var suffix by remember { mutableStateOf("Шт.") } // Единица измерения
//    var priceAll by remember { mutableStateOf("") } // Цена за все
//    var date by remember { mutableStateOf(dateToday()) }
//    var note by remember { mutableStateOf("") } // Примечание
//    var showFoodUI by remember { mutableStateOf(false) } // Корм
//    var showWarehouseUI by remember { mutableStateOf(false) } // Показать на складе
//    var showAnimalsUI by remember { mutableStateOf(false) } // Расход на животных
//    var dailyExpensesFoodUI by remember { mutableStateOf("") } //Ежедневный расход
//    var countAnimalUI by remember { mutableStateOf("") } // Кол-во животных
//    var foodDesignedDayUI by remember { mutableStateOf(Pair<Int, String>(0, "")) } // Кол-во дней
//    var dailyExpensesFoodTotal by remember { mutableStateOf(0.0) } // Общий ежедневный расход
//    var setDailyExpensesFoodAndCountUI by remember { mutableStateOf(false) } // Уставновить ежедневный расход

//    val toastText = stringResource(
//        R.string.toast_expenses_s_s,
//        "$title $count $suffix",
//        priceAll,
//        stringResource(R.string.currency_ruble)
//    )

    // Для расчета
    var countAnimal by remember { mutableStateOf(0) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorDailyExpensesFood by rememberSaveable { mutableStateOf(false) }
    var isErrorCountAnimalUI by rememberSaveable { mutableStateOf(false) }

//    fun validateCount(text: String) {
//        if (text == "") {
//            isErrorCount = true
//            showFoodUI = false
//            showAnimalsUI = false
//            dailyExpensesFoodTotal = 0.0
//            countAnimal = 0
//        }
//    }

//    fun validatePrice(text: String) {
//        if (text == "") {
//            isErrorPrice = true
//            showAnimalsUI = false
//        }
//    }

    fun validateDailyExpensesFood(text: String) {
        isErrorDailyExpensesFood = text == ""
    }

    fun validateCountAnimalUI(text: String) {
        isErrorCountAnimalUI = text == ""
    }

    // Прочее
//    if (count == "") {
//        showFoodUI = false
//        showWarehouseUI = false
//    }


    Column(modifier = modifier) {
        OutlinedTextTitleAdd(
            value = domainExpensesTable.title,
            onValueChange = {
                onValueChange(domainExpensesTable.copy(title = it))
                isErrorTitle = it.isError()
                isErrorSlash = it.isErrorSlash()
                updateCountWarehouse(it)
            },
            titleList = titleList,
            isErrorTitle = isErrorTitle,
            isErrorSlash = isErrorSlash,
            focusManager = focusManager
        )
        OutlinedTextCount(
            value = domainExpensesTable.count,
            onValueChange = {
                onValueChange(domainExpensesTable.copy(count = it))
                isErrorCount = it.isError()

//                if (showFoodUI && !setDailyExpensesFoodAndCountUI) {
//                    foodDesignedDayUI = settingDay(
//                        date,
//                        if (count == "") 0.0 else count.toDouble(),
//                        dailyExpensesFoodTotal
//                    )
//                }
//
//                if (showFoodUI && setDailyExpensesFoodAndCountUI) {
//                    foodDesignedDayUI = settingDay(
//                        date,
//                        if (count == "") 0.0 else count.toDouble(),
//                        if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
//                        if (countAnimalUI == "") 0 else countAnimalUI.toInt()
//                    )
//                }
            },
            onClick = { onValueChange(domainExpensesTable.copy(suffix = it)) },
            isError = isErrorCount,
            suffix = domainExpensesTable.suffix,
            intResSup = R.string.support_text_count_product_expenses,
            countWarehouse = countWarehouse.first,
            focusManager = focusManager
        )
        OutlinedTextPrice(
            value = domainExpensesTable.priceAll,
            onValueChange = {
                onValueChange(domainExpensesTable.copy(priceAll = it))
                isErrorPrice = it.isError()
            },
            isError = isErrorPrice,
            intSupportText = R.string.support_text_price_expenses,
            focusManager = focusManager
        )
        OutlinedTextCategory(
            value = domainExpensesTable.category,
            onValueChange = { onValueChange(domainExpensesTable.copy(category = it)) },
            titleList = categoryList,
            focusManager = focusManager
        )
        OutlinedTextDateEdit(
            value = date,
            onValueChange = {
                date = it
                val dateList = it.split(".")
                onValueChange(
                    domainExpensesTable.copy(
                        day = dateList[0].toInt(),
                        mount = dateList[1].toInt(),
                        year = dateList[2].toInt()
                    )
                )
            }
        )
        OutlinedTextNote(
            value = domainExpensesTable.note,
            onValueChange = { onValueChange(domainExpensesTable.copy(note = it)) },
            focusManager = focusManager
        )
        val animalList = mutableListOf<AnimalExpensesList2>()
        Switch(
            domainExpensesTable,
            onValueChange,
            animalList
        )
        ButtonPanel(
            title = domainExpensesTable.title,
            count = domainExpensesTable.count,
            priceAll = domainExpensesTable.priceAll,
            isEntry = isEntry,
            dailyExpensesFoodUI = domainExpensesTable.dailyExpensesFood,
            countAnimalUI = domainExpensesTable.countAnimal,
            setDailyExpensesFoodAndCountUI = false, //todo
            focusManager = focusManager,
            isErrorTitle = { isErrorTitle = it },
            isErrorCount = { isErrorCount = it },
            isErrorSlash = { isErrorSlash = it },
            isErrorPrice = { isErrorPrice = it },
            isErrorDailyExpensesFood = { isErrorDailyExpensesFood = it },
            isErrorCountAnimalUI = { isErrorCountAnimalUI = it },
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}


@Composable
private fun ButtonPanel(
    title: String,
    count: String,
    priceAll: String,
    isEntry: Boolean,
    dailyExpensesFoodUI: String,
    countAnimalUI: String,
    setDailyExpensesFoodAndCountUI: Boolean,
    focusManager: FocusManager,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    isErrorPrice: (Boolean) -> Unit,
    isErrorDailyExpensesFood: (Boolean) -> Unit,
    isErrorCountAnimalUI: (Boolean) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    if (isEntry)
        ButtonStandart(
            intRes = R.string.button_expenses,
            onClick = {
                onClickButton(
                    title = title,
                    count = count,
                    priceAll = priceAll,
                    focusManager = focusManager,
                    isErrorTitle = isErrorTitle,
                    isErrorCount = isErrorCount,
                    isErrorSlash = isErrorSlash,
                    isErrorPrice = isErrorPrice,
                    onClick = onClickInsert,
                    dailyExpensesFoodUI = dailyExpensesFoodUI,
                    countAnimalUI = countAnimalUI,
                    setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
                    isErrorDailyExpensesFood = isErrorDailyExpensesFood,
                    isErrorCountAnimalUI = isErrorCountAnimalUI
                )
            }
        )
    else {
        ButtonRefresh {
            onClickButton(
                title = title,
                count = count,
                priceAll = priceAll,
                focusManager = focusManager,
                isErrorTitle = isErrorTitle,
                isErrorCount = isErrorCount,
                isErrorSlash = isErrorSlash,
                isErrorPrice = isErrorPrice,
                onClick = onClickUpdate,
                dailyExpensesFoodUI = dailyExpensesFoodUI,
                countAnimalUI = countAnimalUI,
                setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
                isErrorDailyExpensesFood = isErrorDailyExpensesFood,
                isErrorCountAnimalUI = isErrorCountAnimalUI
            )
        }
        ButtonDelete { onClickDelete() }
    }
}

private fun onClickButton(
    title: String,
    count: String,
    priceAll: String,
    dailyExpensesFoodUI: String,
    countAnimalUI: String,
    setDailyExpensesFoodAndCountUI: Boolean,
    focusManager: FocusManager,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    isErrorPrice: (Boolean) -> Unit,
    isErrorDailyExpensesFood: (Boolean) -> Unit,
    isErrorCountAnimalUI: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    if (isErrorExpenses(
            title = title, count = count,
            price = priceAll,
            dailyExpensesFoodUI = dailyExpensesFoodUI,
            countAnimalUI = countAnimalUI,
            setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
            isErrorTitle = { isErrorTitle(it) },
            isErrorCount = { isErrorCount(it) },
            isErrorSlash = { isErrorSlash(it) },
            isErrorPrice = { isErrorPrice(it) },
            isErrorDailyExpensesFood = { isErrorDailyExpensesFood(it) },
            isErrorCountAnimalUI = { isErrorCountAnimalUI(it) })
    ) {
        focusManager.clearFocus()
        onClick()
    }
}

//ButtonStandart(
//intRes = R.string.button_expenses,
//onClick = {
//    if (isErrorExpenses(
//            title = title,
//            count = count,
//            price = priceAll,
//            dailyExpensesFoodUI = dailyExpensesFoodUI,
//            countAnimalUI = countAnimalUI,
//            setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
//            isErrorTitle = { isErrorTitle = it },
//            isErrorCount = { isErrorCount = it },
//            isErrorSlash = { isErrorSlash = it },
//            isErrorPrice = { isErrorPrice = it },
//            isErrorDailyExpensesFood = { isErrorDailyExpensesFood = it },
//            isErrorCountAnimalUI = { isErrorCountAnimalUI = it }
//        )
//    ) {
//        val formattedDateList = date.split(".")
//        saveInRoomSale(
//            Pair(
//                ExpensesTable(
//                    id = 0,
//                    title = title,
//                    count = count.replace(Regex("[^\\d.]"), "").replace(",", ".")
//                        .toDouble(),
//                    day = formattedDateList[0].toInt(),
//                    mount = formattedDateList[1].toInt(),
//                    year = formattedDateList[2].toInt(),
//                    suffix = suffix,
//                    category = category,
//                    priceAll = priceAll.replace(Regex("[^\\d.]"), "").replace(",", ".")
//                        .toDouble(),
//                    note = note,
//                    showFood = showFoodUI,
//                    showWarehouse = showWarehouseUI,
//                    showAnimals = showAnimalsUI,
//                    dailyExpensesFoodAndCount = setDailyExpensesFoodAndCountUI,
//                    dailyExpensesFood = if (setDailyExpensesFoodAndCountUI) dailyExpensesFoodUI.toDouble() else dailyExpensesFoodTotal,
//                    countAnimal = if (setDailyExpensesFoodAndCountUI) countAnimalUI.toInt() else countAnimal,
//                    foodDesignedDay = foodDesignedDayUI.first,
//                    lastDayFood = foodDesignedDayUI.second,
//                    idPT = idProject.toLong()
//                ),
//                selectedFilters2
//            )
//        )
//        toastShort(
//            context = context,
//            text = toastText
//        )
//        metricaExpenses(
//            title,
//            count,
//            suffix,
//            priceAll,
//            category,
//            showFoodUI,
//            showWarehouseUI,
//            showAnimalsUI,
//            note
//        )
//    }
//}
//)


@Composable
fun Switch(
    expensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit,
    animalList: MutableList<AnimalExpensesList2>,
) {
    val focusManager = LocalFocusManager.current
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorDailyExpensesFood by rememberSaveable { mutableStateOf(false) }
    var isErrorСountAnimalUI by rememberSaveable { mutableStateOf(false) }

    var countAnimal by remember { mutableIntStateOf(0) } //кол-во животных
    var foodDesignedDayUI by remember { mutableStateOf(Pair<Int, String>(0, "")) }
    var dailyExpensesFoodTotal by remember { mutableDoubleStateOf(0.0) } // Общий ежедневный расход

    var formattedDate = String.format(
        "%02d.%02d.%d",
        expensesTable.day,
        expensesTable.mount,
        expensesTable.year
    )

    fun validateDailyExpensesFood(text: String) {
        isErrorDailyExpensesFood = text == ""
    }

    fun validateСountAnimalUI(text: String) {
        isErrorСountAnimalUI = text == ""
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
                                onValueChange(
                                    expensesTable.copy(
                                        dailyExpensesFoodAndCount = it,
                                        dailyExpensesFood = "0",
                                        countAnimal = "0"
                                    )
                                )
                                animalListClean(animalList)
//
//                                    expensesTable.dailyExpensesFood = ""
//                                    expensesTable.countAnimal = ""
//                                    foodDesignedDayUI = settingDay(
//                                        formattedDate,
//                                        expensesTable.count.toDouble(),
//                                        if (expensesTable.dailyExpensesFood == "") 0.0 else expensesTable.dailyExpensesFood.toDouble(),
//                                        if (expensesTable.countAnimal == "") 0 else expensesTable.countAnimal.toInt()
//                                    )

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


            // Первый расчет при загрузки
            countAnimal =
                if (expensesTable.countAnimal == "") 0 else expensesTable.countAnimal.toInt()
            dailyExpensesFoodTotal =
                if (expensesTable.dailyExpensesFood == "") 0.0 else expensesTable.dailyExpensesFood.toDouble()

            foodDesignedDayUI = if (expensesTable.dailyExpensesFoodAndCount) {
                settingDay(
                    formattedDate,
                    expensesTable.count.toDouble(),
                    if (expensesTable.dailyExpensesFood == "") 0.0 else expensesTable.dailyExpensesFood.toDouble(),
                    if (expensesTable.countAnimal == "") 0 else expensesTable.countAnimal.toInt()
                )
            } else {
                settingDay(
                    formattedDate,
                    expensesTable.count.toDouble(),
                    dailyExpensesFoodTotal
                )
            }

            Text(
                text = "${if (expensesTable.title == "") "Корма" else expensesTable.title} хватит на ${if (foodDesignedDayUI.first >= 1000) "более" else ""} ${foodDesignedDayUI.first} суток до ${foodDesignedDayUI.second}\n" +
                        "Ежедневный расход составляет - ${expensesTable.dailyExpensesFood} ${expensesTable.suffix}\n" +
                        "Кол-во голов -  ${expensesTable.countAnimal} шт. ",
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
                                        it.presentException =
                                            (it.foodDay / dailyExpensesFoodTotal) * 100.0
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
                                    onValueChange(
                                        expensesTable.copy(
                                            countAnimal = countAnimal.toString(),
                                            dailyExpensesFood = dailyExpensesFoodTotal.toString()
                                        )
                                    )
                                },
                                label = { Text(it.name) },
                                selected = if (expensesTable.dailyExpensesFoodAndCount) {
                                    selected = false
                                    false
                                } else selected,
                                leadingIcon = if (selected) {
                                    {
//                                            Icon(
//                                                imageVector = Icons.Filled.Done,
//                                                contentDescription = "Done icon",
//                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
//                                            )
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
                        value = expensesTable.dailyExpensesFood,
                        onValueChange = {
                            onValueChange(
                                expensesTable.copy(
                                    dailyExpensesFood = it.replace(
                                        Regex("[^\\d.]"),
                                        ""
                                    ).replace(",", ".")
                                )
                            )
                            validateDailyExpensesFood(it)
                            foodDesignedDayUI = settingDay(
                                formattedDate,
                                expensesTable.count.toDouble(),
                                if (expensesTable.dailyExpensesFood == "") 0.0 else expensesTable.dailyExpensesFood.toDouble(),
                                if (expensesTable.countAnimal == "") 0 else expensesTable.countAnimal.toInt()
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
                        value = expensesTable.countAnimal,
                        onValueChange = {
                            onValueChange(
                                expensesTable.copy(
                                    countAnimal = it.replace(
                                        Regex("[^\\d.]"),
                                        "".replace(".", "").replace(",", "")
                                    )
                                )
                            )
                            validateСountAnimalUI(it)
                            foodDesignedDayUI = settingDay(
                                formattedDate,
                                expensesTable.count.toDouble(),
                                if (expensesTable.dailyExpensesFood == "") 0.0 else expensesTable.dailyExpensesFood.toDouble(),
                                if (expensesTable.countAnimal == "") 0 else expensesTable.countAnimal.toInt()
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
                                    if (selected) animal.ps = true
                                    else animal.ps = false

                                    var i = 0
                                    animalList.forEach { if (it.ps) i++ }

                                    animalList.forEach {
                                        it.presentException =
                                            (totalFood / i).toDouble()
                                    }
                                },
                                label = { Text(animal.name) },
                                leadingIcon = if (selected) {
                                    {
//                                        Icon(
//                                            imageVector = Icons.Filled.Done,
//                                            contentDescription = "Done icon",
//                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
//                                        )
                                    }
                                } else null,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                            )
                        }
                    } else Text(text = "Нет животных")
                }

                // Отображаем слайдеры для каждого выбранного животного
                animalList.forEachIndexed { index, animal ->

                    if (animal.ps) {
                        var presentException2 by remember { mutableDoubleStateOf(0.0) }
                        presentException2 = animal.presentException
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(top = 5.dp)
                        ) {
                            Text(
                                text = animal.name +
                                        " ${
                                            formatter(animal.presentException * expensesTable.count.toDouble() / 100.0)
                                        } " +
                                        "${expensesTable.suffix} /" +
                                        " ${formatter(animal.presentException * expensesTable.priceAll.toDouble() / 100.0)} ₽" +
                                        " -  ${formatter(animal.presentException)}%",
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                            )

                            Slider(
                                value = presentException2.toFloat(),
                                onValueChange = {
                                    presentException2 = it.toDouble()
                                    animal.presentException = presentException2
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
}

//@Composable
//fun Switch(
//    domainExpensesTable: DomainExpensesTable,
//    onValueChange: (DomainExpensesTable) -> Unit,
//) {
//
//    var selectedFilters2 = remember { mutableStateMapOf<Long, Double>() }
//
//    var setDailyExpensesFoodAndCountUI by remember { mutableStateOf(false) } // Уставновить ежедневный расход
//
//    //Подсказки
//    var openAlertFood by remember { mutableStateOf(false) }
//    var openAlertWarehouse by remember { mutableStateOf(false) }
//    var openAlertAnimal by remember { mutableStateOf(false) }
//
//    if (openAlertFood) {
//        AlertDialogInfo(
//            onConfirmation = { openAlertFood = false },
//            intText = R.string.alert_dialog_info_title_food,
//            intTitleText = R.string.alert_dialog_info_text_food
//        )
//    }
//
//    if (openAlertWarehouse) {
//        AlertDialogInfo(
//            onConfirmation = { openAlertWarehouse = false },
//            intText = R.string.alert_dialog_info_text_show_warehouse,
//            intTitleText = R.string.alert_dialog_info_title_show_warehouse
//        )
//    }
//
//    if (openAlertAnimal) {
//        AlertDialogInfo(
//            onConfirmation = { openAlertAnimal = false },
//            intText = R.string.alert_dialog_info_text_animals_expenses,
//            intTitleText = R.string.alert_dialog_info_title_animals_expenses
//        )
//    }
//
//    // ВЫКЛЮЧАТЕЛИ
//    CardField() {
//        Column(
//            modifier = Modifier
//                .selectableGroup()
//                .fillMaxWidth()
//                .padding(10.dp),
//        ) {
//            Text(
//                text = stringResource(R.string.card_extra_set),
//                style = textBold_20
//            )
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                CheckboxTextIcon(
//                    checked = domainExpensesTable.showFood,
//                    onCheckedChange = {
//                        onValueChange(
//                            domainExpensesTable.copy(
//                                showFood = it,
//                                showWarehouse = it,
//                                showAnimals = if (it) false else domainExpensesTable.showAnimals
//                            )
//                        )
//                        selectedFilters2.clear()
//                    },
//                    enabled = domainExpensesTable.count != "",
//                    intTitle = R.string.checkbox_food,
//                    onClick = {
//                        openAlertFood = !openAlertFood
//                    }
//                )
//                if (domainExpensesTable.showFood && (domainExpensesTable.count != "")) {
//                    CheckboxTextIcon(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 14.dp)
//                            .padding(top = 5.dp),
//                        checked = true //todo
////                            setDailyExpensesFoodAndCountUI
//                        ,
//                        onCheckedChange = {
//                            setDailyExpensesFoodAndCountUI = !setDailyExpensesFoodAndCountUI
////                            dailyExpensesFoodTotal = 0.0
////                            countAnimal = 0
////                            foodDesignedDayUI = settingDay(
////                                date,
////                                count.toDouble(),
////                                if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
////                                if (countAnimalUI == "") 0 else countAnimalUI.toInt()
////                            )
//                            selectedFilters2.clear()
//                        },
//                        intTitle = R.string.checkbox_food
//                    )
//                }
//            }
//
//            CheckboxTextIcon(
//                checked = domainExpensesTable.showWarehouse,
//                onCheckedChange = { onValueChange(domainExpensesTable.copy(showWarehouse = it)) },
////                enabled = enableCheckBoxExpenses(
////                    value = count,
////                    boolean = showFoodUI
////                ),
//                intTitle = R.string.checkbox_show_warehouse,
//                onClick = { openAlertWarehouse = !openAlertWarehouse }
//            )
//
//            CheckboxTextIcon(
//                checked = domainExpensesTable.showAnimals,
//                onCheckedChange = {
//                    onValueChange(domainExpensesTable.copy(showAnimals = it))
//                    selectedFilters2.clear()
//                },
////                enabled = enableCheckBoxExpenses(
////                    value = count,
////                    valueTwo = priceAll,
////                    boolean = showFoodUI
////                ),
//                intTitle = R.string.checkbox_animals_expenses,
//                onClick = { openAlertAnimal = !openAlertAnimal }
//            )
//
//            // КОРМА
//            if (domainExpensesTable.showFood && (domainExpensesTable.count != "")) {
////
////                TextFoodExpenses(
////                    title = domainExpensesTable.title,
////                    foodDesignedDayUI = foodDesignedDayUI,
////                    setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
////                    dailyExpensesFoodUI = dailyExpensesFoodUI,
////                    dailyExpensesFoodTotal = dailyExpensesFoodTotal,
////                    suffix = domainExpensesTable.suffix,
////                    countAnimalUI = countAnimalUI,
////                    countAnimal = countAnimal
////                )
//                TextFoodExpenses(
//                    title = domainExpensesTable.title,
//                    foodDesignedDayUI = Pair(0, ""), //todo
//                    setDailyExpensesFoodAndCountUI = setDailyExpensesFoodAndCountUI,
//                    dailyExpensesFoodUI = domainExpensesTable.dailyExpensesFood,
//                    dailyExpensesFoodTotal = 0.0, //todo
//                    suffix = domainExpensesTable.suffix,
//                    countAnimalUI = domainExpensesTable.countAnimal,
//                    countAnimal = 0 //todo
//                )
//
//                if (!setDailyExpensesFoodAndCountUI) {
//                    Text(
//                        text = stringResource(R.string.support_text_choice_animal),
//                        textDecoration = TextDecoration.Underline
//                    )
//                    FlowRow(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ) {
//                        if (animalList.isNotEmpty()) {
//                            animalList.forEach {
//                                var selected by remember { mutableStateOf(false) }
//
//                                FilterChip(
//                                    onClick = {
//                                        selected = !selected
//                                        if (selected) {
//                                            countAnimal += it.countAnimal
//                                            dailyExpensesFoodTotal += (it.foodDay * it.countAnimal)
//                                            selectedFilters2.set(
//                                                it.id.toLong(),
//                                                ((it.foodDay / dailyExpensesFoodTotal) * 100.0)
//                                            )
//                                        } else {
//                                            countAnimal -= it.countAnimal
//                                            dailyExpensesFoodTotal -= (it.foodDay * it.countAnimal)
//                                            selectedFilters2.remove(it.id.toLong())
//                                        }
//                                        foodDesignedDayUI = settingDay(
//                                            date,
//                                            count.toDouble(),
//                                            dailyExpensesFoodTotal
//                                        )
//                                    },
//                                    label = { Text(it.name) },
//                                    selected = if (setDailyExpensesFoodAndCountUI) {
//                                        selected = false
//                                        false
//                                    } else selected,
//                                    leadingIcon = if (selected) {
//                                        {
//                                            Icon(
//                                                imageVector = Icons.Filled.Done,
//                                                contentDescription = "Done icon",
//                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
//                                            )
//                                        }
//                                    } else null,
//                                    modifier = Modifier.padding(4.dp)
//                                )
//                            }
//                        } else Text(text = stringResource(R.string.support_text_no_animal))
//                    }
//                }
////
//                if (setDailyExpensesFoodAndCountUI) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 14.dp, vertical = 5.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        OutlinedTextField(
//                            value = dailyExpensesFoodUI,
//                            onValueChange = {
//                                dailyExpensesFoodUI = it.replace(Regex("[^\\d.]"), "")
//                                validateDailyExpensesFood(it)
//                                foodDesignedDayUI = settingDay(
//                                    date,
//                                    count.toDouble(),
//                                    if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
//                                    if (countAnimalUI == "") 0 else countAnimalUI.toInt()
//                                )
//                            },
//                            isError = isErrorDailyExpensesFood,
//                            supportingText = {
//                                ErrorSupportTextSlash(
//                                    isError = isErrorDailyExpensesFood,
//                                    intRes = R.string.support_text_daily_expenses,
//                                    intResError = R.string.error_no_count_animals //TODO text refresh
//                                )
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth(0.5f)
//                                .padding(4.dp),
//                            suffix = { Text(text = suffix) },
//                            keyboardOptions = keyboardOptionsNextNumber(),
//                            keyboardActions = keyboardActionsRight(focusManager)
//                        )
//
//                        OutlinedTextField(
//                            value = countAnimalUI,
//                            onValueChange = {
//                                countAnimalUI = it.replace(Regex("[^\\d.]"), "")
//                                validateCountAnimalUI(it)
//                                foodDesignedDayUI = settingDay(
//                                    date,
//                                    count.toDouble(),
//                                    if (dailyExpensesFoodUI == "") 0.0 else dailyExpensesFoodUI.toDouble(),
//                                    if (countAnimalUI == "") 0 else countAnimalUI.toInt()
//                                )
//                            },
//                            isError = isErrorCountAnimalUI,
//                            supportingText = {
//                                ErrorSupportTextSlash(
//                                    isError = isErrorCountAnimalUI,
//                                    intRes = R.string.support_text_count_animals,
//                                    intResError = R.string.error_no_count_animals
//                                )
//                            },
//                            modifier = Modifier,
//                            suffix = { Text(text = stringResource(R.string.suffix_pieces)) },
//                            keyboardOptions = keyboardOptionsNextNumber(),
//                            keyboardActions = keyboardActionsClear(focusManager)
//                        )
//                    }
//                }
//            }
//
//            //РАСПРЕДЕЛЕНИЕ РАСХОДОВ
//            if (showAnimalsUI) {
//                val totalFood by remember { mutableFloatStateOf(100f) }
//
//                Column {
//                    FlowRow(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 14.dp)
//                    ) {
//                        if (animalList.isNotEmpty()) {
//                            animalList.forEach { animal ->
//                                var selected by remember { mutableStateOf(false) }
//                                FilterChip(
//                                    selected = selected,
//                                    onClick = {
//                                        selected = !selected
//                                        if (selected) selectedFilters2.set(
//                                            animal.id.toLong(),
//                                            0.0
//                                        )
//                                        else selectedFilters2.remove(animal.id.toLong())
//                                        selectedFilters2.forEach {
//                                            selectedFilters2[it.key] =
//                                                (totalFood / selectedFilters2.size).toDouble()
//                                        }
//                                    },
//                                    label = { Text(animal.name) },
//                                    leadingIcon = {
//                                        Icon(
//                                            imageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add,
//                                            contentDescription = "Done icon",
//                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
//                                        )
//                                    },
//                                    modifier = Modifier
//                                        .padding(horizontal = 10.dp)
//                                )
//                            }
//                        } else Text(text = stringResource(R.string.support_text_no_animal))
//                    }
//
//                    // Отображаем слайдеры для каждого выбранного животного
//                    selectedFilters2.forEach { animal ->
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 14.dp)
//                                .padding(top = 5.dp)
//                        ) {
//
//                            Text(
//                                text = "${animalList.find { it.id.toLong() == animal.key }?.name}: ${
//                                    formatter(
//                                        animal.value * count.toDouble() / 100.0
//                                    )
//                                } $suffix /" +
//                                        " ${formatter(animal.value * priceAll.toDouble() / 100.0)} ₽" +
//                                        " -  ${formatter(animal.value)}%",
//                                fontSize = 18.sp,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 14.dp)
//                            )
//
//                            Slider(
//                                value = animal.value.toFloat(),
//                                onValueChange = {
//                                    selectedFilters2[animal.key] = it.toDouble()
//                                    // Пересчитываем значения для остальных животных
//                                    val remainingFood = totalFood - it
//                                    val otherAnimalsCount = selectedFilters2.size - 1
//
//                                    selectedFilters2.forEach { otherAnimal ->
//                                        if (otherAnimal.key != animal.key) {
//                                            selectedFilters2[otherAnimal.key] =
//                                                (remainingFood / otherAnimalsCount).toDouble()
//                                        }
//                                    }
//                                },
//                                valueRange = 0f..totalFood,
//                                steps = 99,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 5.dp, vertical = 2.5.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


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

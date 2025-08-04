@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.zaroslikov.fermacompose2.ui.sections.expenses

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsClear
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCountNoCard
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse
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
            animalList2 = viewModel.animalList2,
            countWarehouse = viewModel.itemUiState,
            updateCountWarehouse = viewModel::updateWarehouseUiState,
            onValueChange = viewModel::updateUiState,
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
        )
    }
}

@Composable
fun ExpensesEntryContainerProduct(
    modifier: Modifier,
    domainExpensesTable: DomainExpensesTable,
    isEntry: Boolean,
    isIndicationValue: Boolean,
    isAutoCalculate: MutableState<Boolean>,
    titleList: List<String>,
    categoryList: List<String>,
    animalList2: MutableList<AnimalExpensesList3>,
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

    Column(modifier = modifier) {
        OutlinedTextTitleAdd(
            value = domainExpensesTable.title,
            onValueChange = {
                onValueChange(
                    domainExpensesTable.copy(title = it).validateTitle()
                )
                updateCountWarehouse(it)
            },
            titleList = titleList,
            isErrorTitle = domainExpensesTable.error.isErrorTitle,
            isErrorSlash = domainExpensesTable.error.isErrorSlash,
            focusManager = focusManager
        )
        OutlinedTextCount(
            value = domainExpensesTable.count,
            onValueChange = {
                onValueChange(
                    domainExpensesTable.copy(count = it).validateCount()
                )
            },
            onSuffixChange = { onValueChange(domainExpensesTable.copy(suffix = it)) },
            isError = domainExpensesTable.error.isErrorCount,
            suffix = domainExpensesTable.suffix,
            intResSup = R.string.support_text_count_product_expenses,
            countWarehouse = countWarehouse.first,
            onWeightChange = {onValueChange(
                domainExpensesTable.copy(weight = it)
            )},
            isWarehouseShow = false,
            weightValue = domainExpensesTable.weight,
            weightSuffix = domainExpensesTable.weightSuffix,
            isAutoCalculate = domainExpensesTable.isAutoWeight,
            onAutoCalculate = {onValueChange(domainExpensesTable.copy(isAutoWeight = it))},
            onWeightSuffixChance = {onValueChange(domainExpensesTable.copy(weightSuffix = it))},
            focusManager = focusManager
        )
        OutlinedPriceInput(
            price = domainExpensesTable.priceAll,
            onPriceChange = {
                onValueChange(
                    domainExpensesTable.copy(priceAll = it).validatePrice()
                )
            },
            count = domainExpensesTable.count,
            isError = domainExpensesTable.error.isErrorPrice,
            isAutoCalculate = isAutoCalculate.value,
            onAutoCalculate = { isAutoCalculate.value = it },
            isManyCount = true,
            supportTextRes = R.string.support_text_price_expenses,
            supportTextResAutoCal = R.string.support_text_price_expenses,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
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
        AdditionalSettings(
            domainExpensesTable = domainExpensesTable,
            onValueChange = onValueChange,
            animalList2 = animalList2
        )
        ButtonPanel(
            isEntry = isEntry,
            focusManager = focusManager,
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}


@Composable
private fun ButtonPanel(
    isEntry: Boolean,
    focusManager: FocusManager,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    if (isEntry)
        ButtonStandart(
            intRes = R.string.button_expenses,
            onClick = {
                focusManager.clearFocus()
                onClickInsert()
            }
        )
    else {
        ButtonRefresh {
            focusManager.clearFocus()
            onClickUpdate()
        }
        ButtonDelete { onClickDelete() }
    }
}


@Composable
private fun AdditionalSettings(
    domainExpensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit,
    animalList2: MutableList<AnimalExpensesList3>
) {
    var details by rememberSaveable { mutableStateOf(true) }
    val extraPadding by animateDpAsState(
        if (details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )
    if (!domainExpensesTable.count.isBlank() && !domainExpensesTable.priceAll.isBlank()) {
        TextButtonWarehouse(
            boolean = details,
            onClick = { details = !details },
            intRes = R.string.card_extra_set
        )
        if (details) {
            Food(
                modifier = Modifier.padding(extraPadding),
                expensesTable = domainExpensesTable,
                onValueChange = onValueChange,
                animalList = animalList2
            )
            ShowWarehouse(
                modifier = Modifier.padding(extraPadding),
                domainExpensesTable = domainExpensesTable,
                onValueChange = onValueChange
            )
            ShowAnimal(
                modifier = Modifier.padding(extraPadding),
                domainExpensesTable = domainExpensesTable,
                onValueChange = onValueChange,
                animalList = animalList2
            )
        }
    }
}


// КОРМ
@Composable
private fun Food(
    modifier: Modifier = Modifier,
    expensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit,
    animalList: MutableList<AnimalExpensesList3>,
) {
    CardField(
        modifier = modifier,
        row = false
    ) {
        Checkbox(
            expensesTable = expensesTable,
            onValueChange = onValueChange,
            animalList = animalList
        )
        if (expensesTable.showFood && expensesTable.count != "") {
            if (!expensesTable.dailyExpensesFoodAndCount)
                ChoiceAnimal(
                    expensesTable,
                    animalList,
                    onValueChange
                )
            if (expensesTable.dailyExpensesFoodAndCount)
                InputText(
                    expensesTable = expensesTable,
                    onValueChange = onValueChange
                )
            if ((!expensesTable.dailyExpensesFoodAndCount && animalList.isNotEmpty() && animalList.any { it.ps })
                || (expensesTable.dailyExpensesFoodAndCount && expensesTable.countAnimal != "" && expensesTable.dailyExpensesFood != "")
            )
                TextFoodExpenses(expensesTable)
        }
    }
}

@Composable
private fun ShowWarehouse(
    modifier: Modifier = Modifier,
    domainExpensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit
) {
    var openAlertWarehouse by remember { mutableStateOf(false) }
    if (openAlertWarehouse) {
        AlertDialogInfo(
            onConfirmation = { openAlertWarehouse = false },
            intText = R.string.alert_dialog_info_text_show_warehouse,
            intTitleText = R.string.alert_dialog_info_title_show_warehouse
        )
    }
    CardField(
        modifier = modifier
    ) {
        CheckboxTextIcon(
            checked = domainExpensesTable.showWarehouse,
            onCheckedChange = { onValueChange(domainExpensesTable.copy(showWarehouse = it)) },
            enabled = !domainExpensesTable.showFood,
//                enabled = enableCheckBoxExpenses(
//                    value = count,
//                    boolean = showFoodUI
//                ),
            intTitle = R.string.checkbox_show_warehouse,
            onClick = { openAlertWarehouse = !openAlertWarehouse }
        )
    }
}

@Composable
private fun ShowAnimal(
    modifier: Modifier = Modifier,
    domainExpensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit,
    animalList: MutableList<AnimalExpensesList3>
) {
    var openAlertAnimal by remember { mutableStateOf(false) }
    if (openAlertAnimal) {
        AlertDialogInfo(
            onConfirmation = { openAlertAnimal = false },
            intText = R.string.alert_dialog_info_text_animals_expenses,
            intTitleText = R.string.alert_dialog_info_title_animals_expenses
        )
    }
    CardField(
        modifier = modifier,
        row = false
    ) {
        CheckboxTextIcon(
            checked = domainExpensesTable.showAnimals,
            onCheckedChange = {
                onValueChange(domainExpensesTable.copy(showAnimals = it))
                animalListClean(animalList)
            },
            enabled = !domainExpensesTable.showFood,
//                enabled = enableCheckBoxExpenses(
//                    value = count,
//                    valueTwo = priceAll,
//                    boolean = showFoodUI
//                ),
            intTitle = R.string.checkbox_animals_expenses,
            onClick = { openAlertAnimal = !openAlertAnimal }
        )

//    РАСПРЕДЕЛЕНИЕ РАСХОДОВ
        if (domainExpensesTable.showAnimals) {
            val totalFood = 100f
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                if (animalList.isNotEmpty()) {
                    animalList.forEach { animal ->
                        FilterChip(
                            selected = animal.ps,
                            onClick = {
                                animal.ps = !animal.ps
                                val selectedCount = animalList.count { it.ps }

                                // Распределение процентов поровну
                                if (selectedCount > 0) {
                                    val equalShare = 100.0 / selectedCount
                                    animalList.forEach {
                                        if (it.ps) it.presentException = equalShare
                                        else it.presentException = 0.0
                                    }
                                } else {
                                    animalList.forEach { it.presentException = 0.0 }
                                }
                            },
                            label = { Text(animal.name) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (animal.ps) Icons.Filled.Done else Icons.Filled.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            },
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                } else Text(text = stringResource(R.string.support_text_no_animal))

            }

            // Отображаем слайдеры для каждого выбранного животного
            animalList.filter { it.ps }.forEach { animal ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    val productExpenses =
                        formatter(animal.presentException * domainExpensesTable.count.toDouble() / 100.0)
                    val price =
                        formatter(animal.presentException * domainExpensesTable.priceAll.toDouble() / 100.0)

                    Text(
                        text = "${animal.name}: $productExpenses ${domainExpensesTable.suffix} / $price ₽ - ${
                            formatter(
                                animal.presentException
                            )
                        }%",
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Slider(
                        value = animal.presentException.toFloat(),
                        onValueChange = { newValue ->
                            animal.presentException = newValue.toDouble()

                            val remaining = totalFood - newValue
                            val others = animalList.count { it.ps } - 1
                            if (others > 0) {
                                animalList.filter { it.ps && it.id != animal.id }.forEach {
                                    it.presentException = (remaining / others).toDouble()
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

@Composable
private fun Checkbox(
    expensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit,
    animalList: MutableList<AnimalExpensesList3>
) {
    //Подсказки
    var openAlertFood by remember { mutableStateOf(false) }
    if (openAlertFood) {
        AlertDialogInfo(
            onConfirmation = { openAlertFood = false },
            intTitleText = R.string.alert_dialog_info_title_food,
            intText = R.string.alert_dialog_info_text_food,
        )
    }
    CheckboxTextIcon(
        checked = expensesTable.showFood,
        onCheckedChange = {
            onValueChange(
                expensesTable.copy(
                    showFood = it,
                    showWarehouse = it,
                    showAnimals = if (it) false else false
                )
            )
            animalListClean(animalList)
        },
        enabled = expensesTable.count != "",
        intTitle = R.string.checkbox_food,
        onClick = { openAlertFood = !openAlertFood }
    )
    if (expensesTable.showFood)
        CheckboxTextIcon(
            checked = expensesTable.dailyExpensesFoodAndCount,
            onCheckedChange = {
                onValueChange(
                    expensesTable.copy(
                        dailyExpensesFoodAndCount = it,
                        dailyExpensesFood = "",
                        countAnimal = ""
                    )
                )
                animalListClean(animalList)
            },
            intTitle = R.string.checkbox_set_hand
        )
}

@Composable
fun ChoiceAnimal(
    expensesTable: DomainExpensesTable,
    animalList: List<AnimalExpensesList3>,
    onValueChange: (DomainExpensesTable) -> Unit
) {
    if (animalList.isNotEmpty()) {
        var countAnimal by remember { mutableIntStateOf(0) } //кол-во животных
        var dailyExpensesFoodTotal by remember { mutableDoubleStateOf(0.0) } // Общий ежедневный расход

        Text(
            text = stringResource(R.string.expenses_entry_screen_chooise_animal_auto_food),
            modifier = Modifier
                .fillMaxWidth()
//                .padding(horizontal = 14.dp)
                .padding(top = 5.dp)
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(horizontal = 14.dp)
                .padding(top = 5.dp)
        ) {
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
                            Log.i(
                                "dailyExpensesFoodTotal",
                                "ChoiceAnimal: $dailyExpensesFoodTotal "
                            )
                        } else {
                            countAnimal -= it.countAnimal
                            dailyExpensesFoodTotal -= (it.foodDay * it.countAnimal)
                        }
                        it.ps = selected
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
                            Icon(
                                imageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    } else Text(text = stringResource(R.string.expenses_entry_screen_no_animal_auto_food))
}

@Composable
private fun InputText(
    modifier: Modifier = Modifier,
    expensesTable: DomainExpensesTable,
    onValueChange: (DomainExpensesTable) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var suffixCountAnimal by rememberSaveable { mutableStateOf(expensesTable.suffix) }
    var suffixExpensesFood by rememberSaveable { mutableStateOf(expensesTable.suffix) }

    OutlinedTextCountNoCard(
        modifier = modifier,
        value = expensesTable.dailyExpensesFood,
        onValueChange = {
            onValueChange(expensesTable.copy(dailyExpensesFood = it).validateDailyExpensesFood())
        },
        isWarehouseShow = false,
        versionDropMenu = 0,
        intRes = R.string.outlined_food_day_animals,
        intResSup = R.string.support_text_food_day,
        intResError = R.string.error_no_count_animals,
        isError = expensesTable.error.isErrorDailyExpensesFood,
        onClick = { suffixExpensesFood = it },
        suffix = suffixExpensesFood,
        focusManager = focusManager
    )
    OutlinedTextCountNoCard(
        modifier = modifier,
        value = expensesTable.countAnimal,
        onValueChange = {
            onValueChange(expensesTable.copy(countAnimal = it).validateCountAnimal())
        },
        drawableRes = R.drawable.baseline_spoke_24,
        isWarehouseShow = false,
        versionDropMenu = 2,
        intRes = R.string.outlined_text_field_quantity,
        intResSup = R.string.support_text_count_animals,
        intResError = R.string.error_no_count_product,
        isError = expensesTable.error.isErrorCountAnimal,
        suffix = suffixCountAnimal,
        onClick = { suffixCountAnimal = it },
        focusManager = focusManager,
        keyboardActions = keyboardActionsClear(focusManager)
    )
}


@Composable
fun TextFoodExpenses(
    expensesTable: DomainExpensesTable
) {
    val foodDesignedDayUI = settingDay(expensesTable)
    Text(
        text = stringResource(
            R.string.expenses_entry_screen_food_day
        ).format(
            if (expensesTable.title == "") stringResource(R.string.support_text_food) else expensesTable.title,
            if (foodDesignedDayUI.first >= 1000) stringResource(R.string.support_text_more) else "",
            foodDesignedDayUI.first,
            foodDesignedDayUI.second,
        )
    )
    if (!expensesTable.dailyExpensesFoodAndCount) {
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_every_day
            ).format(
                expensesTable.dailyExpensesFood,
                expensesTable.suffix,
            )
        )
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_animal_count
            ).format(
                expensesTable.countAnimal,
                stringResource(R.string.suffix_pieces)
            )
        )
    }
}

fun settingDay(
    domainExpensesTable: DomainExpensesTable
): Pair<Int, String> {
    val date = formatDateToString(
        domainExpensesTable.day,
        domainExpensesTable.mount,
        domainExpensesTable.year
    )
    val count = domainExpensesTable.count.toDouble()
    val foodDay = domainExpensesTable.dailyExpensesFood.toConvertZeroDouble()
    val countAnimal = domainExpensesTable.countAnimal.toConvertZeroDouble()

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dateLocal = LocalDate.parse(date, formatter)

    var days =
        (if (domainExpensesTable.dailyExpensesFoodAndCount) count / (countAnimal * foodDay)
        else count / foodDay).toLong()

    if (days > 1000) days = 1000
    val newDate = dateLocal.plusDays(days)
    return days.toInt() to newDate.format(formatter)
}

fun animalListClean(list: MutableList<AnimalExpensesList3>) {
    list.forEach {
        it.ps = false
    }
}
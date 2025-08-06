@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.zaroslikov.fermacompose2.ui.sections.expenses

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
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
            expensesEntryState = viewModel.expensesUiState,
            isEntry = viewModel.isEntry,
            titleList = viewModel.titleUiState.collectAsState().value.list,
            categoryList = viewModel.categoryUiState.collectAsState().value.list,
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
    expensesEntryState: ExpensesEntryState,
    isEntry: Boolean,
    titleList: List<String>,
    categoryList: List<String>,
    updateCountWarehouse: (String) -> Unit,
    onValueChange: (ExpensesEntryState) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    var date by rememberSaveable {
        mutableStateOf(
            formatDateToString(
                expensesEntryState.domainExpensesTable.day,
                expensesEntryState.domainExpensesTable.mount,
                expensesEntryState.domainExpensesTable.year
            )
        )
    }

    Column(modifier = modifier) {
        OutlinedTextTitleAdd(
            value = expensesEntryState.domainExpensesTable.title,
            onValueChange = {
                onValueChange(expensesEntryState.updateTitle(it))
                updateCountWarehouse(it)
            },
            titleList = titleList,
            isErrorTitle = expensesEntryState.error.isErrorTitle,
            isErrorSlash = expensesEntryState.error.isErrorSlash,
        )
        OutlinedTextCount(
            value = expensesEntryState.domainExpensesTable.count,
            onValueChange = { onValueChange(expensesEntryState.updateCount(it)) },
            onSuffixChange = { onValueChange(expensesEntryState.updateSuffix(it)) },
            isError = expensesEntryState.error.isErrorCount,
            suffix = expensesEntryState.domainExpensesTable.suffix,
            intResSup = R.string.support_text_count_product_expenses,
            countWarehouse = expensesEntryState.countInWarehouse.first,
            onWeightChange = { onValueChange(expensesEntryState.copy(weight = it)) },
            isWarehouseShow = false,
            weightValue = expensesEntryState.weight,
            weightSuffix = expensesEntryState.weightSuffix,
            isAutoCalculate = expensesEntryState.isAutoWeight,
            onAutoCalculate = { onValueChange(expensesEntryState.copy(isAutoWeight = it)) },
            onWeightSuffixChance = { onValueChange(expensesEntryState.copy(weightSuffix = it)) },
            isWeightCalculate = true,
        )
        OutlinedPriceInput(
            price = expensesEntryState.domainExpensesTable.priceAll,
            onPriceChange = { onValueChange(expensesEntryState.updatePrice(it)) },
            count = expensesEntryState.domainExpensesTable.count,
            isError = expensesEntryState.error.isErrorPrice,
            isAutoCalculate = expensesEntryState.isAutoCalculate,
            onAutoCalculate = { onValueChange(expensesEntryState.updateAutoCalculate(it)) },
            isManyCount = true,
            supportTextRes = R.string.support_text_price_expenses,
            supportTextResAutoCal = R.string.support_text_price_expenses,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
        )
        OutlinedTextCategory(
            value = expensesEntryState.domainExpensesTable.category,
            onValueChange = { onValueChange(expensesEntryState.updateCategory(it)) },
            titleList = categoryList,
        )
        OutlinedTextDateEdit(
            value = date,
            onValueChange = {
                date = it
                onValueChange(
                    expensesEntryState.updateDate(it)
                )
            }
        )
        OutlinedTextNote(
            value = expensesEntryState.domainExpensesTable.note,
            onValueChange = { onValueChange(expensesEntryState.updateNote(it)) },
        )
        AdditionalSettings(
            domainExpensesTable = expensesEntryState,
            onValueChange = onValueChange,
        )
        ButtonPanel(
            isEntry = isEntry,
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}


@Composable
private fun ButtonPanel(
    isEntry: Boolean,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
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
    domainExpensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit,
) {
    var details by rememberSaveable { mutableStateOf(true) }
    val extraPadding by animateDpAsState(
        if (details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )
    if (!domainExpensesTable.domainExpensesTable.count.isBlank() && !domainExpensesTable.domainExpensesTable.priceAll.isBlank()) {
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
            )
        }
    }
}


// КОРМ
@Composable
private fun Food(
    modifier: Modifier = Modifier,
    expensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit,
) {
    CardField(
        modifier = modifier,
        row = false
    ) {
        Checkbox(
            expensesTable = expensesTable,
            onValueChange = onValueChange,
        )
        if (expensesTable.domainExpensesTable.showFood && expensesTable.domainExpensesTable.count != "") {
            if (!expensesTable.domainExpensesTable.dailyExpensesFoodAndCount)
                ChoiceAnimal(
                    expensesTable,
                    onValueChange
                )
            if (expensesTable.domainExpensesTable.dailyExpensesFoodAndCount)
                InputText(
                    expensesTable = expensesTable,
                    onValueChange = onValueChange
                )
            if ((!expensesTable.domainExpensesTable.dailyExpensesFoodAndCount &&
                        expensesTable.animalList2.isNotEmpty() &&
                        expensesTable.animalList2.any { it.ps })
                || (expensesTable.domainExpensesTable.dailyExpensesFoodAndCount &&
                        expensesTable.feedFoodInput != "" &&
                        expensesTable.countAnimalInput != "")
            )
                TextFoodExpenses(expensesTable)
        }
    }
}

@Composable
private fun ShowWarehouse(
    modifier: Modifier = Modifier,
    domainExpensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit
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
            checked = domainExpensesTable.domainExpensesTable.showWarehouse,
            onCheckedChange = { onValueChange(domainExpensesTable.updateShowWarehouse(it)) },
            enabled = !domainExpensesTable.domainExpensesTable.showFood,
            intTitle = R.string.checkbox_show_warehouse,
            onClick = { openAlertWarehouse = !openAlertWarehouse }
        )
    }
}

@Composable
private fun ShowAnimal(
    modifier: Modifier = Modifier,
    domainExpensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit
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
            checked = domainExpensesTable.domainExpensesTable.showAnimals,
            onCheckedChange = { onValueChange(domainExpensesTable.updateShowAnimal(it)) },
            enabled = !domainExpensesTable.domainExpensesTable.showFood,
            intTitle = R.string.checkbox_animals_expenses,
            onClick = { openAlertAnimal = !openAlertAnimal }
        )

//    РАСПРЕДЕЛЕНИЕ РАСХОДОВ
        if (domainExpensesTable.domainExpensesTable.showAnimals) {
            val totalFood = 100f
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                if (domainExpensesTable.animalList2.isNotEmpty()) {
                    domainExpensesTable.animalList2.forEach { animal ->
                        FilterChip(
                            selected = animal.ps,
                            onClick = {
                                onValueChange(domainExpensesTable.toggleAnimalSelection(animal.id))
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
            domainExpensesTable.animalList2.filter { it.ps }.forEach { animal ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    val count =
                        if (domainExpensesTable.isAutoWeight) domainExpensesTable.weight.toConvertZeroDouble() * domainExpensesTable.domainExpensesTable.count.toDouble()
                        else domainExpensesTable.domainExpensesTable.count.toDouble()

                    val priceAll =
                        if (domainExpensesTable.isAutoCalculate) domainExpensesTable.domainExpensesTable.priceAll.toDouble() * domainExpensesTable.domainExpensesTable.count.toDouble()
                        else domainExpensesTable.domainExpensesTable.priceAll.toDouble()

                    val suffix =
                        if (domainExpensesTable.isAutoWeight) domainExpensesTable.domainExpensesTable.dailyExpensesFoodSuffix
                        else domainExpensesTable.domainExpensesTable.suffix

                    val productExpenses =
                        formatter(animal.presentException * count / 100.0)
                    val price =
                        formatter(animal.presentException * priceAll / 100.0)

                    Text(
                        text = "${animal.name}: $productExpenses $suffix / $price ₽ - " +
                                "${formatter(animal.presentException)}%",
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Slider(
                        value = animal.presentException.toFloat(),
                        onValueChange = { newValue ->
                            onValueChange(domainExpensesTable.updateAnimalSlider(animal.id, newValue.toDouble()))
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
    expensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit,
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
        checked = expensesTable.domainExpensesTable.showFood,
        onCheckedChange = { onValueChange(expensesTable.updateShowFood(it)) },
        enabled = expensesTable.domainExpensesTable.count != "",
        intTitle = R.string.checkbox_food,
        onClick = { openAlertFood = !openAlertFood }
    )
    if (expensesTable.domainExpensesTable.showFood)
        CheckboxTextIcon(
            checked = expensesTable.domainExpensesTable.dailyExpensesFoodAndCount,
            onCheckedChange = { onValueChange(expensesTable.updateDailyExpensesFoodAndCount(it)) },
            intTitle = R.string.checkbox_set_hand
        )
}

@Composable
fun ChoiceAnimal(
    expensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit
) {
    if (expensesTable.animalList2.isNotEmpty()) {
        Text(
            text = stringResource(R.string.expenses_entry_screen_chooise_animal_auto_food),
            modifier = Modifier.fillMaxWidth()
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            expensesTable.animalList2.forEach { animal ->
                val selected = animal.ps
                FilterChip(
                    selected = selected,
                    onClick = {
                        onValueChange(expensesTable.toggleAnimalChipSelection(animal))
                    },
                    label = { Text(animal.name) },
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
    expensesTable: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit
) {
    var suffixCountAnimal by rememberSaveable { mutableStateOf(expensesTable.domainExpensesTable.suffix) }

    OutlinedTextCountNoCard(
        value = expensesTable.feedFoodInput,
        onValueChange = { onValueChange(expensesTable.updateDailyExpensesFood(it)) },
        versionDropMenu = 0,
        intRes = R.string.outlined_food_day_animals,
        intResSup = R.string.support_text_food_day,
        intResError = R.string.error_no_count_animals,
        isError = expensesTable.error.isErrorDailyExpensesFood,
        suffix = expensesTable.feedFoodChipSuffix,
        onSuffixChance = { onValueChange(expensesTable.updateDailyExpensesFoodSuffix(it)) },
    )
    OutlinedTextCountNoCard(
        value = expensesTable.countAnimalInput,
        onValueChange = { onValueChange(expensesTable.updateCountAnimal(it)) },
        versionDropMenu = 2,
        intRes = R.string.outlined_text_field_quantity,
        intResSup = R.string.support_text_count_animals,
        intResError = R.string.error_no_count_product,
        isError = expensesTable.error.isErrorCountAnimal,
        suffix = suffixCountAnimal,
        onSuffixChance = { suffixCountAnimal = it },
        drawableRes = R.drawable.baseline_spoke_24,
        keyboardActions = KeyboardActionFocus.CLEAN
    )
}


@Composable
fun TextFoodExpenses(
    expensesTable: ExpensesEntryState
) {
    val foodDesignedDayUI = expensesTable.updateSettingDay()
    Text(
        text = stringResource(
            R.string.expenses_entry_screen_food_day
        ).format(
            if (expensesTable.domainExpensesTable.title == "") stringResource(R.string.support_text_food)
            else expensesTable.domainExpensesTable.title,
            if (foodDesignedDayUI.daysFood >= 1000) stringResource(R.string.support_text_more) else "",
            foodDesignedDayUI.daysFood,
            foodDesignedDayUI.dateEndFood,
        )
    )
    if (!expensesTable.domainExpensesTable.dailyExpensesFoodAndCount) {
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_every_day
            ).format(
                expensesTable.feedFoodChip.toFormatNumber(),
                expensesTable.feedFoodChipSuffix,
            )
        )
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_animal_count
            ).format(
                expensesTable.countAnimalChip.toFormatNumber(),
                stringResource(R.string.suffix_pieces)
            )
        )
    }
}

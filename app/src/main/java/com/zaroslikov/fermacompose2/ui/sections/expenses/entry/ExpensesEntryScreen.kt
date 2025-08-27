@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.zaroslikov.fermacompose2.ui.sections.expenses.entry

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.data.room.dto.PairData
import com.zaroslikov.fermacompose2.supportFun.animatedErrorPadding
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
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd2
import com.zaroslikov.fermacompose2.ui.composeElement.Suffix
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.expenses_screen_title, navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ExpensesEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding),
            state = viewModel.expensesUiState,
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
    state: ExpensesEntryState,
    titleList: List<PairData>,
    categoryList: List<String>,
    updateCountWarehouse: (String) -> Unit,
    onValueChange: (ExpensesEntryState) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val suffixSet =
        setOf(Suffix.GRAM.asString(), Suffix.KILOGRAM.asString(), Suffix.TONS.asString())
    Column(modifier = modifier) {
        OutlinedTextTitleAdd2(
            value = state.title,
            onValueChange = {
                onValueChange(state.updateTitle(it))
                updateCountWarehouse(it)
            },
            onValueChangeSuffix = {
                onValueChange(state.updateTitleAndSuffix(it, suffixSet))
                updateCountWarehouse(it.first)
            },
            titleList = titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue
        )
        OutlinedTextCount(
            value = state.count,
            onValueChange = { onValueChange(state.updateCount(it)) },
            onSuffixChange = { onValueChange(state.updateCountSuffix(it, suffixSet)) },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_expenses,
            countWarehouse = state.countInWarehouse.first.toString(),
            onWeightChange = { onValueChange(state.updateWeight(it)) },
            isWarehouseShow = false,
            versionDropMenu = if (state.isIndicatorsValue) 2 else 5,
            weightValue = state.weight,
            weightSuffix = state.weightSuffix,
            onWeightSuffixChance = { onValueChange(state.updateWeightSuffix(it)) },
            isAutoCalculate = state.isAutoWeight,
            onAutoCalculate = { onValueChange(state.updateAutoWeight(it, suffixSet)) },
            isWeightCalculate = !state.isIndicatorsValue,
        )
        OutlinedPriceInput(
            price = state.price,
            onPriceChange = { onValueChange(state.updatePrice(it)) },
            count = state.priceAll,
            isError = state.error.isErrorPrice,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = { onValueChange(state.updateAutoPrice(it)) },
            isManyCount = true,
            isNecessarily = true,
            supportTextRes = R.string.support_text_price_expenses,
            supportTextResAutoCal = R.string.support_text_price_expenses,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
        )
        if (!state.isIndicatorsValue)
            OutlinedTextCategory(
                value = state.category,
                onValueChange = { onValueChange(state.updateCategory(it)) },
                titleList = categoryList
            )
        if (!state.isIndicatorsValue)
            OutlinedTextDateEdit(
                value = state.date,
                onValueChange = { onValueChange(state.updateDate(it)) },
            )
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onValueChange(state.updateNote(it)) },
        )
        AdditionalSettings(
            state = state,
            onValueChange = onValueChange,
        )
        ButtonPanel(
            state = state,
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}


@Composable
private fun ButtonPanel(
    state: ExpensesEntryState,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    if (state.isEntry)
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
        if (!state.isIndicatorsValue)
            ButtonDelete { onClickDelete() }
    }
}


@Composable
private fun AdditionalSettings(
    state: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit,
) {
    var details by rememberSaveable { mutableStateOf(true) }
    if (!state.count.isBlank() && !state.price.isBlank() && !state.isIndicatorsValue) {
        TextButtonWarehouse(
            boolean = details,
            onClick = { details = !details },
            intRes = R.string.card_extra_set
        )
        if (details) {
            Food(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onValueChange = onValueChange,
            )
            ShowWarehouse(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onValueChange = onValueChange
            )
            ShowAnimal(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onValueChange = onValueChange,
            )
        }
    }
}

// КОРМ
@Composable
private fun Food(
    modifier: Modifier = Modifier,
    state: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit,
) {
    CardField(
        modifier = modifier.padding(bottom = animatedErrorPadding(state.error.isErrorFood)),
        row = false,
        isError = state.error.isErrorFood,
        isNecessarily = state.isShowFood
    ) {
        Checkbox(
            expensesTable = state,
            onValueChange = onValueChange,
        )
        if (state.isShowFood && state.count != "") {
            if (!state.isShowFoodHand)
                ChoiceAnimal(
                    state,
                    onValueChange
                )
            if (state.isShowFoodHand)
                InputText(
                    state = state,
                    onValueChange = onValueChange
                )
            if ((!state.isShowFoodHand &&
                        state.animalList2.isNotEmpty() &&
                        state.animalList2.any { it.ps })
                || (state.isShowFoodHand &&
                        state.feedFoodInput != "" &&
                        state.countAnimalInput != "")
            )
                TextFoodExpenses(state)
            if (state.error.isErrorFood)
                Text(
                    text = stringResource(R.string.error_show_food),
                    color = MaterialTheme.colorScheme.error
                )
        }
    }
}

@Composable
private fun ShowWarehouse(
    modifier: Modifier = Modifier,
    state: ExpensesEntryState,
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
            checked = state.isShowWarehouse,
            onCheckedChange = { onValueChange(state.updateShowWarehouse(it)) },
            enabled = !state.isShowFood,
            intTitle = R.string.checkbox_show_warehouse,
            onClick = { openAlertWarehouse = !openAlertWarehouse }
        )
    }
}

@Composable
private fun ShowAnimal(
    modifier: Modifier = Modifier,
    state: ExpensesEntryState,
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
        modifier = modifier.padding(bottom = animatedErrorPadding(state.error.isErrorAnimal)),
        row = false,
        isError = state.error.isErrorAnimal,
        isNecessarily = state.isShowAnimals
    ) {
        CheckboxTextIcon(
            checked = state.isShowAnimals,
            onCheckedChange = { onValueChange(state.updateShowAnimal(it)) },
            enabled = !state.isShowFood,
            intTitle = R.string.checkbox_animals_expenses,
            onClick = { openAlertAnimal = !openAlertAnimal }
        )

//    РАСПРЕДЕЛЕНИЕ РАСХОДОВ
        if (state.isShowAnimals) {
            val totalFood = 100f
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                if (state.animalList2.isNotEmpty()) {
                    state.animalList2.forEach { animal ->
                        FilterChip(
                            selected = animal.ps,
                            onClick = {
                                onValueChange(state.toggleAnimalSelection(animal.id))
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
            state.animalList2.filter { it.ps }.forEach { animal ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                ) {
                    val countTable =
                        state.count.toConvertZeroDouble()
                    val weightTable =
                        state.weight.toConvertZeroDouble()
                    val priceTable =
                        state.price.toConvertZeroDouble()

                    val count =
                        if (state.isAutoWeight) weightTable * countTable
                        else countTable

                    val priceAll =
                        if (state.isAutoPrice) priceTable * countTable
                        else priceTable

                    val suffix =
                        if (state.isAutoWeight) state.weightSuffix
                        else state.countSuffix

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
                            onValueChange(
                                state.updateAnimalSlider(
                                    animal.id,
                                    newValue.toDouble()
                                )
                            )
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
        if (state.error.isErrorAnimal)
            Text(
                text = stringResource(R.string.error_show_food),
                color = MaterialTheme.colorScheme.error
            )
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
    val suffixSet =
        setOf(Suffix.GRAM.asString(), Suffix.KILOGRAM.asString(), Suffix.TONS.asString())
    CheckboxTextIcon(
        checked = expensesTable.isShowFood,
        onCheckedChange = { onValueChange(expensesTable.updateShowFood(it)) },
        enabled = !(expensesTable.countSuffix !in suffixSet && !expensesTable.isAutoWeight),
        intTitle = R.string.checkbox_food,
        onClick = { openAlertFood = !openAlertFood }
    )
    if (expensesTable.isShowFood)
        CheckboxTextIcon(
            checked = expensesTable.isShowFoodHand,
            onCheckedChange = { onValueChange(expensesTable.updateDailyExpensesFoodAndCount(it)) },
            intTitle = R.string.checkbox_set_hand
        )
}

@Composable
fun ChoiceAnimal(
    state: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit
) {
    val animalList = state.animalList2.filter { it.foodDay != 0.0 }

    if (animalList.isNotEmpty()) {
        Text(
            text = stringResource(R.string.expenses_entry_screen_chooise_animal_auto_food),
            modifier = Modifier.fillMaxWidth()
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            animalList.forEach { animal ->
                val selected = animal.ps
                FilterChip(
                    selected = selected,
                    onClick = {
                        onValueChange(state.toggleAnimalChipSelection(animal))
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
    state: ExpensesEntryState,
    onValueChange: (ExpensesEntryState) -> Unit
) {
    var suffixCountAnimal by rememberSaveable { mutableStateOf(state.countSuffix) }

    OutlinedTextCountNoCard(
        value = state.feedFoodInput,
        onValueChange = { onValueChange(state.updateDailyExpensesFood(it)) },
        versionDropMenu = 0,
        intRes = R.string.outlined_food_day_animals,
        intResSup = R.string.support_text_food_day,
        intResError = R.string.error_no_count_animals,
        isError = state.error.isErrorDailyExpensesFood,
        suffix = state.feedFoodInputSuffix,
        onSuffixChance = { onValueChange(state.updateDailyExpensesFoodSuffix(it)) },
    )
    OutlinedTextCountNoCard(
        value = state.countAnimalInput,
        onValueChange = { onValueChange(state.updateCountAnimal(it)) },
        versionDropMenu = 2,
        intRes = R.string.outlined_text_field_quantity,
        intResSup = R.string.support_text_count_animals,
        intResError = R.string.error_no_count_product,
        isError = state.error.isErrorCountAnimal,
        suffix = suffixCountAnimal,
        onSuffixChance = { suffixCountAnimal = it },
        drawableRes = R.drawable.baseline_spoke_24,
        keyboardActions = KeyboardActionFocus.CLEAN
    )
}


@Composable
fun TextFoodExpenses(
    state: ExpensesEntryState
) {
    val foodDesignedDayUI = state.updateSettingDay()
    Text(
        text = stringResource(
            R.string.expenses_entry_screen_food_day
        ).format(
            if (state.title == "") stringResource(R.string.support_text_food)
            else state.title,
            if (foodDesignedDayUI.daysFood >= 1000) stringResource(R.string.support_text_more) else "",
            foodDesignedDayUI.daysFood,
            foodDesignedDayUI.dateEndFood,
        )
    )
    if (!state.isShowFoodHand) {
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_every_day
            ).format(
                state.feedFoodChip.toFormatNumber(),
                state.feedFoodChipSuffix,
            )
        )
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_animal_count
            ).format(
                state.countAnimalChip.toFormatNumber(),
                stringResource(R.string.suffix_pieces)
            )
        )
    }
}

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.zaroslikov.fermacompose2.ui.sections.expenses.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.supportFun.animatedErrorPadding
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.elements.DropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountNoCard
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleAdd2
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
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
    val eventFlow = viewModel.navigation
    val state by viewModel.state.collectAsStateWithLifecycle()
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
            state = state,
            onIntent = viewModel::onIntent,
            updateCountWarehouse = viewModel::updateWarehouseUiState
        )
    }
}

@Composable
fun ExpensesEntryContainerProduct(
    modifier: Modifier,
    state: ExpensesEntryState,
    updateCountWarehouse: (String) -> Unit,
    onIntent: (ExpensesEntryIntent) -> Unit
) {
    Column(modifier = modifier) {
        OutlinedTextTitleAdd2(
            value = state.title,
            onValueChange = {
                onIntent(ExpensesEntryIntent.TitleChanged(it))
                updateCountWarehouse(it)
            },
            onValueChangeSuffix = {
                onIntent(ExpensesEntryIntent.TitleAndSuffixClicked(it.first, it.second))
                updateCountWarehouse(it.first)
            },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue,
            cardBorder = true
        )
        OutlinedTextCountAnimal(
            value = state.count,
            onValueChange = {
                onIntent(ExpensesEntryIntent.CountChanged(it))
            },
            onSuffixChange = {
                onIntent(ExpensesEntryIntent.SuffixClicked(it))
            },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_expenses,
            countWarehouse = state.countInWarehouse.count.toString(),
            onWeightChange = {
                onIntent(ExpensesEntryIntent.WeightChanged(it))
            },
            isWarehouseShow = false,
            versionDropMenu = if (state.isIndicatorsValue) DropdownMenu.COUNT else DropdownMenu.ALL,
            weightValue = state.weight,
            weightSuffix = state.weightSuffix,
            onWeightSuffixChance = {
                onIntent(ExpensesEntryIntent.WeightSuffixChanged(it))
            },
            isAutoCalculate = state.isAutoWeight,
            onAutoCalculate = {
                onIntent(ExpensesEntryIntent.AutoWeightClicked(it))
            },
            isWeightCalculate = !state.isIndicatorsValue,
        )
        OutlinedPriceInput(
            price = state.price,
            onPriceChange = {
                onIntent(ExpensesEntryIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isError = state.error.isErrorPrice,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(ExpensesEntryIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            isNecessarily = true,
            supportTextRes = R.string.support_text_price_expenses,
            supportTextResAutoCal = R.string.support_text_price_expenses,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
        )
        if (!state.isIndicatorsValue)
            OutlinedTextCategory(
                value = state.category,
                onValueChange = {
                    onIntent(ExpensesEntryIntent.CategoryChanged(it))
                },
                titleList = state.pickList.categoryList
            )
        if (!state.isIndicatorsValue)
            OutlinedTextDate(
                value = state.date,
                onValueChange = {
                    onIntent(ExpensesEntryIntent.DateClicked(it))
                },
            )
        OutlinedTextNote(
            value = state.note,
            onValueChange = {
                onIntent(ExpensesEntryIntent.NoteChanged(it))
            }
        )
        AdditionalSettings(
            state = state,
            onIntent = onIntent
        )
        ButtonPanel(
            isEntry = state.isEntry,
            isIndicatorsValue = state.isIndicatorsValue,
            entryButton = R.string.button_expenses,
            onClickInsert = { onIntent(ExpensesEntryIntent.Insert) },
            onClickUpdate = { onIntent(ExpensesEntryIntent.Update) },
            onClickDelete = { onIntent(ExpensesEntryIntent.Delete) }
        )
    }
}


@Composable
private fun AdditionalSettings(
    state: ExpensesEntryState,
    onIntent: (ExpensesEntryIntent) -> Unit
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
                onShowFoodClick = { onIntent(ExpensesEntryIntent.ShowFoodClicked(it)) },
                onShowFoodHandClick = { onIntent(ExpensesEntryIntent.ShowFoodHandClicked(it)) },
                onAnimalChipClick = { onIntent(ExpensesEntryIntent.AnimalChipClicked(it)) },
                onFeedFoodChanged = { onIntent(ExpensesEntryIntent.FeedFoodChanged(it)) },
                onFeedFoodSuffixClick = { onIntent(ExpensesEntryIntent.FeedFoodSuffixClicked(it)) },
                onCountAnimalChanged = { onIntent(ExpensesEntryIntent.CountAnimalChanged(it)) },
            )
            ShowWarehouse(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onCheckboxClick = { onIntent(ExpensesEntryIntent.ShowWarehouseClicked(it)) }
            )
            ShowAnimal(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onShowAnimal = { onIntent(ExpensesEntryIntent.ShowAnimalClicked(it)) },
                onAnimalChipClick = { onIntent(ExpensesEntryIntent.AnimalChipByIdClicked(it)) },
                onAnimalSliderClick = {
                    onIntent(
                        ExpensesEntryIntent.AnimalSliderClicked(
                            animal = it.first,
                            newValue = it.second
                        )
                    )
                }
            )
        }
    }
}

// КОРМ
@Composable
private fun Food(
    modifier: Modifier = Modifier,
    state: ExpensesEntryState,
    onShowFoodClick: (Boolean) -> Unit,
    onShowFoodHandClick: (Boolean) -> Unit,
    onAnimalChipClick: (AnimalExpensesDomain) -> Unit,
    onFeedFoodChanged: (String) -> Unit,
    onFeedFoodSuffixClick: (Suffix) -> Unit,
    onCountAnimalChanged: (String) -> Unit,
) {
    CardField(
        modifier = modifier.padding(bottom = animatedErrorPadding(state.error.isErrorFood)),
        row = false,
        isError = state.error.isErrorFood,
        isNecessarily = state.isShowFood
    ) {
        Checkbox(
            expensesTable = state,
            onShowFoodClick = onShowFoodClick,
            onShowFoodHandClick = onShowFoodHandClick,
        )
        if (state.isShowFood && state.count != "") {
            if (!state.isShowFoodHand)
                ChoiceAnimal(
                    animalListState = state.pickList.animalList2,
                    onAnimalChipClick = onAnimalChipClick
                )
            if (state.isShowFoodHand)
                InputText(
                    state = state,
                    onFeedFoodChanged = onFeedFoodChanged,
                    onFeedFoodSuffixClick = onFeedFoodSuffixClick,
                    onCountAnimalChanged = onCountAnimalChanged,
                )
            if ((!state.isShowFoodHand &&
                        state.pickList.animalList2.isNotEmpty() &&
                        state.pickList.animalList2.any { it.ps })
                || (state.isShowFoodHand &&
                        state.feedFoodInput != "" &&
                        state.countAnimalInput != "")
            )
                TextFoodExpenses(state) // TODO Не обновлятет надо что-то думать
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
    onCheckboxClick: (Boolean) -> Unit
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
            onCheckedChange = {
                onCheckboxClick(it)
            },
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
    onShowAnimal: (Boolean) -> Unit,
    onAnimalChipClick: (Long) -> Unit,
    onAnimalSliderClick: (Pair<Long, Double>) -> Unit,
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
            onCheckedChange = {
                onShowAnimal(it)
            },
            enabled = !state.isShowFood,
            intTitle = R.string.checkbox_animals_expenses,
            onClick = { openAlertAnimal = !openAlertAnimal }
        )

//    РАСПРЕДЕЛЕНИЕ РАСХОДОВ
        if (state.isShowAnimals) {
            val totalFood = 100f
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                if (state.pickList.animalList2.isNotEmpty()) {
                    state.pickList.animalList2.forEach { animal ->
                        FilterChip(
                            selected = animal.ps,
                            onClick = {
                                onAnimalChipClick(animal.id)
                            },
                            label = { Text(animal.name) },
                            leadingIcon = {
                                Icon(
                                    painterResource(if (animal.ps) R.drawable.icon_check else R.drawable.icon_add),
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
            state.pickList.animalList2.filter { it.ps }.forEach { animal ->
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
                            onAnimalSliderClick(
                                animal.id to
                                        newValue.toDouble()
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
    onShowFoodClick: (Boolean) -> Unit,
    onShowFoodHandClick: (Boolean) -> Unit,
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
        setOf(
            Suffix.GRAM,
            Suffix.KILOGRAM,
            Suffix.TONS
        )
    CheckboxTextIcon(
        checked = expensesTable.isShowFood,
        onCheckedChange = { onShowFoodClick(it) },
        enabled = !(expensesTable.countSuffix !in suffixSet && !expensesTable.isAutoWeight),
        intTitle = R.string.checkbox_food,
        onClick = { openAlertFood = !openAlertFood }
    )
    if (expensesTable.isShowFood)
        CheckboxTextIcon(
            checked = expensesTable.isShowFoodHand,
            onCheckedChange = { onShowFoodHandClick(it) },
            intTitle = R.string.checkbox_set_hand
        )
}

@Composable
fun ChoiceAnimal(
    animalListState: List<AnimalExpensesDomain>,
    onAnimalChipClick: (AnimalExpensesDomain) -> Unit
) {
    val animalList = animalListState.filter { it.foodDay != 0.0 }

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
                        onAnimalChipClick(animal)
                    },
                    label = { Text(animal.name) },
                    leadingIcon = if (selected) {
                        {
                            Icon(
                                painterResource(if (selected) R.drawable.icon_check else R.drawable.icon_add),
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
    onFeedFoodChanged: (String) -> Unit,
    onFeedFoodSuffixClick: (Suffix) -> Unit,
    onCountAnimalChanged: (String) -> Unit,
) {
    var suffixCountAnimal by rememberSaveable { mutableStateOf(state.countSuffix) }

    OutlinedTextCountNoCard(
        value = state.feedFoodInput,
        onValueChange = { onFeedFoodChanged(it) },
        versionDropMenu = DropdownMenu.WEIGHT,
        intRes = R.string.outlined_food_day_animals,
        intResSup = R.string.support_text_food_day,
        intResError = R.string.error_no_count_animals,
        isError = state.error.isErrorDailyExpensesFood,
        suffix = state.feedFoodInputSuffix,
        onSuffixChance = { onFeedFoodSuffixClick(it) },
    )
    OutlinedTextCountNoCard(
        value = state.countAnimalInput,
        onValueChange = { onCountAnimalChanged },
        versionDropMenu = DropdownMenu.COUNT,
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
//    val foodDesignedDayUI = state.updateSettingDay()
    Text(
        text = stringResource(
            R.string.expenses_entry_screen_food_day
        ).format(
            if (state.title == "") stringResource(R.string.support_text_food)
            else state.title,
            if (state.daysFood >= 1000) stringResource(R.string.support_text_more) else "",
            state.daysFood,
            state.dateEndFood,
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

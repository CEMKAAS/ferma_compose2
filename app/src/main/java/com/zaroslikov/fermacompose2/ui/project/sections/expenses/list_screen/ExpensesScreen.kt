package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.fermacompose2.supportFun.animatedErrorPadding
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountNoCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.formatter
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet

object ExpensesDestination : NavigationDestination {
    override val route = "expenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    /* modifier: Modifier = Modifier,
     navigateToStart: () -> Unit,
     navigateToModalSheet: (DrawerNavigation) -> Unit,
     navigateToItemUpdate: (Pair<Long, Long>) -> Unit,
     navigateToItemAdd: (Long) -> Unit,
     drawerState: DrawerState,*/
    viewModel: ExpensesViewModel = hiltViewModel()
    /* state: AddListState,
     onIntent: (AddListIntent) -> Unit*/
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(orang_1, orang_2)
    val primeColor = orang_1
    val idProject = state.idPT

    val query = state.textSearch.trim().lowercase()
    val searchList = if (query.isBlank() && !state.isGroup) state.list
    else
        state.list.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.note.lowercase().contains(query) ||
                    item.category.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    stringResource(item.countSuffix.toResId()).lowercase().contains(query)
            "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                .contains(query) ||
                    (item.priceAll ?: item.price).toString().lowercase().contains(query)
        }

    val searchList2 = if (query.isBlank() && state.isGroup) state.briefly
    else
        state.briefly.filter { item ->
            item.title.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) ||
                    (item.price).toString().lowercase().contains(query)
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isGroup,
                onValueChange = {
                    viewModel.onIntent(ExpensesListIntent.SearchChanged(it))
                },
                onClick = { viewModel.onIntent(ExpensesListIntent.GroupClicked(it)) }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(ExpensesListIntent.OpenBottomSheetEntry(true))
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            ExpensesContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                color = primeColor,
                searchText = state.textSearch,
                itemList = state.list,
                searchList = searchList,
                brieflyList = searchList2,
                onInsertClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetEntry(true)
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        ExpensesListIntent.OpenBottomSheetGroup(true, it)
                    )
                },
                details = state.isGroup,
            )
            if (state.openBottomSheetEntry)
                ExpensesEntryBottomSheet(
                    modifier = Modifier,
                    state = state.currentProduct,
                    colors = colors,
                    onIntent = viewModel::onIntent
                )
            if (state.openBottomSheetGroup)
                BrieflyBottomSheetExpenses(
                    color = primeColor,
                    list = state.listBriefly,
                    titleProduct = state.currentBriefly.title,
                    count = state.currentBriefly.count,
                    suffix = state.currentBriefly.suffix,
                    countEntry = state.currentBriefly.rowCount,
                    onDismissRequest = {
                        viewModel.onIntent(
                            ExpensesListIntent.OpenBottomSheetGroup(
                                false
                            )
                        )
                    },
                    onEditClick = {
                        viewModel.onIntent(
                            ExpensesListIntent.OpenBottomSheetEntry(true, it)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(ExpensesListIntent.Delete(it)) },
                )
        }
    }
}


@Composable
fun ExpensesContainer(
    modifier: Modifier = Modifier,
    details: Boolean,
    color: Color = blue_1,
    searchText: String,
    itemList: List<DomainExpensesTable>,
    searchList: List<DomainExpensesTable>,
    brieflyList: List<BrieflyExpensesDomain>,
    onInsertClick: () -> Unit,
    onEditClick: (DomainExpensesTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (BrieflyExpensesDomain) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailsClick = onDetailsClick,
        detailCard = { index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = Suffix.RUBLE,
                category = item.category,
                note = item.note,
                animal = "Murka",
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                onClick = { },
                onEditClick = { onEditClick(item) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                titleProduct = item.title,
                count = item.count,
                suffix = item.suffix,
                price = item.price,
                priceSuffix = Suffix.RUBLE,
                countEntry = item.rowCount,
                color = color,
                colorSecondary = Color(0xFFFFF7ED),
                onClick = { onDetailsClick(item) },
                icon = R.drawable.icon_expenses,
            )
        },
        titleRes = R.string.message_no_date_title_sale,
        messageRes = R.string.message_no_date_message_sale,
        supportRes = R.string.message_no_date_support_text_sale,
        buttonRes = R.string.button_sale_message_no_data,
        details = details
    )
}


@Composable
fun BrieflyBottomSheetExpenses(
    list: List<DomainExpensesTable>,
    color: Color = blue_1,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    onEditClick: (DomainExpensesTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    BrieflyBottomSheetUniversal(
        list = list,
        titleProduct = titleProduct,
        count = count,
        suffix = suffix,
        countEntry = countEntry,
        onDismissRequest = onDismissRequest,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        itemCard = { product ->
            DetailProductCardNew(
                modifier = Modifier,
                isCardField = false,
                count = product.count,
                suffix = product.countSuffix,
                price = product.price,
                category = product.category,
                note = product.note,
                animal = "Mas",
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                onClick = {},
                onDeleteClick = { onDeleteClick(product.id) },
                onEditClick = { onEditClick(product) }
            )
        }
    )
}

@Composable
fun ExpensesEntryBottomSheet(
    modifier: Modifier,
    colors: List<Color>,
    state: ExpensesEntryState2,
    onIntent: (ExpensesListIntent) -> Unit
) {
    EntryBottomSheet(
        modifier = Modifier,
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(
                ExpensesListIntent.OpenBottomSheetEntry(false)
            )
        },
        onInsertClick = { onIntent(ExpensesListIntent.Insert) },
        onUpdateClick = { onIntent(ExpensesListIntent.Update) }
    ) {
        OutlinedTextTitleAddNew(
            value = state.title,
            onValueChange = {
                onIntent(ExpensesListIntent.TitleChanged(it))
                /*updateCountWarehouse(it)*/
            },
            onValueChangeSuffix = {
                onIntent(ExpensesListIntent.TitleAndSuffixClicked(it.first, it.second))
                /*updateCountWarehouse(it.first)*/
            },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue,
            isBorderCard = true
        )
        OutlinedTextCountAnimal(
            value = state.count,
            onValueChange = {
                onIntent(ExpensesListIntent.CountChanged(it))
            },
            onSuffixChange = {
                onIntent(ExpensesListIntent.SuffixClicked(it))
            },
            isError = state.error.isErrorCount,
            suffix = state.countSuffix,
            intResSup = R.string.support_text_count_product_expenses,
            countWarehouse = state.countInWarehouse.count.toString(),
            onWeightChange = {
                onIntent(ExpensesListIntent.WeightChanged(it))
            },
            isWarehouseShow = false,
            versionDropMenu = if (state.isIndicatorsValue) DropdownMenu.COUNT else DropdownMenu.ALL,
            weightValue = state.weight,
            weightSuffix = state.weightSuffix,
            onWeightSuffixChance = {
                onIntent(ExpensesListIntent.WeightSuffixChanged(it))
            },
            isAutoCalculate = state.isAutoWeight,
            onAutoCalculate = {
                onIntent(ExpensesListIntent.AutoWeightClicked(it))
            },
            isWeightCalculate = !state.isIndicatorsValue,
        )
       /* WarehouseCountCard(
            title = state.title,
            warehouseList = state.warehouseList
        )*/

        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(ExpensesListIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isError = state.error.isErrorPrice,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(ExpensesListIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            isNecessarily = true,
            supportTextRes = R.string.support_text_price_expenses,
            supportTextResAutoCal = R.string.support_text_price_expenses,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
            count = state.count,
            countSuffix = state.countSuffix,
            priceSuffix = Suffix.RUBLE,
        )
        if (!state.isIndicatorsValue)
            OutlinedTextCategoryNew(
                value = state.category,
                onValueChange = {
                    onIntent(ExpensesListIntent.CategoryChanged(it))
                },
                titleList = state.pickList.categoryList
            )
        if (!state.isIndicatorsValue)
            OutlinedTextDateNew(
                value = state.date,
                onValueChange = {
                    onIntent(ExpensesListIntent.DateClicked(it))
                },
            )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = {
                onIntent(ExpensesListIntent.NoteChanged(it))
            }
        )
        AdditionalSettings2(
            state = state,
            onIntent = onIntent
        )
    }
}


@Composable
private fun AdditionalSettings2(
    state: ExpensesEntryState2,
    onIntent: (ExpensesListIntent) -> Unit
) {
    var details by rememberSaveable { mutableStateOf(true) }
    if (!state.count.isBlank() && !state.price.isBlank() && !state.isIndicatorsValue) {
      /*  TextButtonWarehouse(
            boolean = details,
            onClick = { details = !details },
            intRes = R.string.card_extra_set
        )*/
        if (details) {
            Food(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onShowFoodClick = { onIntent(ExpensesListIntent.ShowFoodClicked(it)) },
                onShowFoodHandClick = { onIntent(ExpensesListIntent.ShowFoodHandClicked(it)) },
                onAnimalChipClick = { onIntent(ExpensesListIntent.AnimalChipClicked(it)) },
                onFeedFoodChanged = { onIntent(ExpensesListIntent.FeedFoodChanged(it)) },
                onFeedFoodSuffixClick = { onIntent(ExpensesListIntent.FeedFoodSuffixClicked(it)) },
                onCountAnimalChanged = { onIntent(ExpensesListIntent.CountAnimalChanged(it)) },
            )
            ShowWarehouse(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onCheckboxClick = { onIntent(ExpensesListIntent.ShowWarehouseClicked(it)) }
            )
            ShowAnimal(
                modifier = Modifier.padding(bottom = animatedErrorPadding(details)),
                state = state,
                onShowAnimal = { onIntent(ExpensesListIntent.ShowAnimalClicked(it)) },
                onAnimalChipClick = { onIntent(ExpensesListIntent.AnimalChipByIdClicked(it)) },
                onAnimalSliderClick = {
                    onIntent(
                        ExpensesListIntent.AnimalSliderClicked(
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
    state: ExpensesEntryState2,
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
    state: ExpensesEntryState2,
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
    state: ExpensesEntryState2,
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
    expensesTable: ExpensesEntryState2,
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
    state: ExpensesEntryState2,
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
    state: ExpensesEntryState2
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
/*

@Composable
private fun ExpensesBody(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel,
    itemList: List<DomainExpensesTable>,
    brieflyList: List<BrieflyExpensesDomain>,
    onItemClick: (Pair<Long, Long>) -> Unit,
    navigateToItemAdd: () -> Unit
) {
    if (itemList.isNotEmpty())
        InventoryList(
            itemList = itemList,
            brieflyList = brieflyList,
            viewModel = viewModel,
            onItemClick = { onItemClick(Pair(it.idPT, it.id)) },
            modifier = modifier
        )
    else MessageNoData(
        modifier = modifier,
        onClick = navigateToItemAdd,
        titleRes = R.string.message_no_date_title_sale,
        messageRes = R.string.message_no_date_message_sale,
        supportRes = R.string.message_no_date_support_text_sale,
        buttonRes = R.string.button_sale_message_no_data
    )
}

@Composable
private fun InventoryList(
    viewModel: ExpensesViewModel,
    itemList: List<DomainExpensesTable>,
    brieflyList: List<BrieflyExpensesDomain>,
    onItemClick: (DomainExpensesTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var details by rememberSaveable { mutableStateOf(true) }

    val extraPadding by animateDpAsState(
        if (details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )
    val extraPaddingResd by animateDpAsState(
        if (!details) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            TextButtonWarehouse(
                boolean = details,
                onClick = { details = !details },
                intRes = if (details) R.string.widget_briefly else R.string.widget_detail
            )
        }
        if (details) {
            items(items = itemList, key = { it.id }) { item ->
                ExpensesCard(
                    expensesTable = item,
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(bottom = extraPadding.coerceAtLeast(0.dp))
                )
            }
        } else {
            items(items = brieflyList) { item ->
                BrieflyPriceCard(
                    viewModel = viewModel,
                    product = item,
                    onItemClick = onItemClick,
                    modifier = Modifier
                        .padding(bottom = extraPaddingResd.coerceAtLeast(0.dp))
                )
            }
        }
    }
}

@Composable
fun BrieflyPriceCard(
    viewModel: ExpensesViewModel,
    product: BrieflyExpensesDomain,
    onItemClick: (DomainExpensesTable) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    CardField(modifier = modifier.clickable {
        expanded = !expanded
    }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                TextLine(
                    modifier = Modifier.padding(start = 3.dp, bottom = 5.dp),
                    valueString = product.title,
                    textStyle = textBold_20
                )
                IconAndText(
                    iconRes = R.drawable.baseline_shopping_basket_24,
                    valueString = stringResource(
                        R.string.card_count_briefly_s,
                        "${product.count.formatNumber()} ${product.suffix}",
                        product.price.formatNumber(),
                        stringResource(R.string.currency_ruble),
                    ),
                )
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painterResource(if (expanded) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
                    contentDescription = "Показать меню"
                )
            }
        }
    }
    if (expanded) {
        val products = viewModel.getDetailsName(product.title).collectAsState(initial = emptyList())
        products.value.forEach {
            ExpensesCard(
                expensesTable = it,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = 0.95f
                    }
                    .clickable { onItemClick(it) }
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp)))
        }
    }
}


@Composable
fun ExpensesCard(
    expensesTable: DomainExpensesTable,
    modifier: Modifier = Modifier
) {
    val price = expensesTable.priceAll ?: expensesTable.price
    CardField(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(start = 3.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = expensesTable.title,
                        style = textBold_20,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (expensesTable.animalId != null)
                        Icon(
                            painter = painterResource(R.drawable.baseline_pets_24),
                            contentDescription = stringResource(R.string.is_empty)
                        )
                }
                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateBuilder(
                        expensesTable.day,
                        expensesTable.month,
                        expensesTable.year
                    )
                )
                expensesTable.category.takeUnless { it == "Без категории" || it.isEmpty() }
                    ?.let { category ->
                        IconAndText(
                            iconRes = R.drawable.baseline_format_list_bulleted_24,
                            valueString = category
                        )
                    }
                if (expensesTable.note != "")
                    IconAndText(
                        iconRes = R.drawable.baseline_sticky_note_2_24,
                        valueString = expensesTable.note
                    )
            }
            Text(
                text = stringResource(
                    R.string.card_count_s,
                    "${expensesTable.count.formatNumber()} ${expensesTable.countSuffix}",
                    price.formatNumber(),
                    stringResource(R.string.currency_ruble),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                style = textBold_20
            )
        }
    }
}*/

package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAny
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_13
import com.zaroslikov.fermacompose2.green_14
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.fermacompose2.supportFun.animatedErrorPadding
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.ButtonForGroupButtons
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountNoCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedCountInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.formatter
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.GroupCard
import com.zaroslikov.fermacompose2.white

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
        if (state.pickList.animalList2.isNotEmpty())
            GroupCard(
                titleRes = R.string.expenses_screen_type_expenses,
                iconOneRes = R.drawable.icon_sale,
                iconTwoRes = R.drawable.baseline_shopping_basket_24,
                textOneRes = R.string.expenses_screen_common_expenses,
                textTwoRes = R.string.expenses_screen_button_food,
                isSecondValue = state.isFood,
                onClick = { onIntent(ExpensesListIntent.FoodClicked(it)) }
            )
        OutlinedTextTitleAddNew(
            value = state.title,
            onValueChange = {
                onIntent(ExpensesListIntent.TitleChanged(it))
            },
            onValueChangeSuffix = {
                onIntent(ExpensesListIntent.TitleAndSuffixClicked(it.first, it.second))
            },
            titleList = state.pickList.titleList,
            isErrorTitle = state.error.isErrorTitle,
            isErrorSlash = state.error.isErrorSlash,
            readOnly = state.isIndicatorsValue,
            enable = !state.isIndicatorsValue,
            isBorderCard = true
        )
        OutlinedCountInputNew(
            count = state.count,
            countSuffixList = state.suffixList,
            onCountChange = { onIntent(ExpensesListIntent.CountChanged(it)) },
            countSuffix = state.countSuffix,
            onSuffixChange = { onIntent(ExpensesListIntent.SuffixClicked(it)) },
            isAutoCalculate = state.isAutoWeight,
            onAutoCalculate = { onIntent(ExpensesListIntent.AutoWeightClicked(it)) },
            weight = state.weight,
            onWeightChange = { onIntent(ExpensesListIntent.WeightChanged(it)) },
            weightSuffix = state.weightSuffix,
            onWeightSuffixChance = { onIntent(ExpensesListIntent.WeightSuffixChanged(it)) },
            isError = state.error.isErrorCount,
            isShowCheckbox = state.isFood && !state.isIndicatorsValue && state.isShowCheckbox,
            weightAll = state.weightAll,
            weightAllSuffix = state.weightAllSuffix,
            enabledWeightSuffix = !(state.weightSuffix == Suffix.KILOGRAM_TO_CUBIC_METERS ||
                    state.weightSuffix == Suffix.KILOGRAM_TO_LITERS)
        )
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
            supportTextRes = R.string.support_text_price_expenses,
            supportTextResAutoCal = R.string.support_text_price_expenses,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
            count = state.count,
            countSuffix = state.countSuffix,
            priceSuffix = Suffix.RUBLE,
        )
        if (state.isIndicatorsValue || !state.isFood)
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
        if (state.isFood)
            AdditionalFunctionFood(
                animalList = state.pickList.animalList2,
                onAnimalClick = { onIntent(ExpensesListIntent.AnimalChipClicked2(it)) },
                countAnimal = state.countAnimalChip,
                feedFood = "${state.feedFoodChip} ${stringResource(state.feedFoodChipSuffix.toResId())}",
                day = state.daysFood.formatNumber(),
                dateEnd = state.dateEnd,
            )
        AdditionalFunction(
            state = state,
            onIntent = onIntent
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = {
                onIntent(ExpensesListIntent.NoteChanged(it))
            }
        )
        /*AdditionalSettings2(
            state = state,
            onIntent = onIntent
        )*/
    }
}

@Composable
private fun AdditionalFunctionFood(
    animalList: List<AnimalExpensesDomain>,
    onAnimalClick: (Long) -> Unit,
    feedFood: String,
    day: String,
    dateEnd: String,
    countAnimal: String
) {
    val animatedPadding by animateDpAsState(
        targetValue = 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    AnimalList(
        modifier = Modifier.padding(bottom = animatedPadding.coerceAtLeast(0.dp)),
        animalList = animalList,
        onAnimalClick = onAnimalClick
    )
    AnimatedVisibility(
        visible = animalList.fastAny { it.ps }
    ) {
        InfoFoodCard(
            modifier = Modifier,
            foodFeel = feedFood,
            day = day,
            dateEnd = dateEnd,
            countAnimal = countAnimal
        )
    }
}

@Composable
private fun AdditionalFunction(
    state: ExpensesEntryState2,
    onIntent: (ExpensesListIntent) -> Unit
) {
    if (!state.isFood && state.pickList.animalList2.isNotEmpty()) {
        ChoiceAnimalCard(
            state = state
        ) { onIntent(ExpensesListIntent.AnimalChipByIdClicked(it)) }
        if (state.pickList.animalList2.any { it.ps }) {
            ChoiceMode(
                isTemplatesPlan = state.isPercent,
                onValueChange = { onIntent(ExpensesListIntent.PercentClicked(it)) },
                onEquallyClick = { onIntent(ExpensesListIntent.EquallyClicked) })
            AnimalFinanceList(
                isPercent = state.isPercent,
                animalList = state.pickList.animalList2.filter { it.ps },
                onSliderChanged = {
                    onIntent(
                        ExpensesListIntent.AnimalSliderClicked(
                            animal = it.first,
                            newValue = it.second
                        )
                    )
                })
        }
    }
}


@Composable
private fun ChoiceAnimalCard(
    state: ExpensesEntryState2,
    onAnimalChipClick: (Long) -> Unit,
) {
    BorderCard {
        Text(
            stringResource(R.string.expenses_screen_choice_animals),
            style = text_16,
            color = dark
        )
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            state.pickList.animalList2.forEach { animal ->
                FilterChip(
                    selected = animal.ps,
                    onClick = {
                        onAnimalChipClick(animal.id)
                    },
                    label = {
                        Column(
                            Modifier.padding(vertical = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(animal.name, style = text_14, color = marengo)
                            Text(animal.name, style = text_12)
                        }
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(if (animal.ps) R.drawable.icon_check else R.drawable.icon_add),
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun ChoiceMode(
    isTemplatesPlan: Boolean,
    onValueChange: (Boolean) -> Unit,
    onEquallyClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            GroupButton {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val (textColor, backgroundColor) =
                        if (isTemplatesPlan) white to blue_4
                        else marengo to gray_6

                    val (textColorTwo, backgroundColorTwo) =
                        if (!isTemplatesPlan) white to green_6
                        else marengo to gray_6

                    ButtonForGroupButtons(
                        modifier = Modifier.weight(1f),
                        text = R.string.expenses_screen_percents,
                        backgroundColor = backgroundColor,
                        textColor = textColor
                    ) { onValueChange(!isTemplatesPlan) }

                    ButtonForGroupButtons(
                        modifier = Modifier.weight(1f),
                        text = R.string.expenses_screen_by_hand,
                        backgroundColor = backgroundColorTwo,
                        textColor = textColorTwo
                    ) { onValueChange(!isTemplatesPlan) }
                }
            }
        }
        BorderCard(
            modifier = Modifier.fillMaxHeight(),
            padding = PaddingValues(horizontal = 12.dp),
            onClick = { onEquallyClick() }
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.expenses_screen_equally),
                    style = text_14,
                    color = black
                )
            }
        }
    }
}

@Composable
private fun AnimalFinanceList(
    isPercent: Boolean,
    animalList: List<AnimalExpensesDomain>,
    onSliderChanged: (Pair<Long, Double>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        animalList.forEach { animal ->
            if (isPercent)
                AnimalFinanceCard2(
                    id = animal.id,
                    name = animal.name,
                    price = 0.0,
                    suffix = Suffix.RUBLE,
                    percent = animal.presentException,
                    onSliderChanged = onSliderChanged
                )
            else
                AnimalFinanceCard(
                    name = animal.name,
                    price = "22",
                    suffix = Suffix.RUBLE,
                    percent = 12.0
                )
        }
    }
}

@Composable
private fun AnimalFinanceCard(
    name: String,
    price: String,
    suffix: Suffix,
    percent: Double
) {
    BorderCard(
        padding = PaddingValues(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(name, style = text_16, color = black_2)
            OutlinedNumberNew(
                value = price,
                onValueChange = { },
                suffix = suffix,
                intRes = R.string.expenses_screen_sum,
                isBorderCard = false
            )
            BorderCard(
                containerColor = blue_3,
                borderColor = blue_9,
                padding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.expenses_screen_percent),
                        style = text_14,
                        color = blue_8
                    )
                    Text("${percent.formatNumber()}%", style = text_16, color = blue_14)
                }
            }
        }
    }
}

@Composable
private fun AnimalFinanceCard2(
    id: Long,
    name: String,
    price: Double,
    suffix: Suffix,
    percent: Double,
    onSliderChanged: (Pair<Long, Double>) -> Unit
) {
    BorderCard(
        padding = PaddingValues(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(name, style = text_16, color = black_2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.expenses_screen_percent),
                    style = text_14,
                    color = marengo
                )
                Text("${percent.formatNumber()}%", color = blue_1, style = text_18)
            }
            Slider(
                value = percent.toFloat(),
                onValueChange = { newValue ->
                    onSliderChanged(
                        id to newValue.toDouble()
                    )
                },
                valueRange = 0f..100f,
                steps = 99,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 2.5.dp)
            )
            BorderCard(
                containerColor = price_green_2,
                borderColor = green_11,
                padding = PaddingValues(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.expenses_screen_sum),
                        style = text_14,
                        color = green_9
                    )
                    Text(
                        "${price.formatNumber()} ${stringResource(suffix.toResId())}",
                        style = text_16,
                        color = green_13
                    )
                }
            }
        }
    }
}


@Composable
private fun AnimalList(
    modifier: Modifier,
    animalList: List<AnimalExpensesDomain>,
    onAnimalClick: (Long) -> Unit
) {
    var isShowAnimalList by rememberSaveable { mutableStateOf(false) }
    val icon =
        if (isShowAnimalList) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down
    BorderCard(
        modifier = modifier,
        borderColor = green_1,
        containerColor = green_g_1,
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(18.dp),
        onClick = { isShowAnimalList = !isShowAnimalList }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_pets_24),
                        contentDescription = null,
                        tint = green_shamrock,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        stringResource(R.string.expenses_screen_choice_animals),
                        style = text_16,
                        color = green_14
                    )
                }
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = green_shamrock
                )
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isShowAnimalList
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    animalList.forEach { animal ->
                        AnimalCard(
                            name = animal.name,
                            type = animal.name,
                            feedFood = animal.foodDay,
                            feedFoodSuffix = animal.foodDaySuffix,
                            countAnimal = animal.countAnimal,
                            totalFeedFood = "22",
                            isChoice = animal.ps
                        ) {
                            onAnimalClick(animal.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoFoodCard(
    modifier: Modifier,
    foodFeel: String,
    day: String,
    dateEnd: String,
    countAnimal: String
) {
    BorderCard(
        modifier = modifier,
        borderColor = blue_9,
        containerColor = blue_3,
        padding = PaddingValues(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painterResource(R.drawable.baseline_analytics_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(stringResource(R.string.expenses_screen_sum), style = text_16, color = blue_14)
            }
            DoubleSupportCards(
                titleRes = R.string.expenses_screen_count_animal,
                value = countAnimal,
                secondTitleRes = R.string.expenses_screen_food_feed,
                secondValue = foodFeel
            )
            DoubleSupportCards(
                titleRes = R.string.expenses_screen_have,
                value = day,
                secondTitleRes = R.string.expenses_screen_end_day,
                secondValue = dateEnd
            )
        }
    }
}

@Composable
private fun DoubleSupportCards(
    @StringRes titleRes: Int,
    value: String,
    @StringRes secondTitleRes: Int,
    secondValue: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SupportCard(
            modifier = Modifier.weight(1f),
            titleRes = titleRes,
            value = value,
        )
        SupportCard(
            modifier = Modifier.weight(1f),
            titleRes = secondTitleRes,
            value = secondValue
        )
    }
}

@Composable
private fun SupportCard(
    modifier: Modifier,
    @StringRes titleRes: Int,
    value: String
) {
    BorderCard(
        modifier = modifier,
        borderColor = blue_9,
        containerColor = white, shape = RoundedCornerShape(14.dp), padding = PaddingValues(13.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(stringResource(titleRes), style = text_12, color = blue_1)
            Text(value, style = text_14, color = blue_14)
        }
    }
}


@Composable
private fun AnimalCard(
    isChoice: Boolean,
    name: String,
    type: String,
    feedFood: Double,
    feedFoodSuffix: Suffix,
    countAnimal: Int,
    totalFeedFood: String,
    onClick: () -> Unit
) {
    val suffix = stringResource(feedFoodSuffix.toResId())

    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = white.copy(alpha = 0.5f),
        padding = PaddingValues((12.dp)),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(checked = isChoice, onCheckedChange = { onClick() })
            Icon(
                painter = painterResource(R.drawable.baseline_pets_24),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(name, style = text_16, color = black_2)
                Text(type, style = text_14, color = marengo)
                Text(
                    "${feedFood.formatNumber()} $suffix × $countAnimal = $totalFeedFood $suffix",
                    style = text_14,
                    color = marengo
                )
            }
        }
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
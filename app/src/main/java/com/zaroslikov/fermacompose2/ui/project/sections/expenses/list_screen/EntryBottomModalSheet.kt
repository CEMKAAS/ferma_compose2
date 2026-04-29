package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
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
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_8
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
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.ButtonForGroupButtons
import com.zaroslikov.fermacompose2.ui.elements.CustomCheckbox
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedCountInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.GroupCard
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.white

@Composable
fun ExpensesEntryBottomSheet(
    colors: List<Color>,
    priceSuffix: Suffix,
    state: ExpensesEntryState2,
    onIntent: (ExpensesListIntent) -> Unit
) {
    val titleEdit =
        if (state.isIndicatorsValue) R.string.expenses_screen_title_edit_animal else R.string.expenses_screen_title_edit
    EntryBottomSheet(
        titleEntryRes = R.string.expenses_screen_title_entry,
        titleEditRes = titleEdit,
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                ExpensesListIntent.OpenEntryBottomSheetByItem(
                    value = false,
                    isSaveStateForBottomSheet = state.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(ExpensesListIntent.OpenEntryBottomSheetByItem(false))
        },
        onInsertClick = { onIntent(ExpensesListIntent.Insert) },
        onUpdateClick = { onIntent(ExpensesListIntent.Update) }
    ) {
        if (state.pickList.animalList2.isNotEmpty() && !state.isIndicatorsValue)
            GroupCard(
                titleRes = R.string.expenses_screen_type_expenses,
                iconOneRes = R.drawable.icon_sale,
                iconTwoRes = R.drawable.wheat_24dp_000000_fill0_wght400_grad0_opsz24,
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
            enabled = !state.isIndicatorsValue,
            isAutoCalculate = state.isAutoWeight,
            onAutoCalculate = { onIntent(ExpensesListIntent.AutoWeightClicked(it)) },
            weight = state.weight,
            onWeightChange = { onIntent(ExpensesListIntent.WeightChanged(it)) },
            weightSuffix = state.weightSuffix,
            onWeightSuffixChance = { onIntent(ExpensesListIntent.WeightSuffixChanged(it)) },
            isError = state.error.isErrorCount,
            isShowCheckbox = state.isFood && !state.isIndicatorsValue && state.isShowAutoWeightCheckbox,
            weightAll = state.weightAll,
            weightAllSuffix = state.weightAllSuffix,
            enabledWeightSuffix = !(state.weightSuffix == Suffix.KILOGRAM_TO_CUBIC_METERS ||
                    state.weightSuffix == Suffix.KILOGRAM_TO_LITERS)
        )
        if (!state.isIndicatorsValue)
            WarehouseCountCard(
                title = state.title,
                warehouseList = state.pickList.warehouseList
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
            priceSuffix = priceSuffix,
        )
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
        if (!state.isIndicatorsValue && state.isFood && (!state.isShowAutoWeightCheckbox || state.isAutoWeight))
            AdditionalFunctionFood(
                animalList = state.pickList.animalList2,
                onAnimalClick = { onIntent(ExpensesListIntent.AnimalChipByIdFoodClicked(it)) },
                countAnimal = state.countAnimalFood,
                feedFood = "${state.feedFood} ${stringResource(state.feedFoodSuffix.toResId())}",
                day = "${state.daysFood.formatNumber()} ${stringResource(R.string.expenses_screen_days)}",
                dateEnd = state.dateEndFood,
                isEntry = state.isEntry
            )
        if (!state.isIndicatorsValue && !state.isFood && state.pickList.animalList2.isNotEmpty())
            AdditionalFunction(
                animalList = state.pickList.animalList2,
                isPercent = state.isPercent,
                priceSuffix = priceSuffix,
                onAnimalChipClicked = { onIntent(ExpensesListIntent.AnimalChipByIdClicked(it)) },
                onPercentClicked = { onIntent(ExpensesListIntent.PercentClicked(it)) },
                onEquallyClick = { onIntent(ExpensesListIntent.EquallyClicked) },
                onSliderChange = {
                    onIntent(
                        ExpensesListIntent.AnimalSliderClicked(
                            animal = it.first,
                            newValue = it.second
                        )
                    )
                },
                onValueChange = {
                    onIntent(
                        ExpensesListIntent.AnimalValueChanged(
                            animal = it.first, newValue = it.second
                        )
                    )
                }
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
    animalList: List<AnimalExpensesUi>,
    isEntry: Boolean,
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
        isEntry = isEntry,
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
    animalList: List<AnimalExpensesUi>,
    isPercent: Boolean,
    priceSuffix: Suffix,
    onAnimalChipClicked: (Long) -> Unit,
    onPercentClicked: (Boolean) -> Unit,
    onEquallyClick: () -> Unit,
    onSliderChange: (Pair<Long, Double>) -> Unit,
    onValueChange: (Pair<Long, String>) -> Unit
) {
    WarningCard(
        colorBackground = blue_3,
        colorBorder = blue_9,
        colorIcon = blue_1,
        colorTitle = black_2,
        colorText = marengo,
        icon = R.drawable.baseline_pets_24,
        title = R.string.expenses_screen_cost_allocation_title,
        text = R.string.expenses_screen_cost_allocation_text
    )
    ChoiceAnimalCard(
        animalList = animalList,
        onAnimalChipClick = onAnimalChipClicked
    )
    if (animalList.any { it.ps }) {
        ChoiceMode(
            isTemplatesPlan = isPercent,
            onValueChange = onPercentClicked,
            onEquallyClick = onEquallyClick
        )
        AnimalFinanceList(
            isPercent = isPercent,
            priceSuffix = priceSuffix,
            animalList = animalList.filter { it.ps },
            onValueChange = onValueChange,
            onSliderChange = onSliderChange
        )
    }
}


@Composable
private fun ChoiceAnimalCard(
    animalList: List<AnimalExpensesUi>,
    onAnimalChipClick: (Long) -> Unit,
) {
    BorderCard {
        Text(
            stringResource(R.string.expenses_screen_choice_animals),
            style = text_16,
            color = dark
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            animalList.forEach { animal ->
                val leadingIcon: @Composable (() -> Unit)? = if (animal.ps) {
                    {
                        Icon(
                            painterResource(R.drawable.icon_check),
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null

                val borderColor = if (animal.ps) blue_4 else gray_8

                FilterChip(
                    selected = animal.ps,
                    onClick = {
                        onAnimalChipClick(animal.id)
                    },
                    shape = RoundedCornerShape(99.dp),
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(animal.name, style = text_14, color = marengo)
                            TextMiniCard(
                                value = animal.type,
                                textColor = white,
                                backgroundColor = blue_1
                            )
                        }
                    },
                    leadingIcon = leadingIcon,
                    colors = FilterChipDefaults.filterChipColors().copy(
                        containerColor = ghostly_white,
                        labelColor = dark,
                        selectedContainerColor = blue_3,
                        selectedLabelColor = blue_8,
                        selectedLeadingIconColor = blue_8,
                    ),
                    border = BorderStroke(
                        1.dp, color = borderColor
                    )
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
    priceSuffix: Suffix,
    animalList: List<AnimalExpensesUi>,
    onSliderChange: (Pair<Long, Double>) -> Unit,
    onValueChange: (Pair<Long, String>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        animalList.forEach { animal ->
            AnimalFinanceCard(
                isPercent = isPercent,
                name = animal.name,
                type = animal.type,
                price = animal.price,
                priceSuffix = priceSuffix,
                percent = animal.presentException,
                isError = animal.error.isErrorPrice,
                onValueChange = { onValueChange(animal.id to it) },
                onSliderChange = { onSliderChange(animal.id to it) }
            )
        }
    }
}

@Composable
private fun AnimalFinanceCard(
    isPercent: Boolean,
    name: String,
    type: String,
    price: Double,
    priceSuffix: Suffix,
    percent: Double,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onSliderChange: (Double) -> Unit
) {
    BorderCard(
        padding = PaddingValues(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, style = text_16, color = black_2)
                TextMiniCard(
                    value = type,
                    textColor = white,
                    backgroundColor = violet_1
                )
            }
            if (isPercent)
                SliderFunction(
                    price = price,
                    priceSuffix = priceSuffix,
                    percent = percent,
                    onSliderChanged = onSliderChange
                )
            else
                ValueFunction(
                    price = price.formatNumber(),
                    suffix = Suffix.RUBLE,
                    percent = percent,
                    isError = isError,
                    onValueChange = onValueChange
                )
        }
    }
}


@Composable
private fun ValueFunction(
    price: String,
    suffix: Suffix,
    percent: Double,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    OutlinedNumberNew(
        value = price,
        onValueChange = onValueChange,
        suffix = suffix,
        intRes = R.string.expenses_screen_sum,
        isError = isError,
        isBorderCard = false
    )
    SupportFinanceCard(
        titleRes = R.string.expenses_screen_percent,
        textColor = blue_8,
        value = "${percent.formatNumber()}%",
        valueColor = blue_14,
        containerColor = blue_3,
        borderColor = blue_9
    )
}

@Composable
private fun SliderFunction(
    price: Double,
    priceSuffix: Suffix,
    percent: Double,
    onSliderChanged: (Double) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
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
            onSliderChanged(newValue.toDouble())
        },
        colors = SliderDefaults.colors(
            thumbColor = blue_4,
            activeTrackColor = blue_4,
            activeTickColor = blue_4,
            inactiveTrackColor = blue_9,
            inactiveTickColor = blue_9
        ),
        valueRange = 0f..100f,
        steps = 99,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
    SupportFinanceCard(
        titleRes = R.string.expenses_screen_sum,
        textColor = green_9,
        value = "${price.formatNumber()} ${stringResource(priceSuffix.toResId())}",
        valueColor = green_13,
        containerColor = price_green_2,
        borderColor = green_11
    )
}

@Composable
private fun SupportFinanceCard(
    @StringRes titleRes: Int,
    textColor: Color,
    value: String,
    valueColor: Color,
    containerColor: Color,
    borderColor: Color
) {
    BorderCard(
        containerColor = containerColor,
        borderColor = borderColor,
        padding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(titleRes),
                style = text_14,
                color = textColor
            )
            Text(
                value,
                style = text_16,
                color = valueColor
            )
        }
    }
}

@Composable
private fun AnimalList(
    modifier: Modifier,
    isEntry: Boolean,
    animalList: List<AnimalExpensesUi>,
    onAnimalClick: (Long) -> Unit
) {
    var isShowAnimalList by rememberSaveable { mutableStateOf(!isEntry) }
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
                            type = animal.type,
                            feedFood = animal.foodDay,
                            feedFoodSuffix = animal.foodDaySuffix,
                            countAnimal = animal.countAnimal,
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
    onClick: () -> Unit
) {
    val suffix = stringResource(feedFoodSuffix.toResId())
    val totalFeedFood = (feedFood * countAnimal).formatNumber()
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomCheckbox(checked = isChoice, onCheckedChange = { onClick() })
                Icon(
                    painter = painterResource(R.drawable.baseline_pets_24),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
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

/*
@Composable
private fun AdditionalSettings2(
    state: ExpensesEntryState2,
    onIntent: (ExpensesListIntent) -> Unit
) {
    var details by rememberSaveable { mutableStateOf(true) }
    if (!state.count.isBlank() && !state.price.isBlank() && !state.isIndicatorsValue) {
        */
/*  TextButtonWarehouse(
              boolean = details,
              onClick = { details = !details },
              intRes = R.string.card_extra_set
          )*//*

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
*/
/*

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
    animalListState: List<AnimalExpensesUi>,
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
//                        onAnimalChipClick(animal)
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
                state.feedFood.toFormatNumber(),
                state.feedFoodSuffix,
            )
        )
        Text(
            text = stringResource(
                R.string.expenses_entry_screen_animal_count
            ).format(
                state.countAnimalFood.toFormatNumber(),
                stringResource(R.string.suffix_pieces)
            )
        )
    }
}*/

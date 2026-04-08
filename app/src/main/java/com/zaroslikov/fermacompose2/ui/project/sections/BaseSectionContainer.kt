@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_3
import com.zaroslikov.fermacompose2.animal_4
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData2
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelNew
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.DetailBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen.Food
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_8
import com.zaroslikov.fermacompose2.violet_9
import com.zaroslikov.fermacompose2.white

@Composable
fun <T, B> InventoryBody(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<T>,
    searchList: List<T>,
    brieflyList: List<B>,
    // карточки передаются как composable-функции
    detailCard: @Composable (Int, T) -> Unit,
    brieflyCard: @Composable (B) -> Unit,
    // ресурсы строк для пустого состояния
    isArchive: Boolean,
    detailEmptyState: EmptyState,
    brieflyEmptyState: EmptyState? = null,
    iconColor: Color,
    backgroundColor: Color,
    isBorderCard: Boolean = true
) {
    val currentListIsNotEmpty = if (details) {
        itemList.isNotEmpty()
    } else {
        brieflyList.isNotEmpty()
    }

    if (currentListIsNotEmpty) {
        if (searchList.isNotEmpty())
            InventoryList(
                modifier = modifier,
                details = details,
                itemList = searchList,
                brieflyList = brieflyList,
                detailCard = detailCard,
                brieflyCard = brieflyCard,
            )
        else MessageNoData2(
            modifier = modifier,
            titleRes = R.string.search_section_search_nothing,
            messageRes = R.string.search_section_search_nothing_sup,
            supportSecondText = R.string.search_section_search_nothing_sup_2,
            iconRes = R.drawable.icon_search_off,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
        )
    } else {
        val (titleRes, messageRes, support, icon) =
            if (details) detailEmptyState else brieflyEmptyState ?: detailEmptyState

        val supportSecondTextArchive =
            if (isArchive) R.string.message_no_data_message_archive_project else support

        MessageNoData2(
            modifier = modifier,
            titleRes = titleRes,
            messageRes = messageRes,
            supportSecondText = supportSecondTextArchive,
            iconRes = icon,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
            isBorderCard = isBorderCard
        )
    }
}

data class EmptyState(
    @StringRes val title: Int,
    @StringRes val message: Int,
    @StringRes val support: Int? = null,
    @DrawableRes val icon: Int
)

@Composable
private fun <T, B> InventoryList(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<T>,
    brieflyList: List<B>,
    detailCard: @Composable (Int, T) -> Unit,
    brieflyCard: @Composable (B) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (details)
            itemsIndexed(items = itemList) { index, item -> detailCard(index, item) }
        else
            items(items = brieflyList) { item -> brieflyCard(item) }
    }
}

@Composable
fun <T> BrieflyBottomSheetUniversal(
    list: List<T>,
    title: String,
    price: Pair<Double, Suffix>?,
    weight: ValueItem?,
    linear: ValueItem?,
    volume: ValueItem?,
    pieces: ValueItem?,
    @DrawableRes iconRes: Int,
    onDismissRequest: () -> Unit,
    onAnalysisClick: ((Suffix) -> Unit)? = null,
    itemCard: @Composable ((T) -> Unit),
) {
    BaseBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
        isScroll = false
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val valuesCount = listOfNotNull(weight, volume, linear, pieces)
                valuesCount.forEach { valueItem ->
                    Card2(
                        iconRes, valueItem.value, valueItem.price,
                        onAnalysisClick = if (valuesCount.size >= 2 && onAnalysisClick != null) {
                            { onAnalysisClick(valueItem.value.second) }
                        } else null)
                    if (valuesCount.size == 1)
                        onAnalysisClick?.let { AnalysisButton { it(valueItem.value.second) } }
                }
                if (valuesCount.size >= 2) {
                    price?.let {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = grey_2
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(R.string.briefly_detail_card_total_price),
                                style = text_14,
                                color = marengo
                            )
                            Text(
                                "${it.first.formatNumber()} ${stringResource(it.second.toResId())}",
                                color = price_green,
                                style = text_20
                            )
                        }
                    }
                }

            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { item ->
                    itemCard(item)
                }
            }
        }
    }
}


@Composable
private fun Card2(
    @DrawableRes iconRes: Int,
    value: Pair<Double, Suffix>,
    price: Pair<Double, Suffix>? = null,
    onAnalysisClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = ghostly_white),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    icon = iconRes,
                    boxColor = value.second.toColorList(),
                    iconColor = white
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "${value.first.formatNumber()} ${stringResource(value.second.toResId())}",
                        style = text_16,
                        color = black_2
                    )
                    onAnalysisClick?.let {
                        Text(
                            stringResource(R.string.briefly_detail_card_analysis),
                            style = text_14,
                            color = price_green
                        )
                    }
                }
            }
            price?.let {
                Text(
                    "${price.first.formatNumber()} ${stringResource(price.second.toResId())}",
                    color = price_green,
                    style = text_16
                )
            }
            onAnalysisClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_chevron_right_24),
                        contentDescription = null,
                        tint = grey,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisButton(onAnalysisClick: () -> Unit) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.briefly_detail_card_analysis),
        onClick = onAnalysisClick,
        colors = listOf(price_green, green_shamrock),
        iconRes = R.drawable.baseline_analytics_24,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}

@Composable
fun EntryIndicationBottomSheet(
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    isEntry: Boolean,
    enabledButton: Boolean,
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onSecondDismissRequest: () -> Unit = {},
    onInsertClick: () -> Unit,
    onUpdateClick: () -> Unit,
    scrollableContent: @Composable ColumnScope.() -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(titleRes),
        supText = if (isEntry) null else stringResource(R.string.animal_indicators_mode_edit),
        iconRes = iconRes,
        colors = colors,
        onDismissRequest = onDismissRequest,
        onSecondDismissRequest = onSecondDismissRequest,
        contentBottom = {
            ButtonPanelNew(
                modifier = Modifier
                    .fillMaxWidth(),
                isEntry = isEntry,
                enable = enabledButton,
                colors = colors,
                onClickInsert = onInsertClick,
                onClickUpdate = onUpdateClick,
                onClickClose = onDismissRequest
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            scrollableContent()
        }
    }
}


@Composable
fun DetailSectionBottomSheet(
    title: String,
    count: Double,
    countSuffix: Suffix,
    price: Double? = null,
    priceAll: Double? = null,
    priceSuffix: Suffix = Suffix.RUBLE,
    category: String,
    buyer: String? = null,
    date: String,
    animal: String? = null,
    note: String,
    statusWriteOff: Boolean? = null,
    iconColor: Color,
    boxColor: Color,
    colors: List<Color>,
    food: Food? = null,
    animalId: Long? = null,
    animalVaccinationId: Long? = null,
    animalCountId: Long? = null,
    isArchive: Boolean,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    DetailBottomSheet(
        title = title,
        colors = colors,
        enabledButton = !(animalVaccinationId != null || animalCountId != null),
        onUpdateClick = onUpdateClick,
        onDeleteClick = onDeleteClick,
        isArchive = isArchive,
        onDismissRequest = onDismissRequest
    ) {
        if (animalVaccinationId != null)
            VaccinationWarningCard()
        if (animalCountId != null)
            AnimalWarningCard()
        if (animalId != null)
            KillAnimalWarningCard()

        statusWriteOff?.let { status ->
            val (iconRes, valueString) = if (status)
                R.drawable.baseline_cottage_24 to R.string.ration_button_own_needs
            else
                R.drawable.baseline_delete_24 to R.string.ration_button_disposal
            ValueStandardCard(
                titleRes = R.string.detail_card_status_write_off,
                value = stringResource(valueString),
                iconRes = iconRes,
                iconColor = iconColor,
                boxColor = boxColor
            )
        }
        ValueStandardCard(
            titleRes = R.string.detail_card_count,
            value = "${count.formatNumber()} ${stringResource(countSuffix.toResId())}",
            iconRes = R.drawable.baseline_spoke_24,
            iconColor = iconColor,
            boxColor = boxColor
        )
        if (price != null) {
            val priceSuffixString = stringResource(priceSuffix.toResId())
            val priceSting = price.formatNumber()
            val priceOne = "$priceSting $priceSuffixString"
            val value =
                if (priceAll == null) priceOne else
                    "$priceOne x ${count.formatNumber()} ${stringResource(countSuffix.toResId())} " +
                            "= ${priceAll.formatNumber()} $priceSuffixString"
            ValueStandardCard(
                titleRes = R.string.detail_card_price,
                value = value,
                iconRes = R.drawable.icon_money,
                iconColor = iconColor,
                boxColor = boxColor
            )
        }
        ValueStandardCard(
            titleRes = R.string.detail_card_category,
            value = category,
            iconRes = R.drawable.icon_list,
            iconColor = iconColor,
            boxColor = boxColor
        )
        if (buyer != null)
            ValueStandardCard(
                titleRes = R.string.detail_card_buyer,
                value = buyer,
                iconRes = R.drawable.baseline_person_24,
                iconColor = iconColor,
                boxColor = boxColor
            )
        ValueStandardCard(
            titleRes = R.string.detail_card_date,
            value = date,
            iconRes = R.drawable.baseline_calendar_month_24,
            iconColor = iconColor,
            boxColor = boxColor
        )
        if (animal != null)
            ValueStandardCard(
                titleRes = R.string.detail_card_animal,
                value = animal,
                iconRes = R.drawable.baseline_pets_24,
                iconColor = iconColor,
                boxColor = boxColor
            )
        if (food != null)
            ValueFood(
                boxColor,
                iconColor,
                food.feedFood,
                food.feedFoodSuffix,
                food.daysEnd,
                food.weightAll,
                food.weightSuffix,
                food.percentFloat,
                food.remainingFood,
                food.animalList
            )
        if (note.isNotBlank())
            ValueStandardCard(
                titleRes = R.string.detail_card_note,
                value = note,
                iconRes = R.drawable.baseline_sticky_note_2_24,
                iconColor = iconColor,
                boxColor = boxColor
            )
    }
}

@Composable
private fun VaccinationWarningCard() {
    WarningCard(
        colorBackground = violet_3,
        colorBorder = violet_5,
        colorIcon = violet_1,
        colorIconBackground = violet_8,
        colorTitle = violet_6,
        colorText = violet_9,
        icon = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
        title = R.string.detail_card_animal_vaccination,
        text = R.string.detail_card_animal_vaccination_text
    )
}

@Composable
private fun AnimalWarningCard() {
    WarningCard(
        colorBackground = green_15,
        colorBorder = green_g_4,
        colorIcon = animal_1,
        colorIconBackground = animal_3,
        colorTitle = animal_4,
        colorText = animal_1,
        icon = R.drawable.baseline_pets_24,
        title = R.string.detail_card_animal_warning,
        text = R.string.detail_card_animal_warning_text
    )
}

@Composable
private fun KillAnimalWarningCard() {
    WarningCard(
        colorBackground = red_11,
        colorBorder = red_15,
        colorIcon = error_base,
        colorIconBackground = red_12,
        colorTitle = orang_17,
        colorText = red_14,
        icon = R.drawable.baseline_pets_24,//
        title = R.string.detail_card_animal_kill_warning,
        text = R.string.detail_card_animal_kill_warning_text
    )
}

@Composable
private fun ValueFood(
    boxColor: Color, iconColor: Color,
    feedFood: Double,
    feedFoodSuffix: Suffix,
    daysEnd: Int,
    weightAll: Double,
    weightSuffix: Suffix,
    percentFloat: Float,
    remainingFood: Double,
    animalList: List<AnimalExpensesDomain>
) {
    BorderCard(
        borderColor = grey_2,
        containerColor = white,
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(17.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTransaction2(
                icon = R.drawable.outline_work_24,
                boxColor = boxColor,
                iconColor = iconColor
            )
            Text(stringResource(R.string.detail_card_food), color = gray_7, style = text_12)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.detail_card_food_animals),
                style = text_14,
                color = marengo
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                animalList.forEach {
                    AnimalFoodCard(
                        name = it.name,
                        type = it.type,
                        value = it.foodDay,
                        valueSuffix = it.foodDaySuffix,
                        countAnimal = it.countAnimal
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.detail_card_food_indicators),
                style = text_14,
                color = marengo
            )
            TwoBorderValueCard(
                firstTitleRes = R.string.detail_card_weight,
                firstValue = "${weightAll.formatNumber()} ${stringResource(weightSuffix.toResId())}",
                firstIconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                firstIconColor = blue_1,
                secondIconColor = blue_1,
                secondIconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                secondValue = "${remainingFood.formatNumber()} ${stringResource(weightSuffix.toResId())}",
                secondTitleRes = R.string.detail_card_rest_weight
            )
            TwoBorderValueCard(
                firstTitleRes = R.string.detail_card_food_feel,
                firstValue = "${feedFood.formatNumber()} ${stringResource(feedFoodSuffix.toResId())}",
                firstIconRes = R.drawable.baseline_shopping_basket_24,
                firstIconColor = green_shamrock,

                secondIconColor = violet_1,
                secondIconRes = R.drawable.baseline_access_time_24,
                secondValue = "${daysEnd.formatNumber()} ${stringResource(R.string.expenses_screen_days)}",
                secondTitleRes = R.string.detail_card_day_end
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.detail_card_rest_food),
                    style = text_14,
                    color = marengo
                )
                Text(
                    "${(percentFloat * 100).toDouble().formatNumber()} %",
                    style = text_14,
                    color = black_2
                )
            }
            BaseSlider(
                modifier = Modifier,
                percentFloat = percentFloat,
                color = when {
                    percentFloat > 0.45f -> green_6
                    percentFloat < 0.45f && percentFloat > 0.25f -> orang_9
                    else -> error_base
                }
            )
        }
    }
}

@Composable
private fun AnimalFoodCard(
    modifier: Modifier = Modifier,
    name: String,
    value: Double,
    valueSuffix: Suffix,
    countAnimal: Int,
    type: String
) {
    val totalCount = value * countAnimal
    val suffix = stringResource(valueSuffix.toResId())
    BorderCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.baseline_pets_24), contentDescription = null,
                tint = black_2, modifier = Modifier.size(16.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("$name • $type", style = text_12, color = black_2)
                Text(
                    "${value.formatNumber()} $suffix x $countAnimal = ${totalCount.formatNumber()} $suffix",
                    style = text_16,
                    color = marengo
                )
            }
        }
    }
}


@Composable
private fun TwoBorderValueCard(
    firstTitleRes: Int,
    firstValue: String,
    firstIconRes: Int,
    firstIconColor: Color,
    secondIconColor: Color,
    secondIconRes: Int,
    secondValue: String,
    secondTitleRes: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BorderValueCard(
            modifier = Modifier.weight(1f),
            titleRes = firstTitleRes,
            value = firstValue,
            iconRes = firstIconRes,
            iconColor = firstIconColor
        )
        BorderValueCard(
            modifier = Modifier.weight(1f),
            titleRes = secondTitleRes,
            value = secondValue,
            iconRes = secondIconRes,
            iconColor = secondIconColor
        )
    }
}


@Composable
private fun BorderValueCard(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int, value: String,
    @DrawableRes iconRes: Int, iconColor: Color
) {
    BorderCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(iconRes), contentDescription = null,
                tint = iconColor, modifier = Modifier.size(16.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(stringResource(titleRes), style = text_12, color = marengo)
                Text(value, style = text_16, color = black_2)
            }
        }
    }
}

@Composable
private fun ValueCard(
    @StringRes titleRes: Int,
    textColor: Color,
    value: String,
    valueColor: Color,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    borderColor: Color, containerColor: Color
) {
    BorderCard(
        borderColor = borderColor,
        containerColor = containerColor,
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(17.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTransaction2(
                icon = iconRes,
                boxColor = white,
                iconColor = iconColor
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(stringResource(titleRes), color = textColor, style = text_12)
                Text(value, color = valueColor, style = text_16)
            }
        }
    }
}

@Composable
private fun ValueStandardCard(
    @StringRes titleRes: Int,
    value: String,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    boxColor: Color
) {
    BorderCard(
        borderColor = grey_2,
        containerColor = white,
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(17.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTransaction2(
                icon = iconRes,
                boxColor = boxColor,
                iconColor = iconColor
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(stringResource(titleRes), color = gray_7, style = text_12)
                Text(value, color = black_2, style = text_16)
            }
        }
    }
}


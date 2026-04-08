@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_5
import com.zaroslikov.fermacompose2.blue_6
import com.zaroslikov.fermacompose2.blue_7
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.red_1
import com.zaroslikov.fermacompose2.red_2
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.ProductKillInfoCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelDetailNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelNew
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.ProductKill
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6

@Composable
fun <T> InventoryAnimalBody(
    modifier: Modifier = Modifier,
    isVaccination: Boolean = false,
    itemList: List<T>,
    @StringRes titleRes2: Int,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    backgroundColor: Color,
    detailCard: @Composable (item: T) -> Unit,
    isArchive: Boolean
) {
    if (itemList.isNotEmpty())
        InventoryList(
            modifier = modifier,
            titleRes2 = titleRes2,
            itemList = itemList,
            isVaccination = isVaccination,
            detailCard = detailCard,
        )
    else {
        val supportSecondTextArchive =
            if (isArchive) R.string.message_no_data_message_archive else null
        EmptyBookmark(
            iconRes = iconRes,
            title = titleRes,
            supportText = messageRes,
            supportSecondText = supportSecondTextArchive,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
            plusColor = iconColor
        )
    }
}


@Composable
private fun <T> InventoryList(
    modifier: Modifier = Modifier,
    @StringRes titleRes2: Int,
    isVaccination: Boolean = false,
    itemList: List<T>,
    detailCard: @Composable (item: T) -> Unit
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeadingIndicators(titleRes2, isVaccination = isVaccination)
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = itemList,
                key = { index, _ -> index }) { index, item ->
                detailCard(item)
            }
        }
    }
}

@Composable
fun EntryBottomSheet(
    @StringRes titleEntryRes: Int,
    @StringRes titleEditRes: Int,
    isEntry: Boolean,
    enabledButton: Boolean,
    colors: List<Color>,
    onInsertClick: () -> Unit,
    onUpdateClick: () -> Unit = {},
    onDismissRequest: () -> Unit,
    onSecondDismissRequest: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val titleRes = if (isEntry) titleEntryRes else titleEditRes
    BaseBottomSheet(
        title = stringResource(titleRes),
        onDismissRequest = onDismissRequest,
        onSecondDismissRequest = onSecondDismissRequest,
        contentBottom = {
            ButtonPanelNew(
                modifier = Modifier.fillMaxWidth(),
                isEntry = isEntry,
                enable = enabledButton,
                colors = colors,
                onClickInsert = onInsertClick,
                onClickUpdate = onUpdateClick,
                onClickClose = onSecondDismissRequest
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun DetailBottomSheet(
    title: String,
    colors: List<Color>,
    enabledButton: Boolean = true,
    isArchive: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    BaseBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
        contentBottom = if (isArchive) null else {
            {
                ButtonPanelDetailNew(
                    modifier = Modifier.fillMaxWidth(),
                    colors = colors,
                    enabled = enabledButton,
                    onClickUpdate = onUpdateClick,
                    onClickDelete = onDeleteClick
                )
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}


@Composable
fun AnimalIndicatorsCardBase(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    date: String,
    vaccinationDate: String? = null,
    nextVaccinationDate: String? = null,
    isArchive: Boolean,
    noteContainer: @Composable (ColumnScope.() -> Unit)? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    CardFieldNew(
        modifier = modifier,
        padding = PaddingValues(),
        onClick = onDetailClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconIndicatorsAnimal(
                        icon = icon,
                        colors = colors
                    )
                    Text(
                        text = value,
                        style = textBold_16,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = date,
                        style = textBold_16,
                        textAlign = TextAlign.Center
                    )
                    if (!isArchive)
                        DropdownMenuEdit(
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick
                        )
                }
            }
            vaccinationDate?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CardVaccinationDate(true, it)
                    nextVaccinationDate?.let { nextVaccinationDate ->
                        CardVaccinationDate(false, nextVaccinationDate)
                    }
                }
            }
        }
        noteContainer?.invoke(this)
    }
}


@Composable
fun AnimalIndicatorsCardNew(
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    suffix: Suffix,
    date: String,
    note: String,
    totalValues: String,
    indicationStatus: IndicationStatus,
    isArchive: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var details by rememberSaveable() { mutableStateOf(false) }
    AnimalIndicatorsCardBase(
        modifier = Modifier,
        icon = icon,
        colors = colors,
        value = "$value ${stringResource(suffix.toResId())}",
        date = date,
        isArchive = isArchive,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailClick = { details = !details },
        noteContainer = {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = details
            ) {
                BaseDetailsAnimalIndication(
                    value = totalValues,
                    suffix = suffix,
                    status = indicationStatus,
                    note = note
                )
            }
        }
    )
}

@Composable
fun AnimalCountCardNew(
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    price: Double? = null,
    suffix: Suffix,
    date: String,
    note: String,
    isArchive: Boolean,
    productKill: List<ProductKill>,
    indicationStatus: IndicationStatus,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var details by rememberSaveable { mutableStateOf(false) }
    AnimalIndicatorsCardBase(
        icon = icon,
        colors = colors,
        value = "$value ${stringResource(suffix.toResId())}",
        date = date,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailClick = { details = !details },
        isArchive = isArchive,
        noteContainer = {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = details
            ) {
                BaseDetailsAnimalIndication(
                    value = value,
                    suffix = suffix,
                    price = price?.formatNumber(),
                    status = indicationStatus,
                    note = note,
                    productKill = productKill
                )
            }
        }
    )
}

@Composable
fun AnimalVaccinationCardNew(
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    count: String,
    price: Double?,
    suffix: Suffix,
    date: String,
    nextDate: String?,
    note: String,
    isArchive: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var details by rememberSaveable { mutableStateOf(false) }

    AnimalIndicatorsCardBase(
        icon = icon,
        colors = colors,
        value = value,
        date = "$count ${stringResource(suffix.toResId())}",
        vaccinationDate = date,
        nextVaccinationDate = nextDate,
        isArchive = isArchive,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailClick = { details = !details },
        noteContainer = {
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = details
            ) {
                BaseDetailsAnimalIndication(
                    value = price?.formatNumber(),
                    suffix = suffix,
                    status = IndicationStatus.PRICE,
                    note = note,
                )
            }
        }
    )
}

@Composable
fun BaseDetailsAnimalIndication(
    value: String?,
    suffix: Suffix,
    price: String? = null,
    note: String,
    status: IndicationStatus,
    productKill: List<ProductKill> = emptyList()
) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = gray_6
    )
    Column(
        modifier = Modifier
            .background(color = ghostly_white)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        value?.let {
            when (status) {
                IndicationStatus.EXPENSES, IndicationStatus.SALE, IndicationStatus.WRITE_OFF -> {
                    CardIndicationChangeChoice(value, suffix, status)
                    price?.let {
                        CardIndicationChangeChoice(
                            price,
                            Suffix.RUBLE,
                            IndicationStatus.PRICE
                        )
                    }
                }

                IndicationStatus.KILL -> {
                    CardIndicationChangeChoice(value, suffix, status)
                    if (productKill.isNotEmpty()) {
                        Text(
                            stringResource(R.string.animal_card_screen_kill_add_product),
                            style = text_14,
                            color = marengo
                        )
                        productKill.forEachIndexed { index, productKill ->
                            ProductKillInfoCard(
                                number = index + 1,
                                name = productKill.title,
                                value = productKill.countProduct,
                                suffix = productKill.suffixProduct
                            )
                        }
                    }
                }

                else -> CardIndicationChangeChoice(value = value, suffix = suffix, status = status)
            }
        }
        if (note.isNotEmpty() && value != null)
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = gray_6
            )
        if (note.isNotEmpty())
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.animal_indicators_note),
                    style = text_14,
                    color = marengo
                )
                Text(
                    text = note,
                    color = dark,
                    style = text_14,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
            }
    }
}

@Composable
fun HeadingIndicators(
    @StringRes titleRes: Int,
    isVaccination: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titleRes),
            style = textBold_18,
            textAlign = TextAlign.Center,
            color = marengo
        )
        Text(
            text = stringResource(if (isVaccination) R.string.count_screen_title else R.string.outlined_text_date),
            style = textBold_18,
            textAlign = TextAlign.Center,
            color = marengo
        )
    }
}

@Composable
fun CardIndicationChangeChoice(
    value: String,
    suffix: Suffix,
    status: IndicationStatus
) {
    val colors = when (status) {
        IndicationStatus.POSITIVE, IndicationStatus.PRICE, IndicationStatus.ADD, IndicationStatus.ALL_WEIGHT ->
            Triple(green_g_2, green_1, green_2)

        IndicationStatus.NEUTRAL -> Triple(grey_2, grey, grey_3)
        IndicationStatus.NEGATIVE, IndicationStatus.KILL -> Triple(red_3, red_2, red_1)

        IndicationStatus.WRITE_OFF -> Triple(violet_3, violet_5, violet_1)
        IndicationStatus.SALE -> Triple(blue_3, blue_9, blue_8)
        IndicationStatus.EXPENSES -> Triple(orang_4, orang_5, orang_6)
    }

    val string =
        when (status) {
            IndicationStatus.POSITIVE -> stringResource(R.string.animal_indicators_change_increased)
                .format(value, stringResource(suffix.toResId()))

            IndicationStatus.NEGATIVE -> stringResource(R.string.animal_indicators_change_decreased)
                .format(value, stringResource(suffix.toResId()))

            IndicationStatus.NEUTRAL -> stringResource(R.string.animal_indicators_size_not_changed_s)
                .format(value, stringResource(suffix.toResId()))

            else -> "$value ${stringResource(suffix.toResId())}"
        }
    val titleRes = stringResource(
        when (status) {
            IndicationStatus.PRICE -> R.string.support_text_all_price
            IndicationStatus.ADD -> R.string.animal_count_screen_add_animal
            IndicationStatus.WRITE_OFF -> R.string.animal_count_screen_write_off_animal
            IndicationStatus.SALE -> R.string.animal_count_screen_sale_animal
            IndicationStatus.KILL -> R.string.animal_count_screen_kill_animal
            IndicationStatus.ALL_WEIGHT -> R.string.animal_card_screen_animal_card_info_weight_all
            IndicationStatus.EXPENSES -> R.string.animal_count_screen_expenses_animal
            else -> R.string.animal_indicators_changed
        }
    )
    CardIndicationChange(titleRes, string, colors)
}

@Composable
fun CardIndicationChange(
    titleRes: String,
    string: String,
    colors: Triple<Color, Color, Color>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titleRes,
            style = text_14,
            color = marengo
        )
        CardClips(
            colorBackground = colors.first,
            colorBorder = colors.second,
            colorText = colors.third,
            value = string,
        )
    }
}

@Composable
fun CardVaccinationDate(
    currentDate: Boolean,
    date: String
) {
    val colors = when (currentDate) {
        true -> Triple(violet_3, violet_5, violet_6)
        false -> Triple(blue_5, blue_6, blue_7)
    }

    val string = stringResource(
        when (currentDate) {
            true -> R.string.card_date
            false -> R.string.animal_vaccination_next_date
        }
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            string,
            style = text_12,
            color = gray_7
        )
        CardClips(
            colorBackground = colors.first,
            colorBorder = colors.second,
            colorText = colors.third,
            value = date,
        )
    }
}
package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.toSuffixList
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconDone
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.NoteColorCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.ExposedDropdownMenuEnum
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedSwitch
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet

private val recyclingGradient = listOf(green_shamrock, price_green)

/** «Тип списания» с переработкой; в шаблонах и при правке остаётся прежний двухпозиционный список. */
@Composable
fun OutlinedWriteOffType(
    type: WriteOffType,
    onValueChange: (WriteOffType) -> Unit
) {
    val focusManager = LocalFocusManager.current
    BorderCard {
        ExposedDropdownMenuEnum(
            valueList = WriteOffType.entries,
            dropdownMenuItem = { _, item, closeMenu ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painterResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = item.color
                        )
                    },
                    text = { Text(text = stringResource(item.titleRes)) },
                    trailingIcon = if (item == type) {
                        { IconDone() }
                    } else null,
                    onClick = {
                        onValueChange(item)
                        closeMenu()
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            }
        ) {
            BaseOutlinedTextNew(
                value = stringResource(type.titleRes),
                onValueChange = { },
                leadingIconRes2 = type.iconRes,
                leadingIconColor2 = type.color,
                labelIntRes = R.string.outlined_text_status_write_off,
                intResSup = R.string.support_text_write_off_type,
                readOnly = true,
                modifier = it.first,
                isMore = true,
                keyboardOptions = keyboardOptionsNext()
            )
        }
    }
}

/** Поля формы переработки: сырьё, готовая продукция, даты. Примечание добавляет форма списания. */
@Composable
fun RecyclingEntryFields(
    form: RecyclingForm,
    date: String,
    rawTitles: List<DomainTitleSuffixCategory>,
    productTitles: List<TitleAndSuffixDomain>,
    isErrorReadyDate: Boolean,
    onDateChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (Long) -> Unit,
    onIngredientChoice: (Long, DomainTitleSuffixCategory) -> Unit,
    onIngredientCountChange: (Long, String) -> Unit,
    onTitleChange: (String) -> Unit,
    onTitleChoice: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onDelayedChange: (Boolean) -> Unit,
    onReadyDateChange: (String) -> Unit,
) {
    InfoRecyclingCard(stringResource(R.string.recycling_info))

    SectionTitle(R.string.recycling_section_raw, R.string.recycling_section_raw_sup)
    form.ingredients.forEachIndexed { index, ingredient ->
        key(ingredient.key) {
            IngredientCard(
                number = index + 1,
                ingredient = ingredient,
                titles = rawTitles,
                canRemove = form.ingredients.size > 1,
                onRemove = { onRemoveIngredient(ingredient.key) },
                onChoice = { onIngredientChoice(ingredient.key, it) },
                onCountChange = { onIngredientCountChange(ingredient.key, it) }
            )
        }
    }
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = green_g_4,
        onClick = onAddIngredient
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconAndTextNew(
                iconRes = R.drawable.baseline_add_circle_outline_24,
                valueString = stringResource(R.string.recycling_add_raw),
                iconColor = green_shamrock,
                textColor = green_shamrock,
                iconSize = 20.dp
            )
        }
    }

    SectionTitle(R.string.recycling_section_product, R.string.recycling_section_product_sup)
    BorderCard {
        OutlinedTextTitleAddNew(
            value = form.title,
            onValueChange = onTitleChange,
            onValueChangeSuffix = onTitleChoice,
            titleList = productTitles,
            isErrorTitle = false,
            isErrorSlash = form.title.isSlash(),
            drawableRes = R.drawable.icon_add_product,
            intResSup = R.string.support_text_recycling_product,
            isBorderCard = false
        )
        OutlinedTextCountNew(
            value = form.count,
            onValueChange = onCountChange,
            suffix = form.countSuffix,
            onSuffixChange = onSuffixChange,
            isError = form.count.isNotEmpty() && form.count.toConvertZeroDouble() <= 0.0,
            intResSup = R.string.support_text_recycling_count,
            isBorderCard = false
        )
    }

    OutlinedTextDateNew(
        value = date,
        onValueChange = onDateChange,
        intRes = R.string.outlined_text_recycling_date,
        intResSup = R.string.support_text_recycling_date
    )
    BorderCard {
        OutlinedSwitch(
            checked = form.isDelayed,
            onCheckedChange = onDelayedChange,
            isBorderCard = false,
            leadingIconRes = R.drawable.baseline_access_time_24,
            labelIntRes = R.string.recycling_delayed,
            supportingText = R.string.recycling_delayed_sup
        )
        if (form.isDelayed) {
            OutlinedTextDateNew(
                value = form.readyDate,
                onValueChange = onReadyDateChange,
                intRes = R.string.outlined_text_recycling_ready_date,
                intResSup = R.string.support_text_recycling_ready_date,
                minDate = date,
                isBorderCard = false
            )
            if (isErrorReadyDate)
                Text(
                    text = stringResource(R.string.error_recycling_ready_date),
                    style = text_12,
                    color = error_base
                )
        }
    }
}

@Composable
private fun IngredientCard(
    number: Int,
    ingredient: RecyclingIngredientForm,
    titles: List<DomainTitleSuffixCategory>,
    canRemove: Boolean,
    onRemove: () -> Unit,
    onChoice: (DomainTitleSuffixCategory) -> Unit,
    onCountChange: (String) -> Unit
) {
    BorderCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconAndTextNew(
                iconRes = R.drawable.baseline_autorenew_24,
                valueString = stringResource(R.string.recycling_raw_item, number),
                iconColor = green_shamrock,
                textStyle = textBold_16
            )
            if (canRemove)
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.recycling_remove_raw),
                        tint = gray_7
                    )
                }
        }
        OutlinedTextTitleSaleNew(
            value = ingredient.title,
            onValueChoice = onChoice,
            productOrigin = ingredient.productOrigin,
            titleList = titles,
            intResSup = R.string.support_text_price_write_product,
            isMore = true,
            readOnly = true,
            isBorderCard = false
        )
        OutlinedTextCountNew(
            value = ingredient.count,
            onValueChange = onCountChange,
            suffix = ingredient.countSuffix,
            suffixList = ingredient.countSuffix.toSuffixList(),
            isError = ingredient.count.isNotEmpty() && ingredient.count.toConvertZeroDouble() <= 0.0,
            intResSup = R.string.support_text_recycling_raw_count,
            isBorderCard = false
        )
        if (ingredient.title.isNotBlank() && ingredient.warehouseList.isNotEmpty())
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.support_text_count_warehouse_s),
                    style = text_12,
                    color = gray_7
                )
                ingredient.warehouseList.forEach {
                    CountColorCard(it.count, it.suffix, it.suffix.toColorList())
                }
            }
    }
}

@Composable
private fun SectionTitle(titleRes: Int, supRes: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = stringResource(titleRes), style = textBold_16)
        Text(text = stringResource(supRes), style = text_12, color = gray_7)
    }
}

@Composable
private fun InfoRecyclingCard(text: String) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = green_15,
        borderColor = green_g_4,
        onClick = null
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painterResource(R.drawable.baseline_autorenew_24),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = green_shamrock
            )
            Text(text = text, style = text_14, color = green_shamrock)
        }
    }
}

/** Блок «В переработке» над списком списаний: продукция, которая ещё не поступила на склад. */
@Composable
fun RecyclingInProgressBlock(
    items: List<RecyclingItem>,
    onClick: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconAndTextNew(
                iconRes = R.drawable.baseline_autorenew_24,
                valueString = stringResource(R.string.recycling_header),
                iconColor = green_shamrock,
                textStyle = textBold_16,
                iconSize = 20.dp
            )
            CardClips(
                colorBackground = green_15,
                colorBorder = green_g_4,
                colorText = green_shamrock,
                value = items.size.toString()
            )
        }
        items.forEach { item ->
            RecyclingProgressCard(item = item, onClick = { onClick(item.recycling.id) })
        }
    }
}

@Composable
private fun RecyclingProgressCard(item: RecyclingItem, onClick: () -> Unit) {
    val recycling = item.recycling
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = green_g_4,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconIndicatorsAnimal(icon = R.drawable.baseline_autorenew_24, colors = recyclingGradient)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = recycling.title, style = textBold_16, maxLines = 2)
                    CountColorCard(
                        recycling.count,
                        recycling.countSuffix,
                        recycling.countSuffix.toColorList()
                    )
                }
            }
            item.daysLeft?.let { DaysLeftClip(it) }
        }
        ReadyDateText(item)
        if (item.ingredients.isNotEmpty()) {
            val raw = item.ingredients.map { it.toCountText() }.joinToString()
            IconAndTextNew(
                iconRes = R.drawable.baseline_shopping_basket_24,
                valueString = stringResource(R.string.recycling_raw_summary, raw),
                iconColor = green_shamrock
            )
        }
        BaseSlider(percentFloat = item.progress, color = green_shamrock)
    }
}

@Composable
private fun DaysLeftClip(daysLeft: Long) {
    CardClips(
        colorBackground = orang_9.copy(alpha = 0.12f),
        colorBorder = orang_9,
        colorText = orang_6,
        colorIcon = orang_9,
        icon = R.drawable.baseline_access_time_24,
        value = if (daysLeft == 0L) stringResource(R.string.recycling_ready_today)
        else stringResource(R.string.recycling_days_left, daysLeft.toInt())
    )
}

@Composable
private fun ReadyDateText(item: RecyclingItem) {
    val recycling = item.recycling
    val readyYear = recycling.readyYear ?: return
    IconAndTextNew(
        iconRes = R.drawable.baseline_calendar_month_24,
        valueString = stringResource(
            R.string.recycling_ready_at,
            formatDate(recycling.readyDay ?: 1, recycling.readyMonth ?: 1, readyYear)
        ),
        iconColor = orang_9
    )
}

@Composable
private fun formatDate(day: Int, month: Int, year: Int): String =
    dateBuilder(day, stringResource(monthToResString(month)), year)

@Composable
private fun DomainWriteOffTable.toCountText(): String =
    "$title ${count.formatNumber()} ${stringResource(countSuffix.toResId())}"

/** Детали переработки: статус, продукция, сырьё; «Готово сейчас» и удаление всей переработки. */
@Composable
fun RecyclingDetailBottomSheet(
    item: RecyclingItem,
    isArchive: Boolean,
    onCompleteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val recycling = item.recycling
    BaseBottomSheet(
        title = stringResource(R.string.recycling_detail_title),
        supText = recycling.title,
        iconRes = R.drawable.baseline_autorenew_24,
        colors = recyclingGradient,
        onDismissRequest = onDismissRequest,
        contentBottom = if (isArchive) null else {
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BorderButton(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.baseline_delete_24,
                        iconColor = error_base,
                        text = stringResource(R.string.button_delete),
                        textColor = error_base,
                        onClick = onDeleteClick
                    )
                    if (!recycling.isCompleted)
                        GradientButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.recycling_complete_now),
                            prefixIconRes = R.drawable.icon_check,
                            colors = recyclingGradient,
                            onClick = onCompleteClick
                        )
                }
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RecyclingSummaryCard(item)
            if (!recycling.isCompleted)
                InfoRecyclingCard(stringResource(R.string.recycling_frozen_hint))
            BorderCard {
                Text(text = stringResource(R.string.recycling_detail_raw), style = textBold_16)
                item.ingredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ingredient.title,
                            style = text_14,
                            modifier = Modifier.weight(1f)
                        )
                        CountColorCard(
                            ingredient.count,
                            ingredient.countSuffix,
                            ingredient.countSuffix.toColorList()
                        )
                    }
                }
            }
            BorderCard(modifier = Modifier.fillMaxWidth(), onClick = null) {
                IconAndTextNew(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = "${stringResource(R.string.recycling_detail_date)}: " +
                            formatDate(recycling.day, recycling.month, recycling.year),
                    iconColor = green_shamrock
                )
                recycling.readyYear?.let { year ->
                    val titleRes = if (recycling.isCompleted) R.string.recycling_detail_done_date
                    else R.string.recycling_detail_ready_date
                    IconAndTextNew(
                        iconRes = R.drawable.baseline_access_time_24,
                        valueString = "${stringResource(titleRes)}: " +
                                formatDate(recycling.readyDay ?: 1, recycling.readyMonth ?: 1, year),
                        iconColor = if (recycling.isCompleted) green_shamrock else orang_9
                    )
                }
            }
            if (recycling.note.isNotBlank()) NoteColorCard(recycling.note, green_shamrock)
        }
    }
}

@Composable
private fun RecyclingSummaryCard(item: RecyclingItem) {
    val recycling = item.recycling
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = green_15,
        borderColor = green_g_4,
        onClick = null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.recycling_detail_product),
                    style = text_12,
                    color = gray_7
                )
                Text(text = recycling.title, style = textBold_16)
                CountColorCard(
                    recycling.count,
                    recycling.countSuffix,
                    recycling.countSuffix.toColorList()
                )
            }
            if (recycling.isCompleted)
                CardClips(
                    colorBackground = green_15,
                    colorBorder = green_shamrock,
                    colorText = green_shamrock,
                    colorIcon = green_shamrock,
                    icon = R.drawable.icon_check,
                    value = stringResource(R.string.recycling_status_done)
                )
            else item.daysLeft?.let { DaysLeftClip(it) }
                ?: CardClips(
                    colorBackground = green_15,
                    colorBorder = green_shamrock,
                    colorText = green_shamrock,
                    value = stringResource(R.string.recycling_status_in_progress)
                )
        }
        if (!recycling.isCompleted) {
            ReadyDateText(item)
            BaseSlider(percentFloat = item.progress, color = green_shamrock)
        }
    }
}

@Composable
fun RecyclingDeleteBottomSheet(
    item: RecyclingItem,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        titleRes = R.string.recycling_delete_title,
        supportRes = R.string.recycling_delete_support,
        textButtonRes = R.string.recycling_delete_button,
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick
    ) {
        RecyclingSummaryCard(item)
    }
}

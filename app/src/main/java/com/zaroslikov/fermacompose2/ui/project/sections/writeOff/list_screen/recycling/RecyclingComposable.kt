package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.white
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toSuffixList
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.ButtonForGroupButtons
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconDone
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.ExposedDropdownMenuEnum
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedSwitch
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import com.zaroslikov.fermacompose2.ui.project.sections.recycling.InfoRecyclingCard

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

/** Поля формы переработки: дата, сырьё, готовая продукция со сроками. Примечание добавляет форма списания. */
@Composable
fun RecyclingEntryFields(
    form: RecyclingForm,
    date: String,
    rawTitles: List<DomainTitleSuffixCategory>,
    productTitles: List<TitleAndSuffixDomain>,
    onDateChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (Long) -> Unit,
    onIngredientChoice: (Long, DomainTitleSuffixCategory) -> Unit,
    onIngredientCountChange: (Long, String) -> Unit,
    onAddProduct: () -> Unit,
    onRemoveProduct: (Long) -> Unit,
    onProductTitleChange: (Long, String) -> Unit,
    onProductTitleChoice: (Long, Pair<String, Suffix>) -> Unit,
    onProductCountChange: (Long, String) -> Unit,
    onProductSuffixChange: (Long, Suffix) -> Unit,
    onProductReadyModeChange: (Long, RecyclingReadyMode) -> Unit,
    onProductDaysChange: (Long, String) -> Unit,
    onProductReadyDateChange: (Long, String) -> Unit,
) {
    InfoRecyclingCard(stringResource(R.string.recycling_info))

    OutlinedTextDateNew(
        value = date,
        onValueChange = onDateChange,
        intRes = R.string.outlined_text_recycling_date,
        intResSup = R.string.support_text_recycling_date
    )

    form.ingredients.forEachIndexed { index, ingredient ->
        key(ingredient.key) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PositionLabel(
                    text = stringResource(R.string.recycling_raw_item, index + 1),
                    removeDescriptionRes = R.string.recycling_remove_raw,
                    canRemove = form.ingredients.size > 1,
                    onRemove = { onRemoveIngredient(ingredient.key) }
                )
                IngredientCard(
                    ingredient = ingredient,
                    titles = rawTitles,
                    onChoice = { onIngredientChoice(ingredient.key, it) },
                    onCountChange = { onIngredientCountChange(ingredient.key, it) }
                )
            }
        }
    }
    AddPositionButton(R.string.recycling_add_raw, onAddIngredient)

    Text(text = stringResource(R.string.recycling_section_product), style = textBold_16)
    // Сырьё не предлагаем в готовой продукции: из молока не получается «Молоко»
    val rawKeys = form.rawTitleKeys()
    val availableTitles = productTitles.filterNot { it.title.trim().lowercase() in rawKeys }
    form.products.forEachIndexed { index, product ->
        key(product.key) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PositionLabel(
                    text = stringResource(R.string.recycling_product_item, index + 1),
                    removeDescriptionRes = R.string.recycling_remove_product,
                    canRemove = form.products.size > 1,
                    onRemove = { onRemoveProduct(product.key) }
                )
                ProductCard(
                    product = product,
                    date = date,
                    titles = availableTitles,
                    onTitleChange = { onProductTitleChange(product.key, it) },
                    onTitleChoice = { onProductTitleChoice(product.key, it) },
                    onCountChange = { onProductCountChange(product.key, it) },
                    onSuffixChange = { onProductSuffixChange(product.key, it) },
                    onReadyModeChange = { onProductReadyModeChange(product.key, it) },
                    onDaysChange = { onProductDaysChange(product.key, it) },
                    onReadyDateChange = { onProductReadyDateChange(product.key, it) }
                )
            }
        }
    }
    AddPositionButton(R.string.recycling_add_product, onAddProduct)
}

/** Подпись позиции над карточкой («Сырьё 1», «Товар 2») и крестик удаления справа. */
@Composable
private fun PositionLabel(
    text: String,
    removeDescriptionRes: Int,
    canRemove: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, style = text_12, color = gray_7)
        if (canRemove)
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(removeDescriptionRes),
                    modifier = Modifier.size(18.dp),
                    tint = gray_7
                )
            }
    }
}

@Composable
private fun AddPositionButton(textRes: Int, onClick: () -> Unit) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = green_g_4,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconAndTextNew(
                iconRes = R.drawable.baseline_add_circle_outline_24,
                valueString = stringResource(textRes),
                iconColor = green_shamrock,
                textColor = green_shamrock,
                iconSize = 20.dp
            )
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: RecyclingIngredientForm,
    titles: List<DomainTitleSuffixCategory>,
    onChoice: (DomainTitleSuffixCategory) -> Unit,
    onCountChange: (String) -> Unit
) {
    BorderCard {
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

/**
 * Позиция готовой продукции. «Готово позже» открывает выбор срока: «Через N дней» (от даты
 * переработки [date]) или «К дате»; в БД в обоих случаях попадает только дата готовности.
 */
@Composable
private fun ProductCard(
    product: RecyclingProductForm,
    date: String,
    titles: List<TitleAndSuffixDomain>,
    onTitleChange: (String) -> Unit,
    onTitleChoice: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onReadyModeChange: (RecyclingReadyMode) -> Unit,
    onDaysChange: (String) -> Unit,
    onReadyDateChange: (String) -> Unit
) {
    BorderCard {
        OutlinedTextTitleAddNew(
            value = product.title,
            onValueChange = onTitleChange,
            onValueChangeSuffix = onTitleChoice,
            titleList = titles,
            isErrorTitle = false,
            isErrorSlash = product.title.isSlash(),
            drawableRes = R.drawable.icon_add_product,
            intResSup = R.string.support_text_recycling_product,
            isBorderCard = false
        )
        OutlinedTextCountNew(
            value = product.count,
            onValueChange = onCountChange,
            suffix = product.countSuffix,
            onSuffixChange = onSuffixChange,
            isError = product.count.isNotEmpty() && product.count.toConvertZeroDouble() <= 0.0,
            intResSup = R.string.support_text_recycling_count,
            isBorderCard = false
        )
        OutlinedSwitch(
            checked = product.readyMode != RecyclingReadyMode.NONE,
            onCheckedChange = {
                onReadyModeChange(if (it) RecyclingReadyMode.DAYS else RecyclingReadyMode.NONE)
            },
            isBorderCard = false,
            leadingIconRes = R.drawable.baseline_access_time_24,
            labelIntRes = R.string.recycling_delayed,
            supportingText = R.string.recycling_delayed_sup
        )
        if (product.readyMode == RecyclingReadyMode.NONE) return@BorderCard
        ReadyModeChoice(mode = product.readyMode, onModeChange = onReadyModeChange)
        if (product.readyMode == RecyclingReadyMode.DAYS) {
            BaseOutlinedTextNew(
                value = product.days,
                onValueChange = onDaysChange,
                leadingIconRes = R.drawable.baseline_access_time_24,
                labelIntRes = R.string.outlined_text_recycling_days,
                intResSup = R.string.support_text_recycling_days,
                isError = product.days.isNotEmpty() && product.isErrorDays(),
                intResError = R.string.error_recycling_days,
                isNecessarily = true,
                maxLength = 4,
                keyboardOptions = keyboardOptionsNextNumber()
            )
            product.readyLocalDate(date)?.let { ready ->
                IconAndTextNew(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = stringResource(
                        R.string.recycling_ready_at,
                        dateBuilder(ready.dayOfMonth, stringResource(monthToResString(ready.monthValue)), ready.year)
                    ),
                    iconColor = orang_9
                )
            }
        } else {
            OutlinedTextDateNew(
                value = product.readyDate,
                onValueChange = onReadyDateChange,
                intRes = R.string.outlined_text_recycling_ready_date,
                intResSup = R.string.support_text_recycling_ready_date,
                minDate = date,
                isBorderCard = false
            )
            if (product.isErrorReadyDate(date))
                Text(
                    text = stringResource(R.string.error_recycling_ready_date),
                    style = text_12,
                    color = error_base
                )
        }
    }
}

@Composable
private fun ReadyModeChoice(
    mode: RecyclingReadyMode,
    onModeChange: (RecyclingReadyMode) -> Unit
) {
    GroupButton(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                RecyclingReadyMode.DAYS to R.string.recycling_ready_mode_days,
                RecyclingReadyMode.DATE to R.string.recycling_ready_mode_date
            ).forEach { (item, textRes) ->
                val isSelected = item == mode
                ButtonForGroupButtons(
                    modifier = Modifier.weight(1f),
                    text = textRes,
                    backgroundColor = if (isSelected) green_shamrock else gray_6,
                    textColor = if (isSelected) white else marengo
                ) { onModeChange(item) }
            }
        }
    }
}

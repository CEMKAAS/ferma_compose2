package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import com.zaroslikov.fermacompose2.supportFun.formatterTime
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
import com.zaroslikov.fermacompose2.ui.elements.TextField.TimeOutlinedTextFieldNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import java.time.LocalDateTime

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
    onProductHoursChange: (Long, String) -> Unit,
    onProductMinutesChange: (Long, String) -> Unit,
    onProductReadyDateChange: (Long, String) -> Unit,
    onProductReadyTimeChange: (Long, String) -> Unit,
    onStartTimeChange: (String) -> Unit,
) {
    val start = form.start(date)

    AnimatedPositions(
        items = form.ingredients,
        keyOf = { it.key },
        addTextRes = R.string.recycling_add_raw,
        onAdd = onAddIngredient,
        onRemoved = onRemoveIngredient
    ) { ingredient, number, canRemove, remove ->
        IngredientCard(
            label = stringResource(R.string.recycling_raw_item, number),
            canRemove = canRemove,
            ingredient = ingredient,
            titles = rawTitles,
            onRemove = remove,
            onChoice = { onIngredientChoice(ingredient.key, it) },
            onCountChange = { onIngredientCountChange(ingredient.key, it) }
        )
    }

    Text(text = stringResource(R.string.recycling_section_product), style = textBold_16)
    // Сырьё не предлагаем в готовой продукции: из молока не получается «Молоко»
    val rawKeys = form.rawTitleKeys()
    val availableTitles = productTitles.filterNot { it.title.trim().lowercase() in rawKeys }
    AnimatedPositions(
        items = form.products,
        keyOf = { it.key },
        addTextRes = R.string.recycling_add_product,
        onAdd = onAddProduct,
        onRemoved = onRemoveProduct
    ) { product, number, canRemove, remove ->
        ProductCard(
            label = stringResource(R.string.recycling_product_item, number),
            canRemove = canRemove,
            onRemove = remove,
            product = product,
            date = date,
            start = start,
            isRawTitle = product.isRawTitle(rawKeys),
            titles = availableTitles,
            onTitleChange = { onProductTitleChange(product.key, it) },
            onTitleChoice = { onProductTitleChoice(product.key, it) },
            onCountChange = { onProductCountChange(product.key, it) },
            onSuffixChange = { onProductSuffixChange(product.key, it) },
            onReadyModeChange = { onProductReadyModeChange(product.key, it) },
            onHoursChange = { onProductHoursChange(product.key, it) },
            onMinutesChange = { onProductMinutesChange(product.key, it) },
            onReadyDateChange = { onProductReadyDateChange(product.key, it) },
            onReadyTimeChange = { onProductReadyTimeChange(product.key, it) }
        )
    }

    // Дата и время — внизу, перед примечанием; своя колонка: отступ до поля времени
    // сворачивается вместе с ним, без скачка в конце
    Column {
        OutlinedTextDateNew(
            value = date,
            onValueChange = onDateChange,
            intRes = R.string.outlined_text_recycling_date,
            intResSup = R.string.support_text_recycling_date
        )
        AnimatedVisibility(
            visible = form.isDelayed(),
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Box(modifier = Modifier.padding(top = 16.dp)) {
                TimeOutlinedTextFieldNew(
                    time = form.startTime,
                    onValueChange = onStartTimeChange,
                    intRes = R.string.outlined_text_recycling_start_time,
                    intResSup = R.string.support_text_recycling_start_time
                )
            }
        }
    }
}

/** Отступ под позицией формы: он внутри анимируемого контента и сворачивается вместе с карточкой. */
private val PositionSpacing = 16.dp

/**
 * Позиции формы и кнопка «Добавить» в собственной колонке без spacedBy: промежуток под позицией —
 * её нижний отступ, поэтому при удалении и добавлении он меняется вместе с карточкой и в конце
 * анимации высота не скачет. Сворачивающиеся позиции не входят в номера и в «можно убрать»,
 * чтобы нельзя было удалить последнюю, пока предыдущая ещё уходит.
 */
@Composable
private fun <T> AnimatedPositions(
    items: List<T>,
    keyOf: (T) -> Long,
    addTextRes: Int,
    onAdd: () -> Unit,
    onRemoved: (Long) -> Unit,
    content: @Composable (item: T, number: Int, canRemove: Boolean, remove: () -> Unit) -> Unit
) {
    val removing = remember { mutableStateListOf<Long>() }
    val remaining = items.filterNot { keyOf(it) in removing }
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            val itemKey = keyOf(item)
            key(itemKey) {
                DisposableEffect(Unit) { onDispose { removing.remove(itemKey) } }
                val position = remaining.indexOfFirst { keyOf(it) == itemKey }.takeIf { it >= 0 } ?: index
                AnimatedPosition(onRemoved = { onRemoved(itemKey) }) { remove ->
                    content(item, position + 1, remaining.size > 1 && itemKey !in removing) {
                        removing.add(itemKey)
                        remove()
                    }
                }
            }
        }
        AddPositionButton(addTextRes, onAdd)
    }
}

/**
 * Позиция формы с анимацией: появляется раскрытием, при удалении сначала сворачивается
 * (вместе с отступом под ней) и только потом вызывает [onRemoved], чтобы строка исчезла из состояния.
 */
@Composable
private fun AnimatedPosition(
    onRemoved: () -> Unit,
    content: @Composable (remove: () -> Unit) -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
    val currentOnRemoved by rememberUpdatedState(onRemoved)
    if (!visibleState.targetState && visibleState.isIdle && !visibleState.currentState)
        LaunchedEffect(Unit) { currentOnRemoved() }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
    ) {
        Column(modifier = Modifier.padding(bottom = PositionSpacing)) {
            content { visibleState.targetState = false }
        }
    }
}

/** Заголовок позиции в карточке («Сырьё 1», «Товар 2») и крестик удаления справа. */
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
        Text(text = text, style = textBold_16)
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
        padding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(12.dp),
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
                iconSize = 18.dp
            )
        }
    }
}

@Composable
private fun IngredientCard(
    label: String,
    canRemove: Boolean,
    ingredient: RecyclingIngredientForm,
    titles: List<DomainTitleSuffixCategory>,
    onRemove: () -> Unit,
    onChoice: (DomainTitleSuffixCategory) -> Unit,
    onCountChange: (String) -> Unit
) {
    BorderCard {
        PositionLabel(label, R.string.recycling_remove_raw, canRemove, onRemove)
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
 * Позиция готовой продукции. «Готово позже» открывает выбор срока: «Через N часов» (от даты
 * переработки [start]) или «Дата и время»; в БД в обоих случаях
 * попадает только момент готовности.
 */
@Composable
private fun ProductCard(
    label: String,
    canRemove: Boolean,
    onRemove: () -> Unit,
    product: RecyclingProductForm,
    date: String,
    start: LocalDateTime,
    isRawTitle: Boolean,
    titles: List<TitleAndSuffixDomain>,
    onTitleChange: (String) -> Unit,
    onTitleChoice: (Pair<String, Suffix>) -> Unit,
    onCountChange: (String) -> Unit,
    onSuffixChange: (Suffix) -> Unit,
    onReadyModeChange: (RecyclingReadyMode) -> Unit,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onReadyDateChange: (String) -> Unit,
    onReadyTimeChange: (String) -> Unit
) {
    BorderCard {
        PositionLabel(label, R.string.recycling_remove_product, canRemove, onRemove)
        OutlinedTextTitleAddNew(
            value = product.title,
            onValueChange = onTitleChange,
            onValueChangeSuffix = onTitleChoice,
            titleList = titles,
            isErrorTitle = isRawTitle,
            intResError = R.string.recycling_error_raw_as_product,
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
                onReadyModeChange(if (it) RecyclingReadyMode.HOURS else RecyclingReadyMode.NONE)
            },
            isBorderCard = false,
            leadingIconRes = R.drawable.baseline_access_time_24,
            labelIntRes = R.string.recycling_delayed,
            supportingText = null,
            infoRes = R.string.recycling_delayed_sup
        )
        // Последний выбранный срок: при сворачивании блок не перескакивает на другой режим
        var shownMode by remember { mutableStateOf(RecyclingReadyMode.HOURS) }
        if (product.readyMode != RecyclingReadyMode.NONE) shownMode = product.readyMode
        val mode = shownMode
        // Во время сворачивания товар уже в NONE: показываем его со старым режимом,
        // иначе ошибки и «Готово …» пропадают раньше анимации и высота дёргается
        val shown = product.copy(readyMode = mode)
        AnimatedVisibility(
            visible = product.readyMode != RecyclingReadyMode.NONE,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReadyModeChoice(mode = mode, onModeChange = onReadyModeChange)
                if (mode == RecyclingReadyMode.HOURS) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BaseOutlinedTextNew(
                            modifier = Modifier.weight(1f),
                            value = shown.hours,
                            onValueChange = onHoursChange,
                            leadingIconRes = R.drawable.baseline_access_time_24,
                            labelIntRes = R.string.outlined_text_recycling_hours,
                            intResSup = R.string.support_text_recycling_hours,
                            isError = shown.isDurationTouched() && shown.isErrorHours(),
                            maxLength = 4,
                            keyboardOptions = keyboardOptionsNextNumber()
                        )
                        BaseOutlinedTextNew(
                            modifier = Modifier.weight(1f),
                            value = shown.minutes,
                            onValueChange = onMinutesChange,
                            labelIntRes = R.string.outlined_text_recycling_minutes,
                            intResSup = R.string.support_text_recycling_minutes,
                            isError = shown.isDurationTouched() && shown.isErrorHours(),
                            maxLength = 2,
                            keyboardOptions = keyboardOptionsNextNumber()
                        )
                    }
                    if (shown.isDurationTouched() && shown.isErrorHours())
                        Text(
                            text = stringResource(R.string.error_recycling_hours),
                            style = text_12,
                            color = error_base
                        )
                    if (!shown.isErrorHours())
                        shown.readyDateTime(start)?.let { ready ->
                            IconAndTextNew(
                                iconRes = R.drawable.baseline_calendar_month_24,
                                valueString = stringResource(
                                    R.string.recycling_ready_at,
                                    stringResource(
                                        R.string.recycling_date_time,
                                        dateBuilder(
                                            ready.dayOfMonth,
                                            stringResource(monthToResString(ready.monthValue)),
                                            ready.year
                                        ),
                                        formatterTime(ready.hour, ready.minute)
                                    )
                                ),
                                iconColor = orang_9
                            )
                        }
                } else {
                    OutlinedTextDateNew(
                        value = shown.readyDate,
                        onValueChange = onReadyDateChange,
                        intRes = R.string.outlined_text_recycling_ready_date,
                        intResSup = R.string.support_text_recycling_ready_date,
                        minDate = date,
                        isBorderCard = false
                    )
                    TimeOutlinedTextFieldNew(
                        time = shown.readyTime,
                        onValueChange = onReadyTimeChange,
                        intRes = R.string.outlined_text_recycling_ready_time,
                        intResSup = R.string.support_text_recycling_ready_time,
                        isBorderCard = false
                    )
                    if (shown.isErrorReadyDateTime(start))
                        Text(
                            text = stringResource(R.string.error_recycling_ready_date_time),
                            style = text_12,
                            color = error_base
                        )
                }
            }
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
                RecyclingReadyMode.HOURS to R.string.recycling_ready_mode_hours,
                RecyclingReadyMode.DATE_TIME to R.string.recycling_ready_mode_date_time
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

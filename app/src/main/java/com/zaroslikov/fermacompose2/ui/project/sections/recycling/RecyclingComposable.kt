package com.zaroslikov.fermacompose2.ui.project.sections.recycling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.white
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.formatterTime
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelDetailNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.ValueStandardCard

private val recyclingGradient = listOf(green_shamrock, price_green)

private const val MINUTES_IN_HOUR = 60L
private const val MINUTES_IN_DAY = 24 * MINUTES_IN_HOUR

/**
 * Блок «В переработке» над списком (списания, «Продукция»): карточка на каждую позицию продукции,
 * которая ещё не поступила на склад. Клик открывает всю партию — [onClick] получает id переработки.
 */
@Composable
fun RecyclingInProgressBlock(
    items: List<RecyclingItem>,
    onClick: (Long) -> Unit
) {
    val inProgress = items
        .flatMap { item -> item.products.filter { !it.product.isCompleted }.map { item to it } }
        .sortedBy { (_, product) -> product.minutesLeft ?: 0L }
    if (inProgress.isEmpty()) return
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
                value = inProgress.size.toString()
            )
        }
        inProgress.forEach { (item, product) ->
            RecyclingProgressCard(
                item = item,
                productItem = product,
                onClick = { onClick(item.recycling.id) }
            )
        }
    }
}

/** Есть ли в партиях продукция, которая ещё в переработке, — показывать ли блок. */
fun List<RecyclingItem>.hasInProgress(): Boolean =
    any { item -> item.products.any { !it.product.isCompleted } }

@Composable
private fun RecyclingProgressCard(
    item: RecyclingItem,
    productItem: RecyclingProductItem,
    onClick: () -> Unit
) {
    val product = productItem.product
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
                IconIndicatorsAnimal(icon = R.drawable.baseline_autorenew_24, colors = listOf(violet_1, violet_2))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = product.title, style = textBold_16, maxLines = 2)
                    CountColorCard(product.count, product.countSuffix, product.countSuffix.toColorList())
                }
            }
            productItem.minutesLeft?.let { TimeLeftClip(it) }
        }
        ReadyDateText(product)
        if (item.ingredients.isNotEmpty()) {
            val raw = item.ingredients.map { it.ingredientText() }.joinToString()
            IconAndTextNew(
                iconRes = R.drawable.baseline_shopping_basket_24,
                valueString = stringResource(R.string.recycling_raw_summary, raw),
                iconColor = green_shamrock
            )
        }
        BaseSlider(percentFloat = productItem.progress, color = green_shamrock)
    }
}

/**
 * Кратко о продукции партии для обычной карточки списания сырья: что и сколько получится,
 * сколько осталось и полоска процесса у ещё не готовых товаров.
 */
@Composable
fun RecyclingProductsBrief(item: RecyclingItem) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item.products.forEach { productItem ->
            val product = productItem.product
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.icon_add_product),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = violet_1
                    )
                    Text(
                        text = product.title,
                        style = text_14,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    CountColorCard(product.count, product.countSuffix, product.countSuffix.toColorList())
                }
                if (product.isCompleted)
                    CardClips(
                        colorBackground = green_15,
                        colorBorder = green_shamrock,
                        colorText = green_shamrock,
                        colorIcon = green_shamrock,
                        icon = R.drawable.icon_check,
                        value = stringResource(R.string.recycling_status_done)
                    )
                else productItem.minutesLeft?.let { TimeLeftClip(it) }
            }
            if (!product.isCompleted)
                BaseSlider(percentFloat = productItem.progress, color = violet_1)
        }
    }
}

/** Остаток до готовности: от суток — в днях, от часа — в часах, иначе «меньше часа». */
@Composable
private fun TimeLeftClip(minutesLeft: Long) {
    CardClips(
        colorBackground = orang_9.copy(alpha = 0.12f),
        colorBorder = orang_9,
        colorText = orang_6,
        colorIcon = orang_9,
        icon = R.drawable.baseline_access_time_24,
        value = when {
            minutesLeft >= MINUTES_IN_DAY ->
                stringResource(R.string.recycling_days_left, (minutesLeft / MINUTES_IN_DAY).toInt())

            minutesLeft >= MINUTES_IN_HOUR ->
                stringResource(R.string.recycling_hours_left, (minutesLeft / MINUTES_IN_HOUR).toInt())

            else -> stringResource(R.string.recycling_less_hour)
        }
    )
}

@Composable
private fun ReadyDateText(product: DomainRecyclingProduct) {
    val readyYear = product.readyYear ?: return
    val date = stringResource(
        R.string.recycling_date_time,
        formatDate(product.readyDay ?: 1, product.readyMonth ?: 1, readyYear),
        formatterTime(product.readyHour ?: 0, product.readyMinute ?: 0)
    )
    IconAndTextNew(
        iconRes = R.drawable.baseline_calendar_month_24,
        valueString = date,
        iconColor = violet_1
    )
}

@Composable
private fun formatDate(day: Int, month: Int, year: Int): String =
    dateBuilder(day, stringResource(monthToResString(month)), year)

@Composable
private fun DomainWriteOffTable.ingredientText(): String =
    "$title ${count.formatNumber()} ${stringResource(countSuffix.toResId())}"

/**
 * Детали партии — по образцу деталей обычного списания: сырьё с количествами, дата и время начала,
 * примечание; ниже — продукция со статусами («Готово сейчас» — по позиции, [onCompleteClick] получает
 * её id). Внизу — удаление, правка ([onEditClick] = null — без кнопки «Изменить») и «Списать в утиль»,
 * пока в партии есть неготовая продукция.
 */
@Composable
fun RecyclingDetailBottomSheet(
    item: RecyclingItem,
    isArchive: Boolean,
    onCompleteClick: (Long) -> Unit,
    onDeleteClick: () -> Unit,
    onDisposeClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onEditClick: (() -> Unit)? = null
) {
    val recycling = item.recycling
    val canDispose = !isArchive && item.products.any { !it.product.isCompleted }
    BaseBottomSheet(
        title = stringResource(R.string.recycling_detail_title),
        onDismissRequest = onDismissRequest,
        contentBottom = if (isArchive) null else {
            {
                RecyclingButtonPanel(
                    canDispose = canDispose,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onDisposeClick = onDisposeClick
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ValueStandardCard(
                titleRes = R.string.recycling_detail_raw,
                value = item.ingredients.map { it.ingredientText() }.joinToString("\n"),
                iconRes = R.drawable.baseline_autorenew_24,
                iconColor = violet_1,
                boxColor = violet_3
            )
            val date = formatDate(recycling.day, recycling.month, recycling.year)
            ValueStandardCard(
                titleRes = R.string.recycling_detail_date,
                value = recycling.startHour?.let { hour ->
                    stringResource(
                        R.string.recycling_date_time,
                        date,
                        formatterTime(hour, recycling.startMinute ?: 0)
                    )
                } ?: date,
                iconRes = R.drawable.baseline_calendar_month_24,
                iconColor = violet_1,
                boxColor = violet_3
            )
            if (recycling.note.isNotBlank())
                ValueStandardCard(
                    titleRes = R.string.detail_card_note,
                    value = recycling.note,
                    iconRes = R.drawable.baseline_sticky_note_2_24,
                    iconColor = violet_1,
                    boxColor = violet_3
                )
            Text(text = stringResource(R.string.recycling_detail_product), style = textBold_16)
            item.products.forEach { productItem ->
                RecyclingProductCard(
                    productItem = productItem,
                    onCompleteClick = if (isArchive || productItem.product.isCompleted) null
                    else {
                        { onCompleteClick(productItem.product.id) }
                    }
                )
            }
            // BaseBottomSheet оставляет место под одну строку кнопок, «Списать в утиль» — вторая
            if (canDispose) Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

/** «Удалить» и «Изменить» (или только «Удалить»), под ними — «Списать в утиль» при [canDispose]. */
@Composable
private fun RecyclingButtonPanel(
    canDispose: Boolean,
    onEditClick: (() -> Unit)?,
    onDeleteClick: () -> Unit,
    onDisposeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
    ) {
        if (onEditClick != null)
            ButtonPanelDetailNew(
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(violet_1, violet_2),
                onClickUpdate = onEditClick,
                onClickDelete = onDeleteClick
            )
        else {
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = gray_6)
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                iconRes = R.drawable.baseline_delete_24,
                iconColor = error_base,
                text = stringResource(R.string.button_delete),
                textColor = error_base,
                onClick = onDeleteClick
            )
        }
        if (canDispose)
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                iconRes = R.drawable.baseline_delete_24,
                iconColor = orang_9,
                borderColor = orang_9,
                text = stringResource(R.string.recycling_dispose_button),
                textColor = orang_6,
                onClick = onDisposeClick
            )
    }
}

/** Позиция продукции партии; [onCompleteClick] = null — без кнопки «Готово сейчас». */
@Composable
private fun RecyclingProductCard(
    productItem: RecyclingProductItem,
    onCompleteClick: (() -> Unit)?
) {
    val product = productItem.product
    BorderCard(modifier = Modifier.fillMaxWidth(), onClick = null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.title,
                    style = textBold_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                CountColorCard(product.count, product.countSuffix, product.countSuffix.toColorList())
            }
            if (product.isCompleted)
                CardClips(
                    colorBackground = green_15,
                    colorBorder = green_shamrock,
                    colorText = green_shamrock,
                    colorIcon = green_shamrock,
                    icon = R.drawable.icon_check,
                    value = stringResource(R.string.recycling_status_done)
                )
        }
        ReadyDateText(product)
        if (!product.isCompleted)
            BaseSlider(percentFloat = productItem.progress, color = violet_1)
        onCompleteClick?.let {
            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.recycling_complete_now),
                prefixIconRes = R.drawable.icon_check,
                colors = recyclingGradient,
                onClick = it
            )
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item.products.forEach { RecyclingProductCard(productItem = it, onCompleteClick = null) }
        }
    }
}

/** Подтверждение «Списать в утиль»: сырьё партии уходит в утиль, неготовая продукция отменяется. */
@Composable
fun RecyclingDisposeBottomSheet(
    item: RecyclingItem,
    onDismissRequest: () -> Unit,
    onDisposeClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        titleRes = R.string.recycling_dispose_title,
        supportRes = R.string.recycling_dispose_support,
        textButtonRes = R.string.recycling_dispose_button,
        textRes = R.string.recycling_dispose_warning_text,
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDisposeClick
    ) {
        ValueStandardCard(
            titleRes = R.string.recycling_detail_raw,
            value = item.ingredients.map { it.ingredientText() }.joinToString("\n"),
            iconRes = R.drawable.baseline_autorenew_24,
            iconColor = violet_1,
            boxColor = violet_3
        )
    }
}

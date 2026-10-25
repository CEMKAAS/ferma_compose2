package com.zaroslikov.fermacompose2.ui.project.sections.recycling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
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
import com.zaroslikov.fermacompose2.supportFun.formatNumber
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
import com.zaroslikov.fermacompose2.ui.elements.NoteColorCard
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet

private val recyclingGradient = listOf(green_shamrock, price_green)

@Composable
fun InfoRecyclingCard(text: String) {
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
        .sortedBy { (_, product) -> product.daysLeft ?: 0L }
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
                IconIndicatorsAnimal(icon = R.drawable.baseline_autorenew_24, colors = recyclingGradient)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = product.title, style = textBold_16, maxLines = 2)
                    CountColorCard(product.count, product.countSuffix, product.countSuffix.toColorList())
                }
            }
            productItem.daysLeft?.let { DaysLeftClip(it) }
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
private fun ReadyDateText(product: DomainRecyclingProduct) {
    val readyYear = product.readyYear ?: return
    val date = formatDate(product.readyDay ?: 1, product.readyMonth ?: 1, readyYear)
    if (product.isCompleted)
        IconAndTextNew(
            iconRes = R.drawable.baseline_calendar_month_24,
            valueString = "${stringResource(R.string.recycling_detail_done_date)}: $date",
            iconColor = green_shamrock
        )
    else
        IconAndTextNew(
            iconRes = R.drawable.baseline_calendar_month_24,
            valueString = stringResource(R.string.recycling_ready_at, date),
            iconColor = orang_9
        )
}

@Composable
private fun formatDate(day: Int, month: Int, year: Int): String =
    dateBuilder(day, stringResource(monthToResString(month)), year)

@Composable
private fun DomainWriteOffTable.ingredientText(): String =
    "$title ${count.formatNumber()} ${stringResource(countSuffix.toResId())}"

/**
 * Детали партии: вся продукция со статусами («Готово сейчас» — по позиции), сырьё, даты;
 * удаление всей переработки. [onCompleteClick] получает id позиции продукции.
 */
@Composable
fun RecyclingDetailBottomSheet(
    item: RecyclingItem,
    isArchive: Boolean,
    onCompleteClick: (Long) -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val recycling = item.recycling
    BaseBottomSheet(
        title = stringResource(R.string.recycling_detail_title),
        supText = item.title,
        iconRes = R.drawable.baseline_autorenew_24,
        colors = recyclingGradient,
        onDismissRequest = onDismissRequest,
        contentBottom = if (isArchive) null else {
            {
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    iconRes = R.drawable.baseline_delete_24,
                    iconColor = error_base,
                    text = stringResource(R.string.recycling_delete_button),
                    textColor = error_base,
                    onClick = onDeleteClick
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
            if (item.products.any { !it.product.isCompleted })
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
            }
            if (recycling.note.isNotBlank()) NoteColorCard(recycling.note, green_shamrock)
        }
    }
}

/** Позиция продукции партии; [onCompleteClick] = null — без кнопки «Готово сейчас». */
@Composable
private fun RecyclingProductCard(
    productItem: RecyclingProductItem,
    onCompleteClick: (() -> Unit)?
) {
    val product = productItem.product
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
                Text(text = product.title, style = textBold_16)
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
            else productItem.daysLeft?.let { DaysLeftClip(it) }
                ?: CardClips(
                    colorBackground = green_15,
                    colorBorder = green_shamrock,
                    colorText = green_shamrock,
                    value = stringResource(R.string.recycling_status_in_progress)
                )
        }
        ReadyDateText(product)
        if (!product.isCompleted)
            BaseSlider(percentFloat = productItem.progress, color = green_shamrock)
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

package com.zaroslikov.fermacompose2.ui.project.sections.recycling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.marengo
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
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.NoteColorCard
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.SliderGradient
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelDetailNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.ValueStandardCard

private val recyclingGradient = listOf(green_shamrock, price_green)

private const val MINUTES_IN_HOUR = 60L
private const val MINUTES_IN_DAY = 24 * MINUTES_IN_HOUR

/**
 * Продукция «В переработке» над списком «Продукции»: каждая позиция, которая ещё не поступила
 * на склад, — обычной карточкой товара с пометкой переработки, сырьём и полоской оставшегося срока.
 * Клик открывает детали — [onClick] получает id переработки и id нажатого товара.
 */
@Composable
fun RecyclingInProgressBlock(
    items: List<RecyclingItem>,
    /** Какую продукцию показать: например, только с названием группы товара. */
    filter: (DomainRecyclingProduct) -> Boolean = { true },
    /** В шторке группы товара: рамочные карточки без названия, как записи группы. */
    inGroup: Boolean = false,
    onClick: (recyclingId: Long, productId: Long) -> Unit
) {
    val inProgress = items
        .flatMap { item ->
            item.products
                .filter { !it.product.isCompleted && filter(it.product) }
                .map { item to it }
        }
        .sortedBy { (_, product) -> product.minutesLeft ?: 0L }
    if (inProgress.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(if (inGroup) 12.dp else 16.dp)) {
        inProgress.forEach { (item, product) ->
            RecyclingProgressCard(
                item = item,
                productItem = product,
                inGroup = inGroup,
                onClick = { onClick(item.recycling.id, product.product.id) }
            )
        }
    }
}

/** Есть ли в партиях продукция, которая ещё в переработке, — показывать ли блок. */
fun List<RecyclingItem>.hasInProgress(
    filter: (DomainRecyclingProduct) -> Boolean = { true }
): Boolean = any { item -> item.products.any { !it.product.isCompleted && filter(it.product) } }

/**
 * Карточка в стиле обычной карточки товара «Продукции» (полоса слева — признак переработки);
 * [inGroup] — как записи в шторке группы: рамка, без полосы и названия (оно в заголовке).
 */
@Composable
private fun RecyclingProgressCard(
    item: RecyclingItem,
    productItem: RecyclingProductItem,
    inGroup: Boolean,
    onClick: () -> Unit
) {
    val product = productItem.product
    val recycling = item.recycling
    val content: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    if (!inGroup)
                        Text(
                            text = product.title,
                            style = textBold_16,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    CountColorCard(product.count, product.countSuffix, product.countSuffix.toColorList())
                }
            }
            product.category?.takeIf { it.isNotBlank() }?.let {
                IconAndTextNew(
                    iconRes = R.drawable.baseline_format_list_bulleted_24,
                    valueString = it,
                    iconColor = green_shamrock
                )
            }
            // Когда будет готово — пока скрыто
            // ReadyAtText(product)
            // Как в списании: сырьё и остаток до готовности над полоской во всю ширину
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecyclingRawLine(item, Modifier.weight(1f))
                productItem.minutesLeft?.let { TimeLeftClip(it) }
            }
            SliderGradient(percentFloat = productItem.progress, colors = recyclingGradient)
            if (recycling.note.isNotBlank()) NoteColorCard(recycling.note, green_shamrock)
        }
    }
    if (inGroup)
        BorderCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) { content() }
    else CardFieldNew(
        modifier = Modifier.fillMaxWidth(),
        colors = recyclingGradient,
        onClick = onClick
    ) { content() }
}

/** Сырьё партии одной строкой: «Молоко 2 шт., Соль 1 кг»; null — сырья уже нет (списано в утиль). */
@Composable
fun RecyclingItem.rawText(): String? =
    ingredients.takeIf { it.isNotEmpty() }?.map { it.ingredientText() }?.joinToString()

/**
 * Строка карточки товара, полученного переработкой: значок переработки и сырьё — название
 * с цветной плашкой количества; без сырья (списано в утиль) — просто «Переработка».
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecyclingRawLine(item: RecyclingItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painterResource(R.drawable.baseline_autorenew_24),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = green_shamrock
        )
        if (item.ingredients.isEmpty())
            Text(text = stringResource(R.string.recycling_detail_title), style = text_14, color = marengo)
        else FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item.ingredients.forEach { raw ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = raw.title, style = text_14, color = marengo)
                    CountColorCard(raw.count, raw.countSuffix, raw.countSuffix.toColorList())
                }
            }
        }
    }
}

/** «Готово 28 окт. 2026, 14:00» — когда товар поступит на склад. */
@Composable
private fun ReadyAtText(product: DomainRecyclingProduct) {
    val readyYear = product.readyYear ?: return
    val dateTime = stringResource(
        R.string.recycling_date_time,
        formatDate(product.readyDay ?: 1, product.readyMonth ?: 1, readyYear),
        formatterTime(product.readyHour ?: 0, product.readyMinute ?: 0)
    )
    IconAndTextNew(
        iconRes = R.drawable.baseline_access_time_24,
        valueString = stringResource(R.string.recycling_ready_at, dateTime),
        iconColor = green_shamrock
    )
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
 * пока в партии есть неготовая продукция ([onDisposeClick] = null — без неё).
 * [productId] — показать только эту позицию продукции (клик по товару в «Продукции»).
 */
@Composable
fun RecyclingDetailBottomSheet(
    item: RecyclingItem,
    isArchive: Boolean,
    onCompleteClick: (Long) -> Unit,
    onDeleteClick: () -> Unit,
    onDisposeClick: (() -> Unit)?,
    onDismissRequest: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    productId: Long? = null
) {
    val recycling = item.recycling
    val products = item.products.filter { productId == null || it.product.id == productId }
    val canDispose = onDisposeClick != null && !isArchive && item.products.any { !it.product.isCompleted }
    BaseBottomSheet(
        title = stringResource(R.string.recycling_detail_title),
        onDismissRequest = onDismissRequest,
        contentBottom = if (isArchive) null else {
            {
                RecyclingButtonPanel(
                    canDispose = canDispose,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onDisposeClick = onDisposeClick ?: {}
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
            products.forEach { productItem ->
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
    onDeleteClick: () -> Unit,
    /** Карточки партии как в разделе (в списаниях — строки сырья); null — карточки продукции. */
    itemCard: (@Composable () -> Unit)? = null
) {
    WarningDeleteBottomSheet(
        titleRes = R.string.recycling_delete_title,
        supportRes = R.string.recycling_delete_support,
        textButtonRes = R.string.recycling_delete_button,
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick
    ) {
        if (itemCard != null) itemCard()
        else Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item.products.forEach { RecyclingProductCard(productItem = it, onCompleteClick = null) }
        }
    }
}

/**
 * Удаление одного товара партии [productId], ещё не поступившего на склад: единственный
 * товар — сырьё уйдёт в утиль, иначе удаляется только он.
 */
@Composable
fun RecyclingProductDeleteBottomSheet(
    item: RecyclingItem,
    productId: Long,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isSingle = item.products.size == 1
    WarningDeleteBottomSheet(
        supportRes = if (isSingle) R.string.recycling_delete_product_single_support
        else R.string.recycling_delete_product_support,
        textRes = if (isSingle) R.string.recycling_delete_progress_single_warning_text
        else R.string.recycling_delete_progress_warning_text,
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick
    ) {
        item.products.find { it.product.id == productId }?.let {
            RecyclingProductCard(productItem = it, onCompleteClick = null)
        }
        if (isSingle && item.ingredients.isNotEmpty()) RecyclingDisposeRawCards(item)
    }
}

/**
 * Сырьё, которое уйдёт в утиль при удалении единственного товара переработки: заголовок
 * и карточки списаний уже со статусом «На утилизацию».
 */
@Composable
fun RecyclingDisposeRawCards(item: RecyclingItem) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IconAndTextNew(
            iconRes = R.drawable.baseline_delete_24,
            valueString = stringResource(R.string.recycling_delete_product_raw_title),
            iconColor = error_base,
            textStyle = textBold_16
        )
        item.ingredients.forEach { raw ->
            DetailProductCardNew(
                title = raw.title,
                count = raw.count,
                suffix = raw.countSuffix,
                statusWriteOff = false,
                productOrigin = raw.productOrigin,
                category = raw.category,
                note = raw.note,
                color = violet_1,
                day = raw.day,
                month = raw.month,
                year = raw.year,
                isArchive = true,
                isCardField = false,
            )
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

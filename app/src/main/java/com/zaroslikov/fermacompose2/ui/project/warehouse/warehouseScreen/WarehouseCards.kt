package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.white
import com.zaroslikov.fermacompose2.supportFun.formatNumber

private val CardGap = 14.dp
private val ContentInset = 15.dp
private val CardShape = RoundedCornerShape(21.dp)
private val CalendarShape = RoundedCornerShape(23.dp)
private val CompactShape = RoundedCornerShape(19.dp)
private val IconShape = RoundedCornerShape(10.dp)
private val HideButtonColor = Color(0xFF363636)
private val CardTextLocales = LocaleList("ru-RU")

@Composable
internal fun WarehouseCards(
    layout: WarehouseCardLayout,
    counts: Map<WarehouseCard, Int>,
    editing: Boolean,
    canChangeLayout: Boolean,
    onStartEditing: () -> Unit,
    onOpenCard: (WarehouseCard) -> Unit,
    onLayoutChange: (WarehouseCardLayout) -> Unit,
    modifier: Modifier = Modifier,
    tasksToday: Int? = null,
    footerContent: @Composable () -> Unit = {},
    calendarContent: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    val layoutDirection = LocalLayoutDirection.current
    val drag = remember(layoutDirection) { WarehouseCardDrag(layoutDirection) }
    val density = LocalDensity.current
    val canEdit = editing && canChangeLayout
    val source = drag.source
    val cards = layout.cards
    val wiggle = if (editing) {
        rememberInfiniteTransition(label = "warehouseEdit").animateFloat(
            initialValue = -1.15f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(tween(220), RepeatMode.Reverse),
            label = "warehouseTileRotation",
        )
    } else null
    LaunchedEffect(editing, canChangeLayout) {
        if (!editing || !canChangeLayout) drag.clear()
    }
    LaunchedEffect(source, scrollState, canEdit, layout, density) {
        if (source == null || !canEdit) return@LaunchedEffect
        val (edge, maxStep) = with(density) { 60.dp.toPx() to 14.dp.toPx() }
        while (drag.source == source) {
            withFrameNanos { }
            val step = drag.autoScrollStep(edge, maxStep)
            if (step != 0f) scrollState.scrollBy(step)
            drag.updateTarget(layout)
        }
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .clipToBounds()
            .onGloballyPositioned { drag.viewport = it },
    ) {
        val tileWidth = (maxWidth - ContentInset * 2 - CardGap) / 2
        val stackLabel = tileWidth < 140.dp || density.fontScale >= 1.35f
        val renderCard: @Composable (WarehouseCard) -> Unit = { card ->
            val above = if (canEdit) layout.adjacentDrop(card, -1) else null
            val below = if (canEdit) layout.adjacentDrop(card, 1) else null
            val onHide: () -> Unit = { onLayoutChange(layout.hide(card)) }
            Box(Modifier.fillMaxWidth()) {
                WarehouseTile(
                    card = card,
                    count = counts[card] ?: 0,
                    tasksToday = tasksToday,
                    stackLabel = stackLabel,
                    modifier = Modifier.fillMaxWidth()
                        .then(warehouseCardDragSource(
                            card, layout, drag,
                            enabled = canChangeLayout,
                            editing = editing,
                            onActivate = onStartEditing,
                            onMove = onLayoutChange,
                        ))
                        .graphicsLayer {
                            alpha = if (source == card) 0.35f else 1f
                            rotationZ = if (source == card) 0f else {
                                (wiggle?.value ?: 0f) * if (card.ordinal % 2 == 0) 1f else -1f
                            }
                        },
                    dropTarget = drag.target == card && source != null && source != card,
                    onOpen = if (editing) null else { { onOpenCard(card) } },
                    onStartEditing = if (!editing && canChangeLayout) onStartEditing else null,
                    onMoveUp = above?.let { { onLayoutChange(layout.move(card, it)) } },
                    onMoveDown = below?.let { { onLayoutChange(layout.move(card, it)) } },
                    onHide = onHide.takeIf { canEdit },
                    calendarContent = calendarContent,
                )
                if (editing && source != card) {
                    WarehouseHideButton(
                        label = card.localizedWarehouseLabel(),
                        enabled = canEdit && source == null,
                        onClick = onHide,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = ContentInset)
                .padding(top = 6.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(CardGap),
        ) {
            if (cards.isEmpty()) {
                Text(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = stringResource(R.string.warehouse_cards_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!editing) {
                    OutlinedButton(onClick = onStartEditing, enabled = canChangeLayout) {
                        Text(stringResource(R.string.warehouse_cards_edit))
                    }
                }
            }
            for ((index, section) in layout.sections.withIndex()) {
                if (index > 0) key(WarehouseCard.CALENDAR) { renderCard(WarehouseCard.CALENDAR) }
                if (section.left.isNotEmpty() || section.right.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(CardGap),
                    ) {
                        for (column in 0..1) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(CardGap),
                            ) {
                                for (card in section[column]) key(card) { renderCard(card) }
                            }
                        }
                    }
                }
            }

            if (!editing && cards.isNotEmpty()) {
                footerContent()
            }
        }
        if (source != null) {
            WarehouseTile(
                card = source,
                count = counts[source] ?: 0,
                tasksToday = tasksToday,
                stackLabel = stackLabel,
                dragging = true,
                modifier = Modifier
                    .width(with(density) { drag.sourceBounds.width.toDp() })
                    .heightIn(min = with(density) { drag.sourceBounds.height.toDp() })
                    .graphicsLayer {
                        translationX = drag.ghostOffset.x
                        translationY = drag.ghostOffset.y
                        alpha = 0.96f
                    }
                    .clearAndSetSemantics { },
                calendarContent = calendarContent,
            )
        }
    }
}

@Composable
private fun WarehouseTile(
    card: WarehouseCard,
    count: Int,
    tasksToday: Int?,
    stackLabel: Boolean,
    modifier: Modifier = Modifier,
    dropTarget: Boolean = false,
    dragging: Boolean = false,
    onOpen: (() -> Unit)? = null,
    onStartEditing: (() -> Unit)? = null,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    onHide: (() -> Unit)? = null,
    calendarContent: @Composable () -> Unit,
) {
    val visuals = card.visuals()
    val label = stringResource(visuals.title)
    val action = if (card == WarehouseCard.CALENDAR) stringResource(R.string.warehouse_cards_open_calendar) else null
    val moves = listOfNotNull(
        onMoveUp?.let { stringResource(R.string.warehouse_cards_move_up) to it },
        onMoveDown?.let { stringResource(R.string.warehouse_cards_move_down) to it },
        onHide?.let { stringResource(R.string.warehouse_cards_hide) to it },
    )
    val shape = when {
        card == WarehouseCard.CALENDAR -> CalendarShape
        visuals.isPrimary -> CardShape
        else -> CompactShape
    }
    val outline = when {
        dropTarget -> Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
        visuals.gradient == null -> Modifier.border(1.dp, grey_2.copy(alpha = 0.8f), shape)
        else -> Modifier
    }
    val gradient = remember(visuals.gradient) { visuals.gradient?.let { Brush.linearGradient(it) } }
    val fill = if (gradient == null) Modifier.background(white) else Modifier.background(gradient)
    val interaction = if (onOpen != null) remember { MutableInteractionSource() } else null
    val editLabel = stringResource(R.string.warehouse_cards_edit)
    val pressAnimation = if (interaction != null) {
        val pressed by interaction.collectIsPressedAsState()
        val scale by animateFloatAsState(if (pressed) 0.98f else 1f, tween(150), label = "warehouseTilePress")
        Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else Modifier
    val openCard = if (onOpen == null) Modifier else Modifier
        .clickable(
            interactionSource = interaction,
            indication = ripple(color = visuals.foreground),
            role = Role.Button,
            onClickLabel = action,
            onClick = onOpen,
        )
        .then(if (action == null) Modifier else Modifier.semantics { contentDescription = action })
        .then(
            if (onStartEditing == null) Modifier else Modifier.semantics {
                onLongClick(label = editLabel) {
                    onStartEditing()
                    true
                }
            },
        )

    Column(
        modifier
            .then(pressAnimation)
            .dropShadow(
                shape,
                Shadow(
                    radius = if (dragging) 12.dp else if (visuals.isPrimary) 7.dp else 9.dp,
                    color = visuals.shadowColor.copy(alpha = if (dragging) 0.27f else if (visuals.isPrimary) 0.14f else 0.09f),
                    spread = (-1).dp,
                    offset = DpOffset(0.dp, 5.dp),
                ),
            )
            .clip(shape)
            .then(fill)
            .then(outline)
            .then(openCard),
    ) {
        val cardSemantics = if (moves.isEmpty()) Modifier else Modifier.semantics(mergeDescendants = true) {
            if (card == WarehouseCard.CALENDAR) contentDescription = label
            customActions = moves.map { (title, move) ->
                CustomAccessibilityAction(title) {
                    move()
                    true
                }
            }
        }
        if (card == WarehouseCard.CALENDAR) {
            Box(Modifier.fillMaxWidth().then(cardSemantics)) { calendarContent() }
        } else {
            Box(Modifier.fillMaxWidth().then(cardSemantics)) {
                val subtitle = if (card == WarehouseCard.TASKS && tasksToday != null) {
                    stringResource(R.string.warehouse_count_tasks_today, tasksToday.formatNumber())
                } else {
                    visuals.subtitle?.let { stringResource(it) }
                }
                WarehouseTileContent(visuals, label, count, subtitle, stackLabel)
            }
        }
    }
}

@Composable
private fun WarehouseTileContent(
    visuals: WarehouseCardVisuals,
    label: String,
    count: Int,
    subtitle: String?,
    stackLabel: Boolean,
) {
    val labelContent: @Composable () -> Unit = {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                localeList = CardTextLocales,
                hyphens = Hyphens.Auto,
                lineBreak = LineBreak.Heading,
                fontSize = if (visuals.isPrimary) 13.sp else 14.sp,
                lineHeight = 17.sp,
                letterSpacing = 0.sp,
            ),
            fontWeight = FontWeight.SemiBold,
            color = visuals.foreground,
        )
    }
    val subtitleContent: @Composable () -> Unit = {
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.sp),
                color = if (visuals.isPrimary) white.copy(alpha = 0.82f) else marengo,
            )
        }
    }
    val icon: @Composable () -> Unit = { WarehouseTileIcon(visuals) }
    val value: @Composable () -> Unit = {
        Text(
            text = count.formatNumber(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = if (visuals.isPrimary) 30.sp else 23.sp,
                lineHeight = if (visuals.isPrimary) 35.sp else 27.sp,
                letterSpacing = 0.sp,
            ),
            fontWeight = FontWeight.SemiBold,
            color = visuals.foreground,
        )
    }
    if (!visuals.isPrimary && !stackLabel) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 65.dp).padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Column(Modifier.weight(1f)) {
                labelContent()
                subtitleContent()
            }
            value()
        }
    } else {
        Column(
            Modifier.fillMaxWidth().heightIn(min = if (visuals.isPrimary) 136.dp else 65.dp)
                .padding(if (visuals.isPrimary) 15.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (stackLabel) {
                icon()
                labelContent()
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    icon()
                    labelContent()
                }
            }
            value()
            subtitleContent()
        }
    }
}

internal val WarehouseFeedGradient = listOf(Color(0xFFFF9918), Color(0xFFF45A05))
internal val WarehouseFeedForeground = Color(0xFF321900)

internal data class WarehouseSummaryMetric(
    val label: String,
    val value: String,
    val note: String? = null,
    val prominent: Boolean = false,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WarehouseSectionSummary(
    card: WarehouseCard,
    metrics: List<WarehouseSummaryMetric>,
    modifier: Modifier = Modifier,
    note: String? = null,
) {
    val visuals = card.visuals()
    val gradient = remember(visuals.gradient) { visuals.gradient?.let { Brush.linearGradient(it) } }
    val fill = if (gradient == null) Modifier.background(white) else Modifier.background(gradient)
    val foreground = if (card == WarehouseCard.FEED) WarehouseFeedForeground else visuals.foreground
    val labelStyle = MaterialTheme.typography.labelMedium.copy(localeList = CardTextLocales)
    val noteStyle = MaterialTheme.typography.bodySmall.copy(localeList = CardTextLocales)

    Row(
        modifier
            .fillMaxWidth()
            .clip(CardShape)
            .then(fill)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WarehouseTileIcon(visuals)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = if (metrics.size == 4) 2 else 3,
            ) {
                metrics.forEach { metric ->
                    Column(Modifier.semantics(mergeDescendants = true) {}) {
                        Text(metric.label, style = labelStyle, color = foreground)
                        Text(
                            text = metric.value,
                            style = if (metric.prominent) MaterialTheme.typography.headlineSmall
                            else MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = foreground,
                        )
                        metric.note?.let { Text(it, style = noteStyle, color = foreground) }
                    }
                }
            }
            note?.let { Text(it, style = noteStyle, color = foreground) }
        }
    }
}

@Composable
private fun WarehouseTileIcon(visuals: WarehouseCardVisuals) {
    Box(
        Modifier.size(if (visuals.isPrimary) 36.dp else 32.dp).background(visuals.iconBackground, IconShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(visuals.icon), null, Modifier.size(19.dp), tint = visuals.iconTint)
    }
}

@Composable
private fun WarehouseHideButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.warehouse_cards_hide_named, label)
    Box(
        modifier = modifier.size(48.dp)
            .semantics { contentDescription = description }
            .clickable(
                interactionSource = null,
                indication = ripple(bounded = false, radius = 24.dp),
                role = Role.Button,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.offset(x = 10.dp, y = (-15).dp)
                .size(28.dp)
                .shadow(elevation = 3.dp, shape = CircleShape)
                .background(HideButtonColor, CircleShape)
                .border(1.5.dp, white, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = white,
            )
        }
    }
}

@Composable
internal fun WarehouseCard.localizedWarehouseLabel(): String = stringResource(visuals().title)

private data class WarehouseCardVisuals(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @StringRes val subtitle: Int? = null,
    val gradient: List<Color>? = null,
    val isPrimary: Boolean = false,
    val iconTint: Color = if (isPrimary) white else marengo,
    val iconBackground: Color = if (gradient != null) white.copy(alpha = 0.22f) else gray_6,
) {
    val foreground: Color get() = if (isPrimary) white else black_2
    val shadowColor: Color get() = gradient?.last() ?: black_2
}

private fun WarehouseCard.visuals(): WarehouseCardVisuals = when (this) {
    WarehouseCard.CALENDAR -> WarehouseCardVisuals(
        R.string.warehouse_cards_calendar, R.drawable.baseline_calendar_month_24,
    )
    WarehouseCard.PRODUCTION -> WarehouseCardVisuals(
        R.string.add_screen_title2, R.drawable.icon_add_product,
        R.string.warehouse_count_positions,
        gradient = listOf(Color(0xFF1DBD59), Color(0xFF059669)),
        isPrimary = true,
    )
    WarehouseCard.FEED -> WarehouseCardVisuals(
        R.string.warehouse_cards_feed, R.drawable.wheat_24dp_000000_fill0_wght400_grad0_opsz24,
        R.string.warehouse_count_feed_entries,
        gradient = WarehouseFeedGradient,
        isPrimary = true,
    )
    WarehouseCard.GOODS -> WarehouseCardVisuals(
        R.string.warehouse_screen_products, R.drawable.baseline_shopping_basket_24,
        R.string.warehouse_count_in_stock,
        gradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
        isPrimary = true,
    )
    WarehouseCard.RECYCLING -> WarehouseCardVisuals(
        R.string.warehouse_cards_recycling, R.drawable.baseline_autorenew_24,
        R.string.warehouse_count_in_progress,
        gradient = listOf(Color(0xFF8855F4), Color(0xFF6825DB)),
        isPrimary = true,
    )
    WarehouseCard.TASKS -> WarehouseCardVisuals(
        R.string.warehouse_cards_tasks, R.drawable.outline_check_circle_24,
    )
    WarehouseCard.NOTES -> WarehouseCardVisuals(
        R.string.warehouse_cards_notes, R.drawable.baseline_sticky_note_2_24,
        R.string.warehouse_count_notes,
        gradient = listOf(Color(0xFFFFE58A), Color(0xFFFFCB16)),
        iconTint = Color(0xFF795900),
        iconBackground = white.copy(alpha = 0.38f),
    )
}

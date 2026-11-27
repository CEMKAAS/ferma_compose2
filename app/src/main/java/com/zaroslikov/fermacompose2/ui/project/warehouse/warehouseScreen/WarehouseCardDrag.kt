package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.LayoutDirection
import com.zaroslikov.domain.models.enums.WarehouseCard

@Stable
internal class WarehouseCardDrag(
    private val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
) {
    var viewport: LayoutCoordinates? = null
    val coordinates = mutableMapOf<WarehouseCard, LayoutCoordinates>()
    var source by mutableStateOf<WarehouseCard?>(null)
        private set
    var target by mutableStateOf<WarehouseCard?>(null)
        private set
    var pointer by mutableStateOf(Offset.Zero)
        private set
    var sourceBounds = Rect.Zero
        private set
    private var grab = Offset.Zero
    private var moved = false
    private var drop: WarehouseCardDrop? = null

    val ghostOffset: Offset get() = pointer - grab

    fun start(card: WarehouseCard, localPosition: Offset): Boolean {
        if (source != null) return false
        val bounds = boundsFor(card) ?: return false
        if (!Rect(Offset.Zero, bounds.size).contains(localPosition)) return false
        sourceBounds = bounds
        grab = localPosition
        pointer = bounds.topLeft + grab
        source = card
        target = card
        return true
    }

    fun move(card: WarehouseCard, localPosition: Offset, layout: WarehouseCardLayout) {
        val bounds = boundsFor(card) ?: return
        moved = true
        pointer = bounds.topLeft + localPosition
        updateTarget(layout)
    }

    fun updateTarget(layout: WarehouseCardLayout) {
        val area = viewportBounds()
        val sourceCard = source
        if (!moved || sourceCard == null || area == null || !area.contains(pointer)) {
            drop = null
            target = sourceCard.takeUnless { moved }
            return
        }

        val calendar = if (layout.hasCalendar) boundsFor(WarehouseCard.CALENDAR) else null
        if (calendar != null && pointer.y >= calendar.top && pointer.y < calendar.bottom) {
            drop = if (sourceCard == WarehouseCard.CALENDAR) null else {
                WarehouseCardDrop.Calendar(below = pointer.y >= calendar.center.y)
            }
            target = WarehouseCard.CALENDAR
            return
        }
        if (layout.hasCalendar && calendar == null) {
            drop = null
            target = null
            return
        }

        val section = if (calendar != null && pointer.y >= calendar.bottom) 1 else 0
        val columns = layout.sections[section]
        val physicalColumn = if (pointer.x < area.center.x) 0 else 1
        val column = if (layoutDirection == LayoutDirection.Ltr) physicalColumn else 1 - physicalColumn
        val cards = columns[column]
        val index = insertionIndex(cards)
        val otherIndex = if (sourceCard == WarehouseCard.CALENDAR) insertionIndex(columns[1 - column]) else index
        drop = WarehouseCardDrop.Column(section, column, index, otherIndex)
        target = cards.getOrNull(index) ?: cards.lastOrNull()
    }

    fun autoScrollStep(edge: Float, maxStep: Float): Float {
        val area = viewportBounds() ?: return 0f
        if (!moved || !area.contains(pointer) || area.height <= edge * 2f) return 0f
        return when {
            pointer.y < edge -> -maxStep * (edge - pointer.y) / edge
            pointer.y > area.bottom - edge -> maxStep * (pointer.y - area.bottom + edge) / edge
            else -> 0f
        }
    }

    fun finish(layout: WarehouseCardLayout): WarehouseCardLayout {
        val card = source
        val destination = drop
        clear()
        return if (card == null || destination == null) layout else layout.move(card, destination)
    }

    fun clear() {
        source = null
        target = null
        drop = null
        moved = false
    }

    private fun insertionIndex(cards: List<WarehouseCard>): Int = cards.indexOfFirst { card ->
        boundsFor(card)?.let { pointer.y < it.center.y } == true
    }.takeIf { it >= 0 } ?: cards.size

    private fun viewportBounds(): Rect? = viewport?.takeIf { it.isAttached }?.let {
        Rect(Offset.Zero, Size(it.size.width.toFloat(), it.size.height.toFloat()))
    }

    private fun boundsFor(card: WarehouseCard): Rect? {
        val parent = viewport?.takeIf { it.isAttached } ?: return null
        val child = coordinates[card]?.takeIf { it.isAttached } ?: return null
        return Rect(
            parent.localPositionOf(child, Offset.Zero),
            Size(child.size.width.toFloat(), child.size.height.toFloat()),
        )
    }
}

@Composable
internal fun warehouseCardDragSource(
    card: WarehouseCard,
    layout: WarehouseCardLayout,
    drag: WarehouseCardDrag,
    enabled: Boolean,
    editing: Boolean,
    onActivate: () -> Unit,
    onMove: (WarehouseCardLayout) -> Unit,
): Modifier {
    val currentLayout by rememberUpdatedState(layout)
    val currentEnabled by rememberUpdatedState(enabled)
    val currentEditing by rememberUpdatedState(editing)
    val currentOnActivate by rememberUpdatedState(onActivate)
    val currentOnMove by rememberUpdatedState(onMove)
    val haptic = LocalHapticFeedback.current
    return Modifier
        .onGloballyPositioned { drag.coordinates[card] = it }
        .pointerInput(card, drag, haptic) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                if (!currentEnabled) return@awaitEachGesture
                val startedInEditMode = currentEditing
                val start = if (startedInEditMode) {
                    awaitTouchSlopOrCancellation(down.id) { change, _ -> change.consume() }
                } else {
                    awaitLongPressOrCancellation(down.id)
                } ?: return@awaitEachGesture
                val grabPosition = if (startedInEditMode) down.position else start.position
                if (!currentEnabled || !drag.start(card, grabPosition)) return@awaitEachGesture

                try {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (startedInEditMode) {
                        drag.move(card, start.position, currentLayout)
                    } else {
                        currentOnActivate()
                    }
                    // После удержания забираем движение и отпускание до клика и прокрутки.
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == start.id } ?: break
                        if (!currentEnabled || drag.source != card || change.isConsumed) break
                        change.consume()
                        if (change.position != change.previousPosition) {
                            drag.move(card, change.position, currentLayout)
                        }
                        if (!change.pressed) {
                            drag.updateTarget(currentLayout)
                            val updated = drag.finish(currentLayout)
                            if (updated != currentLayout) currentOnMove(updated)
                            break
                        }
                    }
                } finally {
                    if (drag.source == card) drag.clear()
                }
            }
        }
}

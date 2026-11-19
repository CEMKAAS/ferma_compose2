package com.zaroslikov.todo.ui.list

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.PinnableContainer
import androidx.compose.ui.layout.LocalPinnableContainer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

// Перенос удержанием — общий для обоих видов экрана списка и «Моих списков» главного экрана:
// источник (строка, заголовок, карточка) ловит долгий тап, контейнер следит за пальцем
// и рисует «призрак», экран считает цель и бросок

/** Ширина полосы у края, где включается автопрокрутка, и её наибольший шаг за кадр. */
internal val TODO_AUTO_SCROLL_EDGE = 64.dp
internal val TODO_AUTO_SCROLL_STEP = 14.dp

/**
 * Состояние переноса одного экрана; [I] — что переносится. Палец ([pointer]) — в координатах
 * контейнера ([todoDragContainer]); цель ([target]) пересчитывает экран каждый кадр.
 */
@Stable
internal class TodoDragState<I : Any> {
    var item: I? by mutableStateOf(null)
        private set
    var target: TodoDropTarget? by mutableStateOf(null)
    var pointer: Offset by mutableStateOf(Offset.Zero)
        internal set

    /** Палец относительно левого верхнего угла переносимого элемента. */
    var grab: Offset = Offset.Zero
        private set
    var size: IntSize by mutableStateOf(IntSize.Zero)
        private set

    /** Где элемент стоял до переноса: бросок туда же ничего не меняет. */
    private var origin: TodoDropTarget? = null
    internal var container: LayoutCoordinates? = null

    /** Задаёт экран: где элемент стоит сейчас (null — переносить нельзя), начало и бросок. */
    var originOf: (I) -> TodoDropTarget? = { null }
    var onStart: () -> Unit = {}

    /** Бросок на новое место (элемент, где стоял, куда упал); бросок на своё место сюда не приходит. */
    var onDrop: (item: I, origin: TodoDropTarget?, target: TodoDropTarget) -> Unit = { _, _, _ -> }

    val ghostOffset: Offset get() = pointer - grab
    val ghostCenter: Offset get() = ghostOffset + Offset(size.width / 2f, size.height / 2f)

    /**
     * Начинает перенос элемента [item] с границами [coordinates]; [travel] — сколько палец
     * прошёл после долгого тапа: «призрак» держится за точку, где палец был при удержании.
     * false — не вышло.
     */
    fun start(item: I, coordinates: LayoutCoordinates, travel: Offset): Boolean {
        val container = container
        if (this.item != null || container == null || !container.isAttached || !coordinates.isAttached) return false
        val origin = originOf(item) ?: return false
        onStart()
        grab = pointer - travel - container.localPositionOf(coordinates, Offset.Zero)
        size = coordinates.size
        this.origin = origin
        target = origin
        this.item = item
        return true
    }

    /** Палец отпущен: бросок на текущую цель. */
    fun drop() {
        val item = item
        val target = target
        if (item != null && target != null && target != origin) onDrop(item, origin, target)
        cancel()
    }

    fun cancel() {
        item = null
        target = null
        origin = null
    }
}

/** Контейнер переноса: координаты для «призрака» и палец — до того, как его заберут дети. */
internal fun Modifier.todoDragContainer(drag: TodoDragState<*>): Modifier = this
    .onGloballyPositioned { drag.container = it }
    .pointerInput(drag) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                event.changes.firstOrNull()?.let { drag.pointer = it.position }
            }
        }
    }

private class TodoCoordinatesHolder {
    var value: LayoutCoordinates? = null
}

/**
 * Источник переноса: долгий тап (с тактильной отдачей) и отпускание без движения — [onLongClick]
 * (меню), сдвиг пальца после долгого тапа — перенос [item]. Пока идёт перенос, события пальца
 * забираются на проходе Initial: обычный тап, свайп и прокрутка их уже не видят. Обычный тап
 * и свайп без удержания работают как прежде.
 */
@Composable
internal fun <I : Any> rememberTodoDragSource(
    drag: TodoDragState<I>,
    item: I,
    enabled: Boolean,
    onLongClick: (() -> Unit)? = null,
    longClickLabel: String? = null
): Modifier {
    val haptic = LocalHapticFeedback.current
    val coordinates = remember { TodoCoordinatesHolder() }
    val currentItem by rememberUpdatedState(item)
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    if (!enabled) return Modifier
    return Modifier
        .onGloballyPositioned { coordinates.value = it }
        .then(
            if (onLongClick == null) Modifier
            else Modifier.semantics {
                onLongClick(label = longClickLabel) {
                    currentOnLongClick?.invoke()
                    true
                }
            }
        )
        .pointerInput(drag) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val longPress = awaitLongPressOrCancellation(down.id) ?: return@awaitEachGesture
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                var dragging = false
                var travel = Offset.Zero
                try {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == longPress.id } ?: break
                        if (!change.pressed) {
                            change.consume()
                            if (dragging) {
                                dragging = false
                                drag.drop()
                            } else currentOnLongClick?.invoke()
                            break
                        }
                        if (!dragging) {
                            travel += change.positionChange()
                            if (travel.getDistance() > viewConfiguration.touchSlop) {
                                val placed = coordinates.value
                                dragging = placed != null && drag.start(currentItem, placed, travel)
                                // Не вышло — палец отдаётся остальным
                                if (!dragging) break
                            }
                        }
                        change.consume()
                    }
                } finally {
                    // Источник ушёл из композиции или жест отменён — без броска
                    if (dragging) drag.cancel()
                }
            }
        }
}

/**
 * Элемент Lazy-списка, который сейчас переносится ([active]), остаётся в композиции, даже уйдя
 * за край при прокрутке: иначе жест переноса оборвётся.
 */
@Composable
internal fun TodoPinWhile(active: Boolean) {
    val pinnable: PinnableContainer? = LocalPinnableContainer.current
    DisposableEffect(active, pinnable) {
        val handle = if (active) pinnable?.pin() else null
        onDispose { handle?.release() }
    }
}

/**
 * Перед перестановкой элементов: прокрутка остаётся на том же месте по номеру элемента. Иначе
 * Lazy-список держится за ключ первого видимого элемента и уезжает вслед за ним, если переносят его.
 */
internal fun LazyListState.keepScrollPosition() {
    requestScrollToItem(firstVisibleItemIndex, firstVisibleItemScrollOffset)
}

/** Шаг автопрокрутки у края: отрицательный — к началу; 0 — палец не у края. */
internal fun todoAutoScrollStep(position: Float, start: Float, end: Float, edge: Float, maxStep: Float): Float = when {
    end - start < edge * 2 -> 0f
    position < start + edge -> -maxStep * ((start + edge - position) / edge).coerceAtMost(1f)
    position > end - edge -> maxStep * ((position - (end - edge)) / edge).coerceAtMost(1f)
    else -> 0f
}

/**
 * «Призрак» под пальцем: приподнятая копия переносимого элемента с тенью; [tilt] — слегка
 * повёрнута и увеличена (карточки и столбцы). [width] — ширина, по умолчанию как у элемента.
 */
@Composable
internal fun TodoDragGhost(
    drag: TodoDragState<*>,
    tilt: Boolean,
    width: Dp? = null,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    Box(
        Modifier
            .offset { drag.ghostOffset.round() }
            .width(width ?: with(density) { drag.size.width.toDp() })
            .graphicsLayer {
                shadowElevation = 12.dp.toPx()
                shape = RoundedCornerShape(14.dp)
                clip = true
                alpha = 0.96f
                if (tilt) {
                    rotationZ = 2.5f
                    scaleX = 1.04f
                    scaleY = 1.04f
                }
            }
    ) { content() }
}

package com.zaroslikov.todo.ui.task_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned

internal class TodoAssigneeFieldFocus(val clearFocus: () -> Unit) {
    var boundsInWindow: Rect? = null
    var isFocused: Boolean = false
}

internal class TodoAssigneeFocusRegistry {
    var field: TodoAssigneeFieldFocus? = null
    var coordinates: LayoutCoordinates? = null
}

internal val LocalTodoAssigneeFocus = staticCompositionLocalOf<TodoAssigneeFocusRegistry?> { null }

/** Наблюдает внешние тапы всей шторки, не забирая жесты у её полей, кнопок и прокрутки. */
@Composable
internal fun TodoAssigneeFocusHost(content: @Composable (Modifier) -> Unit) {
    val registry = remember { TodoAssigneeFocusRegistry() }
    val modifier = Modifier.onGloballyPositioned { registry.coordinates = it }.pointerInput(registry) {
        awaitPointerEventScope {
            var outsidePointer: PointerId? = null
            var downPosition = Offset.Zero
            var tappedField: TodoAssigneeFieldFocus? = null
            while (true) {
                val initial = awaitPointerEvent(PointerEventPass.Initial)
                initial.changes.firstOrNull { it.changedToDownIgnoreConsumed() }?.let { down ->
                    val field = registry.field
                    val root = registry.coordinates?.takeIf { it.isAttached }
                    val bounds = field?.boundsInWindow
                    if (field?.isFocused == true && root != null && bounds != null &&
                        !bounds.contains(root.localToWindow(down.position))) {
                        outsidePointer = down.id
                        downPosition = down.position
                        tappedField = field
                    }
                }
                val final = awaitPointerEvent(PointerEventPass.Final)
                final.changes.firstOrNull { it.id == outsidePointer }?.let { change ->
                    if ((change.position - downPosition).getDistance() > viewConfiguration.touchSlop) {
                        outsidePointer = null
                        tappedField = null
                    } else if (change.changedToUpIgnoreConsumed()) {
                        // Другое поле уже могло получить фокус — тогда его не сбрасываем.
                        if (registry.field === tappedField && tappedField?.isFocused == true) tappedField.clearFocus()
                        outsidePointer = null
                        tappedField = null
                    }
                }
            }
        }
    }
    CompositionLocalProvider(LocalTodoAssigneeFocus provides registry) { content(modifier) }
}

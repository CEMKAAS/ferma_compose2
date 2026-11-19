package com.zaroslikov.todo.ui.base

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private class TodoUndoVisuals(
    override val message: String,
    override val actionLabel: String
) : SnackbarVisuals {
    // Один таймер в хосте одновременно обновляет счётчик и закрывает сообщение.
    override val duration = SnackbarDuration.Indefinite
    override val withDismissAction = false
}

suspend fun SnackbarHostState.showTodoMessage(context: Context, event: TodoUiEvent.Message) {
    val message = context.getString(event.resId)
    val result = if (event.onUndo != null) {
        showSnackbar(TodoUndoVisuals(message, context.getString(R.string.todo_undo_delete)))
    } else {
        showSnackbar(message, duration = SnackbarDuration.Short)
    }
    if (result == SnackbarResult.ActionPerformed) event.onUndo?.invoke()
}

/** Общий хост: отсчёт времени есть только у сообщений с отменой удаления. */
@Composable
fun TodoSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        if (data.visuals is TodoUndoVisuals) TodoUndoSnackbar(data) else Snackbar(data)
    }
}

@Composable
private fun TodoUndoSnackbar(data: SnackbarData) {
    val accessibility = LocalAccessibilityManager.current
    val timeout = remember(data, accessibility) {
        accessibility?.calculateRecommendedTimeoutMillis(
            originalTimeoutMillis = 10_000L,
            containsIcons = true,
            containsText = true,
            containsControls = true
        ) ?: 10_000L
    }
    var remainingMillis by remember(data) { mutableLongStateOf(timeout) }
    LaunchedEffect(data) {
        if (timeout != Long.MAX_VALUE) {
            val start = withFrameMillis { it }
            while (remainingMillis > 0) {
                // Пауза не держит Compose в постоянной перерисовке между изменениями счётчика.
                delay(minOf(remainingMillis, 100L).milliseconds)
                val elapsed = withFrameMillis { it } - start
                remainingMillis = (timeout - elapsed).coerceAtLeast(0L)
            }
            data.dismiss()
        }
    }
    val progress = if (timeout == Long.MAX_VALUE) 1f
    else (remainingMillis.toFloat() / timeout).coerceIn(0f, 1f)
    Surface(
        shape = SnackbarDefaults.shape,
        color = SnackbarDefaults.color,
        contentColor = SnackbarDefaults.contentColor,
        shadowElevation = 6.dp
    ) {
        ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(data.visuals.message, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.matchParentSize(),
                    color = SnackbarDefaults.actionColor,
                    trackColor = Color.Transparent,
                    strokeWidth = 2.dp
                )
                Text(
                    if (timeout == Long.MAX_VALUE) stringResource(R.string.todo_snackbar_unlimited)
                    else stringResource(
                        R.string.todo_snackbar_seconds,
                        (remainingMillis + 999) / 1000
                    ),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.width(8.dp))
            TextButton(
                onClick = data::performAction,
                colors = ButtonDefaults.textButtonColors(contentColor = SnackbarDefaults.actionColor)
            ) { Text(data.visuals.actionLabel.orEmpty()) }
        }
        }
    }
}

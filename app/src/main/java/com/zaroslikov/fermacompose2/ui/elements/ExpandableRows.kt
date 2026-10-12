package com.zaroslikov.fermacompose2.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1

/** Сколько строк показываем до кнопки «Показать все». */
const val COLLAPSED_ROWS = 3

/** Длительность раскрытия/сворачивания списка. */
const val EXPAND_MS = 250

/**
 * Первые [collapsedCount] строк, остальные — по кнопке «Показать все (N)» / «Свернуть».
 * [key] сбрасывает раскрытие (например, при смене выбранного вида продукции).
 */
@Composable
fun <T> ExpandableRows(
    items: List<T>,
    key: Any? = null,
    spacing: Dp = 8.dp,
    collapsedCount: Int = COLLAPSED_ROWS,
    accent: Color = animal_1,
    row: @Composable (Int, T) -> Unit
) {
    var expanded by remember(key) { mutableStateOf(false) }
    // Стрелка плавно разворачивается вслед за списком
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrow")
    // Без spacedBy снаружи: отступ перед скрытыми строками живёт внутри анимации,
    // иначе он пропадает рывком в последний кадр сворачивания
    Column {
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            items.take(collapsedCount).forEachIndexed { index, item -> row(index, item) }
        }
        // Остальные строки выезжают вниз с проявлением и так же сворачиваются
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(EXPAND_MS)) + fadeIn(tween(EXPAND_MS)),
            exit = shrinkVertically(animationSpec = tween(EXPAND_MS)) + fadeOut(tween(EXPAND_MS))
        ) {
            Column(
                modifier = Modifier.padding(top = spacing),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                items.drop(collapsedCount).forEachIndexed { index, item -> row(index + collapsedCount, item) }
            }
        }
        if (items.size > collapsedCount)
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (expanded) stringResource(R.string.button_wrap)
                        else stringResource(R.string.animal_card_screen_show_product_all).format(items.size),
                        style = text_14,
                        color = accent
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }
            }
    }
}

package com.zaroslikov.crm.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.gray_6
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.grey_2
import com.zaroslikov.crm.ui.theme.textBold_14
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.crmCompact
import com.zaroslikov.crm.util.crmFormat
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow

private const val Y_STEPS = 4

/**
 * «Красивый» шаг сетки оси Y: 1, 2, 2.5, 5 × 10ⁿ, чтобы [steps] шагов покрывали [maxValue].
 */
internal fun crmNiceStep(maxValue: Float, steps: Int = Y_STEPS): Float {
    if (maxValue <= 0f || steps <= 0) return 1f
    val raw = maxValue.toDouble() / steps
    val magnitude = 10.0.pow(floor(log10(raw)))
    val normalized = raw / magnitude
    val nice = when {
        normalized <= 1.0 -> 1.0
        normalized <= 2.0 -> 2.0
        normalized <= 2.5 -> 2.5
        normalized <= 5.0 -> 5.0
        else -> 10.0
    }
    return (nice * magnitude).toFloat()
}

// Область построения: слева место под подписи Y, снизу — под подписи X
private fun Density.chartArea(width: Float, height: Float): Rect = Rect(
    left = 48.dp.toPx(),
    top = 8.dp.toPx(),
    right = width - 8.dp.toPx(),
    bottom = height - 22.dp.toPx()
)

private fun Rect.slotCenter(index: Int, count: Int): Float =
    left + width / count * (index + 0.5f)

private fun Rect.indexAt(x: Float, count: Int): Int =
    ((x - left) / (width / count)).toInt().coerceIn(0, count - 1)

private fun DrawScope.drawYGrid(
    area: Rect,
    step: Float,
    textMeasurer: TextMeasurer,
    style: TextStyle
) {
    val top = step * Y_STEPS
    for (i in 0..Y_STEPS) {
        val value = step * i
        val y = area.bottom - value / top * area.height
        drawLine(
            color = grey_2,
            start = Offset(area.left, y),
            end = Offset(area.right, y),
            strokeWidth = 1.dp.toPx()
        )
        val layout = textMeasurer.measure(value.toDouble().crmCompact(), style)
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                x = area.left - 6.dp.toPx() - layout.size.width,
                y = y - layout.size.height / 2f
            )
        )
    }
}

private fun DrawScope.drawXLabels(
    area: Rect,
    labels: List<String>,
    count: Int,
    textMeasurer: TextMeasurer,
    style: TextStyle
) {
    if (count == 0) return
    val layouts = labels.take(count).map { textMeasurer.measure(it, style) }
    val maxWidth = layouts.maxOfOrNull { it.size.width } ?: 0
    val slot = area.width / count
    // Если подписи не влезают — рисуем каждую n-ю
    val every = ceil((maxWidth + 4.dp.toPx()) / slot).toInt().coerceAtLeast(1)
    layouts.forEachIndexed { i, layout ->
        if (i % every != 0) return@forEachIndexed
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                x = area.slotCenter(i, count) - layout.size.width / 2f,
                y = area.bottom + 6.dp.toPx()
            )
        )
    }
}

private fun DrawScope.drawTooltip(
    anchor: Offset,
    text: String,
    textMeasurer: TextMeasurer,
    style: TextStyle
) {
    val layout = textMeasurer.measure(text, style)
    val padH = 8.dp.toPx()
    val padV = 4.dp.toPx()
    val w = layout.size.width + padH * 2
    val h = layout.size.height + padV * 2
    val left = (anchor.x - w / 2).coerceIn(0f, (size.width - w).coerceAtLeast(0f))
    val top = (anchor.y - h - 10.dp.toPx()).coerceAtLeast(0f)
    drawRoundRect(
        color = black_2,
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = CornerRadius(8.dp.toPx())
    )
    drawText(textLayoutResult = layout, topLeft = Offset(left + padH, top + padV))
}

private fun tooltipText(label: String?, value: Float, suffix: String): String {
    val amount = (value.toDouble().crmFormat() + " " + suffix).trim()
    return if (label.isNullOrEmpty()) amount else "$label: $amount"
}

/**
 * Линейный график с градиентной заливкой и тултипом по тапу (порт `CustomLineChart` из :app).
 * [labels] — подписи оси X, по одной на значение.
 */
@Composable
fun CrmLineChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    color: Color = green_6,
    suffix: String = "",
    height: Dp = 200.dp
) {
    val textMeasurer = rememberTextMeasurer()
    val axisStyle = TextStyle(fontSize = 10.sp, color = gray_7)
    val tooltipStyle = TextStyle(fontSize = 12.sp, color = white, fontWeight = FontWeight.SemiBold)
    var selected by remember(values) { mutableStateOf<Int?>(null) }
    val step = crmNiceStep(values.maxOrNull() ?: 0f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(values) {
                detectTapGestures { tap ->
                    if (values.isEmpty()) return@detectTapGestures
                    val area = chartArea(size.width.toFloat(), size.height.toFloat())
                    val index = area.indexAt(tap.x, values.size)
                    selected = if (selected == index) null else index
                }
            }
    ) {
        val area = chartArea(size.width, size.height)
        drawYGrid(area, step, textMeasurer, axisStyle)
        if (values.isEmpty()) return@Canvas
        val top = step * Y_STEPS

        val points = values.mapIndexed { i, v ->
            Offset(area.slotCenter(i, values.size), area.bottom - v.coerceAtLeast(0f) / top * area.height)
        }

        // Плавная кривая: контрольные точки на середине по X между соседними точками
        fun Path.curve() {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val cur = points[i]
                val midX = (prev.x + cur.x) / 2f
                cubicTo(midX, prev.y, midX, cur.y, cur.x, cur.y)
            }
        }
        val line = Path().apply { curve() }
        val fill = Path().apply {
            curve()
            lineTo(points.last().x, area.bottom)
            lineTo(points.first().x, area.bottom)
            close()
        }
        drawPath(
            path = fill,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
                startY = area.top,
                endY = area.bottom
            )
        )
        drawPath(
            path = line,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        selected?.takeIf { it in points.indices }?.let { idx ->
            drawLine(
                color = color.copy(alpha = 0.5f),
                start = Offset(points[idx].x, area.top),
                end = Offset(points[idx].x, area.bottom),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
        }
        points.forEachIndexed { i, point ->
            val radius = if (i == selected) 6.dp.toPx() else 4.dp.toPx()
            drawCircle(color = white, radius = radius, center = point)
            drawCircle(color = color, radius = radius, center = point, style = Stroke(2.dp.toPx()))
        }

        drawXLabels(area, labels, values.size, textMeasurer, axisStyle)

        selected?.takeIf { it in points.indices }?.let { idx ->
            drawTooltip(
                anchor = points[idx],
                text = tooltipText(labels.getOrNull(idx), values[idx], suffix),
                textMeasurer = textMeasurer,
                style = tooltipStyle
            )
        }
    }
}

/**
 * Столбчатый график с тултипом по тапу. [highlightIndex] — столбец, выделенный цветом
 * (например, текущий месяц); остальные тогда рисуются приглушённо.
 */
@Composable
fun CrmBarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    color: Color = green_6,
    suffix: String = "",
    height: Dp = 200.dp,
    highlightIndex: Int? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val axisStyle = TextStyle(fontSize = 10.sp, color = gray_7)
    val tooltipStyle = TextStyle(fontSize = 12.sp, color = white, fontWeight = FontWeight.SemiBold)
    var selected by remember(values) { mutableStateOf<Int?>(null) }
    val step = crmNiceStep(values.maxOrNull() ?: 0f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(values) {
                detectTapGestures { tap ->
                    if (values.isEmpty()) return@detectTapGestures
                    val area = chartArea(size.width.toFloat(), size.height.toFloat())
                    val index = area.indexAt(tap.x, values.size)
                    selected = if (selected == index) null else index
                }
            }
    ) {
        val area = chartArea(size.width, size.height)
        drawYGrid(area, step, textMeasurer, axisStyle)
        if (values.isEmpty()) return@Canvas
        val top = step * Y_STEPS
        val slot = area.width / values.size
        val barWidth = min(slot * 0.6f, 28.dp.toPx())
        val radius = CornerRadius(6.dp.toPx())

        values.forEachIndexed { i, v ->
            val barTop = area.bottom - v.coerceAtLeast(0f) / top * area.height
            if (area.bottom - barTop < 0.5f) return@forEachIndexed
            val centerX = area.slotCenter(i, values.size)
            val accent = selected == i || highlightIndex == null || highlightIndex == i
            drawPath(
                path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(centerX - barWidth / 2, barTop, centerX + barWidth / 2, area.bottom),
                            topLeft = radius,
                            topRight = radius
                        )
                    )
                },
                color = if (accent) color else color.copy(alpha = 0.45f)
            )
        }

        drawXLabels(area, labels, values.size, textMeasurer, axisStyle)

        selected?.takeIf { it in values.indices }?.let { idx ->
            drawTooltip(
                anchor = Offset(
                    area.slotCenter(idx, values.size),
                    area.bottom - values[idx].coerceAtLeast(0f) / top * area.height
                ),
                text = tooltipText(labels.getOrNull(idx), values[idx], suffix),
                textMeasurer = textMeasurer,
                style = tooltipStyle
            )
        }
    }
}

/** Строка рейтинга: подпись, значение и полоса доли [fraction] (0..1). */
@Composable
fun CrmHorizontalBar(
    label: String,
    valueText: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "crmHorizontalBar"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = text_14,
                color = black_2,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = valueText, style = textBold_14, color = black_2, maxLines = 1)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(gray_6)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

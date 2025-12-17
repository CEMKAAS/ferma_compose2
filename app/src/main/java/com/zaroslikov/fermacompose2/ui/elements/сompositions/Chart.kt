package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.math.roundToInt


@Composable
fun CustomLineChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF2ECC71),
    pointColor: Color = Color(0xFF2ECC71),
    fillColor: Color = Color(0xFF2ECC71).copy(alpha = 0.25f),
    ySteps: Int = 4,
    labelWidthDp: Dp = 36.dp, // место слева под Y-метки,
    suffix: String
) {
    /* require(values.isNotEmpty()) { "values must not be empty" }*/
    val maxValue = (values.maxOrNull() ?: 0f).coerceAtLeast(1f)
    val minValue = 0f

    var selectedPoint by remember { mutableStateOf<Int?>(null) }

    // Контейнер: Canvas занимает всю ширину, но внутри Canvas мы оставляем левый отступ
    Box(modifier = modifier) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .pointerInput(values) {
                    detectTapGestures { tapOffset ->
                        // расчёт — нужно учитывать левый отступ, который мы зарезервировали
                        val labelWidthPx = labelWidthDp.toPx()
                        val chartLeft = labelWidthPx
                        val chartWidth = size.width - labelWidthPx
                        if (chartWidth <= 0f) return@detectTapGestures

                        val stepX = chartWidth / (values.size - 1).coerceAtLeast(1)
                        val relativeX = tapOffset.x - chartLeft
                        val index = (relativeX / stepX).roundToInt().coerceIn(0, values.size - 1)
                        // проверим, что тап близко к точке (не где попало)
                        val pointX = chartLeft + index * stepX
                        val yRatio = size.height / (maxValue - minValue)
                        val pointY = size.height - (values[index] - minValue) * yRatio
                        val distance = kotlin.math.hypot(
                            (tapOffset.x - pointX).toDouble(),
                            (tapOffset.y - pointY).toDouble()
                        )
                        val touchThreshold = 24.dp.toPx() // допустимая дистанция для срабатывания
                        selectedPoint = if (distance <= touchThreshold) index else null
                    }
                }
        ) {

            // размеры
            val labelWidthPx = labelWidthDp.toPx()
            val chartLeft = labelWidthPx
            val chartRight = size.width
            val chartWidth = chartRight - chartLeft
            val chartHeight = size.height

            // Защита на случай одного значения
            val stepsCount = (values.size - 1).coerceAtLeast(1)
            val stepX = chartWidth / stepsCount
            val yRatio = if (maxValue - minValue == 0f) 0f else chartHeight / (maxValue - minValue)

            // ---- 1) Рисуем Y сетку и метки (они совпадают по координатам) ----
            fun valueToY(v: Float): Float =
                chartHeight - (v - minValue) / (maxValue - minValue) * chartHeight

            for (i in 0..ySteps) {
                val v = minValue + (maxValue - minValue) / ySteps * i
                val y = valueToY(v)
                // линия сетки (отступаем от левой метки)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    start = Offset(chartLeft, y),
                    end = Offset(chartRight, y),
                    strokeWidth = 1.dp.toPx()
                )

                // текст метки слева — рисуем с nativeCanvas для лучшего контроля
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        textSize = 12.sp.toPx()
                        color = android.graphics.Color.GRAY
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }

                    val x = chartLeft - 8.dp.toPx()
                    val yPos = y - (paint.descent() + paint.ascent()) / 2f

                    drawText(v.toInt().toString() + " " + suffix, x, yPos, paint)
                }
            }

            // ---- 2) Рассчитываем точки
            val points = values.mapIndexed { i, v ->
                val x = chartLeft + stepX * i
                val y = chartHeight - (v - minValue) * yRatio
                Offset(x, y)
            }

            // ---- 3) Строим путь линии и fillPath
            val linePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
            }

            val fillPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                    val last = points.last()
                    lineTo(last.x, chartHeight)
                    lineTo(points.first().x, chartHeight)
                    close()
                }
            }

            // ---- 4) Рисуем заполнение, линию и точки
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(fillColor, Color.Transparent),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            points.forEachIndexed { index, point ->
                drawCircle(color = pointColor, radius = 6.dp.toPx(), center = point)
            }

            // ---- 5) X-подписи под графиком: рисуем их с nativeCanvas, равномерно по chartWidth
            val labelPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                textSize = 8.sp.toPx()
                color = android.graphics.Color.GRAY
                textAlign = android.graphics.Paint.Align.CENTER
            }
            labels.forEachIndexed { i, txt ->
                val x = chartLeft + stepX * i
                val y = chartHeight + 16.dp.toPx() // немного ниже графика
                drawContext.canvas.nativeCanvas.drawText(txt, x, y, labelPaint)
            }

            // ---- 6) Tooltip при выборе точки
            selectedPoint?.let { idx ->
                if (idx in points.indices) {
                    val pt = points[idx]
                    // фон тултипа
                    val tooltipText = values[idx].toDouble().formatNumber() + " " + suffix
                    val padding = 6.dp.toPx()
                    val paint = android.graphics.Paint().apply {
                        isAntiAlias = true
                        textSize = 12.sp.toPx()
                        color = android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val textWidth = paint.measureText(tooltipText)
                    val rectW = textWidth + padding * 2
                    val rectH = (paint.descent() - paint.ascent()) + padding * 2

                    val rectLeft =
                        (pt.x - rectW / 2).coerceIn(4.dp.toPx(), size.width - rectW - 4.dp.toPx())
                    val rectTop = (pt.y - rectH - 12.dp.toPx()).coerceAtLeast(4.dp.toPx())

                    // рисуем прямоугольник с закруглением
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(rectLeft, rectTop),
                        size = androidx.compose.ui.geometry.Size(rectW, rectH),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                    // обводка
                    drawRoundRect(
                        color = Color.Gray,
                        topLeft = Offset(rectLeft, rectTop),
                        size = androidx.compose.ui.geometry.Size(rectW, rectH),
                        cornerRadius = CornerRadius(6.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // текст в rect (nativeCanvas для удобства)
                    drawContext.canvas.nativeCanvas.drawText(
                        tooltipText,
                        rectLeft + rectW / 2,
                        rectTop + padding - paint.ascent(),
                        paint
                    )
                }
            }
        }
    }
}


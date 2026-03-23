package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.gray_6

@Composable
fun BaseSlider(
    modifier: Modifier = Modifier,
    percentFloat: Float,
    color: Color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(gray_6.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentFloat)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

@Composable
fun SliderGradient(
    modifier: Modifier = Modifier,
    percentFloat: Float,
    colors: List<Color>
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(gray_6)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentFloat)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .drawBehind {
                    val gradient = Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f) // диагональ
                    )
                    drawRoundRect(
                        brush = gradient,
                        cornerRadius = CornerRadius(50.dp.toPx()) // скругление здесь
                    )
                })
    }
}
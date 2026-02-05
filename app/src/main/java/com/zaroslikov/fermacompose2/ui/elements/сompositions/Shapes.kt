package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleShape(
    color: Color,
    size: Dp = 12.dp,
) {
    Box(
        modifier = Modifier
            .background(color = color, shape = CircleShape)
            .size(size)
    )
}

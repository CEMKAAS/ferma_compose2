package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_4

@Composable
fun IconDone() {
    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(20.dp))
}


@Composable
fun IconIndicatorsAnimal(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int, colors: List<Color>
) {
    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    Box(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp))
            .size(40.dp)
            .background(brush = gradient, shape = RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun IconFinance(
    @DrawableRes icon: Int, color: Color,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(color = color, shape = RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
        )
    }
}


@Composable
fun IconTransaction(
    @DrawableRes icon: Int, colorIcon: Color, color: Color,
    sizeCard: Dp = 36.dp, sizeIcon: Dp = 20.dp
) {
    Box(
        modifier = Modifier
            .size(sizeCard)
            .background(color = color, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = colorIcon,
            modifier = Modifier
                .size(sizeIcon)
        )
    }
}


@Composable
fun IconText(
    number: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(color = green_4, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(number, color = green_2, style = text_12)
    }
}
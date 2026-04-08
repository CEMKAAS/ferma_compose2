package com.zaroslikov.fermacompose2.ui.elements


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.zaroslikov.fermacompose2.white
import java.io.File

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
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
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
            modifier = Modifier.size(20.dp)
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
            .background(color = color, shape = RoundedCornerShape(10.dp)), //TODO 14
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
fun IconTransaction2(
    modifier: Modifier = Modifier,
    sizeCard: Dp = 40.dp,
    @DrawableRes icon: Int, iconColor: Color, boxColor: Color
) {
    Box(
        modifier = modifier
            .size(sizeCard)
            .background(color = boxColor, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(sizeCard / 2)
        )
    }
}

@Composable
fun IconGradient(
    modifier: Modifier = Modifier,
    sizeCard: Dp = 40.dp,
    shape: Shape = RoundedCornerShape(10.dp),
    @DrawableRes icon: Int, colorIcon: Color, colors: List<Color>
) {
    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    Box(
        modifier = modifier
            .size(sizeCard)
            .background(brush = gradient, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = colorIcon,
            modifier = Modifier
                .size(sizeCard / 2)
        )
    }
}

@Composable
fun IconTransaction2(
    modifier: Modifier = Modifier,
    sizeCard: Dp = 40.dp,
    imagePath: String?,
    currentIcon: Int,
    color: Color,
) {
    val painter = when {
        imagePath != null -> rememberAsyncImagePainter(File(imagePath))
        else -> painterResource(currentIcon)
    }
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .size(sizeCard)
            .clip(shape)
            .background(color = color, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .then(
                    if (imagePath == null) Modifier.size(sizeCard * 0.75f) else Modifier
                )
        )
    }
}

@Composable
fun IconTransaction2(
    modifier: Modifier = Modifier,
    sizeCard: Dp = 40.dp,
    image: ImageBitmap,
    color: Color,
    isPainter: Boolean
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .size(sizeCard)
            .clip(shape)
            .background(color = color, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .background(color = color, shape = shape)
                .then(
                    if (!isPainter) Modifier.size(sizeCard / 2) else Modifier
                )
        )
    }
}


@Composable
fun IconText(
    number: String,
    colorBackground: Color,
    colorText: Color,
    sizeCard: Dp = 24.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(sizeCard)
            .background(color = colorBackground, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(number, color = colorText, style = text_12)
    }
}


@Composable
fun IconCircleText(
    number: String,
    colorBackground: Color,
    colorText: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color = colorBackground, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(number, color = colorText, style = text_16)
    }
}

@Composable
fun IconCircle(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    colorBackground: Color,
    colorIcon: Color = white,
    sizeBox: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(sizeBox)
            .background(color = colorBackground, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = colorIcon,
            modifier = Modifier.size(sizeBox / 2)
        )
    }
}
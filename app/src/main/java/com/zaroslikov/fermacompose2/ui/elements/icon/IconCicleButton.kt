package com.zaroslikov.fermacompose2.ui.elements.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.black_1

@Composable
fun IconCircleButton(
    @DrawableRes iconRes: Int,
    iconColor: Color = Color.Black,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(78.dp)
            .background(
                Color.White.copy(alpha = 0.25f),
                CircleShape
            ),
        onClick = onClick
    ) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(42.dp)
        )
    }
}
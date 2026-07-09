package com.zaroslikov.fermacompose2.ui.elements.icon


import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.ui.elements.BorderCard

@Composable
fun IconBorder(
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconSize: Dp,
    borderColor: Color,
    containerColor: Color,
) {
    BorderCard(
        shape = RoundedCornerShape(16.dp),
        containerColor = containerColor,
        borderColor = borderColor,
        elevation = 1.dp
    ) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconColor
        )
    }
}
package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2


@Composable
fun IconTitle(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    sizeIcon: Dp,
    colorIcon: Color,
    colorBackgroundIcon: Color,
    textTitle: String,
    styleTitle: androidx.compose.ui.text.TextStyle,
    colorTitle: Color,
    textSup: String,
    styleSup: androidx.compose.ui.text.TextStyle,
    colorSup: Color
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconTransaction2(
            sizeCard = sizeIcon,
            icon = iconRes,
            iconColor = colorIcon,
            boxColor = colorBackgroundIcon
        )
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = textTitle,
                style = styleTitle,
                color = colorTitle
            )
            Text(textSup, style = styleSup, color = colorSup)
        }
    }
}
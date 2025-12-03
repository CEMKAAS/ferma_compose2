package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ui.elements.text_16

@Composable
fun HeadingAnimalCard(
    @DrawableRes icon: Int,
    @StringRes intRes: Int
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(icon), contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF6A7282)
        )
        Text(
            text = stringResource(intRes),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = text_16,
            color = dark
        )
    }
}
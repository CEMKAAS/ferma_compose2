package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TitleAndText(
    modifier: Modifier = Modifier,
    @StringRes intString: Int,
    valueString: String
) {
    Row(
        modifier = modifier
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = modifier.padding(end = 3.dp),
            text = stringResource(intString),
        )
        Text(
            text = valueString,
        )
    }

}

@Composable
fun IconAndText(
    modifier: Modifier = Modifier,
    iconRes: Int,
    valueString: String
) {
    Row(
        modifier = modifier
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes), contentDescription = null,
            modifier = Modifier.padding(end = 5.dp)
        )
        Text(
            text = valueString,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
fun TextLine(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = textBold_20,
    valueString: String
) {
    Text(
        modifier = modifier
            .padding(bottom = 5.dp),
        text = valueString,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis

    )
}


package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


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
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(iconRes), contentDescription = null,
            modifier = Modifier.padding(end = 5.dp))
        Text(
            text = valueString,
        )
    }

}

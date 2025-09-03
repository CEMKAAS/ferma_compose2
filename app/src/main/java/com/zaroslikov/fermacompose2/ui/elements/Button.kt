package com.zaroslikov.fermacompose2.ui.elements


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R

@Composable
fun ButtonRefresh(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_create_24),
            contentDescription = stringResource(R.string.button_refresh)
        )
        Text(text = stringResource(R.string.button_refresh))
    }
}

@Composable
fun ButtonArchive(
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick, modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_archive_24),
            contentDescription = stringResource(R.string.button_archive)
        )
        Text(text = stringResource(R.string.button_archive))
    }
}


@Composable
fun ButtonDelete(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.toButton(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_delete_24),
            contentDescription = stringResource(R.string.button_delete)
        )
        Text(text = stringResource(R.string.button_delete))
    }
}

@Composable
fun ButtonStandart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes intRes: Int
) {
    Button(
        onClick = onClick, modifier = modifier.toButton()
    ) {
        Text(text = stringResource(intRes))
    }
}

@Composable
fun ButtonCustom(
    modifier: Modifier = Modifier,
    @StringRes intRes: Int,
    drawableRes: Int? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick, modifier = modifier.toButton()
    ) {
        drawableRes?.let {
            Icon(
                modifier = Modifier.padding(end = 3.dp),
                painter = painterResource(it),
                contentDescription = stringResource(intRes)
            )
        }
        Text(text = stringResource(intRes))
    }
}
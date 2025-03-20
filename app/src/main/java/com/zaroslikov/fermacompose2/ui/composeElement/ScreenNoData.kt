package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R

@Composable
fun CircularProgress(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MessageNoData(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    @StringRes supportRes: Int,
    @StringRes buttonRes: Int,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(titleRes),
            style = text_20_center,
            modifier = Modifier.toOutlinedText()
        )
        Text(
            text = stringResource(messageRes),
            style = text_20_justify,
            modifier = Modifier.toOutlinedText(),
            )
        Text(
            text = stringResource(supportRes),
            style = text_20_center,
            modifier = Modifier.toOutlinedText()
        )
        ButtonStandart(onClick = onClick, intRes = buttonRes)
    }
}
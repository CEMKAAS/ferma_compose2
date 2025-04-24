package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
    onClick: (() -> Unit)? = null
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
        if (onClick !=null)
        ButtonStandart(onClick = onClick, intRes = buttonRes)
    }
}
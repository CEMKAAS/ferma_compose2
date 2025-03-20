package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R


fun Modifier.toOutlinedText(): Modifier {
    return this
        .fillMaxWidth()
        .padding(bottom = 10.dp)
}

fun Modifier.toButton(): Modifier {
    return this
        .fillMaxWidth()
}




@Composable
fun Modifier.modifierScreen(
    innerPadding: PaddingValues
): Modifier {
    return this
        .fillMaxSize()
        .padding(innerPadding)
        .padding(
            horizontal = dimensionResource(id = R.dimen.padding_medium),
            vertical = dimensionResource(R.dimen.padding_small)
        )
}
package com.zaroslikov.fermacompose2.ui.elements

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R


fun Modifier.toOutlinedText(): Modifier {
    return this
        .fillMaxWidth()
        .padding(bottom = 5.dp)
}

fun Modifier.toButton(): Modifier {
    return this
        .fillMaxWidth()
}


@Composable
fun Modifier.modifierScreen(
    innerPadding: PaddingValues,
    horizontalPaddingValues: Dp = dimensionResource(id = R.dimen.padding_medium)
): Modifier {
    val focusManager = LocalFocusManager.current
    return this
        .padding(top = innerPadding.calculateTopPadding())
        .padding(
            horizontal = horizontalPaddingValues,
//            vertical = dimensionResource(R.dimen.padding_small)
        )
        .verticalScroll(rememberScrollState())
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus() // Убираем фокус при тапе в любую область
            })
        }
        .fillMaxHeight()
}

@Composable
fun Modifier.modifierScreenLazy(
    innerPadding: PaddingValues
): Modifier {
//    val focusManager = LocalFocusManager.current
    return this
        .padding(top = innerPadding.calculateTopPadding())
        .padding(
            horizontal = dimensionResource(id = R.dimen.padding_medium),
            vertical = dimensionResource(R.dimen.padding_small)
        )
        .fillMaxHeight()
//        .pointerInput(Unit) {
//            detectTapGestures(onTap = {
//                focusManager.clearFocus() // Убираем фокус при тапе в любую область
//            })
//        }
}

@Composable
fun Modifier.modifierDialogScreen(isScroll: Boolean): Modifier {
    val focusManager = LocalFocusManager.current
    return this
        .then(
            if (isScroll)
                Modifier.verticalScroll(rememberScrollState())
            else Modifier
        )
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus() // Убираем фокус при тапе в любую область
            })
        }
}

@Composable
fun Modifier.modifierBottomSheet(isScroll: Boolean): Modifier {
    val focusManager = LocalFocusManager.current
    return this
        .then(
            if (isScroll)
                Modifier.verticalScroll(rememberScrollState())
            else Modifier
        )
        .padding(
            horizontal = dimensionResource(id = R.dimen.padding_medium)
        )
        .padding(
            top = dimensionResource(R.dimen.padding_small),
//            bottom = 100.dp
        )
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus() // Убираем фокус при тапе в любую область
            })
        }

}
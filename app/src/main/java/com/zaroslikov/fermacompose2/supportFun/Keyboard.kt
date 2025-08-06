package com.zaroslikov.fermacompose2.supportFun

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

fun keyboardOptionsNext(): KeyboardOptions {
    return KeyboardOptions(
        imeAction = ImeAction.Next,
        capitalization = KeyboardCapitalization.Sentences // Первая буква будет заглавной
    )
}

fun keyboardOptionsGo(): KeyboardOptions {
    return KeyboardOptions(
        imeAction = ImeAction.Go,
        capitalization = KeyboardCapitalization.Sentences // Первая буква будет заглавной
    )
}

fun keyboardOptionsEnter(): KeyboardOptions {
    return KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Default,
        capitalization = KeyboardCapitalization.Sentences // Первая буква будет заглавной
    )
}


fun keyboardOptionsNextNumber(): KeyboardOptions {
    return KeyboardOptions(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Number,
        capitalization = KeyboardCapitalization.Sentences // Первая буква будет заглавной
    )
}


enum class KeyboardActionFocus {
    DOWN, RIGHT, CLEAN;

    fun toFocus(focusManager: FocusManager) = when (this) {
        DOWN -> keyboardActionsDown(focusManager)
        RIGHT -> keyboardActionsRight(focusManager)
        CLEAN -> keyboardActionsClear(focusManager)
    }
}


fun keyboardActionsDown(
    focusManager: FocusManager
): KeyboardActions {
    return KeyboardActions(onNext = {
        focusManager.moveFocus(
            FocusDirection.Down
        )
    })
}

fun keyboardActionsRight(
    focusManager: FocusManager
): KeyboardActions {
    return KeyboardActions(onNext = {
        focusManager.moveFocus(
            FocusDirection.Right
        )
    })
}

@OptIn(ExperimentalComposeUiApi::class)
fun keyboardActionsEnter(
): KeyboardActions {
    return KeyboardActions(
        onAny = { }
    )
}

fun keyboardActionsClear(
    focusManager: FocusManager
): KeyboardActions {
    return KeyboardActions(onNext = {
        focusManager.clearFocus()
    })
}

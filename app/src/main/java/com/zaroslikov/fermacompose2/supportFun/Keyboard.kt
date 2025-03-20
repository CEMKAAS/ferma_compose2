package com.zaroslikov.fermacompose2.supportFun

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

fun keyboardOptionsNext(): KeyboardOptions {
//    return KeyboardOptions(
//        imeAction = ImeAction.Next,
//        capitalization = KeyboardCapitalization.Sentences // Первая буква будет заглавной
//    )
    return KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next,
        capitalization = KeyboardCapitalization.Sentences
    )

}

fun keyboardOptionsGo(): KeyboardOptions {
    return KeyboardOptions(
        imeAction = ImeAction.Go,

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


fun keyboardActionsDown(
    focusManager: FocusManager
): KeyboardActions {
    return KeyboardActions(onNext = {
        focusManager.moveFocus(
            FocusDirection.Down
        )
    })
}

fun keyboardActionsClear(
    focusManager: FocusManager
): KeyboardActions {
    return KeyboardActions(onGo = {
        focusManager.clearFocus()
    })
}

package com.zaroslikov.fermacompose2.ui.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun WidgetNote(
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    textState: MutableState<String?>,
    onInputFinished: () -> Unit = {}
) {
    var isInFocus by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(96.dp)
            .border(
                width = when {
                    isInFocus -> 2.dp
                    else -> 1.dp
                },
                color = when {
                    isInFocus -> Color.DarkGray
                    else -> Color.Red
                },
                shape = RoundedCornerShape(8.dp)
            )
            .onFocusChanged {
                isInFocus = it.isFocused
                if (!it.isFocused) onInputFinished()
            },
        readOnly = readOnly,
        shape = RoundedCornerShape(8.dp),
        value = textState.value ?: "",
        onValueChange = {
            textState.value = it
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )

}

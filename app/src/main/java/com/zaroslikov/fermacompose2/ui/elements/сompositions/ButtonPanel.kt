package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.ButtonDelete
import com.zaroslikov.fermacompose2.ui.elements.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.elements.ButtonStandart
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.ExpensesEntryState

@Composable
fun ButtonPanel(
    isEntry: Boolean,
    isIndicatorsValue: Boolean = false,
    @StringRes entryButton: Int,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    if (isEntry)
        ButtonStandart(
            intRes = entryButton,
            onClick = {
                focusManager.clearFocus()
                onClickInsert()
            }
        )
    else {
        ButtonRefresh {
            focusManager.clearFocus()
            onClickUpdate()
        }
        if (!isIndicatorsValue)
            ButtonDelete { onClickDelete() }
    }
}

@Composable
private fun ButtonPanel2(
    state: ExpensesEntryState,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    if (state.isEntry)
        ButtonStandart(
            intRes = R.string.button_expenses,
            onClick = {
                focusManager.clearFocus()
                onClickInsert()
            }
        )
    else {
        ButtonRefresh {
            focusManager.clearFocus()
            onClickUpdate()
        }
        if (!state.isIndicatorsValue)
            ButtonDelete { onClickDelete() }
    }
}

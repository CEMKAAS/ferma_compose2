package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.ui.elements.ButtonDelete
import com.zaroslikov.fermacompose2.ui.elements.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.elements.ButtonStandart
import com.zaroslikov.fermacompose2.ui.elements.CloseButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.ExpensesEntryState
import com.zaroslikov.fermacompose2.white

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

@Composable
fun ButtonPanelNew(
    modifier: Modifier,
    isEntry: Boolean,
    enable: Boolean,
    colors: List<Color>,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickClose: () -> Unit
) {
    val (text, onClick) = if (isEntry) R.string.button_add to onClickInsert
    else R.string.button_save to onClickUpdate
    Box(modifier = modifier.background(Color.White)) {
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(1f),
            thickness = 1.dp,
            color = gray_6
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CloseButton(
                text = R.string.button_cancel,
                onClick = onClickClose,
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = stringResource(text),
                onClick = onClick,
                enable = enable,
                modifier = Modifier.weight(1f),
                colors = colors
            )
        }
    }
}

@Composable
fun ButtonPanelDetailNew(
    modifier: Modifier,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    Box(modifier = modifier.background(Color.White)) {
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .zIndex(1f),
            thickness = 1.dp,
            color = gray_6
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CloseButton(
                iconRes = R.drawable.icon_edit,
                text = R.string.button_edit,
                onClick = onClickUpdate,
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = stringResource(R.string.button_delete),
                iconRes = R.drawable.baseline_delete_24,
                onClick = onClickDelete,
                enable = false,
                modifier = Modifier.weight(1f),
                colors = listOf(error_base, error_base)
            )
        }
    }
}
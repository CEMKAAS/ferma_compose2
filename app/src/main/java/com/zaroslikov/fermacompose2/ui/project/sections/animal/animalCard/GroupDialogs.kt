package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextSexNew
import com.zaroslikov.fermacompose2.ui.elements.text_14

/** Выделение особей из обезличенного счётчика группы. */
@Composable
fun SplitCounterDialog(
    state: AnimalCardState,
    onIntent: (AnimalCardIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    AlertDialogStandard(
        titleRes = R.string.animal_group_split_title,
        iconRes = R.drawable.icon_group,
        titleBackgroundColor = green_15,
        onDismissRequest = { onIntent(AnimalCardIntent.OpenSplitDialog(false)) },
        onClick = {
            focusManager.clearFocus()
            onIntent(AnimalCardIntent.SplitConfirmed)
        },
        colors = listOf(animal_1, animal_2),
        enabled = state.canConfirmSplit,
        textButtonRes = R.string.animal_group_button_split
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.animal_group_split_count, state.splitMax.toString()),
                style = text_14,
                color = marengo
            )
            OutlinedTextCountNew(
                value = state.split.count,
                onValueChange = { onIntent(AnimalCardIntent.SplitCountChanged(it)) },
                suffix = null,
                isError = state.isSplitCountError,
                drawableRes = R.drawable.baseline_spoke_24,
                errorText = stringResource(R.string.animal_group_split_error, state.splitMax),
                isBorderCard = false
            )
            BaseOutlinedTextNew(
                value = state.split.prefix,
                onValueChange = { onIntent(AnimalCardIntent.SplitPrefixChanged(it)) },
                leadingIconRes = R.drawable.baseline_pets_24,
                labelIntRes = R.string.animal_group_split_prefix,
                intResSup = R.string.animal_group_split_prefix_hint,
                isNecessarily = true
            )
            OutlinedTextSexNew(
                value = state.split.sex,
                onValueChange = { onIntent(AnimalCardIntent.SplitSexChanged(it)) },
                isBorderCard = false
            )
            if (state.splitNames.isNotEmpty())
                Text(
                    text = stringResource(
                        R.string.animal_group_split_preview,
                        state.splitNames.joinToString(limit = PREVIEW_LIMIT)
                    ),
                    style = text_14,
                    color = marengo
                )
        }
    }
}

/** Подтверждение операции над группой: обезличить / расформировать / вывести особь. */
@Composable
fun GroupConfirmDialog(
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int = R.drawable.ic_herd,
    text: String,
    @StringRes textButtonRes: Int,
    enabled: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialogStandard(
        titleRes = titleRes,
        iconRes = iconRes,
        titleBackgroundColor = orang_4,
        onDismissRequest = onDismissRequest,
        onClick = onConfirm,
        colors = listOf(orang_9, orang_15),
        enabled = enabled,
        textButtonRes = textButtonRes
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = text,
            style = text_14,
            color = marengo
        )
    }
}

@Composable
fun GroupDialogs(
    state: AnimalCardState,
    onIntent: (AnimalCardIntent) -> Unit
) {
    if (state.split.isOpen)
        SplitCounterDialog(state = state, onIntent = onIntent)
    if (state.isOpenLeaveDialog)
        GroupConfirmDialog(
            titleRes = R.string.animal_group_button_leave,
            iconRes = R.drawable.ic_herd_leave,
            text = stringResource(R.string.animal_group_leave_text),
            textButtonRes = R.string.animal_group_button_leave,
            enabled = !state.isGroupOperationRunning,
            onDismissRequest = { onIntent(AnimalCardIntent.OpenLeaveDialog(false)) },
            onConfirm = { onIntent(AnimalCardIntent.LeaveConfirmed) }
        )
}

private const val PREVIEW_LIMIT = 5

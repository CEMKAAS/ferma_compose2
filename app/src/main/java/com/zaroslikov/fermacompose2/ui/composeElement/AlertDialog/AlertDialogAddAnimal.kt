package com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AddCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote


@Composable
fun AlertDialogAddAnimal(
    state: AnimalCardState.AddAnimal,
    onIntent: (AddCardIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val (titleText, titlePaint, titleButton) =
        if (state.price.isBlank())
            Triple(
                stringResource(R.string.alert_dialog_info_add_animals),
                R.drawable.baseline_add_circle_outline_24,
                stringResource(R.string.button_text_add)
            )
        else
            Triple(
                stringResource(R.string.alert_dialog_info_expenses_animals),
                R.drawable.baseline_add_shopping_cart_24,
                stringResource(R.string.button_text_expenses)
            )

    AlertDialogAni(
        icon = painterResource(titlePaint),
        title = titleText,
        titleButton = titleButton,
        onDismissClick = { onIntent(AddCardIntent.Exit) },
        content = {
            OutlinedTextCount(
                value = state.count,
                onValueChange = { onIntent(AddCardIntent.CountChanged(it)) },
                isError = state.isErrorCount,
                intRes = R.string.outlined_text_field_quantity,
                drawableRes = R.drawable.baseline_spoke_24,
                count = state.price,
                suffix = state.countSuffix,
                focusManager = focusManager
            )
            OutlinedPriceInput(
                price = state.price,
                onPriceChange = { onIntent(AddCardIntent.PriceChanged(it)) },
                count = state.priceAll,
                isAutoCalculate = state.isAutoPrice,
                onAutoCalculate = { onIntent(AddCardIntent.AutoPriceChanged(it)) },
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AddCardIntent.NoteChanged(it)) },
                label = R.string.outlined_text_reason,
                supportingText = R.string.support_text_add_animal_reason,
                cardBorder = false
            )
        },
        onConfirmationClick = { onIntent(AddCardIntent.SaveButton) }
    )
}
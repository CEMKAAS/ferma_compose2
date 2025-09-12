package com.zaroslikov.fermacompose2.ui.elements.AlertDialog


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote


@Composable
fun AlertDialogAddAnimal(
    state: AnimalCardState.CountAnimal,
    countAllAnimal: String,
    onIntent: (AnimalCardIntent) -> Unit
) {
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
    Log.i("AddAnimal", "AlertDialogAddAnimal: $state ")
    AlertDialogAni(
        icon = painterResource(titlePaint),
        title = titleText,
        titleButton = titleButton,
        onDismissClick = { onIntent(AnimalCardIntent.DialogAddClicked(false)) },
        content = {
            OutlinedTextCountAnimal2(
                value = state.countAnimal,
                onValueChange = {
                    onIntent(AnimalCardIntent.CountAddChanged(it))
                },
                countAnimalAll = countAllAnimal,
                suffix = state.suffixAnimal,
                isError = state.error.isErrorCount
            )
            OutlinedPriceInput(
                price = state.price,
                onPriceChange = { onIntent(AnimalCardIntent.PriceAddChanged(it)) },
                priceAll = state.priceAll,
                isManyCount = true,
                isAutoCalculate = state.isAutoPrice,
                onAutoCalculate = { onIntent(AnimalCardIntent.AutoPriceAddClicked(it)) },
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AnimalCardIntent.NoteAddChanged(it)) },
                label = R.string.outlined_text_reason,
                supportingText = R.string.support_text_add_animal_reason
            )
        },
        onConfirmationClick = { onIntent(AnimalCardIntent.SaveAddPressed) }
    )
}
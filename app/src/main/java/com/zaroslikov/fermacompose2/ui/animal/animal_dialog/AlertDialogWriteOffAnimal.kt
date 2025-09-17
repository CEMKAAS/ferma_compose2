package com.zaroslikov.fermacompose2.ui.animal.animal_dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogAni
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote

@Composable
fun AlertDialogWriteOffAnimal(
    state: AnimalCardState.CountAnimal,
    onIntent: (AnimalCardIntent) -> Unit,
    isAnimalGroup: Boolean,
    countAll: String,
    countSuffix: String,
) {
    AlertDialogAni(
        icon = painterResource(R.drawable.baseline_edit_note_24),
        title = stringResource(R.string.alert_dialog_info_write_off_animals),
        titleButton = stringResource(R.string.button_text_write_off),
        onDismissClick = { onIntent(AnimalCardIntent.DialogWriteOffClicked(false)) },
        content = {
            if (isAnimalGroup)
                OutlinedTextCountAnimal2(
                    value = state.countAnimal,
                    onValueChange = {
                        onIntent(AnimalCardIntent.CountWriteOffChanged(it))
                    },
                    isError = state.error.isErrorCount,
                    isErrorCountMore = state.error.isErrorCountMore,
                    countAnimalAll = countAll,
                    suffix = countSuffix,
                )
            OutlinedPriceInput(
                price = state.price,
                onPriceChange = {
                    onIntent(AnimalCardIntent.PriceWriteOffChanged(it))
                },
                priceAll = state.priceAll,
                isAutoCalculate = state.isAutoPrice,
                onAutoCalculate = {
                    onIntent(AnimalCardIntent.AutoPriceWriteOffClicked(it))
                },
                isManyCount = isAnimalGroup,
                supportTextRes = if (isAnimalGroup) R.string.support_text_price_animals else R.string.support_text_price_animal,
                supportTextResAutoCal = R.string.support_text_price_one_animals,
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = {
                    onIntent(AnimalCardIntent.NoteWriteOffChanged(it))
                },
                label = R.string.outlined_text_reason,
                supportingText = R.string.support_text_write_off_animal_reason,
            )
        },
        onConfirmationClick = { onIntent(AnimalCardIntent.SaveWriteOffPressed) }
    )
}
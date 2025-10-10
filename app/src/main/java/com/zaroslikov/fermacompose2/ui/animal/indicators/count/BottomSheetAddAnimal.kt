@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.CountBottomSheet2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote


@Composable
fun BottomSheetAddAnimal(
    state: DomainAnimalCountPrice,
    errorState: AnimalCountState.Error,
    isEntry: Boolean,
    countAllAnimal: String,
    onIntent: (AnimalCountIntent) -> Unit
) {
    CountBottomSheet2(
        version = state.version,
        isEntry = isEntry,
        intEntryButton = R.string.alert_dialog_info_add_animals,
        onDismiss = { onIntent(AnimalCountIntent.EndDialogClicked) },
        onInsert = { onIntent(AnimalCountIntent.InsertAddPressed) },
        onUpdate = { onIntent(AnimalCountIntent.UpdateAddPressed) },
        onDelete = { onIntent(AnimalCountIntent.DeleteCountPressed(state.id)) },
    ) {
        OutlinedTextCountAnimal2(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountAddChanged(it))
            },
            countAnimalAll = countAllAnimal,
            suffix = state.suffix,
            isError = errorState.isErrorCount
        )
        OutlinedTextDate(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) }
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onIntent(AnimalCountIntent.NoteChanged(it)) },
            label = R.string.outlined_text_reason,
            supportingText = R.string.support_text_add_animal_reason
        )
    }
}
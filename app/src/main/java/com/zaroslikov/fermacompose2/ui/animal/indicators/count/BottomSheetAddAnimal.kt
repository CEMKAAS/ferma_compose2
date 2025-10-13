@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.CountBottomSheet2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote


@SuppressLint("RememberInComposition")
@Composable
fun BottomSheetAddAnimal(
    state: DomainAnimalCountPrice,
    errorState: AnimalCountState.Error,
    isEntry: Boolean,
    countAllAnimal: String,
    onIntent: (AnimalCountIntent) -> Unit
) {

    val focusRequester =
        remember { FocusRequester() } // ✅ нужно помнить, иначе при recomposition фокус сбрасывается
    // ✅ Важно: просим фокус, когда bottom sheet появился
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
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
            isError = errorState.isErrorCount,
            modifier = Modifier.focusRequester(focusRequester)
        )
        OutlinedTextDate(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) }
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onIntent(AnimalCountIntent.NoteChanged(it)) }
        )
    }
}
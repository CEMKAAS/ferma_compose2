@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.project.sections.EntryIndicationBottomSheet


@SuppressLint("RememberInComposition")
@Composable
fun BottomSheetAddAnimal(
    state: CountItem,
    countAllAnimal: String,
    onIntent: (AnimalCountIntent) -> Unit
) {
    val focusRequester =
        remember { FocusRequester() } // ✅ нужно помнить, иначе при recomposition фокус сбрасывается
    // ✅ Важно: просим фокус, когда bottom sheet появился
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    EntryIndicationBottomSheet(
        icon = state.version.toDrawRes(),
        titleRes = state.version.toResId(),
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = state.version.toColorList(),
        onDismissRequest = { onIntent(AnimalCountIntent.EndDialogClicked) },
        onInsertClick = { onIntent(AnimalCountIntent.InsertAddPressed) },
        onUpdateClick = { onIntent(AnimalCountIntent.UpdateAddPressed) }
    ) {
        OutlinedTextCountAnimalNew(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountAddChanged(it))
            },
            suffix = state.suffix,
            isError = state.error.isErrorCount,
            /*isErrorCountZero = errorState.isErrorCountZero,*/
            modifier = Modifier.focusRequester(focusRequester),
            countAnimal = countAllAnimal
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) }
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AnimalCountIntent.NoteChanged(it)) }
        )
    }
}
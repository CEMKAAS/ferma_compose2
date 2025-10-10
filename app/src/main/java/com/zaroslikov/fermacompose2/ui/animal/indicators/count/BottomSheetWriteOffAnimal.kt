@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.CountBottomSheet2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.start.formatNumber

@Composable
fun BottomSheetWriteOffAnimal(
    state: DomainAnimalCountPrice,
    errorState: AnimalCountState.Error,
    isEntry: Boolean,
    isAnimalGroup: Boolean,
    isAutoPrice: Boolean,
    countAllAnimal: String,
    countSuffix: Suffix,
    onIntent: (AnimalCountIntent) -> Unit
) {
    CountBottomSheet2(
        version = state.version,
        isEntry = isEntry,
        intEntryButton = R.string.button_text_write_off,
        onDismiss = { onIntent(AnimalCountIntent.EndDialogClicked) },
        onInsert = { onIntent(AnimalCountIntent.InsertWriteOffPressed) },
        onUpdate = { onIntent(AnimalCountIntent.UpdateWriteOffPressed) },
        onDelete = { onIntent(AnimalCountIntent.DeleteCountPressed(state.id)) },
    ) {
        OutlinedTextCountAnimal2(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountChanged(it))
            },
            isError = errorState.isErrorCount,
            isErrorCountMore = errorState.isErrorCountMore,
            countAnimalAll = countAllAnimal,
            suffix = countSuffix,
        )
        OutlinedPriceInput(
            price = state.price?.formatNumber(false) ?: "",
            onPriceChange = {
                onIntent(AnimalCountIntent.PriceChanged(it))
            },
            priceAll = state.priceAll?.formatNumber(false) ?: "",
            isAutoCalculate = isAutoPrice,
            onAutoCalculate = {
                onIntent(AnimalCountIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            supportTextRes = if (isAnimalGroup) R.string.support_text_price_animals else R.string.support_text_price_animal,
            supportTextResAutoCal = R.string.support_text_price_one_animals,
        )
        OutlinedTextDate(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) },
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = {
                onIntent(AnimalCountIntent.NoteChanged(it))
            },
            label = R.string.outlined_text_reason,
            supportingText = R.string.support_text_write_off_animal_reason,
        )
    }
}
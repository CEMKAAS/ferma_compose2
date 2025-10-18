@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.elements.CountBottomSheet2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.start.formatNumber

@Composable
fun BottomSheetSaleAnimal(
    state: DomainAnimalCountPrice,
    errorState: AnimalCountState.Error,
    onIntent: (AnimalCountIntent) -> Unit,
    isEntry: Boolean,
    price: String,
    priceAll: String,
    isAutoPrice: Boolean,
    countAllAnimal: String,
    buyerList: List<String>,
) {
    CountBottomSheet2(
        version = state.version,
        isEntry = isEntry,
        intEntryButton = R.string.button_text_sale,
        onDismiss = { onIntent(AnimalCountIntent.EndDialogClicked) },
        onInsert = { onIntent(AnimalCountIntent.InsertSalePressed) },
        onUpdate = { onIntent(AnimalCountIntent.UpdateSalePressed) },
        onDelete = { onIntent(AnimalCountIntent.DeleteCountPressed(state.id)) },
    ) {
        OutlinedTextCountAnimal2(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountChanged(it))
            },
            isError = errorState.isErrorCount,
            isErrorCountZero = errorState.isErrorCountZero,
            intRes = R.string.outlined_text_field_quantity,
            countAnimalAll = countAllAnimal,
            suffix = state.suffix,
        )
        OutlinedPriceInput(
            price = price,
            onPriceChange = {
                onIntent(AnimalCountIntent.PriceChanged(it))
            },
            priceAll = priceAll,
            isAutoCalculate = isAutoPrice,
            onAutoCalculate = {
                onIntent(AnimalCountIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            isError = errorState.isErrorPrice,
            supportTextRes = R.string.support_text_price_animals,
            supportTextResAutoCal = R.string.support_text_price_one_animals,
            isNecessarily = true
        )
        OutlinedTextDate(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) }
        )
        OutlinedTextBuyer(
            value = state.buyer ?: "",
            onValueChange = {
                onIntent(AnimalCountIntent.BuyerSaleChanged(it))
            },
            list = buyerList,
        )
        OutlinedTextNote(
            value = state.note,
            onValueChange = { onIntent(AnimalCountIntent.NoteChanged(it)) }
        )
    }
}
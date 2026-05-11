@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.project.sections.EntryIndicationBottomSheet

@Composable
fun BottomSheetSaleAnimal(
    state: CountItem,
    onIntent: (AnimalCountIntent) -> Unit,
    countAllAnimal: String?,
    currencyPrice: Suffix,
) {
    val focusRequester =
        remember { FocusRequester() } // ✅ нужно помнить, иначе при recomposition фокус сбрасывается
    // ✅ Важно: просим фокус, когда bottom sheet появился
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    EntryIndicationBottomSheet(
        iconRes = state.version.toDrawRes(),
        titleRes = state.version.toResId(),
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = state.version.toColorList(),
        onDismissRequest = {
            onIntent(
                AnimalCountIntent.DialogClicked(
                    false,
                    isSaveStateForBottomSheet = state.isEntry,
                    version = state.version
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AnimalCountIntent.DialogClicked(false))
        },
        onInsertClick = { onIntent(AnimalCountIntent.InsertSalePressed) },
        onUpdateClick = { onIntent(AnimalCountIntent.UpdateSalePressed) }
    ) {
        OutlinedTextCountAnimalNew(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountChanged(it))
            },
            isError = state.error.isErrorCount,
            isErrorCountZero = state.error.isErrorCountZero,
            intRes = R.string.outlined_text_field_quantity,
            suffix = state.suffix,
            countAnimal = countAllAnimal,
        )
        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(AnimalCountIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isAutoCalculate = state.isAutoCalculate,
            onAutoCalculate = {
                onIntent(AnimalCountIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            isError = state.error.isErrorPrice,
            supportTextRes = R.string.support_text_price_animals,
            supportTextResAutoCal = R.string.support_text_price_one_animals,
            tooltipTextResAutoCal = R.string.tooltip_auto_calculate_animal,
            isNecessarily = true,
            count = state.count,
            countSuffix = state.suffix,
            priceSuffix = currencyPrice
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) }
        )
        OutlinedTextBuyerNew(
            value = state.buyer,
            onValueChange = {
                onIntent(AnimalCountIntent.BuyerSaleChanged(it))
            },
            list = state.buyerList,
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(AnimalCountIntent.NoteChanged(it)) }
        )
    }
}
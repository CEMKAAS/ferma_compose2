package com.zaroslikov.fermacompose2.ui.animal.animal_dialog


import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogAni
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2

@Composable
fun AlertDialogSaleAnimal(
    state: AnimalCardState.CountAnimal,
    onIntent: (AnimalCardIntent) -> Unit,
    isAnimalGroup: Boolean,
    countAll: String,
    countSuffix: String,
    buyerList: List<String>,
) {
    val textTitle =
        stringResource(if (isAnimalGroup) R.string.alert_dialog_info_sale_animals else R.string.alert_dialog_info_sale_animal)

    AlertDialogAni(
        icon = painterResource(R.drawable.baseline_add_card_24),
        title = textTitle,
        titleButton = stringResource(R.string.button_text_sale),
        onDismissClick = { onIntent(AnimalCardIntent.DialogSaleClicked(false)) },
        content = {
            if (isAnimalGroup)
                OutlinedTextCountAnimal2(
                    value = state.countAnimal,
                    onValueChange = {
                        onIntent(AnimalCardIntent.CountSaleChanged(it))
                    },
                    isError = state.error.isErrorCount,
                    isErrorCountMore = state.error.isErrorCountMore,
                    isErrorCountZero = state.error.isErrorCountZero,
                    intRes = R.string.outlined_text_field_quantity,
                    countAnimalAll = countAll,
                    suffix = countSuffix,
                )
            OutlinedPriceInput(
                price = state.price,
                onPriceChange = {
                    onIntent(AnimalCardIntent.PriceSaleChanged(it))
                },
                priceAll = state.priceAll,
                isAutoCalculate = state.isAutoPrice,
                onAutoCalculate = {
                    onIntent(AnimalCardIntent.AutoPriceSaleClicked(it))
                },
                isManyCount = isAnimalGroup,
                isError = state.error.isErrorPrice,
                supportTextRes = if (isAnimalGroup) R.string.support_text_price_animals else R.string.support_text_price_animal,
                supportTextResAutoCal = R.string.support_text_price_one_animals,
                isNecessarily = true
            )
            OutlinedTextBuyer(
                value = state.buyer,
                onValueChange = {
                    onIntent(AnimalCardIntent.BuyerSaleChanged(it))
                },
                list = buyerList,
            )
        },
        onConfirmationClick = { onIntent(AnimalCardIntent.SaveSalePressed) }
    )
}
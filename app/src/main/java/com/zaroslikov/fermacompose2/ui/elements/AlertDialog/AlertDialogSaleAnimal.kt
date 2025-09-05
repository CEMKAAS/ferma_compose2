package com.zaroslikov.fermacompose2.ui.elements.AlertDialog


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountDifference
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountIncrease
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAnimalSale
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.elements.textBold_16

@Composable
fun AlertDialogSaleAnimal(
    state: AnimalCardState.SaleAnimal,
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
                OutlinedTextCount(
                    value = state.countAnimal,
                    onValueChange = {
                        onIntent(AnimalCardIntent.CountSaleChanged(it))
                    },
                    isError = state.error.isErrorCount,
                    isErrorCountMore = state.error.isErrorCountMore,
                    isErrorCountZero = state.error.isErrorCountZero,
                    intRes = R.string.outlined_text_field_quantity,
                    drawableRes = R.drawable.baseline_spoke_24,
                    count = countAll,
                    suffix = countSuffix,
                    focusManager = focusManager
                )
            OutlinedPriceInput(
                price = state.price,
                onPriceChange = {
                    onIntent(AnimalCardIntent.PriceSaleChanged(it))
                },
                count = state.countAnimal,
                isAutoCalculate = state.isAutoPrice,
                onAutoCalculate = {
                    onIntent(AnimalCardIntent.AutoPriceSaleClicked(it))
                },
                isManyCount = state.isAutoPrice,
                isError = state.error.isErrorPrice,
                supportTextRes = if (isAnimalGroup) R.string.support_text_price_animals else R.string.support_text_price_animal,
                supportTextResAutoCal = R.string.support_text_price_one_animals,
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
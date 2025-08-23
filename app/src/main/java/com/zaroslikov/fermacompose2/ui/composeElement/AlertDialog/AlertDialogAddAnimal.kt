package com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog


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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAddAnimal
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertOnlyInt
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AddCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.animal.entry.updateIsAutoPrice
import com.zaroslikov.fermacompose2.ui.animal.entry.updatePrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.autoCalculate
import com.zaroslikov.fermacompose2.ui.composeElement.modifierDialogScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16


@Composable
fun AlertDialogAddAnimal(
    state: AnimalCardState.AddAnimal,
    onIntent: (AddCardIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var price by rememberSaveable { mutableStateOf("") }
    var priceAll by rememberSaveable { mutableStateOf("") }
    var countAnimal by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    val isAutoCalculate = rememberSaveable { mutableStateOf(false) }

    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val category = stringResource(R.string.animal_card_screen_add_category_expenses)

    val priceInDB = if (isAutoCalculate.value) priceAll else price

    val reasonNote = if ((priceInDB.isBlank() || priceInDB == "0")) {
        if (note == "") stringResource(R.string.animal_card_screen_add_no_note_reason)
        else stringResource(R.string.animal_card_screen_add_note_reason, note)
    } else ""

    val (titleText, titlePaint, titleButton) =
        if (price.isBlank() && (priceAll.isBlank() || priceAll == "0")) {
            Triple(
                stringResource(R.string.alert_dialog_info_add_animals),
                R.drawable.baseline_add_circle_outline_24,
                stringResource(R.string.button_text_add)
            )
        } else {
            Triple(
                stringResource(R.string.alert_dialog_info_expenses_animals),
                R.drawable.baseline_add_shopping_cart_24,
                stringResource(R.string.button_text_expenses)
            )
        }

    AlertDialogAni(
        icon = painterResource(titlePaint),
        title = titleText,
        titleButton = titleButton,
        onDismissClick = { onIntent(AddCardIntent.Exit) },
        content = {
            OutlinedTextCount(
                value = state.count,
                onValueChange = { onIntent(AddCardIntent.CountChanged(it)) },
                isError = state.isErrorCount,
                intRes = R.string.outlined_text_field_quantity,
                drawableRes = R.drawable.baseline_spoke_24,
                count = state.price,
                suffix = state.countSuffix,
                focusManager = focusManager
            )
            OutlinedPriceInput(
                price = state.price,
                onPriceChange = { onIntent(AddCardIntent.PriceChanged(it)) },
                count = state.priceAll,
                isAutoCalculate = state.isAutoPrice,
                onAutoCalculate = { onIntent(AddCardIntent.AutoPriceChanged(it)) },
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AddCardIntent.NoteChanged(it)) },
                label = R.string.outlined_text_reason,
                supportingText = R.string.support_text_add_animal_reason,
                cardBorder = false
            )
        },
        onConfirmationClick = { onIntent(AddCardIntent.SaveButton) }
    )
}
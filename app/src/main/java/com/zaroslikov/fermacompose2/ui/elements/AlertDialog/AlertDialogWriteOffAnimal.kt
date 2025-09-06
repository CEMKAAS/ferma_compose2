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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountIncrease
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorWriteOffAnimal
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertOnlyInt
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.elements.autoCalculate
import com.zaroslikov.fermacompose2.ui.elements.modifierDialogScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.sections.add.entry.AddEntryIntent

@Composable
fun AlertDialogWriteOffAnimal(
    state: AnimalCardState.WriteOffAnimal,
    onIntent: (AnimalCardIntent) -> Unit,
    isAnimalGroup: Boolean,
    countAll: String,
    countSuffix: String,
) {
    AlertDialogAni(
        icon = painterResource(R.drawable.baseline_edit_note_24),
        title = stringResource(R.string.alert_dialog_info_write_off_animals),
        titleButton = stringResource(R.string.button_text_write_off),
        onDismissClick = { onIntent(AnimalCardIntent.DialogSaleClicked(false)) },
        content = {
            Column(modifier = Modifier.modifierDialogScreen()) {
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
                    count = state.countAnimal,
                    isAutoCalculate = state.isAutoPrice,
                    onAutoCalculate = {
                        onIntent(AnimalCardIntent.AutoPriceWriteOffClicked(it))
                    },
                    isManyCount = state.isAutoPrice,
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
            }
        },
        onConfirmationClick = { onIntent(AnimalCardIntent.SaveWriteOffPressed) }
    )
}
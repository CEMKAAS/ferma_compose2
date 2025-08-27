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
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.autoCalculate
import com.zaroslikov.fermacompose2.ui.composeElement.modifierDialogScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16

@Composable
fun AlertDialogWriteOffAnimal(
    isAnimalGroup: Boolean,
    title: String,
    countAll: String,
    countSuffix: String,
    idPT: Int,
    idAnimal: Long,
    onConfirmation: () -> Unit,
    onSaveClick: (Triple<DomainIndicatorsVM, WriteOffTable?, Boolean>) -> Unit,
    onUpdateAnimalGroupClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    var price by rememberSaveable { mutableStateOf("") }
    var priceAll by rememberSaveable { mutableStateOf("") }
    var countAnimal by rememberSaveable { mutableStateOf(if (isAnimalGroup) "" else "1") }
    var note by rememberSaveable { mutableStateOf("") }

    val isAutoCalculate = rememberSaveable { mutableStateOf(false) }

    var openDialogGroup by rememberSaveable { mutableStateOf(false) }

    //Error
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorCountMore by rememberSaveable { mutableStateOf(false) }

    val priceInDB = if (isAutoCalculate.value) priceAll else price

    val reasonNote = if ((priceInDB.isBlank() || priceInDB == "0")) {
        if (note == "") stringResource(R.string.animal_card_screen_add_no_note_reason)
        else stringResource(R.string.animal_card_screen_add_note_reason, note)
    } else ""


    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.baseline_edit_note_24),
                contentDescription = stringResource(R.string.alert_dialog_info_write_off_animals)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.alert_dialog_info_write_off_animals),
                style = textBold_16
            )
        },
        text = {
            Column(modifier = Modifier.modifierDialogScreen()) {
                if (isAnimalGroup)
                    OutlinedTextCount(
                        value = countAnimal,
                        onValueChange = {
                            countAnimal = it
                            isErrorCount = it.isError()
                            isErrorCountMore = isAnimalCountIncrease(it, countAll)
                        },
                        isError = isErrorCount,
                        isErrorCountMore = isErrorCountMore,
                        intRes = R.string.outlined_text_field_quantity,
                        drawableRes = R.drawable.baseline_spoke_24,
                        count = countAll,
                        suffix = countSuffix,
                        focusManager = focusManager
                    )
                OutlinedTextPrice(
                    value = price,
                    onValueChange = {
                        price = it
                    },
                    intSupportText = R.string.support_text_price_one_animals,
                    focusManager = focusManager
                )
                if (isAnimalGroup)
                    priceAll = autoCalculate(
                        isAutoCalculate = isAutoCalculate,
                        count = countAnimal,
                        price = price
                    )
                OutlinedTextNote(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    label = R.string.outlined_text_reason,
                    supportingText = R.string.support_text_write_off_animal_reason,
                )
            }
        },
        onDismissRequest = onConfirmation,
        dismissButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(stringResource(R.string.button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    if (isErrorWriteOffAnimal(
                            count = countAnimal,
                            countAll = countAll,
                            isErrorCount = { isErrorCount = it },
                            isErrorCountAll = { isErrorCountMore = it },
                            isAnimalGroup = isAnimalGroup
                        )
                    ) {
                        val count =
                            (countAnimal.toConvertOnlyInt().toInt() - countAll.toConvertOnlyInt()
                                .toInt()).toString()
                                .toConvertOnlyInt()
                        onSaveClick(
                            Triple(
                                first = DomainIndicatorsVM(
//                                    weight = count,
                                    weight = countAnimal,
                                    suffix = countSuffix,
                                    date = dateToday(),
                                    idAnimal = idAnimal.toInt(),
                                    note = reasonNote,
                                    version = 3
                                ),
                                second = if (priceInDB.isBlank() || priceInDB == "0") null else {
                                    WriteOffTable(
                                        title = title,
                                        count = countAnimal.toConvertDbDouble(),
                                        day = dateTodayArray()[0],
                                        mount = dateTodayArray()[1],
                                        year = dateTodayArray()[2],
                                        status = true,
                                        priceAll = priceInDB.toConvertDbDouble(),
                                        countSuffix = countSuffix,
                                        note = reasonNote,
                                        idPT = idPT.toLong(),
                                        id = TODO(),
                                        price = TODO(),
                                        animalCountId = TODO()
                                    )
                                },
                                third = count.toInt() == 0
                            )
                        )
                        if (count.toInt() == 1) openDialogGroup = true
                        else onConfirmation()
                    }
                }
            ) {
                Text(stringResource(R.string.button_text_write_off))
            }
        }
    )
    if (openDialogGroup)
        AlertDialogGroupToSolo(
            onConfirmation = {
                openDialogGroup = !openDialogGroup
                onConfirmation()
            },
            onUpdateClick = onUpdateAnimalGroupClick
        )
}
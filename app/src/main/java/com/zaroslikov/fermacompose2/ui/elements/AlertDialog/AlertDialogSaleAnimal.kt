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
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.elements.textBold_16

@Composable
fun AlertDialogSaleAnimal(
    isAnimalGroup: Boolean,
    title: String,
    countAll: String,
    countSuffix: String,
    idAnimal: Long,
    idPT: Int,
    buyerList: List<String>,
    onConfirmation: () -> Unit,
    onUpdateAnimalGroupClick: (String) -> Unit,
    onSaveClick: (Triple<DomainIndicatorsVM, SaleTable, Boolean>) -> Unit
) {
    val focusManager = LocalFocusManager.current

    //Value
    var price by rememberSaveable { mutableStateOf("") }
    var buyer by rememberSaveable { mutableStateOf("") }
    var countAnimalSale by rememberSaveable { mutableStateOf(if (isAnimalGroup) "" else "1") }
    var isAutoCalculate by rememberSaveable { mutableStateOf(false) }

    //Error
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorCountMore by rememberSaveable { mutableStateOf(false) }
    var isErrorCountZero by rememberSaveable { mutableStateOf(false) }

    //OpenDialog
    var openDialogGroup by rememberSaveable { mutableStateOf(false) }

    //Text
    val note = stringResource(R.string.animal_card_screen_note_sale)
    val animalCategory = stringResource(R.string.animal_card_screen_category_sale)
    val textTitle =
        stringResource(if (isAnimalGroup) R.string.alert_dialog_info_sale_animals else R.string.alert_dialog_info_sale_animal)

    //SupportFun
    val finalPrice = if (isAutoCalculate && isAnimalGroup) calculatePriceAll(price, countAnimalSale)
    else price

    val finalBuyer =
        if (buyer == "") stringResource(R.string.animal_card_screen_sale_note_no_buyer) else buyer


    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.baseline_add_card_24),
                contentDescription = textTitle
            )
        },
        title = {
            Text(
                text = textTitle,
                style = textBold_16
            )
        },
        text = {
            Column {
                if (isAnimalGroup)
                    OutlinedTextCount(
                        value = countAnimalSale,
                        onValueChange = {
                            countAnimalSale = it
                            isErrorCount = it.isError()
                            isErrorCountMore = isAnimalCountIncrease(it, countAll)
                            isErrorCountZero = isAnimalCountZero(it)
                        },
                        isError = isErrorCount,
                        isErrorCountMore = isErrorCountMore,
                        isErrorCountZero = isErrorCountZero,
                        intRes = R.string.outlined_text_field_quantity,
                        drawableRes = R.drawable.baseline_spoke_24,
                        count = countAll,
                        suffix = countSuffix,
                        focusManager = focusManager
                    )
                OutlinedPriceInput(
                    price = price,
                    onPriceChange = {
                        price = it
                        isErrorPrice = it.isError()
                    },
                    count = countAnimalSale,
                    isAutoCalculate = isAutoCalculate,
                    onAutoCalculate = { isAutoCalculate = it },
                    isManyCount = isAnimalGroup,
                    isError = isErrorPrice,
                    supportTextRes = if (isAnimalGroup) R.string.support_text_price_animals else R.string.support_text_price_animal,
                    supportTextResAutoCal = R.string.support_text_price_one_animals,
                )
                OutlinedTextBuyer(
                    value = buyer,
                    onValueChange = { buyer = it },
                    list = buyerList,
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
                    if (isErrorAnimalSale(
                            title = finalPrice,
                            count = countAnimalSale,
                            countAll = countAll.toInt(),
                            isAnimalGroup = isAnimalGroup,
                            isErrorTitle = { isErrorPrice = it },
                            isErrorCount = { isErrorCount = it },
                            isErrorCountMore = { isErrorCountMore = it },
                            isErrorCountZero = { isErrorCountZero = it }
                        )
                    ) {
                        val count = if (isAnimalGroup) isAnimalCountDifference(
                            countAnimalSale,
                            countAll
                        ) else countAll

                        onSaveClick(
                            Triple(
                                first = DomainIndicatorsVM(
//                                    weight = count,
                                    weight = countAnimalSale,
                                    suffix = countSuffix,
                                    date = dateToday(),
                                    idAnimal = idAnimal.toInt(),
                                    note = "",
                                    version = 0
                                ),
                                second = SaleTable(
                                    title = title,
                                    count = countAnimalSale.toConvertDbDouble(),
                                    priceAll = finalPrice.toConvertDbDouble(),
                                    day = dateTodayArray()[0],
                                    mount = dateTodayArray()[1],
                                    year = dateTodayArray()[2],
                                    countSuffix = countSuffix,
                                    category = animalCategory,
                                    note = note,
                                    buyer = finalBuyer,
                                    idPT = idPT.toLong(),
                                    price = 0.0,

                                ),
                                third = if (isAnimalGroup) isAnimalCountZero(
                                    countAnimalSale,
                                    countAll
                                ) else true
                            )
                        )
                        if (count.toInt() == 1 && isAnimalGroup) openDialogGroup = true
                        else onConfirmation()
                    }
                }
            ) {
                Text(stringResource(R.string.button_text_sale))
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
package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAnimalSale
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.start.formatNumber


@Composable
fun AlertDialogInfo(
    onConfirmation: () -> Unit,
    @StringRes intTitleText: Int,
    @StringRes intText: Int,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Example Icon")
        },
        title = {
            Text(text = stringResource(intTitleText))
        },
        text = {
            Text(text = stringResource(intText), textAlign = TextAlign.Justify)
        },
        onDismissRequest = onConfirmation,
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(stringResource(R.string.button_text_great))
            }
        }
    )
}

@Composable
fun AlertDialogSaleAnimal(
    drawableRes: Int,
    isAnimalGroup: Boolean,
    title: String,
    countAll: Int,
    suffix: String,
    idPT: Int,
    buyerList: List<String>,
    onConfirmation: () -> Unit,
    onSaveClick: (Triple<SaleTable, AnimalCountTable, Boolean>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var priceAll by rememberSaveable { mutableStateOf("") }
    var buyer by rememberSaveable { mutableStateOf("") }
    var countSale by rememberSaveable { mutableStateOf("") }
    var isAutoCalculate by rememberSaveable { mutableStateOf(false) }

    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorCountMore by rememberSaveable { mutableStateOf(false) }


    val factoryPadding by animateDpAsState(
        if (!isAutoCalculate) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val animalCategory = stringResource(R.string.support_text_animal)

    AlertDialog(
        icon = {
            Icon(painterResource(drawableRes), contentDescription = "Example Icon")
        },
        title = {
            Text(text = stringResource(if (isAnimalGroup) R.string.alert_dialog_info_sale_animals else R.string.alert_dialog_info_sale_animal))
        },
        text = {
            Column {
                if (isAnimalGroup) {
                    OutlinedTextCount(
                        value = countSale,
                        onValueChange = {
                            countSale = it.toConvertOnlyInt()
                            isErrorCountMore = it.toConvertOnlyInt().toConvertZero() > countAll
                            isErrorCount = it.isError()
                        },
                        isError = isErrorCount,
                        isErrorCountMore = isErrorCountMore,
                        intRes = R.string.outlined_text_field_quantity,
                        drawableRes = R.drawable.baseline_spoke_24,
                        count = countAll,
                        suffix = suffix,
                        focusManager = focusManager
                    )
                }
                OutlinedTextPrice(
                    value = priceAll,
                    onValueChange = {
                        priceAll = it.toConvertDb()
                        isErrorPrice = it.isError()
                    },
                    isError = isErrorPrice,
                    intSupportText = if (isAnimalGroup) if (isAutoCalculate) R.string.support_text_price_one_animals else R.string.support_text_price_animals else R.string.support_text_price_animal,
                    focusManager = focusManager
                )
                if (isAnimalGroup) {
                    CardField(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .padding(bottom = factoryPadding.coerceAtLeast(0.dp))
                    ) {
                        Column {
                            CheckboxTextIcon(
                                modifier = if (isAutoCalculate) Modifier.toOutlinedText() else Modifier,
                                checked = isAutoCalculate,
                                onCheckedChange = {
                                    isAutoCalculate = it
                                },
                                intTitle = R.string.checkbox_auto_calculate,
                                isTooltipShow = true,
                                intTooltip = R.string.tooltip_auto_calculate_price
                            )
                            if (isAutoCalculate)
                                TextBuildAnnotated(
                                    intRes = R.string.support_text_all_price,
                                    priceAll = priceAll,
                                    count = countSale
                                )
                        }
                    }
                }
                OutlinedTextBuyer(
                    value = buyer,
                    onValueChange = { buyer = it },
                    list = buyerList,
                    focusManager = focusManager
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
                            title = priceAll,
                            count = countSale,
                            countAll = countAll,
                            isAnimalGroup = isAnimalGroup,
                            isErrorTitle = { isErrorPrice = it },
                            isErrorCount = { isErrorCount = it },
                            isErrorCountMore = { isErrorCountMore = it },
                        )
                    ) {
                        onSaveClick(
                            Triple(
                                first = SaleTable(
                                    id = 0,
                                    title = title,
                                    count = if (isAnimalGroup) countSale.toDouble() else 1.0,
                                    priceAll = if (isAutoCalculate) priceAll.toConvertZeroDouble() * countSale.toConvertZero() else priceAll.toConvertDbDouble(),
                                    day = dateTodayArray()[0],
                                    mount = dateTodayArray()[1],
                                    year = dateTodayArray()[2],
                                    suffix = suffix,
                                    category = animalCategory,
                                    note = "",
                                    buyer = buyer,
                                    idPT = idPT,
                                ),
                                second = AnimalCountTable(
                                    id = 0,
                                    count = if (isAnimalGroup) (countAll - countSale.toInt()).toString() else "0",
                                    suffix = suffix,
                                    date = dateToday(),
                                    idAnimal = idPT
                                ),
                                third = if (isAnimalGroup) (countAll - countSale.toInt()) == 0 else true
                            )
                        )
                        onConfirmation()
                    }
                }
            ) {
                Text(stringResource(R.string.button_text_sale))
            }
        }
    )
}

@Composable
fun AlertDialogKillAnimal(
    drawableRes: Int,
    isAnimalGroup: Boolean,
    title: String,
    countAll: Int,
    suffix: String,
    idPT: Int,
    countWarehouse: Double,
    titleList: List<String>,
    onConfirmation: () -> Unit,
    onUpdateCountWarehouse: (String) -> Unit,
    onSaveClick: (Triple<AddTable, AnimalCountTable, Boolean>) -> Unit
) {
    val focusManager = LocalFocusManager.current

    var countSale by rememberSaveable { mutableStateOf("") }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorCountMore by rememberSaveable { mutableStateOf(false) }


    val animalCategory = stringResource(R.string.support_text_animal)
    val textFields =
        remember { mutableStateListOf(KillTitleList(suffix = suffix)) }

    AlertDialog(
        icon = {
            Icon(painterResource(drawableRes), contentDescription = "Example Icon")
        },
        title = {
            Text(text = stringResource(if (isAnimalGroup) R.string.alert_dialog_info_sale_animals else R.string.alert_dialog_info_sale_animal))
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                if (textFields.size > 1) {
                    CardField {
                        Column {
                            Text(text = "Cводка:", textDecoration = TextDecoration.Underline)
                            textFields.forEachIndexed { index, text ->
                                TextAndIconRow(
                                    title = if (text.title == "") stringResource(R.string.support_text_empty) else text.title,
                                    value = "${
                                        text.count.toConvertZero().toString().toFormatNumber()
                                    } ${text.suffix}"
                                )
                            }
                        }
                    }
                }

                if (isAnimalGroup) {
                    OutlinedTextCount(
                        value = countSale,
                        onValueChange = {
                            countSale = it.toConvertOnlyInt()
                            isErrorCountMore = it.toConvertOnlyInt().toConvertZero() > countAll
                            isErrorCount = it.isError()
                        },
                        isError = isErrorCount,
                        isErrorCountMore = isErrorCountMore,
                        intRes = R.string.outlined_text_field_quantity,
                        drawableRes = R.drawable.baseline_spoke_24,
                        count = countAll,
                        suffix = suffix,
                        focusManager = focusManager
                    )
                }

                textFields.forEachIndexed { index, text ->
                    OutlinedTextTitleAdd(
                        value = text.title,
                        onValueChange = {
                            textFields[index] = textFields[index].copy(title = it.trim())
                            textFields[index] = textFields[index].copy(isError = it.isError())
                            textFields[index] =
                                textFields[index].copy(isErrorSlash = it.isErrorSlash())
                            onUpdateCountWarehouse(title)
                        },
                        titleList = titleList,
                        isErrorTitle = text.isError,
                        isErrorSlash = text.isErrorSlash,
                        focusManager = focusManager
                    )
                    OutlinedTextCount(
                        value = text.count,
                        onValueChange = {
                            textFields[index] = textFields[index].copy(count = it.toConvertDb())
                            textFields[index] = textFields[index].copy(isErrorCount = it.isError())
                        },
                        onClick = { textFields[index] = textFields[index].copy(suffix = it) },
                        isError = text.isErrorCount,
                        suffix = text.suffix,
                        intResSup = R.string.support_text_count_product,
                        countWarehouse = countWarehouse,
                        focusManager = focusManager
                    )
                }
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
            Row {
                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        if (!textFields.any { killTitle ->
                                killTitle.isError || killTitle.isErrorCount || killTitle.isErrorSlash // добавь все нужные булевы поля
                            }
                        ) onConfirmation()
//                        textFields.forEachIndexed { index, text ->
//                            if (isErrorAnimalSale(
//                                    title = "sd",
//                                    count = countSale,
//                                    countAll = countAll,
//                                    isAnimalGroup = isAnimalGroup,
//                                    isErrorTitle = { },
//                                    isErrorCount = { isErrorCount = it },
//                                    isErrorCountMore = { isErrorCountMore = it },
//                                )
//                            ) {
//                                onSaveClick(
//                                    Triple(
//                                        first = AddTable(
//                                            title = title,
//                                            count = 0.toDouble(),
//                                            day = dateTodayArray()[0],
//                                            mount = dateTodayArray()[1],
//                                            year = dateTodayArray()[2],
//                                            suffix = suffix,
//                                            category = "category",
//                                            idAnimal = idPT.toLong(),
//                                            animal = animalCategory,
//                                            note = "",
//                                            priceAll = 0.0,
//                                            idPT = idPT
//                                        ),
//                                        second = AnimalCountTable(
//                                            id = 0,
//                                            count = if (isAnimalGroup) (countAll - countSale.toInt()).toString() else "0",
//                                            suffix = suffix,
//                                            date = dateToday(),
//                                            idAnimal = idPT
//                                        ),
//                                        third = if (isAnimalGroup) (countAll - countSale.toInt()) == 0 else true
//                                    )
//                                )
//                            }

                    }
                ) {
                    Text(stringResource(R.string.button_text_add))
                }
                TextButton(
                    onClick = { textFields.add(KillTitleList(suffix = suffix)) }
                ) {
                    Text(stringResource(R.string.button_text_add_title))
                }
            }
        }
    )
}

data class KillTitleList(
    val title: String = "",
    val isError: Boolean = false,
    val isErrorSlash: Boolean = false,
    val count: String = "",
    val suffix: String = "",
    val isErrorCount: Boolean = false
)
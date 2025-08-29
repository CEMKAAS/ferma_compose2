package com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.dto.PairData
import com.zaroslikov.fermacompose2.supportFun.animalCountWeightComposition
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.infoTextKillAnimal
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountDifference
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountIncrease
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isAnimalWeightIncrease
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorKillAnimal
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd2
import com.zaroslikov.fermacompose2.ui.composeElement.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch

@Composable
fun AlertDialogKillAnimal(
    isAnimalGroup: Boolean,
    title: String,
    countAnimalAll: String,
    countSuffix: String,
    weight: String?,
    weightSuffix: String?,
    idPT: Int,
    idAnimal: Long,
    titleList: List<PairData>,
    onConfirmation: () -> Unit,
    onUpdateAnimalGroupClick: (String) -> Unit,
    onUpdateCountWarehouse: suspend (String) -> DomainPairDataDoubleSting,
    onSaveProductClick: (AddTable) -> Unit,
    onSaveCountClick: (Triple<DomainIndicatorsVM, WriteOffTable, Boolean>) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val noNullWeightSuffix = weightSuffix ?: stringResource(R.string.suffix_kilogram)

    //Value
    var countAnimal by rememberSaveable { mutableStateOf(if (isAnimalGroup) "" else "1") }
    val textFields =
        remember { mutableStateListOf(KillTitleList(suffix = noNullWeightSuffix)) }

    //Error
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorCountMore by rememberSaveable { mutableStateOf(false) }
    var isErrorCountZero by rememberSaveable { mutableStateOf(false) }

    //OpenDialog
    var openDialogGroup by rememberSaveable { mutableStateOf(false) }

    //Text
    val textTitle =
        stringResource(if (isAnimalGroup) R.string.alert_dialog_info_kill_animals else R.string.alert_dialog_info_kill_animal)
    val note = stringResource(R.string.animal_card_screen_note_kill)
    val animalCategory = stringResource(R.string.animal_card_screen_category_kill)

    val reasonNote = stringResource(
        R.string.animal_card_screen_kill_add_product,
    )

    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.icons8__meat60),
                contentDescription = "Meat Icon"
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
                CardTotal(
                    textFields = textFields,
                    countAnimal = countAnimal,
                    weight = weight,
                    weightSuffix = weightSuffix,
                    isAnimalGroup = isAnimalGroup
                )
                LazyColumn {
                    item {
                        if (isAnimalGroup)
                            OutlinedTextCount(
                                value = countAnimal,
                                onValueChange = {
                                    countAnimal = it
                                    isErrorCount = it.isError()
                                    isErrorCountMore = isAnimalCountIncrease(it, countAnimalAll)
                                    isErrorCountZero = isAnimalCountZero(it)
                                },
                                isError = isErrorCount,
                                isErrorCountMore = isErrorCountMore,
                                isErrorCountZero = isErrorCountZero,
                                intRes = R.string.outlined_text_field_quantity,
                                drawableRes = R.drawable.baseline_spoke_24,
                                count = countAnimalAll,
                                suffix = countSuffix,
                                focusManager = focusManager
                            )

                    }
                    itemsIndexed(textFields) { index, text ->
                        CardField {
                            Column {
                                val textPosition =
                                    stringResource(R.string.support_text_position_s, index + 1)

                                if (textFields.size > 1)
                                    TextAndIconRow(
                                        title = textPosition,
                                        iconResEnd = Icons.Default.Close,
                                        onClickIconEnd = { textFields.remove(text) }
                                    )

                                OutlinedTextTitleAdd2(
                                    value = text.title,
                                    onValueChange = { newTitle ->
                                        textFields[index] = textFields[index].copy(
                                            title = newTitle,
                                            isError = newTitle.isError(),
                                            isErrorSlash = newTitle.isSlash()
                                        )
                                        coroutineScope.launch {
                                            val countAndSuffix = onUpdateCountWarehouse(
                                                newTitle
                                            )
                                            textFields[index] =
                                                textFields[index].copy(
                                                    countWarehouse = countAndSuffix.first,
                                                    countWarehouseSuffix = countAndSuffix.second
                                                )
                                        }
                                    },
                                    titleList = titleList,
                                    isErrorTitle = text.isError,
                                    isErrorSlash = text.isErrorSlash,
                                    onValueChangeSuffix = {  },
                                )
                                OutlinedTextCount(
                                    value = text.count,
                                    onValueChange = { newCount ->
                                        val current = textFields[index]
                                        textFields[index] = current.copy(
                                            count = newCount,
                                            isErrorCount = newCount.isError()
                                        )
                                    },
                                    onSuffixChange = {
                                        textFields[index] = textFields[index].copy(suffix = it)
                                    },
                                    versionDropMenu = 0,
                                    isError = text.isErrorCount,
                                    suffix = text.suffix,
                                    intResSup = R.string.support_text_count_product,
                                    countWarehouse = text.countWarehouse.toString(),
                                    countWarehouseSuffix = text.countWarehouseSuffix,
                                )
                            }
                        }
                    }
                    item {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { textFields.add(KillTitleList(suffix = noNullWeightSuffix)) }
                        ) {
                            Text(stringResource(R.string.button_text_add_title))
                        }
                    }
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
                        if (isErrorKillAnimal(
                                countAnimal = countAnimal,
                                countAnimalAll = countAnimalAll,
                                isAnimalGroup = isAnimalGroup,
                                textFields = textFields,
                                isErrorCount = { isErrorCount = it },
                                isErrorCountMore = { isErrorCountMore = it },
                                isErrorCountZero = { isErrorCountZero = it }
                            )
                        ) {
                            val count = if (isAnimalGroup) isAnimalCountDifference(
                                countAnimal,
                                countAnimalAll
                            ) else countAnimalAll

                            val resultText = textFields.mapIndexed { index, it ->
                                        "${index + 1}. ${it.title} - ${it.count} ${it.suffix}"
                                    }.joinToString("\n")

                            textFields.forEach {
                                onSaveProductClick(
                                    AddTable(
                                        title = it.title,
                                        count = it.count.toConvertZeroDouble(),
                                        day = dateTodayArray()[0],
                                        mount = dateTodayArray()[1],
                                        year = dateTodayArray()[2],
                                        countSuffix = it.suffix,
                                        category = animalCategory,
                                        animalId = idPT.toLong(),
                                        note = note,
                                        price = 0.0,
                                        idPT = idPT.toLong()
                                    )
                                )
                            }
                            onSaveCountClick(
                                Triple(
                                    first = DomainIndicatorsVM(
//                                        weight = count,
                                        weight = countAnimal,
                                        suffix = countSuffix,
                                        date = dateToday(),
                                        idAnimal = idAnimal.toInt(),
                                        note = resultText,
                                        version = 2
                                    ),
                                    second = WriteOffTable(
                                        title = title,
                                        count = countAnimal.toConvertDbDouble(),
                                        day = dateTodayArray()[0],
                                        mount = dateTodayArray()[1],
                                        year = dateTodayArray()[2],
                                        status = false,
                                        priceAll = 0.0,
                                        countSuffix = countSuffix,
                                        note = reasonNote + resultText,
                                        idPT = idPT.toLong(),
                                        id = TODO(),
                                        price = TODO(),
                                        animalCountId = TODO()
                                    ),
                                    third = count.toInt() == 0
                                )
                            )
                            if (count.toInt() == 1 && isAnimalGroup) openDialogGroup = true
                            else onConfirmation()
                        }
                    }
                ) {
                    Text(stringResource(R.string.button_text_add))
                }
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

@Composable
private fun CardTotal(
    textFields: List<KillTitleList>,
    countAnimal: String,
    weight: String?,
    weightSuffix: String?,
    isAnimalGroup: Boolean
) {

    val (weightIfNot, noWeightSuffix, isTotalWeightBig) = weightAnimal(
        weight = weight,
        weightSuffix = weightSuffix,
        isAnimalGroup = isAnimalGroup,
        textFields = textFields,
        countAnimal = countAnimal
    )

    //Todo
    val totalWeight = textFields.sumOf {
        it.count.toConvertZeroDouble()
            .convertWeight(to = weightSuffix ?: "кг.", from = it.suffix)
    }

    val intTooltip =
        if (isAnimalGroup) R.string.tooltip_weight_big_average_kill else R.string.tooltip_weight_big_kill

    CardField(
        row = false
    ) {
        TextAndIconRow(
            title = stringResource(R.string.animal_card_screen_animal_card_info_weight_all),
            value = "$weightIfNot $noWeightSuffix",
            color = if (isTotalWeightBig) MaterialTheme.colorScheme.error else Color.Unspecified,
            isTooltipShow = isTotalWeightBig,
            intTooltip = intTooltip

        )
        textFields.forEachIndexed { index, text ->
            TextAndIconRow(
                title = "${index + 1}. " + if (text.title == "") stringResource(
                    R.string.support_text_empty
                ) else text.title,
                value = infoTextKillAnimal(
                    count = text.count,
                    suffix = text.suffix
                )
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
        TextAndIconRow(
            title = stringResource(R.string.analysis_screen_total),
            value = totalWeight.formatNumber() + " ${weightSuffix ?: "кг."}"
        )
    }
}


@Composable
private fun weightAnimal(
    weight: String?,
    weightSuffix: String?,
    isAnimalGroup: Boolean,
    countAnimal: String,
    textFields: List<KillTitleList>
): Triple<String, String, Boolean> {

    return if (weight != null && weightSuffix != null) {
        val totalWeight = textFields.sumOf {
            it.count.toConvertZeroDouble()
                .convertWeight(to = weightSuffix, from = it.suffix)
        }
        Log.i("weight", "weightAnimal: $weight  totalWeight: $totalWeight ")
        if (isAnimalGroup) {
            val weightAverage = animalCountWeightComposition(
                weight = weight,
                countAnimal = countAnimal
            )
            Triple(
                weightAverage,
                weightSuffix,
                isAnimalWeightIncrease(totalWeight.toString(), weightAverage)
            )
        } else {
            Triple(
                weight.toFormatNumber(),
                weightSuffix,
                isAnimalWeightIncrease(totalWeight.toString(), weight)
            )
        }
    } else {
        Triple(
            stringResource(R.string.animal_card_screen_animal_card_no_weight_),
            stringResource(R.string.suffix_kilogram),
            true,
        )
    }
}


data class KillTitleList(
    val title: String = "",
    val isError: Boolean = false,
    val isErrorSlash: Boolean = false,
    val count: String = "",
    val suffix: String = "",
    val isErrorCount: Boolean = false,
    val countWarehouse: Double = 0.0,
    val countWarehouseSuffix: String = ""
)
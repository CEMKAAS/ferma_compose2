package com.zaroslikov.fermacompose2.ui.animal.animal_dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.animalCountWeightComposition
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.infoTextKillAnimal
import com.zaroslikov.fermacompose2.supportFun.isAnimalWeightIncrease
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardIntent
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState.CountAnimal.ProductKill
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogAni
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleAdd2
import com.zaroslikov.fermacompose2.ui.elements.Suffix
import com.zaroslikov.fermacompose2.ui.elements.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.elements.toResId
import com.zaroslikov.fermacompose2.ui.start.formatNumber

@Composable
fun AlertDialogKillAnimal(
    state: AnimalCardState.CountAnimal,
    onIntent: (AnimalCardIntent) -> Unit,
    isAnimalGroup: Boolean,
    countAnimalAll: String,
    countSuffix: String,
    weight: String?,
    weightSuffix: String?
) {
    val textTitle =
        stringResource(if (isAnimalGroup) R.string.alert_dialog_info_kill_animals else R.string.alert_dialog_info_kill_animal)

    AlertDialogAni(
        icon = painterResource(R.drawable.icons8__meat60),
        title = textTitle,
        titleButton = stringResource(R.string.button_text_add),
        onDismissClick = { onIntent(AnimalCardIntent.DialogKillClicked(false)) },
        isScroll = false,
        content = {
            CardTotal(
                productList = state.productKill,
                countAnimal = state.countAnimal,
                weight = weight,
                weightSuffix = weightSuffix,
                isAnimalGroup = isAnimalGroup
            )
            LazyColumn {
                item {
                    if (isAnimalGroup)
                        OutlinedTextCountAnimal2(
                            value = state.countAnimal,
                            onValueChange = {
                                onIntent(AnimalCardIntent.CountKillChanged(it))
                            },
                            isError = state.error.isErrorCount,
                            isErrorCountMore = state.error.isErrorCountMore,
                            isErrorCountZero = state.error.isErrorCountZero,
                            countAnimalAll = countAnimalAll,
                            suffix = countSuffix,
                        )
                }
                itemsIndexed(state.productKill) { index, product ->
                    CardField(
                        row = false
                    ) {
                        val textPosition =
                            stringResource(R.string.support_text_position_s, index + 1)

                        if (state.productKill.size > 1)
                            TextAndIconRow(
                                title = textPosition,
                                iconResEnd = Icons.Default.Close,
                                onClickIconEnd = {
                                    onIntent(
                                        AnimalCardIntent.RemoveProductKillChanged(
                                            index
                                        )
                                    )
                                }
                            )
                        OutlinedTextTitleAdd2(
                            value = product.title,
                            onValueChange = {
                                onIntent(
                                    AnimalCardIntent.TitleProductKillChanged(
                                        index,
                                        it
                                    )
                                )
                            },
                            onValueChangeSuffix = {
                                onIntent(
                                    AnimalCardIntent.TitleAndSuffixKillClicked(
                                        index,
                                        it
                                    )
                                )
                            },
                            titleList = state.titleList,
                            isErrorTitle = product.error.isError,
                            isErrorSlash = product.error.isErrorSlash,
                            cardBorder = false
                        )
                        OutlinedTextCount2(
                            value = product.countProduct,
                            onValueChange = {
                                onIntent(
                                    AnimalCardIntent.CountProductKillChanged(
                                        index,
                                        it
                                    )
                                )
                            },
                            onSuffixChange = {
                                onIntent(
                                    AnimalCardIntent.SuffixProductKillChanged(
                                        index,
                                        it
                                    )
                                )
                            },
                            isError = product.error.isErrorCount,
                            suffix = product.suffixProduct,
                            intResSup = R.string.support_text_count_product,
                            isWarehouseShow = product.title.isNotBlank() && product.warehouseList.isNotEmpty(),
                            warehouseList = product.warehouseList,
                            cardBorder = false
                        )
                    }
                }
                item {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onIntent(AnimalCardIntent.AddProductKillChanged) }
                    ) {
                        Text(stringResource(R.string.button_text_add_title))
                    }
                }
            }
        },
        onConfirmationClick = {
            onIntent(AnimalCardIntent.SaveKillChanged)
        }
    )
}

@Composable
private fun CardTotal(
    productList: List<ProductKill>,
    countAnimal: String,
    weight: String?,
    weightSuffix: String?,
    isAnimalGroup: Boolean
) {
    val suffixList = setOf(
        stringResource(R.string.suffix_kilogram),
        stringResource(R.string.suffix_tons),
        stringResource(R.string.suffix_gram)
    )
    val weightSuffixNotNull = weightSuffix ?: stringResource(Suffix.KILOGRAM.toResId())
    val totalWeight = productList.sumOf { product ->
        if (product.suffixProduct in suffixList)
            product.countProduct.toConvertZeroDouble()
                .convertWeight(
                    to = weightSuffixNotNull,
                    from = product.suffixProduct
                ) else 0.0
    }

    val weightIfNot = when {
        weight == null -> stringResource(R.string.animal_card_screen_animal_card_no_weight_)
        isAnimalGroup -> animalCountWeightComposition(
            weight = weight,
            countAnimal = countAnimal
        )

        else -> weight.toFormatNumber()
    }

    val isTotalWeightBig =
        if (weight != null) isAnimalWeightIncrease(totalWeight.toString(), weightIfNot) else true

    val intTooltip =
        if (isAnimalGroup) R.string.tooltip_weight_big_average_kill else R.string.tooltip_weight_big_kill

    CardField(
        row = false
    ) {
        TextAndIconRow(
            title = stringResource(R.string.animal_card_screen_animal_card_info_weight_all),
            value = "$weightIfNot $weightSuffixNotNull",
            color = if (isTotalWeightBig) MaterialTheme.colorScheme.error else Color.Unspecified,
            isTooltipShow = isTotalWeightBig,
            intTooltip = intTooltip
        )
        productList.forEachIndexed { index, product ->
            val productTitle = if (product.title == "") stringResource(
                R.string.animal_card_screen_animal_card_no_weight_
            ) else product.title

            TextAndIconRow(
                title = "${index + 1}. $productTitle",
                value = infoTextKillAnimal(
                    count = product.countProduct,
                    suffix = product.suffixProduct
                )
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
        TextAndIconRow(
            title = stringResource(R.string.analysis_screen_total),
            value = totalWeight.formatNumber() + " $weightSuffixNotNull"
        )
    }
}

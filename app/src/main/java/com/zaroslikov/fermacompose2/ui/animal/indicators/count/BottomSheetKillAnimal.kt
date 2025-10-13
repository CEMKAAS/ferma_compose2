@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.animalCountWeightComposition
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.infoTextKillAnimal
import com.zaroslikov.fermacompose2.supportFun.isAnimalWeightIncrease
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CountBottomSheet2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCount2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextTitleAdd2
import com.zaroslikov.fermacompose2.ui.elements.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.start.formatNumber

@Composable
fun BottomSheetKillAnimal(
    state: DomainAnimalCountPrice,
    onIntent: (AnimalCountIntent) -> Unit,
    errorState: AnimalCountState.Error,
    isEntry: Boolean,
    productKill: List<AnimalCountState.ProductKill>,
    titleList: List<TitleAndSuffixDomain>,
    isAnimalGroup: Boolean,
    countAnimalAll: String,
    countSuffix: Suffix,
    weight: DomainAnimalWeight?
) {
    CountBottomSheet2(
        version = state.version,
        isEntry = isEntry,
        intEntryButton = R.string.button_text_add,
        onDismiss = { onIntent(AnimalCountIntent.EndDialogClicked) },
        onInsert = { onIntent(AnimalCountIntent.InsertKillChanged) },
        onUpdate = { onIntent(AnimalCountIntent.EditKillChanged) },
        onDelete = { onIntent(AnimalCountIntent.DeleteCountPressed(state.id, true)) },
        topContent = {
            CardTotal(
                productList = productKill,
                countAnimal = countAnimalAll,
                weightAnimal = weight,
                isAnimalGroup = isAnimalGroup
            )
        }
    ) {
        OutlinedTextCountAnimal2(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountChanged(it))
            },
            isError = errorState.isErrorCount,
//                        isErrorCountMore = error.isErrorCountMore,
//                        isErrorCountZero = error.isErrorCountZero,
            countAnimalAll = countAnimalAll,
            suffix = countSuffix,
        )
        OutlinedTextDate(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) },
        )
        productKill.filter { it.isVisibility == true }.forEachIndexed { index, product ->
            CardField(
                row = false
            ) {
                val textPosition = stringResource(R.string.support_text_position_s, index + 1)

                if (productKill.size > 1)
                    TextAndIconRow(
                        title = textPosition,
                        iconResEnd = R.drawable.baseline_clear_24,
                        onClickIconEnd = {
                            onIntent(AnimalCountIntent.RemoveProductKillChanged(index))
                        }
                    )
                OutlinedTextTitleAdd2(
                    value = product.title,
                    onValueChange = {
                        onIntent(
                            AnimalCountIntent.TitleProductKillChanged(
                                index,
                                it
                            )
                        )
                    },
                    onValueChangeSuffix = {
                        onIntent(
                            AnimalCountIntent.TitleAndSuffixKillClicked(
                                index,
                                it
                            )
                        )
                    },
                    titleList = titleList,
                    isErrorTitle = product.error.isError,
                    isErrorSlash = product.error.isErrorSlash,
                    cardBorder = false
                )
                OutlinedTextCount2(
                    value = product.countProduct,
                    onValueChange = {
                        onIntent(
                            AnimalCountIntent.CountProductKillChanged(
                                index,
                                it
                            )
                        )
                    },
                    onSuffixChange = {
                        onIntent(
                            AnimalCountIntent.SuffixProductKillChanged(
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
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onIntent(AnimalCountIntent.AddProductKillChanged) }
        ) {
            Text(stringResource(R.string.button_text_add_title))
        }
    }
}

@Composable
private fun CardTotal(
    productList: List<AnimalCountState.ProductKill>,
    countAnimal: String,
    weightAnimal: DomainAnimalWeight?,
    isAnimalGroup: Boolean
) {
    val weight = weightAnimal?.weight
    val weightSuffixNotNull = weightAnimal?.suffix ?: Suffix.KILOGRAM
    val suffixList = setOf(
        Suffix.GRAM,
        Suffix.KILOGRAM,
        Suffix.TONS
    )

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
            value = "$weightIfNot ${stringResource(weightSuffixNotNull.toResId())}",
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
                    suffix = stringResource(product.suffixProduct.toResId())
                )
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
        TextAndIconRow(
            title = stringResource(R.string.analysis_screen_total),
            value = totalWeight.formatNumber() + " ${stringResource(weightSuffixNotNull.toResId())}"
        )
    }
}

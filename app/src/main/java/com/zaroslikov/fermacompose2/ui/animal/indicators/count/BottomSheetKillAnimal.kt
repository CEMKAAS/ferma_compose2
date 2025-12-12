@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_3
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.red_2
import com.zaroslikov.fermacompose2.red_7
import com.zaroslikov.fermacompose2.red_8
import com.zaroslikov.fermacompose2.red_9
import com.zaroslikov.fermacompose2.supportFun.animalCountWeightComposition
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.isAnimalWeightIncrease
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble2
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.ProductKillCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TextLineProductKill
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.sections.EntryIndicationBottomSheet
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.white

@Composable
fun BottomSheetKillAnimal(
    state: CountItem,
    onIntent: (AnimalCountIntent) -> Unit,
    productKill: List<ProductKill>,
    isAnimalGroup: Boolean,
    countAnimalAll: String,
    countSuffix: Suffix,
    weight: DomainAnimalWeight?
) {
    val productKillFilter = productKill.filter { it.isVisibility == true }
    val focusRequester =
        remember { FocusRequester() } // ✅ нужно помнить, иначе при recomposition фокус сбрасывается
    // ✅ Важно: просим фокус, когда bottom sheet появился
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val isEntry = state.currentProduct.isEntry
    EntryIndicationBottomSheet(
        icon = state.version.toDrawRes(),
        titleRes = state.version.toResId(),
        isEntry = state.isEntry,
        enabledButton = state.enabledButton(),
        colors = state.version.toColorList(),
        onDismissRequest = { onIntent(AnimalCountIntent.EndDialogClicked) },
        onInsertClick = { onIntent(AnimalCountIntent.InsertKillChanged) },
        onUpdateClick = { onIntent(AnimalCountIntent.EditKillChanged) }
    ) {
        CardTotal(
            productList = productKillFilter,
            countAnimal = state.count,
            weightAnimal = weight,
            isAnimalGroup = isAnimalGroup,
            openWeightDialog = { /*onIntent(AnimalCountIntent.OpenWeightDialog(true))*/ }
        )
        OutlinedTextCountAnimalNew(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountChanged(it))
            },
            isError = state.error.isErrorCount,
            /*isErrorCountZero = state.error.isErrorCountZero,*/
            suffix = countSuffix,
            countAnimal = countAnimalAll,
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) },
        )

        BorderCard {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add_product),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = gray_7
                )
                Text(
                    text = stringResource(R.string.add_screen_title2),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = text_16,
                    color = dark
                )
            }
            productKillFilter.forEachIndexed { index, product ->
                ProductKillCard(
                    number = index + 1,
                    name = product.title,
                    value = product.countProduct.toFormatNumber(),
                    suffix = product.suffixProduct,
                    isEditMode = product.isEntry,
                    onClick = { onIntent(AnimalCountIntent.ChoiceProductKillChanged(index)) },
                ) { onIntent(AnimalCountIntent.RemoveProductKillChanged(index)) }
            }
            BorderCard(
                containerColor = ghostly_white,
                padding = PaddingValues(18.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!state.currentProduct.isEntry)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    stringResource(R.string.animal_count_screen_edit_mode),
                                    style = text_14,
                                    color = orang_6
                                )
                                TextButton(
                                    onClick = { onIntent(AnimalCountIntent.CancelProductKillChanged) }
                                ) {
                                    Text(
                                        stringResource(R.string.button_text_cancel_2),
                                        style = text_12,
                                        color = gray_7
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 1.dp, color = orang_5)
                        }

                    OutlinedTextTitleAddNew(
                        value = state.currentProduct.title,
                        onValueChange = {
                            onIntent(
                                AnimalCountIntent.TitleProductKillChanged(it)
                            )
                        },
                        onValueChangeSuffix = {
                            onIntent(
                                AnimalCountIntent.TitleAndSuffixKillClicked(it)
                            )
                        },
                        titleList = state.titleList,
                        isErrorTitle = state.currentProduct.error.isError,
                        isErrorSlash = state.currentProduct.error.isErrorSlash,
                        isBorderCard = false,
                        colorTextField = white
                    )
                    OutlinedTextCountNew(
                        value = state.currentProduct.countProduct,
                        onValueChange = {
                            onIntent(
                                AnimalCountIntent.CountProductKillChanged(it)
                            )
                        },
                        onSuffixChange = {
                            onIntent(
                                AnimalCountIntent.SuffixProductKillChanged(it)
                            )
                        },
                        isError = state.currentProduct.error.isErrorCount,
                        suffix = state.currentProduct.suffixProduct,
                        intResSup = R.string.support_text_count_product,
                        isBorderCard = false,
                        colorTextField = white
                        /*isWarehouseShow = product.title.isNotBlank() && product.warehouseList.isNotEmpty(),
                warehouseList = product.warehouseList,
                cardBorder = false*/
                    )
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        2.dp,
                        if (state.currentProduct.enabledButton()) green_3 else grey
                    ),
                    enabled = state.currentProduct.enabledButton(),
                    onClick = {
                        onIntent(
                            if (isEntry) AnimalCountIntent.AddProductKillChanged
                            else AnimalCountIntent.EditProductKillChanged
                        )
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            if (isEntry) Icons.Default.Add else Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (state.currentProduct.enabledButton()) green_2 else grey,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            stringResource(
                                if (isEntry) R.string.button_text_add_title
                                else R.string.button_text_edit_title
                            ),
                            style = text_14,
                            color = if (state.currentProduct.enabledButton()) green_2 else grey
                        )
                    }
                }
            }
        }
    }
  /*  if (state.isOpenWeightDialog)
        AlertDialogWeightAnimal(
            onDismissRequest = { onIntent(AnimalCountIntent.OpenWeightDialog(false)) },
            colors = state.version.toColorList()
        )*/
}

@Composable
private fun CardTotal(
    productList: List<ProductKill>,
    countAnimal: String,
    weightAnimal: DomainAnimalWeight?,
    isAnimalGroup: Boolean,
    openWeightDialog: () -> Unit
) {
    val weight =
        if (weightAnimal?.weight != null && countAnimal.toConvertZeroDouble2() > 0) weightAnimal.weight else null
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

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = red_9
        ),
        border = BorderStroke(
            width = 1.dp,
            color = red_8
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.animal_card_screen_animal_card_info_weight_all),
                    style = text_14,
                    color = marengo
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "$weightIfNot ${stringResource(weightSuffixNotNull.toResId())}",
                        style = text_16,
                        color = red_7
                    )
                    IconButton(
                        onClick = openWeightDialog
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = red_7
                        )
                    }
                }
            }
            if (productList.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    HorizontalDivider(thickness = 1.dp, color = red_2)
                    Text(
                        stringResource(R.string.animal_count_screen_product),
                        style = text_12,
                        color = gray_7
                    )
                    productList.forEachIndexed { index, product ->
                        val productTitle = if (product.title == "") stringResource(
                            R.string.animal_card_screen_animal_card_no_weight_
                        ) else product.title
                        TextLineProductKill(
                            number = index + 1,
                            title = productTitle,
                            value = product.countProduct.toConvertZeroString().toFormatNumber(),
                            suffix = product.suffixProduct
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HorizontalDivider(thickness = 1.dp, color = red_2)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.animal_count_screen_total_weight),
                            style = text_16,
                            color = black_1
                        )
                        Text(
                            "${totalWeight.formatNumber()}  ${stringResource(weightSuffixNotNull.toResId())}",
                            style = text_16, color = black_1
                        )
                    }
                }
            }
        }
    }
}

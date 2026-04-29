@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_3
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_7
import com.zaroslikov.fermacompose2.red_2
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.red_7
import com.zaroslikov.fermacompose2.red_8
import com.zaroslikov.fermacompose2.red_9
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDbInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.IconToggle
import com.zaroslikov.fermacompose2.ui.project.sections.EntryIndicationBottomSheet
import com.zaroslikov.fermacompose2.white

@Composable
fun BottomSheetKillAnimal(
    state: CountItem,
    onIntent: (AnimalCountIntent) -> Unit,
    countAnimalAll: String?,
    countSuffix: Suffix,
) {
    val focusRequester =
        remember { FocusRequester() } // ✅ нужно помнить, иначе при recomposition фокус сбрасывается
    // ✅ Важно: просим фокус, когда bottom sheet появился
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    EntryIndicationBottomSheet(
        iconRes = state.version.toDrawRes(),
        titleRes = state.version.toResId(),
        isEntry = state.isEntry,
        enabledButton = state.hasAnyError,
        colors = state.version.toColorList(),
        onDismissRequest = {
            onIntent(
                AnimalCountIntent.DialogClicked(
                    false,
                    isSaveStateForBottomSheet = state.isEntry,
                    version = state.version
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AnimalCountIntent.DialogClicked(false))
        },
        onInsertClick = { onIntent(AnimalCountIntent.InsertKillChanged) },
        onUpdateClick = { onIntent(AnimalCountIntent.EditKillChanged) }
    ) {
        CardTotal(
            productList = state.productKillList.filter { it.isVisibility },
            totalWeight = state.totalWeight,
            finalWeight = state.finalWeight,
            weightSuffix = state.weightSuffix,
            isShowButton = state.count.isNotBlank(),
            openWeightDialog = { onIntent(AnimalCountIntent.OpenWeightAlertDialogClicked(true)) },
            isShowWarningIcon = state.totalWeight?.let { state.finalWeight > it } ?: false,
            isAnimalMany = state.count.toConvertZeroDbInt() > 1
        )
        OutlinedTextCountAnimalNew(
            value = state.count,
            onValueChange = {
                onIntent(AnimalCountIntent.CountChanged(it))
            },
            isError = state.error.isErrorCount,
            isErrorCountZero = state.error.isErrorCountZero,
            suffix = countSuffix,
            countAnimal = countAnimalAll,
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) },
        )
        ProductCard(
            state = state,
            productKillFilter = state.productKillList,
            onIntent = onIntent
        )
    }
    if (state.isOpenWeightDialog)
        AlertDialogWeightAnimal(
            onDismissRequest = { onIntent(AnimalCountIntent.OpenWeightAlertDialogClicked(false)) },
            colors = state.version.toColorList(),
            weight = state.totalWeight ?: 0.0,
            weightSuffix = state.weightSuffix,
            countAnimal = state.count.toConvertZeroDbInt(),
            onClick = {
                onIntent(AnimalCountIntent.WeightChanged(it))
                onIntent(AnimalCountIntent.OpenWeightAlertDialogClicked(false))
            }
        )
}

@Composable
private fun CardTotal(
    productList: List<ProductKill>,
    totalWeight: Double?,
    finalWeight: Double,
    weightSuffix: Suffix,
    openWeightDialog: () -> Unit,
    isShowButton: Boolean,
    isShowWarningIcon: Boolean,
    isAnimalMany: Boolean
) {
    BorderCard(
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(18.dp),
        containerColor = red_9,
        borderColor = red_8
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TotalWeight(
                totalWeight = totalWeight,
                weightSuffix = weightSuffix,
                onClick = openWeightDialog,
                isShowButton = isShowButton,
                isShowWarningIcon = isShowWarningIcon,
                isAnimalMany = isAnimalMany
            )
            if (productList.isNotEmpty()) {
                ProductList(productList)
                FinalWeight(
                    totalWeight = finalWeight,
                    totalWeightSuffix = weightSuffix
                )
            }
        }
    }
}

@Composable
private fun TotalWeight(
    totalWeight: Double?,
    weightSuffix: Suffix,
    isShowButton: Boolean,
    isShowWarningIcon: Boolean,
    isAnimalMany: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val intTooltip =
            if (isAnimalMany) R.string.tooltip_weight_big_average_kill else R.string.tooltip_weight_big_kill
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.animal_card_screen_animal_card_info_weight_all),
                style = text_14,
                color = marengo
            )
            if (isShowWarningIcon)
                IconToggle(
                    stringRes = intTooltip,
                    iconRes = R.drawable.outline_info_24,
                    iconColor = error_base,
                )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${totalWeight?.formatNumber() ?: "-"} ${stringResource(weightSuffix.toResId())}",
                style = text_16,
                color = red_7
            )
            if (isShowButton)
                IconButton(
                    onClick = onClick
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
}

@Composable
private fun ProductList(
    productList: List<ProductKill>
) {
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
            TextLineProductKill(
                number = index + 1,
                title = product.title,
                value = product.countProduct.toConvertZeroString().toFormatNumber(),
                suffix = product.suffixProduct
            )
        }
    }
}


@Composable
private fun FinalWeight(
    totalWeight: Double,
    totalWeightSuffix: Suffix
) {
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
                "${totalWeight.formatNumber()} ${stringResource(totalWeightSuffix.toResId())}",
                style = text_16, color = black_1
            )
        }
    }
}

@Composable
fun TextLineProductKill(
    number: Int,
    title: String,
    value: String,
    suffix: Suffix
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$number. $title", style = text_14, color = marengo)
        Text("$value ${stringResource(suffix.toResId())}", style = text_14, color = black_1)
    }
}


@Composable
fun ProductCard(
    state: CountItem,
    productKillFilter: List<ProductKill>,
    onIntent: (AnimalCountIntent) -> Unit
) {
    BorderCard {
        TitleCard()
        productKillFilter.forEachIndexed { index, product ->
            if (product.isVisibility)
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
                EditTitle(state.currentProduct.isEntry) { onIntent(AnimalCountIntent.CancelProductKillChanged) }
                OutlinedTextTitleAddNew(
                    value = state.currentProduct.title,
                    onValueChange = {
                        onIntent(AnimalCountIntent.TitleProductKillChanged(it))
                    },
                    onValueChangeSuffix = {
                        onIntent(AnimalCountIntent.TitleAndSuffixKillClicked(it))
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
                        onIntent(AnimalCountIntent.CountProductKillChanged(it))
                    },
                    onSuffixChange = {
                        onIntent(AnimalCountIntent.SuffixProductKillChanged(it))
                    },
                    isError = state.currentProduct.error.isErrorCount,
                    suffix = state.currentProduct.suffixProduct,
                    intResSup = R.string.support_text_count_product,
                    isBorderCard = false,
                    colorTextField = white
                )
            }
            OutlinedCustomButton(
                isEntry = state.currentProduct.isEntry,
                enabled = state.currentProduct.hasAnyError,
                onAddClick = { onIntent(AnimalCountIntent.AddProductKillChanged) },
                onEditClick = { onIntent(AnimalCountIntent.EditProductKillChanged) }
            )
        }
    }
}

@Composable
fun ProductKillCard(
    number: Int,
    name: String,
    value: String,
    suffix: Suffix?,
    isEditMode: Boolean = false,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val (containerColor, borderColor) = if (isEditMode) orang_4 to orang_7 else green_g_2 to green_1
    val suffixText = suffix?.let { stringResource(it.toResId()) } ?: ""
    BorderCard(
        padding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        containerColor = containerColor,
        borderColor = borderColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("$number.", style = text_14, color = marengo)
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(name, style = text_14, color = black_1)
                    if (value.isNotBlank())
                        Text(
                            "$value $suffixText",
                            style = text_12,
                            color = green_shamrock
                        )
                }
            }
            IconButton(
                onClick = onDeleteClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    tint = red_4,
                    contentDescription = null
                )
            }
        }
    }
}


@Composable
private fun TitleCard() {
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
}

@Composable
private fun EditTitle(
    isEntry: Boolean,
    onCancel: () -> Unit
) {
    if (!isEntry)
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
                    onClick = onCancel
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
}

@Composable
private fun OutlinedCustomButton(
    isEntry: Boolean,
    enabled: Boolean,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            2.dp,
            if (enabled) green_3 else grey
        ),
        enabled = enabled,
        onClick = { if (isEntry) onAddClick() else onEditClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (isEntry) Icons.Default.Add else Icons.Default.Edit,
                contentDescription = null,
                tint = if (enabled) green_2 else grey,
                modifier = Modifier.size(16.dp)
            )
            Text(
                stringResource(
                    if (isEntry) R.string.button_text_add_title
                    else R.string.button_text_edit_title
                ),
                style = text_14,
                color = if (enabled) green_2 else grey
            )
        }
    }
}
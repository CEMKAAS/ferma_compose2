@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.count.ProductKill
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardIndicationChangeChoice
import com.zaroslikov.fermacompose2.ui.elements.CardVaccinationDate
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.ProductKillInfoCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelDetailNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelNew
import com.zaroslikov.fermacompose2.ui.start.formatNumber

@Composable
fun <T> InventoryAnimalBody(
    modifier: Modifier = Modifier,
    itemList: List<T>,
    onInsertClick: () -> Unit,
    @StringRes titleRes2: Int,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    @StringRes supportRes: Int,
    @StringRes buttonRes: Int,
    detailCard: @Composable (item: T, previous: T?) -> Unit
) {
    if (itemList.isNotEmpty())
        InventoryList(
            modifier = modifier,
            itemList = itemList,
            detailCard = detailCard,
            titleRes2 = titleRes2
        )
    else
        MessageNoData(
            modifier = modifier,
            onClick = onInsertClick,
            titleRes = titleRes,
            messageRes = messageRes,
            supportRes = supportRes,
            buttonRes = buttonRes
        )
}


@Composable
private fun <T> InventoryList(
    modifier: Modifier = Modifier,
    @StringRes titleRes2: Int,
    itemList: List<T>,
    detailCard: @Composable (item: T, previous: T?) -> Unit
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeadingIndicators(titleRes2)
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = itemList,
                key = { index, _ -> index }) { index, item ->
                val previousItem =
                    if (index < itemList.size - 1) itemList[index + 1] else null
                detailCard(item, previousItem)
            }
        }
    }
}

@Composable
fun EntryBottomSheet(
    modifier: Modifier = Modifier,
    isEntry: Boolean,
    enabledButton: Boolean,
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onInsertClick: () -> Unit,
    onUpdateClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.modifierBottomSheet(true),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextLine(
                            modifier = Modifier.weight(1f),
                            valueString = "Добавить продуцию",
                            textStyle = textBold_20
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                    Text(text = "Введите информацию о новой продукции")
                }
                content()
            }
            ButtonPanelNew(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                isEntry = isEntry,
                enable = enabledButton,
                colors = colors,
                onClickInsert = onInsertClick,
                onClickUpdate = onUpdateClick,
                onClickClose = onDismissRequest
            )
        }
    }
}

@Composable
fun DetailBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    date: String,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.modifierBottomSheet(true),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextLine(
                            modifier = Modifier.weight(1f),
                            valueString = title,
                            textStyle = textBold_20
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                    Text(text = date, style = text_14, color = grey_3)
                }
                content()
            }
            ButtonPanelDetailNew(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onClickUpdate = onUpdateClick,
                onClickDelete = onDeleteClick
            )
        }
    }
}


@Composable
fun AnimalIndicatorsCardNew(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    suffix: Suffix,
    date: String,
    note: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDetailClick: () -> Pair<String, IndicationStatus>
) {
    var details by rememberSaveable { mutableStateOf(false) }
    var sd by rememberSaveable { mutableStateOf<Pair<String, IndicationStatus>>("" to IndicationStatus.NEGATIVE) }
    CardFieldNew(
        modifier = modifier,
        padding = PaddingValues(),
        onClick = {
            sd = onDetailClick()
            details = !details
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconIndicatorsAnimal(
                        icon = icon,
                        colors = colors
                    )
                    Text(
                        text = "$value ${stringResource(suffix.toResId())}",
                        style = textBold_16,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = date,
                        style = textBold_16,
                        textAlign = TextAlign.Center
                    )
                    DropdownMenuEdit(
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = details
        ) {
            BaseDetailsAnimalIndication(
                value = sd.first,
                suffix = suffix,
                status = sd.second,
                note = note
            )
        }
    }
}

@Composable
fun AnimalCountCardNew(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    price: Double? = null,
    suffix: Suffix,
    date: String,
    note: String,
    productKill: List<ProductKill>,
    indicationStatus: IndicationStatus,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    /* onDetailClick: () -> List<ProductKill>,*/
) {
    var details by rememberSaveable { mutableStateOf(false) }
    /*var productKill by rememberSaveable { mutableStateOf<List<ProductKill>>(emptyList()) }*/
    CardFieldNew(
        modifier = modifier,
        padding = PaddingValues(),
        onClick = {
            /*productKill = onDetailClick()*/
            details = !details
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconIndicatorsAnimal(
                        icon = icon,
                        colors = colors
                    )
                    Text(
                        text = "$value ${stringResource(suffix.toResId())}",
                        style = textBold_16,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = date,
                        style = textBold_16,
                        textAlign = TextAlign.Center
                    )
                    DropdownMenuEdit(
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = details
        ) {
            BaseDetailsAnimalIndication(
                value = value,
                suffix = suffix,
                price = price?.formatNumber(),
                status = indicationStatus,
                note = note,
                productKill = productKill
            )
        }
    }
}

@Composable
fun AnimalVaccinationCardNew(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    colors: List<Color>,
    value: String,
    count: String,
    price: Double?,
    suffix: Suffix,
    date: String,
    nextDate: String?,
    note: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    /* onDetailClick: () -> Pair<String, IndicationStatus>*/
) {
    var details by rememberSaveable { mutableStateOf(false) }
    CardFieldNew(
        modifier = modifier,
        padding = PaddingValues(),
        onClick = {
            details = !details
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconIndicatorsAnimal(
                        icon = icon,
                        colors = colors
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            value,
                            style = text_16,
                            color = black_1
                        )
                        Text(
                            "$count ${stringResource(Suffix.PIECES.toResId())}",
                            style = text_12,
                            color = gray_7
                        )
                    }
                }
                DropdownMenuEdit(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CardVaccinationDate(true, date)
                nextDate?.let {
                    CardVaccinationDate(false, it)
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = details
        ) {
            BaseDetailsAnimalIndication(
                value = price?.formatNumber(),
                suffix = suffix,
                status = IndicationStatus.PRICE,
                note = note,
            )
        }
    }
}

@Composable
fun BaseDetailsAnimalIndication(
    value: String?,
    suffix: Suffix,
    price: String? = null,
    note: String,
    status: IndicationStatus,
    productKill: List<ProductKill> = emptyList()
) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = gray_6
    )
    Column(
        modifier = Modifier
            .background(color = Color(0xFFF9FAFB))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        value?.let {
            when (status) {
                IndicationStatus.EXPENSES, IndicationStatus.SALE, IndicationStatus.WRITE_OFF -> {
                    CardIndicationChangeChoice(value, suffix, status)
                    price?.let {
                        CardIndicationChangeChoice(
                            price,
                            Suffix.RUBLE,
                            IndicationStatus.PRICE
                        )
                    }
                }

                IndicationStatus.KILL -> {
                    CardIndicationChangeChoice(value, suffix, status)
                    if (productKill.isNotEmpty()) {
                        Text(
                            stringResource(R.string.animal_card_screen_kill_add_product),
                            style = text_14,
                            color = marengo
                        )
                        productKill.forEachIndexed { index, productKill ->
                            ProductKillInfoCard(
                                number = index + 1,
                                name = productKill.title,
                                value = productKill.countProduct,
                                suffix = productKill.suffixProduct
                            )
                        }
                    }
                }

                else -> CardIndicationChangeChoice(value = value, suffix = suffix, status = status)
            }

        }
        if (value != null && note.isNotEmpty())
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = gray_6
            )
        if (note.isNotEmpty())
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.animal_indicators_note),
                    style = text_14,
                    color = marengo
                )
                Text(
                    text = note,
                    color = Color(0xFF364153),
                    style = text_14,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
    }
}

@Composable
fun HeadingIndicators(
    @StringRes titleRes: Int,
    isVaccination: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(titleRes),
            style = textBold_18,
            textAlign = TextAlign.Center,
            color = marengo
        )
        Text(
            text = stringResource(R.string.outlined_text_date),
            style = textBold_18,
            textAlign = TextAlign.Center,
            color = marengo
        )
        /*if (isVaccination)
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.support_text_data_next_vaccination),
                style = textBold_18,
                textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.weight(0.25f))*/
    }
}

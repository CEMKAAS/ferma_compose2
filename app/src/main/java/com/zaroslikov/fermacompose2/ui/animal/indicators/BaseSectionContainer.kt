@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.size.HeadingIndicators
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelNew

@Composable
fun <T> InventoryAnimalBody(
    modifier: Modifier = Modifier,
    itemList: List<T>,
    onInsertClick: () -> Unit,
    onEditClick: (T) -> Unit,
    onDeleteClick: (Long) -> Unit,
    titleRes: Int,
    messageRes: Int,
    supportRes: Int,
    buttonRes: Int,
    detailCard: @Composable (item: T, previous: T?) -> Unit
) {
    if (itemList.isNotEmpty())
        InventoryList(
            modifier = modifier,
            itemList = itemList,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            detailCard = detailCard,
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
    itemList: List<T>,
    onEditClick: (T) -> Unit,
    onDeleteClick: (Long) -> Unit,
    detailCard: @Composable (item: T, previous: T?) -> Unit
) {
    HeadingIndicators(R.string.height_screen_title)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp),
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
fun AnimalIndicatorsCardNew(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    domainAnimalSize: DomainAnimalSize,
    previousDomainAnimalSize: DomainAnimalSize?
) {
    var details by rememberSaveable { mutableStateOf(false) }
    CardFieldNew(
        modifier = modifier,
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
                    icon = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                    colors = listOf(Color(0xFF009966), Color(0xFF00A63E))
                )
                Text(
                    text = "${domainAnimalSize.size.toFormatNumber()} ${
                        stringResource(
                            domainAnimalSize.suffix.toResId()
                        )
                    }",
                    style = textBold_16,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = domainAnimalSize.date,
                    style = textBold_16,
                    textAlign = TextAlign.Center
                )
                DropdownMenuEdit(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
                if (previousDomainAnimalSize != null)
                    IconButton(
                        onClick = { details = !details }) {
                        Icon(
                            painterResource(if (details) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
                            contentDescription = null
                        )
                    }
            }

        }
        /*if (details && previousDomainAnimalSize != null)
            DetailsCount(
                domainAnimalSize,
                previousDomainAnimalSize
            )*/
    }
}
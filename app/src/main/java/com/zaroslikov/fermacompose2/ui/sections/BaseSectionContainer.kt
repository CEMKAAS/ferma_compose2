@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.TextField.SearchBar
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelNew
import com.zaroslikov.fermacompose2.ui.start.formatNumber

@Composable
fun <T, B> InventoryBody(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<T>,
    searchList: List<T>,
    brieflyList: List<B>,
    onInsertClick: () -> Unit,
    onEditClick: (T) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (B) -> Unit,
    // карточки передаются как composable-функции
    detailCard: @Composable (T) -> Unit,
    brieflyCard: @Composable (B) -> Unit,
    // ресурсы строк для пустого состояния
    titleRes: Int,
    messageRes: Int,
    supportRes: Int,
    buttonRes: Int,
) {
    if (itemList.isNotEmpty()) {
        InventoryList(
            modifier = modifier,
            details = details,
            itemList = searchList,
            brieflyList = brieflyList,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            onDetailsClick = onDetailsClick,
            detailCard = detailCard,
            brieflyCard = brieflyCard,
        )
    } else {
        MessageNoData(
            modifier = modifier,
            onClick = onInsertClick,
            titleRes = titleRes,
            messageRes = messageRes,
            supportRes = supportRes,
            buttonRes = buttonRes
        )
    }
}


@Composable
private fun <T, B> InventoryList(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<T>,
    brieflyList: List<B>,
    onEditClick: (T) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (B) -> Unit,
    detailCard: @Composable (T) -> Unit,
    brieflyCard: @Composable (B) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (details)
            items(items = itemList) { item -> detailCard(item) }
        else
            items(items = brieflyList) { item -> brieflyCard(item) }
    }
}

@Composable
fun <T> BrieflyBottomSheetUniversal(
    list: List<T>,
    titleProduct: String,
    count: Double,
    suffix: Suffix,
    countEntry: Long,
    onDismissRequest: () -> Unit,
    onEditClick: ((T) -> Unit)? = null,
    onDeleteClick: ((Long) -> Unit)? = null,
    itemCard: @Composable (T) -> Unit,
) {
    val countString = count.formatNumber()
    val suffixString = stringResource(suffix.toResId())
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextLine(
                        modifier = Modifier.weight(1f),
                        valueString = titleProduct,
                        textStyle = textBold_20
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Text("Всего: $countString $suffixString ($countEntry записей)")
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { item ->
                    itemCard(item)
                }
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

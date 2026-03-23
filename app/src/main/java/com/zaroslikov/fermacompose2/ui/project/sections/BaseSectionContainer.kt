@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData2
import com.zaroslikov.fermacompose2.ui.elements.TextLine
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanelNew
import com.zaroslikov.fermacompose2.ui.formatNumber

@Composable
fun <T, B> InventoryBody(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<T>,
    searchList: List<T>,
    brieflyList: List<B>,
    // карточки передаются как composable-функции
    detailCard: @Composable (Int, T) -> Unit,
    brieflyCard: @Composable (B) -> Unit,
    // ресурсы строк для пустого состояния
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    @StringRes supportSecondText: Int? = null,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    backgroundColor: Color
) {
    if (itemList.isNotEmpty()) {
        if (searchList.isNotEmpty())
            InventoryList(
                modifier = modifier,
                details = details,
                itemList = searchList,
                brieflyList = brieflyList,
                detailCard = detailCard,
                brieflyCard = brieflyCard,
            )
        else MessageNoData2(
            modifier = modifier,
            titleRes = R.string.search_section_search_nothing,
            messageRes = R.string.search_section_search_nothing_sup,
            supportSecondText = R.string.search_section_search_nothing_sup_2,
            iconRes = R.drawable.icon_search_off,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
        )
    } else
        MessageNoData2(
            modifier = modifier,
            titleRes = titleRes,
            messageRes = messageRes,
            supportSecondText = supportSecondText,
            iconRes = iconRes,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
        )
}


@Composable
private fun <T, B> InventoryList(
    modifier: Modifier = Modifier,
    details: Boolean,
    itemList: List<T>,
    brieflyList: List<B>,
    detailCard: @Composable (Int, T) -> Unit,
    brieflyCard: @Composable (B) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (details)
            itemsIndexed(items = itemList) { index, item -> detailCard(index, item) }
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
    onAnalysisClick: () -> Unit = {},
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
                Button(
                    onClick = { onAnalysisClick() },
                ) { Text("Анализ") }
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
fun EntryIndicationBottomSheet(
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    isEntry: Boolean,
    enabledButton: Boolean,
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onSecondDismissRequest: () -> Unit = {},
    onInsertClick: () -> Unit,
    onUpdateClick: () -> Unit,
    scrollableContent: @Composable ColumnScope.() -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(titleRes),
        supText = if (isEntry) null else stringResource(R.string.animal_indicators_mode_edit),
        iconRes = iconRes,
        colors = colors,
        onDismissRequest = onDismissRequest,
        onSecondDismissRequest = onSecondDismissRequest,
        contentBottom = {
            ButtonPanelNew(
                modifier = Modifier
                    .fillMaxWidth(),
                isEntry = isEntry,
                enable = enabledButton,
                colors = colors,
                onClickInsert = onInsertClick,
                onClickUpdate = onUpdateClick,
                onClickClose = onDismissRequest
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            scrollableContent()
        }
    }
}

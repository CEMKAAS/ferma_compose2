@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.white

@ExperimentalMaterial3Api
@Composable
fun CountBottomSheet2(
    version: AnimalCountVersion?,
    isEntry: Boolean,
    @StringRes intEntryButton: Int,
    onDismiss: () -> Unit,
    onInsert: () -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    topContent: @Composable () -> Unit = {},
    scrollableContent: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,

        ) {
        Column(modifier = Modifier.modifierBottomSheet(false)) {
            IconAndText(
                modifier = Modifier.fillMaxWidth(),
                iconRes = version?.toDrawRes() ?: AnimalCountVersion.ADD.toDrawRes(),
                valueString = stringResource(
                    version?.toResId() ?: AnimalCountVersion.ADD.toResId()
                ),
                horizontalArrangement = Arrangement.Center,
                textStyle = textBold_18
            )
            topContent()
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                scrollableContent()
            }
            ButtonPanel(
                isEntry = isEntry,
                entryButton = intEntryButton,
                onClickInsert = onInsert,
                onClickUpdate = onUpdate,
                onClickDelete = onDelete
            )

        }
    }
}

@Composable
fun BaseBottomSheet(
    modifier: Modifier = Modifier,
    title: String = "",
    supText: String? = null,
    @DrawableRes iconRes: Int? = null,
    colors: List<Color> = listOf(white, white),
    isScroll: Boolean = true,
    skipPartiallyExpanded: Boolean = true,
    onDismissRequest: () -> Unit,
    onSecondDismissRequest: (() -> Unit)? = null,
    contentBottom: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val bottomPadding = if (contentBottom != null) 80.dp else 0.dp
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
//                .fillMaxSize()
//                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .modifierBottomSheet(isScroll)
                    .padding(bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            iconRes?.let {
                                IconIndicatorsAnimal(
                                    icon = it,
                                    colors = colors
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextLine(

                                    valueString = title,
                                    textStyle = textBold_20
                                )
                                supText?.let {
                                    Text(it, style = text_12, color = gray_7)
                                }
                            }
                        }
                        IconButton(onClick = onSecondDismissRequest ?: onDismissRequest) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = gray_6
                )
                Column(
                    modifier = Modifier.then(
                        if (isScroll)
                            Modifier.verticalScroll(rememberScrollState())
                        else Modifier
                    )
                ) {
                    content()
                }
            }
            contentBottom?.let {
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                ) {
                    it()
                }
            }
        }
    }
}
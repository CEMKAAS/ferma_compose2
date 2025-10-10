package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId

import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel

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
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.modifierBottomSheet()) {
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

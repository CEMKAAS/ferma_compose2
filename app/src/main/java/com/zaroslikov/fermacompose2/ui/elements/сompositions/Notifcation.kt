package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.gray_5
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.ui.elements.OutlineIconButtonNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.TimeOutlinedTextFieldNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.NotificationParameters
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.ProductKillCard


@Composable
fun NotificationFun(
    notificationList: List<NotificationParameters>,
    time: String,
    note: String,
    isEntry: Boolean,
    maxCount: Int = 4,
    onChoiceClick: (Long) -> Unit,
    onRemoveClick: (Long) -> Unit,
    onCancelClick: () -> Unit,
    onTimeChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        notificationList.forEachIndexed { index, product ->
            ProductKillCard(
                number = index + 1,
                name = product.time,
                value = product.note,
                suffix = null,
                isEditMode = product.isEntry,
                onClick = { onChoiceClick(product.localId) },
            ) { onRemoveClick(product.localId) }
        }
        if (notificationList.size < maxCount || !isEntry)
            HorizontalDivider(thickness = 1.dp, color = gray_5)

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
                        onClick = onCancelClick
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
        if (notificationList.size <= maxCount || !isEntry) {
            TimeOutlinedTextFieldNew(
                time = time,
                onValueChange = onTimeChange,
                intRes = R.string.entry_bookmark_time_notification,
                isBorderCard = false
            )
            OutlinedTextNoteNew(
                value = note,
                onValueChange = onNoteChange,
                labelIntRes = R.string.entry_bookmark_text_notification,
                supportingText = R.string.entry_bookmark_support_text_notification,
                minLines = 2,
                isBorderCard = false
            )
            OutlineIconButtonNew(
                enabled = true,
                isEntry = isEntry,
                intRes = R.string.button_text_add_notification
            ) {
                if (isEntry) onAddClick() else onEditClick()
            }
        }
    }
}
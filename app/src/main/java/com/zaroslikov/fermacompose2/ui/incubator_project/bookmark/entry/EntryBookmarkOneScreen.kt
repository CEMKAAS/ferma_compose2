package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.typeEggList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.gray_5
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.OutlineIconButtonNew
import com.zaroslikov.fermacompose2.ui.elements.ProductKillCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDropdownMenuNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDropdownMenuTypeEgg
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.TimeOutlinedTextFieldNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.FunctionIncubatorCard

@Composable
fun EntryBookmarkOneScreen(
    modifier: Modifier = Modifier,
    isEntry: Boolean,
    state: EntryBookmark,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EntryValue(state, onIntent)
        FunctionIncubatorCard(
            isAutoRotation = state.autoRotation,
            isAutoVentilation = state.autoVentilation,
            onAutoRotationCheckedChange = {
                onIntent(EntryBookmarkIntent.AutoRotationClicked(it))
            },
            onAutoVentilationCheckedChange = {
                onIntent(EntryBookmarkIntent.AutoVentilationClicked(it))
            }
        )
        NotificationCard(
            state = state,
            notificationList = state.notificationList,
            onIntent = onIntent
        )
        NoteCard(state.note) { onIntent(EntryBookmarkIntent.NoteChanged(it)) }
        SaveButton(
            isInsertProject = isEntry,
            enabledButton = state.enabledButton(),
            onInsertClick = { onIntent(EntryBookmarkIntent.Insert) },
            onUpdateClick = { onIntent(EntryBookmarkIntent.Update) }
        )
    }
}

@Composable
private fun EntryValue(
    state: EntryBookmark,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    CardFieldNew(
        padding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextNew(
            value = state.title,
            onValueChange = { onIntent(EntryBookmarkIntent.TitleChanged(it)) },
            isError = state.error.isErrorTitle,
            labelIntRes = R.string.entry_bookmark_title,
            supportingText = R.string.entry_bookmark_title_support,
            isBorderCard = false
        )
        OutlinedTextDropdownMenuTypeEgg(
            value = state.type,
            onValueChange = { onIntent(EntryBookmarkIntent.TypeChanged(it)) },
            titleList = typeEggList,
            labelIntRes = R.string.entry_bookmark_type,
            intResSup = R.string.is_empty,
            isBorderCard = false
        )
        OutlinedTextDropdownMenuNew(
            value = state.breed,
            onValueChange = { onIntent(EntryBookmarkIntent.BreedChanged(it)) },
            titleList = state.breedList,
            labelIntRes = R.string.entry_bookmark_breed,
            intResSup = R.string.entry_bookmark_support_breed,
            isBorderCard = false
        )
        OutlinedNumberNew(
            value = state.count,
            onValueChange = { onIntent(EntryBookmarkIntent.CountChanged(it)) },
            intRes = R.string.entry_bookmark_count_egg,
            intResSup = R.string.entry_bookmark_support_count_egg,
            isError = state.error.isErrorCount,
            suffix = Suffix.PIECES,
            isBorderCard = false
        )
        OutlinedNumberNew(
            value = state.price,
            onValueChange = { onIntent(EntryBookmarkIntent.PriceChanged(it)) },
            intRes = R.string.entry_bookmark_price_egg,
            intResSup = R.string.entry_bookmark_price_support,
            suffix = Suffix.PIECES,
            isBorderCard = false
        )
        OutlinedTextDateNew(
            value = state.date,
            onValueChange = { onIntent(EntryBookmarkIntent.DateClicked(it)) },
            intRes = R.string.entry_bookmark_data,
            isBorderCard = false
        )
        TimeOutlinedTextFieldNew(
            time = state.time,
            onValueChange = { onIntent(EntryBookmarkIntent.TimeClicked(it)) },
            intRes = R.string.entry_bookmark_time,
            isBorderCard = false
        )
    }
}

@Composable
private fun NoteCard(
    note: String,
    onValueChange: (String) -> Unit
) {
    CardNewWithTitle(
        titleRes = R.string.entry_bookmark_note
    ) {
        OutlinedTextNoteNew(
            value = note,
            onValueChange = onValueChange,
            leadingIconRes = null,
            supportingText = R.string.entry_bookmark_note_text,
            labelIntRes = null,
            isBorderCard = false
        )
    }
}

@Composable
private fun NotificationCard(
    state: EntryBookmark,
    notificationList: List<NotificationParameters>,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    CardNewWithTitle(
        titleRes = R.string.entry_bookmark_notification
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
                    onClick = { onIntent(EntryBookmarkIntent.ChoiceNotificationClicked(index)) },
                ) { onIntent(EntryBookmarkIntent.RemoveNotificationClicked(index)) }
            }
            if (notificationList.isNotEmpty())
                HorizontalDivider(thickness = 1.dp, color = gray_5)

            if (!state.currentNotification.isEntry)
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
                            onClick = { onIntent(EntryBookmarkIntent.CancelNotificationClicked) }
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

            TimeOutlinedTextFieldNew(
                time = state.currentNotification.time,
                onValueChange = { onIntent(EntryBookmarkIntent.TimeNotificationChanged(it)) },
                intRes = R.string.entry_bookmark_time_notification,
                isBorderCard = false
            )
            OutlinedTextNoteNew(
                value = state.currentNotification.note,
                onValueChange = { onIntent(EntryBookmarkIntent.NoteNotificationChanged(it)) },
                labelIntRes = R.string.entry_bookmark_text_notification,
                supportingText = R.string.entry_bookmark_support_text_notification,
                minLines = 2,
                isBorderCard = false
            )
        }
        OutlineIconButtonNew(
            enabled = true,
            isEntry = state.currentNotification.isEntry,
            intRes = R.string.button_text_add_notification
        ) {
            if (state.currentNotification.isEntry) onIntent(EntryBookmarkIntent.AddNotificationClicked)
            else onIntent(EntryBookmarkIntent.EditNotificationClicked)
        }
    }
}

@Composable
private fun SaveButton(
    isInsertProject: Boolean,
    enabledButton: Boolean,
    onInsertClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(if (isInsertProject) R.string.entry_bookmark_screen_save else R.string.button_text_edit_title),
        onClick = {
            if (isInsertProject) onInsertClick()
            else onUpdateClick()
        },
        colors = listOf(orang_9, orang_15),
        enable = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}
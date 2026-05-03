package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.typeEggList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDropdownMenuNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDropdownMenuTypeEgg
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.TimeOutlinedTextFieldNew
import com.zaroslikov.fermacompose2.ui.elements.сompositions.NotificationFun
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
        EntryValue(isEntry, state, onIntent)
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
            notificationList = state.notificationList.filter { it.isVisibility },
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
    isEntry: Boolean,
    state: EntryBookmark,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    CardFieldNew(
        padding = PaddingValues(24.dp)
    ) {
        Column(
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
                enable = isEntry,
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
                intResError = R.string.entry_bookmark_error_count,
                isError = state.error.isErrorLargeCount,
                suffix = Suffix.PIECES,
                isBorderCard = false
            )
            if (!isEntry)
                OutlinedNumberNew(
                    value = state.rejectedCount,
                    onValueChange = { onIntent(EntryBookmarkIntent.RejectedCountChanged(it)) },
                    intRes = R.string.entry_bookmark_rejected_count_egg,
                    intResSup = R.string.entry_bookmark_support_count_egg,
                    intResError = R.string.entry_bookmark_error_reject_count,
                    isError = state.error.isErrorRejectedCount,
                    suffix = Suffix.PIECES,
                    isBorderCard = false
                )
            OutlinedPriceInputNew(
                price = state.price,
                onPriceChange = {
                    onIntent(EntryBookmarkIntent.PriceChanged(it))
                },
                priceAll = state.priceAll,
                isAutoCalculate = state.isAutoPrice,
                isManyCount = true,
                isBorderCard = false,
                onAutoCalculate = {
                    onIntent(EntryBookmarkIntent.AutoPriceClicked(it))
                },
                count = state.count,
                countSuffix = Suffix.PIECES,
                priceSuffix = state.currencySuffix,
                leadingIconRes = null,
                supportTextRes = R.string.entry_bookmark_support_text_all_price,
                supportTextResAutoCal = R.string.entry_bookmark_support_text_price,
                tooltipTextResAutoCal = R.string.entry_bookmark_tooltip_auto_calculate_price,
            )
            OutlinedTextDateNew(
                value = state.startDate,
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
        NotificationFun(
            notificationList = notificationList,
            time = state.currentNotification.time,
            note = state.currentNotification.note,
            isEntry = state.currentNotification.isEntry,
            onChoiceClick = { onIntent(EntryBookmarkIntent.ChoiceNotificationClicked(it)) },
            onRemoveClick = { onIntent(EntryBookmarkIntent.RemoveNotificationClicked(it)) },
            onCancelClick = { onIntent(EntryBookmarkIntent.CancelNotificationClicked) },
            onTimeChange = { onIntent(EntryBookmarkIntent.TimeNotificationChanged(it)) },
            onNoteChange = { onIntent(EntryBookmarkIntent.NoteNotificationChanged(it)) },
            onAddClick = { onIntent(EntryBookmarkIntent.AddNotificationClicked) },
            onEditClick = { onIntent(EntryBookmarkIntent.EditNotificationClicked) }
        )
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
        enabled = enabledButton,
        paddingValues = PaddingValues(vertical = 14.dp)
    )
}
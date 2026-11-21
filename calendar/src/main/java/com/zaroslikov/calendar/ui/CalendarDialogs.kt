package com.zaroslikov.calendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zaroslikov.calendar.R
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarRepeat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@Composable
internal fun CalendarDialogs(state: CalendarState, onIntent: (CalendarIntent) -> Unit) {
    val dismiss = { onIntent(CalendarIntent.Dismiss) }
    when (val overlay = state.overlay) {
        null -> Unit
        CalendarOverlay.Filters -> FiltersSheet(state, onIntent, dismiss)
        is CalendarOverlay.Details -> {
            val event = state.events.firstOrNull { it.id == overlay.event.id } ?: overlay.event
            EventDetails(event, onIntent, dismiss)
        }
        is CalendarOverlay.Edit -> EventEditorSheet(overlay, state.savingEvent, onIntent, dismiss)
        is CalendarOverlay.Scope -> ScopeDialog(overlay.delete, state.savingEvent, onIntent, dismiss)
        is CalendarOverlay.Delete -> AlertDialog(
            onDismissRequest = dismiss,
            title = { Text(stringResource(R.string.calendar_delete_event)) },
            text = { Text(overlay.event.title) },
            confirmButton = {
                TextButton(
                    enabled = !state.savingEvent,
                    onClick = { onIntent(CalendarIntent.ConfirmDelete) },
                ) {
                    Text(stringResource(R.string.calendar_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { CancelButton(dismiss, enabled = !state.savingEvent) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarSheet(
    title: String,
    dismiss: () -> Unit,
    saving: Boolean = false,
    footer: @Composable FlowRowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val savingNow by rememberUpdatedState(saving)
    val confirmValueChange = remember {
        { value: SheetValue -> value != SheetValue.Hidden || !savingNow }
    }
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        confirmValueChange = confirmValueChange,
    )
    // ModalBottomSheet учитывает отступ клавиатуры для всего содержимого.
    ModalBottomSheet(
        onDismissRequest = { if (!savingNow) dismiss() },
        sheetState = sheetState,
        sheetGesturesEnabled = !saving,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = !saving,
            shouldDismissOnClickOutside = !saving,
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 4.dp).semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
            )
            content()
        }
        HorizontalDivider(Modifier.padding(top = 8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = footer,
        )
    }
}

@Composable
private fun FiltersSheet(state: CalendarState, onIntent: (CalendarIntent) -> Unit, dismiss: () -> Unit) {
    CalendarSheet(
        title = stringResource(R.string.calendar_filters),
        dismiss = dismiss,
        footer = { Button(onClick = dismiss) { Text(stringResource(R.string.calendar_close)) } },
    ) {
        when {
            state.isLoadingCategories -> LinearProgressIndicator(Modifier.fillMaxWidth())
            state.categoriesLoadFailed -> {
                Text(stringResource(R.string.calendar_filter_load_error))
                OutlinedButton(onClick = { onIntent(CalendarIntent.RetryFilters) }) {
                    Text(stringResource(R.string.calendar_retry))
                }
            }
            else -> CalendarCategory.entries.forEach { category ->
                val enabled = category !in state.savingCategories
                val checked = category in state.categories
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .toggleable(checked, enabled = enabled, role = Role.Switch) {
                            onIntent(CalendarIntent.Filter(category, it))
                        },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CategoryDot(category, Modifier.size(8.dp))
                    Text(
                        text = stringResource(category.label),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Switch(checked = checked, onCheckedChange = null, enabled = enabled)
                }
            }
        }
    }
}

@Composable
private fun EventDetails(event: CalendarEvent, onIntent: (CalendarIntent) -> Unit, dismiss: () -> Unit) {
    CalendarSheet(
        title = event.title,
        dismiss = dismiss,
        footer = {
            if (event.occurrence == null) {
                Button(onClick = dismiss) { Text(stringResource(R.string.calendar_close)) }
            } else {
                OutlinedButton(onClick = dismiss) { Text(stringResource(R.string.calendar_close)) }
                OutlinedButton(
                    onClick = { onIntent(CalendarIntent.Delete) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(R.string.calendar_delete))
                }
                Button(onClick = { onIntent(CalendarIntent.Edit) }) {
                    Text(stringResource(R.string.calendar_edit))
                }
            }
        },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            CalendarEventIcon(event, Modifier.size(20.dp))
            Text(stringResource(event.label))
        }
        Text("${event.date.calendarFullDate()} · ${calendarTimeLabel(event.time)}")
        val task = event.task
        val status = event.statusTitle ?: task?.let {
            stringResource(if (it.completed) R.string.calendar_completed else R.string.calendar_not_completed)
        }
        if (!status.isNullOrBlank()) Text(status)

        val series = event.occurrence?.series
        val repeat = if (event.annual) CalendarRepeat.YEARLY else event.repeat ?: series?.repeat
        if (repeat != null && repeat != CalendarRepeat.NONE) {
            val repeatLabel = stringResource(repeat.label)
            val repeatUntil = event.repeatUntil ?: series?.repeatUntil
            Text(
                if (repeatUntil == null) repeatLabel
                else stringResource(R.string.calendar_repeat_until, repeatLabel, repeatUntil.calendarFullDate()),
            )
        }
        if (event.note.isNotBlank()) Text(event.note)
    }
}

@Composable
private fun ScopeDialog(
    delete: Boolean,
    saving: Boolean,
    onIntent: (CalendarIntent) -> Unit,
    dismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(stringResource(if (delete) R.string.calendar_delete_repeating else R.string.calendar_edit_repeating))
        },
        text = {
            Column {
                OutlinedButton(
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onIntent(CalendarIntent.ChooseScope(singleOccurrence = true)) },
                ) {
                    Text(stringResource(R.string.calendar_single_occurrence))
                }
                OutlinedButton(
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onIntent(CalendarIntent.ChooseScope(singleOccurrence = false)) },
                ) {
                    Text(stringResource(R.string.calendar_entire_series))
                }
            }
        },
        confirmButton = { CancelButton(dismiss, enabled = !saving) },
    )
}

private enum class EventPicker { DATE, TIME, END }

@Composable
private fun EventEditorSheet(
    editor: CalendarOverlay.Edit,
    saving: Boolean,
    onIntent: (CalendarIntent) -> Unit,
    dismiss: () -> Unit,
) {
    val draft = editor.draft
    val enabled = !saving
    var picker by rememberSaveable { mutableStateOf<EventPicker?>(null) }
    val endError = editor.endError
    CalendarSheet(
        title = stringResource(if (editor.occurrence == null) R.string.calendar_new_event else R.string.calendar_edit_event),
        dismiss = dismiss,
        saving = saving,
        footer = {
            OutlinedButton(onClick = dismiss, enabled = enabled) {
                Text(stringResource(R.string.calendar_cancel))
            }
            Button(
                enabled = enabled && draft.title.isNotBlank() && endError == null,
                onClick = { onIntent(CalendarIntent.Save) },
            ) {
                Text(stringResource(R.string.calendar_save))
            }
        },
    ) {
        val focusManager = LocalFocusManager.current
        val openPicker = { next: EventPicker ->
            focusManager.clearFocus()
            picker = next
        }
        OutlinedTextField(
            value = draft.title,
            onValueChange = { onIntent(CalendarIntent.UpdateDraft(draft.copy(title = it))) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.calendar_event_title)) },
            singleLine = true,
            enabled = enabled,
        )
        EditorValue(
            label = stringResource(R.string.calendar_event_date),
            value = draft.date.calendarFullDate(),
            icon = Icons.Outlined.CalendarMonth,
            enabled = enabled,
            onClick = { openPicker(EventPicker.DATE) },
        )
        EditorValue(
            label = stringResource(R.string.calendar_event_time),
            value = calendarTimeLabel(draft.time),
            icon = Icons.Outlined.Schedule,
            enabled = enabled,
            onClick = { openPicker(EventPicker.TIME) },
        )
        if (!editor.singleOccurrence) {
            RepeatMenu(draft.repeat, enabled) { repeat ->
                val end = draft.repeatUntil.takeUnless { repeat == CalendarRepeat.NONE }
                onIntent(CalendarIntent.UpdateDraft(draft.copy(repeat = repeat, repeatUntil = end)))
            }
        }
        if (editor.showsRepeatEnd) {
            Text(stringResource(R.string.calendar_repeat_end), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !editor.endsOnDate,
                    enabled = enabled,
                    onClick = { if (editor.endsOnDate) onIntent(CalendarIntent.ChangeEndMode(false)) },
                    label = { Text(stringResource(R.string.calendar_repeat_never)) },
                )
                FilterChip(
                    selected = editor.endsOnDate,
                    enabled = enabled,
                    onClick = { if (!editor.endsOnDate) onIntent(CalendarIntent.ChangeEndMode(true)) },
                    label = { Text(stringResource(R.string.calendar_repeat_on_date)) },
                )
            }
            if (editor.endsOnDate) {
                EditorValue(
                    label = stringResource(R.string.calendar_repeat_end_date),
                    value = draft.repeatUntil?.calendarFullDate() ?: stringResource(R.string.calendar_pick_date),
                    icon = Icons.Outlined.CalendarMonth,
                    enabled = enabled,
                    onClick = { openPicker(EventPicker.END) },
                )
                endError?.let {
                    Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                }
            }
        }
        OutlinedTextField(
            value = draft.note,
            onValueChange = { onIntent(CalendarIntent.UpdateDraft(draft.copy(note = it))) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.calendar_event_note)) },
            maxLines = 5,
            enabled = enabled,
        )
        if (editor.saveFailed) {
            Text(stringResource(R.string.calendar_save_error), color = MaterialTheme.colorScheme.error)
        }
    }

    val closePicker = { picker = null }
    when (picker) {
        null -> Unit
        EventPicker.DATE -> CalendarDatePicker(draft.date, closePicker) {
            onIntent(CalendarIntent.UpdateDraft(draft.copy(date = it)))
            closePicker()
        }
        EventPicker.END -> CalendarDatePicker(
            date = maxOf(draft.repeatUntil ?: draft.date, draft.date),
            dismiss = closePicker,
            minimum = draft.date,
        ) {
            onIntent(CalendarIntent.UpdateDraft(draft.copy(repeatUntil = it)))
            closePicker()
        }
        EventPicker.TIME -> CalendarTimePicker(draft.time, closePicker) {
            onIntent(CalendarIntent.UpdateDraft(draft.copy(time = it)))
            closePicker()
        }
    }
}

@Composable
private fun RepeatMenu(repeat: CalendarRepeat, enabled: Boolean, onClick: (CalendarRepeat) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        EditorValue(
            label = stringResource(R.string.calendar_event_repeat),
            value = stringResource(repeat.label),
            icon = Icons.Outlined.ExpandMore,
            enabled = enabled,
            onClick = { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CalendarRepeat.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(option.label)) },
                    onClick = {
                        onClick(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun EditorValue(label: String, value: String, icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(icon, null, Modifier.padding(start = 8.dp).size(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarDatePicker(
    date: LocalDate,
    dismiss: () -> Unit,
    minimum: LocalDate? = null,
    onConfirm: (LocalDate) -> Unit,
) {
    val picker = remember(date, minimum) {
        DatePickerState(
            locale = calendarLocale,
            initialSelectedDate = date,
            yearRange = calendarYearRange,
            selectableDates = object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean = minimum == null || year >= minimum.year

                override fun isSelectableDate(utcTimeMillis: Long): Boolean = minimum == null ||
                    Instant.ofEpochMilli(utcTimeMillis).atOffset(ZoneOffset.UTC).toLocalDate() >= minimum
            },
        )
    }
    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(enabled = picker.getSelectedDate() != null, onClick = { picker.getSelectedDate()?.let(onConfirm) }) {
                Text(stringResource(R.string.calendar_ok))
            }
        },
        dismissButton = { CancelButton(dismiss) },
    ) {
        DatePicker(
            state = picker,
            modifier = Modifier.verticalScroll(rememberScrollState()),
            title = null,
            headline = {
                Text(
                    text = picker.getSelectedDate()?.calendarFullDate() ?: stringResource(R.string.calendar_pick_date),
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTimePicker(time: LocalTime?, dismiss: () -> Unit, onConfirm: (LocalTime?) -> Unit) {
    val picker = rememberTimePickerState(initialHour = time?.hour ?: 9, initialMinute = time?.minute ?: 0, is24Hour = true)
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(stringResource(R.string.calendar_event_time)) },
        text = { TimeInput(state = picker) },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(picker.hour, picker.minute)) }) {
                Text(stringResource(R.string.calendar_ok))
            }
        },
        dismissButton = {
            CancelButton(dismiss)
            TextButton(onClick = { onConfirm(null) }) {
                Text(stringResource(R.string.calendar_all_day))
            }
        },
    )
}

@Composable
private fun CancelButton(onClick: () -> Unit, enabled: Boolean = true) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(stringResource(R.string.calendar_cancel))
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.animal.pregnancy.LitterMode
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseExposedDropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.ExposedDropdownMenuEnum
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButtonWitchAnimate
import com.zaroslikov.fermacompose2.ui.elements.сompositions.TimePicker
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import java.time.LocalTime

/** Все листы и диалоги экрана беременности. */
@Composable
fun PregnancySheets(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    if (state.start.isOpen && state.deleteId == null)
        PregnancyStartSheet(
            form = state.start,
            animalName = state.animal.name,
            sireCandidates = state.sireCandidates,
            matingDateError = state.matingDateError,
            isSireError = state.isSireError,
            canConfirm = state.canConfirmStart,
            isSaving = state.isSaving,
            isEditingFinished = state.isEditingFinished,
            editingSireId = state.editingSireId,
            editingSireName = state.editingSireName
        ) { event ->
            onIntent(
                when (event) {
                    StartFormEvent.Confirm -> AnimalPregnancyIntent.StartConfirmed
                    StartFormEvent.Dismiss -> AnimalPregnancyIntent.OpenStart(false)
                    is StartFormEvent.Delete -> AnimalPregnancyIntent.OpenDelete(event.id)
                    else -> AnimalPregnancyIntent.StartFormChanged(event)
                }
            )
        }
    if (state.birth.isOpen)
        PregnancyBirthSheet(state = state, onIntent = onIntent)
    if (state.interrupt.isOpen)
        PregnancyInterruptSheet(state = state, onIntent = onIntent)
    if (state.editNote != null && state.deleteId == null)
        PregnancyNoteSheet(state = state, onIntent = onIntent)
    if (state.dayNote.isOpen && state.deleteId == null) {
        if (state.dayNote.isDeleteConfirm)
            WarningDeleteBottomSheet(
                titleRes = R.string.pregnancy_day_note_delete_title,
                supportRes = R.string.pregnancy_day_notes_title,
                textButtonRes = R.string.button_delete,
                textRes = R.string.pregnancy_day_note_delete_text,
                onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenDayNoteDelete(false)) },
                onDeleteClick = { if (!state.isSaving) onIntent(AnimalPregnancyIntent.DayNoteDeleted) }
            ) {
                Text(
                    text = dayNoteHeader(state.dayNoteDay, state.dayNote.date, state.dayNote.time),
                    style = text_12,
                    color = animal_1
                )
                Text(text = state.dayNote.text, style = text_14, color = marengo)
            }
        else
            PregnancyDayNoteSheet(state = state, onIntent = onIntent)
    }
    state.deleteId?.let { id ->
        WarningDeleteBottomSheet(
            titleRes = R.string.pregnancy_delete_title,
            supportRes = R.string.pregnancy_title,
            textButtonRes = R.string.button_delete,
            textRes = R.string.pregnancy_delete_text,
            onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenDelete(null)) },
            onDeleteClick = { if (!state.isSaving) onIntent(AnimalPregnancyIntent.DeleteConfirmed) }
        ) {
            state.pregnancyById[id]?.let {
                Text(text = pregnancySummary(it), style = text_14, color = marengo)
            }
        }
    }
}

/** Роды: дата, живые ♀/♂, мертворождённые, отдельные особи или группа-помёт. */
@Composable
private fun PregnancyBirthSheet(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val form = state.birth
    val terms = state.norms?.terms
    BaseBottomSheet(
        title = terms?.birthName ?: stringResource(R.string.pregnancy_birth_date),
        supText = state.animal.name,
        iconRes = R.drawable.baseline_favorite_24,
        colors = listOf(animal_1, animal_2),
        onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenBirth(false)) }
    ) {
        // BaseBottomSheet не разносит содержимое — отступы между полями задаём сами
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextDateNew(
                    value = form.date,
                    onValueChange = { onIntent(AnimalPregnancyIntent.BirthDateChanged(it)) },
                    intRes = R.string.pregnancy_birth_date,
                    isLimit = true,
                    isBorderCard = false
                )
                state.birthDateError?.let { ErrorText(it.toResId()) }
                state.pretermDays?.let {
                    Text(
                        text = stringResource(R.string.pregnancy_birth_preterm, it),
                        style = text_12,
                        color = orang_9
                    )
                }
            }
            OutlinedTextCountNew(
                value = form.female,
                onValueChange = { onIntent(AnimalPregnancyIntent.FemaleChanged(it)) },
                suffix = null,
                isError = state.isFemaleCountError,
                drawableRes = R.drawable.baseline_female_24,
                intRes = R.string.pregnancy_born_female,
                isNecessarily = false,
                isBorderCard = false
            )
            OutlinedTextCountNew(
                value = form.male,
                onValueChange = { onIntent(AnimalPregnancyIntent.MaleChanged(it)) },
                suffix = null,
                isError = state.isMaleCountError,
                drawableRes = R.drawable.baseline_male_24,
                intRes = R.string.pregnancy_born_male,
                isNecessarily = false,
                isBorderCard = false
            )
            OutlinedTextCountNew(
                value = form.stillborn,
                onValueChange = { onIntent(AnimalPregnancyIntent.StillbornChanged(it)) },
                suffix = null,
                isError = state.isStillbornCountError,
                drawableRes = R.drawable.outline_cancel_24,
                intRes = R.string.pregnancy_stillborn,
                isNecessarily = false,
                isBorderCard = false
            )
            if (state.isBirthEmpty)
                ErrorText(R.string.pregnancy_birth_empty_error)
            if (state.isLitterTooBig)
                Text(
                    text = stringResource(R.string.pregnancy_litter_too_many, PregnancyRules.MAX_LITTER),
                    style = text_12,
                    color = error_base
                )
            if (state.bornAlive > 0) {
                ChoiceButtons(
                    options = listOf(R.string.pregnancy_litter_individuals, R.string.pregnancy_litter_group),
                    selectedIndex = form.mode.ordinal,
                    onSelect = { onIntent(AnimalPregnancyIntent.LitterModeChanged(LitterMode.entries[it])) }
                )
                when (form.mode) {
                    LitterMode.INDIVIDUALS -> BaseOutlinedTextNew(
                        value = form.prefix,
                        onValueChange = { onIntent(AnimalPregnancyIntent.PrefixChanged(it)) },
                        leadingIconRes = R.drawable.baseline_pets_24,
                        labelIntRes = R.string.pregnancy_litter_prefix,
                        intResSup = R.string.animal_group_split_prefix_hint,
                        isError = form.prefix.isBlank(),
                        isNecessarily = true
                    )

                    LitterMode.GROUP -> BaseOutlinedTextNew(
                        value = form.groupName,
                        onValueChange = { onIntent(AnimalPregnancyIntent.GroupNameChanged(it)) },
                        leadingIconRes = R.drawable.icon_group,
                        labelIntRes = R.string.pregnancy_litter_group_name,
                        intResSup = R.string.pregnancy_litter_group,
                        isError = form.groupName.isBlank(),
                        isNecessarily = true
                    )
                }
                litterPreview(state)?.let {
                    Text(
                        text = stringResource(R.string.pregnancy_litter_preview, it),
                        style = text_14,
                        color = marengo
                    )
                }
            }
            OutlinedTextNoteNew(
                value = form.note,
                onValueChange = { onIntent(AnimalPregnancyIntent.BirthNoteChanged(it)) },
                isBorderCard = false
            )
            ConfirmButton(
                text = terms?.birthName ?: stringResource(R.string.button_save),
                enabled = state.canConfirmBirth
            ) {
                focusManager.clearFocus()
                onIntent(AnimalPregnancyIntent.BirthConfirmed)
            }
        }
    }
}

/** «Крольчонок 1 ♀, Крольчонок 2 ♂…» или «Крольчата (Маруся, 06.10.2026) — 5». */
@Composable
private fun litterPreview(state: AnimalPregnancyState): String? = when (state.birth.mode) {
    LitterMode.INDIVIDUALS -> {
        val female = state.bornFemale ?: 0
        val femaleFormat = stringResource(R.string.pregnancy_young_female)
        val maleFormat = stringResource(R.string.pregnancy_young_male)
        state.birthNames
            .mapIndexed { i, name -> (if (i < female) femaleFormat else maleFormat).format(name) }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(limit = PREVIEW_LIMIT)
    }

    LitterMode.GROUP -> state.birth.groupName.trim()
        .takeIf { it.isNotEmpty() }
        ?.let { stringResource(R.string.pregnancy_litter_group_preview, it, state.bornAlive) }
}

/** Прерывание: причина (из норм вида), дата, комментарий (обязателен для «Другое»). */
@Composable
private fun PregnancyInterruptSheet(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val form = state.interrupt
    val reasons = state.norms?.interruptReasons
        ?: PregnancyNorms.of(state.active?.species ?: state.animal.species)?.interruptReasons
        ?: emptyList()
    BaseBottomSheet(
        title = stringResource(R.string.pregnancy_interrupt_title),
        supText = state.animal.name,
        iconRes = R.drawable.outline_cancel_24,
        colors = listOf(red_13, error_base),
        onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenInterrupt(false)) }
    ) {
        // BaseBottomSheet не разносит содержимое — отступы между полями задаём сами
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuEnum(
                valueList = reasons,
                dropdownMenuItem = { _, reason, closeMenu ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(reason.toResId())) },
                        trailingIcon = if (reason == form.reason) {
                            { Icon(Icons.Default.Done, contentDescription = null) }
                        } else null,
                        onClick = {
                            onIntent(AnimalPregnancyIntent.ReasonChanged(reason))
                            closeMenu()
                        }
                    )
                }
            ) {
                BaseOutlinedTextNew(
                    modifier = it.first,
                    value = form.reason?.let { reason -> stringResource(reason.toResId()) }.orEmpty(),
                    onValueChange = { },
                    readOnly = true,
                    leadingIconRes = R.drawable.outline_info_24,
                    labelIntRes = R.string.pregnancy_interrupt_reason,
                    intResSup = R.string.pregnancy_interrupt_title,
                    isNecessarily = true
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextDateNew(
                    value = form.date,
                    onValueChange = { onIntent(AnimalPregnancyIntent.InterruptDateChanged(it)) },
                    intRes = R.string.pregnancy_interrupt_date,
                    isLimit = true,
                    isBorderCard = false
                )
                state.interruptDateError?.let { ErrorText(it.toResId()) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextNoteNew(
                    value = form.note,
                    onValueChange = { onIntent(AnimalPregnancyIntent.InterruptNoteChanged(it)) },
                    isBorderCard = false
                )
                if (state.isInterruptNoteRequired)
                    ErrorText(R.string.pregnancy_interrupt_note_required)
            }
            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.pregnancy_interrupt_button),
                colors = listOf(red_13, error_base),
                enabled = state.canConfirmInterrupt,
                paddingValues = PaddingValues(16.dp)
            ) {
                focusManager.clearFocus()
                onIntent(AnimalPregnancyIntent.InterruptConfirmed)
            }
        }
    }
}

/** Заметка к записи истории + удаление записи. */
@Composable
private fun PregnancyNoteSheet(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val (id, note) = state.editNote ?: return
    val pregnancy = state.pregnancyById[id] ?: return
    BaseBottomSheet(
        title = stringResource(R.string.pregnancy_note_edit_title),
        supText = pregnancySummary(pregnancy),
        iconRes = R.drawable.baseline_sticky_note_2_24,
        colors = listOf(animal_1, animal_2),
        onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenEditNote(null)) }
    ) {
        // BaseBottomSheet не разносит содержимое — отступы между полями задаём сами
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextNoteNew(
                value = note,
                onValueChange = { onIntent(AnimalPregnancyIntent.EditNoteChanged(it)) },
                isBorderCard = false
            )
            ConfirmButton(
                text = stringResource(R.string.button_save),
                enabled = !state.isSaving
            ) {
                focusManager.clearFocus()
                onIntent(AnimalPregnancyIntent.EditNoteConfirmed)
            }
            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.button_delete),
                iconRes = R.drawable.baseline_delete_24,
                iconColor = error_base,
                textColor = error_base,
                paddingValues = PaddingValues(16.dp),
                enabled = !state.isSaving
            ) {
                onIntent(AnimalPregnancyIntent.OpenDelete(id))
            }
        }
    }
}

/** Заметка за день: дата (по умолчанию сегодня, меняется), день беременности, текст; у сохранённой — удаление. */
@Composable
private fun PregnancyDayNoteSheet(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val form = state.dayNote
    val pregnancy = state.dayNotePregnancy ?: return
    BaseBottomSheet(
        title = stringResource(
            if (form.editingId == null) R.string.pregnancy_day_note_title
            else R.string.pregnancy_day_note_edit_title
        ),
        supText = pregnancySummary(pregnancy),
        iconRes = R.drawable.baseline_sticky_note_2_24,
        colors = listOf(animal_1, animal_2),
        onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenDayNote(false)) }
    ) {
        // BaseBottomSheet не разносит содержимое — отступы между полями задаём сами
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextDateNew(
                    value = form.date,
                    onValueChange = { onIntent(AnimalPregnancyIntent.DayNoteDateChanged(it)) },
                    intRes = R.string.pregnancy_day_note_date,
                    isLimit = true,
                    isBorderCard = false,
                    minDate = pregnancy.matingDate
                )
                state.dayNoteDateError?.let { ErrorText(it.toResId()) }
                state.dayNoteDay?.let {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = stringResource(R.string.pregnancy_day_note_day, it),
                        style = text_12,
                        color = animal_1
                    )
                }
            }
            DayNoteTimeField(
                time = form.time,
                onTimeChange = { onIntent(AnimalPregnancyIntent.DayNoteTimeChanged(it)) }
            )
            // Пустую заметку не сохранить — кнопка неактивна
            OutlinedTextNoteNew(
                value = form.text,
                onValueChange = { onIntent(AnimalPregnancyIntent.DayNoteTextChanged(it)) },
                isBorderCard = false
            )
            ConfirmButton(
                text = stringResource(R.string.button_save),
                enabled = state.canConfirmDayNote
            ) {
                focusManager.clearFocus()
                onIntent(AnimalPregnancyIntent.DayNoteConfirmed)
            }
            if (form.editingId != null)
                BorderButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_delete),
                    iconRes = R.drawable.baseline_delete_24,
                    iconColor = error_base,
                    textColor = error_base,
                    paddingValues = PaddingValues(16.dp),
                    enabled = !state.isSaving
                ) {
                    onIntent(AnimalPregnancyIntent.OpenDayNoteDelete(true))
                }
        }
    }
}

/** Необязательное время: пустое — подсказка «Не обязательно», заполненное — с крестиком сброса. */
@Composable
private fun DayNoteTimeField(time: String, onTimeChange: (String) -> Unit) {
    var isPickerOpen by remember { mutableStateOf(false) }
    if (isPickerOpen)
        TimePicker(
            // Пустое время — пикер открывается на текущем
            time = time.ifBlank { PregnancyDates.formatTime(LocalTime.now()) },
            onDismiss = { isPickerOpen = false }
        ) {
            onTimeChange(it)
            isPickerOpen = false
        }
    BaseOutlinedTextNew(
        value = time,
        onValueChange = { },
        onClick = { isPickerOpen = true },
        onClear = { onTimeChange("") },
        readOnly = true,
        leadingIconRes = R.drawable.baseline_access_time_24,
        labelIntRes = R.string.pregnancy_day_note_time,
        intResSup = R.string.pregnancy_day_note_time_hint,
        isMore = if (time.isNotBlank()) false else null
    )
}

/** «День 12 · 05.10.2026 · 08:30»; день и время — если известны. */
@Composable
internal fun dayNoteHeader(day: Int?, date: String, time: String?): String {
    val base = if (day != null && day >= 1) stringResource(R.string.pregnancy_day_note_header, day, date) else date
    return time?.takeIf { it.isNotBlank() }?.let { "$base · $it" } ?: base
}

/** Однострочная сводка записи для истории, листов и подтверждения удаления. */
@Composable
fun pregnancySummary(p: DomainAnimalPregnancy): String {
    val terms = PregnancyNorms.of(p.species)?.terms
    return when (p.status) {
        PregnancyStatus.ACTIVE -> stringResource(
            R.string.pregnancy_active_summary,
            terms?.pregnantAdjective ?: stringResource(R.string.pregnancy_title),
            p.matingDate
        )

        PregnancyStatus.BIRTH -> stringResource(
            R.string.pregnancy_history_birth,
            terms?.birthName ?: stringResource(R.string.pregnancy_birth_date),
            p.endDate ?: p.matingDate,
            p.bornAlive,
            p.bornFemale,
            p.bornMale,
            p.stillborn
        )

        PregnancyStatus.INTERRUPTED -> stringResource(
            R.string.pregnancy_history_interrupted,
            p.endDate ?: p.matingDate,
            p.interruptReason?.let { stringResource(it.toResId()) }.orEmpty()
        )
    }
}

@StringRes
internal fun PregnancyRules.DateError.toResId(): Int = when (this) {
    PregnancyRules.DateError.INVALID -> R.string.pregnancy_error_date_invalid
    PregnancyRules.DateError.IN_FUTURE -> R.string.pregnancy_error_future
    PregnancyRules.DateError.BEFORE_FEMALE_BIRTH -> R.string.pregnancy_error_before_birth
    PregnancyRules.DateError.BEFORE_MATING -> R.string.pregnancy_error_before_mating
    PregnancyRules.DateError.AFTER_END -> R.string.pregnancy_error_after_end
}

@Composable
internal fun ErrorText(@StringRes textRes: Int) {
    Text(
        modifier = Modifier.padding(horizontal = 4.dp),
        text = stringResource(textRes),
        style = text_12,
        color = error_base
    )
}

@Composable
internal fun ConfirmButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        colors = listOf(animal_1, animal_2),
        enabled = enabled,
        paddingValues = PaddingValues(16.dp),
        onClick = onClick
    )
}

/** Сегментный переключатель на [GroupButtonWitchAnimate]. */
@Composable
internal fun ChoiceButtons(
    options: List<Int>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    GroupButtonWitchAnimate(
        modifier = Modifier.fillMaxWidth(),
        itemCount = options.size,
        selectedIndex = selectedIndex,
        paddingValues = PaddingValues(4.dp)
    ) {
        options.forEachIndexed { index, textRes ->
            val textColor by animateColorAsState(
                if (selectedIndex == index) black_2 else gray_7,
                label = ""
            )
            Text(
                text = stringResource(textRes),
                style = text_14,
                color = textColor,
                fontWeight = if (selectedIndex == index) FontWeight.Medium else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSelect(index) }
                    .padding(PaddingValues(6.dp))
            )
        }
    }
}

private const val PREVIEW_LIMIT = 10

@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseExposedDropdownMenu
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14

/**
 * Лист «Случка»: новая запись или правка существующей ([StartForm.editingId]).
 * Общий для экрана беременности и карточки животного.
 */
@Composable
fun PregnancyStartSheet(
    form: StartForm,
    animalName: String,
    sireCandidates: List<DomainAnimalTable>,
    matingDateError: PregnancyRules.DateError?,
    isSireError: Boolean,
    canConfirm: Boolean,
    isSaving: Boolean,
    /** Для завершённой записи меняется только заметка. */
    isEditingFinished: Boolean = false,
    editingSireId: Long? = null,
    editingSireName: String? = null,
    onEvent: (StartFormEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val isNew = form.editingId == null
    BaseBottomSheet(
        title = stringResource(if (isNew) R.string.pregnancy_start_title else R.string.pregnancy_edit_title),
        supText = animalName,
        iconRes = R.drawable.baseline_favorite_24,
        colors = listOf(animal_1, animal_2),
        onDismissRequest = { onEvent(StartFormEvent.Dismiss) }
    ) {
        // BaseBottomSheet не разносит содержимое — отступы между полями задаём сами
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isEditingFinished) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextDateNew(
                        value = form.matingDate,
                        onValueChange = { onEvent(StartFormEvent.MatingDateChanged(it)) },
                        intRes = R.string.pregnancy_mating_date,
                        isLimit = true,
                        isBorderCard = false
                    )
                    matingDateError?.let { ErrorText(it.toResId()) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.pregnancy_sire), style = text_14, color = dark)
                    ChoiceButtons(
                        options = listOf(
                            R.string.pregnancy_sire_mode_farm,
                            R.string.pregnancy_sire_mode_external,
                            R.string.pregnancy_sire_mode_none
                        ),
                        selectedIndex = form.sireMode.ordinal,
                        onSelect = { onEvent(StartFormEvent.SireModeChanged(SireMode.entries[it])) }
                    )
                    when (form.sireMode) {
                        SireMode.FARM ->
                            if (sireCandidates.isEmpty() && editingSireId == null)
                                Text(
                                    stringResource(R.string.pregnancy_sire_empty),
                                    style = text_12,
                                    color = gray_7
                                )
                            else SireDropdown(
                                form = form,
                                sireCandidates = sireCandidates,
                                editingSireName = editingSireName,
                                isSireError = isSireError,
                                onEvent = onEvent
                            )

                        SireMode.EXTERNAL -> BaseOutlinedTextNew(
                            value = form.sireName,
                            onValueChange = { onEvent(StartFormEvent.SireNameChanged(it)) },
                            leadingIconRes = R.drawable.baseline_male_24,
                            labelIntRes = R.string.pregnancy_sire_name,
                            intResSup = R.string.pregnancy_sire,
                            isError = isSireError,
                            errorText = stringResource(R.string.pregnancy_sire_name_required),
                            isNecessarily = true
                        )

                        SireMode.NONE -> Unit
                    }
                }
            }
            OutlinedTextNoteNew(
                value = form.note,
                onValueChange = { onEvent(StartFormEvent.NoteChanged(it)) },
                isBorderCard = false
            )
            ConfirmButton(
                text = stringResource(if (isNew) R.string.pregnancy_start_button else R.string.button_save),
                enabled = canConfirm
            ) {
                focusManager.clearFocus()
                onEvent(StartFormEvent.Confirm)
            }
            form.editingId?.let { id ->
                BorderButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_delete),
                    iconRes = R.drawable.baseline_delete_24,
                    iconColor = error_base,
                    textColor = error_base,
                    paddingValues = PaddingValues(16.dp),
                    enabled = !isSaving
                ) {
                    onEvent(StartFormEvent.Delete(id))
                }
            }
        }
    }
}

@Composable
private fun SireDropdown(
    form: StartForm,
    sireCandidates: List<DomainAnimalTable>,
    editingSireName: String?,
    isSireError: Boolean,
    onEvent: (StartFormEvent) -> Unit
) {
    val selected = sireCandidates.firstOrNull { it.id == form.sireId }
    BaseExposedDropdownMenu(
        list = sireCandidates,
        dropdownMenuItem = { _, sire, closeMenu ->
            DropdownMenuItem(
                text = { Text(text = sireLabel(sire.name, sire.archive)) },
                trailingIcon = if (sire.id == form.sireId) {
                    { Icon(Icons.Default.Done, contentDescription = null) }
                } else null,
                onClick = {
                    onEvent(StartFormEvent.SireChosen(sire.id))
                    closeMenu()
                }
            )
        },
        content = {
            BaseOutlinedTextNew(
                modifier = it.first,
                value = selected?.let { sire -> sireLabel(sire.name, sire.archive) }
                    ?: editingSireName.orEmpty(),
                onValueChange = { },
                readOnly = true,
                leadingIconRes = R.drawable.baseline_male_24,
                labelIntRes = R.string.pregnancy_sire_choose,
                intResSup = R.string.pregnancy_sire,
                isError = isSireError,
                errorText = stringResource(R.string.pregnancy_sire_required),
                isNecessarily = true
                )
        }
    )
}

@Composable
private fun sireLabel(name: String, isArchive: Boolean): String =
    if (isArchive) stringResource(R.string.pregnancy_sire_archived, name) else name

package com.zaroslikov.todo.ui.assignees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import com.zaroslikov.todo.ui.components.TodoBottomSheet
import com.zaroslikov.todo.ui.components.TodoDatePickerDialog
import com.zaroslikov.todo.ui.components.TodoTextField
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.ui.theme.marengo
import com.zaroslikov.todo.ui.theme.text_14
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.util.isoToDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoAssigneeProfileSheet(
    assignee: DomainTodoAssignee,
    onEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    val profile = assignee.profile
    val details = listOf(
        R.string.todo_assignee_first_name to profile.firstName,
        R.string.todo_assignee_last_name to profile.lastName,
        R.string.todo_assignee_middle_name to profile.middleName,
        R.string.todo_assignee_birthday to isoToDisplay(profile.birthday),
        R.string.todo_assignee_phone to formatTodoAssigneePhone(profile.phone),
        R.string.todo_assignee_email to profile.email,
        R.string.todo_assignee_address to profile.address,
        R.string.todo_assignee_description to profile.description
    ).filter { (_, value) -> value.isNotBlank() }

    TodoBottomSheet(
        title = assignee.name,
        onDismiss = onDismiss,
        headerActions = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.todo_details_close), tint = gray_7)
            }
        },
        footer = {
            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green_shamrock)
            ) {
                Text(stringResource(R.string.todo_assignee_edit_action))
            }
        }
    ) {
        if (details.isEmpty()) {
            Text(stringResource(R.string.todo_assignee_profile_empty), color = marengo, style = text_16)
        }
        for ((label, value) in details) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(stringResource(label), color = marengo, style = text_14)
                Text(value, color = black_2, style = text_16)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoAssigneeFormSheet(
    form: TodoAssigneeForm,
    isSaving: Boolean,
    onIntent: (TodoAssigneesIntent) -> Unit
) {
    val profile = form.profile
    val onDismiss = { onIntent(TodoAssigneesIntent.Dismiss) }
    val updateProfile = { updated: TodoAssigneeProfile ->
        onIntent(TodoAssigneesIntent.ProfileChanged(updated))
    }
    val fallbackName = stringResource(R.string.todo_assignee_default_name)
    val saving by rememberUpdatedState(isSaving)
    var showBirthdayPicker by rememberSaveable { mutableStateOf(false) }
    val nameKeyboard = KeyboardOptions(capitalization = KeyboardCapitalization.Words)

    TodoBottomSheet(
        title = stringResource(
            if (form.originalName == null) R.string.todo_assignee_add else R.string.todo_assignee_edit
        ),
        onDismiss = onDismiss,
        sheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
            confirmValueChange = { it != SheetValue.Hidden || !saving }
        ),
        hideFooterOnIme = true,
        headerActions = {
            IconButton(onClick = onDismiss, enabled = !isSaving) {
                Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.todo_cancel), tint = gray_7)
            }
        },
        footer = {
            form.error?.let { error ->
                Text(stringResource(error), color = error_base, style = text_14)
            }
            Button(
                onClick = { onIntent(TodoAssigneesIntent.Save(fallbackName)) },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green_shamrock)
            ) {
                Text(stringResource(R.string.todo_save))
            }
        }
    ) {
        val focusManager = LocalFocusManager.current
        TodoTextField(
            value = profile.firstName,
            onValueChange = { updateProfile(profile.copy(firstName = it)) },
            label = stringResource(R.string.todo_assignee_first_name),
            readOnly = isSaving,
            keyboardOptions = nameKeyboard
        )
        TodoTextField(
            value = profile.lastName,
            onValueChange = { updateProfile(profile.copy(lastName = it)) },
            label = stringResource(R.string.todo_assignee_last_name),
            readOnly = isSaving,
            keyboardOptions = nameKeyboard
        )
        TodoTextField(
            value = profile.middleName,
            onValueChange = { updateProfile(profile.copy(middleName = it)) },
            label = stringResource(R.string.todo_assignee_middle_name),
            readOnly = isSaving,
            keyboardOptions = nameKeyboard
        )
        val birthdayLabel = stringResource(R.string.todo_assignee_birthday_choose)
        val birthday = isoToDisplay(profile.birthday)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .clickable(
                        enabled = !isSaving,
                        role = Role.Button,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                        showBirthdayPicker = true
                    }
                    .semantics {
                        contentDescription = birthdayLabel
                        if (birthday.isNotEmpty()) stateDescription = birthday
                    }
            ) {
                TodoTextField(
                    value = birthday,
                    onValueChange = {},
                    label = stringResource(R.string.todo_assignee_birthday),
                    modifier = Modifier.clearAndSetSemantics {},
                    readOnly = true,
                    enabled = false,
                    trailingIcon = { Icon(Icons.Rounded.DateRange, contentDescription = null) }
                )
            }
            if (birthday.isNotEmpty()) {
                IconButton(onClick = { updateProfile(profile.copy(birthday = "")) }, enabled = !isSaving) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.todo_assignee_birthday_clear)
                    )
                }
            }
        }
        TodoTextField(
            value = profile.phone,
            onValueChange = { updateProfile(profile.copy(phone = it)) },
            label = stringResource(R.string.todo_assignee_phone),
            placeholder = stringResource(R.string.todo_assignee_phone_example),
            readOnly = isSaving,
            isError = form.phoneError != null,
            errorText = form.phoneError?.let { stringResource(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = TodoAssigneePhoneTransformation
        )
        TodoTextField(
            value = profile.email,
            onValueChange = { updateProfile(profile.copy(email = it)) },
            label = stringResource(R.string.todo_assignee_email),
            placeholder = stringResource(R.string.todo_assignee_email_example),
            readOnly = isSaving,
            isError = form.emailError != null,
            errorText = form.emailError?.let { stringResource(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false)
        )
        TodoTextField(
            value = profile.address,
            onValueChange = { updateProfile(profile.copy(address = it)) },
            label = stringResource(R.string.todo_assignee_address),
            readOnly = isSaving
        )
        TodoTextField(
            value = profile.description,
            onValueChange = { updateProfile(profile.copy(description = it)) },
            label = stringResource(R.string.todo_assignee_description),
            singleLine = false,
            minLines = 3,
            readOnly = isSaving
        )
    }
    if (showBirthdayPicker) {
        TodoDatePickerDialog(
            value = profile.birthday,
            onConfirm = { updateProfile(profile.copy(birthday = it)) },
            onDismiss = { showBirthdayPicker = false },
            showModeToggle = false
        )
    }
}

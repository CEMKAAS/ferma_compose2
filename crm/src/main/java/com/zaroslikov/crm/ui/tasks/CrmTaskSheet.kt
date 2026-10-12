package com.zaroslikov.crm.ui.tasks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.ui.components.CrmBottomSheet
import com.zaroslikov.crm.ui.components.CrmDatePickerField
import com.zaroslikov.crm.ui.components.CrmTextField
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.text_16
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.today

/**
 * Лист создания задачи: название (обязательно), срок (по умолчанию сегодня),
 * клиент из [clients] (необязательно) и заметка. Используется на экране задач и на дашборде;
 * у карточки клиента свой лист — клиент там уже известен.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmTaskSheet(
    clients: List<DomainCrmClient>,
    onDismiss: () -> Unit,
    onSave: (title: String, dueDate: String, clientId: Long?, note: String) -> Unit,
    initialClientId: Long? = null
) {
    var title by rememberSaveable { mutableStateOf("") }
    var titleError by rememberSaveable { mutableStateOf(false) }
    var dueDate by rememberSaveable { mutableStateOf(today()) }
    var clientId by rememberSaveable { mutableStateOf(initialClientId) }
    var note by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val noClient = stringResource(R.string.crm_no_client)
    val clientName = clients.firstOrNull { it.id == clientId }?.name ?: noClient

    CrmBottomSheet(title = stringResource(R.string.crm_new_task), onDismiss = onDismiss) {
        CrmTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = stringResource(R.string.crm_field_task_title),
            isError = titleError,
            errorText = stringResource(R.string.crm_error_title_empty)
        )
        CrmDatePickerField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = stringResource(R.string.crm_field_due_date)
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            CrmTextField(
                value = clientName,
                onValueChange = {},
                label = stringResource(R.string.crm_field_client),
                readOnly = true,
                leadingIcon = Icons.Rounded.Person,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(14.dp),
                containerColor = white
            ) {
                DropdownMenuItem(
                    text = { Text(text = noClient, style = text_16, color = black_2) },
                    onClick = {
                        clientId = null
                        expanded = false
                    }
                )
                clients.forEach { client ->
                    DropdownMenuItem(
                        text = { Text(text = client.name, style = text_16, color = black_2) },
                        onClick = {
                            clientId = client.id
                            expanded = false
                        }
                    )
                }
            }
        }
        CrmTextField(
            value = note,
            onValueChange = { note = it },
            label = stringResource(R.string.crm_note),
            singleLine = false,
            minLines = 3
        )
        Button(
            onClick = {
                if (title.isBlank()) titleError = true
                else onSave(title, dueDate, clientId, note)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
        ) {
            Text(text = stringResource(R.string.crm_save), style = textBold_16)
        }
    }
}

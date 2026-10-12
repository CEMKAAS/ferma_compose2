package com.zaroslikov.crm.ui.client_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.crmSalesKey
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.client_card.icon
import com.zaroslikov.crm.ui.components.CrmCard
import com.zaroslikov.crm.ui.components.CrmSegmentedButtons
import com.zaroslikov.crm.ui.components.CrmTextField
import com.zaroslikov.crm.ui.components.CrmTopBar
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.error_base
import com.zaroslikov.crm.ui.theme.gray_6
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.gray_8
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.marengo
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.textBold_18
import com.zaroslikov.crm.ui.theme.text_12
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.text_16
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.toResId

@Composable
fun CrmClientEditScreen(
    navigateBack: () -> Unit,
    viewModel: CrmClientEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CrmUiEvent.NavigateBack -> navigateBack()
                is CrmUiEvent.Message -> snackbarHostState.showSnackbar(context.getString(event.resId))
            }
        }
    }

    Scaffold(
        topBar = {
            CrmTopBar(
                title = stringResource(if (state.isNew) R.string.crm_new_client else R.string.crm_edit_client),
                navigateBack = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = gray_6
    ) { innerPadding ->
        if (state.isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = green_6)
            }
        else
            CrmClientEditBody(
                state = state,
                onIntent = viewModel::onIntent,
                modifier = Modifier.padding(innerPadding)
            )
    }
}

@Composable
private fun CrmClientEditBody(
    state: CrmClientEditState,
    onIntent: (CrmClientEditIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CrmCard {
                CrmTextField(
                    value = state.name,
                    onValueChange = { onIntent(CrmClientEditIntent.NameChange(it)) },
                    label = stringResource(R.string.crm_field_name),
                    leadingIcon = Icons.Rounded.Person,
                    isError = state.nameError != null,
                    errorText = state.nameError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                // Продажи матчатся по неизменному ключу — подсказываем, если имя от него ушло
                val original = state.original
                if (original != null && original.salesKey != crmSalesKey(state.name))
                    Text(
                        text = stringResource(R.string.crm_sales_key_hint, original.salesKey),
                        style = text_12,
                        color = gray_7
                    )

                Text(text = stringResource(R.string.crm_field_status), style = text_14, color = marengo)
                CrmSegmentedButtons(
                    options = listOf(
                        stringResource(R.string.crm_status_regular),
                        stringResource(R.string.crm_status_vip)
                    ),
                    selectedIndex = if (state.isVip) 1 else 0,
                    onSelect = { onIntent(CrmClientEditIntent.VipChange(it == 1)) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.crm_field_favorite),
                        style = text_16,
                        color = black_2,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.isFavorite,
                        onCheckedChange = { onIntent(CrmClientEditIntent.FavoriteChange(it)) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = green_6,
                            checkedThumbColor = white
                        )
                    )
                }

                CrmTextField(
                    value = state.note,
                    onValueChange = { onIntent(CrmClientEditIntent.NoteChange(it)) },
                    label = stringResource(R.string.crm_field_note),
                    singleLine = false,
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.crm_contacts),
                style = textBold_18,
                color = black_2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(state.contacts, key = { it.key }) { contact ->
            CrmContactEditor(contact = contact, onIntent = onIntent)
        }

        item {
            OutlinedButton(
                onClick = { onIntent(CrmClientEditIntent.AddContact) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = green_shamrock)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(R.string.crm_add_contact), style = text_16, color = green_shamrock)
            }
        }

        item {
            Button(
                onClick = { onIntent(CrmClientEditIntent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
            ) {
                if (state.isSaving)
                    CircularProgressIndicator(color = white, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                else
                    Text(text = stringResource(R.string.crm_save), style = textBold_16)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrmContactEditor(
    contact: CrmContactDraft,
    onIntent: (CrmClientEditIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    CrmCard(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            CrmTextField(
                value = stringResource(contact.type.toResId()),
                onValueChange = {},
                label = stringResource(R.string.crm_field_contact_type),
                readOnly = true,
                leadingIcon = contact.type.icon(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = white
            ) {
                CrmContactType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(type.toResId()), style = text_16) },
                        leadingIcon = { Icon(imageVector = type.icon(), contentDescription = null) },
                        onClick = {
                            onIntent(CrmClientEditIntent.ContactTypeChange(contact.key, type))
                            expanded = false
                        }
                    )
                }
            }
        }

        CrmTextField(
            value = contact.value,
            onValueChange = { onIntent(CrmClientEditIntent.ContactValueChange(contact.key, it)) },
            label = stringResource(R.string.crm_field_contact_value),
            isError = contact.error != null,
            errorText = contact.error?.let { stringResource(it) },
            keyboardOptions = KeyboardOptions(keyboardType = contact.type.keyboardType())
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = contact.isPrimary,
                onCheckedChange = { onIntent(CrmClientEditIntent.ContactPrimaryChange(contact.key, it)) },
                colors = CheckboxDefaults.colors(checkedColor = green_6, uncheckedColor = gray_8)
            )
            Text(
                text = stringResource(R.string.crm_primary_contact),
                style = text_14,
                color = marengo,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onIntent(CrmClientEditIntent.RemoveContact(contact.key)) }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.crm_remove_contact),
                    tint = error_base
                )
            }
        }
    }
}

private fun CrmContactType.keyboardType(): KeyboardType = when (this) {
    CrmContactType.PHONE, CrmContactType.WHATSAPP -> KeyboardType.Phone
    CrmContactType.EMAIL -> KeyboardType.Email
    CrmContactType.VK -> KeyboardType.Uri
    else -> KeyboardType.Text
}

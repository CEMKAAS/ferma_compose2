package com.zaroslikov.crm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.crm.R
import com.zaroslikov.crm.ui.theme.error_base
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.gray_8
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_8
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.marengo
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.text_16
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.isoToDisplay
import com.zaroslikov.crm.util.isoToMillis
import com.zaroslikov.crm.util.millisToIso

/** Поле ввода с подписью, ошибкой под полем и необязательной иконкой слева. */
@Composable
fun CrmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = placeholder?.let { { Text(text = it, color = gray_7) } },
        leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null) } },
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = if (isError && errorText != null) {
            { Text(text = errorText) }
        } else null,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        readOnly = readOnly,
        textStyle = text_16,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = green_6,
            unfocusedBorderColor = gray_8,
            focusedLabelColor = green_shamrock,
            cursorColor = green_shamrock,
            focusedLeadingIconColor = green_shamrock,
            unfocusedLeadingIconColor = marengo,
            errorBorderColor = error_base,
            errorLabelColor = error_base,
            errorSupportingTextColor = error_base,
            focusedContainerColor = white,
            unfocusedContainerColor = white,
            errorContainerColor = white
        )
    )
}

/**
 * Поле даты: только чтение, по тапу открывает `DatePickerDialog`.
 * [value] и результат [onValueChange] — `yyyy-MM-dd`, показывается `dd.MM.yyyy`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmDatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        CrmTextField(
            value = if (value.isEmpty()) "" else isoToDisplay(value),
            onValueChange = {},
            label = label,
            readOnly = true,
            isError = isError,
            errorText = errorText,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = stringResource(R.string.crm_select_date)
                )
            }
        )
        // Перехватываем тап поверх поля: readOnly-поле само по себе диалог не откроет
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

    if (showDialog) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = isoToMillis(value))
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { onValueChange(millisToIso(it)) }
                        showDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.crm_ok), color = green_shamrock)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.crm_cancel), color = marengo)
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

/** Сегментированный переключатель с одним выбранным вариантом. */
@Composable
fun CrmSegmentedButtons(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = green_8,
                    activeContentColor = green_shamrock,
                    activeBorderColor = green_6,
                    inactiveContainerColor = white,
                    inactiveContentColor = marengo,
                    inactiveBorderColor = gray_8
                ),
                label = {
                    Text(
                        text = option,
                        style = text_14,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

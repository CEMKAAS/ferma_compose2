package com.zaroslikov.todo.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.parseTodoTime
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.green_8
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.ui.theme.marengo
import com.zaroslikov.todo.ui.theme.text_14
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.white
import com.zaroslikov.todo.util.isoToMillis
import com.zaroslikov.todo.util.millisToIso
import com.zaroslikov.todo.util.timeToIso

/** Поле ввода с подписью, ошибкой под полем и необязательной иконкой слева. */
@Composable
fun TodoTextField(
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        // Одна строка: в узком поле (время рядом с датой) подпись иначе переносится и поле растёт
        label = { Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
        enabled = enabled,
        textStyle = text_16,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
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
            errorContainerColor = white,
            disabledTextColor = black_2,
            disabledContainerColor = white,
            disabledBorderColor = gray_8,
            disabledLabelColor = marengo,
            disabledTrailingIconColor = marengo
        )
    )
}

/**
 * Диалог выбора даты. [value] и результат [onConfirm] — `yyyy-MM-dd`; пустой [value] — сегодня
 * не выделено, выбор начинается с текущего месяца.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDatePickerDialog(
    value: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    accent: Color = green_shamrock,
    showModeToggle: Boolean = true
) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = isoToMillis(value))
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { onConfirm(millisToIso(it)) }
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.todo_ok), color = accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.todo_cancel), color = marengo)
            }
        }
    ) {
        DatePicker(state = pickerState, showModeToggle = showModeToggle)
    }
}

/** Диалог выбора времени (24 часа). [value] и результат [onConfirm] — `HH:mm`; пусто — 09:00. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTimePickerDialog(
    value: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    accent: Color = green_shamrock
) {
    val initial = parseTodoTime(value)
    val pickerState = rememberTimePickerState(
        initialHour = initial?.hour ?: 9,
        initialMinute = initial?.minute ?: 0,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = white,
        text = { TimePicker(state = pickerState) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timeToIso(pickerState.hour, pickerState.minute))
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.todo_ok), color = accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.todo_cancel), color = marengo)
            }
        }
    )
}

/**
 * Сегментированный переключатель с одним выбранным вариантом. Без галочки и с узкими отступами,
 * подпись уменьшается под ширину сегмента — четыре варианта помещаются на телефоне без обрезки.
 */
@Composable
fun TodoSegmentedButtons(
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
                icon = {},
                contentPadding = PaddingValues(horizontal = 4.dp),
                label = {
                    Text(
                        text = option,
                        style = text_14,
                        maxLines = 1,
                        softWrap = false,
                        autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = text_14.fontSize)
                    )
                }
            )
        }
    }
}

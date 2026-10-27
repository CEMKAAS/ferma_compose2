package com.zaroslikov.assistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.zaroslikov.assistant.nlu.Units
import com.zaroslikov.domain.models.enums.Suffix
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.assistant.R
import com.zaroslikov.assistant.nlu.ParsedCommand
import com.zaroslikov.assistant.ui.chat.ActionEditForm
import com.zaroslikov.assistant.ui.theme.black_2
import com.zaroslikov.assistant.ui.theme.error_base
import com.zaroslikov.assistant.ui.theme.gray_6
import com.zaroslikov.assistant.ui.theme.gray_7
import com.zaroslikov.assistant.ui.theme.gray_8
import com.zaroslikov.assistant.ui.theme.green_6
import com.zaroslikov.assistant.ui.theme.green_8
import com.zaroslikov.assistant.ui.theme.green_shamrock
import com.zaroslikov.assistant.ui.theme.marengo
import com.zaroslikov.assistant.ui.theme.textBold_16
import com.zaroslikov.assistant.ui.theme.textBold_20
import com.zaroslikov.assistant.ui.theme.text_14
import com.zaroslikov.assistant.ui.theme.text_16
import com.zaroslikov.assistant.ui.theme.white

/** Число из поля ввода: запятая допускается как десятичный разделитель. */
private fun String.toNumberOrNull(): Double? = trim().replace(',', '.').toDoubleOrNull()

/** Ошибка числового поля: пусто и обязательно → «Заполните поле», не число → «Введите число». */
private enum class FieldError { EMPTY, NUMBER }

private fun numberError(value: String, required: Boolean, positive: Boolean = true): FieldError? {
    if (value.isBlank()) return if (required) FieldError.EMPTY else null
    val number = value.toNumberOrNull() ?: return FieldError.NUMBER
    return if (positive && number <= 0.0) FieldError.NUMBER else null
}

/** Поголовье — только целое число больше нуля. */
private fun headCountError(value: String): FieldError? {
    if (value.isBlank()) return FieldError.EMPTY
    val count = value.trim().toIntOrNull() ?: return FieldError.NUMBER
    return if (count <= 0) FieldError.NUMBER else null
}

private fun textError(value: String): FieldError? = if (value.isBlank()) FieldError.EMPTY else null

/**
 * Шторка «Изменить запись»: набор полей зависит от типа команды
 * (продукция, продажа, расход, животное). «Сохранить» недоступна, пока есть ошибки.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionEditSheet(
    form: ActionEditForm,
    onChange: (ActionEditForm) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val command = form.command

    // Ошибки по полям; null — поле в порядке или не показывается для этой команды
    val titleError = if (command is ParsedCommand.AddAnimal) null else textError(form.title)
    val animalTypeError = if (command is ParsedCommand.AddAnimal) textError(form.animalType) else null
    val countError = when (command) {
        is ParsedCommand.AddProduction, is ParsedCommand.AddSale -> numberError(form.count, required = true)
        is ParsedCommand.AddExpense -> numberError(form.count, required = false)
        is ParsedCommand.AddAnimal -> headCountError(form.count)
    }
    // Одиночному животному нужна кличка, группе — нет
    val animalNameError = if (command is ParsedCommand.AddAnimal && form.count.trim().toIntOrNull() == 1)
        textError(form.animalName) else null
    val priceError = when (command) {
        is ParsedCommand.AddSale, is ParsedCommand.AddExpense -> numberError(form.price, required = true)
        is ParsedCommand.AddAnimal -> numberError(form.price, required = false)
        is ParsedCommand.AddProduction -> null
    }
    val canSave = listOf(titleError, animalTypeError, countError, animalNameError, priceError)
        .all { it == null }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.assistant_edit_title), style = textBold_20, color = black_2)

            if (command is ParsedCommand.AddAnimal) {
                EditTextField(
                    value = form.animalType,
                    onValueChange = { onChange(form.copy(animalType = it)) },
                    label = stringResource(R.string.assistant_edit_animal_type),
                    error = animalTypeError
                )
                EditTextField(
                    value = form.animalName,
                    onValueChange = { onChange(form.copy(animalName = it)) },
                    label = stringResource(R.string.assistant_edit_animal_name),
                    error = animalNameError
                )
                EditTextField(
                    value = form.count,
                    onValueChange = { onChange(form.copy(count = it)) },
                    label = stringResource(R.string.assistant_edit_count),
                    error = countError,
                    keyboardType = KeyboardType.Number
                )
                EditTextField(
                    value = form.price,
                    onValueChange = { onChange(form.copy(price = it)) },
                    label = stringResource(R.string.assistant_edit_animal_price),
                    error = priceError,
                    keyboardType = KeyboardType.Decimal
                )
            } else {
                EditTextField(
                    value = form.title,
                    onValueChange = { onChange(form.copy(title = it)) },
                    label = stringResource(R.string.assistant_edit_name),
                    error = titleError
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    EditTextField(
                        value = form.count,
                        onValueChange = { onChange(form.copy(count = it)) },
                        label = stringResource(R.string.assistant_edit_count),
                        error = countError,
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    UnitDropdown(
                        selected = form.unitSuffix,
                        onSelect = { onChange(form.copy(unitSuffix = it, unitLabel = Units.label(it))) },
                        modifier = Modifier.width(132.dp)
                    )
                }
                if (command is ParsedCommand.AddSale || command is ParsedCommand.AddExpense) {
                    EditTextField(
                        value = form.price,
                        onValueChange = { onChange(form.copy(price = it)) },
                        label = stringResource(R.string.assistant_edit_price),
                        error = priceError,
                        keyboardType = KeyboardType.Decimal
                    )
                    PriceModeToggle(
                        perUnit = form.priceIsPerUnit,
                        onChange = { onChange(form.copy(priceIsPerUnit = it)) }
                    )
                }
                if (command is ParsedCommand.AddSale)
                    EditTextField(
                        value = form.buyer,
                        onValueChange = { onChange(form.copy(buyer = it)) },
                        label = stringResource(R.string.assistant_edit_buyer)
                    )
                if (command is ParsedCommand.AddExpense)
                    EditTextField(
                        value = form.category,
                        onValueChange = { onChange(form.copy(category = it)) },
                        label = stringResource(R.string.assistant_edit_category)
                    )
            }

            EditTextField(
                value = form.date,
                onValueChange = { onChange(form.copy(date = it)) },
                label = stringResource(R.string.assistant_edit_date),
                placeholder = stringResource(R.string.assistant_edit_date_hint)
            )
            EditTextField(
                value = form.note,
                onValueChange = { onChange(form.copy(note = it)) },
                label = stringResource(R.string.assistant_edit_note),
                singleLine = false
            )

            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green_6,
                    contentColor = white,
                    disabledContainerColor = gray_6,
                    disabledContentColor = gray_7
                )
            ) {
                Text(text = stringResource(R.string.assistant_save), style = textBold_16)
            }
        }
    }
}

/** Переключатель «За единицу / Сумма» для цены продажи и расхода. */
@Composable
private fun PriceModeToggle(perUnit: Boolean, onChange: (Boolean) -> Unit) {
    val options = listOf(
        stringResource(R.string.assistant_edit_price_per_unit),
        stringResource(R.string.assistant_edit_price_total)
    )
    val selectedIndex = if (perUnit) 0 else 1
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onChange(index == 0) },
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

/** Поле шторки с подписью, текстом ошибки под полем и необязательным суффиксом единицы. */
@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: FieldError? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    suffix: String = "",
    placeholder: String? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val errorText = when (error) {
        FieldError.EMPTY -> stringResource(R.string.assistant_edit_error_empty)
        FieldError.NUMBER -> stringResource(R.string.assistant_edit_error_number)
        null -> null
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(text = label) },
        placeholder = placeholder?.let { { Text(text = it, color = gray_7) } },
        suffix = if (suffix.isNotBlank()) {
            { Text(text = suffix, style = text_16, color = gray_7) }
        } else null,
        isError = errorText != null,
        supportingText = errorText?.let { { Text(text = it) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        textStyle = text_16,
        keyboardOptions = KeyboardOptions(
            capitalization = if (keyboardType == KeyboardType.Text)
                KeyboardCapitalization.Sentences else KeyboardCapitalization.None,
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = green_6,
            unfocusedBorderColor = gray_8,
            focusedLabelColor = green_shamrock,
            cursorColor = green_shamrock,
            errorBorderColor = error_base,
            errorLabelColor = error_base,
            errorSupportingTextColor = error_base,
            focusedContainerColor = white,
            unfocusedContainerColor = white,
            errorContainerColor = white
        )
    )
}

/** Единицы, доступные для выбора в шторке правки. */
private val EDIT_UNITS = listOf(
    Suffix.PIECES, Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS, Suffix.MILLILITRES, Suffix.LITERS,
    Suffix.CUBIC_METERS, Suffix.HEADS, Suffix.UNITS, Suffix.METERS, Suffix.CENTIMETERS
)

/** Выпадающий список единицы измерения рядом с количеством. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    selected: Suffix,
    onSelect: (Suffix) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = Units.label(selected),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(text = stringResource(R.string.assistant_edit_unit)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = text_16,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = green_6,
                unfocusedBorderColor = gray_8,
                focusedTextColor = black_2,
                unfocusedTextColor = black_2
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = white
        ) {
            EDIT_UNITS.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(text = Units.label(unit), style = text_16, color = black_2) },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

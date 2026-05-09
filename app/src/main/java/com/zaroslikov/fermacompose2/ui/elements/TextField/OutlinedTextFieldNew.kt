@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements.TextField

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.gray_9
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AutoCalculateCheckbox
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.AutoWeightCheckbox
import com.zaroslikov.fermacompose2.ui.elements.сompositions.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.elements.сompositions.MinDateSelectableDates
import com.zaroslikov.fermacompose2.ui.elements.сompositions.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.elements.сompositions.TimePicker
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.violet_1

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    isGroup: Boolean? = null,
    onClick: (Boolean) -> Unit = {},
    @StringRes intRes: Int = R.string.search_section,
    iconRes: Int = if (isGroup == true) R.drawable.icon_group else R.drawable.icon_list
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderWidth = when {
        isFocused -> 2.dp//green_g_2 //green_1
        else -> 1.dp
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BorderCard(
            modifier = Modifier
                .height(62.dp)
                .weight(1f),
            borderWidth = borderWidth
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = grey
                )
                BaseOutlinedTextNew2(
                    value = value,
                    onValueChange = onValueChange,
                    intResSup = intRes,
                    interactionSource = interactionSource
                )
            }
        }
        isGroup?.let {
            BorderCard(
                modifier = Modifier
                    .size(62.dp)
                    .wrapContentWidth(),
                onClick = { onClick(!isGroup) }
            ) {
                Icon(
                    painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}


@Composable
fun OutlinedTextNoteNew(
    value: String,
    onValueChange: (String) -> Unit,
    isBorderCard: Boolean = true,
    @DrawableRes leadingIconRes: Int? = R.drawable.baseline_sticky_note_2_24,
    @StringRes labelIntRes: Int? = R.string.outlined_text_note,
    @StringRes supportingText: Int = R.string.support_text_note,
    minLines: Int = 3
) {
    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            value = value,
            onValueChange = { onValueChange(it) },
            leadingIconRes = leadingIconRes,
            labelIntRes = labelIntRes,
            intResSup = supportingText,
            singleLine = false,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = KeyboardActionFocus.CLEAN,
            minLines = minLines
        )
    }
    if (isBorderCard) BorderCard {
        textField()
    } else textField()
}

@Composable
fun OutlinedTextNew(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    isBorderCard: Boolean = true,
    isNecessarily: Boolean = false,
    @StringRes labelIntRes: Int? = R.string.outlined_text_note,
    @StringRes supportingText: Int = R.string.support_text_note,
) {
    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            value = value,
            onValueChange = { onValueChange(it) },
            labelIntRes = labelIntRes,
            intResSup = supportingText,
            isError = isError,
            singleLine = false,
            isNecessarily = isNecessarily,
            keyboardOptions = keyboardOptionsNext(),
        )
    }
    if (isBorderCard) BorderCard {
        textField()
    } else textField()
}

@Composable
fun OutlinedNumberNew(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix? = null,
    isError: Boolean = false,
    @DrawableRes drawableRes: Int? = null,
    @DrawableRes drawableRes2: Int? = null,
    drawableColor2: Color = Color(0xFF9A9A9A),
    @StringRes intRes: Int? = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    maxLength: Int? = null,
    isBorderCard: Boolean = true,
    isNecessarily: Boolean = false
    /*  colorTextField: Color = gray_9,*/
) {
    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            suffix = suffix,
            isError = isError,
            leadingIconRes = drawableRes,
            labelIntRes = intRes,
            intResSup = intResSup,
            intResError = intResError,
            keyboardOptions = keyboardOptions,
            leadingIconRes2 = drawableRes2,
            leadingIconColor2 = drawableColor2,
            maxLength = maxLength,
            isNecessarily = isNecessarily
            /*colorTextField = colorTextField*/
        )
    }
    if (isBorderCard) BorderCard {
        textField()
    } else textField()
}

@Composable
fun OutlinedTextDropdownMenuNew(
    value: String,
    onValueChange: (String) -> Unit,
    titleList: List<String>,
    enable: Boolean = true,
    readOnly: Boolean = false,
    isBorderCard: Boolean = true,
    @DrawableRes leadingIconRes: Int? = null,
    @StringRes labelIntRes: Int,
    @StringRes intResSup: Int
) {
    val textField: @Composable () -> Unit = {
        ExposedDropdownMenuCategoryBuyer(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = titleList
        ) {
            BaseOutlinedTextNew(
                modifier = it.first,
                value = value,
                onValueChange = { onValueChange(it) },
                onClear = { onValueChange("") },
                leadingIconRes = leadingIconRes,
                isError = false,
                singleLine = false,
                labelIntRes = labelIntRes,
                intResSup = intResSup,
                keyboardOptions = keyboardOptionsNext(),
                enabled = enable,
                readOnly = readOnly, isMore = value.isBlank()
            )
        }
    }
    if (isBorderCard) BorderCard {
        textField()
    } else textField()
}

@Composable
fun OutlinedTextDropdownMenuTypeEgg(
    value: TypeEgg,
    onValueChange: (TypeEgg) -> Unit,
    titleList: List<TypeEgg>,
    enable: Boolean = true,
    readOnly: Boolean = true,
    isBorderCard: Boolean = true,
    @DrawableRes leadingIconRes: Int? = null,
    @StringRes labelIntRes: Int,
    @StringRes intResSup: Int
) {
    val textField: @Composable () -> Unit = {
        ExposedDropdownMenuEnum(
            valueList = titleList,
            enabled = enable,
            dropdownMenuItem = { index, item, closeMenu ->
                val trailingIcon: @Composable (() -> Unit)? = if (item == value) {
                    { Icon(Icons.Default.Done, contentDescription = null) }
                } else null
                DropdownMenuItem(
                    text = { Text(text = stringResource(item.toResId())) },
                    trailingIcon = trailingIcon,
                    onClick = {
                        onValueChange(item)
                        closeMenu()
                    }
                )
            }
        ) {
            BaseOutlinedTextNew(
                modifier = it.first,
                value = stringResource(value.toResId()),
                onValueChange = { },
                leadingIconRes = leadingIconRes,
                isError = false,
                labelIntRes = labelIntRes,
                intResSup = intResSup,
                keyboardOptions = keyboardOptionsNext(),
                enabled = enable,
                readOnly = readOnly,
            )
        }
    }
    if (isBorderCard) BorderCard {
        textField()
    } else textField()
}

@Composable
fun OutlinedTextAnimalNew(
    value: String,
    onValueChange: (Pair<Long, String>) -> Unit,
    onClickClear: (String) -> Unit,
    selectedAnimalIndex: Long,
    animalList: List<AnimalForAddDomain>,
) {
    val focusManager = LocalFocusManager.current
    BorderCard {
        ExposedDropdownMenuAnimals(
            selectedItemIndex = selectedAnimalIndex,
            setTitle = {
                onValueChange(it)
                focusManager.clearFocus()
            },
            animalList = animalList
        ) {
            BaseOutlinedTextNew(
                modifier = it.first,
                value = value,
                onValueChange = {},
                onClear = {
                    onClickClear("")
                    focusManager.clearFocus()
                },
                leadingIconRes = R.drawable.baseline_pets_24,
                readOnly = true, isMore = value.isBlank(),
                labelIntRes = R.string.outlined_text_animals,
                intResSup = R.string.support_text_animal,
            )
        }
    }
}

@Composable
fun OutlinedTextTitleAddNew(
    value: String,
    onValueChange: (String) -> Unit,
    onValueChangeSuffix: (Pair<String, Suffix>) -> Unit,
    @StringRes intRes: Int = R.string.outlined_text_product,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_product,
    drawableRes: Int? = null,
    readOnly: Boolean = false,
    enable: Boolean = true,
    titleList: List<TitleAndSuffixDomain>,
    isErrorTitle: Boolean,
    isErrorSlash: Boolean,
    isBorderCard: Boolean = true,
    isMore: Boolean = true,
    colorTextField: Color = gray_9,
) {
    val focusManager = LocalFocusManager.current
    val textField: @Composable () -> Unit = {
        ExposedDropdownMenuProduct(
            title = value,
            setTitle = {
                onValueChangeSuffix(it)
                focusManager.moveFocus(FocusDirection.Down)
            },
            titleList = titleList,
            enableDropMenu = enable
        ) {
            BaseOutlinedTextNew(
                modifier = it.first,
                isNecessarily = true,
                value = value,
                onValueChange = { onValueChange(it) },
                onClear = { onValueChange("") },
                leadingIconRes = drawableRes,
                isError = isErrorTitle,
                isErrorSlash = isErrorSlash,
                labelIntRes = intRes,
                intResError = intResError,
                intResSup = intResSup,
                singleLine = false,
                readOnly = readOnly, enabled = enable, isMore = value.isBlank(),
                keyboardOptions = keyboardOptionsNext(),
                colorTextField = colorTextField
            )
        }
    }
    if (isBorderCard) BorderCard {
        textField()
    } else textField()
}


/*@Composable
fun OutlinedText(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    leadingIconRes: Int? = null,
    isError: Boolean = false,
    isEnable: Boolean = true,
    isReadOnly: Boolean = false,
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        BaseOutlinedText(
            value = value,
            onValueChange = { onValueChange(it) },
            leadingIconRes = leadingIconRes,
            isError = isError,
            labelIntRes = intRes,
            intResError = intResError,
            intResSup = intResSup,
            keyboardOptions = keyboardOptionsNext(),
            enable = isEnable,
            readOnly = isReadOnly
        )
    }
}

*/


/*
@Composable
fun OutlinedTextNoteWidget(
    value: String,
    onValueChange: (String) -> Unit,
) {
    var text by remember { mutableStateOf(value) }
    var isInFocus by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (!isInFocus) {
            text = value
        }
    }
    BasicTextField(
        modifier = Modifier
            .toOutlinedText()
            .onFocusChanged {
                isInFocus = it.isFocused
                if (!it.isFocused) onValueChange(text)
            },
        value = text,
        onValueChange = {
            text = it
        },
        decorationBox = { innerTextField ->
            Box {
                if (text == "") {
                    Text(
                        stringResource(R.string.support_text_widget_animal_note),
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        },
        keyboardOptions = keyboardOptionsEnter(),
        keyboardActions = keyboardActionsEnter()
    )
}
*/


@Composable
fun OutlinedTextDateNew(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes intRes: Int = R.string.outlined_text_date,
    @StringRes intResSup: Int = R.string.support_text_date,
    drawableRes: Int = R.drawable.baseline_calendar_month_24,
    enable: Boolean = true,
    isLimit: Boolean = true,
    isNecessarily: Boolean = false,
    isBorderCard: Boolean = true,
    minDate: String? = null
) {
    val dateList = value.split(".")
    val date =
        dateBuilder(
            dateList[0].toInt(),
            stringResource(monthToResString(dateList[1].toInt())),
            dateList[2].toInt()
        )
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        val selectableDates = when {
            minDate != null -> MinDateSelectableDates(formatDateToLong(minDate))
            isLimit -> PastOrPresentSelectableDates
            else -> DatePickerDefaults.AllDates
        }
        val datePickerState = rememberDatePickerState(
            selectableDates = selectableDates,
            initialSelectedDateMillis = formatDateToLong(value)
        )
        DatePickerDialogSample(datePickerState, value) {
            onValueChange(it)
            openDialog = !openDialog
        }
    }
    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            onClick = { openDialog = !openDialog },
            value = date,
            onValueChange = { },
            readOnly = true,
            enabled = enable,
            labelIntRes = intRes,
            intResSup = intResSup,
            leadingIconRes = drawableRes,
        )
    }
    if (isBorderCard) BorderCard(
        onClick = { openDialog = !openDialog }
    ) { textField() }
    else textField()
}


@Composable
fun TimeOutlinedTextFieldNew(
    time: String,
    onValueChange: (String) -> Unit,
    @StringRes intRes: Int = R.string.outlined_text_notification,
    @StringRes intResSup: Int = R.string.support_text_notification,
    drawableRes: Int = R.drawable.baseline_access_time_24,
    enable: Boolean = true,
    isBorderCard: Boolean = true,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog)
        TimePicker(
            time = time,
            showDialog = {
                onValueChange(it)
                openDialog = !openDialog
            })

    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            modifier = Modifier.clickable(onClick = { openDialog = !openDialog }),
            value = time,
            onValueChange = { },
            onClick = { openDialog = !openDialog },
            readOnly = true,
            enabled = enable,
            labelIntRes = intRes,
            intResSup = intResSup,
            leadingIconRes = drawableRes,
        )
    }
    if (isBorderCard) BorderCard(onClick = { openDialog = !openDialog }) { textField() }
    else textField()
}


@Composable
fun OutlinedTextCategoryNew(
    value: String,
    onValueChange: (String) -> Unit,
    titleList: List<String>,
    enable: Boolean = true,
    readOnly: Boolean = false,
) {
    BorderCard {
        ExposedDropdownMenuCategoryBuyer(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = titleList
        ) {
            BaseOutlinedTextNew(
                modifier = it.first,
                value = value,
                onValueChange = { onValueChange(it) },
                onClear = { onValueChange("") },
                leadingIconRes = R.drawable.baseline_format_list_bulleted_24,
                isError = false,
                labelIntRes = R.string.outlined_text_field_category,
                intResSup = R.string.support_text_category,
                keyboardOptions = keyboardOptionsNext(),
                enabled = enable,
                readOnly = readOnly, isMore = value.isBlank()
            )
        }
    }
}

@Composable
fun OutlinedTextFilterNew(
    value: FilterDate,
    currentPeriod: String,
    onValueChange: (FilterDate) -> Unit,
    titleList: List<FilterDate>,
) {
    val valueFinal = if (value == FilterDate.PERIOD) currentPeriod
    else stringResource(value.toResId())
    ExposedDropdownMenuFilterDate(
        title = value,
        setTitle = { onValueChange(it) },
        titleList = titleList
    ) {
        BaseOutlinedTextNew(
            modifier = it.first,
            value = valueFinal,
            onValueChange = { },
            isError = false,
            leadingIconRes2 = R.drawable.baseline_filter_alt_24,
            intResSup = R.string.support_text_category,
            keyboardOptions = keyboardOptionsNext(),
            readOnly = true,
            isMore = true
        )
    }
}

@Composable
fun OutlinedTextVaccinationNew(
    value: String,
    onValueChange: (String) -> Unit,
    titleList: List<String>,
    isError: Boolean,
    enable: Boolean = true,
    readOnly: Boolean = false,
) {
    BorderCard {
        ExposedDropdownMenuCategoryBuyer(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = titleList
        ) {
            BaseOutlinedTextNew(
                modifier = it.first,
                value = value,
                onValueChange = { onValueChange(it) },
                onClear = { onValueChange("") },
                leadingIconRes = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
                isError = isError,
                labelIntRes = R.string.animal_vaccination_title,
                intResSup = R.string.support_text_vaccination_animals,
                intResError = R.string.error_no_vaccination,
                isNecessarily = true,
                singleLine = false,
                keyboardOptions = keyboardOptionsNext(),
                enabled = enable,
                readOnly = readOnly, isMore = value.isBlank()
            )
        }
    }
}

@Composable
fun OutlinedTextCountAnimalNew(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    isErrorCountZero: Boolean,
    countAnimal: String?,
    suffix: Suffix,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_count_animals,
    @StringRes intResError: Int = R.string.error_no_count_product,
    @StringRes countZeroTextErrorRes: Int = R.string.error_count_zero_animals,
    @StringRes tooltipTextResAutoCal: Int = R.string.tooltip_auto_calculate_weight,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    isBorderCard: Boolean = true,
) {

    val (isError, textError) = when {
        isError -> true to intResError
        isErrorCountZero -> true to countZeroTextErrorRes
        else -> false to intResError
    }

    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            suffix = suffix,
            isError = isError,
            isNecessarily = true,
            leadingIconRes = drawableRes,
            labelIntRes = intRes,
            intResSup = intResSup,
            intResError = textError,
            keyboardOptions = keyboardOptions,
        )
        Text(
            countAnimal?.let {
                stringResource(R.string.support_text_count_sale_animals).format(
                    countAnimal,
                    stringResource(suffix.toResId())
                )
            } ?: stringResource(R.string.support_text_count_sale_no_animals),
            style = text_14,
            color = gray_7
        )
    }

    if (isBorderCard) BorderCard { textField() } else textField()


    /*if (isWeightCalculate && suffix !in setOf(
            Suffix.KILOGRAM,
            Suffix.GRAM,
            Suffix.TONS
        )
    )
        AutoWeightCheckbox(
            count = value,
            weight = weightValue,
            onWeightChange = onWeightChange,
            suffix = weightSuffix,
            onSuffixChance = onWeightSuffixChance,
            isChecked = isAutoCalculate,
            onCheckedChange = onAutoCalculate,
            tooltipTextResAutoCal = tooltipTextResAutoCal
        )*/
}


@Composable
fun OutlinedTextCountNew(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChange: ((Suffix) -> Unit)? = null,
    suffixList: List<Suffix> = suffixAllList,
    isError: Boolean,
    enabled: Boolean = true,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    isBorderCard: Boolean = true,
    colorTextField: Color = gray_9,
    isNecessarily: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    val suffixText = stringResource(suffix.toResId())
    val textField: @Composable () -> Unit = {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BaseOutlinedTextNew(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                isError = isError,
                leadingIconRes = drawableRes,
                labelIntRes = intRes,
                intResSup = R.string.outlined_text_count,
                intResError = intResError,
                keyboardOptions = keyboardOptions,
                colorTextField = colorTextField,
                isNecessarily = isNecessarily,
                enabled = enabled
            )
            ExposedDropdownMenuSuffix(
                modifier = Modifier.weight(1f),
                suffix = suffix,
                setSuffix = {
                    onSuffixChange?.let { it1 ->
                        it1(it)
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                },
                suffixList = suffixList,
                enableDropMenu = enabled,
                content = {
                    BaseOutlinedTextNew(
                        modifier = it.first,
                        value = suffixText,
                        onValueChange = {},
                        leadingIconRes = R.drawable.baseline_edit_document_24,
                        labelIntRes = R.string.outlined_text_suffix,
                        intResSup = R.string.outlined_text_suffix,
                        keyboardOptions = keyboardOptions,
                        enabled = enabled,
                        isMore = true,
                        readOnly = true,
                        colorTextField = colorTextField
                    )
                }
            )
        }
    }
    if (isBorderCard) BorderCard {
        textField()
    } else Box { textField() }
}


/*
@Composable
fun OutlinedTextCountAnimal2(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    isErrorCountMore: Boolean = false,
    isErrorCountZero: Boolean = false,
    countAnimalAll: String = "",
    suffix: Suffix,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val errorText = when {
        isErrorCountZero -> R.string.error_count_zero_animals
        isErrorCountMore -> R.string.error_count_sale_animals
        else -> R.string.error_no_count_product
    }
    Log.i("count23", "OutlinedTextCountAnimal2: $countAnimalAll ")

    val error = isError || isErrorCountZero || isErrorCountMore

    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false,
        isNecessarily = true
    ) {
        BaseOutlinedText(
            modifier = modifier,
            value = value,
            onValueChange = { onValueChange(it) },
            countAnimal = countAnimalAll,
            suffix = suffix,
            isAnimal = true,
            leadingIconRes = R.drawable.baseline_spoke_24,
            labelIntRes = intRes,
            isError = error,
            intResSup = R.string.is_empty,
            intResError = errorText,
        )
    }
}

@Composable
fun OutlinedTextTitleAdd(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes intRes: Int = R.string.outlined_text_product,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_product,
    drawableRes: Int? = null,
    readOnly: Boolean = false,
    enable: Boolean = true,
    titleList: List<String>,
    isErrorTitle: Boolean,
    isErrorSlash: Boolean,
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false,
        isNecessarily = true
    ) {
        ExposedDropdownMenuProduct(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = titleList,
            enableDropMenu = enable
        ) {
            BaseOutlinedText(
                modifier = it.first,
                value = value,
                onValueChange = { onValueChange(it) },
                leadingIconRes = drawableRes,
                isError = isErrorTitle,
                isErrorSlash = isErrorSlash,
                labelIntRes = intRes,
                intResError = intResError,
                intResSup = intResSup,
                readOnly = readOnly, enable = enable
            )
        }
    }
}*/



/*

@Composable
fun OutlinedTextPrice(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    @StringRes intSupportText: Int = R.string.support_text_count_product_sale,
    focusManager: FocusManager
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it.toConvertDb())
            },
            label = { Text(stringResource(R.string.outlined_text_price)) },
            modifier = Modifier
                .fillMaxSize(),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_card_24),
                    contentDescription = null
                )
            },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isError,
                    intRes = intSupportText,
                    intResError = R.string.error_no_count_sale
                )
            },
            suffix = {
                Text(text = stringResource(R.string.currency_ruble))
            },
            keyboardOptions = keyboardOptionsNextNumber(),
            keyboardActions = keyboardActionsDown(focusManager),
            isError = isError
        )
    }
}
*/
@Composable
fun OutlinedPriceInputNew(
    price: String,
    onPriceChange: (String) -> Unit,
    isAutoCalculate: Boolean,
    onAutoCalculate: (Boolean) -> Unit,
    isManyCount: Boolean = false,
    isError: Boolean = false,
    isNecessarily: Boolean = false,
    leadingIconRes: Int? = R.drawable.icon_money,
    count: String,
    countSuffix: Suffix,
    priceAll: String,
    priceSuffix: Suffix,
    @StringRes supportTextRes: Int = R.string.support_text_price_all,
    @StringRes supportTextResAutoCal: Int = R.string.support_text_price_one,
    @StringRes tooltipTextResAutoCal: Int = R.string.tooltip_auto_calculate_price,
    isBorderCard: Boolean = true
) {
    val target = when {
        isAutoCalculate -> 4.dp
        isManyCount -> 2.dp
        else -> 0.dp
    }
    val animatedPadding by animateDpAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val supportText = if (isAutoCalculate) supportTextResAutoCal else supportTextRes
    val textField: @Composable () -> Unit = {
        Column {
            BaseOutlinedTextNew(
                value = price,
                onValueChange = { onPriceChange(it) },
                labelIntRes = R.string.outlined_text_price,
                intResSup = supportText,
                intResError = R.string.error_no_count_sale,
                isError = isError,
                suffix = priceSuffix,
                isNecessarily = isNecessarily,
                modifier = Modifier
                    .padding(bottom = animatedPadding.coerceAtLeast(0.dp)),
                leadingIconRes = leadingIconRes,
                keyboardOptions = keyboardOptionsNextNumber(),
            )
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isManyCount
            ) {
                AutoCalculateCheckbox(
                    isChecked = isAutoCalculate,
                    onCheckedChange = onAutoCalculate,
                    tooltipTextResAutoCal = tooltipTextResAutoCal,
                    price = price,
                    count = count,
                    countSuffix = countSuffix,
                    priceSuffix = priceSuffix,
                    priceAll = priceAll,
                )
            }
        }
    }
    if (isBorderCard) BorderCard { textField() } else textField()
}

@Composable
fun OutlinedCountInputNew(
    count: String,
    onCountChange: (String) -> Unit,
    countSuffixList: List<Suffix> = suffixAllList,
    countSuffix: Suffix,
    onSuffixChange: ((Suffix) -> Unit)? = null,
    enabled: Boolean = true,
    isAutoCalculate: Boolean,
    onAutoCalculate: (Boolean) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit = {},
    weightSuffix: Suffix,
    onWeightSuffixChance: (Suffix) -> Unit = {},
    isError: Boolean = false,
    isShowCheckbox: Boolean = false,
    weightAll: String,
    weightAllSuffix: Suffix,
    enabledWeightSuffix: Boolean = true,
    isBorderCard: Boolean = true
) {
    val target = when {
        isAutoCalculate -> 4.dp
        else -> 0.dp
    }
    val animatedPadding by animateDpAsState(
        targetValue = target,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val textField: @Composable () -> Unit = {
        Column {
            OutlinedTextCountNew(
                modifier = Modifier.padding(bottom = animatedPadding.coerceAtLeast(0.dp)),
                value = count,
                onValueChange = onCountChange,
                suffix = countSuffix,
                onSuffixChange = onSuffixChange,
                suffixList = countSuffixList,
                isError = isError,
                enabled = enabled,
                isBorderCard = false,
            )
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isShowCheckbox
            ) {
                AutoWeightCheckbox(
                    count = count,
                    countSuffix = countSuffix,
                    weightAll = weightAll,
                    weight = weight,
                    onWeightChange = onWeightChange,
                    suffix = weightSuffix,
                    onSuffixChance = onWeightSuffixChance,
                    isChecked = isAutoCalculate,
                    onCheckedChange = onAutoCalculate,
                    suffixEnabled = enabledWeightSuffix,
                    totalSuffix = weightAllSuffix,
                    tooltipTextResAutoCal = R.string.tooltip_auto_calculate_weight,
                )
            }
        }
    }
    if (isBorderCard) BorderCard { textField() } else textField()
}

@Composable
fun WeightOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChance: (Suffix) -> Unit,
) {
    BaseOutlinedTextNew(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        suffix = suffix,
        isError = false,
        leadingIconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
        labelIntRes = R.string.weight_screen_title,
        intResSup = R.string.support_text_weight,
        keyboardOptions = keyboardOptionsNextNumber(),
    )
}

@Composable
fun WeightOutlinedTextFieldNew(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChange: ((Suffix) -> Unit)? = null,
    suffixList: List<Suffix> = suffixWeightList,
    isError: Boolean = false,
    suffixEnabled: Boolean = true,
    drawableRes: Int = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
    @StringRes intRes: Int = R.string.weight_screen_title,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    @StringRes tooltipTextResAutoCal: Int = R.string.tooltip_auto_calculate_weight,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    isBorderCard: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    val suffixText = stringResource(suffix.toResId())
    val textField: @Composable () -> Unit = {
        BaseOutlinedTextNew(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            leadingIconRes = drawableRes,
            labelIntRes = intRes,
            intResSup = R.string.outlined_text_count,
            intResError = intResError,
            keyboardOptions = keyboardOptions,
        )
    }

    val textField2: @Composable () -> Unit = {
        ExposedDropdownMenuSuffix(
            suffix = suffix,
            setSuffix = {
                onSuffixChange?.let { it1 ->
                    it1(it)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            },
            suffixList = suffixList,
            enableDropMenu = suffixEnabled,
            content = {
                BaseOutlinedTextNew(
                    modifier = it.first,
                    value = suffixText,
                    onValueChange = {},
                    leadingIconRes = R.drawable.baseline_edit_document_24,
                    labelIntRes = R.string.outlined_text_suffix,
                    intResSup = R.string.outlined_text_suffix,
                    keyboardOptions = keyboardOptions,
                    isMore = true,
                    readOnly = true,
                    enabled = suffixEnabled
                )
            },
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isBorderCard) BorderCard(modifier = Modifier.weight(1f)) {
            textField()
        } else Box(modifier = Modifier.weight(1f)) { textField() }
        if (isBorderCard) BorderCard(modifier = Modifier.weight(1f)) {
            textField2()
        } else Box(modifier = Modifier.weight(1f)) { textField2() }
    }
}


/*
@Composable
fun OutlinedTextPriceCount(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    focusManager: FocusManager
) {
    val sale = stringResource(R.string.sale_screen_title)
    val expenses = stringResource(R.string.expenses_screen_title)
    val writeOff = stringResource(R.string.write_off_screen_title)

    val list = listOf(
        Triple(
            R.drawable.baseline_add_shopping_cart_24,
            expenses,
            R.string.animal_indicators_screen_expenses_count
        ),
        Triple(R.drawable.baseline_add_card_24, sale, R.string.animal_indicators_screen_sale_count),
        Triple(
            R.drawable.baseline_edit_note_24,
            writeOff,
            R.string.animal_indicators_screen_write_off
        )
    )
    val expanded = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    var supportText by remember { mutableIntStateOf(R.string.animal_indicators_screen_expenses_count) }
    var leadingIcon by remember { mutableIntStateOf(R.drawable.baseline_add_shopping_cart_24) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    ExposedDropdownMenuIcon(
        selectedItemIndex = selectedItemIndex,
        list = list,
        expanded = expanded,
        setTitle = {
            selectedItemIndex = it.first
            leadingIcon = it.second
            supportText = it.third
        }
    ) { modifier ->
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(stringResource(R.string.outlined_text_price)) },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            leadingIcon = {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = stringResource(supportText)
                )
            },
            trailingIcon = {
                IconButton(onClick = { expanded.value = true }) {
                    Icon(
                        painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isError,
                    intRes = supportText,
                    intResError = R.string.error_no_count_sale
                )
            },
            suffix = {
                Text(text = stringResource(R.string.currency_ruble))
            },
            keyboardOptions = keyboardOptionsNextNumber(),
            keyboardActions = keyboardActionsDown(focusManager),
            isError = isError
        )

    }

}
*/
@Composable
fun OutlinedTextTitleSaleNew(
    value: String,
    onValueChange: (String) -> Unit = {},
    onValueChoice: (DomainTitleSuffixCategory) -> Unit = {},
    productOrigin: ProductOrigin?,
    @StringRes intResSup: Int = R.string.support_text_product,
    readOnly: Boolean = false,
    enable: Boolean = true,
    titleList: List<DomainTitleSuffixCategory>,
    isMore: Boolean = value.isBlank(),
    isErrorTitle: Boolean = false,
    isErrorSlash: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    var category by rememberSaveable { mutableStateOf(productOrigin) }
    var suffix by rememberSaveable { mutableStateOf(Suffix.PIECES) }

    BorderCard {
        ExposedDropdownMenuPair(
            title = value,
            setTitle = {
                onValueChoice(it)
                category = it.productOrigin
                suffix = it.suffix
                focusManager.moveFocus(FocusDirection.Down)
            },
            suffix = suffix,
            productOrigin = category,
            list = titleList,
            enableDropMenu = enable,
            content = {
                BaseOutlinedTextNew(
                    value = value,
                    onValueChange = { text -> onValueChange(text) },
                    onClear = {
                        onValueChange("")
                        category = null
                    },
                    leadingIconRes2 = category?.toDrawRes(),
                    leadingIconColor2 = category?.toColorList() ?: ProductOrigin.ADD.toColorList(),
                    labelIntRes = R.string.outlined_text_product,
                    isError = isErrorTitle, isErrorSlash = isErrorSlash,
                    intResSup = intResSup,
                    intResError = R.string.error_no_product,
                    readOnly = readOnly,
                    enabled = enable,
                    singleLine = false,
                    modifier = it.first,
                    isMore = isMore,
                    isNecessarily = true,
                    keyboardOptions = keyboardOptionsNext()
                )
            },
        )
    }
}

@Composable
fun OutlinedWriteOffStatus(
    value: Boolean,
    onValueChange: (Boolean) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var selectedItemIndex by remember { mutableIntStateOf(if (value) 0 else 1) }
    val statusList = listOf(
        Triple(R.drawable.baseline_cottage_24, R.string.ration_button_own_needs, true),
        Triple(R.drawable.baseline_delete_24, R.string.ration_button_disposal, false)
    )
    BorderCard {
        ExposedDropdownMenuStatusWriteOff(
            status = value,
            setStatus = {
                onValueChange(it.first)
                selectedItemIndex = it.second
                focusManager.moveFocus(FocusDirection.Down)
            },
            statusList = statusList
        ) {
            BaseOutlinedTextNew(
                value = stringResource(statusList[selectedItemIndex].second),
                onValueChange = { },
                leadingIconRes2 = statusList[selectedItemIndex].first,
                leadingIconColor2 = if (selectedItemIndex == 0) violet_1 else error_base,
                labelIntRes = R.string.outlined_text_status_write_off,
                intResSup = R.string.support_text_product,
                readOnly = true,
                modifier = it.first,
                isMore = true,
                keyboardOptions = keyboardOptionsNext()
            )
        }
    }
}

@Composable
fun OutlinedTextSexNew(
    value: Boolean,
    onValueChange: (Boolean) -> Unit = {},
    isBorderCard: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    var selectedItemIndex by remember { mutableIntStateOf(if (value) 0 else 1) }
    val statusList = listOf(
        Triple(R.drawable.baseline_male_24, R.string.animal_entry_screen_sex_man, true),
        Triple(R.drawable.baseline_female_24, R.string.animal_entry_screen_sex_woman, false)
    )
    val textField: @Composable () -> Unit = {
        ExposedDropdownMenuStatusWriteOff(
            status = value,
            setStatus = {
                onValueChange(it.first)
                selectedItemIndex = it.second
                focusManager.moveFocus(FocusDirection.Down)
            },
            statusList = statusList
        ) {
            BaseOutlinedTextNew(
                value = stringResource(statusList[selectedItemIndex].second),
                onValueChange = { },
                leadingIconRes2 = statusList[selectedItemIndex].first,
                leadingIconColor2 = if (selectedItemIndex == 0) violet_1 else error_base,
                labelIntRes = R.string.outlined_text_sex,
                intResSup = R.string.support_text_sex_animals,
                readOnly = true,
                modifier = it.first,
                isMore = true,
                keyboardOptions = keyboardOptionsNext()
            )
        }
    }
    if (isBorderCard) BorderCard { textField() } else textField()
}

@Composable
fun OutlinedTextBuyerNew(
    value: String,
    onValueChange: (String) -> Unit,
    onTrailingChance: () -> Unit = {},
    list: List<String>,
) {
    val focusManager = LocalFocusManager.current
    BorderCard {
        ExposedDropdownMenuCategoryBuyer(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = list
        ) {
            BaseOutlinedTextNew(
                value = value,
                onValueChange = { onValueChange(it) },
                onClear = { onValueChange("") },
                labelIntRes = R.string.outlined_text_buyer,
                intResSup = R.string.support_text_buyer,
                modifier = it.first,
                leadingIconRes = R.drawable.baseline_person_24,
                keyboardOptions = keyboardOptionsNext(),
                isMore = value.isBlank()
            )
        }
    }
}

@Composable
fun OutlinedTextAnimalTypeNew(
    value: String,
    @StringRes intResSup: Int,
    onValueChange: (String) -> Unit,
    onTrailingChance: () -> Unit = {},
    list: List<String>,
) {
    val focusManager = LocalFocusManager.current
    BorderCard {
        ExposedDropdownMenuCategoryBuyer(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = list
        ) {
            BaseOutlinedTextNew(
                value = value,
                onValueChange = onValueChange,
                onClear = { onValueChange("") },
                labelIntRes = R.string.outlined_text_type,
                intResSup = intResSup,
                isNecessarily = true,
                singleLine = false,
                modifier = it.first,
                leadingIconRes = R.drawable.baseline_pets_24,
                keyboardOptions = keyboardOptionsNext(),
                isMore = value.isBlank()
            )
        }
    }
}

@Composable
fun AnimalNameOutlinedTextNew(
    value: String,
    onValueChange: (String) -> Unit,
    isAnimalGroup: Boolean,
    isErrorTitle: Boolean
) {
    BorderCard {
        BaseOutlinedTextNew(
            value = value,
            onValueChange = onValueChange,
            labelIntRes = if (!isAnimalGroup) R.string.outlined_text_name_animal else R.string.outlined_text_name_animals,
            isError = isErrorTitle,
            isNecessarily = true,
            singleLine = false,
            intResSup = if (!isAnimalGroup) R.string.support_text_name_animal else R.string.support_text_names_animals,
            intResError = if (!isAnimalGroup) R.string.error_no_name_animal else R.string.error_no_name_animals,
            keyboardOptions = keyboardOptionsNext()
        )
    }
}
/*
@Composable
fun OutlinedTextSex(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    standardPadding: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    val setList = listOf(
        Triple(
            true,
            R.drawable.baseline_male_24,
            stringResource(R.string.animal_entry_screen_sex_man)
        ),
        Triple(
            false,
            R.drawable.baseline_female_24,
            stringResource(R.string.animal_entry_screen_sex_woman)
        )
    )
    val current = setList.first { it.first == value }
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuSex(
            sex = value,
            setSex = {
                onValueChange(it)
                focusManager.moveFocus(FocusDirection.Down)
            },
            standardPadding = standardPadding,
            setList = setList,
            isFilterUsed = false
        ) {
            BaseOutlinedText(
                value = current.third,
                onValueChange = {},
                readOnly = true,
                labelIntRes = R.string.outlined_text_sex,
                modifier = it.first,
                intResSup = R.string.support_text_sex_animals,
                leadingIconRes = current.second
//            onTrailingChance = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = it.second)}
            )
        }
    }
}

@Composable
fun OutlinedTextCountNoCard(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChance: (Suffix) -> Unit = {},
    isError: Boolean,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    versionDropMenu: DropdownMenu = DropdownMenu.ALL,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    keyboardActions: KeyboardActionFocus = KeyboardActionFocus.DOWN
) {
    BaseOutlinedText(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it) },
        labelIntRes = intRes,
        intResSup = intResSup,
        intResError = intResError,
        leadingIconRes = drawableRes,
        isError = isError,
        versionDropMenu = versionDropMenu,
        suffix = suffix,
        onSuffixChance = { onSuffixChance(it) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
fun WeightOutlinedText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChance: (Suffix) -> Unit,
) {
    BaseOutlinedText(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        suffix = suffix,
        onSuffixChance = onSuffixChance,
        versionDropMenu = DropdownMenu.WEIGHT,
        isError = false,
        leadingIconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
        labelIntRes = R.string.weight_screen_title,
        intResSup = R.string.support_text_weight,
        keyboardOptions = keyboardOptionsNextNumber(),
    )
}




@Composable
fun BaseOutlinedText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix? = null,
    onSuffixChance: ((Suffix) -> Unit)? = null,
    trailingIcon: Int? = null,
    onTrailingChance: (() -> Unit)? = null,
    versionDropMenu: DropdownMenu = DropdownMenu.ALL,
    isError: Boolean = false,
    isErrorSlash: Boolean = false,
    isWarehouseShow: Boolean = false,
    warehouseList: List<DomainCountSuffix> = emptyList(),
    isAnimal: Boolean = false,
    countAnimal: String = "",
    leadingIconRes: Int? = null,
    leadingIconClick: () -> Unit = {},
    readOnly: Boolean = false,
    enable: Boolean = true,
    @StringRes labelIntRes: Int,
    @StringRes intResSup: Int,
    @StringRes intResError: Int = R.string.error_no_count_product,
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActionFocus = KeyboardActionFocus.DOWN
) {
    val leadingIcon: @Composable (() -> Unit)? = if (leadingIconRes != null) {
        {
            IconButton(onClick = { leadingIconClick() }) {
                Icon(
                    painter = painterResource(leadingIconRes),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
        }
    } else null

    val suffixValue: @Composable (() -> Unit)? =
        suffix?.let { { Text(text = stringResource(it.toResId())) } }

    val trailingIcon: @Composable (() -> Unit)? = when {
        onSuffixChance != null -> {
            { GetDropDownMenu(versionDropMenu) { onSuffixChance(it) } }
        }

        onTrailingChance != null -> {
            {
                IconButton(onClick = { onTrailingChance() }) {
                    trailingIcon?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        else -> null
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(labelIntRes)) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        supportingText = {
            ErrorSupportTextSlash(
                isError = isError,
                isErrorSlash = isErrorSlash,
                isWarehouse = isWarehouseShow,
                warehouseList = warehouseList,
                isAnimal = isAnimal,
                countAnimals = countAnimal,
                intRes = intResSup,
                intResError = intResError,
                suffix = suffix ?: Suffix.PIECES
            )
        },
        trailingIcon = trailingIcon,
        suffix = suffixValue,
        readOnly = readOnly,
        enabled = enable,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions.toFocus(focusManager),
        isError = isError
    )
}
*/



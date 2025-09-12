@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.fermacompose2.supportFun.animatedErrorPadding
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsEnter
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsEnter
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSampleNoLimit
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
//import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
//import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSampleNoLimit
//import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import java.time.Instant


@Composable
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


@Composable
fun OutlinedTextNote(
    value: String,
    onValueChange: (String) -> Unit,
    cardBorder: Boolean = true,
    @StringRes label: Int = R.string.outlined_text_note,
    @StringRes supportingText: Int = R.string.support_text_note,
) {

    val outlined: @Composable () -> Unit = {
        BaseOutlinedText(
            value = value,
            onValueChange = { onValueChange(it) },
            leadingIconRes = R.drawable.baseline_sticky_note_2_24,
            labelIntRes = label,
            intResSup = supportingText,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = KeyboardActionFocus.CLEAN
        )
    }

    if (cardBorder) {
        CardField(
            modifier = Modifier
                .toOutlinedText(),
            row = false
        ) {
            outlined()
        }
    } else outlined()
}

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

@Composable
fun OutlinedTextAnimal(
    value: String,
    onValueChange: (Pair<Long, String>) -> Unit,
    onClickClear: (String) -> Unit,
    selectedAnimalIndex: Long,
    animalList: List<AnimalForAddDomain>,
) {
    val focusManager = LocalFocusManager.current
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuAnimals(
            selectedItemIndex = selectedAnimalIndex,
            setTitle = {
                onValueChange(it)
                focusManager.clearFocus()
            },
            animalList = animalList
        ) {
            BaseOutlinedText(
                modifier = it,
                value = value,
                onValueChange = {},
                leadingIconRes = R.drawable.baseline_pets_24,
                readOnly = true,
                labelIntRes = R.string.outlined_text_animals,
                intResSup = R.string.support_text_animal,
                trailingIcon = R.drawable.baseline_clear_24,
                onTrailingChance = {
                    onClickClear("")
                    focusManager.clearFocus()
                }
            )
        }
    }
}

@Composable
fun OutlinedTextDate(
    value: String,
    initialSelectedDateMilli: Long? = Instant.now().toEpochMilli(),
    @StringRes intRes: Int = R.string.outlined_text_date,
    @StringRes intResSup: Int = R.string.support_text_date,
    drawableRes: Int = R.drawable.baseline_calendar_month_24,
    onValueChange: (String) -> Unit,
    cardBorder: Boolean = true
) {
    var openDialog by remember { mutableStateOf(false) }
    val state = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = initialSelectedDateMilli
    )
    if (openDialog) {
        DatePickerDialogSample(state, value) {
            onValueChange(it)
            openDialog = !openDialog
        }
    }

    val textField: @Composable () -> Unit = {
        OutlinedTextField(
            value = value,
            onValueChange = {
                openDialog = !openDialog
            },
            readOnly = true,
            label = { Text(stringResource(intRes)) },
            supportingText = {
                Text(stringResource(intResSup))
            },
            leadingIcon = {
                IconButton(onClick = { openDialog = !openDialog }) {
                    Icon(
                        painter = painterResource(drawableRes),
                        contentDescription = stringResource(R.string.content_description_show_calendary)
                    )
                }
            },
            modifier = Modifier
                .toOutlinedText()
                .clickable { openDialog = !openDialog }
        )
    }
    if (cardBorder) {
        CardField(
            modifier = Modifier
                .toOutlinedText(),
            row = false
        ) {
            textField()
        }
    } else textField()
}

@Composable
fun OutlinedTextDateEdit(
    value: String,
    @StringRes intRes: Int = R.string.outlined_text_date,
    @StringRes intResSup: Int = R.string.support_text_date,
    drawableRes: Int = R.drawable.baseline_calendar_month_24,
    onValueChange: (String) -> Unit,
    enable: Boolean = true
) {
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = formatDateToLong(value)
        )
        DatePickerDialogSample(datePickerState, value) {
            onValueChange(it)
            openDialog = !openDialog
        }
    }
    CardField(
        modifier = Modifier
            .toOutlinedText()
            .clickable(onClick = { openDialog = !openDialog }),
        row = false,
    ) {
        BaseOutlinedText(
            value = value,
            onValueChange = { openDialog = !openDialog },
            readOnly = true,
            enable = enable,
            labelIntRes = intRes,
            intResSup = intResSup,
            leadingIconRes = drawableRes,
            leadingIconClick = { openDialog = !openDialog }
        )
    }
}

@Composable
fun OutlinedTextDateNoLimit(
    value: String,
    initialSelectedDateMilli: Long? = Instant.now().toEpochMilli(),
    @StringRes intRes: Int = R.string.outlined_text_date,
    @StringRes intResSup: Int = R.string.support_text_date,
    drawableRes: Int = R.drawable.baseline_calendar_month_24,
    onValueChange: (String) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }
    val datePickerStateNoLimit =
        rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMilli)

    if (openDialog) {
        DatePickerDialogSampleNoLimit(datePickerStateNoLimit, value) {
            onValueChange(it)
            openDialog = !openDialog
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            openDialog = !openDialog
        },
        readOnly = true,
        label = { Text(stringResource(intRes)) },
        supportingText = {
            Text(stringResource(intResSup))
        },
        leadingIcon = {
            IconButton(onClick = { openDialog = !openDialog }) {
                Icon(
                    painter = painterResource(drawableRes),
                    contentDescription = stringResource(R.string.content_description_show_calendary)
                )
            }
        },
        modifier = Modifier
            .toOutlinedText()
            .clickable { openDialog = !openDialog }
    )
}

@Composable
fun OutlinedTextCategory(
    value: String,
    onValueChange: (String) -> Unit,
    titleList: List<String>,
    enable: Boolean = true,
    readOnly: Boolean = false,
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuProduct(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = titleList
        ) {
            BaseOutlinedText(
                modifier = it.first,
                value = value,
                onValueChange = { onValueChange(it) },
                leadingIconRes = R.drawable.baseline_format_list_bulleted_24,
                isError = false,
                labelIntRes = R.string.outlined_text_field_category,
                intResSup = R.string.support_text_category,
                keyboardOptions = keyboardOptionsNext(),
                enable = enable,
                readOnly = readOnly
            )
        }
    }
}

@Composable
fun OutlinedTextCountAnimal(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSuffixChange: ((String) -> Unit)? = null,
    isError: Boolean,
    suffix: String,
    isAnimal: Boolean = false,
    isWarehouseShow: Boolean = true,
    isDropMenuShow: Boolean = true,
    versionDropMenu: Int = 5,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    @StringRes tooltipTextResAutoCal: Int = R.string.tooltip_auto_calculate_weight,
    countWarehouse: String = "",
    countWarehouseSuffix: String = "",
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    isWeightCalculate: Boolean = false,
    weightValue: String = "",
    onWeightChange: (String) -> Unit = {},
    weightSuffix: String = stringResource(R.string.suffix_kilogram),
    onWeightSuffixChance: (String) -> Unit = {},
    isNecessarily: Boolean = true,
    isAutoCalculate: Boolean = true,
    onAutoCalculate: (Boolean) -> Unit = {},
) {
    CardField(
        modifier = Modifier
            .toOutlinedText()
            .padding(bottom = animatedErrorPadding(isAutoCalculate)),
        row = false,
        isNecessarily = isNecessarily
    ) {
        BaseOutlinedText(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            suffix = suffix,
            onSuffixChance = onSuffixChange,
            versionDropMenu = versionDropMenu,
            isError = isError,
            isWarehouseShow = isWarehouseShow,
            leadingIconRes = drawableRes,
            labelIntRes = intRes,
            intResSup = intResSup,
            intResError = intResError,
            keyboardOptions = keyboardOptions,
        )
        if (isWeightCalculate && suffix !in setOf(
                stringResource(R.string.suffix_kilogram),
                stringResource(R.string.suffix_tons),
                stringResource(R.string.suffix_gram)
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
            )
    }
}

@Composable
fun OutlinedTextCount2(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSuffixChange: ((String) -> Unit)? = null,
    isError: Boolean,
    suffix: String,
    isAnimal: Boolean = false,
    isWarehouseShow: Boolean = true,
    isDropMenuShow: Boolean = true,
    versionDropMenu: Int = 5,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    @StringRes tooltipTextResAutoCal: Int = R.string.tooltip_auto_calculate_weight,
    warehouseList: List<DomainCountSuffix>,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    isWeightCalculate: Boolean = false,
    weightValue: String = "",
    onWeightChange: (String) -> Unit = {},
    weightSuffix: String = stringResource(R.string.suffix_kilogram),
    onWeightSuffixChance: (String) -> Unit = {},
    isAutoCalculate: Boolean = true,
    onAutoCalculate: (Boolean) -> Unit = {},
    cardBorder: Boolean = true
) {

    val textField: @Composable () -> Unit = {
        BaseOutlinedText(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            suffix = suffix,
            onSuffixChance = onSuffixChange,
            versionDropMenu = versionDropMenu,
            isError = isError,
            isWarehouseShow = isWarehouseShow,
            warehouseList = warehouseList,
            leadingIconRes = drawableRes,
            labelIntRes = intRes,
            intResSup = intResSup,
            intResError = intResError,
            keyboardOptions = keyboardOptions,
        )
        if (isWeightCalculate && suffix !in setOf(
                stringResource(R.string.suffix_kilogram),
                stringResource(R.string.suffix_tons),
                stringResource(R.string.suffix_gram)
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
            )
    }
    if (cardBorder) {
        CardField(
            modifier = Modifier
                .toOutlinedText()
                .padding(bottom = animatedErrorPadding(isAutoCalculate)),
            row = false,
            isNecessarily = true
        ) {
            textField()
        }
    } else textField()
}


@Composable
fun OutlinedTextCountAnimal2(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    isErrorCountMore: Boolean = false,
    isErrorCountZero: Boolean = false,
    countAnimalAll: String = "",
    suffix: String,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
) {
    val errorText = when {
        isErrorCountZero -> R.string.error_count_zero_animals
        isErrorCountMore -> R.string.error_count_sale_animals
        else -> R.string.error_no_count_product
    }

    val error = isError || isErrorCountZero || isErrorCountMore

    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false,
        isNecessarily = true
    ) {
        BaseOutlinedText(
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
}

@Composable
fun OutlinedTextTitleAdd2(
    value: String,
    onValueChange: (String) -> Unit,
    onValueChangeSuffix: (Pair<String, String>) -> Unit,
    @StringRes intRes: Int = R.string.outlined_text_product,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_product,
    drawableRes: Int? = null,
    readOnly: Boolean = false,
    enable: Boolean = true,
    titleList: List<TitleAndSuffixDomain>,
    isErrorTitle: Boolean,
    isErrorSlash: Boolean,
    cardBorder: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    val textField: @Composable () -> Unit = {
        ExposedDropdownMenuProduct2(
            title = value,
            setTitle = {
                onValueChangeSuffix(it)
                focusManager.moveFocus(FocusDirection.Down)
            },
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
                readOnly = readOnly, enable = enable,
                keyboardOptions = keyboardOptionsNext()
            )
        }
    }

    if (cardBorder)
        CardField(
            modifier = Modifier
                .toOutlinedText(),
            row = false,
            isNecessarily = true
        ) {
            textField()
        }
    else textField()
}

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

@Composable
fun OutlinedPriceInput(
    price: String,
    onPriceChange: (String) -> Unit,
    priceAll: String = "",
    isAutoCalculate: Boolean,
    onAutoCalculate: (Boolean) -> Unit,
    isManyCount: Boolean = false,
    isError: Boolean = false,
    isNecessarily: Boolean = false,
    @StringRes supportTextRes: Int = R.string.support_text_count_product_sale,
    @StringRes supportTextResAutoCal: Int = R.string.support_text_count_product_sale,
    @StringRes tooltipTextResAutoCal: Int = R.string.tooltip_auto_calculate_price,
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
    val supportText = if (isAutoCalculate) supportTextRes else supportTextResAutoCal
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false,
        isNecessarily = isNecessarily
    ) {
        BaseOutlinedText(
            value = price,
            onValueChange = { onPriceChange(it) },
            labelIntRes = R.string.outlined_text_price,
            intResSup = supportText,
            intResError = R.string.error_no_count_sale,
            isError = isError,
            suffix = stringResource(R.string.currency_ruble),
            modifier = Modifier
                .padding(bottom = animatedPadding.coerceAtLeast(0.dp)),
            leadingIconRes = R.drawable.baseline_add_card_24,
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
                price = priceAll,
            )
        }
    }
}

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
                        imageVector = Icons.Default.ArrowDropDown,
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

@Composable
fun OutlinedTextTitleSale(
    value: String,
    onValueChange: (String) -> Unit = {},
    onValueChoice: (DomainTitleSuffixCategory) -> Unit = {},
    readOnly: Boolean = false,
    enable: Boolean = true,
    titleList: List<DomainTitleSuffixCategory>,
    isErrorTitle: Boolean = false,
    isErrorSlash: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var leadingIconRes by rememberSaveable { mutableStateOf<Int?>(null) }
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false,
        isNecessarily = true
    ) {
        ExposedDropdownMenuPair(
            title = value,
            setTitle = {
                onValueChoice(it)
                leadingIconRes = it.category.ordinal
                focusManager.moveFocus(FocusDirection.Down)
            },
            list = titleList
        ) {
            BaseOutlinedText(
                value = value,
                onValueChange = { text -> onValueChange(text) },
                leadingIconRes = leadingIconRes,
                labelIntRes = R.string.outlined_text_product,
                isError = isErrorTitle, isErrorSlash = isErrorSlash,
                intResSup = R.string.support_text_product,
                intResError = R.string.error_no_product,
                readOnly = readOnly,
                modifier = it,
                keyboardOptions = keyboardOptionsNext()
            )
        }
    }
}

@Composable
fun OutlinedTextBuyer(
    value: String,
    onValueChange: (String) -> Unit,
    onTrailingChance: () -> Unit = {},
    list: List<String>,
) {
    val focusManager = LocalFocusManager.current
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuProduct(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = list
        ) {
            BaseOutlinedText(
                value = value,
                onValueChange = { onValueChange(it) },
                labelIntRes = R.string.outlined_text_buyer,
                intResSup = R.string.support_text_buyer,
                modifier = it.first,
                leadingIconRes = R.drawable.baseline_person_24,
                trailingIcon = R.drawable.baseline_clear_24,
                onTrailingChance = {
                    onTrailingChance()
                    focusManager.clearFocus()
                },
                keyboardOptions = keyboardOptionsNext(),
            )
        }
    }
}

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
    suffix: String,
    onSuffixChance: (String) -> Unit = {},
    isError: Boolean,
    @StringRes intRes: Int = R.string.outlined_text_field_quantity,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    versionDropMenu: Int = 5,
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
    suffix: String,
    onSuffixChance: (String) -> Unit,
) {
    BaseOutlinedText(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        suffix = suffix,
        onSuffixChance = onSuffixChance,
        versionDropMenu = 0,
        isError = false,
        leadingIconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
        labelIntRes = R.string.weight_screen_title,
        intResSup = R.string.support_text_weight,
        keyboardOptions = keyboardOptionsNextNumber(),
    )
}

@Composable
fun AnimalNameOutlinedText(
    value: String,
    onValueChange: (String) -> Unit,
    isAnimalGroup: Boolean,
    isErrorTitle: Boolean
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false,
        isNecessarily = true
    ) {
        BaseOutlinedText(
            value = value,
            onValueChange = onValueChange,
            labelIntRes = if (!isAnimalGroup) R.string.outlined_text_name_animal else R.string.outlined_text_name_animals,
            isError = isErrorTitle,
            intResSup = if (!isAnimalGroup) R.string.support_text_name_animal else R.string.support_text_names_animals,
            intResError = if (!isAnimalGroup) R.string.error_no_name_animal else R.string.error_no_name_animals,
            keyboardOptions = keyboardOptionsNext()
        )
    }
}


@Composable
fun BaseOutlinedText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String? = null,
    onSuffixChance: ((String) -> Unit)? = null,
    trailingIcon: Int? = null,
    onTrailingChance: (() -> Unit)? = null,
    versionDropMenu: Int = 5,
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
    val suffixValue: @Composable (() -> Unit)? = suffix?.let { { Text(text = it) } }
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
                suffix = suffix ?: ""
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

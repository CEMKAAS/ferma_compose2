@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsEnter
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsEnter
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsGo
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toConvertOnlyInt
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSampleNoLimit
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import java.time.Instant

@Composable
fun OutlinedTextNote(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int = R.string.outlined_text_note,
    @StringRes supportingText: Int = R.string.support_text_note,
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        BaseOutlinedText(
            value = value,
            onValueChange = { onValueChange(it) },
            leadingIconRes = R.drawable.baseline_sticky_note_2_24,
            intRes = label,
            intResSup = supportingText,
            keyboardOptions = keyboardOptionsGo(),
            keyboardActions = KeyboardActionFocus.CLEAN
        )
    }
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
    onValueChange: (Pair<Int, String>) -> Unit,
    onClickClear: (String) -> Unit,
    selectedAnimalIndex: Int,
    animalList: List<TripleData>,
    focusManager: FocusManager
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuAnimals(
            title = value,
            selectedItemIndex = selectedAnimalIndex,
            setTitle = {
                onValueChange(it)
            },
            animalList = animalList
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                label = { Text(stringResource(R.string.outlined_text_animals)) },
                readOnly = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_pets_24),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onClickClear("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.content_description_clear)
                        )
                    }
                },
                supportingText = {
                    Text(stringResource(R.string.support_text_animal))
                },
                modifier = it
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

    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
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
}

@Composable
fun OutlinedTextDateEdit(
    value: String,
    @StringRes intRes: Int = R.string.outlined_text_date,
    @StringRes intResSup: Int = R.string.support_text_date,
    drawableRes: Int = R.drawable.baseline_calendar_month_24,
    onValueChange: (String) -> Unit,
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
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        BaseOutlinedText(
            value = value,
            onValueChange = {
                openDialog = !openDialog
            },
            readOnly = true,
            intRes = intRes,
            intResSup = intResSup,
            leadingIconRes = drawableRes,
            modifier = Modifier
                .clickable { openDialog = !openDialog },
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
    titleList: List<String>
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
                intRes = R.string.outlined_text_field_category,
                intResSup = R.string.support_text_category,
                keyboardOptions = keyboardOptionsNext(),
            )
        }
    }
}

@Composable
fun OutlinedTextCount(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSuffixChange: (String) -> Unit = {},
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
    countWarehouse: Double = 0.0,
    countWarehouseSuffix: String = "",
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    isWeightCalculate: Boolean = false,
    weightValue: String = "",
    onWeightChange: (String) -> Unit = {},
    weightSuffix: String = stringResource(R.string.suffix_kilogram),
    onWeightSuffixChance: (String) -> Unit = {},
    isAutoCalculate: Boolean = true,
    onAutoCalculate: (Boolean) -> Unit = {},
) {
    val animatedPadding by animateDpAsState(
        targetValue = if (isAutoCalculate) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    CardField(
        modifier = Modifier
            .toOutlinedText()
            .padding(bottom = animatedPadding.coerceAtLeast(0.dp)),
        row = false
    ) {
        BaseOutlinedText(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            suffix = suffix,
            onSuffixChance = onSuffixChange,
            isError = isError,
            isWarehouseShow = isWarehouseShow,
            countWarehouse = countWarehouse.formatNumber(),
            suffixWarehouse = countWarehouseSuffix,
            leadingIconRes = drawableRes,
            intRes = intRes,
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
fun OutlinedTextCount(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    isErrorCountMore: Boolean = false,
    isErrorCountZero: Boolean = false,
    count: String = "0",
    suffix: String,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_product,
    focusManager: FocusManager,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    keyboardActions: KeyboardActions = keyboardActionsDown(focusManager)
) {

    val errorText = stringResource(R.string.error_no_count_product)
    val errorCountAllText = stringResource(R.string.error_count_sale_animals)
    val errorCountZeroText = stringResource(R.string.error_count_zero_animals)
    val supportText = stringResource(R.string.support_text_count_sale_animals, count, suffix)


    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it.toConvertOnlyInt())
            },
            label = { Text(stringResource(intRes)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(drawableRes),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
            },
            supportingText = {
                ErrorSupportAnimal(
                    isError = isError,
                    isErrorAnimal = isErrorCountMore,
                    isErrorCountZero = isErrorCountZero,
                    errorText = errorText,
                    errorCountAllText = errorCountAllText,
                    errorCountZeroText = errorCountZeroText,
                    supportText = supportText,
                )
            },
            suffix = {
                Text(text = suffix)
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError || isErrorCountMore || isErrorCountZero
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
    titleList: List<String>,
    isErrorTitle: Boolean,
    isErrorSlash: Boolean,
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
                leadingIconRes = drawableRes,
                isError = isErrorTitle,
                isErrorSlash = isErrorSlash,
                intRes = intRes,
                intResError = intResError,
                intResSup = intResSup
            )
        }
    }
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
    count: String = "",
    isAutoCalculate: Boolean,
    onAutoCalculate: (Boolean) -> Unit,
    isManyCount: Boolean = false,
    isError: Boolean = false,
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
        row = false
    ) {
        BaseOutlinedText(
            value = price,
            onValueChange = { onPriceChange(it) },
            intRes = R.string.outlined_text_price,
            intResSup = supportText,
            intResError = R.string.error_no_count_sale,
            isError = isError,
            suffix = stringResource(R.string.currency_ruble),
            modifier = Modifier
                .padding(bottom = animatedPadding.coerceAtLeast(0.dp)),
            leadingIconRes = R.drawable.baseline_add_card_24,
            keyboardOptions = keyboardOptionsNextNumber(),
        )
        if (isManyCount) {
            AutoCalculateCheckbox(
                isChecked = isAutoCalculate,
                onCheckedChange = onAutoCalculate,
                tooltipTextResAutoCal = tooltipTextResAutoCal,
                price = price,
                count = count
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
    onValueChoice: (Triple<Int, String, String>) -> Unit,
    readOnly: Boolean = false,
    enable: Boolean = true,
    selectedItemIndex: Int,
    titleList: List<PairData>,
    isErrorTitle: Boolean = false,
    isErrorSlash: Boolean = false,
    focusManager: FocusManager
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuPair(
            title = value,
            selectedItemIndex = selectedItemIndex,
            setTitle = {
                onValueChoice(it)
            },
            list = titleList
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { text ->
                    onValueChange(text)
                },
                label = { Text(text = stringResource(R.string.outlined_text_product)) },
                supportingText = {
                    ErrorSupportTextSlash(
                        isError = isErrorTitle,
                        isErrorSlash = isErrorSlash,
                        intRes = R.string.support_text_product,
                        intResError = R.string.error_no_product,
                    )
                },
                readOnly = readOnly,
                modifier = it,
                enabled = enable,
                isError = isErrorTitle,
                keyboardOptions = keyboardOptionsNext(),
                keyboardActions = keyboardActionsDown(focusManager)
            )
        }
    }
}

@Composable
fun OutlinedTextBuyer(
    value: String,
    onValueChange: (String) -> Unit,
    list: List<String>,
    focusManager: FocusManager
) {
    CardField(
        modifier = Modifier.toOutlinedText(),
        row = false
    ) {
        ExposedDropdownMenuProduct(
            title = value,
            setTitle = { onValueChange(it) },
            titleList = list
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { text ->
                    onValueChange(text)
                },
                label = { Text(stringResource(R.string.outlined_text_buyer)) },
                modifier = it.first,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_person_24),
                        contentDescription = null
                    )
                },
                supportingText = {
                    Text(stringResource(R.string.support_text_buyer))
                },
                trailingIcon = {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.content_description_clear)
                        )
                    }
                },
                keyboardOptions = keyboardOptionsNext(),
                keyboardActions = keyboardActionsDown(focusManager)
            )
        }
    }
}

@Composable
fun OutlinedTextSex(
    value: String,
    onValueChange: (String) -> Unit,
    standardPadding: Boolean = true,
    focusManager: FocusManager
) {
    val list = arrayListOf("Мужской", "Женский")

    ExposedDropdownMenuSex(
        title = value,
        setTitle = { onValueChange(it) },
        standardPadding = standardPadding,
        titleList = list,
        isFilterUsed = false
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.outlined_text_sex)) },
            modifier = it.first,
            leadingIcon = {
                Icon(
                    painter = painterResource(if (value == list[0]) R.drawable.baseline_male_24 else R.drawable.baseline_female_24),
                    contentDescription = null
                )
            },
            supportingText = {
                Text(stringResource(R.string.support_text_sex_animals))
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = it.second) },
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = keyboardActionsDown(focusManager)
        )
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
        intRes = intRes,
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
        intRes = R.string.weight_screen_title,
        intResSup = R.string.support_text_weight,
        keyboardOptions = keyboardOptionsNextNumber(),
    )
}

@Composable
fun BaseOutlinedText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String? = null,
    onSuffixChance: ((String) -> Unit)? = null,
    versionDropMenu: Int = 5,
    isError: Boolean = false,
    isErrorSlash: Boolean = false,
    isWarehouseShow: Boolean = false,
    countWarehouse: String = "",
    suffixWarehouse: String = "",
    isAnimal: Boolean = false,
    countAnimal: String = "",
    leadingIconRes: Int? = null,
    leadingIconClick: () -> Unit = {},
    readOnly: Boolean = false,
    @StringRes intRes: Int,
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
    val suffixValue: @Composable (() -> Unit)? = if (suffix != null) {
        {
            Text(text = suffix)
        }
    } else null
    val trailingIcon: @Composable (() -> Unit)? = if (onSuffixChance != null) {
        {
            GetDropDownMenu(versionDropMenu) { onSuffixChance(it) }
        }
    } else null

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(intRes)) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        supportingText = {
            ErrorSupportTextSlash(
                isError = isError,
                intRes = intResSup,
                intResError = intResError
            )
            ErrorSupportTextSlash(
                isError = isError,
                isErrorSlash = isErrorSlash,
                isWarehouse = isWarehouseShow,
                countWarehouse = countWarehouse,
                suffixWarehouse = suffixWarehouse,
                isAnimal = isAnimal,
                countAnimals = countAnimal,
                intRes = intResSup,
                intResError = intResError,
            )
        },
        trailingIcon = trailingIcon,
        suffix = suffixValue,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions.toFocus(focusManager),
        isError = isError
    )
}
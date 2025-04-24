@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsClear
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsDown
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsGo
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.ui.start.formatter


@Composable
fun OutlinedTextNote(
    value: String,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(R.string.outlined_text_note)) },
        modifier = Modifier.toOutlinedText(),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_sticky_note_2_24),
                contentDescription = null
            )
        },
        supportingText = {
            Text(stringResource(R.string.support_text_note))
        },
        keyboardOptions = keyboardOptionsGo(),
        keyboardActions = keyboardActionsClear(focusManager)
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

@Composable
fun OutlinedTextDate(
    value: String,
    @StringRes intRes: Int = R.string.outlined_text_date,
    @StringRes intResSup: Int = R.string.support_text_date,
    onValueChange: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange()
        },
        readOnly = true,
        label = { Text(stringResource(intRes)) },
        supportingText = {
            Text(stringResource(intResSup))
        },
        leadingIcon = {
            IconButton(onClick = onValueChange) {
                Icon(
                    painter = painterResource(R.drawable.baseline_calendar_month_24),
                    contentDescription = stringResource(R.string.content_description_show_calendary)
                )
            }
        },
        modifier = Modifier
            .toOutlinedText()
            .clickable { onValueChange() }
    )
}

@Composable
fun OutlinedTextCategory(
    value: String,
    onValueChange: (String) -> Unit,
    titleList: List<String>,
    focusManager: FocusManager
) {
    ExposedDropdownMenuProduct(
        title = value,
        setTitle = { onValueChange(it.trim()) },
        titleList = titleList
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { text -> onValueChange(text.trim()) },
            label = { Text(stringResource(R.string.outlined_text_field_category)) },
            modifier = it.first,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_format_list_bulleted_24),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
            },
            supportingText = {
                Text(stringResource(R.string.support_text_category))
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

@Composable
fun OutlinedTextCount(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: (String) -> Unit = {},
    isError: Boolean,
    suffix: String,
    isAnimal: Boolean = false,
    isWarehouseShow: Boolean = true,
    isDropMenuShow: Boolean = true,
    versionDropMenu: Int = 5,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_product,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_count_product,
    countWarehouse: Double = 0.0,
    focusManager: FocusManager,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    keyboardActions: KeyboardActions = keyboardActionsDown(focusManager)
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(intRes)) },
        modifier = Modifier.toOutlinedText(),
        leadingIcon = {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = null,
                modifier = Modifier.padding(end = 5.dp)
            )
        },
        supportingText = {
            ErrorSupportTextSlash(
                isError = isError,
                isWarehouse = isWarehouseShow,
                isAnimal = isAnimal,
                count = formatter(countWarehouse),
                suffix = suffix,
                intRes = intResSup,
                intResError = intResError
            )
        },
        trailingIcon = {
            if (isDropMenuShow)
                GetDropDownMenu(versionDropMenu) { onClick(it) }
        },
        suffix = {
            Text(text = suffix)
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError
    )
}

@Composable
fun OutlinedTextCount(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    isErrorCountMore: Boolean,
    count: Int = 0,
    suffix: String,
    drawableRes: Int = R.drawable.baseline_shopping_basket_24,
    @StringRes intRes: Int = R.string.outlined_text_product,
    focusManager: FocusManager,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    keyboardActions: KeyboardActions = keyboardActionsDown(focusManager)
) {

    val errorText = stringResource(R.string.error_no_count_product)
    val errorCountAllText = stringResource(R.string.error_count_sale_animals)
    val supportText = stringResource(R.string.support_text_count_sale_animals, count, suffix)


    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(intRes)) },
        modifier = Modifier.toOutlinedText(),
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
                errorText = errorText,
                errorCountAllText = errorCountAllText,
                supportText = supportText,
            )
        },
        suffix = {
            Text(text = suffix)
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError || isErrorCountMore
    )
}


@Composable
fun OutlinedTextTitleAdd(
    @StringRes intRes: Int = R.string.outlined_text_product,
    @StringRes intResSup: Int = R.string.support_text_product,
    @StringRes intResError: Int = R.string.error_no_product,
    drawableRes: Int? = null,
    value: String,
    onValueChange: (String) -> Unit,
    titleList: List<String>,
    isErrorTitle: Boolean,
    isErrorSlash: Boolean,
    focusManager: FocusManager
) {
    ExposedDropdownMenuProduct(
        title = value,
        setTitle = {
            onValueChange(it)
        },
        titleList = titleList
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { text ->
                onValueChange(text)
            },
            label = { Text(text = stringResource(intRes)) },
            leadingIcon = {
                if (drawableRes != null)
                    Icon(
                        painter = painterResource(drawableRes),
                        contentDescription = null
                    )
            },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isErrorTitle,
                    isErrorSlash = isErrorSlash,
                    intRes = intResSup,
                    intResError = intResError,
                )
            },
            modifier = it.first,
            isError = isErrorTitle,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = keyboardActionsDown(focusManager)
        )
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
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(R.string.outlined_text_price)) },
        modifier = Modifier.toOutlinedText(),
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

@Composable
fun OutlinedTextTitleSale(
    value: String,
    onValueChange: (String) -> Unit = {},
    onValueChoice: (Triple<Int, String, String>) -> Unit,
    readOnly: Boolean = false,
    selectedItemIndex: Int,
    titleList: List<PairData>,
    isErrorTitle: Boolean = false,
    isErrorSlash: Boolean = false,
    focusManager: FocusManager
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
            isError = isErrorTitle,
            keyboardOptions = keyboardOptionsNext(),
            keyboardActions = keyboardActionsDown(focusManager)
        )
    }
}

@Composable
fun OutlinedTextBuyer(
    value: String,
    onValueChange: (String) -> Unit,
    list: List<String>,
    focusManager: FocusManager
) {
    ExposedDropdownMenuProduct(
        title = value,
        setTitle = { onValueChange(it.trim()) },
        titleList = list
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { text ->
                onValueChange(text.trim())
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

@Composable
fun OutlinedTextSex(
    value: String,
    selectedItemIndex: Int,
    onValueChange: (Int) -> Unit,
    list: List<String>,
    focusManager: FocusManager
) {
    ExposedDropdownMenuSex(
        title = value,
        setTitle = { onValueChange(it) },
        selectedItemIndex = selectedItemIndex,
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

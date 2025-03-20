package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
    onValueChange: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange()
        },
        readOnly = true,
        label = { Text(stringResource(R.string.outlined_text_date)) },
        supportingText = {
            Text(stringResource(R.string.support_text_date))
        },
        trailingIcon = {
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
            modifier = it,
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
    onClick: (String) -> Unit,
    isError: Boolean,
    suffix: String,
    countWarehouse: Double = 0.0,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(stringResource(R.string.outlined_text_field_quantity)) },
        modifier = Modifier.toOutlinedText(),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_shopping_basket_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 5.dp)
            )
        },
        supportingText = {
            ErrorSupportTextSlash(
                isError = isError,
                isWarehouse = true,
                count = formatter(countWarehouse),
                suffix = suffix,
                intRes = R.string.support_text_count_product,
                intResError = R.string.error_no_count_product
            )
        },
        trailingIcon = {
            DropdownMenuIconProductSuffix(setSuffix = { onClick(it) })
        },
        suffix = {
            Text(text = suffix)
        },
        keyboardOptions = keyboardOptionsNextNumber(),
        keyboardActions = keyboardActionsDown(focusManager),
        isError = isError
    )
}

@Composable
fun OutlinedTextTitleAdd(
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
            label = { Text(text = stringResource(R.string.outlined_text_product)) },
            supportingText = {
                ErrorSupportTextSlash(
                    isError = isErrorTitle,
                    isErrorSlash = isErrorSlash,
                    intRes = R.string.support_text_product,
                    intResError = R.string.error_no_product,
                )
            },
            modifier = it,
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
    isError: Boolean,
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
                intRes = R.string.support_text_count_product_sale,
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
    onValueChange: (String) -> Unit,
    onValueChoice: (Triple<Int, String, String>) -> Unit,
    selectedItemIndex: Int,
    titleList: List<PairData>,
    isErrorTitle: Boolean,
    isErrorSlash: Boolean,
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
            modifier = it,
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

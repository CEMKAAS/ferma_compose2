package com.zaroslikov.fermacompose2.ui.elements.TextField

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_9
import com.zaroslikov.fermacompose2.supportFun.KeyboardActionFocus
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.text_16

@Composable
fun BaseOutlinedTextNew(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: Suffix? = null,
    onSuffixChance: ((Suffix) -> Unit)? = null,
    trailingIcon: Int? = null,
    onTrailingChance: (() -> Unit)? = null,
    onClear: () -> Unit = {},
    versionDropMenu: DropdownMenu = DropdownMenu.ALL,
    isError: Boolean = false,
    isErrorSlash: Boolean = false,
    isWarehouseShow: Boolean = false,
    warehouseList: List<DomainCountSuffix> = emptyList(),
    isAnimal: Boolean = false,
    countAnimal: String = "",
    leadingIconRes2: Int? = null,
    leadingIconColor2: Color = Color(0xFF9A9A9A),
    leadingIconRes: Int? = null,
    leadingIconClick: () -> Unit = {},
    readOnly: Boolean = false,
    enable: Boolean = true,
    @StringRes labelIntRes: Int? = null,
    @StringRes intResSup: Int,
    @StringRes intResError: Int = R.string.error_no_count_product,
    isMore: Boolean? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    colorTextField: Color = gray_9,
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
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderWidth = when {
        isFocused -> 2.dp//green_g_2 //green_1
        else -> 1.dp
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (labelIntRes != null || leadingIconRes != null)
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIconRes?.let {
                    Icon(
                        painter = painterResource(it), contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF6A7282)
                    )
                }
                labelIntRes?.let {
                    Text(
                        text = stringResource(it),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = text_16,
                        color = dark
                    )
                }
            }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isError) Color(0xFFFFEAEA) else colorTextField,
                    shape = RoundedCornerShape(14.dp)
                )
                .border(borderWidth, color = Color(0xFFD1D5DC), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // не SpaceBetween — контролируем ширины явно
            ) {
                leadingIconRes2?.let {
                    Icon(
                        painterResource(it),
                        modifier = Modifier
                            .size(20.dp)
                            .wrapContentWidth()/*.padding(end = 5.dp)*/,
                        contentDescription = null,
                        tint = leadingIconColor2
                    )
                    Spacer(modifier = Modifier.padding(3.dp))
                }
                // ТЕКСТОВОЕ ПОЛЕ: занимает всё оставшееся место
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)           // <-- ключ: занимает свободное место
                        .padding(end = if (isMore == true) 8.dp else 0.dp) // отступ перед иконкой
                        .defaultMinSize(minHeight = (minLines * 26).dp)
                        .align(Alignment.CenterVertically),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 26.sp
                    ),
                    cursorBrush = SolidColor(Color(0xFF007AFF)),
                    singleLine = singleLine,
                    readOnly = readOnly,
                    enabled = enable,
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = if (minLines == 1) Alignment.CenterStart else Alignment.TopStart) {
                            if (value.isEmpty()) {
                                Text(
                                    text = stringResource(intResSup),
                                    color = Color(0xFF9A9A9A),
                                    style = TextStyle(fontSize = 16.sp, lineHeight = 26.sp)
                                )
                            }
                            // Обязательно делаем innerTextField заполняющим ширину своего контейнера
                            Box(modifier = Modifier.wrapContentWidth()) {
                                innerTextField()
                            }
                        }
                    },
                    keyboardOptions = keyboardOptions
                )
                // ИКОНКА: фиксированного размера, не будет ужиматься
                when (isMore) {
                    true -> Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .wrapContentWidth(),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF9A9A9A)
                    )

                    false -> Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .wrapContentWidth()
                            .clickable {
                                onClear()
                            },
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color(0xFF9A9A9A)
                    )

                    else -> {}
                }

            }
        }
    }
}

@Composable
fun BaseOutlinedTextNew2(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes intResSup: Int,
    singleLine: Boolean = true,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource,
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActionFocus = KeyboardActionFocus.DOWN
) {
    // ТЕКСТОВОЕ ПОЛЕ: занимает всё оставшееся место
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            lineHeight = 26.sp
        ),
        cursorBrush = SolidColor(Color(0xFF007AFF)),
        singleLine = singleLine,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box(contentAlignment = if (minLines == 1) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(intResSup),
                        color = Color(0xFF9A9A9A),
                        style = TextStyle(fontSize = 16.sp, lineHeight = 26.sp)
                    )
                }
                // Обязательно делаем innerTextField заполняющим ширину своего контейнера
                Box(modifier = Modifier.fillMaxWidth()) {
                    innerTextField()
                }
            }
        }
    )
}


@Composable
fun BaseOutlinedTextNew3(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes intResSup: Int,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    minLines: Int = 1,
    dividerColor: Color,
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    keyboardActions: KeyboardActionFocus = KeyboardActionFocus.DOWN
) {
    // ТЕКСТОВОЕ ПОЛЕ: занимает всё оставшееся место
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 24.sp,
            lineHeight = 26.sp
        ),
        readOnly = readOnly,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Box(contentAlignment = if (minLines == 1) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(intResSup),
                        color = Color(0xFF9A9A9A),
                        style = TextStyle(fontSize = 16.sp, lineHeight = 26.sp)
                    )
                }
                // Обязательно делаем innerTextField заполняющим ширину своего контейнера
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        innerTextField()
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 2.dp,
                            color = dividerColor
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun BaseOutlinedTextNew4(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes intResSup: Int,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    minLines: Int = 1,
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardOptions: KeyboardOptions = keyboardOptionsNextNumber(),
    keyboardActions: KeyboardActionFocus = KeyboardActionFocus.DOWN
) {
    // ТЕКСТОВОЕ ПОЛЕ: занимает всё оставшееся место
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center
        ),
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = minLines,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(intResSup),
                        color = Color(0xFF9A9A9A),
                        style = TextStyle(fontSize = 16.sp, lineHeight = 26.sp)
                    )
                }
                // Обязательно делаем innerTextField заполняющим ширину своего контейнера
                Box(Modifier.fillMaxWidth()) {
                    Column { innerTextField() }
                }
            }
        },
        keyboardOptions = keyboardOptions
    )
}
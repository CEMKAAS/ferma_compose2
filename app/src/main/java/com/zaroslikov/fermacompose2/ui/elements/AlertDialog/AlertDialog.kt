package com.zaroslikov.fermacompose2.ui.elements.AlertDialog

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconGradient
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextSexNew
import com.zaroslikov.fermacompose2.ui.elements.modifierDialogScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.white


@Composable
fun AlertDialogInfo(
    onConfirmation: () -> Unit,
    @StringRes intTitleText: Int,
    @StringRes intText: Int,
) {
    AlertDialog(
        icon = {
            Icon(painterResource(R.drawable.icon_info), contentDescription = "Example Icon")
        },
        title = {
            Text(text = stringResource(intTitleText))
        },
        text = {
            Text(text = stringResource(intText), textAlign = TextAlign.Justify)
        },
        onDismissRequest = onConfirmation,
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(stringResource(R.string.button_text_great))
            }
        }
    )
}

@Composable
fun AlertDialogGroupToSolo(
    sex: Boolean,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onUpdateSex: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    AlertDialogStandard(
        titleRes = R.string.alert_dialog_info_group_to_solo_text,
        iconRes = R.drawable.baseline_pets_24,
        titleBackgroundColor = green_15,
        onDismissRequest = onDismissRequest,
        onClick = {
            focusManager.clearFocus()
            onSave()
            onDismissRequest()
        },
        colors = listOf(Color(0xFF00A63E), Color(0xFF009966)),
        textButtonRes = R.string.button_text_edit
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.alert_dialog_group_to_solo_text),
                style = text_14,
                color = marengo
            )
            OutlinedTextSexNew(
                value = sex,
                onValueChange = {
                    onUpdateSex(it)
                },
                isBorderCard = false
            )
        }
    }
}


// возможно пригодится для запроса на удаление
@Composable
fun AlertDialogArchiveAnimal(
    onConfirmation: () -> Unit,
    onArchiveClick: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.baseline_archive_24),
                contentDescription = "Example Icon"
            )
        },
        title = {
            Text(text = stringResource(R.string.alert_dialog_title_archive_animal))
        },
        text = {
            Column {
                Text(text = stringResource(R.string.alert_dialog_info_archive_animal_text))
            }
        },
        onDismissRequest = onConfirmation,
        dismissButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(stringResource(R.string.button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onArchiveClick()
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.button_archive))
            }
        }
    )
}


@Composable
fun AlertDialogAni(
    icon: Painter,
    title: String,
    titleButton: String,
    onDismissClick: () -> Unit,
    onConfirmationClick: () -> Unit,
    content: @Composable () -> Unit,
    isScroll: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    AlertDialog(
        icon = {
            Icon(
                icon,
                contentDescription = title
            )
        },
        title = {
            Text(
                text = title,
                style = textBold_16
            )
        },
        text = {
            Column(modifier = Modifier.modifierDialogScreen(isScroll)) {
                content()
            }
        },
        onDismissRequest = onDismissClick,
        dismissButton = {
            TextButton(
                onClick = onDismissClick
            ) {
                Text(stringResource(R.string.button_text_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    onConfirmationClick()
                }
            ) {
                Text(titleButton)
            }
        }
    )
}

@Composable
fun AlertDialogBase(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                content()
            }
        }
    }
}

@Composable
fun AlertDialogStandard(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int,
    titleBackgroundColor: Color,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
    colors: List<Color>,
    isShowCancelButton: Boolean = true,
    enabled: Boolean = true,
    isScroll: Boolean = true,
    @StringRes textButtonRes: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialogBase(
        modifier = modifier,
        paddingValues = PaddingValues(),
        onDismissRequest = onDismissRequest
    ) {
        Column {
            Column(
                modifier = Modifier.background(color = titleBackgroundColor),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconGradient(
                            sizeCard = 48.dp,
                            icon = iconRes,
                            colorIcon = white,
                            colors = colors
                        )
                        Text(
                            stringResource(titleRes),
                            style = text_16,
                            color = black_2
                        )
                    }
                    IconButton(
                        onClick = onDismissRequest
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_clear_24),
                            contentDescription = null, tint = marengo
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = grey_2)
            }
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 16.dp)
                    .then(
                        if (isScroll)
                            Modifier.verticalScroll(rememberScrollState())
                        else Modifier
                    )
            ) {
                content()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isShowCancelButton)
                    GradientButton(
                        modifier = Modifier.weight(1f),
                        colors = listOf(gray_6, gray_6),
                        text = stringResource(R.string.button_text_cancel_2),
                        textColor = dark,
                        onClick = onDismissRequest
                    )
                GradientButton(
                    modifier = Modifier.weight(1f),
                    colors = colors,
                    enabled = enabled,
                    isShadow = true,
                    text = stringResource(textButtonRes),
                    onClick = onClick
                )
            }
        }
    }
}
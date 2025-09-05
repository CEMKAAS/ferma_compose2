package com.zaroslikov.fermacompose2.ui.elements.AlertDialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextSex
import com.zaroslikov.fermacompose2.ui.elements.modifierDialogScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16


@Composable
fun AlertDialogInfo(
    onConfirmation: () -> Unit,
    @StringRes intTitleText: Int,
    @StringRes intText: Int,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "Example Icon")
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
    onConfirmation: () -> Unit,
    onUpdateSex: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.baseline_spoke_24),
                contentDescription = "Meat Icon"
            )
        },
        title = {
            Text(
                text = stringResource(R.string.alert_dialog_info_group_to_solo_text),
                style = textBold_16
            )
        },
        text = {
            Column {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = stringResource(R.string.alert_dialog_group_to_solo_text)
                )
                OutlinedTextSex(
                    value = sex,
                    onValueChange = {
                        onUpdateSex(it)
                    }
                )
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
            Row {
                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        onUpdateClick(sex)
                        onConfirmation()
                    }
                ) {
                    Text(stringResource(R.string.button_text_edit))
                }
            }
        }
    )
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
            Column(modifier = Modifier.modifierDialogScreen()) {
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

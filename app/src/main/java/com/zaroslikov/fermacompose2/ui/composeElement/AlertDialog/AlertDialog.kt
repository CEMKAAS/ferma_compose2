package com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.infoTextKillAnimal
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAnimalSale
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CheckboxTextIcon
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextSex
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.composeElement.TextBuildAnnotated
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
import com.zaroslikov.fermacompose2.ui.composeElement.toOutlinedText
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch


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
    onConfirmation: () -> Unit,
    onUpdateClick: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var sex by rememberSaveable { mutableStateOf("Мужской") }
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
                    onValueChange = { sex = it },
                    standardPadding = false,
                    focusManager = focusManager
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

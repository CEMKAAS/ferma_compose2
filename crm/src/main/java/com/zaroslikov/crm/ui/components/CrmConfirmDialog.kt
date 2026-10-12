package com.zaroslikov.crm.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zaroslikov.crm.R
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.error_base
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.marengo
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.textBold_18
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white

/** Подтверждение необратимого действия (удаление); кнопка подтверждения — красная. */
@Composable
fun CrmConfirmDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = white,
        title = { Text(text = title, style = textBold_18, color = black_2) },
        text = { Text(text = text, style = text_14, color = gray_7) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, style = textBold_16, color = error_base)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.crm_cancel), style = textBold_16, color = marengo)
            }
        }
    )
}

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.red_5
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.text_14


@Composable
fun AlertDialogWarningAnimal(
    onDismissRequest: () -> Unit,
    onConfirmationClick: () -> Unit,
    textWarning: String
) {
    AlertDialogStandard(
        titleRes = R.string.animal_count_screen_warning_title,
        iconRes = R.drawable.outline_info_24,
        titleBackgroundColor = red_3,
        onDismissRequest = onDismissRequest,
        onClick = {
            onConfirmationClick()
            onDismissRequest()
        },
        colors = listOf(red_4, red_5),
        textButtonRes = R.string.button_text_take
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = textWarning,
                style = text_14,
                color = marengo
            )
        }
    }
}
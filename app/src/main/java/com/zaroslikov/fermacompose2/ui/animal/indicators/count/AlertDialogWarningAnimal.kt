package com.zaroslikov.fermacompose2.ui.animal.indicators.count


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogAni


@Composable
fun AlertDialogWarningAnimal(
    onDismissClick: () -> Unit,
    onConfirmationClick: () -> Unit,
    textWarning: String
) {
    AlertDialogAni(
        icon = painterResource(R.drawable.icon_warning),
        title = stringResource(R.string.animal_count_screen_warning_title),
        titleButton = stringResource(R.string.button_text_take),
        onDismissClick = onDismissClick,
        isScroll = false,
        content = {
            Text(text = textWarning)
        },
        onConfirmationClick = onConfirmationClick
    )
}
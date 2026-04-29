package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.supportFun.formatterTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(time: String, showDialog: (String) -> Unit) {

    val timeList = time.split(":")
    val timeState = rememberTimePickerState(
        initialHour = timeList[0].toInt(),
        initialMinute = timeList[1].toInt()
    )
    AlertDialogStandard(
        titleRes = R.string.time_picker_title,
        iconRes = R.drawable.baseline_access_time_24,
        titleBackgroundColor = ghostly_white,
        onDismissRequest = { showDialog(time) },
        onClick = { showDialog(formatterTime(timeState.hour, timeState.minute)) },
        colors = listOf(green_6, green_shamrock),
        textButtonRes = R.string.button_text_take
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            TimePicker(
                state = timeState, colors = TimePickerDefaults.colors().copy(
                    clockDialColor = gray_6,
                    containerColor = ghostly_white
                )
            )
        }
    }
}
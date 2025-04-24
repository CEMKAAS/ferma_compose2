package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R


@Composable
fun RadioButtonWriteOff(
    state: Boolean,
    onStateSelect: (Boolean) -> Unit,
    imageOne: Int = R.drawable.baseline_cottage_24,
    imageTwo: Int = R.drawable.baseline_delete_24,
    @StringRes intResOne: Int = R.string.ration_button_own_needs,
    @StringRes intResTwo: Int = R.string.ration_button_disposal,
) {
    Column(
        Modifier
            .selectableGroup()
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = state,
                onClick = { onStateSelect(!state) },
                modifier = Modifier.semantics { contentDescription = "Localized Description" }
            )
            Image(
                painter = painterResource(id = imageOne),
                contentDescription = "delete"
            )
            Text(
                text = stringResource(intResOne),
                style = text_14
            )
        }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !state,
                onClick = { onStateSelect(!state) },
                modifier = Modifier.semantics { contentDescription = "Localized Description" },
            )
            Image(
                painter = painterResource(id = imageTwo),
                contentDescription = "delete"
            )
            Text(
                text = stringResource(intResTwo),
                style = text_14
            )
        }
    }
}

@Composable
fun RadioButtonRow(
    state: Boolean,
    onStateSelect: (Boolean) -> Unit,
    imageOne: Int = R.drawable.baseline_cottage_24,
    imageTwo: Int = R.drawable.baseline_delete_24,
    @StringRes intResOne: Int = R.string.ration_button_own_needs,
    @StringRes intResTwo: Int = R.string.ration_button_disposal,
) {
    Row(
        Modifier
            .selectableGroup()
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !state,
                onClick = { onStateSelect(!state) },
                modifier = Modifier.semantics { contentDescription = "Localized Description" }
            )
            Image(
                painter = painterResource(id = imageOne),
                contentDescription = "delete"
            )
            Text(
                modifier = Modifier.padding(start = 3.dp),
                text = stringResource(intResOne),
                style = text_14
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = state,
                onClick = { onStateSelect(!state) },
                modifier = Modifier.semantics { contentDescription = "Localized Description" },
            )
            Image(
                painter = painterResource(id = imageTwo),
                contentDescription = "delete"
            )
            Text(
                modifier = Modifier.padding(start = 3.dp),
                text = stringResource(intResTwo),
                style = text_14
            )
        }
    }
}
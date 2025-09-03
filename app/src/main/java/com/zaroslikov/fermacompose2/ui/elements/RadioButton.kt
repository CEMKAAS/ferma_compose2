package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
    CardField(
        modifier = Modifier
            .selectableGroup(),
        row = false
    ) {
        RowRadioButton(
            selected = state,
            onStateSelect = { onStateSelect(!state) },
            imageRes = imageOne,
            intRes = intResOne
        )
        RowRadioButton(
            selected = !state,
            onStateSelect = { onStateSelect(state) },
            imageRes = imageTwo,
            intRes = intResTwo
        )
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
        RowRadioButton(
            modifier = Modifier.weight(1f),
            selected = !state,
            onStateSelect = { onStateSelect(!state) },
            imageRes = imageOne,
            intRes = intResOne
        )
        RowRadioButton(
            modifier = Modifier.weight(1f),
            selected = state,
            onStateSelect = { onStateSelect(!state) },
            imageRes = imageTwo,
            intRes = intResTwo
        )
    }
}

@Composable
fun RowRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onStateSelect: () -> Unit,
    imageRes: Int,
    @StringRes intRes: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onStateSelect()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onStateSelect,
            modifier = Modifier.semantics { contentDescription = "Localized Description" },
        )
        Image(
            modifier = Modifier.padding(end = 10.dp),
            painter = painterResource(id = imageRes),
            contentDescription = null
        )
        Text(
            text = stringResource(intRes),
            style = text_14
        )
    }
}

package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.green_g_5
import com.zaroslikov.fermacompose2.grey

@Composable
fun SwitchWitchText(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @StringRes intRes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (checked)
                Icon(
                    painter = painterResource(R.drawable.icon_check),
                    contentDescription = null,
                    tint = green_g_5,
                    modifier = Modifier.size(14.dp)
                )
            Text(
                text = stringResource(intRes),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (checked) green_g_5 else grey
            )
        }
    }
}
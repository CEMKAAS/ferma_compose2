@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.ui.elements.IconText
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew4
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.violet_4
import com.zaroslikov.fermacompose2.white
import kotlinx.coroutines.launch


@Composable
fun Table(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WarningCard(
            colorBackground = orang_4,
            colorBorder = orang_5,
            colorIcon = orang_2,
            colorIconBackground = Color(0xFFFEF3C6),
            colorTitle = Color(0xFF7B3306),
            colorText = orang_6,
            icon = R.drawable.icon_warning,
            title = R.string.entry_bookmark_warning_title,
            text = R.string.entry_bookmark_warning_text
        )
        Card(
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(grey_2)
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconToggle(
                    stringRes = R.string.entry_bookmark_day_tooltip,
                    iconRes = R.drawable.baseline_calendar_month_24,
                    iconColor = dark
                )
                IconToggle(
                    stringRes = R.string.entry_bookmark_temp_tooltip,
                    iconRes = R.drawable.outline_device_thermostat_24,
                    iconColor = red_13
                )
                IconToggle(
                    stringRes = R.string.entry_bookmark_damp_tooltip,
                    iconRes = R.drawable.outline_dew_point_24,
                    iconColor = blue_4
                )
                IconToggle(
                    stringRes = R.string.entry_bookmark_over_tooltip,
                    iconRes = R.drawable.outline_cached_24,
                    iconColor = violet_4
                )
                IconToggle(
                    stringRes = R.string.entry_bookmark_airing_tooltip,
                    iconRes = R.drawable.outline_air_24,
                    iconColor = green_6
                )
            }
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
            IncubatorRow()
        }
    }
}

@Composable
private fun IncubatorRow() {
    HorizontalDivider(thickness = 1.dp, color = gray_6)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            IconText(
                number = "1",
                colorBackground = orang_8,
                colorText = orang_6,
                sizeCard = 40.dp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = "32",
            onValueChange = {},
            intResSup = R.string.is_empty
        )
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = "32",
            onValueChange = {},
            intResSup = R.string.is_empty
        )
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = "323",
            onValueChange = {},
            intResSup = R.string.is_empty
        )
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = "32",
            onValueChange = {},
            intResSup = R.string.is_empty
        )
    }
}


@Composable
private fun IconToggle(
    @StringRes stringRes: Int,
    @DrawableRes iconRes: Int,
    iconColor: Color
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = rememberTooltipPositionProvider(positioning = TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                Text(
                    text = stringResource(stringRes),
                    style = text_14
                )
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = { scope.launch { tooltipState.show() } }) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconColor,
            )
        }
    }
}
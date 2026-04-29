package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.TextField.WeightOutlinedTextFieldNew
import kotlinx.coroutines.launch


@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    size: Dp = 24.dp
) {
    Checkbox(
        modifier = Modifier.size(size),
        checked = checked,
        onCheckedChange = { onCheckedChange(it) },
        enabled = enabled,
        colors = CheckboxDefaults.colors().copy(
            checkedBoxColor = Color(0xFF030213),
            checkedBorderColor = Color(0xFF030213)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckboxTextIcon(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    @StringRes intTitle: Int,
    @StringRes intTooltip: Int = R.string.tooltip_animals_born,
    onClick: (() -> Unit)? = null,
    isTooltipShow: Boolean = false
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .clickable(enabled = enabled) { // клик по всей строке
                onCheckedChange(!checked)
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomCheckbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
            Text(
                text = stringResource(id = intTitle),
            )
            if (onClick != null)
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        painterResource(R.drawable.icon_info),
                        contentDescription = "Показать меню"
                    )
                }
        }

        if (isTooltipShow)
            TooltipBox(
                positionProvider = rememberTooltipPositionProvider(positioning = TooltipAnchorPosition.Above),
                tooltip = {
                    PlainTooltip {
                        Text(
                            text = stringResource(intTooltip),
                            style = text_14
                        )
                    }
                },
                state = tooltipState
            ) {
                IconButton(onClick = { scope.launch { tooltipState.show() } }) {
                    Icon(
                        painterResource(R.drawable.icon_info),
                        contentDescription = "Показать меню"
                    )
                }
            }
    }
}

@Composable
fun AutoCalculateCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @StringRes tooltipTextResAutoCal: Int,
    count: String,
    countSuffix: Suffix,
    price: String,
    priceSuffix: Suffix,
    priceAll: String,
) {
    Column {
        CheckboxTextIcon(
            modifier = if (isChecked) Modifier.toOutlinedText() else Modifier,
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            intTitle = R.string.checkbox_auto_calculate,
            isTooltipShow = true,
            intTooltip = tooltipTextResAutoCal
        )
        if (isChecked)
            SupportTotalCard(
                count = count,
                countSuffix = countSuffix,
                value = price,
                valueSuffix = priceSuffix,
                totalValue = priceAll,
            )
    }
}

@Composable
fun AutoWeightCheckbox(
    count: String,
    countSuffix: Suffix,
    weightAll: String,
    weight: String,
    onWeightChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChance: (Suffix) -> Unit,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    totalSuffix: Suffix? = null,
    suffixEnabled: Boolean = true,
    @StringRes tooltipTextResAutoCal: Int,
) {
    Column {
        CheckboxTextIcon(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            intTitle = R.string.checkbox_auto_weight,
            isTooltipShow = true,
            intTooltip = tooltipTextResAutoCal
        )
        if (isChecked) {
            WeightOutlinedTextFieldNew(
                value = weight,
                onValueChange = onWeightChange,
                suffix = suffix,
                onSuffixChange = onSuffixChance,
                suffixEnabled = suffixEnabled
            )
            Spacer(Modifier.padding(vertical = 4.dp))
            SupportTotalCard(
                titleRes = R.string.support_text_all_weight,
                count = count,
                countSuffix = countSuffix,
                value = weight,
                valueSuffix = suffix,
                totalValue = weightAll,
                totalSuffix = totalSuffix
            )
        }
    }
}
package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch


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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
            },
            enabled = enabled
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Text(
                modifier = Modifier.weight(0.8f),
                text = stringResource(intTitle),
                style = text_16
            )

            if (onClick != null)
                IconButton(
                    modifier = Modifier.weight(0.2f),
                    onClick = onClick
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Показать меню"
                    )
                }
            if (isTooltipShow)
                TooltipBox(
                    modifier = Modifier.weight(0.2f),
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip {
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
                            Icons.Default.Info,
                            contentDescription = "Показать меню"
                        )
                    }
                }
        }

    }
}

@Composable
fun autoCalculate(
    isAutoCalculate: MutableState<Boolean>,
    price: String,
    count: String
): String {

    val isAutoCalculatePadding by animateDpAsState(
        if (isAutoCalculate.value) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val amount = (price.toConvertZeroDouble() * count.toConvertZeroDouble()).formatNumber()
    CardField(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .padding(bottom = isAutoCalculatePadding.coerceAtLeast(0.dp)),
        row = false
    ) {
        CheckboxTextIcon(
            modifier = if (isAutoCalculate.value) Modifier.toOutlinedText() else Modifier,
            checked = isAutoCalculate.value,
            onCheckedChange = {
                isAutoCalculate.value = it
            },
            intTitle = R.string.checkbox_auto_calculate,
            isTooltipShow = true,
            intTooltip = R.string.tooltip_auto_calculate_price
        )
        if (isAutoCalculate.value)
            TextBuildAnnotated(
                intRes = R.string.support_text_all_price,
                priceAll = price,
                count = count
            )
    }
    return amount
}

@Composable
fun AutoCalculateCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @StringRes tooltipTextResAutoCal: Int,
    price: String,
    count: String
) {
    CheckboxTextIcon(
        modifier = if (isChecked) Modifier.toOutlinedText() else Modifier,
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        intTitle = R.string.checkbox_auto_calculate,
        isTooltipShow = true,
        intTooltip = tooltipTextResAutoCal
    )
    if (isChecked) {
        TextBuildAnnotated(
            intRes = R.string.support_text_all_price,
            priceAll = price,
            count = count
        )
    }
}

@Composable
fun AutoWeightCheckbox(
    count: String,
    weight: String,
    onWeightChange: (String) -> Unit,
    suffix: String,
    onSuffixChance: (String) -> Unit,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @StringRes tooltipTextResAutoCal: Int,
) {
    CheckboxTextIcon(
        modifier = if (isChecked) Modifier.toOutlinedText() else Modifier,
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        intTitle = R.string.checkbox_auto_weight,
        isTooltipShow = true,
        intTooltip = tooltipTextResAutoCal
    )
    if (isChecked) {
        WeightOutlinedText(
            value = weight,
            onValueChange = onWeightChange,
            suffix = suffix,
            onSuffixChance = onSuffixChance
        )
        TextBuildAnnotated(
            intRes = R.string.support_text_all_weight,
            priceAll = weight,
            count = count,
            suffix = suffix
        )
    }
}
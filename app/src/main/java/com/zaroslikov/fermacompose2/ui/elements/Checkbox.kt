package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
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
            Checkbox(
                modifier = Modifier.size(24.dp),
                checked = checked,
                onCheckedChange = { onCheckedChange(it) },
                enabled = enabled,
                colors = CheckboxDefaults.colors().copy(
                    checkedBoxColor = Color(0xFF030213),
                    checkedBorderColor = Color(0xFF030213)
                )
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
                modifier = Modifier.weight(0.2f, fill = false),
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
                        painterResource(R.drawable.icon_info),
                        contentDescription = "Показать меню"
                    )
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
            CardAllPrice(
                count = count,
                countSuffix = countSuffix,
                price = price,
                priceSuffix = priceSuffix,
                priceAll = priceAll,
            )
    }
}


@Composable
fun AutoWeightCheckbox(
    count: String,
    weight: String,
    onWeightChange: (String) -> Unit,
    suffix: Suffix,
    onSuffixChance: (Suffix) -> Unit,
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
            modifier = Modifier.padding(bottom = 8.dp),
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
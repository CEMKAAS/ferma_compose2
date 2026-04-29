package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CustomCheckbox
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.supportFun.formatNumber

@Composable
fun AlertDialogWeightAnimal(
    onDismissRequest: () -> Unit,
    weight: Double,
    countAnimal: Int,
    onClick: (Double) -> Unit,
    colors: List<Color>,
    weightSuffix: Suffix,
) {
    var checked by rememberSaveable { mutableStateOf(true) }
    var totalWeight by rememberSaveable(weight) { mutableStateOf(weight.formatNumber()) }
    val weights = rememberSaveable {
        mutableStateListOf<String>().apply {
            repeat(countAnimal) { add("") }
        }
    }
    // считаем сумму
    val total = weights.sumOf { it.toDoubleOrNull() ?: 0.0 }
    AlertDialogStandard(
        titleRes = R.string.animal_count_screen_weight_animals,
        iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
        titleBackgroundColor = red_3,
        onDismissRequest = onDismissRequest,
        onClick = {
            val result = if (checked) totalWeight.toConvertZeroDouble() else total
            onClick(result)
        },
        isShowCancelButton = false,
        enabled = if (checked) totalWeight.isNotEmpty() else weights.all { it.isNotBlank() },
        colors = colors,
        textButtonRes = R.string.button_text_take,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WarningWeightCard()
            IndividualCheckedBox(checked = checked) { checked = it }
            if (checked)
                OutlinedNumberNew(
                    value = totalWeight,
                    onValueChange = { totalWeight = it },
                    intRes = R.string.weight_screen_title,
                    intResError = R.string.error_no_weight_animal,
                    isError = totalWeight.isEmpty(),
                    suffix = weightSuffix,
                    isBorderCard = false
                )
            else WeightAnimalList(weights = weights, weightSuffix)
            FinalWeightCard(checked = checked, totalWeight = totalWeight, total = total)
        }
    }
}

@Composable
private fun IndividualCheckedBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            stringResource(R.string.animal_count_screen_weight_enter),
            style = text_14,
            color = marengo
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomCheckbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                size = 16.dp
            )
            Text(
                text = stringResource(R.string.animal_count_screen_weight_individual),
                style = text_14,
                color = marengo,
            )
        }
    }
}

@Composable
private fun WeightAnimalList(weights: SnapshotStateList<String>, weightSuffix: Suffix) {
    Text(
        stringResource(R.string.animal_count_screen_weight_every_animal),
        style = text_14,
        color = marengo
    )
    weights.forEachIndexed { index, weight ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("${index + 1}.", style = text_14, color = marengo)
            OutlinedNumberNew(
                value = weight,
                onValueChange = { weights[index] = it },
                intRes = null,
                intResError = R.string.error_no_weight_animal,
                isError = weight.isEmpty(),
                suffix = weightSuffix,
                isBorderCard = false
            )
        }
    }
}

@Composable
private fun FinalWeightCard(checked: Boolean, totalWeight: String, total: Double) {
    if (!checked)
        BorderCard(
            containerColor = green_g_1,
            borderColor = green_1,
            padding = PaddingValues(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.card_all), style = text_14, color = dark)
                Text(
                    if (checked) totalWeight else total.formatNumber(false) +
                            " ${stringResource(Suffix.KILOGRAM.toResId())}",
                    style = text_16,
                    color = green_2
                )
            }
        }
}

@Composable
private fun WarningWeightCard() {
    BorderCard(
        containerColor = red_11,
        borderColor = red_15,
        padding = PaddingValues(13.dp)
    ) {
        Text(
            stringResource(R.string.animal_count_screen_warning_text_weight),
            style = text_12,
            color = red_14
        )
    }
}

package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.red_9
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconIndicatorsAnimal
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.white

@Composable
fun AlertDialogWeightAnimal(
    onDismissRequest: () -> Unit,
    isEntry: Boolean = true,
    colors: List<Color>,
) {
    var checked by rememberSaveable { mutableStateOf(false) }
    var weight by rememberSaveable { mutableStateOf("0") }
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = white
            ),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Column(
                        modifier = Modifier
                            .background(red_9)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconIndicatorsAnimal(
                                    icon = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                                    colors = colors
                                )
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        stringResource(R.string.button_text_edit),
                                        style = if (isEntry) text_20 else text_16,
                                        color = black_1
                                    )
                                    if (!isEntry)
                                        Text(
                                            stringResource(R.string.animal_indicators_mode_edit),
                                            style = text_12,
                                            color = gray_7
                                        )
                                }
                            }
                            IconButton(
                                onClick = onDismissRequest,
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = marengo
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = gray_6
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
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
                        Checkbox(
                            modifier = Modifier.size(16.dp),
                            checked = checked,
                            onCheckedChange = { checked = !checked },
                            colors = CheckboxDefaults.colors().copy(
                                checkedBoxColor = Color(0xFF030213),
                                checkedBorderColor = Color(0xFF030213)
                            )
                        )
                        Text(
                            text = stringResource(R.string.animal_count_screen_weight_individual),
                            style = text_14,
                            color = marengo,
                        )
                    }
                }
                OutlinedTextCountAnimalNew(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 24.dp),
                    value = weight,
                    onValueChange = {
                        weight = it
                    },
                    isError = weight.isEmpty(),
                    /*isErrorCountZero = state.error.isErrorCountZero,*/
                    suffix = Suffix.KILOGRAM,
                    countAnimal = "2",
                    isBorderCard = false
                )
                GradientButton(
                    text = stringResource(R.string.button_text_take),
                    onClick = { },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 24.dp),
                    colors = colors
                )
            }
        }
    }
}
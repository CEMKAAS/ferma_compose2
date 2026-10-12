@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.group

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import kotlinx.coroutines.launch

/**
 * Пол в обезличенной группе: сколько самок и сколько самцов — карточка с рамкой, как другие
 * блоки форм. Пояснение — по иконке «i» рядом с подписью поля ([infoRes]). Необязательно: остальное
 * поголовье считается «пол не известен». [isError] — сумма или значения превышают допустимое.
 */
@Composable
fun GroupSexCountFields(
    females: String,
    males: String,
    onFemalesChange: (String) -> Unit,
    onMalesChange: (String) -> Unit,
    isError: Boolean,
    @StringRes infoRes: Int = R.string.animal_group_sex_info,
    @StringRes errorRes: Int = R.string.animal_group_sex_error
) {
    BorderCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SexCountField(
                modifier = Modifier.weight(1f),
                labelRes = R.string.animal_group_females,
                iconRes = R.drawable.baseline_female_24,
                infoRes = infoRes,
                value = females,
                onValueChange = onFemalesChange
            )
            SexCountField(
                modifier = Modifier.weight(1f),
                labelRes = R.string.animal_group_males,
                iconRes = R.drawable.baseline_male_24,
                infoRes = infoRes,
                value = males,
                onValueChange = onMalesChange
            )
        }
        // Одна ошибка на оба поля — под ними
        if (isError)
            Text(stringResource(errorRes), style = text_12, color = error_base)
    }
}

/** Поле с подписью сверху: «Самок» / «Самцов» и «i» — по нажатию пояснение. */
@Composable
private fun SexCountField(
    modifier: Modifier,
    @StringRes labelRes: Int,
    @DrawableRes iconRes: Int,
    @StringRes infoRes: Int,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SexCountLabel(labelRes, infoRes)
        BaseOutlinedTextNew(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            leadingIconRes = iconRes,
            intResSup = R.string.is_empty,
            keyboardOptions = SEX_COUNT_KEYBOARD
        )
    }
}

@Composable
private fun SexCountLabel(@StringRes labelRes: Int, @StringRes infoRes: Int) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(labelRes),
            style = text_16,
            color = dark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = { RichTooltip { Text(text = stringResource(infoRes), style = text_14) } },
            state = tooltipState
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_info),
                contentDescription = stringResource(R.string.animal_group_sex_info_description),
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .clickable { scope.launch { tooltipState.show() } },
                tint = gray_7
            )
        }
    }
}

private val SEX_COUNT_KEYBOARD = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)

/** Самок и самцов вместе больше, чем голов в группе [total]. */
fun isSexSplitExceeding(total: Double, females: String, males: String): Boolean =
    (females.toIntOrNull() ?: 0) + (males.toIntOrNull() ?: 0) > total

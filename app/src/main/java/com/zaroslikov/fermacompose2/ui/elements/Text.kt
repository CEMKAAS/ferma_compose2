package com.zaroslikov.fermacompose2.ui.elements

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.theme.errorLight
import com.zaroslikov.fermacompose2.ui.theme.tertiaryLight
import kotlinx.coroutines.launch


@Composable
fun TitleAndText(
    modifier: Modifier = Modifier,
    @StringRes intString: Int,
    valueString: String
) {
    Row(
        modifier = modifier
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = modifier.padding(end = 3.dp),
            text = stringResource(intString),
        )
        Text(
            text = valueString,
        )
    }

}

@Composable
fun IconAndText(
    modifier: Modifier = Modifier,
    iconRes: Int,
    valueString: String,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    textStyle: TextStyle = text_14
) {
    Row(
        modifier = modifier
//            .fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            painter = painterResource(iconRes), contentDescription = null,
            modifier = Modifier.padding(end = 5.dp)
        )
        Text(
//            modifier = Modifier.fillMaxWidth(),
            text = valueString,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = textStyle
        )
    }
}

@Composable
fun IconAndTextNew(
    modifier: Modifier = Modifier,
    iconRes: Int,
    valueString: String,
    color: Color,
    textStyle: TextStyle = text_14
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes), contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        Text(
//            modifier = Modifier.fillMaxWidth(),
            text = valueString,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
            color = marengo
        )
    }
}


@Composable
fun IconAndTextMore(
    modifier: Modifier = Modifier,
    iconRes: Int,
    valueString: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconAndText(
            iconRes = iconRes,
            valueString = valueString
        )
        IconButton(
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_read_more_24),
                contentDescription = null,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAndIconRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    title: String = "",
    titleStyle: TextStyle = text_16,
    @StringRes intTooltip: Int = R.string.is_empty,
    @StringRes intRes: Int? = null,
    value: String = "",
    color: Color = Color.Unspecified,
    iconRes: Int = R.drawable.icon_info,
    isShowIcon: Boolean = false,
    isShowValue: Boolean = true,
    onClick: () -> Unit = {},
    iconResEnd: Int? = null,
    onClickIconEnd: () -> Unit = {},
    isTooltipShow: Boolean = false
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.wrapContentWidth(Alignment.Start),
                text = if (intRes != null) stringResource(intRes) else title,
                style = titleStyle,
            )
            if (isShowIcon) {
                Icon(
                    painterResource(iconRes), contentDescription = null,
                    modifier =
                        Modifier
                            .size(18.dp)
                            .padding(start = 3.dp)
                            .clickable { onClick() }
                )
            }
            if (isTooltipShow)
                TooltipBox(
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 3.dp),
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
                            painterResource(iconRes), contentDescription = null
                        )
                    }
                }
        }
        if (isShowValue) {
            Box(
                modifier = Modifier.weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    style = textBold_16,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
        if (iconResEnd != null) {
            Icon(
                painterResource(iconResEnd), contentDescription = null,
                modifier =
                    Modifier
                        .size(18.dp)
                        .clickable { onClickIconEnd() }
            )
        }

    }
}

@Composable
fun TextAndIcon(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String = "",
    @StringRes intRes: Int? = null
) {
    Text(
        text = if (intRes != null) stringResource(intRes) else title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = textBold_16
    )
    Icon(
        painterResource(iconRes), contentDescription = null,
        modifier = Modifier.padding(end = 5.dp)
    )
}


@Composable
fun TextLine(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = textBold_20,
    valueString: String
) {
    Text(
        modifier = modifier,
        text = valueString,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start
    )
}

@Composable
fun TextLineProductKill(
    number: Int,
    title: String,
    value: String,
    suffix: Suffix
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text("$number. $title", style = text_14, color = marengo)
        Text("$value ${stringResource(suffix.toResId())}", style = text_14, color = black_1)
    }
}

@Composable
fun TextColumn(
    @StringRes titleRes: Int,
    value: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(titleRes),
            style = textBold_16
        )
        Text(
            text = stringResource(R.string.card_ruble_s, value.formatNumber()),
            style = textBold_16
        )
    }
}

@Composable
fun TextColumn(
    titleRes: String,
    value: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titleRes,
            textAlign = TextAlign.Center,
            style = textBold_16
        )
        Text(
            text = stringResource(R.string.card_ruble_s, value.formatNumber()),
            textAlign = TextAlign.Center,
            style = textBold_16
        )
    }
}

@Composable
fun TextBuildAnnotated(
    priceAll: String,
    count: String,
    @StringRes intRes: Int,
    suffix: Suffix = Suffix.RUBLE
) {
    val amount = (priceAll.toConvertZeroDouble() * count.toConvertZero()).formatNumber()
    Text(
        text = buildAnnotatedString {
            val suffixText = stringResource(suffix.toResId())
            val fullText =
                stringResource(intRes).format(amount, suffixText)

            val startIndex = fullText.indexOf(amount)
            val endIndex = startIndex + amount.length

            append(fullText)
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.Bold),
                start = startIndex,
                end = endIndex
            )
        }
    )
}

@Composable
fun CardAllPrice(
    count: String,
    countSuffix: Suffix,
    price: String,
    priceSuffix: Suffix,
    priceAll: String,
) {
    /* val gradient = Brush.linearGradient(
         colors = listOf(green_g_1, green_g_2),
         start = Offset(0f, 0f),
         end = Offset(Float.POSITIVE_INFINITY, 0f)
     )
 */
    val countText = count.ifBlank { "-" }
    val priceText = price.ifBlank { "-" }

    GreenCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.support_text_all_price),
                    style = text_14.copy(color = Color(0xFF364153))
                )
                Text(
                    text = "$priceAll ${stringResource(priceSuffix.toResId())}",
                    style = text_16.copy(color = Color(0xFF007A55))
                )
            }
            Text(
                text = "$countText ${stringResource(countSuffix.toResId())} " +
                        "× $priceText ${stringResource(priceSuffix.toResId())}",
                style = text_12.copy(color = Color(0xFF6A7282))
            )
        }
    }
}

@Composable
fun textBuildIndicatorsAnnotated(
    @StringRes intRes: Int,
    totalValue: String,
    suffix: String,
    price: Double? = null,
    buyer: String? = null,
    isPlus: Boolean,
    note: String
): AnnotatedString {
    return buildAnnotatedString {
        Log.i("Count", "lastValue: $price")
        val fullText = when {
            price != null && buyer != null -> stringResource(intRes).format(
                totalValue,
                suffix,
                price.formatNumber(),
                buyer
            )

            price != null -> stringResource(intRes).format(totalValue, suffix, price.formatNumber())
            else -> stringResource(intRes).format(totalValue, suffix)
        } + note

        val startIndex = fullText.indexOf(totalValue)
        val endIndex = startIndex + totalValue.length

        append(fullText)
        addStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = if (isPlus) tertiaryLight else errorLight
            ),
            start = startIndex,
            end = endIndex
        )
    }
}


fun textBuildIndicatorsAnnotated2(
    context: Context,
    @StringRes intRes: Int,
    totalValue: String,
    suffix: Suffix,
    isPlus: Boolean,
    note: String
): AnnotatedString {
    return buildAnnotatedString {
        val suffixText = context.getString(suffix.toResId())
        val fullText = context.getString(intRes).format(totalValue, suffixText) + note

        val startIndex = fullText.indexOf(totalValue)
        val endIndex = startIndex + totalValue.length

        append(fullText)
        addStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = if (isPlus) tertiaryLight else errorLight
            ),
            start = startIndex,
            end = endIndex
        )
    }
}

fun textBuildIndicatorsAnnotated3(
    context: Context,
    @StringRes intRes: Int,
    totalValue: String,
    suffix: Suffix,
    price: Double? = null,
    buyer: String? = null,
    isPlus: Boolean,
    note: String
): AnnotatedString {
    return buildAnnotatedString {
        val suffixText = context.getString(suffix.toResId())
        val fullText = when {
            price != null && buyer != null -> context.getString(intRes).format(
                totalValue,
                suffixText,
                price.formatNumber(),
                buyer
            )

            price != null -> context.getString(intRes)
                .format(totalValue, suffixText, price.formatNumber())

            else -> context.getString(intRes).format(totalValue, suffixText)
        } + note

        val startIndex = fullText.indexOf(totalValue)
        val endIndex = startIndex + totalValue.length

        append(fullText)
        addStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = if (isPlus) tertiaryLight else errorLight
            ),
            start = startIndex,
            end = endIndex
        )
    }
}
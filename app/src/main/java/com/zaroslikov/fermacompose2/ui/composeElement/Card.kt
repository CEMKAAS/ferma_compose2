package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.start.formatNumber


@Composable
fun CardField(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment
        ) {
            content()
        }
    }
}

@Composable
fun CardOutlined(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier.padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(3.dp, CardDefaults.cardColors().containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun CardFinance(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes titleRes: Int,
    value: Double
) {
    CardField(
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinance(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    titleRes: String,
    value: Double
) {
    CardField(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceOutlined(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    value: Double
) {
    CardOutlined(
        modifier = modifier
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceOutlinedRow(
    modifier: Modifier = Modifier,
    @StringRes oneIntRes: Int,
    @StringRes twoIntRes: Int,
    oneValue: Double,
    twoValue: Double,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CardFinanceOutlined(
            modifier = modifier.weight(1f),
            titleRes = oneIntRes,
            value = oneValue,
        )
        CardFinanceOutlined(
            modifier = modifier.weight(1f),
            titleRes = twoIntRes,
            value = twoValue,
        )
    }
}

@Composable
fun CardFinanceRow(
    modifier: Modifier = Modifier,
    title: String,
    value: Double
) {
    CardField(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth(0.7f),
            style = textBold_16
        )
        Text(
            text = stringResource(R.string.card_ruble_s, value.formatNumber()),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f),
            style = textBold_16
        )
    }
}

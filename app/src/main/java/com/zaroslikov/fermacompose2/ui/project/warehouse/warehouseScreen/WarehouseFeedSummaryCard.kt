package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.theme.FermaCompose2Theme

private object FeedSummaryTokens {
    val gradient = Brush.linearGradient(listOf(Color(0xFFA64B00), Color(0xFF933006)))
    val onGradient = Color.White
    val forecastContainer = Color(0xFF6B350B)
    val onForecast = Color.White
    val cardRadius = 20.dp
    val forecastRadius = 12.dp
    val cardPadding = 16.dp
    val forecastPadding = 12.dp
    val sectionGap = 16.dp
    val metricGap = 12.dp
    val headerGap = 8.dp
    val labelGap = 4.dp
    val iconSize = 24.dp
    val elevation = 0.dp
    val horizontalMinWidth = 260.dp
}

@Composable
internal fun WarehouseFeedSummaryCard(summary: WarehouseFeedSummary) {
    val stock = buildList {
        if (summary.stockKilograms > 0.0 || summary.stockWithoutMass.isEmpty()) {
            add(feedMassLabel(summary.stockKilograms))
        }
        for ((suffix, quantity) in summary.stockWithoutMass) {
            add("${quantity.formatNumber()} ${stringResource(suffix.toResId())}")
        }
    }.joinToString("\n")
    val consumption = if (summary.dailyKilograms > 0.0) feedMassLabel(summary.dailyKilograms, perDay = true)
    else stringResource(R.string.warehouse_feed_consumption_not_set)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(FeedSummaryTokens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = FeedSummaryTokens.onGradient),
        elevation = CardDefaults.cardElevation(defaultElevation = FeedSummaryTokens.elevation),
    ) {
        Column(
            Modifier.fillMaxWidth().background(FeedSummaryTokens.gradient).padding(FeedSummaryTokens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(FeedSummaryTokens.sectionGap),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(FeedSummaryTokens.headerGap), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.wheat_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    modifier = Modifier.size(FeedSummaryTokens.iconSize),
                    tint = FeedSummaryTokens.onGradient,
                )
                Text(
                    stringResource(R.string.warehouse_cards_feed),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FeedSummaryTokens.onGradient,
                )
            }
            if (summary.forecastDays != null) {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
                    if (maxWidth >= FeedSummaryTokens.horizontalMinWidth * fontScale) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(FeedSummaryTokens.sectionGap),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FeedForecast(summary.forecastDays, Modifier.weight(1f))
                            FeedSupportingMetrics(stock, consumption, Modifier.weight(1f))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(FeedSummaryTokens.sectionGap)) {
                            FeedForecast(summary.forecastDays, Modifier.fillMaxWidth())
                            FeedSupportingMetrics(stock, consumption)
                        }
                    }
                }
            } else {
                FeedMetric(stringResource(R.string.warehouse_feed_stock), stock, prominent = true)
                FeedMetric(stringResource(R.string.warehouse_feed_daily_consumption), consumption)
                Text(
                    stringResource(R.string.warehouse_feed_forecast_unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = FeedSummaryTokens.onGradient,
                )
            }
            if (summary.partialForecast) {
                Text(
                    stringResource(R.string.warehouse_feed_partial_forecast),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = FeedSummaryTokens.onGradient,
                )
            }
        }
    }
}

@Composable
private fun FeedForecast(days: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(FeedSummaryTokens.forecastRadius),
        color = FeedSummaryTokens.forecastContainer,
        contentColor = FeedSummaryTokens.onForecast,
        tonalElevation = FeedSummaryTokens.elevation,
        shadowElevation = FeedSummaryTokens.elevation,
    ) {
        Column(Modifier.padding(FeedSummaryTokens.forecastPadding), verticalArrangement = Arrangement.spacedBy(FeedSummaryTokens.labelGap)) {
            Text(
                feedForecastLabel(days, R.string.warehouse_feed_forecast_unavailable),
                style = MaterialTheme.typography.headlineMedium.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.warehouse_feed_forecast),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun FeedSupportingMetrics(stock: String, consumption: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FeedSummaryTokens.metricGap)) {
        FeedMetric(stringResource(R.string.warehouse_feed_stock), stock)
        FeedMetric(stringResource(R.string.warehouse_feed_daily_consumption), consumption)
    }
}

@Composable
private fun FeedMetric(label: String, value: String, prominent: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(FeedSummaryTokens.labelGap)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = FeedSummaryTokens.onGradient,
        )
        Text(
            value,
            style = (if (prominent) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium)
                .copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.SemiBold,
            color = FeedSummaryTokens.onGradient,
        )
    }
}

@Composable
internal fun feedForecastLabel(days: Int?, @StringRes unavailable: Int): String = when (days) {
    null -> stringResource(unavailable)
    0 -> stringResource(R.string.warehouse_feed_less_than_a_day)
    else -> pluralStringResource(R.plurals.warehouse_feed_days, days, days.formatNumber())
}

@Composable
private fun feedMassLabel(kilograms: Double, perDay: Boolean = false): String {
    val inGrams = kilograms > 0.0 && kilograms < 1.0
    val value = if (inGrams) kilograms * 1_000.0 else kilograms
    val suffix = when {
        perDay && inGrams -> Suffix.GRAM_DAY
        perDay -> Suffix.KILOGRAM_DAY
        inGrams -> Suffix.GRAM
        else -> Suffix.KILOGRAM
    }
    return "${value.formatNumber()} ${stringResource(suffix.toResId())}"
}

@Preview(name = "Запас по времени, окно 360dp", widthDp = 328, locale = "ru")
@Composable
private fun FeedSummaryPreview() {
    FermaCompose2Theme {
        WarehouseFeedSummaryCard(WarehouseFeedSummary(stockKilograms = 248.0, dailyKilograms = 20.5, forecastDays = 12))
    }
}

@Preview(name = "Частичный прогноз и разные единицы", widthDp = 360, locale = "ru")
@Composable
private fun FeedSummaryPartialPreview() {
    FermaCompose2Theme {
        WarehouseFeedSummaryCard(
            WarehouseFeedSummary(
                stockKilograms = 248.0,
                stockWithoutMass = mapOf(Suffix.PIECES to 3.0, Suffix.LITERS to 8.0),
                dailyKilograms = 20.5,
                forecastDays = 12,
                partialForecast = true,
            )
        )
    }
}

@Preview(name = "Без прогноза", widthDp = 360, locale = "ru")
@Composable
private fun FeedSummaryUnavailablePreview() {
    FermaCompose2Theme { WarehouseFeedSummaryCard(WarehouseFeedSummary(stockKilograms = 0.75)) }
}

@Preview(name = "Узкая карточка, шрифт 200%", widthDp = 280, fontScale = 2f, locale = "ru")
@Composable
private fun FeedSummaryLargeTextPreview() {
    FermaCompose2Theme {
        WarehouseFeedSummaryCard(
            WarehouseFeedSummary(stockKilograms = 0.75, dailyKilograms = 0.5, forecastDays = 0, partialForecast = true)
        )
    }
}

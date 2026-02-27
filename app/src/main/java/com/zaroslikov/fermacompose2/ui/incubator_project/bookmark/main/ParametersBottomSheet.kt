package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.green_g_5
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.orang_16
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.IconText
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_10
import com.zaroslikov.fermacompose2.violet_11
import com.zaroslikov.fermacompose2.violet_12
import com.zaroslikov.fermacompose2.violet_3


@Composable
fun ParametersBottomSheet(
    currentDay: Int,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    parameterDayList: List<ParametersIncubatorUi>,
    onIntent: (BookmarkIntent) -> Unit
) {
    val scrollState = rememberLazyListState()
    LaunchedEffect(currentDay) {
        scrollState.scrollToItem(currentDay - 1)
    }
    BaseBottomSheet(
        isScroll = false,
        title = stringResource(R.string.bookmark_screen_incubation_schedule_s).format(currentDay),
        onDismissRequest = { onIntent(BookmarkIntent.OpenBottomSheetClick(false)) }
    ) {
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(parameterDayList) { parameterDay ->
                ParametersCard(
                    isCurrentDay = parameterDay.day == currentDay,
                    isAutoAiring = isAutoAiring,
                    isAutoOver = isAutoOver,
                    domain = parameterDay
                ) {
                    onIntent(BookmarkIntent.OpenEditBottomSheetClick(true, parameterDay))
                }
            }
        }
    }
}

@Composable
fun EditParametersBottomSheet(
    state: ParametersIncubatorUi,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    onIntent: (BookmarkIntent) -> Unit
) {
    BaseBottomSheet(
        isScroll = false,
        title = stringResource(R.string.bookmark_screen_fact_parameters_s).format(state.day),
        onDismissRequest = {
            onIntent(
                BookmarkIntent.OpenEditBottomSheetClick(false, ParametersIncubatorUi())
            )
        },
        contentBottom = {
            BottomPanel(
                enabled = true,
                stringRes = R.string.button_save,
                onCloseClick = {
                    onIntent(
                        BookmarkIntent.OpenEditBottomSheetClick(false, ParametersIncubatorUi())
                    )
                },
                onSaveClick = { onIntent(BookmarkIntent.SaveParameterClick) }
            )
        }
    ) {
        BorderCard(
            padding = PaddingValues(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    stringResource(R.string.bookmark_screen_plan_parameters),
                    style = text_14,
                    color = black_2
                )
                ParameterRow(state, isAutoOver = isAutoOver, isAutoAiring = isAutoAiring)
            }
        }
        FactParameresCard(
            factTemp = state.tempFact,
            factDamp = state.dampFact,
            factAiring = state.airingFact,
            factOver = state.overFact,
            isAutoAiring = isAutoAiring,
            isAutoOver = isAutoOver,
            onTempFactChange = { onIntent(BookmarkIntent.TempFactChanged(it)) },
            onDampFactChange = { onIntent(BookmarkIntent.DampFactChanged(it)) },
            onOverFactChange = { onIntent(BookmarkIntent.OverFactChanged(it)) },
            onAiringFactChange = { onIntent(BookmarkIntent.AiringFactChanged(it)) }
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = { onIntent(BookmarkIntent.NoteFactChanged(it)) },
        )
    }
}

@Composable
private fun FactParameresCard(
    factTemp: String,
    factDamp: String,
    factAiring: String,
    factOver: String,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    onTempFactChange: (String) -> Unit,
    onDampFactChange: (String) -> Unit,
    onOverFactChange: (String) -> Unit,
    onAiringFactChange: (String) -> Unit,
) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.bookmark_screen_input_fact_parameters),
                style = text_14,
                color = black_2
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedNumberNew(
                    modifier = Modifier.weight(1f),
                    value = factTemp,
                    onValueChange = onTempFactChange,
                    drawableRes2 = R.drawable.outline_device_thermostat_24,
                    drawableColor2 = error_base,
                    intRes = R.string.entry_bookmark_temp_tooltip,
                    intResSup = R.string.is_empty,
                    isBorderCard = false,
                    maxLength = 6
                )
                OutlinedNumberNew(
                    modifier = Modifier.weight(1f),
                    value = factDamp,
                    onValueChange = onDampFactChange,
                    drawableRes2 = R.drawable.outline_dew_point_24,
                    drawableColor2 = blue_1,
                    intRes = R.string.entry_bookmark_damp_tooltip,
                    intResSup = R.string.is_empty,
                    isBorderCard = false,
                    maxLength = 6
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isAutoOver)
                    OutlinedNumberNew(
                        modifier = Modifier.weight(1f),
                        value = factOver,
                        onValueChange = onOverFactChange,
                        drawableRes2 = R.drawable.outline_cached_24,
                        drawableColor2 = violet_1,
                        intRes = R.string.entry_bookmark_temp_tooltip,
                        intResSup = R.string.is_empty,
                        isBorderCard = false,
                        maxLength = 6
                    )
                if (!isAutoAiring)
                    OutlinedNumberNew(
                        modifier = Modifier.weight(1f),
                        value = factAiring,
                        onValueChange = onAiringFactChange,
                        drawableRes2 = R.drawable.outline_air_24,
                        drawableColor2 = green_g_5,
                        intRes = R.string.entry_bookmark_airing_tooltip,
                        intResSup = R.string.is_empty,
                        isBorderCard = false,
                        maxLength = 6
                    )
            }
        }
    }
}

/*@Composable
private fun RowParameter(
    valueOne: String,
    valueTwo: String,
    @StringRes intResOne: Int,
    @StringRes intResTwo: Int,
    @DrawableRes iconResOne: Int,
    @DrawableRes iconResTwo: Int,
    iconColorOne: Color,
    iconColorTwo: Color,
    onValueOneChange: (String) -> Unit,
    onValueTwoChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isAutoOver)
            OutlinedNumberNew(
                modifier = Modifier.weight(1f),
                value = valueOne,
                onValueChange = onValueOneChange,
                drawableRes2 = iconResOne,
                drawableColor2 = iconColorOne,
                intRes = intResOne,
                intResSup = R.string.is_empty,
                isBorderCard = false
            )
        if (!isAutoAiring)
            OutlinedNumberNew(
                modifier = Modifier.weight(1f),
                value = valueTwo,
                onValueChange = onValueTwoChange,
                drawableRes2 = iconResTwo,
                drawableColor2 = iconColorTwo,
                intRes = intResTwo,
                intResSup = R.string.is_empty,
                isBorderCard = false
            )
    }
}*/

@Composable
private fun ParametersCard(
    isCurrentDay: Boolean,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    domain: ParametersIncubatorUi,
    onClick: () -> Unit,
) {
    val (borderWidth, borderColor) = if (isCurrentDay) 2.dp to green_1 else 1.dp to grey_2
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        borderWidth = borderWidth,
        borderColor = borderColor,
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconText(
                    number = domain.day.toString(),
                    colorBackground = if (isCurrentDay) green_8 else gray_6,
                    colorText = dark,
                    sizeCard = 40.dp,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        stringResource(R.string.bookmark_screen_current_day_s).format(domain.day),
                        style = text_14,
                        color = black_2
                    )
                    val textRes = when {
                        domain.ovoscopyState.isOvoscopyDay && isCurrentDay -> R.string.bookmark_screen_ovoscopy_day_today
                        isCurrentDay -> R.string.bookmark_screen_current_day
                        domain.ovoscopyState.isOvoscopyDay -> R.string.bookmark_screen_ovoscopy_day
                        else -> null
                    }

                    textRes?.let {
                        Text(
                            stringResource(it),
                            style = text_12,
                            color = gray_7
                        )
                    }
                }
            }
            ParameterRow(domain, isAutoOver, isAutoAiring)
            if (domain.note.isNotBlank())
                BorderCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = orang_4,
                    padding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    borderColor = orang_5
                ) {
                    Text(text = domain.note, style = text_12, color = orang_16)
                }
            if (domain.ovoscopyState.isOvoscopyDay)
                BorderCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = violet_10,
                    padding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    borderColor = violet_11
                ) {
                    Text(
                        text = domain.ovoscopyState.supportText,
                        style = text_12,
                        color = violet_12
                    )
                }
        }
    }
}

@Composable
private fun ParameterRow(
    domain: ParametersIncubatorUi,
    isAutoOver: Boolean,
    isAutoAiring: Boolean
) {
    val auto = stringResource(R.string.entry_bookmark_auto)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ColumnParameterIcon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = domain.temp,
            valueFact = domain.tempFact,
            suffix = "°C",
            iconRes = R.drawable.outline_device_thermostat_24,
            iconColor = error_base,
            containerColor = red_11
        )
        ColumnParameterIcon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = domain.damp,
            valueFact = domain.dampFact,
            suffix = "%",
            iconRes = R.drawable.outline_dew_point_24,
            iconColor = blue_1,
            containerColor = blue_3
        )
        ColumnParameterIcon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = if (isAutoOver) auto else domain.over,
            valueFact = if (isAutoOver) auto else domain.overFact,
            suffix = if (isAutoOver) null else "раза/день",
            iconRes = R.drawable.outline_cached_24,
            iconColor = violet_1,
            containerColor = violet_3
        )
        ColumnParameterIcon(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            value = if (isAutoAiring) auto else domain.airing,
            valueFact = if (isAutoAiring) auto else domain.airingFact,
            suffix = if (isAutoAiring) null else "минут",
            iconRes = R.drawable.outline_air_24,
            iconColor = price_green,
            containerColor = green_g_2
        )
    }
}

@Composable
private fun ColumnParameterIcon(
    modifier: Modifier = Modifier,
    value: String,
    valueFact: String,
    suffix: String? = null,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    containerColor: Color
) {
    val one = value != valueFact
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Row {
                Text(text = value, style = text_14, color = black_2)
                if (one) {
                    Text(text = "/", style = text_14, color = black_2)
                    Text(text = valueFact, style = text_14, color = green_g_5)
                }
            }
            suffix?.let {
                Text(text = it, style = text_12, color = gray_7)
            }
        }
    }
}
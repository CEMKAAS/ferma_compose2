package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconGradient
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.white
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSample(
    datePickerState: DatePickerState,
    dateToday: String,
    onDateSelected: (String) -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = {
            onDateSelected(dateToday)
        },
        confirmButton = {
            Buttons(onDismissRequest = { onDateSelected(dateToday) }) {
                val format = SimpleDateFormat("dd.MM.yyyy")
                val formattedDate: String =
                    format.format(datePickerState.selectedDateMillis)
                onDateSelected(formattedDate)
            }
        }
    ) {
        DatePicker(
            title = { Title(R.string.date_picker_title) { onDateSelected(dateToday) } },
            state = datePickerState,
            dateFormatter = DatePickerDefaults.dateFormatter(),
            showModeToggle = false
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismissRequest: () -> Unit,
    dateBegin: Long,
    dateEnd: Long
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedStartDateMillis = dateBegin,
        initialSelectedEndDateMillis = dateEnd
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Buttons(onDismissRequest = onDismissRequest) {
                onDateRangeSelected(
                    Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                )
                onDismissRequest()
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = { Title(R.string.date_picker_range_title) { onDismissRequest() } },
            showModeToggle = false
        )
    }
}


@Composable
private fun Buttons(
    onDismissRequest: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GradientButton(
            modifier = Modifier.weight(1f),
            colors = listOf(gray_6, gray_6),
            text = stringResource(R.string.button_text_cancel_2),
            textColor = dark,
            onClick = onDismissRequest
        )
        GradientButton(
            modifier = Modifier.weight(1f),
            colors = listOf(green_6, green_shamrock),
            isShadow = true,
            text = stringResource(R.string.button_text_take),
            onClick = onClick
        )
    }
}

@Composable
private fun Title(
    @StringRes titleRes: Int,
    onDismissClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(
            bottom = 12.dp
        )
    ) {
        Column(
            modifier = Modifier.background(color = ghostly_white)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconGradient(
                        sizeCard = 48.dp,
                        icon = R.drawable.baseline_calendar_month_24,
                        colorIcon = white,
                        colors = listOf(green_6, green_shamrock)
                    )
                    Text(
                        stringResource(titleRes),
                        style = text_16,
                        color = black_2
                    )
                }
                IconButton(
                    onClick = onDismissClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_clear_24),
                        contentDescription = null, tint = marengo
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = grey_2)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class MinDateSelectableDates(
    private val minDateMillis: Long
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= minDateMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        // Можно ограничить и года (чтобы не проматывали слишком далеко назад)
        val minYear = Instant.ofEpochMilli(minDateMillis)
            .atZone(ZoneId.systemDefault()).year
        return year >= minYear
    }
}
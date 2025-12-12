package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.list.filterDateList
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextFilterNew

@Composable
fun FilterDateElement(
    value: FilterDate,
    currentPeriod: String,
    onValueChange: (FilterDate) -> Unit,
    titleList: List<FilterDate> = filterDateList,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextFilterNew(
            value = value,
            currentPeriod = currentPeriod,
            onValueChange = { onValueChange(it) },
            titleList = titleList
        )
        HorizontalDivider(thickness = 1.dp, color = gray_6)
    }
}
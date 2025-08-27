package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.dto.PairDataDoubleSting
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.start.formatNumber


@Composable
fun ErrorSupportText(
    isError: Boolean,
    @StringRes intRes: Int,
    @StringRes intResError: Int
) {
    if (isError)
        Text(
            text = stringResource(intResError),
            color = MaterialTheme.colorScheme.error
        )
    else
        Text(stringResource(intRes))
}

@Composable
fun ErrorSupportTextSlash(
    isError: Boolean,
    isWarehouse: Boolean = false,
    isAnimal: Boolean = false,
    isErrorSlash: Boolean = false,
    warehouseList: List<PairDataDoubleSting> = emptyList(),
    countAnimals: String = "",
    @StringRes intRes: Int,
    @StringRes intResError: Int
) {
    when (true) {

        isError -> Text(
            text = stringResource(intResError),
            color = MaterialTheme.colorScheme.error
        )

        isErrorSlash -> Text(
            text = stringResource(R.string.error_slash),
            color = MaterialTheme.colorScheme.error
        )

        isWarehouse -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.support_text_count_warehouse_s)
                )
                warehouseList.forEachIndexed { index, item ->
                    Text(
                        text = "${item.first.formatNumber()} ${item.second}",
                        color = if (item.first < 0) MaterialTheme.colorScheme.error else Color.Unspecified
                    )

                    if (index != warehouseList.lastIndex) {
                        Text(text = "/")
                    }
                }
            }
        }

        isAnimal -> Text(
            text = stringResource(
                R.string.support_text_count_sale_animals, countAnimals.toFormatNumber(), "todo"
            )
        )

        else -> Text(stringResource(intRes))
    }
}

@Composable
fun ErrorSupportAnimal(
    isError: Boolean = false,
    isErrorAnimal: Boolean = false,
    isErrorCountZero: Boolean = false,
    errorText: String = "",
    errorCountAllText: String = "",
    errorCountZeroText: String = "",
    supportText: String
) {
    when (true) {
        isError -> Text(
            text = errorText,
            color = MaterialTheme.colorScheme.error
        )

        isErrorAnimal -> Text(
            text = errorCountAllText,
            color = MaterialTheme.colorScheme.error
        )

        isErrorCountZero -> Text(
            text = errorCountZeroText,
            color = MaterialTheme.colorScheme.error
        )

        else -> Text(text = supportText)
    }
}

package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber


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
    isErrorSlash: Boolean = false,
    count: String = "",
    suffix: String = "",
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

        isWarehouse -> Text(
            text = stringResource(R.string.support_text_count_warehouse_s, "${count.toFormatNumber()} $suffix"),
            color = if (count.contains("-")) MaterialTheme.colorScheme.error else Color.Unspecified
        )

        else -> Text(stringResource(intRes))
    }
}

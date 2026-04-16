package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard

@Composable
fun WarningDeleteBottomSheet(
    @StringRes titleRes: Int = R.string.base_section_delete_add,
    @StringRes supportRes: Int = R.string.base_section_delete_support_add,
    @StringRes textButtonRes: Int = R.string.base_section_button_delete_add,
    @StringRes textRes: Int = R.string.base_section_warning_text,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    itemCard: @Composable ColumnScope.() -> Unit
) {
    BaseBottomSheet(
        title = stringResource(titleRes),
        supText = stringResource(supportRes),
        skipPartiallyExpanded = true,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemCard()
            WarningCard(
                colorBackground = red_11,
                colorBorder = red_15,
                colorIcon = orang_17,
                colorTitle = orang_17,
                colorText = red_14,
                icon = R.drawable.outline_info_24,
                title = R.string.base_section_warning,
                text = textRes
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(textButtonRes),
                    onClick = { onDeleteClick() },
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(red_13, error_base)
                )
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_text_cancel),
                    onClick = onDismissRequest,
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(gray_6, gray_6), textColor = dark
                )
            }
        }
    }
}
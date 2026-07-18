package com.zaroslikov.fermacompose2.ui.elements.сompositions.card

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14

@Composable
fun InfoPatternCard(
    title: String?,
    count: String?,
    countSuffix: Suffix?,
    category: String?,
    buyer: String?,
    animalName: String?,
    note: String?
) {
    CardFieldNew(
        elevation = 0.dp,
        containerColor = ghostly_white
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.template_qr_code_bottom_sheet_data_from_template),
                style = text_12,
                lineHeight = 16.sp,
                color = grey
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                title?.let {
                    TitleWithValue(
                        titleRes = R.string.outlined_text_product,
                        value = it
                    )
                }
                FlowRow(
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    count?.let {
                        TitleWithValue(
                            modifier = Modifier.weight(1f),
                            titleRes = R.string.outlined_text_field_quantity,
                            value = it
                        )
                    }
                    countSuffix?.let {
                        TitleWithValue(
                            modifier = Modifier.weight(1f),
                            titleRes = R.string.outlined_text_suffix,
                            value = stringResource(it.toResId())
                        )
                    }
                    category?.let {
                        TitleWithValue(
                            modifier = Modifier.weight(1f),
                            titleRes = R.string.outlined_text_field_category,
                            value = it
                        )
                    }
                    buyer?.let {
                        TitleWithValue(
                            modifier = Modifier.weight(1f),
                            titleRes = R.string.outlined_text_buyer,
                            value = it
                        )
                    }
                    animalName?.let {
                        TitleWithValue(
                            modifier = Modifier.weight(1f),
                            titleRes = R.string.outlined_text_animals,
                            value = it
                        )
                    }
                    note?.let {
                        TitleWithValue(
                            modifier = Modifier.weight(1f),
                            titleRes = R.string.outlined_text_note,
                            value = it
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleWithValue(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    value: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            stringResource(titleRes),
            style = text_12,
            lineHeight = 16.sp,
            color = grey
        )
        Text(value, style = text_14, lineHeight = 20.sp, color = black_2)
    }
}
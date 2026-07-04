package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_12
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem

@Composable
fun TemplatesBottomSheet(
    list: List<TemplateItem> = emptyList(),
    @DrawableRes iconRes: Int,
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onCreatePatternClick: () -> Unit,
    onChoicePatternClick: () -> Unit,
    onEditTemplateClick: (Long) -> Unit,
    onCreateQrCodeClick: () -> Unit,
    onDeleteTemplateClick: (Long) -> Unit
) {
    BaseBottomSheet(
        title = stringResource(R.string.pattern_bottom_sheet_title),
        supText = stringResource(R.string.pattern_bottom_sheet_title_support),
        iconRes = iconRes,
        colors = colors,
        onDismissRequest = onDismissRequest,
        contentBottom = {
            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.pattern_bottom_sheet_create_pattern),
                iconRes = R.drawable.icon_add,
                borderColor = green_12,
                onClick = onCreatePatternClick
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            list.forEach {
                TemplateCard(
                    iconRes = iconRes,
                    title = it.nameTemplate,
                    value = it.description.ifBlank { null },
                    onClick = onChoicePatternClick,
                    onEditClick = { onEditTemplateClick(it.id) },
                    onCreateQrCodeClick = onCreateQrCodeClick,
                    onDeleteClick = { onDeleteTemplateClick(it.id) }
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    @DrawableRes iconRes: Int,
    title: String,
    value: String?,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onCreateQrCodeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    CardFieldNew(
        containerColor = ghostly_white,
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(title, fontSize = 14.sp, color = black_2, lineHeight = 20.sp)
                    if (value != null)
                        Text(value, fontSize = 12.sp, color = grey, lineHeight = 16.sp)
                }
            }
            DropdownMenuEdit(
                onEditClick = onEditClick,
                onCreateQrCodeClick = onCreateQrCodeClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}
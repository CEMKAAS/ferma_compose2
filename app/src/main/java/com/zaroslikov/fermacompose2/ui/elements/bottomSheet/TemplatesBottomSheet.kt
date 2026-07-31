package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.empty_list.MessageNoData2
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.empty_list.EmptyListNew
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem

@Composable
fun TemplatesBottomSheet(
    list: List<TemplateItem> = emptyList(),
    @DrawableRes iconRes: Int,
    colors: List<Color>,
    iconColor: Color,
    iconBorderColor: Color,
    onDismissRequest: () -> Unit,
    onCreatePatternClick: () -> Unit,
    onSetPinnedClick: (Pair<Boolean, Long>) -> Unit = {},
    onChoicePatternClick: (Long) -> Unit,
    onEditTemplateClick: (Long) -> Unit,
    onCreateQrCodeClick: (Long) -> Unit,
    onDeleteTemplateClick: (Long) -> Unit
) {
    BaseBottomSheet(
        title = stringResource(R.string.pattern_bottom_sheet_title),
        supText = if (list.isNotEmpty()) stringResource(R.string.pattern_bottom_sheet_title_support) else null,
        iconRes = R.drawable.outline_description_24,
        colors = colors,
        onDismissRequest = onDismissRequest,
        contentBottom = {
            if (list.isNotEmpty())
                BorderButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.pattern_bottom_sheet_create_pattern),
                    iconRes = R.drawable.icon_add,
                    onClick = onCreatePatternClick
                )
            else
                GradientButton(
                    modifier = Modifier.fillMaxWidth(1f),
                    text = stringResource(R.string.pattern_bottom_sheet_create_pattern),
                    prefixIconRes = R.drawable.icon_add,
                    onClick = onCreatePatternClick,
                    colors = colors
                )
        }
    ) {
        if (list.isNotEmpty())
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                list.forEach {
                    TemplateCard(
                        iconRes = iconRes,
                        title = it.name,
                        value = it.description.ifBlank { null },
                        pin = it.isPinned,
                        isMultiProject = it.isMultiProject,
                        iconColor = iconColor,
                        iconBorderColor = iconBorderColor,
                        onClick = { onChoicePatternClick(it.id) },
                        onEditClick = { onEditTemplateClick(it.id) },
                        onCreateQrCodeClick = { onCreateQrCodeClick(it.id) },
                        onDeleteClick = { onDeleteTemplateClick(it.id) },
                        onPinnedClick = { onSetPinnedClick(true to it.id) },
                        onUnpinnedClick = { onSetPinnedClick(false to it.id) }
                    )
                }
            }
        else EmptyListNew(
            iconRes = R.drawable.outline_description_24,
            title = R.string.message_no_data_title_template,
            supportText = R.string.message_no_data_message_template,
            list = listOf(
                R.drawable.baseline_electric_bolt_24 to R.string.message_no_data_message_template_item_1,
                R.drawable.outline_description_24 to R.string.message_no_data_message_template_item_2,
                R.drawable.outline_qr_code_24 to R.string.message_no_data_message_template_item_3
            ),
            primeColor = iconColor
        )
    }
}

@Composable
fun TemplateCard(
    @DrawableRes iconRes: Int,
    title: String,
    value: String?,
    pin: Boolean,
    isMultiProject: Boolean,
    iconColor: Color,
    iconBorderColor: Color,
    onClick: (() -> Unit)? = null,
    onPinnedClick: () -> Unit = {},
    onUnpinnedClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onCreateQrCodeClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    BorderCard(
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
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = iconBorderColor
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        tint = iconColor
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(title, fontSize = 14.sp, color = black_2, lineHeight = 20.sp)
                        if (isMultiProject)
                            Icon(
                                painterResource(R.drawable.outline_language_24),
                                contentDescription = null,
                                tint = grey,
                                modifier = Modifier.size(16.dp)
                            )
                        if (pin)
                            Icon(
                                painterResource(R.drawable.outline_keep_24),
                                contentDescription = null,
                                tint = grey,
                                modifier = Modifier.size(16.dp)
                            )
                    }
                    if (value != null)
                        Text(value, fontSize = 12.sp, color = grey, lineHeight = 16.sp)
                }
            }
            if (onClick != null)
                DropdownMenuEdit(
                    onPinnedClick = if (!pin) onPinnedClick else null,
                    onUnpinnedClick = if (pin) onUnpinnedClick else null,
                    onEditClick = onEditClick,
                    onCreateQrCodeClick = onCreateQrCodeClick,
                    onDeleteClick = onDeleteClick
                )
        }
    }
}
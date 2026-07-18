package com.zaroslikov.fermacompose2.ui.elements.bottomSheet


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData2
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.white
import kotlin.collections.forEach

@Composable
fun ChoiceProjectBottomSheet(
    list: List<DomainProjectTable> = emptyList(),
    onDismissRequest: () -> Unit,
    onChoiceProjectClick: (Long) -> Unit
) {
    BaseBottomSheet(
        title = stringResource(R.string.choice_project_for_template_bottom_sheet_title),
        supText = if (list.isNotEmpty()) stringResource(R.string.choice_project_for_template_bottom_sheet_title_support) else null,
        iconRes = R.drawable.outline_language_24,
        colors = listOf(price_green, green_shamrock),
        onDismissRequest = onDismissRequest,
        contentBottom = {
            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.button_close),
                onClick = onDismissRequest
            )
        }
    ) {
        if (list.isNotEmpty())
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                list.forEach { projectTable ->
                    ProjectCard(projectTable.title) {
                        onChoiceProjectClick(projectTable.id)
                    }
                }
            }
        else MessageNoData2(
            titleRes = R.string.message_no_data_title_choice_project_for_template,
            messageRes = R.string.message_no_data_message_choice_project_for_template,
            supportSecondText = R.string.is_empty,
            iconRes = R.drawable.outline_work_24,
            iconColor = green_shamrock,
            backgroundColor = alabaster,
            iconSize = 48.dp
        )
    }
}

@Composable
private fun ProjectCard(
    nameProject: String,
    onClick: () -> Unit
) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ghostly_white,
        padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        onClick = onClick
    ) {
        Text(
            nameProject,
            style = text_14,
            color = black_1,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
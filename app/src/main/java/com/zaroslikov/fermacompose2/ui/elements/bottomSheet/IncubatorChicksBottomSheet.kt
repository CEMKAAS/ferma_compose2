package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.utils.IncubatorChicks

/**
 * «Куда добавить птенцов из «Инкубатора»?» — новый проект с названием закладки или один
 * из имеющихся. Нажатие и есть решение: птенцы записываются сразу, без формы и без
 * кнопки «Сохранить» — всё о группе пришло в ссылке, а согласие человек дал ещё в
 * «Инкубаторе». Поэтому в шапке стоит, что именно будет добавлено, — последнее место,
 * где это видно до записи.
 *
 * Новый проект — первым и всегда: под новую партию его заводят не реже, чем кладут в
 * имеющийся, и это единственный путь, когда проектов нет вовсе.
 */
@Composable
fun IncubatorChicksBottomSheet(
    chicks: IncubatorChicks,
    projects: List<DomainProjectTable>,
    onDismissRequest: () -> Unit,
    onNewProjectClick: () -> Unit,
    onProjectClick: (Long) -> Unit,
) {
    BaseBottomSheet(
        title = stringResource(R.string.incubator_chicks_sheet_title),
        supText = stringResource(R.string.incubator_chicks_sheet_support)
            .format(chicks.name, chicks.type, chicks.count),
        iconRes = R.drawable.outline_egg_24,
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChoiceCard(
                title = stringResource(R.string.incubator_chicks_new_project),
                subtitle = "«${chicks.name}»",
                iconRes = R.drawable.icon_add,
                onClick = onNewProjectClick,
            )
            if (projects.isNotEmpty()) {
                Text(
                    stringResource(R.string.incubator_chicks_existing_project),
                    style = text_14,
                    color = dark,
                    modifier = Modifier.padding(top = 8.dp)
                )
                projects.forEach { project ->
                    ChoiceCard(
                        title = project.title,
                        subtitle = project.date,
                        iconRes = R.drawable.outline_work_24,
                        onClick = { onProjectClick(project.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChoiceCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = ghostly_white,
        padding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = green_shamrock,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = text_14,
                    color = black_1,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle.isNotBlank())
                    Text(subtitle, style = text_12, color = dark)
            }
        }
    }
}

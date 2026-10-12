package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.SecondAnimalCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter

/**
 * Секция «Группа» в карточке обезличенной группы: выделить особей из счётчика.
 * Новые карточки попадут в её сборную группу (или в новую, если её нет).
 */
@Composable
fun GroupSection(
    state: AnimalCardState,
    onIntent: (AnimalCardIntent) -> Unit
) {
    if (state.isArchive || state.splitMax < 1) return
    SecondAnimalCard(
        icon = R.drawable.icon_group,
        intRes = R.string.animal_group_card_title
    ) {
        GroupButton(R.string.animal_group_button_split, enabled = !state.isGroupOperationRunning) {
            onIntent(AnimalCardIntent.OpenSplitDialog(true))
        }
    }
}

/** Строка «Группа» в первом блоке карточки члена сборной группы: → её карточка, рядом — «Вывести из группы». */
@Composable
fun HerdParameter(
    herdName: String,
    onClick: (() -> Unit)?,
    isLeaveVisible: Boolean,
    isLeaveEnabled: Boolean,
    onLeaveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimalParameter(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp)),
            titleParameter = R.string.animal_group_card_title,
            parameter = herdName,
            icon = R.drawable.ic_herd,
            iconColor = animal_1,
            iconColorSecond = green_g_3,
            isMore = onClick != null,
            onClick = onClick
        )
        if (isLeaveVisible)
            IconButton(onClick = onLeaveClick, enabled = isLeaveEnabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_herd_leave),
                    contentDescription = stringResource(R.string.animal_group_button_leave),
                    tint = grey
                )
            }
    }
}

@Composable
internal fun GroupButton(
    textRes: Int,
    enabled: Boolean,
    @DrawableRes iconRes: Int = R.drawable.ic_herd,
    onClick: () -> Unit
) {
    BorderButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(textRes),
        iconRes = iconRes,
        iconColor = animal_1,
        enabled = enabled,
        onClick = onClick
    )
}

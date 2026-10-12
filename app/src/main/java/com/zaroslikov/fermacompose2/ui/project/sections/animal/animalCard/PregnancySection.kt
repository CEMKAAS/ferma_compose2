package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.animal.pregnancy.SpeciesPregnancyNorms
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.SecondAnimalCard
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter

/**
 * Секция «Беременность» в карточке самки: краткий прогресс активной беременности или
 * «Отметить случку» / история. «Отметить случку» открывает лист прямо в карточке,
 * остальные клики ведут на экран беременности.
 */
@Composable
fun PregnancySection(
    state: AnimalCardState,
    onClick: () -> Unit,
    onStartClick: () -> Unit
) {
    SecondAnimalCard(
        icon = R.drawable.baseline_calendar_month_24,
        intRes = R.string.pregnancy_title
    ) {
        val active = state.activePregnancy
        val progress = state.pregnancyProgress
        val norms = state.pregnancyNorms
        when {
            active != null && progress != null && norms != null ->
                ActivePregnancyBlock(progress = progress, norms = norms, onClick = onClick)

            active != null ->
                AnimalParameter(
                    titleParameter = R.string.pregnancy_mating_date,
                    parameter = active.matingDate,
                    icon = R.drawable.baseline_calendar_month_24,
                    iconColor = animal_1,
                    iconColorSecond = green_15,
                    isMore = true,
                    onClick = onClick
                )

            state.canStartPregnancy ->
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.pregnancy_start_button),
                    colors = listOf(animal_1, animal_2),
                    prefixIconRes = R.drawable.baseline_favorite_24,
                    enabled = !state.isPregnancySaving,
                    isShadow = true,
                    paddingValues = PaddingValues(16.dp),
                    onClick = onStartClick
                )
        }
        if (state.pregnancyHistoryCount > 0)
            AnimalParameter(
                titleParameter = R.string.pregnancy_history_title,
                parameter = stringResource(R.string.pregnancy_history_count, state.pregnancyHistoryCount),
                icon = R.drawable.baseline_edit_calendar_24,
                iconColor = animal_1,
                iconColorSecond = green_15,
                isMore = true,
                onClick = onClick
            )
    }
}

@Composable
private fun ActivePregnancyBlock(
    progress: PregnancyProgress,
    norms: SpeciesPregnancyNorms,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.pregnancy_section_progress,
                    norms.terms.pregnantAdjective,
                    progress.day
                ),
                style = textBold_16,
                color = dark,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painterResource(R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = null,
                tint = grey
            )
        }
        LinearProgressIndicator(
            progress = { progress.fraction },
            modifier = Modifier.fillMaxWidth(),
            color = animal_1,
            trackColor = green_15
        )
        Text(
            text = stringResource(
                R.string.pregnancy_section_expected,
                norms.terms.birthName.lowercase(),
                PregnancyDates.format(progress.expectedDate)
            ),
            style = text_14,
            color = marengo
        )
        val isOverdue = progress.daysLeft < 0
        Text(
            text = if (isOverdue)
                pluralStringResource(R.plurals.pregnancy_overdue, -progress.daysLeft, -progress.daysLeft)
            else
                pluralStringResource(R.plurals.pregnancy_days_left, progress.daysLeft, progress.daysLeft),
            style = text_14,
            color = if (isOverdue) red_14 else marengo
        )
    }
}

/**
 * Архивация беременной самки: прервать беременность (гибель, забой, продажа) и убрать в архив
 * или только убрать в архив (беременность остаётся активной, напоминания не придут, пока самка
 * в архиве).
 */
@Composable
fun ArchivePregnancyDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (interrupt: Boolean) -> Unit
) {
    AlertDialogStandard(
        titleRes = R.string.pregnancy_archive_interrupt_title,
        iconRes = R.drawable.baseline_archive_24,
        titleBackgroundColor = orang_4,
        onDismissRequest = onDismissRequest,
        onClick = { onConfirm(true) },
        colors = listOf(animal_1, animal_2),
        textButtonRes = R.string.pregnancy_archive_interrupt_confirm
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.pregnancy_archive_interrupt_text),
                style = text_14,
                color = marengo
            )
            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.pregnancy_archive_keep),
                iconRes = R.drawable.baseline_archive_24,
                onClick = { onConfirm(false) }
            )
        }
    }
}

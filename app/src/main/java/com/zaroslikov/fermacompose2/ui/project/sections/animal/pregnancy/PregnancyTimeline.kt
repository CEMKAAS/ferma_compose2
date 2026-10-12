package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyStage
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyStageKind
import com.zaroslikov.domain.models.animal.pregnancy.StageStatus
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.white
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import java.time.LocalDate

/**
 * Этапы беременности вертикальной лентой: пройденные — с галочкой, текущий — цветной точкой,
 * будущие — серым кольцом. [day] — день активной беременности от случки.
 */
@Composable
fun PregnancyTimeline(
    stages: List<PregnancyStage>,
    matingDate: LocalDate,
    day: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val statuses = stages.map { PregnancyCalculator.stageStatus(it, day) }
        stages.forEachIndexed { index, stage ->
            TimelineRow(
                stage = stage,
                status = statuses[index],
                nextStatus = statuses.getOrNull(index + 1),
                matingDate = matingDate
            )
        }
    }
}

@Composable
private fun TimelineRow(
    stage: PregnancyStage,
    status: StageStatus,
    /** null — последний этап, линии под ним нет. */
    nextStatus: StageStatus?,
    matingDate: LocalDate
) {
    val isLast = nextStatus == null
    val accent = if (stage.kind == PregnancyStageKind.OVERDUE) error_base else animal_1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StageDot(status = status, accent = accent)
            // Следующий этап начался — линия залита целиком; этот начался, а следующий ещё нет — наполовину
            if (nextStatus != null)
                Column(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (nextStatus != StageStatus.UPCOMING) accent else gray_4)
                ) {
                    if (status != StageStatus.UPCOMING && nextStatus == StageStatus.UPCOMING)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                                .background(accent)
                        )
                }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stageDaysText(stage, matingDate),
                style = text_12,
                color = if (status == StageStatus.CURRENT) accent else grey
            )
            Text(
                text = stage.title,
                style = text_14,
                fontWeight = if (status == StageStatus.CURRENT) FontWeight.SemiBold else FontWeight.Medium,
                color = if (status == StageStatus.UPCOMING) gray_7 else dark
            )
            Text(
                text = stage.description,
                style = text_12,
                color = gray_7
            )
        }
    }
}

@Composable
private fun StageDot(status: StageStatus, accent: Color) {
    val size = 20.dp
    when (status) {
        StageStatus.DONE -> Box(
            modifier = Modifier
                .size(size)
                .background(accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.icon_check),
                contentDescription = null,
                tint = white,
                modifier = Modifier.size(12.dp)
            )
        }

        StageStatus.CURRENT -> Box(
            modifier = Modifier
                .size(size)
                .border(2.dp, accent, CircleShape)
                .padding(4.dp)
                .background(accent, CircleShape)
        )

        StageStatus.UPCOMING -> Box(
            modifier = Modifier
                .size(size)
                .border(2.dp, gray_4, CircleShape)
                .background(white, CircleShape)
        )
    }
}

/** «10–14 д · 11.10.2026 – 15.10.2026» или «31 д · 01.11.2026». */
@Composable
private fun stageDaysText(stage: PregnancyStage, matingDate: LocalDate): String {
    val from = PregnancyDates.format(PregnancyCalculator.dateOfDay(matingDate, stage.dayFrom))
    return if (stage.dayFrom == stage.dayTo)
        stringResource(R.string.pregnancy_stage_day, stage.dayFrom, from)
    else
        stringResource(
            R.string.pregnancy_stage_days,
            stage.dayFrom,
            stage.dayTo,
            stringResource(
                R.string.pregnancy_date_range,
                from,
                PregnancyDates.format(PregnancyCalculator.dateOfDay(matingDate, stage.dayTo))
            )
        )
}

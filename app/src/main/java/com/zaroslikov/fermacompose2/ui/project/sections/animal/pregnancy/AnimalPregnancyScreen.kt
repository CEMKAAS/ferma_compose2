@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyBlock
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyPhase
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.animal.pregnancy.SpeciesPregnancyNorms
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_13
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_5
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.EXPAND_MS
import com.zaroslikov.fermacompose2.ui.elements.ExpandableRows
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.SecondAnimalCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalBreedText
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal
import kotlin.math.roundToInt

/** Карточка «Особенности вида» пока скрыта; вернуть — поставить true. */
private const val SHOW_SPECIES_TIPS = false

/**
 * Экран беременности самки.
 * [onNavigateAnimalCard] получает (itemIdPT, id животного) — переход в карточку самца.
 */
@Composable
fun AnimalPregnancyScreen(
    navigateBack: () -> Unit,
    onNavigateAnimalCard: (Pair<Long, Long>) -> Unit,
    viewModel: AnimalPregnancyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.pregnancy_title,
                onNavigateBackClick = navigateBack,
                onSettingsClick = state.active
                    ?.takeIf { state.canEditActive && !state.isSaving }
                    ?.let { { viewModel.onIntent(AnimalPregnancyIntent.OpenEdit(it.id)) } },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (!state.isLoading && state.canAddDayNote)
                NeonGlowFab(
                    colors = listOf(animal_1, animal_2),
                    iconRes = R.drawable.baseline_sticky_note_2_24
                ) {
                    if (!state.isSaving) viewModel.onIntent(AnimalPregnancyIntent.OpenDayNote(true))
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(modifier = Modifier.padding(innerPadding))
        else
            AnimalPregnancyContainer(
                modifier = Modifier.modifierScreen(innerPadding),
                state = state,
                onIntent = viewModel::onIntent,
                onAnimalClick = { onNavigateAnimalCard(state.itemIdPT to it) }
            )
    }
}

@Composable
private fun AnimalPregnancyContainer(
    modifier: Modifier,
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit,
    onAnimalClick: (Long) -> Unit
) {
    val active = state.active
    val progress = state.progress
    val norms = state.norms
    val hasProgress = active != null && progress != null && norms != null
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // При активной беременности самка показывается в шапке карточки прогресса
        if (!hasProgress) HeaderCard(state)
        when {
            state.isReadOnly -> InfoBanner(stringResource(R.string.pregnancy_read_only))
            state.isFemaleArchived && active != null ->
                InfoBanner(stringResource(R.string.pregnancy_female_archived_hint))
        }
        if (active != null && progress != null && norms != null) {
            ProgressCard(
                state = state,
                active = active,
                progress = progress,
                norms = norms,
                onIntent = onIntent
            )
            if (state.isBirthAction)
                BirthButton(state = state, norms = norms, onIntent = onIntent)
            DayNotesCard(state = state, pregnancy = active, onIntent = onIntent)
            SecondAnimalCard(icon = R.drawable.baseline_event_24, intRes = R.string.pregnancy_stages_title) {
                PregnancyTimeline(stages = norms.stages, matingDate = progress.matingDate, day = progress.day)
            }
        } else if (active != null) {
            // Беременность есть, но норм нет (или дата не читается) — управлять записью всё равно можно
            SecondAnimalCard(icon = R.drawable.baseline_favorite_24, intRes = R.string.pregnancy_title) {
                Text(pregnancySummary(active), style = text_14, color = marengo)
                Text(stringResource(R.string.pregnancy_no_norms), style = text_12, color = gray_7)
            }
            DayNotesCard(state = state, pregnancy = active, onIntent = onIntent)
        } else
            NoActiveCard(state = state, onIntent = onIntent)
        if (SHOW_SPECIES_TIPS && norms != null && norms.tips.isNotEmpty())
            TipsCard(norms.tips)
        if (state.history.isNotEmpty())
            HistoryCard(state = state, onIntent = onIntent)
        if (state.canInterrupt && !state.isBirthAction)
            InterruptButton(state = state, onIntent = onIntent)
        // FAB не должен перекрывать последнюю кнопку/карточку
        if (state.canAddDayNote)
            Spacer(modifier = Modifier.height(56.dp))
        PregnancySheets(state = state, onIntent = onIntent)
        if (state.isSireSheetOpen)
            PregnancySireSheet(
                state = state,
                onDismissRequest = { onIntent(AnimalPregnancyIntent.OpenSireSheet(false)) },
                onOpenCard = {
                    onIntent(AnimalPregnancyIntent.OpenSireSheet(false))
                    onAnimalClick(it)
                }
            )
    }
}

/** Самка: иконка, имя, тип и пол. */
@Composable
private fun HeaderCard(state: AnimalPregnancyState) {
    CardFieldNew {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimalIcon(state)
            AnimalNameType(state)
        }
    }
}

@Composable
private fun AnimalIcon(state: AnimalPregnancyState, sizeIcon: Dp = 64.dp) {
    val animal = state.animal
    IconAnimal(
        sizeIcon = sizeIcon,
        sex = animal.sex,
        currentIcon = animal.currentIcon ?: AppIcon.PETS,
        imagePath = animal.imagePath,
        isArchive = animal.archive
    )
}

/** Имя, тип и пол самки. */
@Composable
private fun AnimalNameType(state: AnimalPregnancyState) {
    val animal = state.animal
    Column(
        modifier = Modifier.fillMaxWidth(),
        // Имя, тип и порода — один плотный блок; различаются размером и цветом (как в карточке животного)
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = animal.name,
            style = textBold_16,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = animal.type,
                style = text_14,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = marengo,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (!animal.group)
                CountColorGradientCard(sex = animal.sex)
        }
        // Порода необязательна — строку показываем, только если её указали
        animal.breed?.takeIf { it.isNotBlank() }?.let { AnimalBreedText(breed = it) }
    }
}

/** Самка, круг прогресса, срок, случка и отец, ожидаемая дата и диапазон, дни до родов / просрочка. */
@Composable
private fun ProgressCard(
    state: AnimalPregnancyState,
    active: DomainAnimalPregnancy,
    progress: PregnancyProgress,
    norms: SpeciesPregnancyNorms,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val accent = when (progress.phase) {
        PregnancyPhase.IN_PROGRESS -> animal_1
        PregnancyPhase.BIRTH_WINDOW -> orang_9
        PregnancyPhase.OVERDUE -> error_base
    }
    CardFieldNew {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Фото/иконка самки в кольце прогресса, под кольцом — «день / всего»
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier.size(104.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress.fraction },
                            modifier = Modifier.fillMaxSize(),
                            color = accent,
                            trackColor = gray_4,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round,
                            gapSize = 0.dp
                        )
                        AnimalIcon(state, sizeIcon = 84.dp)
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = progress.day.toString(), style = textBold_20, color = dark)
                        Text(
                            text = " " + stringResource(R.string.pregnancy_gestation_total, progress.gestationDays),
                            style = text_14,
                            color = grey,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AnimalNameType(state)
                    HorizontalDivider(thickness = 1.dp, color = gray_4)
                    Text(
                        text = stringResource(R.string.pregnancy_trimester, progress.trimester),
                        style = text_16,
                        fontWeight = FontWeight.SemiBold,
                        color = dark
                    )
                    Text(
                        text = daysLeftText(progress),
                        style = text_14,
                        fontWeight = FontWeight.Medium,
                        color = if (progress.daysLeft < 0) accent else animal_1
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = gray_4)
            AnimalParameter(
                titleParameter = R.string.pregnancy_mating_date,
                parameter = active.matingDate,
                icon = R.drawable.baseline_favorite_24,
                iconColor = animal_1,
                iconColorSecond = green_15
            )
            SireParameter(
                state = state,
                active = active,
                onClick = { onIntent(AnimalPregnancyIntent.OpenSireSheet(true)) }
            )
            InfoRow(
                title = stringResource(R.string.pregnancy_expected, norms.terms.birthName.lowercase()),
                value = PregnancyDates.format(progress.expectedDate),
                icon = R.drawable.baseline_calendar_month_24,
                iconColor = accent,
                iconColorSecond = orang_3
            )
            AnimalParameter(
                titleParameter = R.string.pregnancy_range,
                parameter = stringResource(
                    R.string.pregnancy_date_range,
                    PregnancyDates.format(progress.earliestDate),
                    PregnancyDates.format(progress.latestDate)
                ),
                icon = R.drawable.baseline_event_24,
                iconColor = blue_1,
                iconColorSecond = blue_3
            )
            if (active.note.isNotBlank())
                InfoRow(
                    title = stringResource(R.string.pregnancy_note),
                    value = active.note,
                    icon = R.drawable.baseline_sticky_note_2_24,
                    iconColor = gray_7,
                    iconColorSecond = orang_13
                )
        }
    }
}

@Composable
private fun daysLeftText(progress: PregnancyProgress): String = when {
    progress.phase == PregnancyPhase.OVERDUE ->
        pluralStringResource(R.plurals.pregnancy_overdue, progress.overdueDays, progress.overdueDays)

    progress.daysLeft >= 0 ->
        pluralStringResource(R.plurals.pregnancy_days_left, progress.daysLeft, progress.daysLeft)

    else -> pluralStringResource(R.plurals.pregnancy_overdue, -progress.daysLeft, -progress.daysLeft)
}

/**
 * Кнопка действия зависит от срока: в окне родов и при перенашивании — «Роды» под прогрессом,
 * раньше (или у архивной самки) — красная «Прервать» внизу экрана.
 * Изменить/удалить — шестерёнка в заголовке.
 */
@Composable
private fun BirthButton(
    state: AnimalPregnancyState,
    norms: SpeciesPregnancyNorms,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = norms.terms.birthName,
        colors = listOf(animal_1, animal_2),
        prefixIconRes = R.drawable.baseline_favorite_24,
        enabled = !state.isSaving,
        isShadow = true,
        paddingValues = PaddingValues(16.dp)
    ) { onIntent(AnimalPregnancyIntent.OpenBirth(true)) }
}

@Composable
private fun InterruptButton(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.pregnancy_interrupt_button),
        colors = listOf(red_13, red_5),
        prefixIconRes = R.drawable.outline_cancel_24,
        enabled = !state.isSaving,
        isShadow = true,
        paddingValues = PaddingValues(16.dp)
    ) { onIntent(AnimalPregnancyIntent.OpenInterrupt(true)) }
}

/** Строка «Отец»: по нажатию — лист с подробностями о самце; «Не указан» не кликается. */
@Composable
private fun SireParameter(
    state: AnimalPregnancyState,
    active: DomainAnimalPregnancy,
    onClick: () -> Unit
) {
    val sireId = active.sireId
    val sireName = active.sireName?.takeIf { it.isNotBlank() }
    // Подробности есть у самца из хозяйства и у чужого производителя; «Не указан» не кликается
    val isKnown = sireId != null || sireName != null
    AnimalParameter(
        titleParameter = R.string.pedigree_father,
        parameter = when {
            sireId != null -> sireName ?: state.sireCandidates.firstOrNull { it.id == sireId }?.name.orEmpty()
            sireName != null -> stringResource(R.string.pedigree_absent, sireName)
            else -> stringResource(R.string.pedigree_unknown)
        },
        icon = R.drawable.baseline_male_24,
        iconColor = blue_1,
        iconColorSecond = blue_3,
        isMore = isKnown,
        onClick = if (isKnown) onClick else null
    )
}

/** Нет активной беременности: кнопка «Отметить случку» или причина, почему нельзя. */
@Composable
private fun NoActiveCard(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    CardFieldNew {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = green_15),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = null,
                    tint = animal_1,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                )
            }
            Text(
                text = stringResource(R.string.pregnancy_section_none),
                style = text_16,
                fontWeight = FontWeight.SemiBold,
                color = dark,
                textAlign = TextAlign.Center
            )
            when {
                state.canStart ->
                    GradientButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.pregnancy_start_button),
                        colors = listOf(animal_1, animal_2),
                        prefixIconRes = R.drawable.baseline_favorite_24,
                        enabled = !state.isSaving,
                        isShadow = true,
                        paddingValues = PaddingValues(16.dp)
                    ) { onIntent(AnimalPregnancyIntent.OpenStart(true)) }

                state.isReadOnly -> Unit

                else -> blockText(state)?.let {
                    Text(text = it, style = text_14, color = gray_7, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun blockText(state: AnimalPregnancyState): String? = when (state.startBlock) {
    PregnancyBlock.NOT_MAMMAL -> stringResource(R.string.pregnancy_block_not_mammal)
    PregnancyBlock.GROUP -> stringResource(R.string.pregnancy_block_group)
    PregnancyBlock.MALE -> stringResource(R.string.pregnancy_block_male)
    PregnancyBlock.ARCHIVED -> stringResource(R.string.pregnancy_block_archived)
    PregnancyBlock.ALREADY_ACTIVE -> stringResource(R.string.pregnancy_block_active)
    null -> if (state.norms == null) stringResource(R.string.pregnancy_no_norms) else null
}

@Composable
private fun TipsCard(tips: List<String>) {
    SecondAnimalCard(icon = R.drawable.outline_info_24, intRes = R.string.pregnancy_tips_title) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tips.forEach { tip ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "•", style = text_14, color = animal_1)
                    Text(text = tip, style = text_14, color = marengo)
                }
            }
        }
    }
}

/** Прошлые беременности: результат, причина, приплод; клик — заметка/удаление. */
@Composable
private fun HistoryCard(
    state: AnimalPregnancyState,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    SecondAnimalCard(icon = R.drawable.baseline_event_24, intRes = R.string.pregnancy_history_title) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.history.forEachIndexed { index, p ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = gray_4)
                HistoryItem(
                    p = p,
                    onClick = if (state.canEdit && !state.isSaving) {
                        { onIntent(AnimalPregnancyIntent.OpenEditNote(p.id)) }
                    } else null
                )
                state.dayNotes[p.id]?.takeIf { it.isNotEmpty() }?.let { notes ->
                    DayNoteList(state = state, pregnancy = p, notes = notes, onIntent = onIntent)
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(p: DomainAnimalPregnancy, onClick: (() -> Unit)?) {
    val isBirth = p.status == PregnancyStatus.BIRTH
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardClips(
                colorBackground = if (isBirth) green_g_3 else red_11,
                colorBorder = if (isBirth) green_g_4 else red_15,
                colorText = dark,
                icon = if (isBirth) R.drawable.baseline_favorite_24 else R.drawable.outline_cancel_24,
                colorIcon = if (isBirth) animal_1 else error_base,
                value = p.endDate ?: p.matingDate
            )
            Text(
                text = stringResource(R.string.pregnancy_history_mating, p.matingDate),
                style = text_12,
                color = grey
            )
        }
        Text(text = pregnancySummary(p), style = text_14, color = marengo)
        p.sireName?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = stringResource(R.string.pregnancy_history_father, it),
                style = text_12,
                color = gray_7
            )
        }
        if (p.note.isNotBlank())
            Text(text = p.note, style = text_12, color = gray_7)
    }
}

/** Заметки по дням активной беременности; пока заметок нет — карточки нет (добавление — FAB). */
@Composable
private fun DayNotesCard(
    state: AnimalPregnancyState,
    pregnancy: DomainAnimalPregnancy,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val notes = state.dayNotes[pregnancy.id]?.takeIf { it.isNotEmpty() } ?: return
    SecondAnimalCard(icon = R.drawable.baseline_sticky_note_2_24, intRes = R.string.pregnancy_day_notes_title) {
        DayNoteList(state = state, pregnancy = pregnancy, notes = notes, onIntent = onIntent)
    }
}

/** Заметки новые сверху: три последние, остальные по кнопке; клик — правка дня и текста. */
@Composable
private fun DayNoteList(
    state: AnimalPregnancyState,
    pregnancy: DomainAnimalPregnancy,
    notes: List<DomainAnimalPregnancyNote>,
    onIntent: (AnimalPregnancyIntent) -> Unit
) {
    val mating = PregnancyDates.parseOrNull(pregnancy.matingDate)
    // Три последние заметки (список уже новые сверху), остальные — по кнопке
    ExpandableRows(items = notes, key = pregnancy.id, spacing = 10.dp) { _, note ->
        DayNoteItem(
            note = note,
            day = mating?.let { m -> PregnancyDates.parseOrNull(note.date)?.let { PregnancyCalculator.dayOf(m, it) } },
            onClick = if (state.canEdit && !state.isSaving) {
                { onIntent(AnimalPregnancyIntent.OpenEditDayNote(note.id)) }
            } else null
        )
    }
}

/**
 * Заметка: «День N» на первой строке, дата и время — на второй, справа — карандаш правки.
 * Текст — в [NOTE_COLLAPSED_LINES] строки; длинный раскрывается/сворачивается нажатием на него.
 */
@Composable
private fun DayNoteItem(note: DomainAnimalPregnancyNote, day: Int?, onClick: (() -> Unit)?) {
    var isExpanded by remember(note.id) { mutableStateOf(false) }
    // Пока идёт анимация, текст рисуется целиком и обрезается по анимируемой высоте
    var isAnimating by remember(note.id) { mutableStateOf(false) }
    var isOverflow by remember(note.id, note.note) { mutableStateOf(false) }
    var collapsedHeight by remember(note.id, note.note) { mutableIntStateOf(0) }
    var fullHeight by remember(note.id, note.note) { mutableIntStateOf(0) }
    val height = remember(note.id) { Animatable(0f) }
    LaunchedEffect(isExpanded, collapsedHeight, fullHeight) {
        val target = if (isExpanded) fullHeight else collapsedHeight
        if (target <= 0) return@LaunchedEffect
        if (height.value <= 0f) height.snapTo(target.toFloat())
        else height.animateTo(target.toFloat(), tween(EXPAND_MS))
        isAnimating = false
    }
    // Свёрнуто и анимация закончилась — обычный текст в три строки с многоточием
    val isTruncated = !isExpanded && !isAnimating
    val isExpandable = isOverflow || isExpanded
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(animal_1, CircleShape)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = if (day != null && day >= 1)
                        stringResource(R.string.pregnancy_day_note_day_title, day)
                    else note.date,
                    style = text_14,
                    fontWeight = FontWeight.SemiBold,
                    color = animal_1
                )
                val dateTime = listOfNotNull(
                    note.date.takeIf { day != null && day >= 1 },
                    note.time?.takeIf { it.isNotBlank() }
                ).joinToString(" · ")
                if (dateTime.isNotEmpty())
                    Text(text = dateTime, style = text_12, color = grey)
            }
            if (onClick != null)
                Icon(
                    painter = painterResource(R.drawable.icon_edit),
                    contentDescription = stringResource(R.string.pregnancy_day_note_edit_title),
                    tint = gray_7,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClick)
                        .padding(6.dp)
                        .size(15.dp)
                )
        }
        Column(
            modifier = Modifier
                .padding(start = 18.dp)
                .then(
                    if (isExpandable) Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isAnimating = true
                        isExpanded = !isExpanded
                    }
                    else Modifier
                )
        ) {
            Text(
                text = note.note,
                style = text_14,
                color = dark,
                maxLines = if (isTruncated) NOTE_COLLAPSED_LINES else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { layout ->
                    if (isTruncated) {
                        isOverflow = layout.hasVisualOverflow
                        collapsedHeight = layout.size.height
                    } else {
                        fullHeight = layout.size.height
                        // Высота трёх строк — из свёрнутого текста; отсюда только если её ещё нет
                        if (collapsedHeight == 0 && layout.lineCount > NOTE_COLLAPSED_LINES)
                            collapsedHeight = layout.getLineBottom(NOTE_COLLAPSED_LINES - 1).roundToInt()
                    }
                },
                // Текст меряется без ограничения по высоте (иначе полная высота не узнать),
                // а показывается обрезанным по анимируемой высоте
                modifier = if (isTruncated || height.value <= 0f) Modifier
                else Modifier
                    .clipToBounds()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints.copy(maxHeight = Constraints.Infinity))
                        val visible = height.value.roundToInt().coerceIn(0, placeable.height)
                        layout(placeable.width, visible) { placeable.place(0, 0) }
                    }
            )
            if (isExpandable)
                Text(
                    text = stringResource(if (isExpanded) R.string.button_wrap else R.string.pregnancy_day_note_more),
                    style = text_12,
                    fontWeight = FontWeight.Medium,
                    color = animal_1
                )
        }
    }
}

/** Сколько строк текста заметки видно до раскрытия. */
private const val NOTE_COLLAPSED_LINES = 3

@Composable
private fun InfoBanner(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = orang_3),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(R.drawable.outline_info_24),
                contentDescription = null,
                tint = orang_9,
                modifier = Modifier.size(18.dp)
            )
            Text(text = text, style = text_12, color = dark)
        }
    }
}

/** Как [AnimalParameter], но заголовок — готовая строка, значение переносится. */
@Composable
private fun InfoRow(
    title: String,
    value: String,
    @DrawableRes icon: Int,
    iconColor: Color,
    iconColorSecond: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = iconColorSecond),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .padding(8.dp)
                    .size(14.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, color = grey, style = text_12)
            Text(text = value, color = dark, style = text_12)
        }
    }
}

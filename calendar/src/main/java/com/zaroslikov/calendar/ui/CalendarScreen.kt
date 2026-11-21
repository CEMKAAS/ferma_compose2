package com.zaroslikov.calendar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.calendar.R
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

private val selectedDayGreen = Color(0xFF3F6836)
private const val monthTransitionMillis = 240

private data class CalendarMonthPage(val state: CalendarState, val listState: LazyListState) {
    val month: YearMonth get() = state.month
}

@Composable
internal fun CalendarScreen(
    navigateBack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        viewModel.messages.collect { snackbar.showSnackbar(context.getString(it)) }
    }
    CalendarContent(state, navigateBack, viewModel::onIntent, snackbar)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarContent(
    state: CalendarState,
    navigateBack: () -> Unit,
    onIntent: (CalendarIntent) -> Unit,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    val listState = key(state.month) { rememberLazyListState() }
    val slideDistance = with(LocalDensity.current) { 40.dp.roundToPx() }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.calendar_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.calendar_back))
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(CalendarIntent.ShowFilters) }) {
                        Icon(Icons.Outlined.FilterList, stringResource(R.string.calendar_filters))
                    }
                    IconButton(onClick = { onIntent(CalendarIntent.Create) }) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.calendar_add_event),
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                windowInsets = WindowInsets(0),
            )
        },
    ) { padding ->
        AnimatedContent(
            targetState = CalendarMonthPage(state, listState),
            contentKey = CalendarMonthPage::month,
            modifier = Modifier.fillMaxSize().padding(padding).clipToBounds(),
            transitionSpec = {
                val offset = if (targetState.month > initialState.month) slideDistance else -slideDistance
                val enter = slideInVertically(tween(monthTransitionMillis)) { offset } +
                    fadeIn(tween(monthTransitionMillis))
                val exit = slideOutVertically(tween(monthTransitionMillis)) { -offset } +
                    fadeOut(tween(monthTransitionMillis))
                enter togetherWith exit
            },
            label = "Смена месяца",
        ) { page ->
            val isCurrentMonth = page.month == state.month
            CalendarMonthContent(page.state, page.listState, isCurrentMonth) { intent ->
                if (isCurrentMonth) onIntent(intent)
            }
        }
    }
    CalendarDialogs(state, onIntent)
}

@Composable
private fun CalendarMonthContent(
    state: CalendarState,
    listState: LazyListState,
    isCurrentMonth: Boolean,
    onIntent: (CalendarIntent) -> Unit,
) {
    val month = state.month
    val canGoBack = month.year > calendarYearRange.first || month.monthValue > 1
    val canGoForward = month.year < calendarYearRange.last || month.monthValue < 12
    val categoriesReady = !state.isLoadingCategories && !state.categoriesLoadFailed
    val eventsByDate = remember(state.events, state.categories, categoriesReady) {
        if (categoriesReady) state.events.filter { it.category in state.categories }.groupBy(CalendarEvent::date)
        else emptyMap()
    }
    val monthModifier = if (isCurrentMonth) Modifier else Modifier.clearAndSetSemantics { hideFromAccessibility() }
    LazyColumn(
        modifier = monthModifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = 16.dp),
        userScrollEnabled = isCurrentMonth,
    ) {
        stickyHeader {
            MonthHeading(month, canGoBack, canGoForward) { onIntent(CalendarIntent.MoveMonth(it)) }
        }
        item {
            MonthGrid(state, eventsByDate, canGoBack, canGoForward, onIntent)
        }
        item {
            Text(
                text = state.selectedDate.calendarDayHeading(),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        val dayEvents = eventsByDate[state.selectedDate].orEmpty()
        when {
            state.isLoading || state.isLoadingCategories -> item {
                LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }
            state.loadFailed || state.categoriesLoadFailed -> item {
                val message = if (state.categoriesLoadFailed) R.string.calendar_filter_load_error
                else R.string.calendar_load_error
                val retryLabel = stringResource(R.string.calendar_retry)
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(stringResource(message))
                    if (state.categoriesLoadFailed) {
                        OutlinedButton(onClick = { onIntent(CalendarIntent.RetryFilters) }) {
                            Text(retryLabel)
                        }
                    } else {
                        TextButton(onClick = { onIntent(CalendarIntent.Retry) }) {
                            Text(retryLabel)
                        }
                    }
                }
            }
            dayEvents.isEmpty() -> item {
                Text(
                    text = stringResource(R.string.calendar_empty_day),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> itemsIndexed(dayEvents, key = { _, event -> event.id }) { index, event ->
                val task = event.task
                CalendarEventRow(
                    event = event,
                    saving = task != null && task.key in state.savingTasks,
                    onDetails = { onIntent(CalendarIntent.Details(event)) },
                    onSetCompleted = { completed ->
                        task?.let { onIntent(CalendarIntent.SetTaskCompleted(it.key, completed)) }
                    },
                )
                if (index < dayEvents.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun MonthGrid(
    state: CalendarState,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onIntent: (CalendarIntent) -> Unit,
) {
    val month = state.month
    val firstDay = month.atDay(1).dayOfWeek.value - 1
    val weeks = (firstDay + month.lengthOfMonth() + 6) / 7
    val latestOnIntent by rememberUpdatedState(onIntent)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Column(
        Modifier.fillMaxWidth().pointerInput(month) {
            val threshold = 32.dp.toPx()
            var distance = 0f
            detectVerticalDragGestures(
                onDragStart = { distance = 0f },
                onVerticalDrag = { _, delta -> distance += delta },
                onDragEnd = {
                    when {
                        distance <= -threshold && canGoForward -> latestOnIntent(CalendarIntent.MoveMonth(1))
                        distance >= threshold && canGoBack -> latestOnIntent(CalendarIntent.MoveMonth(-1))
                    }
                },
            )
        },
    ) {
        Row(Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, calendarLocale)
                        .replaceFirstChar { it.titlecase(calendarLocale) },
                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        repeat(weeks) { week ->
            HorizontalDivider(color = dividerColor)
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { column ->
                    val day = week * 7 + column - firstDay + 1
                    if (day in 1..month.lengthOfMonth()) {
                        val date = month.atDay(day)
                        val categories = eventsByDate[date].orEmpty().map(CalendarEvent::category).toSet()
                        DayCell(
                            date = date,
                            selected = date == state.selectedDate,
                            today = date == state.today,
                            categories = categories,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onIntent(CalendarIntent.SelectDate(date))
                            },
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        HorizontalDivider(color = dividerColor)
    }
}

@Composable
private fun MonthHeading(
    month: YearMonth,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onMoveMonth: (Long) -> Unit,
) {
    val previous = stringResource(R.string.calendar_previous_month)
    val next = stringResource(R.string.calendar_next_month)
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
            // Свайп по закреплённому заголовку получает список, даже когда под ним сетка.
            .pointerInput(Unit) {}
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                heading()
                customActions = buildList {
                    if (canGoBack) {
                        add(CustomAccessibilityAction(previous) {
                            onMoveMonth(-1)
                            true
                        })
                    }
                    if (canGoForward) {
                        add(CustomAccessibilityAction(next) {
                            onMoveMonth(1)
                            true
                        })
                    }
                }
            },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = month.calendarMonthName(),
            modifier = Modifier.alignByBaseline(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = month.year.toString(),
            modifier = Modifier.alignByBaseline(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    selected: Boolean,
    today: Boolean,
    categories: Set<CalendarCategory>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val orderedCategories = CalendarCategory.entries.filter { it in categories }
    val labels = orderedCategories.map { stringResource(it.label) }
    val description = if (labels.isEmpty()) date.calendarFullDate()
    else stringResource(R.string.calendar_day_categories, date.calendarFullDate(), labels.joinToString())
    val colors = MaterialTheme.colorScheme
    val showTodayGap = selected && today
    Column(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .selectable(selected = selected, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(32.dp)
                .then(if (showTodayGap) Modifier.background(Color.White, CircleShape) else Modifier)
                .then(if (today) Modifier.border(1.dp, colors.tertiary, CircleShape) else Modifier)
                .then(if (showTodayGap) Modifier.padding(3.dp) else Modifier)
                .then(if (selected) Modifier.background(selectedDayGreen, CircleShape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    selected -> Color.White
                    today -> colors.tertiary
                    else -> colors.onSurface
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (today || selected) FontWeight.Bold else FontWeight.Normal,
            )
        }
        Spacer(Modifier.height(3.dp))
        Row(
            modifier = Modifier.height(4.dp).width((4 + orderedCategories.size * 6).dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            orderedCategories.forEach { category ->
                Box(
                    Modifier.weight(1f).fillMaxHeight().background(category.color, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun CalendarEventRow(
    event: CalendarEvent,
    saving: Boolean,
    onDetails: () -> Unit,
    onSetCompleted: (Boolean) -> Unit,
) {
    val task = event.task
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)
            .clickable(role = Role.Button, onClick = onDetails)
            .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (task == null) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                CalendarEventIcon(event, Modifier.size(24.dp))
            }
        } else {
            val action = if (task.completed) R.string.calendar_reopen_task else R.string.calendar_complete
            IconButton(
                enabled = !saving,
                onClick = { onSetCompleted(!task.completed) },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = if (task.completed) Icons.Outlined.CheckCircle
                    else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = stringResource(action),
                    tint = if (task.completed && !saving) colors.tertiary else LocalContentColor.current,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(event.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(event.label),
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = calendarTimeLabel(event.time),
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.End,
        )
    }
}

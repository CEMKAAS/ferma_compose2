package com.zaroslikov.calendar.ui

import android.os.Build
import android.view.KeyEvent
import android.view.WindowInsets
import android.view.inspector.WindowInspector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.calendar.domain.CalendarDraft
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.calendar.domain.CalendarOccurrence
import com.zaroslikov.calendar.domain.CalendarRepeat
import com.zaroslikov.calendar.domain.CalendarTask
import com.zaroslikov.calendar.domain.CalendarTaskSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.abs

class CalendarScreenTest {
    @get:Rule val compose = createComposeRule()

    private val today = LocalDate.of(2026, 11, 16)
    private val taskDate = today.plusDays(1)
    private val task = CalendarEvent(
        id = "todo-1",
        kind = CalendarEventKind.TODO_TASK,
        title = "Подготовить корм",
        date = taskDate,
        task = CalendarTask(CalendarTaskSource.TODO, 1, false)
    )

    @Test
    fun selectingDayShowsItsEventsAndUpdatesSelection() {
        var state by mutableStateOf(CalendarState(today = today, events = listOf(task)))
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    if (intent is CalendarIntent.SelectDate) {
                        state = state.copy(selectedDate = intent.date)
                    }
                })
            }
        }

        val viewport = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertTrue(abs(day(today).fetchSemanticsNode().boundsInRoot.left - viewport.left) <= 1f)
        assertTrue(abs(day(today.plusDays(6)).fetchSemanticsNode().boundsInRoot.right - viewport.right) <= 1f)
        day(today).assertIsSelected()
        day(taskDate).performClick().assertIsSelected()
        compose.onNodeWithText(task.title).assertIsDisplayed()
        day(today).performClick()
        compose.onNodeWithText(task.title).assertDoesNotExist()
        compose.onNodeWithText("Сегодня").assertDoesNotExist()
        compose.onNodeWithText("Перейти к дате").assertDoesNotExist()
    }

    @Test
    fun swipingGridMovesOnceOnReleaseAndClampsToShortMonth() {
        val january = LocalDate.of(2026, 1, 31)
        val february = LocalDate.of(2026, 2, 28)
        var state by mutableStateOf(CalendarState(today = january))
        val moves = mutableListOf<Long>()
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    if (intent is CalendarIntent.MoveMonth) {
                        moves += intent.delta
                        state = state.copy(selectedDate = state.selectedDate.plusMonths(intent.delta))
                    }
                })
            }
        }

        val januaryCell = day(january.withDayOfMonth(15))
        januaryCell.performTouchInput {
            down(center)
            moveTo(center - Offset(0f, height * 1.5f))
        }
        compose.runOnIdle { assertTrue(moves.isEmpty()) }
        januaryCell.performTouchInput { up() }
        day(february).assertIsSelected()
        day(february.withDayOfMonth(15)).performTouchInput {
            swipe(center, center + Offset(0f, height * 1.5f))
        }
        compose.runOnIdle { assertEquals(listOf(1L, -1L), moves) }
        day(january.withDayOfMonth(28)).assertIsSelected()
    }

    @Test
    fun monthChangeSlidesInBothDirections() {
        val previous = today
        val next = today.plusMonths(1)
        var state by mutableStateOf(CalendarState(today = previous))
        compose.mainClock.autoAdvance = false
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = {})
            }
        }

        val previousTop = day(previous).fetchSemanticsNode().boundsInRoot.top
        compose.runOnIdle { state = state.copy(selectedDate = next) }
        compose.mainClock.advanceTimeBy(96)
        val nextDuringSlide = day(next).fetchSemanticsNode().boundsInRoot.top
        day(previous).assertDoesNotExist()
        compose.mainClock.advanceTimeBy(500)
        assertTrue(nextDuringSlide > day(next).fetchSemanticsNode().boundsInRoot.top)
        day(next).assertIsSelected()

        compose.runOnIdle { state = state.copy(selectedDate = previous) }
        compose.mainClock.advanceTimeBy(96)
        val previousDuringSlide = day(previous).fetchSemanticsNode().boundsInRoot.top
        day(next).assertDoesNotExist()
        assertTrue(previousDuringSlide < previousTop)
        compose.mainClock.advanceTimeBy(500)
        assertEquals(previousTop, day(previous).fetchSemanticsNode().boundsInRoot.top, 1f)
        day(previous).assertIsSelected()
    }

    @Test
    fun returningBeforeExitFinishesResetsMonthScroll() {
        val events = List(20) { index -> task.copy(id = "todo-$index", title = "Задача $index", date = today) }
        var state by mutableStateOf(CalendarState(today = today, events = events))
        compose.mainClock.autoAdvance = false
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = {})
            }
        }

        day(today).assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(10)
        compose.mainClock.advanceTimeByFrame()
        day(today).assertDoesNotExist()

        compose.runOnIdle { state = state.copy(selectedDate = today.plusMonths(1)) }
        compose.mainClock.advanceTimeBy(64)
        compose.runOnIdle { state = state.copy(selectedDate = today) }
        compose.mainClock.advanceTimeBy(500)
        day(today).assertIsDisplayed().assertIsSelected()
    }

    @Test
    fun monthHeadingExposesTalkBackActions() {
        val moves = mutableListOf<Long>()
        compose.setContent {
            MaterialTheme {
                CalendarContent(CalendarState(today = today), navigateBack = {}, onIntent = { intent ->
                    if (intent is CalendarIntent.MoveMonth) moves += intent.delta
                })
            }
        }

        val header = compose.onNodeWithText("Ноябрь", substring = true).fetchSemanticsNode()
        val actions = header.config[SemanticsActions.CustomActions]
        compose.runOnIdle {
            actions.single { it.label == "Предыдущий месяц" }.action()
            actions.single { it.label == "Следующий месяц" }.action()
            assertEquals(listOf(-1L, 1L), moves)
        }
    }

    @Test
    fun swipingEventsScrollsTheListWithoutChangingMonth() {
        val events = List(35) { index -> task.copy(id = "todo-$index", title = "Задача $index", date = today) }
        val moves = mutableListOf<Long>()
        compose.setContent {
            MaterialTheme {
                CalendarContent(CalendarState(today = today, events = events), navigateBack = {}, onIntent = { intent ->
                    if (intent is CalendarIntent.MoveMonth) moves += intent.delta
                })
            }
        }

        val first = compose.onNodeWithText(events.first().title).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val list = compose.onNode(hasScrollToIndexAction())
        val listBounds = list.fetchSemanticsNode().boundsInRoot
        val firstRowTop = first.top - listBounds.top
        list.performTouchInput {
            swipe(Offset(centerX, (firstRowTop + bottom) / 2), Offset(centerX, firstRowTop))
        }
        val afterScroll = compose.onAllNodesWithText(events.first().title).fetchSemanticsNodes().singleOrNull()
        assertTrue(afterScroll == null || afterScroll.boundsInRoot.top < first.top)
        compose.runOnIdle { assertTrue(moves.isEmpty()) }
    }

    @Test
    fun draggingMonthHeadingRevealsEventsOnShortScreens() {
        val event = task.copy(date = today)
        val moves = mutableListOf<Long>()
        compose.setContent {
            MaterialTheme {
                Box(Modifier.fillMaxWidth().height(350.dp)) {
                    CalendarContent(
                        state = CalendarState(today = today, events = listOf(event)),
                        navigateBack = {},
                        onIntent = { intent -> if (intent is CalendarIntent.MoveMonth) moves += intent.delta },
                    )
                }
            }
        }

        val row = compose.onNodeWithText(event.title)
        val heading = compose.onNodeWithText("Ноябрь")
        row.assertIsNotDisplayed()
        repeat(2) {
            heading.performTouchInput {
                swipe(center + Offset(0f, height * 0.25f), center - Offset(0f, height * 1.5f))
            }
        }
        row.assertIsDisplayed()
        compose.runOnIdle { assertTrue(moves.isEmpty()) }
    }

    @Test
    fun togglingTaskFromLeftInEitherDirectionDoesNotOpenDetails() {
        val taskInfo = checkNotNull(task.task)
        var state by mutableStateOf(
            CalendarState(today = today, selectedDate = taskDate, events = listOf(task))
        )
        val requests = mutableListOf<CalendarIntent.SetTaskCompleted>()
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        is CalendarIntent.SetTaskCompleted -> {
                            requests += intent
                            state.copy(savingTasks = setOf(intent.key))
                        }
                        is CalendarIntent.Details -> state.copy(overlay = CalendarOverlay.Details(intent.event))
                        else -> state
                    }
                })
            }
        }

        val complete = compose.onNodeWithContentDescription("Завершить задачу")
        val reopen = compose.onNodeWithContentDescription("Снять выполнение")
        val title = compose.onNodeWithText(task.title, useUnmergedTree = true)
        val completeBounds = complete.fetchSemanticsNode().boundsInRoot
        val touchTarget = with(compose.density) { 48.dp.toPx() }
        assertTrue(abs(completeBounds.width - touchTarget) <= 1f)
        assertTrue(abs(completeBounds.height - touchTarget) <= 1f)
        assertTrue(abs(completeBounds.right - title.fetchSemanticsNode().boundsInRoot.left) <= 1f)
        complete.assertIsEnabled().performTouchInput { click() }
        complete.assertIsNotEnabled().performTouchInput { click() }
        reopen.assertDoesNotExist()
        sheetText(task.title).assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(CalendarIntent.SetTaskCompleted(taskInfo.key, true)), requests)
            state = state.copy(
                events = listOf(task.copy(task = taskInfo.copy(completed = true))),
                savingTasks = emptySet(),
            )
        }

        reopen.assertIsEnabled().performTouchInput { click() }
        reopen.assertIsNotEnabled()
        complete.assertDoesNotExist()
        sheetText(task.title).assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(
                listOf(
                    CalendarIntent.SetTaskCompleted(taskInfo.key, true),
                    CalendarIntent.SetTaskCompleted(taskInfo.key, false),
                ),
                requests,
            )
            state = state.copy(events = listOf(task), savingTasks = emptySet())
        }
        complete.assertIsEnabled()
    }

    @Test
    fun tappingTaskTitleOpensDetailsWithCurrentStatusRepeatAndNote() {
        val scheduledTask = task.copy(
            statusTitle = "В планах",
            repeat = CalendarRepeat.WEEKLY,
            note = "Сначала проверить запасы",
        )
        var state by mutableStateOf(
            CalendarState(today = today, selectedDate = taskDate, events = listOf(scheduledTask)),
        )
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        is CalendarIntent.Details -> state.copy(overlay = CalendarOverlay.Details(intent.event))
                        CalendarIntent.Dismiss -> state.copy(overlay = null)
                        else -> state
                    }
                })
            }
        }

        compose.onNodeWithText(task.title, useUnmergedTree = true).performTouchInput { click() }
        sheetText(task.title).assertIsDisplayed()
        sheetText("Задача Todo").assertIsDisplayed()
        sheetText("В планах").assertIsDisplayed()
        sheetText("Еженедельно").assertIsDisplayed()
        sheetText(scheduledTask.note).assertIsDisplayed()
        compose.runOnIdle {
            state = state.copy(events = listOf(scheduledTask.copy(statusTitle = "Готово")))
        }
        sheetText("Готово").assertIsDisplayed()
        sheetText("Закрыть").performClick()
        sheetText(task.title).assertDoesNotExist()
    }

    @Test
    fun hidingCategoryRemovesItsMarkerAndRow() {
        val personal = CalendarEvent("personal-1", CalendarEventKind.PERSONAL, "Полить теплицу", taskDate)
        var state by mutableStateOf(
            CalendarState(today = today, selectedDate = taskDate, events = listOf(task, personal))
        )
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        CalendarIntent.ShowFilters -> state.copy(overlay = CalendarOverlay.Filters)
                        CalendarIntent.Dismiss -> state.copy(overlay = null)
                        is CalendarIntent.Filter -> state.copy(
                            categories = if (intent.enabled) state.categories + intent.category
                            else state.categories - intent.category,
                        )
                        else -> state
                    }
                })
            }
        }

        val bothCategories = compose.onNodeWithContentDescription(
            "${taskDate.calendarFullDate()}. Задачи Todo и CRM, Свои события",
        )
        bothCategories.assertExists()
        compose.onNodeWithText(task.title).assertExists()
        compose.onNodeWithContentDescription("Фильтры").performClick()
        sheetText("Фильтры").assertIsDisplayed()
        val taskFilter = compose.onNodeWithText("Задачи Todo и CRM")
        taskFilter.assertIsOn().performClick().assertIsOff()
        bothCategories.assertDoesNotExist()
        compose.onNodeWithText("Закрыть").performClick()
        compose.onNodeWithText(task.title).assertDoesNotExist()
        compose.onNodeWithText(personal.title).assertIsDisplayed()
        compose.onNodeWithContentDescription("${taskDate.calendarFullDate()}. Свои события").assertExists()
        compose.onNodeWithContentDescription("Фильтры").performClick()
        taskFilter.assertIsOff()
    }

    @Test
    fun creatingEventUsesSelectedDayAndSavesWithKeyboardOpen() {
        assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        var state by mutableStateOf(CalendarState(today = today, selectedDate = taskDate))
        var saved: CalendarDraft? = null
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        CalendarIntent.Create -> state.copy(
                            overlay = CalendarOverlay.Edit(CalendarDraft("", state.selectedDate)),
                        )
                        is CalendarIntent.UpdateDraft -> state.copy(overlay = CalendarOverlay.Edit(intent.draft))
                        CalendarIntent.Save -> {
                            saved = (state.overlay as CalendarOverlay.Edit).draft
                            state.copy(overlay = null)
                        }
                        CalendarIntent.Dismiss -> state.copy(overlay = null)
                        else -> state
                    }
                })
            }
        }

        val add = compose.onNodeWithContentDescription("Добавить событие")
        val filter = compose.onNodeWithContentDescription("Фильтры")
        val addY = add.fetchSemanticsNode().boundsInRoot.center.y
        val filterY = filter.fetchSemanticsNode().boundsInRoot.center.y
        assertTrue(abs(addY - filterY) <= 1f)
        add.performClick()
        sheetText("Новое событие").assertIsDisplayed()
        sheetText(taskDate.calendarFullDate()).assertIsDisplayed()

        val title = compose.onNode(hasSetTextAction() and hasText("Название"))
        title.performTouchInput { click() }
        title.assertIsFocused().performTextInput("Посев")
        waitForKeyboard()
        compose.onNodeWithText("Отмена").assertIsDisplayed()
        compose.onNodeWithText("Сохранить").assertIsDisplayed().assertIsEnabled().performTouchInput { click() }
        compose.runOnIdle { assertEquals(CalendarDraft("Посев", taskDate), saved) }
        compose.onNodeWithText("Новое событие").assertDoesNotExist()

        add.performClick()
        title.performTouchInput { click() }
        title.assertIsFocused().performTextInput("Черновик")
        waitForKeyboard()
        sheetText("Отмена").assertIsDisplayed().performTouchInput { click() }
        sheetText("Новое событие").assertDoesNotExist()
        compose.runOnIdle { assertEquals(CalendarDraft("Посев", taskDate), saved) }
    }

    @Test
    fun tappingOrdinaryEventOpensReadOnlyDetailsSheet() {
        val event = CalendarEvent(
            id = "birth-1",
            kind = CalendarEventKind.BIRTH,
            title = "Роды коровы",
            date = today,
            time = LocalTime.of(9, 15),
            note = "Осмотреть телёнка",
        )
        var state by mutableStateOf(CalendarState(today = today, events = listOf(event)))
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        is CalendarIntent.Details -> state.copy(overlay = CalendarOverlay.Details(intent.event))
                        CalendarIntent.Dismiss -> state.copy(overlay = null)
                        else -> state
                    }
                })
            }
        }

        compose.onNodeWithText(event.title).performTouchInput { click() }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss)).assertIsDisplayed()
        sheetText(event.title).assertIsDisplayed()
        sheetText("Роды животного").assertIsDisplayed()
        sheetText("${today.calendarFullDate()} · 09:15").assertIsDisplayed()
        sheetText(event.note).assertIsDisplayed()
        compose.onNodeWithText("Изменить").assertDoesNotExist()
        compose.onNodeWithText("Удалить").assertDoesNotExist()
        sheetText("Закрыть").performClick()
        sheetText(event.title).assertDoesNotExist()
    }

    @Test
    fun personalEventActionsWrapAndRemainClickableInNarrowSheet() {
        val draft = CalendarDraft("Посев ".repeat(300).trimEnd(), today)
        val occurrence = CalendarOccurrence(1, today, draft)
        val event = CalendarEvent("personal-1", CalendarEventKind.PERSONAL, draft.title, today, occurrence = occurrence)
        var selectedAction: CalendarIntent? = null
        compose.setContent {
            val typography = MaterialTheme.typography
            MaterialTheme(typography = typography.copy(labelLarge = typography.labelLarge.copy(fontSize = 32.sp))) {
                CalendarDialogs(
                    state = CalendarState(today = today, overlay = CalendarOverlay.Details(event)),
                    onIntent = { selectedAction = it },
                )
            }
        }

        val close = sheetText("Закрыть").assertIsDisplayed()
        val delete = sheetText("Удалить").assertIsDisplayed()
        val edit = sheetText("Изменить").assertIsDisplayed()
        val actionTopPositions = listOf(close, delete, edit)
            .map { it.fetchSemanticsNode().boundsInRoot.top }
        assertTrue(actionTopPositions.distinct().size > 1)
        close.performTouchInput { click() }
        compose.runOnIdle { assertEquals(CalendarIntent.Dismiss, selectedAction) }
        delete.performTouchInput { click() }
        compose.runOnIdle { assertEquals(CalendarIntent.Delete, selectedAction) }
        edit.performTouchInput { click() }
        compose.runOnIdle { assertEquals(CalendarIntent.Edit, selectedAction) }
    }

    @Test
    fun editingFromDetailsPopulatesExistingDraftAndCancelClosesEditor() {
        val draft = CalendarDraft("Посев", today)
        val occurrence = CalendarOccurrence(1, today, draft)
        val event = CalendarEvent("personal-1", CalendarEventKind.PERSONAL, draft.title, today, occurrence = occurrence)
        var state by mutableStateOf(CalendarState(today = today, events = listOf(event)))
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        is CalendarIntent.Details -> state.copy(overlay = CalendarOverlay.Details(intent.event))
                        CalendarIntent.Edit -> state.copy(overlay = CalendarOverlay.Edit(draft, occurrence))
                        CalendarIntent.Dismiss -> state.copy(overlay = null)
                        else -> state
                    }
                })
            }
        }

        compose.onNodeWithText(event.title).performClick()
        compose.onNodeWithText("Изменить").performClick()
        sheetText("Изменить событие").assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText(draft.title)).assertIsDisplayed()
        compose.onNodeWithText("Отмена").performClick()
        compose.onNodeWithText("Изменить событие").assertDoesNotExist()
    }

    @Test
    fun datedRepeatRequiresEndDateOnOrAfterStart() {
        var state by mutableStateOf(
            CalendarState(today = today, overlay = CalendarOverlay.Edit(CalendarDraft("Посев", today)))
        )
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    val editor = state.overlay as CalendarOverlay.Edit
                    val updated = when (intent) {
                        is CalendarIntent.UpdateDraft -> editor.copy(draft = intent.draft)
                        is CalendarIntent.ChangeEndMode -> editor.copy(endsOnDate = intent.endsOnDate)
                        else -> editor
                    }
                    state = state.copy(overlay = updated)
                })
            }
        }

        compose.onNodeWithText("Без повтора").performScrollTo().performClick()
        compose.onNodeWithText("Еженедельно").performClick()
        val save = compose.onNodeWithText("Сохранить")
        save.assertIsEnabled()
        compose.onNodeWithText("В дату").performScrollTo().performClick()
        save.assertIsNotEnabled()
        compose.onNodeWithText("Выберите дату").performScrollTo().performClick()
        compose.onNodeWithText("ОК").performClick()
        save.assertIsEnabled()
        assertEquals(today, (state.overlay as CalendarOverlay.Edit).draft.repeatUntil)

        sheetText("Дата").performScrollTo().performClick()
        compose.onNode(
            hasText("17 ноября", substring = true) and hasClickAction() and hasAnyAncestor(isDialog()),
        ).performScrollTo().performClick()
        compose.onNodeWithText("ОК").performClick()
        sheetText("Дата окончания должна быть не раньше начала серии").performScrollTo().assertIsDisplayed()
        save.assertIsNotEnabled()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun savingPreventsClosingEditor() {
        var state by mutableStateOf(CalendarState(today = today, overlay = CalendarOverlay.Edit(CalendarDraft("Посев", today))))
        compose.setContent {
            MaterialTheme {
                CalendarContent(state = state, navigateBack = {}, onIntent = { intent ->
                    state = when (intent) {
                        CalendarIntent.Save -> state.copy(savingEvent = true)
                        CalendarIntent.Dismiss -> state.copy(overlay = null)
                        else -> state
                    }
                })
            }
        }

        val save = sheetText("Сохранить")
        save.assertIsEnabled().performClick()
        save.assertIsNotEnabled()
        sheetText("Отмена").assertIsNotEnabled()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        sheetText("Новое событие").assertIsDisplayed()

        compose.runOnIdle { state = state.copy(savingEvent = false) }
        sheetText("Отмена").assertIsEnabled()
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.waitUntilDoesNotExist(sheetTextMatcher("Новое событие"), timeoutMillis = 5_000)
    }

    private fun day(date: LocalDate) =
        compose.onNodeWithContentDescription(date.calendarFullDate(), substring = true)

    private fun sheetText(text: String) = compose.onNode(sheetTextMatcher(text))

    private fun sheetTextMatcher(text: String) =
        hasText(text) and hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.PaneTitle))

    private fun waitForKeyboard() {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.runOnUiThread {
                WindowInspector.getGlobalWindowViews().any {
                    it.rootWindowInsets?.isVisible(WindowInsets.Type.ime()) == true
                }
            }
        }
    }
}

package com.zaroslikov.calendar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.calendar.R
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarDraft
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarExternalSource
import com.zaroslikov.calendar.domain.CalendarRepeat
import com.zaroslikov.calendar.domain.CalendarRepository
import com.zaroslikov.calendar.domain.CalendarTaskKey
import com.zaroslikov.calendar.domain.eventsInMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
internal class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CalendarRepository,
    private val externalSource: CalendarExternalSource,
) : ViewModel() {
    private val projectId: Long = checkNotNull(savedStateHandle[calendarProjectArg])
    private val _state = MutableStateFlow(
        CalendarState(today = LocalDate.now(), isLoading = true, isLoadingCategories = true)
    )
    val state = _state.asStateFlow()
    private val _messages = Channel<Int>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    private var observingMonth: Job? = null
    private var observingCategories: Job? = null

    init {
        observeMonth(_state.value.month)
        observeCategories()
    }

    fun onIntent(intent: CalendarIntent) {
        val current = _state.value
        if (current.savingEvent) return
        when (intent) {
            CalendarIntent.Retry -> observeMonth(current.month)
            CalendarIntent.ShowFilters -> show(CalendarOverlay.Filters)
            CalendarIntent.RetryFilters -> if (current.categoriesLoadFailed) observeCategories()
            CalendarIntent.Dismiss -> show(null)
            CalendarIntent.Create -> show(CalendarOverlay.Edit(CalendarDraft("", current.selectedDate)))
            CalendarIntent.Edit -> prepareChange(delete = false)
            CalendarIntent.Delete -> prepareChange(delete = true)
            CalendarIntent.ConfirmDelete -> (current.overlay as? CalendarOverlay.Delete)?.let {
                delete(it.event, singleOccurrence = false)
            }
            CalendarIntent.Save -> save()
            is CalendarIntent.MoveMonth -> selectDate(current.selectedDate.plusMonths(intent.delta))
            is CalendarIntent.SelectDate -> selectDate(intent.date)
            is CalendarIntent.Filter -> setCategory(intent.category, intent.enabled)
            is CalendarIntent.Details -> show(CalendarOverlay.Details(intent.event))
            is CalendarIntent.ChooseScope -> (current.overlay as? CalendarOverlay.Scope)?.let {
                if (it.delete) delete(it.event, intent.singleOccurrence)
                else openEditor(it.event, intent.singleOccurrence)
            }
            is CalendarIntent.UpdateDraft -> edit {
                copy(draft = intent.draft, endsOnDate = intent.draft.repeat != CalendarRepeat.NONE && endsOnDate)
            }
            is CalendarIntent.ChangeEndMode -> edit {
                copy(endsOnDate = intent.endsOnDate, draft = draft.copy(repeatUntil = null))
            }
            is CalendarIntent.SetTaskCompleted -> setTaskCompleted(intent.key, intent.completed)
        }
    }

    private fun observeMonth(month: YearMonth) {
        observingMonth?.cancel()
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        observingMonth = viewModelScope.launch {
            combine(
                repository.observeEvents(projectId, month),
                externalSource.observe(projectId),
            ) { personal, external ->
                withContext(Dispatchers.Default) {
                    (personal + external.eventsInMonth(month)).sortedWith(eventOrder)
                }
            }.catch {
                _state.update { it.copy(isLoading = false, loadFailed = true) }
            }.collect { events ->
                _state.update {
                    it.copy(events = events, isLoading = false, loadFailed = false)
                }
            }
        }
    }

    private fun observeCategories() {
        observingCategories?.cancel()
        _state.update {
            it.copy(isLoadingCategories = it.isLoadingCategories || it.categoriesLoadFailed, categoriesLoadFailed = false)
        }
        observingCategories = viewModelScope.launch {
            repository.observeCategories(projectId)
                .catch {
                    _state.update { it.copy(isLoadingCategories = false, categoriesLoadFailed = true) }
                }
                .collect { categories ->
                    _state.update { it.copy(categories = categories, isLoadingCategories = false) }
                }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (date.year !in calendarYearRange) return
        val monthChanged = YearMonth.from(date) != _state.value.month
        _state.update {
            it.copy(
                today = LocalDate.now(),
                selectedDate = date,
                events = if (monthChanged) emptyList() else it.events,
            )
        }
        if (monthChanged) observeMonth(YearMonth.from(date))
    }

    private fun show(overlay: CalendarOverlay?) = _state.update { it.copy(overlay = overlay) }

    private fun prepareChange(delete: Boolean) {
        val details = _state.value.overlay as? CalendarOverlay.Details ?: return
        val occurrence = details.event.occurrence ?: return
        when {
            occurrence.series.repeat != CalendarRepeat.NONE -> show(CalendarOverlay.Scope(details.event, delete))
            delete -> show(CalendarOverlay.Delete(details.event))
            else -> openEditor(details.event, singleOccurrence = false)
        }
    }

    private fun openEditor(event: CalendarEvent, singleOccurrence: Boolean) {
        val occurrence = event.occurrence ?: return
        val draft = if (singleOccurrence) {
            CalendarDraft(event.title, event.date, event.time, event.note)
        } else {
            occurrence.series
        }
        show(CalendarOverlay.Edit(draft, occurrence, singleOccurrence))
    }

    private fun edit(transform: CalendarOverlay.Edit.() -> CalendarOverlay.Edit) {
        val editor = _state.value.overlay as? CalendarOverlay.Edit ?: return
        show(editor.transform().copy(saveFailed = false))
    }

    private fun save() {
        val editor = _state.value.overlay as? CalendarOverlay.Edit ?: return
        if (editor.draft.title.isBlank() || editor.endError != null) return

        val occurrence = editor.occurrence
        val draft = editor.draft.copy(
            title = editor.draft.title.trim(),
            repeatUntil = editor.draft.repeatUntil.takeIf { editor.showsRepeatEnd && editor.endsOnDate },
        )
        _state.update { it.copy(savingEvent = true, overlay = editor.copy(saveFailed = false)) }
        viewModelScope.launch {
            try {
                if (editor.singleOccurrence && occurrence != null) {
                    repository.saveOccurrence(projectId, occurrence, draft)
                } else {
                    repository.save(projectId, occurrence?.seriesId, draft)
                }
                show(null)
                if (editor.singleOccurrence || occurrence == null || occurrence.series.date != draft.date) {
                    selectDate(draft.date)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                show(editor.copy(saveFailed = true))
            } finally {
                _state.update { it.copy(savingEvent = false) }
            }
        }
    }

    private fun delete(event: CalendarEvent, singleOccurrence: Boolean) {
        val occurrence = event.occurrence ?: return
        _state.update { it.copy(savingEvent = true) }
        viewModelScope.launch {
            try {
                if (singleOccurrence) repository.deleteOccurrence(projectId, occurrence)
                else repository.delete(projectId, occurrence.seriesId)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _messages.trySend(R.string.calendar_delete_error)
            } finally {
                _state.update { it.copy(savingEvent = false, overlay = null) }
            }
        }
    }

    private fun setCategory(category: CalendarCategory, enabled: Boolean) {
        val current = _state.value
        if (current.isLoadingCategories || current.categoriesLoadFailed ||
            category in current.savingCategories || (category in current.categories) == enabled
        ) return

        // После записи повторно читаем проект из БД, чтобы старый снимок не отменил выбор.
        observingCategories?.cancel()
        _state.update {
            it.copy(
                categories = if (enabled) it.categories + category else it.categories - category,
                savingCategories = it.savingCategories + category,
            )
        }
        viewModelScope.launch {
            try {
                repository.setCategoryEnabled(projectId, category, enabled)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.update {
                    it.copy(categories = if (enabled) it.categories - category else it.categories + category)
                }
                _messages.trySend(R.string.calendar_filter_error)
            } finally {
                _state.update { it.copy(savingCategories = it.savingCategories - category) }
                if (_state.value.savingCategories.isEmpty()) observeCategories()
            }
        }
    }

    private fun setTaskCompleted(key: CalendarTaskKey, completed: Boolean) {
        val current = _state.value
        if (key in current.savingTasks) return
        val task = current.events.firstNotNullOfOrNull { it.task?.takeIf { task -> task.key == key } } ?: return
        if (task.completed == completed) return
        _state.update { it.copy(savingTasks = it.savingTasks + key) }
        viewModelScope.launch {
            try {
                externalSource.setTaskCompleted(projectId, key, completed)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _messages.trySend(R.string.calendar_complete_error)
            } finally {
                _state.update { it.copy(savingTasks = it.savingTasks - key) }
            }
        }
    }

    private companion object {
        val eventOrder = compareBy(CalendarEvent::time, CalendarEvent::title, CalendarEvent::id)
    }
}

package com.zaroslikov.fermacompose2.data.calendar

import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.calendar.domain.CalendarExternalSource
import com.zaroslikov.calendar.domain.CalendarRepeat
import com.zaroslikov.calendar.domain.CalendarTask
import com.zaroslikov.calendar.domain.CalendarTaskKey
import com.zaroslikov.calendar.domain.CalendarTaskSource
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmTaskRepository
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.RecyclingRepository
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class FarmCalendarExternalSource @Inject constructor(
    private val todoTasks: TodoTaskRepository,
    private val crmTasks: CrmTaskRepository,
    private val crmClients: CrmClientRepository,
    private val animals: AnimalRepository,
    private val vaccinations: AnimalVaccinationRepository,
    private val pregnancies: AnimalPregnancyRepository,
    private val recycling: RecyclingRepository,
    private val expenses: ExpensesRepository
) : CalendarExternalSource {
    override fun observe(projectId: Long): Flow<List<CalendarEvent>> = combine(
        tasks(projectId),
        animalEvents(projectId),
        birthdays(projectId),
        recycling.getRecyclingList(projectId).map(::recyclingEvents),
        expenses.getProjectFeedWithEndDate(projectId).map(::feedEvents)
    ) { tasks, animals, birthdays, recycling, feed ->
        tasks + animals + birthdays + recycling + feed
    }.distinctUntilChanged().flowOn(Dispatchers.Default)

    override suspend fun setTaskCompleted(projectId: Long, key: CalendarTaskKey, completed: Boolean) {
        when (key.source) {
            CalendarTaskSource.TODO -> todoTasks.setTaskCompleted(projectId, key.id, completed)
            CalendarTaskSource.CRM -> crmTasks.setTaskCompleted(projectId, key.id, completed)
        }
    }

    private fun tasks(projectId: Long): Flow<List<CalendarEvent>> = combine(
        todoTasks.getProjectTasks(projectId),
        crmTasks.getCalendarTasks(projectId)
    ) { todo, crm ->
        todo.mapNotNull { entry ->
            val task = entry.item.task
            val date = calendarDate(task.dueDate) ?: return@mapNotNull null
            val status = entry.item.status
            val repeat = when (task.repeatPeriod) {
                TodoRepeatPeriod.NONE -> null
                TodoRepeatPeriod.DAY -> CalendarRepeat.DAILY
                TodoRepeatPeriod.WEEK -> CalendarRepeat.WEEKLY
                TodoRepeatPeriod.MONTH -> CalendarRepeat.MONTHLY
                TodoRepeatPeriod.YEAR -> CalendarRepeat.YEARLY
            }
            CalendarEvent(
                id = "todo:${task.id}",
                kind = CalendarEventKind.TODO_TASK,
                title = task.title,
                date = date,
                time = calendarTime(task.dueTime),
                note = eventNote(entry.listTitle, entry.parentTitle, task.note),
                task = CalendarTask(CalendarTaskSource.TODO, task.id, status?.isDone == true),
                statusTitle = status?.title?.takeIf(String::isNotBlank),
                repeat = repeat,
                repeatUntil = repeat?.let { calendarDate(task.repeatEndDate) },
            )
        } + crm.mapNotNull { task ->
            val date = calendarDate(task.dueDate) ?: return@mapNotNull null
            CalendarEvent(
                id = "crm:${task.id}",
                kind = CalendarEventKind.CRM_TASK,
                title = task.title,
                date = date,
                note = task.note,
                task = CalendarTask(CalendarTaskSource.CRM, task.id, task.isDone)
            )
        }
    }

    private fun animalEvents(projectId: Long): Flow<List<CalendarEvent>> = combine(
        animals.getAllAnimalWithGroups(projectId).map { rows ->
            rows.associate { it.id to CalendarAnimal(it.name, it.isArchive) }
        }.distinctUntilChanged(),
        vaccinations.getProjectVaccinations(projectId),
        pregnancies.getProjectBirths(projectId)
    ) { animals, vaccinations, pregnancies ->
        vaccinationEvents(vaccinations, animals) + birthEvents(pregnancies, animals)
    }

    private fun birthdays(projectId: Long): Flow<List<CalendarEvent>> = combine(
        todoTasks.getProjectAssigneeProfiles(projectId),
        crmClients.getClients(projectId)
    ) { assignees, clients ->
        assignees.mapNotNull {
            birthdayEvent("assignee:${it.name}", it.profile.displayName.ifBlank { it.name }, it.profile.birthday)
        } + clients.mapNotNull {
            birthdayEvent("client:${it.id}", it.name, it.birthDate)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface FarmCalendarModule {
    @Binds
    fun bindExternalSource(implementation: FarmCalendarExternalSource): CalendarExternalSource
}

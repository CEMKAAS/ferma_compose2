package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.project.DomainTimeNotificationProject
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.TimeNotificationProjectRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepository
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.NotificationParameters
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditIntent
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditReduce
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarehouseEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,
    private val timeNotificationProjectRepository: TimeNotificationProjectRepository,
    private val workManagerRepository: WorkManagerRepository,   private val yandexMetricRepository: YandexMetricRepository
) : BaseViewModel2<WarehouseEditState, WarehouseEditIntent, WarehouseEditReduce>(
    WarehouseEditState(),
    WarehouseEditReduce()
) {

    private val itemId: Long = checkNotNull(savedStateHandle[WarehouseEditDestination.itemIdArg])

    init {
        updateState { it.copy(isInsertProject = itemId == -1L) }
        if (itemId != -1L) loadData(itemId)
    }

    fun onIntent(intent: WarehouseEditIntent) {
        sendIntent(intent)
        return when (intent) {
            WarehouseEditIntent.InsertClicked -> insertProject()
            WarehouseEditIntent.EditClicked -> editProject()
            else -> Unit
        }
    }

    private fun loadData(itemId: Long) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val project = projectRepository.getProject(itemId).first()
            val settings = settingsRepository.getSettings(itemId).first()
            val timeNotification =
                timeNotificationProjectRepository.getTimeNotificationByProjectId(itemId).first()
            updateState { state ->
                state.copy(
                    isLoading = false,
                    nameProject = project.title,
                    dateProject = project.date,
                    currentIcon = project.currentIcon ?: R.drawable.livestock,
                    imagePath = project.imagePath,
                    isShowNotification = timeNotification.isNotEmpty(),
                    currentProject = project,
                    currentSettings = settings,
                    notificationList = timeNotification.map { it.toUi() },
                    hasAnyError = true,
                    idPT = itemId
                )
            }
        }
    }

    private fun insertProject() {
        viewModelScope.launch {
            val id = projectRepository.insertProjectLong(getState().toDomainProjectTable())
            settingsRepository.insertSettings(getState().toDomainSettings(idPT = id))
            if (getState().isShowNotification)
                getState().notificationList.filter { it.isVisibility }.forEach {
                    timeNotificationProjectRepository.insertTimeNotification(it.toDomain(id))
                }
            updateNotifications()
            yandexMetricRepository.metricalProject(getState())
            navigateTo(UiEvent.NavigateBack)
        }
    }

    private fun editProject() {
        viewModelScope.launch {
            projectRepository.updateProject(getState().toDomainProjectTable(itemId))
            settingsRepository.updateSettings(getState().toDomainSettings())
            if (getState().isShowNotification) {
                getState().notificationList.forEach {
                    when {
                        it.id == 0L && it.isVisibility ->
                            timeNotificationProjectRepository.insertTimeNotification(
                                it.toDomain(newProjectId = itemId)
                            )

                        it.id != 0L && it.isVisibility ->
                            timeNotificationProjectRepository.updateTimeNotification(it.toDomain())

                        it.id != 0L && !it.isVisibility ->
                            timeNotificationProjectRepository.deleteTimeNotificationById(it.id)

                        else -> Unit
                    }
                }
            } else {
                getState().notificationList.forEach {
                    timeNotificationProjectRepository.deleteTimeNotificationById(it.id)
                }
            }
            updateNotifications()
            navigateTo(UiEvent.NavigateBack)
        }
    }

    suspend fun updateNotifications() {
        timeNotificationProjectRepository.getTimeNotificationInAllActiveProject()
            .first()
            .let { list ->
                workManagerRepository.cancelProjectNotification()
                list.forEach { item ->
                    workManagerRepository.scheduleReminderProject(
                        name = item.nameProject,
                        time = item.time,
                        note = item.note,
                        projectId = item.projectId
                    )
                }
            }
    }

    private fun WarehouseEditState.toDomainProjectTable(id: Long? = null): DomainProjectTable {
        return DomainProjectTable(
            id = id ?: 0,
            title = currentProject.title,
            archive = currentProject.archive,
            date = currentProject.date,
            dateEnd = currentProject.dateEnd,
            mode = true,
            currentIcon = getState().currentIcon,
            imagePath = getState().imagePath
        )
    }

    private fun WarehouseEditState.toDomainSettings(
        idPT: Long? = null
    ): DomainSettings {
        return DomainSettings(
            id = this.currentSettings.id,
            currencySuffix = this.currentSettings.currencySuffix,
            weightSuffix = this.currentSettings.weightSuffix,
            volumeSuffix = this.currentSettings.volumeSuffix,
            linearSuffix = this.currentSettings.linearSuffix,
            idPT = idPT ?: this.currentSettings.idPT
        )
    }

    private fun NotificationParameters.toDomain(newProjectId: Long? = null): DomainTimeNotificationProject {
        return DomainTimeNotificationProject(
            id = id,
            time = time,
            note = note.ifBlank { null },
            projectId = newProjectId ?: bookmarkId
        )
    }

    private fun DomainTimeNotificationProject.toUi(): NotificationParameters {
        return NotificationParameters(
            id = id,
            time = time,
            note = note ?: "",
            isEntry = false,
            isVisibility = true,
            bookmarkId = projectId
        )
    }
}
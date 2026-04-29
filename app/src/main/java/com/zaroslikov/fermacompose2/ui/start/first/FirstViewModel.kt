package com.zaroslikov.fermacompose2.ui.start.first

import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.TimeNotificationIncubatorRepository
import com.zaroslikov.domain.repository.TimeNotificationProjectRepository
import com.zaroslikov.fermacompose2.BuildConfig
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepository
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.navigation.UiNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val workManagerRepository: WorkManagerRepository,
    private val timeNotificationIncubatorRepository: TimeNotificationIncubatorRepository,
    private val timeNotificationProjectRepository: TimeNotificationProjectRepository,
    private val appSettingsRepository: AppSettingsRepository
) : BaseViewModel2<FirstState, FirstIntent, FirstReducer>(
    FirstState(),
    FirstReducer()
) {
    private val _notification = MutableSharedFlow<UiNotification>()
    val notification = _notification.asSharedFlow()

    init {
        loadData()
        launchNotification()
    }

    private fun launchNotification() {
        viewModelScope.launch {
            launchNotificationForIncubator()
            launchNotificationForProject()
        }
    }

    suspend fun launchNotificationForIncubator() {
        val isIncubatorWorker = workManagerRepository.checkAndStartWorkers(true)
        val timeNotificationList =
            timeNotificationIncubatorRepository.getTimeNotificationInAllActiveBookmark().first()
        if (isIncubatorWorker && timeNotificationList.isNotEmpty()) {
            timeNotificationList.forEach { item ->
                workManagerRepository.scheduleReminderIncubator(
                    name = item.nameBookmark,
                    time = item.time,
                    bookmarkId = item.bookmarkId,
                    note = item.note,
                    projectId = item.projectId
                )
            }
            showMessage("Уведомления у инкубатора перезапущены")
        }
    }

    suspend fun launchNotificationForProject() {
        val isIncubatorWorker = workManagerRepository.checkAndStartWorkers(false)
        val timeNotificationList =
            timeNotificationProjectRepository.getTimeNotificationInAllActiveProject().first()
        if (isIncubatorWorker && timeNotificationList.isNotEmpty()) {
            timeNotificationList.forEach { item ->
                workManagerRepository.scheduleReminderProject(
                    name = item.nameProject,
                    time = item.time,
                    note = item.note,
                    projectId = item.projectId
                )
            }
            showMessage("Уведомления у проекта перезапущены")
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            combine(
                appSettingsRepository.getAppSettings(),
                projectRepository.getAllProject()
            ) { appSettings, projects ->
                updateLastVersion(appSettings) to projects
            }.collectLatest { (appSettings, baseList) ->
                val list = baseList.filter { !it.archive }
                val archiveList = baseList.filter { it.archive }
                updateState {
                    it.copy(
                        isLoading = false,
                        appSettings = appSettings,
                        list = list,
                        archiveList = archiveList
                    )
                }
            }
        }
    }

    fun onIntent(intent: FirstIntent) {
        sendIntent(intent)
        return when (intent) {
            is FirstIntent.DeleteClicked -> deleteProject()
            is FirstIntent.ArchiveClicked -> archiveProject(intent.value)
            is FirstIntent.UnarchiveClicked -> unarchiveProject(intent.value)
            is FirstIntent.SkipTrainingClicked -> updateFirstLaunch()
            else -> Unit
        }
    }

    private fun unarchiveProject(domainProjectTable: DomainProjectTable) {
        viewModelScope.launch {
            projectRepository.updateProject(domainProjectTable.copy(archive = false))
            if (domainProjectTable.mode) updateProjectNotifications() else updateIncubatorNotifications()
        }
    }

    private fun archiveProject(domainProjectTable: DomainProjectTable?) {
        viewModelScope.launch {
            val domain = domainProjectTable ?: getState().currentProjectTable
            domain?.let {
                projectRepository.updateProject(it.copy(archive = true))
                val bookmark = bookmarkRepository.getActivityBookmarkByIdPT(it.id).first()
                bookmark?.let {
                    bookmarkRepository.update(
                        bookmark.copy(
                            isActivityBookmark = false, endDate = dateToday(),
                            isEarlyCompletionStatus = true,
                            rejectedCount = bookmark.count
                        )
                    )
                }
                if (it.mode) updateProjectNotifications() else updateIncubatorNotifications()
                sendIntent(FirstIntent.OpenArchiveIncubatorBottomSheetClicked(false))
            }
        }
    }

    private fun deleteProject() {
        viewModelScope.launch {
            getState().currentProjectTable?.let {
                projectRepository.deleteProject(it)
                if (it.mode) updateProjectNotifications() else updateIncubatorNotifications()
                sendIntent(
                    FirstIntent.OpenDeleteBottomSheetClicked(false, null)
                )
            }
        }
    }

    suspend fun updateProjectNotifications() {
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

    suspend fun updateIncubatorNotifications() {
        timeNotificationIncubatorRepository.getTimeNotificationInAllActiveBookmark()
            .first()
            .let { list ->
                workManagerRepository.cancelProjectNotification()
                list.forEach { item ->
                    workManagerRepository.scheduleReminderIncubator(
                        name = item.nameBookmark,
                        time = item.time,
                        note = item.note,
                        projectId = item.projectId,
                        bookmarkId = item.bookmarkId
                    )
                }
            }
    }


    private suspend fun updateLastVersion(appSettings: DomainAppSettings): DomainAppSettings {
        val currentVersionApp = BuildConfig.VERSION_NAME

        return if (currentVersionApp != appSettings.currentVersionApp) {
            val newAppSettings = appSettings.copy(
                lastVersionApp = appSettings.currentVersionApp,
                currentVersionApp = currentVersionApp
            )
            updateSettings(newAppSettings)
            newAppSettings
        } else appSettings
    }

    private fun updateFirstLaunch() {
        viewModelScope.launch {
            Log.i("app_settings", "updateFirstLaunch_1:${getState().appSettings} ")
            updateSettings(
                domainAppSettings = getState().appSettings.copy(
                    isFirstLaunch = false
                )
            )
            Log.i("app_settings", "updateFirstLaunch_1:${getState().appSettings} ")
            _notification.emit(UiNotification.Notification)
        }
    }

    private suspend fun updateSettings(domainAppSettings: DomainAppSettings? = null) {
        appSettingsRepository.updateAppSettings(domainAppSettings ?: getState().appSettings)
    }
}
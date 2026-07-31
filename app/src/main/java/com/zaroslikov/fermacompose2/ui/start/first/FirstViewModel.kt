package com.zaroslikov.fermacompose2.ui.start.first

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
import com.zaroslikov.fermacompose2.utils.QrCodeDecoder
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import java.util.UUID

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val qrNavigationManager: QrNavigationManager,
    private val projectRepository: ProjectRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val workManagerRepository: WorkManagerRepository,
    private val timeNotificationIncubatorRepository: TimeNotificationIncubatorRepository,
    private val timeNotificationProjectRepository: TimeNotificationProjectRepository,
    private val appSettingsRepository: AppSettingsRepository,
) : BaseViewModel2<FirstState, FirstIntent, FirstReducer>(
    FirstState(),
    FirstReducer()
) {

    private val _notification = MutableSharedFlow<UiNotification>()
    val notification = _notification.asSharedFlow()

    init {
        loadData()
        launchNotification()
        loadTemplate()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            val template = qrNavigationManager.consume()
            Log.i("template", "loadDataForTemplateBottomSheet-template: $template ")
            if (template != null) findProject(template)
        }
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
            is FirstIntent.QrCodeScanner -> qrScanner(intent.value)
            is FirstIntent.ChoiceProjectForTemplateClick -> navigateToProject(intent.value)
            is FirstIntent.OpenMultiProjectBottomSheetClick -> if (!intent.value) qrNavigationManager.clear() else Unit
            else -> Unit
        }
    }

    private fun qrScanner(uri: String) {
        viewModelScope.launch {
            val androidUri = uri.toUri()
            val payload = QrCodeDecoder.decodeForUri(androidUri)

            if (payload == null) {
                sendIntent(FirstIntent.OpenWarningQrCodeClick(true))
                sendIntent(FirstIntent.OpenQrCodeScanner(false))
                return@launch
            }
            findProject(payload)
        }
    }

    private suspend fun findProject(payload: QrPayload) {
        val deviceId = appSettingsRepository.getAppSettings().first().deviceId

        when {
            deviceId != payload.deviceId ->
                when (payload.templateType) {
                    TemplateType.ADD, TemplateType.WRITE_OFF -> {
                        sendIntent(FirstIntent.OpenWarningQrCodeClick(true))
                        sendIntent(FirstIntent.OpenQrCodeScanner(false))
                    }

                    TemplateType.SALE, TemplateType.EXPENSES -> {
                        val projectList = projectRepository.getProjectListAct().first()
                        val newPayload = payload.copy(
                            templateType = TemplateType.EXPENSES,
                            backupData = payload.backupData?.copy(
                                note = if (payload.backupData.note != null) "" else null,
                                isMultiProjectTemplate = false
                            )
                        )
                        qrNavigationManager.put(newPayload)
                        sendIntent(FirstIntent.OpenMultiProjectBottomSheetClick(true, projectList))
                    }
                }

            payload.isMultiProjectTemplate -> {
                val projectList = projectRepository.getProjectListAct().first()
                qrNavigationManager.put(payload)
                sendIntent(FirstIntent.OpenMultiProjectBottomSheetClick(true, projectList))
            }

            else -> {
                val projectExists = projectRepository
                    .getIsProject(payload.idPT)
                    .first()

                if (!projectExists) {
                    sendIntent(FirstIntent.OpenWarningQrCodeClick(true))
                    sendIntent(FirstIntent.OpenQrCodeScanner(false))
                    return
                }
                qrNavigationManager.put(payload)
                navigateTo(UiEvent.Navigate(payload.idPT))
            }
        }
    }

    private fun navigateToProject(id: Long) {
        navigateTo(UiEvent.Navigate(id))
        sendIntent(FirstIntent.OpenMultiProjectBottomSheetClick(false))
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
            updateState { state -> state.copy(isFirstLaunch = true) }


            Log.i("app_settings", "updateFirstLaunch_1:${getState().appSettings} ")
            _notification.emit(UiNotification.Notification)
        }
    }

    private suspend fun updateSettings(domainAppSettings: DomainAppSettings? = null) {
        appSettingsRepository.updateAppSettings(domainAppSettings ?: getState().appSettings)
    }

}
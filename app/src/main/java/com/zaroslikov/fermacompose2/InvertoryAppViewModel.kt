package com.zaroslikov.fermacompose2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.MainIncubatorDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.navigation.UiNotification
import com.zaroslikov.fermacompose2.ui.project.mainScreen.MainProjectsDestination
import com.zaroslikov.fermacompose2.ui.start.first.FirstDestination
import com.zaroslikov.fermacompose2.utils.QrCodeDecoder
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.rustore.sdk.appupdate.listener.InstallStateUpdateListener
import ru.rustore.sdk.appupdate.manager.factory.RuStoreAppUpdateManagerFactory
import ru.rustore.sdk.appupdate.model.AppUpdateOptions
import ru.rustore.sdk.appupdate.model.AppUpdateType
import ru.rustore.sdk.appupdate.model.InstallStatus
import ru.rustore.sdk.appupdate.model.UpdateAvailability
import javax.inject.Inject

@HiltViewModel
class InventoryAppViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    @ApplicationContext private val context: Context,
    val qrNavigationManager: QrNavigationManager,
    val projectRepository: ProjectRepository
) : BaseViewModel2<InvertoryAppState, Event, InvertoryAppReduce>(
    InvertoryAppState(),
    InvertoryAppReduce()
) {
    private val _notification = MutableSharedFlow<UiNotification>()
    val notification = _notification.asSharedFlow()

    var startDestination by mutableStateOf<String?>(null)
        private set

    private var initialized = false

    private val ruStoreAppUpdateManager =
        RuStoreAppUpdateManagerFactory.create(context)


    init {
        loadData()
    }

    fun onIntent(intent: Event) {
        sendIntent(intent)
        when (intent) {
            is Event.SkipTrainingClicked -> updateFirstLaunch()
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val appSettings = appSettingsRepository.getAppSettings().first()
            val newAppSetting = updateLastVersion(appSettings)
            updateState {
                it.copy(
                    appSettings = newAppSetting,
                    isLoading = false
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
            updateState { it.copy(isFirstLaunchUpdate = true, isShowAds = false) }
            updateSettings(newAppSettings)
            newAppSettings
        } else appSettings
    }

    private fun updateFirstLaunch() {
        viewModelScope.launch {
            Log.i("app_settings", "updateFirstLaunch_1:${getState().appSettings} ")

            updateSettings(
                domainAppSettings = getState().appSettings.copy(
                    isFirstLaunch = false,
                )
            )
            Log.i("app_settings", "updateFirstLaunch_1:${getState().appSettings} ")
            _notification.emit(UiNotification.Notification)
        }
    }

    private suspend fun updateSettings(domainAppSettings: DomainAppSettings? = null) {
        appSettingsRepository.updateAppSettings(domainAppSettings ?: getState().appSettings)
    }

    fun resolveStartDestination(intent: Intent?) {
        if (initialized) return
        initialized = true

        viewModelScope.launch {
            startDestination = calculateStartDestination(intent)
        }
    }

    private suspend fun calculateStartDestination(intent: Intent?): String {

        val action = intent?.action
        val projectId = intent?.getLongExtra("itemIdPT", -1L) ?: -1L
        val uri = intent?.data?.getQueryParameter("data")

        return when {
            action == "OPEN_BOOKMARK_DETAIL" && projectId != -1L ->
                "${MainIncubatorDestination.route}/$projectId"

            action == "OPEN_PROJECT_DETAIL" && projectId != -1L ->
                "${MainProjectsDestination.route}/$projectId"

            uri != null -> {
                val payload = QrCodeDecoder.decodeForBase64(uri)
                val deviceId = appSettingsRepository.getAppSettings().first().deviceId

                qrNavigationManager.put(payload)
                when {
                    payload.deviceId != deviceId -> FirstDestination.route
                    payload.isMultiProjectTemplate -> FirstDestination.route
                    else -> {
                        val projectExists = projectRepository
                            .getIsProject(payload.idPT)
                            .first()

                        if (!projectExists) FirstDestination.route
                        else
                            "${MainProjectsDestination.route}/${payload.idPT}?${MainProjectsDestination.templateArg}=true"
                    }
                }
            }

            else -> FirstDestination.route
        }
    }

    fun update() {
        val versionName = BuildConfig.VERSION_NAME
        if (versionName.contains("g", ignoreCase = true)) return

        ruStoreAppUpdateManager
            .getAppUpdateInfo()
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability == UpdateAvailability.UPDATE_AVAILABLE) {
                    ruStoreAppUpdateManager.registerListener(installStateUpdateListener)
                    ruStoreAppUpdateManager
                        .startUpdateFlow(
                            appUpdateInfo, AppUpdateOptions.Builder()
                                .appUpdateType(AppUpdateType.FLEXIBLE)
                                .build()
                        )
                        .addOnSuccessListener { resultCode ->
                            when (resultCode) {
                                Activity.RESULT_CANCELED -> {
                                    // Пользователь отказался от скачивания
                                }

                                Activity.RESULT_OK -> {
                                    completeUpdateRequested()
                                }
                            }
                        }
                        .addOnFailureListener { throwable ->
                            Log.e("RuStore", "startUpdateFlow error", throwable)
                        }
                }
            }
            .addOnFailureListener { throwable ->
                Log.e("RuStore", "getAppUpdateInfo error", throwable)
            }
    }

    private fun completeUpdateRequested() {
        ruStoreAppUpdateManager.completeUpdate(
            AppUpdateOptions.Builder().appUpdateType(AppUpdateType.FLEXIBLE).build()
        )
            .addOnFailureListener { throwable ->
                Log.e("RuStore", "completeUpdate error", throwable)
            }
    }

    private val installStateUpdateListener = InstallStateUpdateListener { installState ->
        Log.i(
            "update_app",
            "status = ${installState.installStatus}"
        )
        when (installState.installStatus) {
            InstallStatus.DOWNLOADED -> {
                sendIntent(Event.ShowDownloadingUpdate(false))
                completeUpdateRequested()
            }

            InstallStatus.DOWNLOADING -> {
                val totalBytes = installState.totalBytesToDownload
                val bytesDownloaded = installState.bytesDownloaded
                sendIntent(Event.ShowDownloadingUpdate(true))
            }

            InstallStatus.FAILED -> {}
        }
    }
}

sealed class Event : BaseIntent {
    data class ShowDownloadingUpdate(val value: Boolean) : Event()
    data object SkipTrainingClicked : Event()
}

data class InvertoryAppState(
    val isOpenDownloadingUpdate: Boolean = false,
    val isFirstLaunchUpdate: Boolean = false,
    val isShowAds: Boolean = true,
    val appSettings: DomainAppSettings = DomainAppSettings(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val isNotificationAsked: Boolean = false
) : BaseState

class InvertoryAppReduce : BaseReducer<InvertoryAppState, Event>() {
    override fun reducer(
        state: InvertoryAppState,
        intent: Event
    ): InvertoryAppState {
        return when (intent) {
            is Event.ShowDownloadingUpdate -> state.copy(isOpenDownloadingUpdate = intent.value)
            is Event.SkipTrainingClicked -> state.updateSkipTraining()
            else -> state
        }
    }

    private fun InvertoryAppState.updateSkipTraining(): InvertoryAppState {
        return copy(
            appSettings = appSettings.copy(
                isFirstLaunch = false
            ),
            isFirstLaunchUpdate = false,
            isNotificationAsked = true,
            isShowAds = false
        )
    }
}
package com.zaroslikov.fermacompose2.ui.start.first

import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstScreenViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val bookmarkRepository: BookmarkRepository
) : BaseViewModel2<FirstState, FirstIntent, FirstScreenReducer>(
    FirstState(),
    FirstScreenReducer()
) {
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            projectRepository.getAllProject().collect { baseList ->
                val list = baseList.filter { !it.archive }
                val archiveList = baseList.filter { it.archive }
                updateState {
                    it.copy(
                        isLoading = false,
                        list = list,
                        archiveList = archiveList
                    )
                }
            }
        }
    }

    fun onIntent(intent: FirstIntent) {
        return when (intent) {
            is FirstIntent.DeleteClicked -> deleteProject()
            is FirstIntent.ArchiveClicked -> archiveProject(intent.value)
            is FirstIntent.UnarchiveClicked -> unarchiveProject(intent.value)
            else -> sendIntent(intent)
        }
    }

    private fun unarchiveProject(domainProjectTable: DomainProjectTable) {
        viewModelScope.launch {
            projectRepository.updateProject(domainProjectTable.copy(archive = false))
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
                sendIntent(FirstIntent.OpenArchiveIncubatorBottomSheetClicked(false))
            }
        }
    }

    private fun deleteProject() {
        viewModelScope.launch {
            getState().currentProjectTable?.let {
                projectRepository.deleteProject(it)
                sendIntent(FirstIntent.OpenDeleteBottomSheetClicked(false))
            }
        }
    }

    /* var time by mutableStateOf("")

                time = waterRepository.getTimeReminder()
        fun onUpdate(time1: String) {
            time = time1
        }

        fun saveItem() {
            viewModelScope.launch {
                *//*   waterRepository.cancelAllNotifications("7bc20e66-fc56-4002-ac33-4cc15dd28213")
               waterRepository.setTimeReminder(time)
               if (time != "") {
                   waterRepository.setupDailyReminder()
               }*//*
        }
    }

    val getAllProject: StateFlow<List<ProjectTableStartScreen>> =
        fermaRepository.getAllProject().map { projectList ->
            projectList.map { project ->
                project.toProjectWithImage()
            }
        }.onStart {
            // Устанавливаем состояние загрузки перед началом загрузки данных
            _isLoading.value = true
        }.onEach {
            // Отключаем состояние загрузки после завершения загрузки данных
            _isLoading.value = false
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private suspend fun DomainProjectTable.toProjectWithImage(): ProjectTableStartScreen =
        withContext(Dispatchers.IO) {

            val imageBitmap = imageData?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
            }


            val data = if (mode == 0) {
                val calendar: Calendar = Calendar.getInstance()
                val dateBefore22: String = data
                val dateBefore222: String =
                    (calendar.get(Calendar.DAY_OF_MONTH)).toString() + "." + (calendar.get(
                        Calendar.MONTH
                    ) + 1) + "." + calendar.get(Calendar.YEAR)
                val myFormat = SimpleDateFormat("dd.MM.yyyy")
                val date1: Date = myFormat.parse(dateBefore22)
                val date2: Date = myFormat.parse(dateBefore222)
                val diff = date2.time - date1.time
                "Идет ${TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1} день"
            } else data

            ProjectTableStartScreen(
                id.toInt(),
                titleProject,
                type,
                data,
                arhive,
                dateEnd,
                mode,
                imageBitmap
            )
        }*/
}

sealed class FirstIntent() : BaseIntent {
    data object DeleteClicked : FirstIntent()
    data class ArchiveClicked(val value: DomainProjectTable?) : FirstIntent()
    data class UnarchiveClicked(val value: DomainProjectTable) : FirstIntent()
    data class OpenArchiveIncubatorBottomSheetClicked(
        val value: Boolean,
        val domainProjectTable: DomainProjectTable? = null
    ) : FirstIntent()

    data class OpenDeleteBottomSheetClicked(
        val value: Boolean,
        val domainProjectTable: DomainProjectTable? = null
    ) : FirstIntent()

    data object ArchiveModeClicked : FirstIntent()
    data class LoadingClicked(val value: Boolean) : FirstIntent()
}
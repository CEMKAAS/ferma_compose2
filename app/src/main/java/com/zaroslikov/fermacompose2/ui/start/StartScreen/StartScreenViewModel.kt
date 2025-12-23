package com.zaroslikov.fermacompose2.ui.start.StartScreen

import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
) : ListViewModel<StartScreenState, StartScreenIntent>(StartScreenState()) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            projectRepository.getAllProject().collectLatest { list ->
                updateState {
                    it.copy(
                        isLoading = false,
                        list = list
                    )
                }
            }
        }
    }

    fun onIntent(intent: StartScreenIntent) {
        return when (intent) {
            is StartScreenIntent.DeleteClicked -> deleteProject(intent.domainProjectTable)
            is StartScreenIntent.ArchiveClicked -> archiveProject(intent.domainProjectTable)
        }
    }

    private fun archiveProject(domainProjectTable: DomainProjectTable) {
        viewModelScope.launch {
            projectRepository.updateProject(domainProjectTable.copy(mode = 0))
        }
    }

    private fun deleteProject(domainProjectTable: DomainProjectTable) {
        viewModelScope.launch {
            projectRepository.deleteProject(domainProjectTable)
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

sealed class StartScreenIntent() : BaseIntent {
    data class DeleteClicked(val domainProjectTable: DomainProjectTable) : StartScreenIntent()
    data class ArchiveClicked(val domainProjectTable: DomainProjectTable) : StartScreenIntent()
}
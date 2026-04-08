package com.zaroslikov.fermacompose2.ui.warehouse

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarehouseEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository
) : ListViewModel<WarehouseEditState, WarehouseEditIntent>(WarehouseEditState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[WarehouseEditDestination.itemIdArg])

    init {
        updateState { it.copy(isInsertProject = itemId == -1L) }
        if (itemId != -1L) loadData(itemId)
    }

    fun onIntent(intent: WarehouseEditIntent) {
        return when (intent) {
            is WarehouseEditIntent.CurrencyClicked -> updateCurrentCurrency(intent.value)
            is WarehouseEditIntent.LinearClicked -> updateCurrentLinear(intent.value)
            is WarehouseEditIntent.VolumeClicked -> updateCurrentVolume(intent.value)
            is WarehouseEditIntent.WeightClicked -> updateCurrentWeight(intent.value)
            is WarehouseEditIntent.IconClicked -> updateIcon(intent.value)
            is WarehouseEditIntent.NameProjectChanged -> updateTitle(intent.value)
            is WarehouseEditIntent.DateClicked -> updateDateProject(intent.value)
            WarehouseEditIntent.InsertClicked -> insertProject()
            WarehouseEditIntent.EditClicked -> editProject()
            is WarehouseEditIntent.ImagePathClicked -> updateImagePath(intent.value)
        }
    }


    private fun loadData(itemId: Long) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val project = projectRepository.getProject(itemId).first()
            val settings = settingsRepository.getSettings(itemId).first()
            updateState {
                it.copy(
                    isLoading = false,
                    nameProject = project.title,
                    dateProject = project.date,
                    currentIcon = project.currentIcon ?: R.drawable.livestock,
                    imagePath = project.imagePath,
                    currentProject = project,
                    currentSettings = settings,
                    idPT = itemId
                )
            }
        }
    }


    private fun updateTitle(nameProject: String) {
        updateState {
            it.copy(currentProject = it.currentProject.copy(title = nameProject))
        }
    }

    private fun updateIcon(currentIcon: Int) {
        updateState { it.copy(currentIcon = currentIcon) }
    }

    private fun updateImagePath(imagePath: String?) {
        updateState { it.copy(imagePath = imagePath) }
    }

    private fun updateDateProject(dateProject: String) {
        updateState { it.copy(currentProject = it.currentProject.copy(date = dateProject)) }
    }

    private fun updateCurrentCurrency(currency: Suffix) {
        updateState { it.copy(currentSettings = it.currentSettings.copy(currencySuffix = currency)) }
    }

    private fun updateCurrentWeight(weight: Suffix) {
        updateState { it.copy(currentSettings = it.currentSettings.copy(weightSuffix = weight)) }
    }

    private fun updateCurrentVolume(volume: Suffix) {
        updateState { it.copy(currentSettings = it.currentSettings.copy(volumeSuffix = volume)) }
    }

    private fun updateCurrentLinear(linear: Suffix) {
        updateState { it.copy(currentSettings = it.currentSettings.copy(linearSuffix = linear)) }
    }

    private fun insertProject() {
        viewModelScope.launch {
            val id = projectRepository.insertProjectLong(getState().toDomainProjectTable())
            settingsRepository.insertSettings(getState().toDomainSettings(idPT = id))
        }
    }

    private fun editProject() {
        viewModelScope.launch {
            projectRepository.updateProject(getState().toDomainProjectTable(itemId))
            settingsRepository.updateSettings(getState().toDomainSettings())
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
}

sealed class WarehouseEditIntent() : BaseIntent {
    data class NameProjectChanged(val value: String) : WarehouseEditIntent()
    data class IconClicked(val value: Int) : WarehouseEditIntent()
    data class ImagePathClicked(val value: String?) : WarehouseEditIntent()
    data class DateClicked(val value: String) : WarehouseEditIntent()
    data class CurrencyClicked(val value: Suffix) : WarehouseEditIntent()
    data class WeightClicked(val value: Suffix) : WarehouseEditIntent()
    data class VolumeClicked(val value: Suffix) : WarehouseEditIntent()
    data class LinearClicked(val value: Suffix) : WarehouseEditIntent()
    data object InsertClicked : WarehouseEditIntent()
    data object EditClicked : WarehouseEditIntent()
}

package com.zaroslikov.fermacompose2.ui.start.settings

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.DomainIncubatorParameters
import com.zaroslikov.domain.models.table.DomainIncubatorTable
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.domain.models.table.profile.DomainProfileTable
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import kotlinx.serialization.Serializable

data class SettingsState(
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val backupDataText: String? = null,
    val isOpenExportBottomSheet: Boolean = false,
    val isOpenImportBottomSheet: Boolean = false,
    val isOpenDeleteBottomSheet: Boolean = false
) : BaseState


@Serializable
data class BackupData(
    val version: Int,
    val projectTable: List<DomainProjectTable> = emptyList(),
    val settingsTable: List<DomainSettings> = emptyList(),

    val addTable: List<DomainAddTable> = emptyList(),
    val saleTable: List<DomainSaleTable> = emptyList(),
    val writeOff: List<DomainWriteOffTable> = emptyList(),
    val expensesTable: List<DomainExpensesTable> = emptyList(),
    val noteTable: List<DomainNoteTable> = emptyList(),

    val expensesAnimal: List<DomainExpensesAnimal> = emptyList(),

    val animalTable: List<DomainAnimalTable> = emptyList(),
    val animalCountTable: List<DomainAnimalCount> = emptyList(),
    val animalWeightTable: List<DomainAnimalWeight> = emptyList(),
    val animalSizeTable: List<DomainAnimalSize> = emptyList(),
    val animalVaccinationTable: List<DomainAnimalVaccination> = emptyList(),

    val incubatorTable: List<DomainIncubatorTable> = emptyList(),
    val bookmarkTable: List<DomainBookmark> = emptyList(),
    val incubatorParameters: List<DomainIncubatorParameters> = emptyList(),
    val profileTable: List<DomainProfileTable> = emptyList(),
    val appSettingsTable: List<DomainAppSettings> = emptyList(),
)
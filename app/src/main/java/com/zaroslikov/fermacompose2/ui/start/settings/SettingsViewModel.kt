package com.zaroslikov.fermacompose2.ui.start.settings

import android.content.Context
import android.util.Base64
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.IncubatorParametersRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.domain.repository.ProfileRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.TimeNotificationRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.ui.navigation.EventFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val database: AppDatabase,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,

    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val writeOffRepository: WriteOffRepository,
    private val expensesRepository: ExpensesRepository,
    private val noteRepository: NoteRepository,

    private val expensesAnimalRepository: ExpensesAnimalRepository,

    private val animalRepository: AnimalRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,

    private val incubatorTableRepository: IncubatorTableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val incubatorParametersRepository: IncubatorParametersRepository,
    private val timeNotificationRepository: TimeNotificationRepository,

    private val profileRepository: ProfileRepository,
    private val appSettingsRepository: AppSettingsRepository

) :
    BaseViewModel2<SettingsState, SettingsIntent, SettingsReduce>(
        SettingsState(),
        SettingsReduce()
    ) {
    init {
        loadData()
    }

    private fun loadData() {

    }

    fun onIntent(intent: SettingsIntent) {
        sendIntent(intent)
        when (intent) {
            is SettingsIntent.OpenExportBottomSheetClick -> createDatabaseForUploading(intent.value)
            is SettingsIntent.ImportDatabasePress -> setAllData(intent.value, intent.context)
            is SettingsIntent.ExportDatabasePress -> shapeExportDatabase()
            is SettingsIntent.DeleteDatabasePress -> deleteDatabase()
            else -> Unit
        }
    }

    private fun deleteDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.withTransaction {
                    database.clearAllTables()
                }

                withContext(Dispatchers.Main) {
                    sendIntent(SettingsIntent.OpenDeleteBottomSheetClick(false))
                    showMessage("База данных очищена")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    sendIntent(SettingsIntent.OpenDeleteBottomSheetClick(false))
                    showMessage("Ошибка очистки базы")
                }
            }
        }
    }

    private fun shapeExportDatabase() {
        getState().backupDataText?.let { event(EventFile.File(it)) }
    }

    private fun createDatabaseForUploading(isOpenExportBottomSheet: Boolean) {
        viewModelScope.launch {
            if (!isOpenExportBottomSheet) {
                updateState { state -> state.copy(backupDataText = null) }
                return@launch
            }
            val projectTable = projectRepository.getAllProjectTableForExport().first()
            val settingsTable = settingsRepository.getAllSettingsTableForExport().first()

            val addTable = addRepository.getAllAddTableForExport().first()
            val saleTable = saleRepository.getAllSaleTableForExport().first()
            val writeOffTable = writeOffRepository.getAllWriteOffTableForExport().first()
            val expensesTable = expensesRepository.getAllExpensesTableForExport().first()
            val noteTable = noteRepository.getAllNoteTableForExport().first()

            val expensesAnimalTable =
                expensesAnimalRepository.getAllExpensesAnimalTableForExport().first()

            val animalTable = animalRepository.getAllAnimalTableForExport().first()
            val animalCountTable = animalCountRepository.getAllAnimalCountTableForExport().first()
            val animalWeightTable =
                animalWeightRepository.getAllAnimalWeightTableForExport().first()
            val animalSizeTable = animalSizeRepository.getAllAnimalSizeTableForExport().first()
            val animalVaccinationTable =
                animalVaccinationRepository.getAllAnimalVaccinationTableForExport().first()

            val incubatorTable = incubatorTableRepository.getAllIncubatorTableForExport().first()
            val bookmarkTable = bookmarkRepository.getAllBookmarkTableForExport().first()
            val timeNotificationTable =
                timeNotificationRepository.getAllTimeNotificationTableForExport().first()
            val incubatorParameters =
                incubatorParametersRepository.getAllIncubatorParameterTableForExport().first()
            val profileTable = profileRepository.getAllProfileTableForExport().first()
            val appSettingsTable = appSettingsRepository.getAllAppSettingsTableForExport().first()

            val backupData = BackupData(
                version = 1,
                projectTable = projectTable.map { project ->
                    val base64 = project.imagePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            val bytes = file.readBytes()
                            Base64.encodeToString(bytes, Base64.DEFAULT)
                        } else null
                    }
                    project.copy(
                        imagePath = base64 // может быть null — это нормально
                    )
                },
                settingsTable = settingsTable,
                addTable = addTable,
                saleTable = saleTable,
                writeOff = writeOffTable,
                expensesTable = expensesTable,
                expensesAnimal = expensesAnimalTable,
                noteTable = noteTable,
                animalTable = animalTable.map { project ->
                    val base64 = project.imagePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            val bytes = file.readBytes()
                            Base64.encodeToString(bytes, Base64.DEFAULT)
                        } else null
                    }
                    project.copy(
                        imagePath = base64 // может быть null — это нормально
                    )
                },
                animalCountTable = animalCountTable,
                animalWeightTable = animalWeightTable,
                animalSizeTable = animalSizeTable,
                animalVaccinationTable = animalVaccinationTable,
                incubatorTable = incubatorTable,
                bookmarkTable = bookmarkTable,
                incubatorParameters = incubatorParameters,
                timeNotificationTable = timeNotificationTable,
                profileTable = profileTable,
                appSettingsTable = appSettingsTable
            )
            val json = Json.encodeToString(backupData)
            updateState { state -> state.copy(backupDataText = json) }
            return@launch
        }
    }

    private fun setAllData(value: String?, context: Context) {
        viewModelScope.launch {
            sendIntent(SettingsIntent.OpenImportBottomSheetClick(false))
            if (value == null) {
                showMessage("Ошибка файла")
                return@launch
            }


            val backup = try {
                Json.decodeFromString<BackupData>(value)
            } catch (e: Exception) {
                showMessage("Ошибка чтения файла")
                return@launch
            }

            projectRepository.clearAndInsertProjectTableForImport(backup.projectTable.map { project ->
                val newPath = project.imagePath?.let { base64 ->
                    try {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)

                        val file = File(
                            context.filesDir,
                            "project_${System.currentTimeMillis()}_${project.id}.jpg"
                        )

                        file.writeBytes(bytes)
                        file.absolutePath

                    } catch (e: Exception) {
                        null // если картинка битая — просто игнорируем
                    }
                }
                project.copy(
                    imagePath = newPath
                )
            })
            settingsRepository.clearAndInsertSettingsTableForImport(backup.settingsTable)
            animalRepository.clearAndInsertAnimalTableForImport(backup.animalTable.map { animalTable ->
                val newPath = animalTable.imagePath?.let { base64 ->
                    try {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        val file = File(
                            context.filesDir,
                            "project_${System.currentTimeMillis()}_${animalTable.id}.jpg"
                        )
                        file.writeBytes(bytes)
                        file.absolutePath

                    } catch (e: Exception) {
                        null // если картинка битая — просто игнорируем
                    }
                }
                animalTable.copy(
                    imagePath = newPath
                )
            })

            addRepository.clearAndInsertAddTableForImport(backup.addTable)
            saleRepository.clearAndInsertSaleTableForImport(backup.saleTable)
            writeOffRepository.clearAndInsertWriteOffTableForImport(backup.writeOff)
            expensesRepository.clearAndInsertExpensesTableForImport(backup.expensesTable)
            noteRepository.clearAndInsertNoteTableForImport(backup.noteTable)

            expensesAnimalRepository.clearAndInsertExpensesAnimalTableForImport(backup.expensesAnimal)

            animalCountRepository.clearAndInsertAnimalCountTableForImport(backup.animalCountTable)
            animalWeightRepository.clearAndInsertAnimalWeightTableForImport(backup.animalWeightTable)
            animalSizeRepository.clearAndInsertAnimalSizeTableForImport(backup.animalSizeTable)
            animalVaccinationRepository.clearAndInsertAnimalVaccinationTableForImport(backup.animalVaccinationTable)

            incubatorTableRepository.clearAndInsertIncubatorTableForImport(backup.incubatorTable)
            bookmarkRepository.clearAndInsertBookmarkTableForImport(backup.bookmarkTable)
            incubatorParametersRepository.clearAndInsertIncubatorParametersTableForImport(backup.incubatorParameters)
            timeNotificationRepository.clearAndInsertTimeNotificationTableForImport(backup.timeNotificationTable)

            profileRepository.clearAndInsertProfileTableForImport(backup.profileTable)
            appSettingsRepository.clearAndInsertAppSettingsTableForImport(backup.appSettingsTable)

            showMessage("Ипорт прошел успешно")
            return@launch
        }
    }
}

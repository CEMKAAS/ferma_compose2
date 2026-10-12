package com.zaroslikov.fermacompose2.ui.start.settings

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.zaroslikov.crm.domain.model.DomainCrmBackup
import com.zaroslikov.crm.domain.repository.CrmBackupRepository
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.domain.models.table.profile.DomainProfileTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.models.animal.pregnancy.normalizedForImport
import com.zaroslikov.domain.models.animal.pregnancy.normalizedNotesForImport
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.domain.models.animal.group.normalizedHerdsForImport
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
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
import com.zaroslikov.domain.repository.TimeNotificationIncubatorRepository
import com.zaroslikov.domain.repository.TimeNotificationProjectRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.domain.repository.template.TemplateRepository
import com.zaroslikov.fermacompose2.BuildConfig
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.data.worker.WorkManagerRepository
import com.zaroslikov.fermacompose2.ui.navigation.EventFile
import com.zaroslikov.fermacompose2.utils.PregnancyReminderScheduler
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val database: AppDatabase,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,
    private val timeNotificationProjectRepository: TimeNotificationProjectRepository,

    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val writeOffRepository: WriteOffRepository,
    private val expensesRepository: ExpensesRepository,
    private val noteRepository: NoteRepository,

    private val templateRepository: TemplateRepository,

    private val expensesAnimalRepository: ExpensesAnimalRepository,

    private val animalRepository: AnimalRepository,
    private val animalHerdRepository: AnimalHerdRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val animalPregnancyRepository: AnimalPregnancyRepository,
    private val pregnancyReminderScheduler: PregnancyReminderScheduler,

    private val incubatorTableRepository: IncubatorTableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val incubatorParametersRepository: IncubatorParametersRepository,
    private val timeNotificationIncubatorRepository: TimeNotificationIncubatorRepository,

    private val profileRepository: ProfileRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val workManagerRepository: WorkManagerRepository,
    private val crmBackupRepository: CrmBackupRepository

) :
    BaseViewModel2<SettingsState, SettingsIntent, SettingsReduce>(
        SettingsState(),
        SettingsReduce()
    ) {


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
                    workManagerRepository.cancelAllNotifications()
                    database.clearAllTables()

                    appSettingsRepository.createAppSettings(
                        domainAppSettings = DomainAppSettings(
                            lastVersionApp = BuildConfig.VERSION_NAME,
                            currentVersionApp = BuildConfig.VERSION_NAME,
                            isFirstLaunch = false,
                            deviceId = UUID.randomUUID().toString()
                        )
                    )
                    profileRepository.createProfile(
                        DomainProfileTable(
                            name = resourceProvider.getString(R.string.profile_screen_base)
                        )
                    )
                }
                // CRM хранится в отдельной базе — чистим её тоже
                crmBackupRepository.clearAll()

                withContext(Dispatchers.Main) {
                    sendIntent(SettingsIntent.OpenDeleteBottomSheetClick(false))
                    showMessage("База данных очищена")
                }
                AppMetrica.reportEvent("Удаление базы данных")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    sendIntent(SettingsIntent.OpenDeleteBottomSheetClick(false))
                    showMessage("Ошибка очистки базы")
                }
            }
        }
    }

    private fun shapeExportDatabase() {
        getState().backupDataText?.let { event(EventFile.FileUri(it)) }
        AppMetrica.reportEvent("Экспорт базы данных через отправку")
    }

    private fun createDatabaseForUploading(isOpenExportBottomSheet: Boolean) {
        viewModelScope.launch {
            if (!isOpenExportBottomSheet) {
                updateState { state -> state.copy(backupDataText = null) }
                return@launch
            }
            val projectTable = projectRepository.getAllProjectTableForExport().first()
            val settingsTable = settingsRepository.getAllSettingsTableForExport().first()
            val timeNotificationProjectTable =
                timeNotificationProjectRepository.getAllTimeNotificationTableForExport().first()

            val addTable = addRepository.getAllAddTableForExport().first()
            val saleTable = saleRepository.getAllSaleTableForExport().first()
            val writeOffTable = writeOffRepository.getAllWriteOffTableForExport().first()
            val expensesTable = expensesRepository.getAllExpensesTableForExport().first()
            val noteTable = noteRepository.getAllNoteTableForExport().first()

            val templateTable = templateRepository.getAllAddTemplateTableForExport().first()

            val expensesAnimalTable =
                expensesAnimalRepository.getAllExpensesAnimalTableForExport().first()

            val animalHerdTable = animalHerdRepository.getAllForExport().first()
            val animalTable = animalRepository.getAllAnimalTableForExport().first()
            val animalCountTable = animalCountRepository.getAllAnimalCountTableForExport().first()
            val animalWeightTable =
                animalWeightRepository.getAllAnimalWeightTableForExport().first()
            val animalSizeTable = animalSizeRepository.getAllAnimalSizeTableForExport().first()
            val animalVaccinationTable =
                animalVaccinationRepository.getAllAnimalVaccinationTableForExport().first()
            val animalPregnancyTable = animalPregnancyRepository.getAllForExport().first()
            val animalPregnancyNoteTable = animalPregnancyRepository.getAllNotesForExport().first()

            val incubatorTable = incubatorTableRepository.getAllIncubatorTableForExport().first()
            val bookmarkTable = bookmarkRepository.getAllBookmarkTableForExport().first()
            val timeNotificationIncubatorTable =
                timeNotificationIncubatorRepository.getAllTimeNotificationTableForExport().first()
            val incubatorParameters =
                incubatorParametersRepository.getAllIncubatorParameterTableForExport().first()
            val profileTable = profileRepository.getAllProfileTableForExport().first()
            val appSettingsTable = appSettingsRepository.getAllAppSettingsTableForExport().first()
            val crm = crmBackupRepository.export()

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
                timeNotificationProjectTable = timeNotificationProjectTable,
                addTable = addTable,
                saleTable = saleTable,
                writeOff = writeOffTable,
                expensesTable = expensesTable,
                expensesAnimal = expensesAnimalTable,
                noteTable = noteTable,
                templateTable = templateTable,
                animalHerdTable = animalHerdTable.map { herd ->
                    herd.copy(imagePath = herd.imagePath?.let(::encodeImage))
                },
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
                animalPregnancyTable = animalPregnancyTable,
                animalPregnancyNoteTable = animalPregnancyNoteTable,
                incubatorTable = incubatorTable,
                bookmarkTable = bookmarkTable,
                incubatorParameters = incubatorParameters,
                timeNotificationIncubatorTable = timeNotificationIncubatorTable,
                profileTable = profileTable,
                appSettingsTable = appSettingsTable,
                crm = crm
            )
            val json = Json.encodeToString(backupData)
            updateState { state -> state.copy(backupDataText = json) }
            AppMetrica.reportEvent("Экспорт базы данных в фаил")
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
                BackupParser.parse(value)
            } catch (e: Exception) {
                showMessage("Ошибка чтения файла")
                return@launch
            }
            try {
                workManagerRepository.cancelAllNotifications()

                projectRepository.clearAndInsertProjectTableForImport(backup.projectTable.map { project ->
                    val newPath = project.imagePath?.let { base64 ->
                        decodeImage(base64, project.id, context)
                    }
                    project.copy(
                        imagePath = newPath
                    )
                })
                settingsRepository.clearAndInsertSettingsTableForImport(backup.settingsTable)
                timeNotificationProjectRepository.clearAndInsertTimeNotificationTableForImport(
                    backup.timeNotificationProjectTable
                )
                // Сборные группы — раньше животных: на них ссылается animal_table.herd_id
                val herds = backup.animalHerdTable
                    .normalizedHerdsForImport(projectIds = backup.projectTable.map { it.id }.toSet())
                    .map { herd ->
                    herd.copy(imagePath = herd.imagePath?.let { base64 ->
                        decodeImage(base64 = base64, herd.id, context, prefix = "herd")
                    })
                }
                animalHerdRepository.clearAndInsertForImport(herds)
                animalRepository.clearAndInsertAnimalTableForImport(backup.animalTable.map { animalTable ->
                    val newPath = animalTable.imagePath?.let { base64 ->
                        decodeImage(base64 = base64, animalTable.id, context)
                    }
                    animalTable.copy(
                        imagePath = newPath
                    )
                }, herds)

                addRepository.clearAndInsertAddTableForImport(backup.addTable)
                saleRepository.clearAndInsertSaleTableForImport(backup.saleTable)
                writeOffRepository.clearAndInsertWriteOffTableForImport(backup.writeOff)
                noteRepository.clearAndInsertNoteTableForImport(backup.noteTable)
                expensesAnimalRepository.clearAndInsertExpensesAnimalTableForImport(backup.expensesAnimal)

                templateRepository.clearAndInsertAddTemplateTableForImport(backup.templateTable)

                // Метка несуществующей сборной группы нарушила бы FK — запись остаётся вне группы
                val herdIds = herds.map { it.id }.toSet()
                animalCountRepository.clearAndInsertAnimalCountTableForImport(
                    backup.animalCountTable.map { count ->
                        if (count.herdId != null && count.herdId !in herdIds) count.copy(herdId = null) else count
                    }
                )
                animalWeightRepository.clearAndInsertAnimalWeightTableForImport(backup.animalWeightTable)
                animalSizeRepository.clearAndInsertAnimalSizeTableForImport(backup.animalSizeTable)
                animalVaccinationRepository.clearAndInsertAnimalVaccinationTableForImport(backup.animalVaccinationTable)
                // Только после животных: их пересоздание каскадно удаляет беременности
                val pregnancies = backup.animalPregnancyTable.normalizedForImport(
                    animals = backup.animalTable,
                    projectIds = backup.projectTable.map { it.id }.toSet()
                )
                animalPregnancyRepository.clearAndInsertForImport(pregnancies)
                // Заметки — после беременностей (каскад) и только к уцелевшим
                animalPregnancyRepository.clearAndInsertNotesForImport(
                    backup.animalPregnancyNoteTable.normalizedNotesForImport(
                        pregnancyIds = pregnancies.map { it.id }.toSet()
                    )
                )

                expensesRepository.clearAndInsertExpensesTableForImport(backup.expensesTable)

                incubatorTableRepository.clearAndInsertIncubatorTableForImport(backup.incubatorTable)
                bookmarkRepository.clearAndInsertBookmarkTableForImport(backup.bookmarkTable)
                incubatorParametersRepository.clearAndInsertIncubatorParametersTableForImport(backup.incubatorParameters)
                timeNotificationIncubatorRepository.clearAndInsertTimeNotificationTableForImport(
                    backup.timeNotificationIncubatorTable
                )

                profileRepository.clearAndInsertProfileTableForImport(backup.profileTable)
                appSettingsRepository.clearAndInsertAppSettingsTableForImport(backup.appSettingsTable.map {
                    it.copy(
                        currentVersionApp = BuildConfig.VERSION_NAME,
                        deviceId = it.deviceId.ifBlank { UUID.randomUUID().toString() }
                    )
                })
                // CRM всегда перезаписывается: иначе старые клиенты прилипнут к чужим idPT.
                // Берутся только строки проектов этого же бэкапа
                crmBackupRepository.import(
                    backup.crm ?: DomainCrmBackup(),
                    projectIds = backup.projectTable.map { it.id }.toSet()
                )
                // Напоминания старых беременностей сняты, новые — по импортированным данным
                pregnancyReminderScheduler.resetAfterImport()
                launchNotification()
                showMessage("Импорт прошел успешно")
                AppMetrica.reportEvent("Импорт базы данных")
                return@launch
            } catch (e: Exception) {
                // Импорт оборвался — старые клиенты CRM не должны остаться при чужих idPT
                try {
                    crmBackupRepository.clearAll()
                } catch (clearError: CancellationException) {
                    throw clearError
                } catch (clearError: Exception) {
                    Log.i("error", "setAllData: clear CRM $clearError")
                }
                showMessage("Ошибка при импорте базы данных!\nОбратитесь в службу поддержки.")
                Log.i("error", "setAllData: $e")
            }
        }
    }

    /** Фото для бэкапа: содержимое файла в base64; файла нет — null. */
    private fun encodeImage(path: String): String? {
        val file = File(path)
        return if (file.exists()) Base64.encodeToString(file.readBytes(), Base64.DEFAULT) else null
    }

    /** [prefix] разводит файлы таблиц с пересекающимися id (животное и сборная группа). */
    private fun decodeImage(base64: String, id: Long, context: Context, prefix: String = "project"): String? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val file = File(
                context.filesDir,
                "${prefix}_${System.currentTimeMillis()}_${id}.jpg"
            )
            file.writeBytes(bytes)
            file.absolutePath

        } catch (e: Exception) {
            null
        }
    }

    private suspend fun launchNotification() {
        launchNotificationForIncubator()
        launchNotificationForProject()
    }

    suspend fun launchNotificationForIncubator() {
        val timeNotificationList =
            timeNotificationIncubatorRepository.getTimeNotificationInAllActiveBookmark().first()
        if (timeNotificationList.isNotEmpty()) {
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
        val timeNotificationList =
            timeNotificationProjectRepository.getTimeNotificationInAllActiveProject().first()
        if (timeNotificationList.isNotEmpty()) {
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
}

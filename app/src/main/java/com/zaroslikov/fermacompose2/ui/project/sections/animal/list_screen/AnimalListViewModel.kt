package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.toUi
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.utils.PregnancyReminderScheduler
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation

import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.toMergeCandidate
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.toMergeGroupChoice
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository

import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class       AnimalListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalHerdRepository: AnimalHerdRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val expensesRepository: ExpensesRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository,
    private val settingsRepository: SettingsRepository,
    private val pregnancyRepository: AnimalPregnancyRepository,
    private val pregnancyReminderScheduler: PregnancyReminderScheduler
) : EntryNewViewModel2<AnimalListState, AnimalListIntent, AnimalListReduce>(
    AnimalListState(),
    AnimalListReduce(resourceProvider)
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalDestination.itemIdArg])

    init {
        loadDate()
    }

    override fun onIntent(intent: AnimalListIntent) {
        sendIntent(intent)
        return when (intent) {
            is AnimalListIntent.OpenBottomSheetEntry ->
                loadDataForEntryOrEdit(intent.value, intent.isSaveStateForBottomSheet)

            AnimalListIntent.Insert -> insert()
            is AnimalListIntent.Archive -> archive(intent.id, intent.isArchive)
            is AnimalListIntent.ArchivePregnancyConfirmed -> archivePregnant(intent.animalId, intent.interrupt)
            is AnimalListIntent.OpenBottomSheetDelete ->
                if (intent.value != null) loadDeletePregnancyHistory(intent.value) else Unit

            is AnimalListIntent.Delete -> delete(0)

            is AnimalListIntent.OpenMergeSheet ->
                if (intent.value) openMergeSheet(intent.preselectedId) else closeMergeSheet()

            AnimalListIntent.MergeConfirmed -> confirmMerge()

            else -> Unit
        }
    }

    private fun archive(id: Long, isArchive: Boolean) {
        viewModelScope.launch {
            if (isArchive) {
                // Беременную самку сначала спрашиваем: прервать беременность или только в архив
                if (pregnancyRepository.getActivePregnancyId(id) != null) {
                    sendIntent(AnimalListIntent.OpenArchivePregnancyDialog(id))
                    return@launch
                }
                animalRepository.updateArchiveAnimalTableById(true, id)
                showSnackbar(ProductOperation.ARCHIVE)
                return@launch
            }
            // Член сборной группы без своего поголовья при разархивации
            // становится самостоятельным — одной транзакцией в БД
            val name = getState().archiveList.find { it.id == id }?.name.orEmpty()
            val leftGroup = try {
                animalHerdRepository.unarchiveAnimal(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_error))
                return@launch
            }
            if (leftGroup) showMessage(
                resourceProvider.getString(R.string.snackbar_animal_group_left_on_unarchive).format(name)
            ) else showSnackbar(ProductOperation.UNARCHIVE)
        }
    }

    /** [interrupt] — гибель, забой или продажа самки: беременность прерывается, напоминания снимаются. */
    private fun archivePregnant(animalId: Long, interrupt: Boolean) {
        viewModelScope.launch {
            try {
                val active = if (interrupt) {
                    pregnancyRepository.getActivePregnancyId(animalId)
                        ?.let { pregnancyRepository.getPregnancy(it) }
                } else null
                if (active != null) {
                    // Прерывание и архивация — одной транзакцией
                    pregnancyRepository.interruptAndArchive(
                        pregnancyId = active.id,
                        reason = PregnancyInterruptReason.FEMALE_LOST,
                        date = dateToday(),
                        note = active.note,
                        animalId = animalId
                    )
                    pregnancyReminderScheduler.cancel(active.id)
                } else {
                    animalRepository.updateArchiveAnimalTableById(true, animalId)
                }
                showSnackbar(ProductOperation.ARCHIVE)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.pregnancy_snackbar_error))
            }
        }
    }

    /** Предупреждение в листе удаления: вместе с самкой удалится история беременностей. */
    private fun loadDeletePregnancyHistory(id: Long) {
        viewModelScope.launch {
            val hasHistory = pregnancyRepository.getPregnancies(id).first().isNotEmpty()
            if (getState().currentAnimal.id == id)
                updateState { it.copy(deleteHasPregnancyHistory = hasHistory) }
        }
    }

    /** Загрузка данных листа объединения; отменяется при закрытии листа. */
    private var mergeSheetJob: Job? = null

    private fun openMergeSheet(preselectedId: Long?) {
        // Лист уже открыт или грузится — не перезапускаем загрузку и не сбрасываем выбор
        if (getState().merge.isOpen || mergeSheetJob?.isActive == true) return
        mergeSheetJob = viewModelScope.launch {
            val candidates = animalHerdRepository.getStandaloneCandidates(itemIdPT).first()
                .map { it.toMergeCandidate() }
            val groups = animalHerdRepository.getActiveHerds(itemIdPT).first()
                .map { it.toMergeGroupChoice() }
            sendIntent(
                AnimalListIntent.RefreshMergeState(
                    MergeGroupState(
                        isOpen = true,
                        candidates = candidates,
                        selectedIds = setOfNotNull(preselectedId?.takeIf { id -> candidates.any { it.id == id } }),
                        groups = groups
                    )
                )
            )
        }
    }

    private fun closeMergeSheet() {
        mergeSheetJob?.cancel()
        mergeSheetJob = null
    }

    private fun confirmMerge() {
        val merge = getState().merge
        if (!merge.canConfirm) return
        sendIntent(AnimalListIntent.RefreshMergeState(merge.copy(isSaving = true)))
        viewModelScope.launch {
            val ids = merge.selectedIds.toList()
            try {
                val targetGroupId = merge.targetGroupId
                if (targetGroupId != null) {
                    animalHerdRepository.addMembers(targetGroupId, ids)
                    showMessage(resourceProvider.getString(R.string.snackbar_animal_group_added))
                } else {
                    val name = merge.name.trim()
                    animalHerdRepository.createHerdWithMembers(
                        // Иконку группы задаёт только пользователь; до этого в списке — иконки членов
                        DomainAnimalHerd(name = name, idPT = itemIdPT),
                        ids
                    )
                    showMessage(
                        resourceProvider.getString(R.string.snackbar_animal_group_merged).format(name)
                    )
                }
                onIntent(AnimalListIntent.OpenMergeSheet(false))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_error))
                sendIntent(AnimalListIntent.RefreshMergeState(getState().merge.copy(isSaving = false)))
            }
        }
    }

    private fun loadDate() {
        viewModelScope.launch {
            val isArchiveProject = projectRepository.getIsArchiveProject(itemIdPT).first()
            combine(
                animalRepository.getAllAnimalWithGroups(itemIdPT),
                animalHerdRepository.getActiveHerds(itemIdPT),
                settingsRepository.getSettings(itemIdPT),
                pregnancyRepository.getActivePregnanciesByProject(itemIdPT)
            ) { animalList, herds, settings, pregnancies ->
                LoadedAnimals(animalList, herds, settings, pregnancies.toPregnancyProgress())
            }.collectLatest { (list, herdList, settings, badges) ->
                val baseList = list.map { animal ->
                    animal.toAnimalListUi(resourceProvider).copy(pregnancyProgress = badges[animal.id])
                }
                val allActive = baseList.filter { !it.isArchive }
                val membersByHerd = allActive.filter { it.herdId != null }.groupBy { it.herdId }
                val herds = herdList.map { it.toUi(membersByHerd[it.id].orEmpty()) }
                    .filter { it.members.isNotEmpty() }
                val shownHerdIds = herds.map { it.id }.toSet()
                // Члены сборных групп на верхнем уровне не показываются — они внутри её карточки
                val animalList = herds.map { AnimalListItem.Herd(it) } +
                        allActive.filter { it.herdId !in shownHerdIds }.map { AnimalListItem.Animal(it) }
                val archiveAnimalList = baseList.filter { it.isArchive }
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = animalList,
                        allActive = allActive,
                        herds = herds,
                        archiveList = archiveAnimalList,
                        isLoading = false,
                        isArchiveProject = isArchiveProject,
                        settings = settings
                    )
                }
                // Пересчитываем поисковые списки с учётом текущего запроса
                sendIntent(AnimalListIntent.SearchChanged(getState().textSearch))
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else AnimalEntryState2()
                onIntent(
                    AnimalListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForEntry) {
                val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
                val breedList = animalRepository.getBreedAnimal(itemIdPT).first()
                AnimalEntryState2(
                    itemIdPT = itemIdPT,
                    typeList = typeList,
                    breedList = breedList
                )
            } else getState().currentProduct
            onIntent(
                AnimalListIntent.RefreshEntryBottomSheetState(true, newState)
            )
        }
    }


    override fun insert() {
        viewModelScope.launch {
            val idAnimal =
                animalRepository.insertAnimalTable(
                    getState().currentProduct.saveAnimal()
                )

            val pair = getState().currentProduct.updateForSave(idAnimal, itemIdPT)

            val id = animalCountRepository.insertAnimalCountTable(pair.first)

            pair.second?.let {
                expensesRepository.insertExpenses(it.copy(animalCountId = id))
            }
            yandexMetricRepository.metricalAnimal(getState().currentProduct)
            showSnackbar(ProductOperation.ADD)
            loadDataForEntryOrEdit(false)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().currentAnimal.let { product ->
                // Беременности удалятся каскадом — снимаем их напоминания заранее
                pregnancyReminderScheduler.cancelForAnimal(product.id)
                animalHerdRepository.deleteAnimal(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(AnimalListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, isGroup) = if (productOperation == ProductOperation.DELETE) getState().currentAnimal.name to getState().currentAnimal.isGroup
        else getState().currentProduct.title to getState().currentProduct.isAnimalGroup

        val (addTextRes, deleteTextRes) = if (isGroup) R.string.snackbar_animal_add_multiple to R.string.snackbar_animal_delete_multiple
        else R.string.snackbar_animal_add_single to R.string.snackbar_animal_delete_single

        val (archiveTextRes, unarchiveTextRes) = if (isGroup) R.string.snackbar_animal_archive_multiple to R.string.snackbar_animal_unarchive_multiple
        else R.string.snackbar_animal_archive_single to R.string.snackbar_animal_unarchive_single

        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(addTextRes).format(title)
                ProductOperation.ARCHIVE -> resourceProvider.getString(archiveTextRes)
                ProductOperation.UNARCHIVE -> resourceProvider.getString(unarchiveTextRes)
                else -> resourceProvider.getString(deleteTextRes).format(title)
            }
        )
    }

    private fun AnimalEntryState2.saveAnimal(): DomainAnimalTable = toDomainAnimal()

    private fun AnimalEntryState2.updateForSave(idAnimal: Long, itemIdPT: Long) = toFirstCount(
        idAnimal = idAnimal,
        itemIdPT = itemIdPT,
        currency = getState().settings.currencySuffix,
        expensesCategory = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses)
    )

    override fun update() {}
}
private data class LoadedAnimals(
    val animals: List<DomainAnimalWithCount>,
    val herds: List<DomainAnimalHerd>,
    val settings: DomainSettings,
    val badges: Map<Long, PregnancyProgress>
)

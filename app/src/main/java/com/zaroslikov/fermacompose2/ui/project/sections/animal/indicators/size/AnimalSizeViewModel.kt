package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.convertSize
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class AnimalSizeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalSizeRepository: AnimalSizeRepository,
    private val projectRepository: ProjectRepository,
    private val animalRepository: AnimalRepository,
    private val resourceProvider: ResourceProvider,   private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel2<AnimalSizeState, AnimalSizeIntent, AnimalSizeReduce>(
    AnimalSizeState(),
    AnimalSizeReduce()
) {
    val itemId: Long = checkNotNull(savedStateHandle[AnimalSizeDestination.itemId])
    val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalSizeDestination.itemIdPT])

    init {
        loadData()
    }

    override fun onIntent(intent: AnimalSizeIntent) {
        sendIntent(intent)
        return when (intent) {
            is AnimalSizeIntent.OpenDialogClicked -> loadDataForEntryOrEdit(
                intent.isEntry,
                intent.domainAnimalSize,
                intent.isSaveStateForBottomSheet
            )

            AnimalSizeIntent.InsertPressed -> insert()
            AnimalSizeIntent.UpdatePressed -> update()
            is AnimalSizeIntent.DeletePressed -> delete(0)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchiveProject = projectRepository.getIsArchiveProject(itemIdPT).first()
            val animal = animalRepository.getAnimal(itemId).first()
            animalSizeRepository.getSizeAnimal(itemId).collectLatest { sizeList ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        sizeList = settingsList(sizeList),
                        isLoading = false,
                        isArchive = isArchiveProject || animal.archive
                    )
                }
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: AnimalSizeUi?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else CurrentAnimalSize()
                onIntent(
                    AnimalSizeIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForEntry || domain != null) {
                val baseState = CurrentAnimalSize(
                    idAnimal = itemId
                )
                domain?.toCurrentAnimalSize() ?: baseState
            } else getState().currentProduct
            sendIntent(
                AnimalSizeIntent.RefreshEntryBottomSheetState(
                    true, newState
                )
            )
        }
    }

    override fun insert() {
        viewModelScope.launch {
            animalSizeRepository.insertAnimalSizeTable(getState().currentProduct.toDomain())
            showSnackbar(ProductOperation.ADD)
            yandexMetricRepository.metricalAnimalSize(getState().currentProduct)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            animalSizeRepository.updateAnimalSizeTable(getState().currentProduct.toDomain())
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().deleteSize?.let {
                animalSizeRepository.deleteAnimalSizeTableById(it.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(AnimalSizeIntent.OpenBottomSheetDelete(null))
            }
        }
    }


    private fun showSnackbar(productOperation: ProductOperation) {
        val (count, countSuffix) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().deleteSize ?: AnimalSizeUi()
                product.size to product.suffix
            } else {
                val product = getState().currentProduct
                product.size to product.suffix
            }
        val suffix = resourceProvider.getString(countSuffix.toResId())
        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_size_add)
                    .format(count, suffix)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_size_update)
                    .format(count, suffix)

                else -> resourceProvider.getString(R.string.snackbar_size_delete)
                    .format(count, suffix)
            }
        )
    }

    private fun settingsList(list: List<DomainAnimalSize>): List<AnimalSizeUi> {
        return list.mapIndexed { index, domain ->
            val previousDomain =
                if (index < list.size - 1) list[index + 1] else null

            val (totalValue, indicationStatus) = if (previousDomain == null)
                domain.size to IndicationStatus.POSITIVE
            else {
                val size = domain.size.toConvertZeroDouble()
                val previousSize = previousDomain.size.toConvertZeroDouble()

                val suffix = domain.suffix
                val suffixPrevious = previousDomain.suffix

                val sizeConverted = size.convertSize(suffix, to = Suffix.MILLIMETERS)
                val previousCountConverted =
                    previousSize.convertSize(suffixPrevious, to = Suffix.MILLIMETERS)

                val totalValue = (sizeConverted - previousCountConverted).convertSize(
                    Suffix.MILLIMETERS,
                    suffix
                ).absoluteValue.formatNumber()
                val status = when {
                    sizeConverted > previousCountConverted -> IndicationStatus.POSITIVE
                    sizeConverted == previousCountConverted -> IndicationStatus.NEUTRAL
                    else -> IndicationStatus.NEGATIVE
                }
                totalValue to status
            }
            domain.toAnimalSizeUi(
                totalValue = totalValue,
                indicationStatus = indicationStatus
            )
        }
    }

    private fun AnimalSizeUi.toCurrentAnimalSize(): CurrentAnimalSize {
        return CurrentAnimalSize(
            id = id,
            size = size,
            suffix = suffix,
            date = date,
            idAnimal = idAnimal,
            note = note,
            isEntry = false
        )
    }

    private fun CurrentAnimalSize.toDomain(): DomainAnimalSize {
        return DomainAnimalSize(
            id = id,
            size = size,
            suffix = suffix,
            date = date,
            idAnimal = idAnimal,
            note = note
        )
    }

    private fun DomainAnimalSize.toAnimalSizeUi(
        totalValue: String,
        indicationStatus: IndicationStatus
    ): AnimalSizeUi {
        return AnimalSizeUi(
            id = id,
            size = size,
            suffix = suffix,
            date = date,
            idAnimal = idAnimal,
            note = note,
            totalValue = totalValue,
            indicationStatus = indicationStatus
        )
    }
}
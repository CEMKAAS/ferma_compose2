package com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainIncubatorTable
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIncubatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val incubatorTableRepository: IncubatorTableRepository
) : EntryNewViewModel<AddIncubatorState, AddIncubatorIntent>(AddIncubatorState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[AddIncubatorDestination.itemIdArg])

    init {
        if (itemId != -1L) loadData() else loadDate2()
    }

    private fun loadDate2() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val modelList = incubatorTableRepository.getModelIncubatorList().first()
            val brandList = incubatorTableRepository.getBrandIncubatorList().first()
            updateState {
                it.copy(
                    isLoading = false,
                    isEntry = true,
                    currentProduct = it.currentProduct.copy(
                        brandList = brandList,
                        modelList = modelList
                    )
                )
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val project = projectRepository.getProject(itemId).first()
            val settings = incubatorTableRepository.getIncubator(itemId).first()
            val modelList = incubatorTableRepository.getModelIncubatorList().first()
            val brandList = incubatorTableRepository.getBrandIncubatorList().first()
            updateState {
                it.copy(
                    isLoading = false,
                    isEntry = false,
                    currentProject = project,
                    currentProduct = it.currentProduct.toUiMap(
                        project,
                        settings,
                        brandList,
                        modelList
                    )
                )
            }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                val id = projectRepository.insertProjectLong(
                    DomainProjectTable(
                        titleProject = getState().currentProduct.title.trim(),
                        data = getState().currentProduct.date,
                        mode = 1
                    )
                )
                incubatorTableRepository.insertIncubator(
                    getState().currentProduct.toDomainIncubatorTable(
                        id
                    )
                )
                navigateTo(UiEvent.NavigateBack)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                projectRepository.updateProject(
                    getState().currentProject.copy(
                        titleProject = getState().currentProduct.title.trim(),
                        data = getState().currentProduct.date
                    )
                )
                incubatorTableRepository.updateIncubator(
                    getState().currentProduct.toDomainIncubatorTable()
                )
                navigateTo(UiEvent.NavigateBack)
            }
        }
    }

    override fun delete(id: Long) {}

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank(),
                        isErrorCapacity = state.currentProduct.capacity.isBlank()
                    )
                )
            )
        }
    }

    override fun onIntent(intent: AddIncubatorIntent) {
        return when (intent) {
            is AddIncubatorIntent.TitleChanged -> updateTitle(intent.value)
            is AddIncubatorIntent.BrandChanged -> updateBrand(intent.value)
            is AddIncubatorIntent.ModelChanged -> updateModel(intent.value)
            is AddIncubatorIntent.CapacityChanged -> updateCapacity(intent.value)
            is AddIncubatorIntent.PriceChanged -> updatePrice(intent.value)
            is AddIncubatorIntent.NoteClicked -> updateNote(intent.value)
            is AddIncubatorIntent.DateClicked -> updateDate(intent.value)
            is AddIncubatorIntent.AutoRotationClicked -> updateAutoRotation(intent.value)
            is AddIncubatorIntent.AutoVentilationClicked -> updateAutoVentilation(intent.value)
            is AddIncubatorIntent.CurrencyClicked -> updateCurrentCurrency(intent.value)
            AddIncubatorIntent.Insert -> insert()
            AddIncubatorIntent.Update -> update()
        }
    }

    private fun updateTitle(title: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    title = title
                )
            )
        }
    }

    private fun updateBrand(brand: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    brand = brand
                )
            )
        }
    }

    private fun updateModel(model: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    model = model
                )
            )
        }
    }


    private fun updateCapacity(capacity: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    capacity = capacity
                )
            )
        }
    }

    private fun updatePrice(price: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    price = price
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    note = note
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    date = date
                )
            )
        }
    }

    private fun updateAutoRotation(isAutoRotation: Boolean) {

        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isAutoRotation = isAutoRotation
                )
            )
        }
    }

    private fun updateAutoVentilation(isAutoVentilation: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isAutoVentilation = isAutoVentilation
                )
            )
        }
    }

    private fun updateCurrentCurrency(currency: Suffix) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    currencySuffix = currency
                )
            )
        }
    }

    private fun AddIncubator.toUiMap(
        domainProjectTable: DomainProjectTable,
        domain: DomainIncubatorTable,
        brandList: List<String>,
        modelList: List<String>,
    ): AddIncubator {
        return copy(
            title = domainProjectTable.titleProject,
            model = domain.model ?: "",
            brand = domain.brand ?: "",
            capacity = domain.capacity.toString(),
            date = domainProjectTable.data,
            price = domain.price?.formatNumber(false) ?: "",
            note = domain.note,
            isAutoRotation = domain.isAutoRotation,
            isAutoVentilation = domain.isAutoVentilation,
            currencySuffix = domain.currencySuffix,
            brandList = brandList,
            modelList = modelList
        )
    }

    private fun AddIncubator.toDomainIncubatorTable(idPT: Long? = null): DomainIncubatorTable {
        return DomainIncubatorTable(
            id = id,
            model = model.ifBlank { null },
            brand = brand.ifBlank { null },
            capacity = capacity.toInt(),
            price = price.toConvertDbDouble(),
            note = note.trim(),
            isAutoRotation = isAutoRotation,
            isAutoVentilation = isAutoVentilation,
            currencySuffix = currencySuffix,
            idPT = idPT ?: itemId
        )
    }
}

sealed class AddIncubatorIntent() : BaseIntent {
    data class TitleChanged(val value: String) : AddIncubatorIntent()
    data class BrandChanged(val value: String) : AddIncubatorIntent()
    data class ModelChanged(val value: String) : AddIncubatorIntent()
    data class CapacityChanged(val value: String) : AddIncubatorIntent()
    data class PriceChanged(val value: String) : AddIncubatorIntent()
    data class DateClicked(val value: String) : AddIncubatorIntent()
    data class NoteClicked(val value: String) : AddIncubatorIntent()
    data class AutoRotationClicked(val value: Boolean) : AddIncubatorIntent()
    data class AutoVentilationClicked(val value: Boolean) : AddIncubatorIntent()
    data class CurrencyClicked(val value: Suffix) : AddIncubatorIntent()
    data object Insert : AddIncubatorIntent()
    data object Update : AddIncubatorIntent()
}
package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryIntent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalCountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalCountRepository: AnimalCountRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<AnimalCountState, AnimalCountIntent>(AnimalCountState()) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemIdPT])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    override fun onIntent(intent: AnimalCountIntent) {
        return when (intent) {
            is AnimalCountIntent.OpenDialogClicked -> updateOpenDialog(intent.value)
            AnimalCountIntent.EndDialogClicked -> updateEndDialog()
            is AnimalCountIntent.CountChanged -> updateCount(intent.value)
            is AnimalCountIntent.PriceClicked -> updatePrice(intent.value)
            is AnimalCountIntent.AutoPriceClicked -> updateAutoCalculate(intent.value)
            is AnimalCountIntent.BuyerChanged -> updateBuyer(intent.value)
            AnimalCountIntent.BuyerClearClicked -> updateBuyerClear()
            is AnimalCountIntent.DateClicked -> updateDate(intent.value)
            is AnimalCountIntent.NoteChanged -> updateNote(intent.value)
            AnimalCountIntent.InsertPressed -> {}
            AnimalCountIntent.UpdatePressed -> update()
            AnimalCountIntent.DeletePressed -> delete()
        }
    }

    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        animalCountRepository.getCountAnimal(itemId).collectLatest { countList ->
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    countList = countList,
                    isLoading = false
                )
            }
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorCount = state.domainAnimalCountPrice.count.isBlank(),
                    isErrorCountZero = isAnimalCountZero(state.domainAnimalCountPrice.count)
                )
            )
        }
    }

    private fun updateOpenDialog(
        domainAnimalCountPrice: DomainAnimalCountPrice
    ) {
        updateState {
            it.copy(
                isOpenDialog = true,
                domainAnimalCountPrice = domainAnimalCountPrice
            )
        }
    }

    private fun updateEndDialog() {
        updateState {
            it.copy(
                error = AnimalCountState.Error(),
                isOpenDialog = false
            )
        }
    }

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    count = count
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    date = date
                )
            )
        }
    }

    private fun updateAutoCalculate(isAutoCalculate: Boolean) {
        /*updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    date = date
                )
            )
        }*/
    }

    private fun updatePrice(price: String) {
        /*updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    date = date
                )
            )
        }*/
    }

    private fun updateBuyer(buyer: String) {
        /*updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    date = date
                )
            )
        }*/
    }

    private fun updateBuyerClear() {
        /*updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    date = date
                )
            )
        }*/
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                domainAnimalCountPrice = it.domainAnimalCountPrice.copy(
                    note = note
                )
            )
        }
    }

    override fun insert() {}

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                val domainCount = getState().domainAnimalCountPrice
                animalCountRepository.updateAnimalCountTable(
                    DomainAnimalCount(
                        id = domainCount.id,
                        count = domainCount.count,
                        suffix = domainCount.suffix,
                        date = domainCount.date,
                        note = domainCount.note,
                        version = domainCount.version,
                        idAnimal = domainCount.animalId
                    )
                )
                updateEndDialog()
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            animalCountRepository.deleteAnimalCountTable(getState().domainAnimalCountPrice.id)
            updateEndDialog()
        }
    }
}


sealed class AnimalCountIntent : BaseIntent {
    data class OpenDialogClicked(val value: DomainAnimalCountPrice) : AnimalCountIntent()
    data object EndDialogClicked : AnimalCountIntent()
    data class CountChanged(val value: String) : AnimalCountIntent()
    data class PriceClicked(val value: String) : AnimalCountIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalCountIntent()
    data class BuyerChanged(val value: String) : AnimalCountIntent()
    data object BuyerClearClicked : AnimalCountIntent()
    data class DateClicked(val value: String) : AnimalCountIntent()
    data class NoteChanged(val value: String) : AnimalCountIntent()
    data object InsertPressed : AnimalCountIntent()
    data object UpdatePressed : AnimalCountIntent()
    data object DeletePressed : AnimalCountIntent()
}
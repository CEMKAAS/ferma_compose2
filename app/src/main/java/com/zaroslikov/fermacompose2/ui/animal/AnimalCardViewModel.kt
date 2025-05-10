package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArg])
    val itemIdPT: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(AnimalEditUiState())
        private set
    var domainWeight by mutableStateOf(DomainIndicatorsVM())
        private set
    var domainHeight by mutableStateOf(DomainIndicatorsVM())
        private set
    var domainCount by mutableStateOf(DomainIndicatorsVM())
        private set
    var domainVaccination by mutableStateOf(DomainIndicatorsVM())
        private set
    var countInWarehouse by mutableDoubleStateOf(0.0)
        private set


    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getAnimal(itemId)
                .filterNotNull()
                .first()
                .toAnimaEditUiState()

            domainWeight = itemsRepository.getWeightAnimalLimit(itemId)
                .filterNotNull()
                .first()
                .toDomainMap()

            domainHeight = itemsRepository.getSizeAnimalLimit(itemId)
                .filterNotNull()
                .first()
                .toDomainMap()

            domainCount = itemsRepository.getCountAnimalLimit(itemId)
                .filterNotNull()
                .first()
                .toDomainMap()

            domainVaccination = itemsRepository.getVaccinationAnimalLimit(itemId)
                .filterNotNull()
                .first()
                .toDomainMap()
        }
    }

    val titleUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsTitleAddList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val buyerUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsBuyerSaleList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    fun updateUiState(name: String) {
        viewModelScope.launch {
            countInWarehouse = itemsRepository.getCurrentBalanceProduct(name, itemIdPT.toLong())
                .filterNotNull()
                .first()
                .toDouble()
        }
    }

    fun productState(name: String): StateFlow<AnimalProductCardUiStateLimit> {
        return itemsRepository.getProductAnimal(name)
            .map { AnimalProductCardUiStateLimit(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalProductCardUiStateLimit()
            )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun saveSaleAnimal(triple: Triple<SaleTable, AnimalCountTable, AnimalTable>) {
        viewModelScope.launch {
            itemsRepository.insertSale(triple.first)
            itemsRepository.insertAnimalCountTable(triple.second)
            itemsRepository.updateAnimalTable(triple.third)
        }
    }

    fun saveAddAnimal(addTable: AddTable) {
        viewModelScope.launch { itemsRepository.insertItem(addTable) }
    }

    fun saveCountAnimal(pair: Pair<AnimalCountTable, AnimalTable>) {
        viewModelScope.launch {
            itemsRepository.insertAnimalCountTable(pair.first)
            itemsRepository.updateAnimalTable(pair.second)
        }
    }

}


data class AnimalProductCardUiStateLimit(val itemList: List<AnimalTitSuff> = listOf())

data class AnimalTitSuff(
    val title: String,
    val priceAll: Double,
    val suffix: String
)
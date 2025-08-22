package com.zaroslikov.fermacompose2.ui.animal.animalCard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArg])
    val itemIdPT: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArgTwo])

    var animalUiState by mutableStateOf(AnimalCardState())
        private set
    var domainWeight by mutableStateOf<DomainIndicatorsVM?>(null)
        private set
    var domainHeight by mutableStateOf<DomainIndicatorsVM?>(null)
        private set
    var domainCount by mutableStateOf(DomainIndicatorsVM())
        private set
    var domainVaccination by mutableStateOf<DomainIndicatorsVM?>(null)
        private set
    var countInWarehouse by mutableDoubleStateOf(0.0)
        private set


    init {
        viewModelScope.launch {
            launch {
                itemsRepository.getAnimalCard(itemId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest {
                        animalUiState = animalUiState.updateFromDomain(it)
                    }
            }
            launch {
                itemsRepository.getWeightAnimalLimit(itemId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest {
                        it?.let {
                            domainWeight = it.toDomainMap()
                        }
                    }
            }
            launch {
                itemsRepository.getSizeAnimalLimit(itemId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest {
                        it?.let {
                            domainHeight = it.toDomainMap()
                        }
                    }
            }
            launch {
                itemsRepository.getCountAnimalLimit(itemId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest {
//                        domainCount = it.toDomainMap()
                    }
            }
            launch {
                itemsRepository.getVaccinationAnimalLimit(itemId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest {
                        it?.let {
                            domainVaccination = it.toDomainMap()
                        }
                    }
            }
        }
    }

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleAddList(itemIdPT).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )

    val buyerUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsBuyerSaleList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


    fun updateNote(note: String) {
        viewModelScope.launch {
            println("___________$note")
//            itemsRepository.updateAnimalTable(animalUiState.copy(note = note).toAnimalTable())
        }
    }

    fun updateArchive() {
        viewModelScope.launch {
//            itemsRepository.updateAnimalTable(animalUiState.copy(arhiv = true).toAnimalTable())
        }
    }

    suspend fun updateUiState(name: String): DomainPairDataDoubleSting {
        return itemsRepository
            .getCurrentBalanceProduct(name, itemIdPT.toLong())
            .filterNotNull()
            .first()
            .toDomainMap()
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

    fun saveAddAnimal(addTable: AddTable) {
        viewModelScope.launch { itemsRepository.insertItem(addTable) }
    }

    fun updateAnimalGroup(sex: String) {
        viewModelScope.launch {
            /*itemsRepository.updateAnimalTable(
                animalUiState.copy(groop = false, sex = sex).toAnimalTable()
            )*/
        }
    }

     //==================== Сохранение кол-ва ====================
    //Продажа
    fun insertSaleAnimal(triple: Triple<DomainIndicatorsVM, SaleTable, Boolean>) {
        viewModelScope.launch {
          /*  val id = itemsRepository.insertAnimalCountTable(triple.first.toCountRoomMap())
            itemsRepository.insertSale(triple.second.copy(animalCountId = id))
            itemsRepository.updateAnimalTable(
                animalUiState.copy(arhiv = triple.third).toAnimalTable()
            )*/
        }
    }
    //Покупка
    suspend fun insertAddAnimal(pair: Pair<DomainIndicatorsVM, ExpensesTable?>) {
        /*val id = itemsRepository.insertAnimalCountTable(pair.first.toCountRoomMap())
        itemsRepository.updateAnimalTable(animalUiState.copy(groop = true).toAnimalTable())
        pair.second?.let {
            itemsRepository.insertExpenses(
                it.copy(
                    animalId = itemId.toLong(),
                    animalCountId = id,
                    idPT = itemIdPT.toLong()
                )
            )
        }*/
    }

    //
    suspend fun insertWriteOffAnimal(triple: Triple<DomainIndicatorsVM, WriteOffTable?, Boolean>) {
        /*val id = itemsRepository.insertAnimalCountTable(triple.first.toCountRoomMap())
        triple.second?.let {
            itemsRepository.insertWriteOff(
                it.copy(animalCountId = id)
            )
        }
        itemsRepository.updateAnimalTable(
            animalUiState.copy(arhiv = triple.third).toAnimalTable()
        )*/
    }

    fun saveCountAnimal(pair: Triple<DomainIndicatorsVM, WriteOffTable, Boolean>) {
        viewModelScope.launch {
           /* val id = itemsRepository.insertAnimalCountTable(pair.first.toCountRoomMap())
            itemsRepository.insertWriteOff(pair.second.copy(animalCountId = id))
            itemsRepository.updateAnimalTable(
                animalUiState.copy(arhiv = pair.third).toAnimalTable()
            )*/
        }
    }


    fun updateUiState(state: AnimalCardState) {
        animalUiState =
            state
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class AnimalProductCardUiStateLimit(val itemList: List<AnimalTitSuff> = listOf())

data class AnimalTitSuff(
    val title: String,
    val priceAll: Double,
    val suffix: String
)

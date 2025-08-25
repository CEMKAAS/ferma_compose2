package com.zaroslikov.fermacompose2.ui.animal.animalCard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalCard
import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalCount
import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState.AddAnimal
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AddCardIntent {
    data class CountChanged(val count: String) : AddCardIntent()
    data class PriceChanged(val price: String) : AddCardIntent()
    data class AutoPriceChanged(val isAutoPrice: Boolean) : AddCardIntent()
    data class NoteChanged(val note: String) : AddCardIntent()
    data object SaveButton : AddCardIntent()
    data object Exit : AddCardIntent()
}


@HiltViewModel
class AnimalCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider,
    private val insertAnimalDataUseCase: InsertAnimalDataUseCase
) : ViewModel() {

    val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdPT])
    val itemId: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemId])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    var animalUiState by mutableStateOf(AnimalCardState())
        private set
    var countInWarehouse by mutableDoubleStateOf(0.0)
        private set

    private val _state = MutableStateFlow(AnimalCardState())
    val state: StateFlow<AnimalCardState> = _state.asStateFlow()


    fun onIntent(intent: AddCardIntent) {
        when (intent) {
            is AddCardIntent.CountChanged -> updateCount(intent.count)
            is AddCardIntent.AutoPriceChanged -> updateAutoPrice(intent.isAutoPrice)
            is AddCardIntent.PriceChanged -> updatePrice(intent.price)
            is AddCardIntent.NoteChanged -> updateNote(intent.note)
            AddCardIntent.Exit -> {}
            AddCardIntent.SaveButton -> {}
        }
    }

    private fun updateState(update: (AddAnimal) -> AddAnimal) {
        _state.update { current ->
            current.copy(addAnimal = update(current.addAnimal))
        }
    }

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                count = count,
                isErrorCount = count.isBlank()
            )
        }
        updatePriceAll()
    }

    private fun updatePrice(price: String) {
        updateState { it.copy(price = price) }
        updatePriceAll()
    }

    private fun updateAutoPrice(isAutoPrice: Boolean) {
        updateState { it.copy(isAutoPrice = isAutoPrice) }
        updatePriceAll()
    }

    private fun updateNote(note: String) {
        updateState { it.copy(note = note) }
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                priceAll = if (it.isAutoPrice) (it.price.toConvertZeroDouble() * it.count.toConvertZeroDouble()).formatNumber() else "0",
            )
        }
    }

    init {
        viewModelScope.launch {
            launch {
                itemsRepository.getAnimalCard(itemId.toInt())
                    .flowOn(Dispatchers.IO).onStart {
                        _isLoading.value = true
                    }.onEach {
                        _isLoading.value = false
                    }.collectLatest {
                        animalUiState = animalUiState.updateFromDomain(it)
                    }
            }
        }
    }

    fun update(animalCardState: AnimalCardState) {
        animalUiState = animalCardState
    }

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleAddList(itemIdPT).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )

    val buyerUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsBuyerSaleList(itemIdPT.toInt()).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


    fun updateNote() {
        viewModelScope.launch {
//            itemsRepository.updateAnimalTable(animalUiState.)
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
//        viewModelScope.launch { itemsRepository.insertItem(addTable) }
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
    /* suspend fun insertAddAnimal(pair: Pair<DomainIndicatorsVM, ExpensesTable?>) {
         val id = itemsRepository.insertAnimalCountTable(pair.first.toCountRoomMap())
         itemsRepository.updateAnimalTable(animalUiState.copy(groop = true).toAnimalTable())
         pair.second?.let {
             itemsRepository.insertExpenses(
                 it.copy(
                     animalId = itemId.toLong(),
                     animalCountId = id,
                     idPT = itemIdPT.toLong()
                 )
             )
         }
     }*/

    private fun insertAddAnimal() {
        viewModelScope.launch {
            insertAnimalDataUseCase(_state.value, itemIdPT, itemId)
        }
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

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

class InsertAnimalDataUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
) {
    /* val reasonNote = if ((priceInDB.isBlank() || priceInDB == "0")) {
                if (note == "") stringResource(R.string.animal_card_screen_add_no_note_reason)
                else stringResource(R.string.animal_card_screen_add_note_reason, note)
            } else ""
            if (isErrorAddAnimal(count = countAnimal, isErrorCount = { isErrorCount = it })
            ) {
                val count =
                    (countAnimal.toConvertOnlyInt().toInt() + countAll.toInt()).toString()
                        .toConvertOnlyInt()*/

    suspend operator fun invoke(
        state: AnimalCardState,
        itemIdPT: Long,
        itemId: Long
    ): Result<Long> {
        return try {
            // Сохраняем основную запись
            val countId = itemsRepository.insertAnimalCountTable(
                DomainAnimalCount(
                    id = 0,
                    count = state.addAnimal.count,
                    suffix = state.countAnimalSuffix.toString(),
                    date = dateToday(),
                    idAnimal = itemIdPT,
                    note = state.addAnimal.note,
                    version = if (state.addAnimal.price.isBlank()) 4 else 1
                )
            )
            itemsRepository.updateAnimalTable(
                DomainAnimalTable(
                    id = itemId,
                    name = state.name,
                    type = state.type,
                    date = state.date,
                    dateFactory = state.dateFactory,
                    group = state.group,
                    sex = state.sex,
                    note = state.note,
                    image = null,
                    archive = state.archive,
                    foodDay = state.foodDay,
                    foodDaySuffix = state.foodDaySuffix,
                    idPT = itemIdPT
                )
            )
            // Сохраняем расходы если есть цена
            /*  if (state.addAnimal.price.isNotBlank()) {
                  itemsRepository.insertExpenses(
                      DomainExpensesTable(
                          id = 0,
                          title = state.name,
                          count = state.countAnimal!!.toConvertDbDouble(),
                          day = dateTodayArray()[0],
                          month = dateTodayArray()[1],
                          year = dateTodayArray()[2],
                          price = state.addAnimal.price.toConvertDbDouble(),
                          countSuffix = state.countAnimalSuffix.toString(),
                          category = "",
                          note = state.addAnimal.note,
                          isShowFood = false,
                          isShowWarehouse = false,
                          isShowAnimals = false,
                          isShowFoodHand = false,
                          idPT = itemIdPT.toLong(),
                          animalId = itemId.toLong(),
                          animalCountId = countId
                      )
                  )
              }*/

            Result.success(countId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


data class AnimalProductCardUiStateLimit(val itemList: List<AnimalTitSuff> = listOf())

data class AnimalTitSuff(
    val title: String,
    val priceAll: Double,
    val suffix: String
)

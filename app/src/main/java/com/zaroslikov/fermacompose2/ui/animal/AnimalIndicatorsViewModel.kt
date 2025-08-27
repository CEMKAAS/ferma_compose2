package com.zaroslikov.fermacompose2.ui.animal

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.repository.ItemsRepository
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.mapper.toSizeRoomMap
import com.zaroslikov.data.room.mapper.toVaccinationRoomMap
import com.zaroslikov.data.room.mapper.toWeightRoomMap
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalIndicatorsViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArg])
    val indicators: Int =
        checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArgTwo])
    val idPT: Long = checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArgTree])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    var indicationUiState by mutableStateOf<DomainIndicatorsVM?>(null)
        private set
    var expensesUiTable by mutableStateOf<ExpensesTable?>(null)
        private set
    var saleUiTable by mutableStateOf<SaleTable?>(null)
        private set
    var writeOffUiTable by mutableStateOf<WriteOffTable?>(null)
        private set
    var animalUiState by mutableStateOf<AnimalTable?>(null)
        private set
    var animalCountUiState by mutableStateOf<DomainIndicatorsVM?>(null)
        private set
    var priceCount by mutableStateOf<Pair<Double, Double>?>(null)
        private set


    suspend fun updateUiState(animalIndicatorsVM: DomainIndicatorsVM) {
        indicationUiState = animalIndicatorsVM
        priceCount = when (indicators) {
            3 -> {
                expensesUiTable =
                    itemsRepository.getItemExpensesForVaccination(animalIndicatorsVM.id.toLong())
                        .first()
                (expensesUiTable?.price ?: 0.0) to (expensesUiTable?.count ?: 0.0)
            }

            2 -> {
                (update2(animalIndicatorsVM.version, animalIndicatorsVM.id) ?: 0.0) to 0.0
            }

            else -> null
        }
    }

    suspend fun updateAnimalGroup(sex: String) {
        println("sex: $sex")
        println("animalUiState: $animalUiState")
        animalUiState?.let {
            println("update: true")
//            itemsRepository.updateAnimalTable(
//                it.copy(
////                    sex = sex,
//                    group = false
//                )
//            )
        }
    }


    init {
        viewModelScope.launch {
//            animalUiState = itemsRepository.getAnimal(itemId).first()
//            animalCountUiState = itemsRepository.getCountAnimalLimit(itemId).first().toDomainMap()
        }
    }

    val indicatorsUiState: StateFlow<AnimalIndicatorsUiState> = when (indicators) {

//        3 -> itemsRepository.getVaccinationAnimal(itemId).map { list ->
//            val mappedList = list.map { item -> item.toDomainMap() }
//            AnimalIndicatorsUiState(mappedList)
//        }.onStart {
//            _isLoading.value = true
//        }.onEach {
//            _isLoading.value = false
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//            initialValue = AnimalIndicatorsUiState()
//        )

        2 -> itemsRepository.getCountAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AnimalIndicatorsUiState()
        )

        1 -> itemsRepository.getSizeAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AnimalIndicatorsUiState()
        )

        0 -> itemsRepository.getWeightAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AnimalIndicatorsUiState()
        )

        else -> itemsRepository.getCountAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AnimalIndicatorsUiState()
        )

    }

    fun saveItem(
        animalIndicatorsVM: DomainIndicatorsVM,
        price: String?,
        count: Double,
        context: Context
    ) {
        viewModelScope.launch {
            when (indicators) {
                3 -> {
                    val vaccinationId =
                        itemsRepository.insertAnimalVaccinationTable(animalIndicatorsVM.toVaccinationRoomMap())
                    if (price != null) {
                        val animalName = itemsRepository.getAnimalCard(itemId).first().name

                        val suffix = getString(context, R.string.suffix_pieces)
                        val category = getString(
                            context,
                            R.string.animal_indicators_category_expenses_vaccination
                        )
                        val note =
                            getString(
                                context,
                                R.string.animal_indicators_note_expenses_vaccination
                            ) + animalName

                        val dateArray = animalIndicatorsVM.date.split(".").map { it.toInt() }
                        itemsRepository.insertExpenses(
                            ExpensesTable(
                                title = animalIndicatorsVM.weight,
                                count = count,
                                day = dateArray[0],
                                mount = dateArray[1],
                                year = dateArray[2],
                                price = price.toConvertZeroDouble(),
                                countSuffix = suffix,
                                category = category,
                                note = note,
                                isShowFood = false,
                                isShowWarehouse = false,
                                isShowAnimals = false,
                                isShowFoodHand = false,
                                feedFood = 0.0,
                                countAnimal = 0,
                                foodDesignedDay = 0,
                                lastDayFood = "",
                                idPT = idPT,
                                animalId = itemId.toLong(),
                                animalVaccinationId = vaccinationId,
                                priceAll = 0.0,
                                feedFoodSuffix = "",
                                weight = 0.0,
                                weightSuffix = "",
                            )
                        )
                    }
                }

                2 -> {
//                    itemsRepository.insertAnimalCountTable(animalIndicatorsVM.toCountRoomMap())
                }

                1 -> itemsRepository.insertAnimalSizeTable(animalIndicatorsVM.toSizeRoomMap())
                0 -> itemsRepository.insertAnimalWeightTable(animalIndicatorsVM.toWeightRoomMap())
            }
        }

    }

    fun updateItem(
        animalIndicatorsVM: DomainIndicatorsVM,
        price: String?,
        count: Double,
        context: Context,
    ) {
        viewModelScope.launch {
            when (indicators) {
                3 -> {
                    itemsRepository.updateAnimalVaccinationTable(animalIndicatorsVM.toVaccinationRoomMap())
                    if (price != null) {
                        val animalName = itemsRepository.getAnimalCard(itemId).first().name

                        val suffix = getString(context, R.string.suffix_pieces)
                        val category = getString(
                            context,
                            R.string.animal_indicators_category_expenses_vaccination
                        )
                        val note =
                            getString(
                                context,
                                R.string.animal_indicators_note_expenses_vaccination
                            ) + animalName
                        val dateArray = animalIndicatorsVM.date.split(".").map { it.toInt() }

                        if (expensesUiTable != null) {
                            expensesUiTable?.copy(
                                title = animalIndicatorsVM.weight,
                                count = count,
                                day = dateArray[0],
                                mount = dateArray[1],
                                year = dateArray[2],
                                price = price.toConvertZeroDouble(),
                            )?.let {
                                itemsRepository.updateExpenses(
                                    it
                                )
                            }
                        } else
                            itemsRepository.insertExpenses(
                                ExpensesTable(
                                    title = animalIndicatorsVM.weight,
                                    count = count,
                                    day = dateArray[0],
                                    mount = dateArray[1],
                                    year = dateArray[2],
                                    price = price.toConvertZeroDouble(),
                                    countSuffix = suffix,
                                    category = category,
                                    note = note,
                                    isShowFood = false,
                                    isShowWarehouse = false,
                                    isShowAnimals = false,
                                    isShowFoodHand = false,
                                    feedFood = 0.0,
                                    countAnimal = 0,
                                    foodDesignedDay = 0,
                                    lastDayFood = "",
                                    idPT = idPT,
                                    animalId = itemId.toLong(),
                                    animalVaccinationId = animalIndicatorsVM.id.toLong(),
                                    priceAll = 0.0,
                                    feedFoodSuffix = "",
                                    weight = 0.0,
                                    weightSuffix = "",
                                )
                            )
                    }
                }

                2 -> {
//                    itemsRepository.updateAnimalCountTable(animalIndicatorsVM.toCountRoomMap())
                    update(
                        count = animalIndicatorsVM.weight,
                        price = price?.toConvertZeroDouble() ?: 0.0,
                        suffix = animalIndicatorsVM.suffix,
                    )
                }

                1 -> itemsRepository.updateAnimalSizeTable(animalIndicatorsVM.toSizeRoomMap())
                0 -> itemsRepository.updateAnimalWeightTable(animalIndicatorsVM.toWeightRoomMap())

                else -> {}
            }
        }
    }

    fun deleteItem(animalIndicatorsVM: DomainIndicatorsVM) {
        viewModelScope.launch {
            when (indicators) {

                3 -> itemsRepository.deleteAnimalVaccinationTable(animalIndicatorsVM.toVaccinationRoomMap())
//                2 -> itemsRepository.deleteAnimalCountTable(animalIndicatorsVM.toCountRoomMap())
                1 -> itemsRepository.deleteAnimalSizeTable(animalIndicatorsVM.toSizeRoomMap())
                0 -> itemsRepository.deleteAnimalWeightTable(animalIndicatorsVM.toWeightRoomMap())

                else -> {}
            }
        }
    }

    suspend fun update(count: String, price: Double, suffix: String) {
        when (indicators) {
            3 -> expensesUiTable?.let {
                itemsRepository.updateExpenses(
                    it.copy(
                        count = count.toDouble(),
                        price = price,
                        countSuffix = suffix
                    )
                )
            }

            2 -> saleUiTable?.let {
                itemsRepository.updateSale(
                    it.copy(
                        count = count.toDouble(), priceAll = price, countSuffix = suffix
                    )
                )
            }

            1 -> writeOffUiTable?.let {
                itemsRepository.updateWriteOff(
                    it.copy(
                        count = count.toDouble(), priceAll = price, countSuffix =  suffix
                    )
                )
            }

            0 -> writeOffUiTable?.let {
                itemsRepository.updateWriteOff(
                    it.copy(
                        count = count.toDouble(), priceAll = price, countSuffix = suffix
                    )
                )
            }

            else -> {}
        }
    }

    suspend fun update2(version: Int?, idCount: Int): Double? {
        return when (version) {
            0 -> {
                saleUiTable = itemsRepository.getItemSaleIdCountAnimal(idCount).first()
                saleUiTable?.price
            }

            1 -> {
                expensesUiTable = itemsRepository.getItemExpensesIdAnimalCount(idCount).first()
                expensesUiTable?.price
            }

            2, 3 -> {
                writeOffUiTable = itemsRepository.getItemWriteOffIdCountAnimal(idCount).first()
                writeOffUiTable?.priceAll
            }

            else -> null
        }
    }

//    suspend fun insert2(
//        version: Int?,
//        expensesTable: ExpensesTable? = null,
//        saleTable: SaleTable? = null,
//        writeOffTable: WriteOffTable? = null
//    ) {
//        when (version) {
//            0 -> saleTable?.let { itemsRepository.insertSale(it) }
//            1 -> expensesTable?.let { itemsRepository.getExpenses(it) }
//            2, 3 -> writeOffTable?.let { itemsRepository.getItemWriteOff(it) }
//        }
//    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class AnimalIndicatorsUiState(val itemList: List<DomainIndicatorsVM> = listOf())




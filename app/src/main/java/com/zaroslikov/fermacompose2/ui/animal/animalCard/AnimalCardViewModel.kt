package com.zaroslikov.fermacompose2.ui.animal.animalCard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountDifference
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountIncrease
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAnimalSale
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnimalCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<AnimalCardState, AnimalCardIntent>(AnimalCardState()) {

    val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdPT])
    val itemId: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemId])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    var animalUiState by mutableStateOf(AnimalCardState())
        private set
    var countInWarehouse by mutableDoubleStateOf(0.0)
        private set

    private suspend fun loadData() {
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalSizeRepository.getSizeAnimalLimit(itemId),
            animalVaccinationRepository.getVaccinationAnimalLimit(itemId),
            animalWeightRepository.getWeightAnimalLimit(itemId)
        ) { animal, count, size, vacc, weight ->
            AnimalData(animal, count, size, vacc, weight)
        }.collectLatest { data ->
            updateState {
                it.copy(
                    animal = data.animal,
                    countAnimal = data.count,
                    size = data.size,
                    vaccination = data.vacc,
                    weight = data.weight,
                    saleAnimal = it.saleAnimal.copy(
                        countAnimal = if (data.animal.group) "" else "1"
                    ),
                    writeOffAnimal = it.writeOffAnimal.copy(
                        countAnimal = if (data.animal.group) "" else "1"
                    )
                )
            }
        }
    }


    fun onIntent(intent: AnimalCardIntent) {
        when (intent) {
            // Animal Card
            is AnimalCardIntent.DialogSoloClicked -> updateSoloDialog(intent.value)
            is AnimalCardIntent.SexClicked -> updateSex(intent.value)
            AnimalCardIntent.SaveGroupPressed -> saveSoloAnimal()

            // Animal Note Animal
            is AnimalCardIntent.NoteChanged -> updateNote(intent.value)

            // Add-Expenses Count Animal
            is AnimalCardIntent.DialogAddClicked -> updateState { it.copy(openAddDialog = intent.value) }
            is AnimalCardIntent.CountAddChanged -> updateCount(intent.count)
            is AnimalCardIntent.AutoPriceAddClicked -> updateAutoPrice(intent.isAutoPrice)
            is AnimalCardIntent.PriceAddChanged -> updatePrice(intent.price)
            is AnimalCardIntent.NoteAddChanged -> updateAddCountNote(intent.note)
            AnimalCardIntent.SaveAddPressed -> saveAddAnimal()

            // Sale Count Animal
            is AnimalCardIntent.DialogSaleClicked -> updateSaleDialog(intent.value)
            is AnimalCardIntent.CountSaleChanged -> updateCountSale(intent.value)
            is AnimalCardIntent.PriceSaleChanged -> updatePriceSale(intent.value)
            is AnimalCardIntent.AutoPriceSaleClicked -> updateAutoCalculateSale(intent.value)
            is AnimalCardIntent.BuyerSaleChanged -> updateBuyerSale(intent.value)
            AnimalCardIntent.SaveSalePressed -> saveSaleAnimal()

            // WriteOff Count Animal
            is AnimalCardIntent.DialogWriteOffClicked -> TODO()
            is AnimalCardIntent.CountWriteOffChanged -> TODO()
            is AnimalCardIntent.PriceWriteOffChanged -> TODO()
            is AnimalCardIntent.AutoPriceWriteOffClicked -> TODO()
            is AnimalCardIntent.NoteWriteOffChanged -> TODO()
            AnimalCardIntent.SaveWriteOffPressed -> TODO()
        }
    }

    // Add-Expenses Count Animal
    private fun updateCount(count: String) {
        updateState { state ->
            state.copy(
                addAnimal = state.addAnimal.copy(
                    count = count,
                    isErrorCount = count.isBlank()
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePrice(price: String) {
        updateState { state ->
            state.copy(
                addAnimal = state.addAnimal.copy(
                    count = price
                )
            )
        }
        updatePriceAll()
    }

    private fun updateAutoPrice(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                addAnimal = state.addAnimal.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updateAddCountNote(note: String) {
        updateState { state ->
            state.copy(
                addAnimal = state.addAnimal.copy(
                    note = note
                )
            )
        }
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                addAnimal = it.addAnimal.copy(
                    priceAll = if (it.addAnimal.isAutoPrice) (it.addAnimal.price.toConvertZeroDouble() * it.addAnimal.count.toConvertZeroDouble()).formatNumber() else "0",
                )
            )
        }
    }

    private fun validationSaveAddAnimal() {
        updateState { state ->
            state.copy(
                addAnimal = state.addAnimal.copy(
                    isErrorCount = state.addAnimal.count.isBlank()
                )
            )
        }
    }

    private fun updateNote(note: String) {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    animal = state.animal.copy(
                        note = note
                    )
                )
            }
            animalRepository.updateAnimalTable(getState().animal)
        }
    }

    fun saveAddAnimal() {
        viewModelScope.launch {
            val state = getState().addAnimal
            validationSaveAddAnimal()
            if (state.isErrorCount) {
                val reasonNote = resourceProvider.getString(
                    if (state.note.isBlank())
                        R.string.animal_card_screen_add_no_note_reason
                    else
                        R.string.animal_card_screen_add_note_reason
                ).format(state.note)

                val domainCountAnimal = DomainAnimalCount(
                    count = state.count,
                    suffix = state.countSuffix,
                    date = dateToday(),
                    idAnimal = itemIdPT,
                    note = reasonNote,
                    version = if (state.price.isBlank()) 4 else 1
                )

                val countId = animalCountRepository.insertAnimalCountTable(domainCountAnimal)
                animalRepository.updateAnimalTable(getState().animal.copy(group = true))

                // Сохраняем расходы если есть цена
                if (state.price.isNotBlank()) {
                    expensesRepository.insertExpenses(
                        DomainExpensesTable(
                            id = 0,
                            title = getState().animal.name,
                            count = state.count.toConvertDbDouble(),
                            day = dateTodayArray()[0],
                            month = dateTodayArray()[1],
                            year = dateTodayArray()[2],
                            price = state.price.toConvertDbDouble(),
                            priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                            countSuffix = state.countSuffix,
                            category = TODO(),
                            note = TODO(),
                            isShowFood = false,
                            isShowWarehouse = false,
                            isShowAnimals = false,
                            isShowFoodHand = false,
                            idPT = itemIdPT,
                            animalId = itemId,
                            animalCountId = countId
                        )
                    )
                }
            }
        }
    }

    // Sale Count Animal
    private fun updateSaleDialog(openDialog: Boolean) {
        viewModelScope.launch {
            val buyer =
                if (openDialog) saleRepository.getItemsBuyerSaleList(itemIdPT)
                    .first() else getState().buyerList

            updateState { state ->
                state.copy(openSaleDialog = openDialog, buyerList = buyer)
            }
        }
    }

    private fun updateCountSale(countAnimal: String) {
        updateState { state ->
            state.copy(
                saleAnimal = state.saleAnimal.copy(
                    countAnimal = countAnimal,
                    error = state.saleAnimal.error.copy(
                        isErrorCount = countAnimal.isBlank(),
                        isErrorCountMore = isAnimalCountIncrease(
                            countAnimal,
                            getState().countAnimal.count
                        ),// текущее кол-во животных
                        isErrorCountZero = isAnimalCountZero(countAnimal)
                    )
                )
            )
        }
    }

    private fun updatePriceSale(price: String) {
        updateState { state ->
            state.copy(
                saleAnimal = state.saleAnimal.copy(
                    price = price,
                    error = state.saleAnimal.error.copy(
                        isErrorPrice = price.isBlank()
                    )
                )
            )
        }
    }

    private fun updateAutoCalculateSale(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                saleAnimal = state.saleAnimal.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
    }

    private fun updateBuyerSale(buyer: String) {
        updateState { state ->
            state.copy(
                saleAnimal = state.saleAnimal.copy(
                    buyer = buyer
                )
            )
        }
    }

    fun saveSaleAnimal() {
        viewModelScope.launch {
            val state = getState().saleAnimal
            // TODO validation
            if (state.error.hasAnyError) {

                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.countSuffix,
                        date = dateToday(),
                        idAnimal = itemId,
                        note = "",
                        version = 0
                    )
                )

                saleRepository.insertSale(
                    DomainSaleTable(
                        title = getState().animal.name,
                        count = state.countAnimal.toConvertDbDouble(),
                        countSuffix = state.countSuffix,
                        price = state.price.toConvertDbDouble(),
                        priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                        day = dateTodayArray()[0],
                        month = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        category = resourceProvider.getString(R.string.animal_card_screen_category_sale),
                        buyer = if (state.buyer.isBlank()) null else state.buyer,
                        note = resourceProvider.getString(R.string.animal_card_screen_note_sale),
                        idPT = itemIdPT,
                        animalId = itemId,
                        animalCountId = countId
                    )
                )

                val isAnimalGroup = if (getState().animal.group) isAnimalCountZero(
                    state.countAnimal,
                    getState().countAnimal.count
                ) else true

                val count = if (getState().animal.group) isAnimalCountDifference(
                    state.countAnimal,
                    getState().countAnimal.count
                ) else state.countAnimal

                animalRepository.insertAnimalTable(getState().animal.copy(group = isAnimalGroup))

                updateSoloDialog(count.toInt() == 1 && isAnimalGroup)
            }
        }
    }

    // WriteOff Animal

    private fun updateCountWriteOff(count: String) {
        updateState { state ->
            state.copy(
                writeOffAnimal = state.writeOffAnimal.copy(
                    countAnimal = count,
                    error = state.writeOffAnimal.error.copy(
                        isErrorCount = count.isBlank(),
                        isErrorCountMore = isAnimalCountIncrease(
                            count,
                            getState().countAnimal.count
                        )
                    )
                )
            )
        }
    }

    private fun updatePriceWriteOff(price: String) {
        updateState { state ->
            state.copy(
                writeOffAnimal = state.writeOffAnimal.copy(
                    price = price,
                )
            )
        }
    }

    private fun updateAutoCalculateWriteOff(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                writeOffAnimal = state.writeOffAnimal.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
    }

    private fun updateNoteWriteOff(note: String) {
        updateState { state ->
            state.copy(
                writeOffAnimal = state.writeOffAnimal.copy(
                    note = note
                )
            )
        }
    }

    fun saveSaleAnimal() {
        viewModelScope.launch {
            val state = getState().writeOffAnimal
            // TODO validation
            if (state.error.hasAnyError) {

                val reasonNote = resourceProvider.getString(
                    if (state.note.isBlank())
                        R.string.animal_card_screen_add_no_note_reason
                    else
                        R.string.animal_card_screen_add_note_reason
                ).format(state.note)

                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.countSuffix,
                        date = dateToday(),
                        idAnimal = itemId,
                        note = reasonNote,
                        version = 3
                    )
                )

                // TODO Раньше если цена пустая, то не пропускали, нужно проверить
                writeOffRepository.insertWriteOff(
                    DomainWriteOffTable(
                        title = getState().animal.name,
                        count = state.countAnimal.toConvertDbDouble(),
                        countSuffix = state.countSuffix,
                        price = if (state.price.isNotBlank()) state.price.toConvertDbDouble() else null,
                        priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                        day = dateTodayArray()[0],
                        month = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        status = true,
                        note = resourceProvider.getString(R.string.animal_card_screen_note_sale),
                        idPT = itemIdPT,
                        animalCountId = countId
                    )
                )

                val isAnimalGroup = if (getState().animal.group) isAnimalCountZero(
                    state.countAnimal,
                    getState().countAnimal.count
                ) else true

                val count = if (getState().animal.group) isAnimalCountDifference(
                    state.countAnimal,
                    getState().countAnimal.count
                ) else state.countAnimal

                animalRepository.insertAnimalTable(getState().animal.copy(group = isAnimalGroup))

                // TODO при нуле мы уехоидим со страницы
                //  count.toInt() == 0

                updateSoloDialog(count.toInt() == 1 && isAnimalGroup)
            }
        }
    }




    // Animal Card

    private fun updateSoloDialog(openDialog: Boolean) {
        updateState {
            it.copy(
                openSoloDialog = openDialog
            )
        }
    }

    private fun updateSex(sex: Boolean) {
        updateState { state ->
            state.copy(
                animal = state.animal.copy(
                    sex = sex
                )
            )
        }
    }

    private fun saveSoloAnimal() {
        viewModelScope.launch {
            animalRepository.insertAnimalTable(getState().animal.copy(group = true))
        }
    }


}


/*
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


    fun updateAnimalGroup(sex: String) {
        viewModelScope.launch {
            *//*itemsRepository.updateAnimalTable(
                animalUiState.copy(groop = false, sex = sex).toAnimalTable()
            )*//*
        }
    }

    //==================== Сохранение кол-ва ====================
    //Продажа
    fun insertSaleAnimal(triple: Triple<DomainIndicatorsVM, SaleTable, Boolean>) {
        viewModelScope.launch {
            *//*  val id = itemsRepository.insertAnimalCountTable(triple.first.toCountRoomMap())
              itemsRepository.insertSale(triple.second.copy(animalCountId = id))
              itemsRepository.updateAnimalTable(
                  animalUiState.copy(arhiv = triple.third).toAnimalTable()
              )*//*
        }
    }

    //Покупка
    *//* suspend fun insertAddAnimal(pair: Pair<DomainIndicatorsVM, ExpensesTable?>) {
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
     }*//*

    private fun insertAddAnimal() {
        viewModelScope.launch {
            insertAnimalDataUseCase(_state.value, itemIdPT, itemId)
        }
    }


    //
    suspend fun insertWriteOffAnimal(triple: Triple<DomainIndicatorsVM, WriteOffTable?, Boolean>) {
        *//*val id = itemsRepository.insertAnimalCountTable(triple.first.toCountRoomMap())
    triple.second?.let {
        itemsRepository.insertWriteOff(
            it.copy(animalCountId = id)
        )
    }
    itemsRepository.updateAnimalTable(
        animalUiState.copy(arhiv = triple.third).toAnimalTable()
    )*//*
    }

    fun saveCountAnimal(pair: Triple<DomainIndicatorsVM, WriteOffTable, Boolean>) {
        viewModelScope.launch {
            *//* val id = itemsRepository.insertAnimalCountTable(pair.first.toCountRoomMap())
         itemsRepository.insertWriteOff(pair.second.copy(animalCountId = id))
         itemsRepository.updateAnimalTable(
             animalUiState.copy(arhiv = pair.third).toAnimalTable()
         )*//*
        }
    }*/

sealed class AnimalCardIntent : BaseIntent {
    // Animal Card
    data class DialogSoloClicked(val value: Boolean) : AnimalCardIntent()
    data class SexClicked(val value: Boolean) : AnimalCardIntent()
    data object SaveGroupPressed : AnimalCardIntent()

    // Animal Note Animal
    data class NoteChanged(val value: String) : AnimalCardIntent()

    // Add-Expenses Count Animal
    data class DialogAddClicked(val value: Boolean) : AnimalCardIntent()
    data class CountAddChanged(val count: String) : AnimalCardIntent()
    data class PriceAddChanged(val price: String) : AnimalCardIntent()
    data class AutoPriceAddClicked(val isAutoPrice: Boolean) : AnimalCardIntent()
    data class NoteAddChanged(val note: String) : AnimalCardIntent()
    data object SaveAddPressed : AnimalCardIntent()

    // Sale Count Animal
    data class DialogSaleClicked(val value: Boolean) : AnimalCardIntent()
    data class CountSaleChanged(val value: String) : AnimalCardIntent()
    data class PriceSaleChanged(val value: String) : AnimalCardIntent()
    data class AutoPriceSaleClicked(val value: Boolean) : AnimalCardIntent()
    data class BuyerSaleChanged(val value: String) : AnimalCardIntent()
    data object SaveSalePressed : AnimalCardIntent()

    // WriteOff Count Animal
    data class DialogWriteOffClicked(val value: Boolean) : AnimalCardIntent()
    data class CountWriteOffChanged(val value: String) : AnimalCardIntent()
    data class PriceWriteOffChanged(val value: String) : AnimalCardIntent()
    data class AutoPriceWriteOffClicked(val value: Boolean) : AnimalCardIntent()
    data class NoteWriteOffChanged(val value: String) : AnimalCardIntent()
    data object SaveWriteOffPressed : AnimalCardIntent()

    // Kill Count Animal
    data class DialogKillClicked(val value: Boolean) : AnimalCardIntent()

}


data class AnimalProductCardUiStateLimit(val itemList: List<AnimalTitSuff> = listOf())

data class AnimalTitSuff(
    val title: String,
    val priceAll: Double,
    val suffix: String
)

data class AnimalData(
    val animal: DomainAnimalTable,
    val count: DomainAnimalCount,
    val size: DomainAnimalSize,
    val vacc: DomainAnimalVaccination,
    val weight: DomainAnimalWeight
)
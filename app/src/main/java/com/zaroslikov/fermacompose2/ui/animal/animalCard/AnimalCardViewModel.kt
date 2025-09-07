package com.zaroslikov.fermacompose2.ui.animal.animalCard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState.CountAnimal.ProductKill
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
                    actionAnimal = it.actionAnimal.copy(
                        countAnimal = if (data.animal.group) "" else "1",
                        /*productSuffix = (data.weight
                            ?: resourceProvider.getString(R.string.suffix_kilogram)).toString()
                  */
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
            is AnimalCardIntent.CountAddChanged -> updateCountAdd(intent.count)
            is AnimalCardIntent.PriceAddChanged -> updatePriceActionAnimal(intent.price)
            is AnimalCardIntent.AutoPriceAddClicked -> updateAutoPriceActionAnimal(intent.isAutoPrice)
            is AnimalCardIntent.NoteAddChanged -> updateNoteActionAnimal(intent.note)
            AnimalCardIntent.SaveAddPressed -> saveAddAnimal()

            // Sale Count Animal
            is AnimalCardIntent.DialogSaleClicked -> updateSaleDialog(intent.value)
            is AnimalCardIntent.CountSaleChanged -> updateCountActionAnimal(intent.value)
            is AnimalCardIntent.PriceSaleChanged -> updatePriceSale(intent.value)
            is AnimalCardIntent.AutoPriceSaleClicked -> updateAutoPriceActionAnimal(intent.value)
            is AnimalCardIntent.BuyerSaleChanged -> updateBuyerSale(intent.value)
            AnimalCardIntent.SaveSalePressed -> saveSaleAnimal()

            // WriteOff Count Animal
            is AnimalCardIntent.DialogWriteOffClicked -> updateState { it.copy(openWriteOffDialog = intent.value) }
            is AnimalCardIntent.CountWriteOffChanged -> updateCountActionAnimal(intent.value)
            is AnimalCardIntent.PriceWriteOffChanged -> updatePriceActionAnimal(intent.value)
            is AnimalCardIntent.AutoPriceWriteOffClicked -> updateAutoPriceActionAnimal(intent.value)
            is AnimalCardIntent.NoteWriteOffChanged -> updateNoteActionAnimal(intent.value)
            AnimalCardIntent.SaveWriteOffPressed -> TODO()

            // Kill Count Animal
            is AnimalCardIntent.DialogKillClicked -> updateKillDialog(intent.value)
            is AnimalCardIntent.CountKillChanged -> TODO()
            is AnimalCardIntent.TitleProductKillChanged -> updateTitleProductKill()
            is AnimalCardIntent.TitleAndSuffixKillClicked -> TODO()
            is AnimalCardIntent.CountProductKillChanged -> TODO()
            is AnimalCardIntent.SuffixProductKillChanged -> TODO()
            AnimalCardIntent.AddProductKillChanged -> TODO()
            is AnimalCardIntent.RemoveProductKillChanged -> TODO()
            AnimalCardIntent.SaveKillChanged -> TODO()
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

    // Action Animal
    private fun updateCountActionAnimal(countAnimal: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    countAnimal = countAnimal,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = countAnimal.isBlank(),
                        isErrorCountMore = isAnimalCountIncrease(
                            countAnimal,
                            getState().countAnimal.count
                        ),
                        isErrorCountZero = isAnimalCountZero(countAnimal)
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updatePriceActionAnimal(price: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    price = price
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateAutoPriceActionAnimal(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateNoteActionAnimal(note: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    note = note
                )
            )
        }
    }

    private fun updatePriceAllActionAnimal() {
        updateState {
            it.copy(
                actionAnimal = it.actionAnimal.copy(
                    priceAll = if (it.actionAnimal.isAutoPrice) (it.actionAnimal.price.toConvertZeroDouble() * it.actionAnimal.countAnimal.toConvertZeroDouble()).formatNumber() else "0",
                )
            )
        }
    }

    // Add-Expenses Count Animal
    private fun updateCountAdd(countAnimal: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    countAnimal = countAnimal,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = countAnimal.isBlank()
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun validationSaveAddAnimal() {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    error = state.actionAnimal.error.copy(
                        isErrorCount = state.actionAnimal.countAnimal.isBlank()
                    )
                )
            )
        }
    }

    fun saveAddAnimal() {
        viewModelScope.launch {
            val state = getState().actionAnimal
            validationSaveAddAnimal()
            if (state.error.isErrorCount) {
                val reasonNote = resourceProvider.getString(
                    if (state.note.isBlank())
                        R.string.animal_card_screen_add_no_note_reason
                    else
                        R.string.animal_card_screen_add_note_reason
                ).format(state.note)

                val domainCountAnimal = DomainAnimalCount(
                    count = state.countAnimal,
                    suffix = state.suffixAnimal,
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
                            count = state.countAnimal.toConvertDbDouble(),
                            day = dateTodayArray()[0],
                            month = dateTodayArray()[1],
                            year = dateTodayArray()[2],
                            price = state.price.toConvertDbDouble(),
                            priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                            countSuffix = state.suffixAnimal,
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


    private fun updatePriceSale(price: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    price = price,
                    error = state.actionAnimal.error.copy(
                        isErrorPrice = price.isBlank()
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateBuyerSale(buyer: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    buyer = buyer
                )
            )
        }
    }

    fun saveSaleAnimal() {
        viewModelScope.launch {
            val state = getState().actionAnimal
            // TODO validation
            if (state.hasAnyError) {

                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.suffixAnimal,
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
                        countSuffix = state.suffixAnimal,
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

    fun saveWriteOffAnimal() {
        viewModelScope.launch {
            val state = getState().actionAnimal
            // TODO validation
            if (state.hasAnyError) {

                val reasonNote = resourceProvider.getString(
                    if (state.note.isBlank())
                        R.string.animal_card_screen_add_no_note_reason
                    else
                        R.string.animal_card_screen_add_note_reason
                ).format(state.note)

                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.suffixAnimal,
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
                        countSuffix = state.suffixAnimal,
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

    // Kill Animal Count
    private fun updateKillDialog(openDialog: Boolean) {
        viewModelScope.launch {
            val buyer =
                if (openDialog) saleRepository.getItemsBuyerSaleList(itemIdPT)
                    .first() else getState().buyerList

            updateState { state ->
                state.copy(openKillDialog = openDialog)
            }
        }
    }

    private fun updateTitleProductKill(index: Int, newTitle: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    productKill = state.actionAnimal.productKill.mapIndexed { i, item ->
                        if (i == index)
                            item.copy(
                                title = newTitle,
                                error = item.error.copy(
                                    isError = newTitle.isBlank(),
                                    isErrorSlash = newTitle.isSlash()
                                )
                            )
                        else item
                    }
                )
            )
        }
        //TODO Warehouse
//        coroutineScope.launch {
//            val countAndSuffix = onUpdateCountWarehouse(
//                newTitle
//            )
//            textFields[index] =
//                textFields[index].copy(
//                    countWarehouse = countAndSuffix.first,
//                    countWarehouseSuffix = countAndSuffix.second
//                )
//        }
    }

    private fun updateCountProductKill() {
        /* newCount ->
                             val current = textFields[index]
                             textFields[index] = current.copy(
                                 count = newCount,
                                 isErrorCount = newCount.isError()
                             )*/
    }

    private fun updateSuffixProductKill() {
//                                textFields[index] = textFields[index].copy(suffix = it)
    }


    private fun saleKillAnimal() {
        val animalCategory = stringResource(R.string.animal_card_screen_category_kill)
        val note = stringResource(R.string.animal_card_screen_note_kill)
        val reasonNote = stringResource(
            R.string.animal_card_screen_kill_add_product,
        )
        if (isErrorKillAnimal(
                countAnimal = countAnimal,
                countAnimalAll = countAnimalAll,
                isAnimalGroup = isAnimalGroup,
                textFields = textFields,
                isErrorCount = { isErrorCount = it },
                isErrorCountMore = { isErrorCountMore = it },
                isErrorCountZero = { isErrorCountZero = it }
            )
        ) {
            val count = if (isAnimalGroup) isAnimalCountDifference(
                countAnimal,
                countAnimalAll
            ) else countAnimalAll

            val resultText = textFields.mapIndexed { index, it ->
                "${index + 1}. ${it.title} - ${it.count} ${it.suffix}"
            }.joinToString("\n")

            textFields.forEach {
                onSaveProductClick(
                    AddTable(
                        title = it.title,
                        count = it.count.toConvertZeroDouble(),
                        day = dateTodayArray()[0],
                        mount = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        countSuffix = it.suffix,
                        category = animalCategory,
                        animalId = idPT.toLong(),
                        note = note,
                        price = 0.0,
                        idPT = idPT.toLong()
                    )
                )
            }
            onSaveCountClick(
                Triple(
                    first = DomainIndicatorsVM(
                        //                                        weight = count,
                        weight = countAnimal,
                        suffix = countSuffix,
                        date = dateToday(),
                        idAnimal = idAnimal.toInt(),
                        note = resultText,
                        version = 2
                    ),
                    second = WriteOffTable(
                        title = title,
                        count = countAnimal.toConvertDbDouble(),
                        day = dateTodayArray()[0],
                        mount = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        status = false,
                        priceAll = 0.0,
                        countSuffix = countSuffix,
                        note = reasonNote + resultText,
                        idPT = idPT.toLong(),
                        id = TODO(),
                        price = TODO(),
                        animalCountId = TODO()
                    ),
                    third = count.toInt() == 0
                )
            )
            if (count.toInt() == 1 && isAnimalGroup) openDialogGroup = true
            else onConfirmation()
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
    data class CountKillChanged(val value: String) : AnimalCardIntent()
    data class TitleProductKillChanged(val value: String) : AnimalCardIntent()
    data class TitleAndSuffixKillClicked(val pair: Pair<String, String>) : AnimalCardIntent()
    data class CountProductKillChanged(val value: String) : AnimalCardIntent()
    data class SuffixProductKillChanged(val value: String) : AnimalCardIntent()
    data object AddProductKillChanged : AnimalCardIntent()
    data class RemoveProductKillChanged(val value: ProductKill) : AnimalCardIntent()
    data object SaveKillChanged : AnimalCardIntent()
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
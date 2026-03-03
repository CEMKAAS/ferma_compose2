package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixFoodList
import com.zaroslikov.domain.models.list.suffixKilogramCubicMetersList
import com.zaroslikov.domain.models.list.suffixKilogramLitersList
import com.zaroslikov.domain.models.list.suffixWeightList
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expensesAnimalRepository: ExpensesAnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val resourceProvider: ResourceProvider
) : EntryNewViewModel<ExpensesListState, ExpensesListIntent>(ExpensesListState()) {

    val itemIdPT: Long = checkNotNull(savedStateHandle[ExpensesDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: ExpensesListIntent) {
        when (intent) {
            is ExpensesListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(
                openBottomSheetGroup = intent.value,
                currentBriefly = intent.currentBriefly
            )

            is ExpensesListIntent.OpenBottomSheetEntry -> openBottomSheetEntry(
                openBottomSheetEntry = intent.value,
                domainExpensesTable = intent.item
            )

            is ExpensesListIntent.FoodClicked -> updateIsFood(intent.value)
            is ExpensesListIntent.PercentClicked -> updateIsPercent(intent.value)
            ExpensesListIntent.EquallyClicked -> updateEqually()

            is ExpensesListIntent.TitleChanged -> updateTitle(intent.value)
            is ExpensesListIntent.TitleAndSuffixClicked -> updateTitleAndSuffix(
                intent.title,
                intent.suffix
            )

            is ExpensesListIntent.CountChanged -> updateCount(intent.value)
            is ExpensesListIntent.SuffixClicked -> updateCountSuffix(intent.value)
            is ExpensesListIntent.AutoWeightClicked -> updateAutoWeight(intent.value)
            is ExpensesListIntent.WeightChanged -> updateWeight(intent.value)
            is ExpensesListIntent.WeightSuffixChanged -> updateWeightSuffix(intent.value)
            is ExpensesListIntent.PriceChanged -> updatePrice(intent.value)
            is ExpensesListIntent.AutoPriceClicked -> updateAutoPrice(intent.value)
            is ExpensesListIntent.CategoryChanged -> updateCategory(intent.value)
            is ExpensesListIntent.DateClicked -> updateDate(intent.value)
            is ExpensesListIntent.NoteChanged -> updateNote(intent.value)

            is ExpensesListIntent.ShowFoodClicked -> updateShowFood(intent.value)
            is ExpensesListIntent.ShowFoodHandClicked -> updateShowFoodHand(intent.value)

            is ExpensesListIntent.AnimalChipClicked -> updateAnimalChipSelection(intent.value)
            is ExpensesListIntent.FeedFoodChanged -> updateFeedFoodInput(intent.value)
            is ExpensesListIntent.FeedFoodSuffixClicked -> updateFeedFoodInputSuffix(intent.value)
            is ExpensesListIntent.CountAnimalChanged -> updateCountAnimal(intent.value)

            is ExpensesListIntent.ShowWarehouseClicked -> updateShowWarehouse(intent.value)
            is ExpensesListIntent.ShowAnimalClicked -> updateShowAnimal(intent.value)
            is ExpensesListIntent.AnimalChipByIdClicked -> updateAnimalSelectionById(intent.value)
            is ExpensesListIntent.AnimalSliderClicked -> updateAnimalSlider(
                intent.animal,
                intent.newValue
            )

            ExpensesListIntent.Insert -> insert()
            ExpensesListIntent.Update -> update()
            is ExpensesListIntent.Delete -> delete(intent.value)


            is ExpensesListIntent.SearchChanged -> updateSearch(intent.value)
            is ExpensesListIntent.CountWarehouse -> {
                /* updateWarehouse(intent.value)*/
            }

            is ExpensesListIntent.GroupClicked -> updateGroup(intent.value)

        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                expensesRepository.getAllExpensesItems(itemIdPT),
                expensesRepository.getBrieflyItemExpenses(itemIdPT)
            ) { addList, briefly ->
                addList to briefly
            }.collectLatest { (addList, briefly) ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = addList,
                        briefly = briefly,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainExpensesTable> {
        return expensesRepository.getBrieflyDetailsItemExpenses(itemIdPT, name).first()
    }

    private fun updateIsFood(isFood: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isFood = isFood,
                    suffixList = if (isFood) suffixFoodList else suffixAllList,
                    countSuffix = if (isFood) Suffix.KILOGRAM else Suffix.PIECES,
                    isShowCheckbox = false,
                )
            )
        }
    }

    private fun updateIsPercent(isPercent: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isPercent = isPercent
                )
            )
        }
    }

    private fun updateEqually() {
    }


    private val suffixSet =
        setOf(
            Suffix.GRAM,
            Suffix.KILOGRAM,
            Suffix.TONS
        )

    /*init {
        viewModelScope.launch {
            loadData()
            loadDataUpdate()
        }
    }

    suspend fun loadData() {
        val titleList = expensesRepository.getItemsTitleExpensesList(itemIdPT).first()
        val categoryList = expensesRepository.getItemsCategoryExpensesList(itemIdPT).first()
        val animalList =
            expensesRepository.getItemsAnimalExpensesList2(itemIdPT, itemId).first()

        updateState {
            it.copy(
                isEntry = isEntry,
                feedFoodChipSuffix = Suffix.GRAM,
                feedFoodInputSuffix = Suffix.GRAM,
                category = resourceProvider.getString(R.string.support_text_no_category),
                countSuffix = Suffix.PIECES,
                weightSuffix = Suffix.KILOGRAM,
                pickList = it.pickList.copy(
                    titleList = titleList,
                    categoryList = categoryList,
                    animalList2 = animalList
                )
            )
        }
    }

    suspend fun loadDataUpdate() {
        if (!isEntry) {
            val domainExpensesTable = expensesRepository.getItemExpenses(itemId)
                .filterNotNull()
                .first()

            updateState { it.updateFromDomain(domainExpensesTable) }
        }
    }*/

    private fun openBottomSheetGroup(
        openBottomSheetGroup: Boolean,
        currentBriefly: BrieflyExpensesDomain
    ) {
        viewModelScope.launch {
            val listBriefly = getDetailsName(name = currentBriefly.title)
            updateState {
                it.copy(
                    openBottomSheetGroup = openBottomSheetGroup,
                    currentBriefly = currentBriefly,
                    listBriefly = listBriefly
                )
            }
        }
    }

    private fun openBottomSheetEntry(
        openBottomSheetEntry: Boolean,
        domainExpensesTable: DomainExpensesTable?
    ) {
        if (openBottomSheetEntry)
            viewModelScope.launch {
                val titleList = expensesRepository.getItemsTitleExpensesList(itemIdPT).first()
                val categoryList = expensesRepository.getItemsCategoryExpensesList(itemIdPT).first()
                val animalList =
                    expensesRepository.getItemsAnimalExpensesList2(
                        itemIdPT,
                        domainExpensesTable?.id ?: 0
                    )
                        .first()

                updateState {
                    it.copy(
                        openBottomSheetEntry = true,
                        currentProduct = ExpensesEntryState2(
                            itemIdPT = itemIdPT,
                            feedFoodChipSuffix = Suffix.GRAM,
                            feedFoodInputSuffix = Suffix.GRAM,
                            category = resourceProvider.getString(R.string.support_text_no_category),
                            countSuffix = Suffix.PIECES,
                            weightSuffix = Suffix.KILOGRAM,
                            error = ErrorExpenses(),
                            pickList = it.currentProduct.pickList.copy(
                                titleList = titleList,
                                categoryList = categoryList,
                                animalList2 = animalList
                            )
                        )
                    )
                }
                domainExpensesTable?.let {
                    updateState {
                        it.copy(
                            currentProduct = it.currentProduct.toUiMap(
                                domainExpensesTable
                            )
                        )
                    }
                }
            }
        else updateState { it.copy(openBottomSheetEntry = false) }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                updateForSaveAnimalList()

                val id = expensesRepository.insertExpenses(
                    getState().currentProduct.toDomainMap()
                )
                setExpensesAnimal(id)
//                metricalExpenses() TODO Нужно придумать, как грамотно сохранять
                openBottomSheetEntry(false, null)
                showMessage(
                    resourceProvider.getString(R.string.toast_expenses_s_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        )
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                updateForSaveAnimalList()
                expensesRepository.updateExpenses(getState().currentProduct.toDomainMap())
                saveExpensesAnimal()
                openBottomSheetEntry(false, null)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        )
                )
            }
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            expensesRepository.deleteExpensesById(id)
            deleteExpensesAnimal()
            openBottomSheetEntry(false, null)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    )
            )
        }
    }

    override fun validation() {
        val (feedFood, countAnimal) = if (getState().currentProduct.isShowFoodHand) {
            getState().currentProduct.feedFoodInput.isBlank() to getState().currentProduct.countAnimalInput.isBlank()
        } else false to false

        val isErrorFood =
            if (getState().currentProduct.isShowFood && !getState().currentProduct.isShowFoodHand) getState().currentProduct.pickList.animalList2.filter { it.foodDay != 0.0 }
                .none { it.ps == true } else false

        val isErrorAnimal =
            if (getState().currentProduct.isShowAnimals) getState().currentProduct.pickList.animalList2.none { it.ps == true } else false

        val newError = ErrorExpenses(
            isErrorTitle = getState().currentProduct.title.isBlank(),
            isErrorSlash = getState().currentProduct.title.isSlash(),
            isErrorCount = getState().currentProduct.count.isBlank(),
            isErrorPrice = getState().currentProduct.price.isBlank(),
            isErrorFood = isErrorFood,
            isErrorAnimal = isErrorAnimal,
            isErrorDailyExpensesFood = feedFood,
            isErrorCountAnimal = countAnimal
        )
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    error = newError
                )
            )
        }
    }

    /* fun updateWarehouseUiState(name: String) {
         viewModelScope.launch {
             val domainCountSuffix = warehouseRepository.getCurrentBalanceProduct(
                 name,
                itemIdPT
             ).filterNotNull().first()
             updateState { it.copy(countInWarehouse = domainCountSuffix) }
         }
     }*/

    private suspend fun setExpensesAnimal(id: Long) {
        getState().currentProduct.pickList.animalList2.filter { it.ps }.map {
            DomainExpensesAnimal(
                id = it.id,
                idExpenses = id,
                idAnimal = it.id,
                percentExpenses = it.presentException,
                idPT = itemIdPT
            )
        }.forEach {
            expensesAnimalRepository.insertExpensesAnimal(it)
        }
    }

    private suspend fun saveExpensesAnimal() {
        getState().currentProduct.pickList.animalList2.forEach {
            val table = DomainExpensesAnimal(
                id = it.idExpensesAnimal,
                idExpenses = getState().currentProduct.itemId,
                idAnimal = it.id,
                percentExpenses = it.presentException,
                idPT = itemIdPT
            )

            when {
                it.ps && it.idExpensesAnimal == 0L -> expensesAnimalRepository.insertExpensesAnimal(
                    table
                )

                it.ps -> expensesAnimalRepository.updateExpensesAnimal(table)
                else -> expensesAnimalRepository.deleteExpensesAnimal(table)
            }
        }
    }

    private suspend fun deleteExpensesAnimal() {
        getState().currentProduct.pickList.animalList2.filter { it.idExpensesAnimal != 0L }
            .forEach {
                val table = DomainExpensesAnimal(
                    id = it.idExpensesAnimal,
                    idExpenses = getState().currentProduct.itemId,
                    idAnimal = it.id,
                    percentExpenses = it.presentException,
                    idPT = itemIdPT
                )
                expensesAnimalRepository.deleteExpensesAnimal(table)
            }
    }

    private fun updateTitle(title: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    title = title,
                    error = it.currentProduct.error.copy(
                        isErrorTitle = title.isBlank(),
                        isErrorSlash = title.isSlash()
                    )
                )
            )
        }
    }

    private fun updateGroup(isGroup: Boolean) {
        updateState {
            it.copy(
                isGroup = isGroup
            )
        }
    }

    private fun updateSearch(search: String) {
        updateState {
            it.copy(
                textSearch = search
            )
        }
    }

    private fun updateTitleAndSuffix(
        title: String,
        suffix: Suffix
    ) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    title = title,
                    countSuffix = suffix,
                    isShowFood = if (suffix !in suffixSet && !getState().currentProduct.isAutoWeight) false else getState().currentProduct.isShowFood,
                    isShowFoodHand = if (suffix !in suffixSet && !getState().currentProduct.isAutoWeight) false else getState().currentProduct.isShowFoodHand,
                    error = it.currentProduct.error.copy(
                        isErrorTitle = title.isBlank(),
                        isErrorSlash = title.isSlash()
                    )
                )
            )
        }
    }

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    count = count,
                    error = it.currentProduct.error.copy(
                        isErrorCount = count.isBlank()
                    )
                )
            )
        }
        updatePriceAll()
    }

    private fun updateCountSuffix(suffix: Suffix) {
        val weightSuffix = when (suffix) {
            Suffix.LITERS -> Suffix.KILOGRAM_TO_LITERS
            Suffix.CUBIC_METERS -> Suffix.KILOGRAM_TO_CUBIC_METERS
            else -> Suffix.KILOGRAM
        }

        val weightAllSuffix = when (suffix) {
            Suffix.LITERS, Suffix.CUBIC_METERS -> Suffix.KILOGRAM
            else -> weightSuffix
        }

        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    countSuffix = suffix,
                    isShowCheckbox = state.currentProduct.isFood && suffix !in suffixSet,
                    isAutoWeight = if (state.currentProduct.isFood && suffix !in suffixSet) getState().currentProduct.isAutoWeight else false,
                    weightSuffix = weightSuffix,
                    weightAllSuffix = weightAllSuffix
                    /*         isAutoWeight = if (suffix in suffixSet) false else getState().currentProduct.isAutoWeight,
                   isShowFood = if (suffix !in suffixSet && !getState().currentProduct.isAutoWeight) false else getState().currentProduct.isShowFood,
                        isShowFoodHand = if (suffix !in suffixSet && !getState().currentProduct.isAutoWeight) false else getState().currentProduct.isShowFoodHand*/
                )
            )
        }
    }

    private fun updateAutoWeight(
        isAutoWeight: Boolean
    ) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isAutoWeight = isAutoWeight,
                    isShowFood = if (getState().currentProduct.countSuffix !in suffixSet && !isAutoWeight) false else getState().currentProduct.isShowFood,
                    isShowFoodHand = if (getState().currentProduct.countSuffix !in suffixSet && !isAutoWeight) false else getState().currentProduct.isShowFoodHand
                )
            )
        }
    }

    private fun updateWeight(weight: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(weight = weight)
            )
        }
    }

    private fun updateWeightSuffix(weightSuffix: Suffix) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    weightSuffix = weightSuffix,
                    weightAllSuffix = weightSuffix
                )
            )
        }
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    price = price,
                    error = it.currentProduct.error.copy(
                        isErrorPrice = price.isBlank()
                    )
                )
            )
        }
        updatePriceAll()
    }

    private fun updateAutoPrice(isAutoCalculate: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isAutoPrice = isAutoCalculate
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    priceAll = if (it.currentProduct.isAutoPrice) (it.currentProduct.price.toConvertZeroDouble() * it.currentProduct.count.toConvertZeroDouble()).formatNumber() else "0"
                )
            )
        }
    }

    private fun updateCategory(category: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(category = category)
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(date = date)
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(note = note)
            )
        }
    }

    private fun updateShowFood(showFood: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isShowFood = showFood,
                    isShowWarehouse = showFood,
                    isShowAnimals = false,
                    pickList = state.currentProduct.pickList.copy(
                        animalList2 = state.currentProduct.pickList.animalList2.map { it.copy(ps = false) }
                    ),
                    error = state.currentProduct.error.copy(
                        isErrorFood = false,
                        isErrorAnimal = false
                    )
                ))
        }
    }

    private fun updateShowFoodHand(dailyExpensesFoodAndCount: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isShowFoodHand = dailyExpensesFoodAndCount,
                    error = it.currentProduct.error.copy(
                        isErrorFood = false
                    )
                )
            )
        }
    }

    private fun updateFeedFoodInput(feedFood: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    feedFoodInput = feedFood,
                    error = it.currentProduct.error.copy(
                        isErrorDailyExpensesFood = feedFood.isBlank()
                    )
                )
            )
        }
    }

    private fun updateFeedFoodInputSuffix(feedFoodSuffix: Suffix) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    feedFoodInputSuffix = feedFoodSuffix,
                )
            )
        }
    }

    private fun updateCountAnimal(countAnimal: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    countAnimalInput = countAnimal,
                    error = it.currentProduct.error.copy(
                        isErrorCountAnimal = countAnimal.isBlank()
                    )
                )
            )
        }
    }

    private fun updateShowWarehouse(isShowWarehouse: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isShowWarehouse = isShowWarehouse
                )
            )
        }
    }

    private fun updateAnimalChipSelection(
        toggledAnimal: AnimalExpensesDomain
    ) {
        val newSelected = !toggledAnimal.ps

        // Обновляем список животных с пересчетом presentException
        val updatedAnimals = getState().currentProduct.pickList.animalList2.map {
            if (it.id == toggledAnimal.id) {
                val convertedFood =
                    it.foodDay.convertWeight(
                        it.foodDaySuffix,
                        getState().currentProduct.feedFoodChipSuffix
                    )
                val dailyFood = convertedFood * it.countAnimal

                it.copy(
                    ps = newSelected,
                    presentException = if (newSelected) (it.foodDay / dailyFood) * 100.0 else 0.0
                )
            } else it
        }

        // Сумма по выбранным животным
        val updatedCountAnimal = updatedAnimals
            .filter { it.ps }
            .sumOf { it.countAnimal }
            .toString()


        val updatedDailyFood = updatedAnimals
            .filter { it.ps }
            .sumOf {
                it.foodDay.convertWeight(
                    it.foodDaySuffix,
                    getState().currentProduct.feedFoodChipSuffix
                ) * it.countAnimal
            }
            .toString()

        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    feedFoodChip = updatedDailyFood,
                    countAnimalChip = updatedCountAnimal,
                    pickList = state.currentProduct.pickList.copy(animalList2 = updatedAnimals),
                    error = state.currentProduct.error.copy(isErrorFood = false)
                )
            )
        }
    }

    private fun updateShowAnimal(showAnimal: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isShowFoodHand = false,
                    isShowAnimals = showAnimal,
                    pickList = state.currentProduct.pickList.copy(
                        animalList2 = state.currentProduct.pickList.animalList2.map { it.copy(ps = false) }
                    ),
                    error = state.currentProduct.error.copy(
                        isErrorAnimal = false
                    )
                ))
        }
    }

    // Хелпер: Перераспределить presentException для всех выбранных
    fun redistributeSelectedShare(totalShare: Double = 100.0) {
        val selected = getState().currentProduct.pickList.animalList2.filter { it.ps }
        val equalShare = if (selected.isNotEmpty()) totalShare / selected.size else 0.0

        val updatedList = getState().currentProduct.pickList.animalList2.map {
            if (it.ps) it.copy(presentException = equalShare) else it.copy(presentException = 0.0)
        }

        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    pickList = it.currentProduct.pickList.copy(
                        animalList2 = updatedList
                    ),
                    error = it.currentProduct.error.copy(
                        isErrorAnimal = false
                    )
                )
            )
        }
    }

    // Хелпер: Обновить ps у одного животного
    private fun updateAnimalSelectionById(animalId: Long) {
        val updatedList = getState().currentProduct.pickList.animalList2.map {
            if (it.id == animalId) it.copy(ps = !it.ps) else it
        }

        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    pickList = it.currentProduct.pickList.copy(
                        animalList2 = updatedList
                    )
                )
            )
        }
        redistributeSelectedShare()
    }


    // Хелпер: Обновить presentException у одного животного, перераспределить остаток
    private fun updateAnimalSlider(
        animalId: Long,
        newValue: Double,
        totalShare: Double = 100.0
    ) {
        val selectedAnimals = getState().currentProduct.pickList.animalList2.filter { it.ps }
        val remainingShare = (totalShare - newValue).coerceAtLeast(0.0)
        val othersCount = selectedAnimals.size - 1

        val updatedList = getState().currentProduct.pickList.animalList2.map {
            when {
                it.id == animalId -> it.copy(presentException = newValue)
                it.ps -> {
                    val redistributed =
                        if (othersCount > 0) remainingShare / othersCount else 0.0
                    it.copy(presentException = redistributed)
                }

                else -> it.copy(presentException = 0.0)
            }
        }
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    pickList = it.currentProduct.pickList.copy(
                        animalList2 = updatedList
                    )
                )
            )
        }
    }

    private fun updateSettingDay() {
        val dailyExpensesFoodAndCount = getState().currentProduct.isShowFoodHand

        val countTable = getState().currentProduct.count.toConvertZeroDouble()
        val weight = getState().currentProduct.weight.toConvertZeroDouble()

        val countAnimal = if (dailyExpensesFoodAndCount)
            getState().currentProduct.countAnimalInput.toConvertZeroDouble() else getState().currentProduct.countAnimalChip.toConvertZeroDouble()
        val dailyExpensesFood = if (dailyExpensesFoodAndCount)
            getState().currentProduct.feedFoodInput.toConvertZeroDouble() else getState().currentProduct.feedFoodChip.toConvertZeroDouble()

        val countProduct =
            if (getState().currentProduct.isAutoWeight) {
                (countTable * weight)
                    .convertWeight(
                        getState().currentProduct.weightSuffix,
                        getState().currentProduct.feedFoodChipSuffix
                    )
            } else countTable.convertWeight(
                getState().currentProduct.countSuffix,
                getState().currentProduct.feedFoodChipSuffix
            )

        val foodDay =
            if (dailyExpensesFoodAndCount) dailyExpensesFood
                .convertWeight(
                    getState().currentProduct.feedFoodInputSuffix,
                    getState().currentProduct.feedFoodChipSuffix
                )
            else dailyExpensesFood

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dateLocal = LocalDate.parse(getState().currentProduct.date, formatter)

        var days =
            (if (dailyExpensesFoodAndCount) countProduct / (countAnimal * foodDay)
            else countProduct / foodDay).toLong()

        if (days > 1000) days = 1000
        val newDate = dateLocal.plusDays(days)

        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    daysFood = days.toInt(), dateEndFood = newDate.format(formatter)
                )
            )
        }
    }

    private fun updateForSaveAnimalList() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    pickList = it.currentProduct.pickList.copy(
                        animalList2 = if (it.currentProduct.isShowFoodHand && it.currentProduct.isShowFood) emptyList()
                        else it.currentProduct.pickList.animalList2
                    )
                )
            )
        }
    }

    fun ExpensesEntryState2.toUiMap(
        domain: DomainExpensesTable
    ): ExpensesEntryState2 {
        val useDaily = domain.isShowFoodHand
        val isIndicatorsValue =
            setOf(domain.animalId, domain.animalVaccinationId, domain.animalCountId)
                .any { it != null }

        Log.i("expenses", "updateFromDomain: $isIndicatorsValue")
        Log.i("expenses", "animalId: ${domain.animalId}")
        Log.i("expenses", "animalVaccinationId: ${domain.animalVaccinationId}")
        Log.i("expenses", "animalCountId: ${domain.animalCountId}")
        Log.i("expenses", "feedFood: ${domain.feedFood}")
        Log.i("expenses", "feedFood: ${domain.weight?.formatNumber(false)}")

        return copy(
            title = domain.title,
            count = domain.count.formatNumber(false),
            date = formatDateToString(
                domain.day,
                domain.month,
                domain.year
            ),
            isAutoPrice = domain.priceAll != null,
            price = domain.price.formatNumber(false),
            priceAll = domain.priceAll?.formatNumber() ?: "",
            countSuffix = domain.countSuffix,
            category = domain.category,
            note = domain.note,
            isShowFood = domain.isShowFood,
            isShowFoodHand = domain.isShowFoodHand,
            isShowWarehouse = domain.isShowWarehouse,
            isShowAnimals = domain.isShowAnimals,
            countAnimalChip = if (!useDaily) domain.countAnimal?.formatNumber() ?: "" else "",
            feedFoodChip = if (!useDaily) domain.feedFood?.formatNumber() ?: "" else "",
            feedFoodChipSuffix = if (!useDaily) domain.feedFoodSuffix
                ?: feedFoodChipSuffix else feedFoodChipSuffix,
            countAnimalInput = if (useDaily) domain.countAnimal?.formatNumber(false) ?: "" else "",
            feedFoodInput = if (useDaily) domain.feedFood?.formatNumber(false) ?: "" else "",
            feedFoodInputSuffix = if (useDaily) domain.feedFoodSuffix
                ?: feedFoodInputSuffix else feedFoodInputSuffix,
            daysFood = domain.foodDesignedDay ?: 0,
            dateEndFood = domain.lastDayFood ?: "",
            isAutoWeight = domain.weight != null,
            weight = domain.weight?.formatNumber(false) ?: "",
            weightSuffix = domain.weightSuffix ?: weightSuffix,
            isIndicatorsValue = isIndicatorsValue,
            isEntry = false
        )
    }

    private fun ExpensesEntryState2.toDomainMap(): DomainExpensesTable {
        val title2 =
            when {
                isShowFood -> Triple(countAnimalChip, feedFoodChip, feedFoodChipSuffix)
                isShowFoodHand -> Triple(countAnimalInput, feedFoodInput, feedFoodInputSuffix)
                else -> Triple(null, null, null)
            }
        val dateList = date.split(".")
        return DomainExpensesTable(
            id = itemId,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            price = price.toConvertDbDouble(),
            priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
            countSuffix = countSuffix,
            category = category.trim(),
            note = note.trim(),
            isShowFood = isShowFood,
            isShowFoodHand = isShowFoodHand,
            isShowWarehouse = isShowWarehouse,
            isShowAnimals = isShowAnimals,
            countAnimal = title2.first?.toConvertZeroString()?.toConvertDbOnlyInt(),
            feedFood = title2.second?.toConvertZeroString()?.toConvertDbDouble(),
            feedFoodSuffix = title2.third,
            foodDesignedDay = if (isShowFood || isShowFoodHand) daysFood else null,
            lastDayFood = if (isShowFood || isShowFoodHand) dateEndFood else null,
            weight = if (isAutoWeight) weight.toConvertDbDouble() else null,
            weightSuffix = if (isAutoWeight) weightSuffix else null,
            idPT = itemIdPT,
        )
    }
}

sealed class ExpensesListIntent : BaseIntent {
    data class OpenBottomSheetGroup(
        val value: Boolean,
        val currentBriefly: BrieflyExpensesDomain = BrieflyExpensesDomain()
    ) : ExpensesListIntent()

    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainExpensesTable? = null
    ) : ExpensesListIntent()

    data class FoodClicked(val value: Boolean) : ExpensesListIntent()
    data class PercentClicked(val value: Boolean) : ExpensesListIntent()
    data object EquallyClicked : ExpensesListIntent()

    data class TitleChanged(val value: String) : ExpensesListIntent()
    data class TitleAndSuffixClicked(val title: String, val suffix: Suffix) :
        ExpensesListIntent()

    data class CountChanged(val value: String) : ExpensesListIntent()
    data class SuffixClicked(val value: Suffix) : ExpensesListIntent()

    data class AutoWeightClicked(val value: Boolean) : ExpensesListIntent()
    data class WeightChanged(val value: String) : ExpensesListIntent()
    data class WeightSuffixChanged(val value: Suffix) : ExpensesListIntent()

    data class AutoPriceClicked(val value: Boolean) : ExpensesListIntent()
    data class PriceChanged(val value: String) : ExpensesListIntent()

    data class CategoryChanged(val value: String) : ExpensesListIntent()
    data class DateClicked(val value: String) : ExpensesListIntent()
    data class NoteChanged(val value: String) : ExpensesListIntent()

    data class ShowFoodClicked(val value: Boolean) : ExpensesListIntent()
    data class ShowFoodHandClicked(val value: Boolean) : ExpensesListIntent()
    data class AnimalChipClicked(val value: AnimalExpensesDomain) : ExpensesListIntent()
    data class FeedFoodChanged(val value: String) : ExpensesListIntent()
    data class FeedFoodSuffixClicked(val value: Suffix) : ExpensesListIntent()
    data class CountAnimalChanged(val value: String) : ExpensesListIntent()
    data class ShowWarehouseClicked(val value: Boolean) : ExpensesListIntent()
    data class ShowAnimalClicked(val value: Boolean) : ExpensesListIntent()
    data class AnimalChipByIdClicked(val value: Long) : ExpensesListIntent()
    data class AnimalSliderClicked(val animal: Long, val newValue: Double) :
        ExpensesListIntent()

    data class CountWarehouse(val value: List<DomainCountSuffix>) : ExpensesListIntent()
    data class GroupClicked(val value: Boolean) : ExpensesListIntent()
    data class SearchChanged(val value: String) : ExpensesListIntent()
    data object Insert : ExpensesListIntent()
    data object Update : ExpensesListIntent()
    data class Delete(val value: Long) : ExpensesListIntent()
}
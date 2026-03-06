package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import android.util.Log
import androidx.compose.ui.util.fastAny
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixFoodList
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.convertWeightDay
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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

            is ExpensesListIntent.AnimalChipByIdFoodClicked -> updateAnimalChipByIdFood(intent.value)
            is ExpensesListIntent.AnimalChipByIdClicked -> updateAnimalSelectionById(intent.value)
            is ExpensesListIntent.AnimalValueChanged -> updateAnimalValue(
                intent.animal,
                intent.newValue
            )

            is ExpensesListIntent.AnimalSliderClicked -> updateAnimalSlider(
                intent.animal,
                intent.newValue
            )

            ExpensesListIntent.Insert -> insert()
            ExpensesListIntent.Update -> update()
            is ExpensesListIntent.Delete -> delete(intent.value)

            is ExpensesListIntent.SearchChanged -> updateSearch(intent.value)
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
        updateFeedFoodSuffix(Suffix.KILOGRAM)
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

    private fun updateEqually(totalShare: Double = 100.0) {

        val state = getState().currentProduct
        val animals = state.pickList.animalList2

        val selectedAnimals = animals.filter { it.ps }
        if (selectedAnimals.isEmpty()) return

        val equalShare = totalShare / selectedAnimals.size

        val price = (if (state.isAutoPrice) state.priceAll else state.price).toConvertZeroDouble()

        val updatedList = animals.map {
            if (it.ps) it.copy(
                presentException = equalShare,
                price = equalShare * price / 100
            ) else it.copy(
                presentException = 0.0,
                price = 0.0
            )
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
                    ).first()

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
                                animalList2 = animalList.map { animalList -> animalList.toUi() }
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

    private fun updateFeedFoodSuffix(suffix: Suffix) {
        val feedFoodChipSuffix = when (suffix) {
            Suffix.GRAM -> Suffix.GRAM_DAY
            Suffix.KILOGRAM -> Suffix.KILOGRAM_DAY
            Suffix.TONS -> Suffix.TONS_DAY
            else -> Suffix.KILOGRAM_DAY
        }
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    feedFoodChipSuffix = feedFoodChipSuffix
                )
            )
        }
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
                .none { it.ps } else false

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

    private fun updateGroup(isGroup: Boolean) {
        updateState { it.copy(isGroup = isGroup) }
    }

    private fun updateSearch(search: String) {
        updateState { it.copy(textSearch = search) }
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
        if (getState().currentProduct.isAutoWeight) updateWeightAll()
        if (getState().currentProduct.isFood) calculateFoodDays()
        if (getState().currentProduct.isAutoPrice) updatePriceAll()
        if (!getState().currentProduct.isFood
            && getState().currentProduct.pickList.animalList2.fastAny { it.ps }
        ) helperSlider()
        if (!getState().currentProduct.isFood
            && getState().currentProduct.pickList.animalList2.fastAny { it.ps }
        ) helperSlider()
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
                )
            )
        }
        if (getState().currentProduct.isFood) {
            updateFeedFoodSuffix(suffix)
            calculateFeedFood()
            calculateFoodDays()
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
                currentProduct = it.currentProduct.copy(
                    weight = weight
                )
            )
        }
        updateWeightAll()
        calculateFeedFood()
        calculateFoodDays()
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
        updateFeedFoodSuffix(weightSuffix)
        calculateFeedFood()
        calculateFoodDays()
    }

    private fun updateWeightAll() {
        updateState { state ->
            val weight = state.currentProduct.weight.toConvertZeroDouble()
            val count = state.currentProduct.count.toConvertZeroDouble()
            state.copy(
                currentProduct = state.currentProduct.copy(
                    weightAll = (weight * count).formatNumber()
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
        if (!getState().currentProduct.isFood
            && getState().currentProduct.pickList.animalList2.fastAny { it.ps }
        ) helperSlider()
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
        if (!getState().currentProduct.isFood
            && getState().currentProduct.pickList.animalList2.fastAny { it.ps }
        ) helperSlider()
    }

    private fun updatePriceAll() {
        updateState { state ->
            val price = state.currentProduct.price.toConvertZeroDouble()
            val count = state.currentProduct.count.toConvertZeroDouble()
            state.copy(
                currentProduct = state.currentProduct.copy(
                    priceAll = (price * count).formatNumber()
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
        if (getState().currentProduct.isFood) calculateFoodDays()
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(note = note)
            )
        }
    }

    private fun updateAnimalChipByIdFood(id: Long) {
        val updatedAnimals = getState().currentProduct.pickList.animalList2.map { animal ->
            if (animal.id == id) {
                Log.i("food", "foodDaySuffix: ${animal.foodDaySuffix}")
                val convertedFood =
                    animal.foodDay.convertWeightDay(
                        animal.foodDaySuffix,
                        getState().currentProduct.feedFoodChipSuffix
                    )
                val dailyFood = convertedFood * animal.countAnimal

                animal.copy(
                    ps = !animal.ps,
                    presentException = if (!animal.ps) (animal.foodDay / dailyFood) * 100.0 else 0.0
                )
            } else animal
        }
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    pickList = state.currentProduct.pickList.copy(animalList2 = updatedAnimals),
                    error = state.currentProduct.error.copy(isErrorFood = false)
                )
            )
        }
        calculateFeedFood()
        calculateFoodDays()
    }

    private fun calculateFeedFood() {
        val animalList = getState().currentProduct.pickList.animalList2
        val updatedCountAnimal = animalList
            .filter { it.ps }
            .sumOf { it.countAnimal }
            .toString()

        val updatedDailyFood = animalList
            .filter { it.ps }
            .sumOf {
                it.foodDay.convertWeightDay(
                    it.foodDaySuffix,
                    getState().currentProduct.feedFoodChipSuffix
                ) * it.countAnimal.toDouble()
            }
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    feedFoodChip = updatedDailyFood.formatNumber(),
                    countAnimalChip = updatedCountAnimal,
                )
            )
        }
    }

    private fun calculateFoodDays() {
        val suffix = getState().currentProduct.countSuffix
        val isWeightSuffix = when (suffix) {
            Suffix.KILOGRAM, Suffix.GRAM, Suffix.TONS -> true
            else -> false
        }

        val weight =
            if (isWeightSuffix) getState().currentProduct.count else getState().currentProduct.weightAll
        val feedFood = getState().currentProduct.feedFoodChip.toConvertZeroDouble()
        val day = (weight.toConvertZeroDouble() / feedFood).toInt()
        val dateEnd = endDay(getState().currentProduct.date, day)

        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    daysFood = day,
                    dateEnd = dateEnd
                )
            )
        }
    }

    private fun endDay(
        startDate: String,
        numberDays: Int
    ): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
        val start = LocalDate.parse(startDate, dateTimeFormatter)
        return start
            .plusDays(numberDays.toLong())
            .format(dateTimeFormatter)
    }


    private fun updateAnimalSelectionById(
        animalId: Long,
        totalShare: Double = 100.0
    ) {
        val state = getState().currentProduct
        val list = state.pickList.animalList2
        val totalPrice =
            (if (getState().currentProduct.isAutoPrice) state.priceAll else state.price)
                .toConvertZeroDouble()

        // переключаем выбранный элемент
        val toggledList = list.map {
            if (it.id == animalId) it.copy(ps = !it.ps) else it
        }

        val count = toggledList.filter { it.ps }.size

        val share = if (count > 0) totalShare / count else 0.0
        val pricePerAnimal = if (count > 0) totalPrice / count else 0.0

        val updatedList = toggledList.map {
            if (it.ps)
                it.copy(
                    presentException = share,
                    price = pricePerAnimal
                )
            else it.copy(
                presentException = 0.0,
                price = 0.0
            )
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

    private fun updateAnimalSlider(
        animalId: Long,
        newValue: Double,
    ) {
        val updatedList = getState().currentProduct.pickList.animalList2.map {
            if (it.id == animalId) it.copy(
                presentException = newValue,
            ) else it
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
        helperSlider()
    }

    private fun helperSlider(totalShare: Double = 100.0) {

        val state = getState().currentProduct
        val animals = state.pickList.animalList2

        val selectedAnimals = animals.filter { it.ps }

        val price = (if (state.isAutoPrice) state.priceAll else state.price).toConvertZeroDouble()

        val updatedList =
            if (selectedAnimals.size <= 1) {
                // одно животное — пользователь управляет сам
                animals.map {
                    if (it.ps)
                        it.copy(price = it.presentException * price / 100)
                    else
                        it.copy(presentException = 0.0, price = 0.0)
                }
            } else {
                val totalCurrentShare = selectedAnimals.sumOf { it.presentException }
                val normalizedList = animals.map {
                    if (it.ps && totalCurrentShare > 0) {
                        val normalized = it.presentException / totalCurrentShare * totalShare
                        it.copy(
                            presentException = normalized,
                            price = normalized * price / 100
                        )
                    } else it.copy(
                        presentException = 0.0,
                        price = 0.0
                    )
                }
                normalizedList
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


    private fun updateAnimalValue(
        animalId: Long,
        newValue: String
    ) {
        val newPrice = newValue.toConvertZeroDouble()
        val state = getState()
        val animals = state.currentProduct.pickList.animalList2

        val totalPrice =
            if (state.currentProduct.isAutoPrice)
                state.currentProduct.priceAll.toConvertZeroDouble()
            else
                state.currentProduct.price.toConvertZeroDouble()

        if (totalPrice == 0.0) return

        // процент изменённого животного
        val changedPercent = (newPrice / totalPrice) * 100

        val selectedAnimals = animals.filter { it.ps }
        val othersCount = selectedAnimals.size - 1

        val remainingPercent = (100 - changedPercent).coerceAtLeast(0.0)

        val updatedList = animals.map { animal ->

            when {

                animal.id == animalId -> {
                    animal.copy(
                        price = newPrice,
                        presentException = changedPercent
                    )
                }

                animal.ps -> {

                    val percent =
                        if (othersCount > 0) remainingPercent / othersCount else 0.0

                    val price = totalPrice * percent / 100

                    animal.copy(
                        presentException = percent,
                        price = price
                    )
                }

                else -> animal.copy(
                    presentException = 0.0,
                    price = 0.0
                )
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
            countAnimalInput = if (useDaily) domain.countAnimal?.formatNumber(false)
                ?: "" else "",
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

    private fun AnimalExpensesDomain.toUi(): AnimalExpensesUi {
        return AnimalExpensesUi(
            id = id,
            name = name,
            foodDay = foodDay,
            foodDaySuffix = foodDaySuffix,
            countAnimal = countAnimal,
            idExpensesAnimal = idExpensesAnimal,
            ps = ps,
            presentException = presentException,
            price = 0.0
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

    data class AnimalChipByIdFoodClicked(val value: Long) : ExpensesListIntent()
    data class AnimalChipByIdClicked(val value: Long) : ExpensesListIntent()
    data class AnimalSliderClicked(val animal: Long, val newValue: Double) :
        ExpensesListIntent()

    data class AnimalValueChanged(val animal: Long, val newValue: String) :
        ExpensesListIntent()

    data class GroupClicked(val value: Boolean) : ExpensesListIntent()
    data class SearchChanged(val value: String) : ExpensesListIntent()
    data object Insert : ExpensesListIntent()
    data object Update : ExpensesListIntent()
    data class Delete(val value: Long) : ExpensesListIntent()
}
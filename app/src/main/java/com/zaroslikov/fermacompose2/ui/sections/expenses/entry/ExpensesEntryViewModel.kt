package com.zaroslikov.fermacompose2.ui.sections.expenses.entry


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.forEach


@HiltViewModel
class ExpensesEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expensesAnimalRepository: ExpensesAnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<ExpensesEntryState, ExpensesEntryIntent>(ExpensesEntryState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1L

    override fun onIntent(intent: ExpensesEntryIntent) {
        when (intent) {
            is ExpensesEntryIntent.TitleChanged -> updateTitle(intent.value)
            is ExpensesEntryIntent.TitleAndSuffixClicked -> updateTitleAndSuffix(
                intent.title,
                intent.suffix
            )
            is ExpensesEntryIntent.CountChanged -> updateCount(intent.value)
            is ExpensesEntryIntent.SuffixClicked -> updateCountSuffix(intent.value)
            is ExpensesEntryIntent.AutoWeightClicked -> updateAutoWeight(intent.value)
            is ExpensesEntryIntent.WeightChanged -> updateState { it.copy(weight = intent.value) }
            is ExpensesEntryIntent.WeightSuffixChanged -> updateState { it.copy(weightSuffix = intent.value) }
            is ExpensesEntryIntent.PriceChanged -> updatePrice(intent.value)
            is ExpensesEntryIntent.AutoPriceClicked -> updateAutoPrice(intent.value)
            is ExpensesEntryIntent.CategoryChanged -> updateState { it.copy(category = intent.value) }
            is ExpensesEntryIntent.DateClicked -> updateState { it.copy(date = intent.value) }
            is ExpensesEntryIntent.NoteChanged -> updateState { it.copy(note = intent.value) }

            is ExpensesEntryIntent.ShowFoodClicked -> updateShowFood(intent.value)
            is ExpensesEntryIntent.ShowFoodHandClicked -> updateShowFoodHand(intent.value)

            is ExpensesEntryIntent.AnimalChipClicked -> updateAnimalChipSelection(intent.value)
            is ExpensesEntryIntent.FeedFoodChanged -> updateFeedFoodInput(intent.value)
            is ExpensesEntryIntent.FeedFoodSuffixClicked -> updateFeedFoodInputSuffix(intent.value)
            is ExpensesEntryIntent.CountAnimalChanged -> updateCountAnimal(intent.value)

            is ExpensesEntryIntent.ShowWarehouseClicked -> updateState { it.copy(isShowWarehouse = intent.value) }
            is ExpensesEntryIntent.ShowAnimalClicked -> updateShowAnimal(intent.value)
            is ExpensesEntryIntent.AnimalChipByIdClicked -> updateAnimalSelectionById(intent.value)
            is ExpensesEntryIntent.AnimalSliderClicked -> updateAnimalSlider(
                intent.animal,
                intent.newValue
            )

            ExpensesEntryIntent.Insert -> insert()
            ExpensesEntryIntent.Update -> update()
            ExpensesEntryIntent.Delete -> delete()

        }
    }

    val suffixSet =
        setOf(
           Suffix.GRAM,
            Suffix.KILOGRAM,
            Suffix.TONS
        )

    init {
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
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                updateForSaveAnimalList()

                val id = expensesRepository.insertExpenses(
                    getState().updateForSave(itemIdPT = itemIdPT)
                )
                setExpensesAnimal(id)
//                metricalExpenses() TODO Нужно придумать, как грамотно сохранять
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_expenses_s_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        )
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                updateForSaveAnimalList()
                expensesRepository.updateExpenses(
                    getState().updateForSave(
                        id = itemId,
                        itemIdPT = itemIdPT
                    )
                )
                saveExpensesAnimal()
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        )
                )
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            expensesRepository.deleteExpenses(
                getState().updateForSave(itemIdPT = itemIdPT)
            )
            deleteExpensesAnimal()
            navigateTo(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().title,
                        getState().count,
                        getState().countSuffix
                    )
            )
        }
    }


    override fun validation() {
        val (feedFood, countAnimal) = if (getState().isShowFoodHand) {
            getState().feedFoodInput.isBlank() to getState().countAnimalInput.isBlank()
        } else false to false

        val isErrorFood =
            if (getState().isShowFood && !getState().isShowFoodHand) getState().pickList.animalList2.filter { it.foodDay != 0.0 }
                .none { it.ps == true } else false

        val isErrorAnimal =
            if (getState().isShowAnimals) getState().pickList.animalList2.none { it.ps == true } else false

        val newError = ExpensesEntryState.ValidationError(
            isErrorTitle = getState().title.isBlank(),
            isErrorSlash = getState().title.isSlash(),
            isErrorCount = getState().count.isBlank(),
            isErrorPrice = getState().price.isBlank(),
            isErrorFood = isErrorFood,
            isErrorAnimal = isErrorAnimal,
            isErrorDailyExpensesFood = feedFood,
            isErrorCountAnimal = countAnimal
        )
        updateState {
            it.copy(error = newError)
        }
    }

    fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            val domainCountSuffix = warehouseRepository.getCurrentBalanceProduct(
                name,
                itemId
            ).filterNotNull().first()
            updateState { it.copy(countInWarehouse = domainCountSuffix) }
        }
    }

    private suspend fun setExpensesAnimal(id: Long) {
        getState().pickList.animalList2.filter { it.ps }.map {
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
        getState().pickList.animalList2.forEach {
            val table = DomainExpensesAnimal(
                id = it.idExpensesAnimal,
                idExpenses = itemId,
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

    suspend fun deleteExpensesAnimal() {
        getState().pickList.animalList2.filter { it.idExpensesAnimal != 0L }
            .forEach {
                val table = DomainExpensesAnimal(
                    id = it.idExpensesAnimal,
                    idExpenses = itemId,
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
                title = title,
                error = it.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
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
                title = title,
                countSuffix = suffix,
                isShowFood = if (suffix !in suffixSet && !getState().isAutoWeight) false else getState().isShowFood,
                isShowFoodHand = if (suffix !in suffixSet && !getState().isAutoWeight) false else getState().isShowFoodHand,
                error = it.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        }
    }

    fun updateCount(count: String) {
        updateState {
            it.copy(
                count = count,
                error = it.error.copy(
                    isErrorCount = count.isBlank()
                )
            )
        }
        updatePriceAll()
    }


    private fun updateCountSuffix(
        suffix: Suffix
    ) {
        updateState {
            it.copy(
                countSuffix = suffix,
                isAutoWeight = if (suffix in suffixSet) false else getState().isAutoWeight,
                isShowFood = if (suffix !in suffixSet && !getState().isAutoWeight) false else getState().isShowFood,
                isShowFoodHand = if (suffix !in suffixSet && !getState().isAutoWeight) false else getState().isShowFoodHand
            )
        }
    }

    private fun updateAutoWeight(
        isAutoWeight: Boolean
    ) {
        updateState {
            it.copy(
                isAutoWeight = isAutoWeight,
                isShowFood = if (getState().countSuffix !in suffixSet && !isAutoWeight) false else getState().isShowFood,
                isShowFoodHand = if (getState().countSuffix !in suffixSet && !isAutoWeight) false else getState().isShowFoodHand
            )
        }
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                price = price,
                error = it.error.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        }
        updatePriceAll()
    }

    private fun updateAutoPrice(isAutoCalculate: Boolean) {
        updateState {
            it.copy(isAutoPrice = isAutoCalculate)
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                priceAll = if (it.isAutoPrice) (it.price.toConvertZeroDouble() * it.count.toConvertZeroDouble()).formatNumber() else "0"
            )
        }
    }


    private fun updateShowFood(showFood: Boolean) {
        updateState { state ->
            state.copy(
                isShowFood = showFood,
                isShowWarehouse = showFood,
                isShowAnimals = false,
                pickList = state.pickList.copy(
                    animalList2 = state.pickList.animalList2.map { it.copy(ps = false) }
                ),
                error = state.error.copy(
                    isErrorFood = false,
                    isErrorAnimal = false
                )
            )
        }
    }

    private fun updateShowFoodHand(dailyExpensesFoodAndCount: Boolean) {
        updateState {
            it.copy(
                isShowFoodHand = dailyExpensesFoodAndCount,
                error = it.error.copy(
                    isErrorFood = false
                )
            )
        }
    }

    private fun updateFeedFoodInput(feedFood: String) {
        updateState {
            it.copy(
                feedFoodInput = feedFood,
                error = it.error.copy(
                    isErrorDailyExpensesFood = feedFood.isBlank()
                )
            )
        }
    }

    private fun updateFeedFoodInputSuffix(feedFoodSuffix: Suffix) {
        updateState {
            it.copy(
                feedFoodInputSuffix = feedFoodSuffix,
            )
        }
    }

    fun updateCountAnimal(countAnimal: String) {
        updateState {
            it.copy(
                countAnimalInput = countAnimal,
                error = it.error.copy(
                    isErrorCountAnimal = countAnimal.isBlank()
                )
            )
        }
    }

    private fun updateAnimalChipSelection(
        toggledAnimal: AnimalExpensesDomain
    ) {
        val newSelected = !toggledAnimal.ps

        // Обновляем список животных с пересчетом presentException
        val updatedAnimals = getState().pickList.animalList2.map {
            if (it.id == toggledAnimal.id) {
                val convertedFood =
                    it.foodDay.convertWeight(it.foodDaySuffix, getState().feedFoodChipSuffix)
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
                    getState().feedFoodChipSuffix
                ) * it.countAnimal
            }
            .toString()

        updateState { state ->
            state.copy(
                feedFoodChip = updatedDailyFood,
                countAnimalChip = updatedCountAnimal,
                pickList = state.pickList.copy(animalList2 = updatedAnimals),
                error = state.error.copy(isErrorFood = false)
            )
        }
    }

    private fun updateShowAnimal(showAnimal: Boolean) {
        updateState { state ->
            state.copy(
                isShowFoodHand = false,
                isShowAnimals = showAnimal,
                pickList = state.pickList.copy(
                    animalList2 = state.pickList.animalList2.map { it.copy(ps = false) }
                ),
                error = state.error.copy(
                    isErrorAnimal = false
                )
            )
        }
    }

    // Хелпер: Перераспределить presentException для всех выбранных
    fun redistributeSelectedShare(totalShare: Double = 100.0) {
        val selected = getState().pickList.animalList2.filter { it.ps }
        val equalShare = if (selected.isNotEmpty()) totalShare / selected.size else 0.0

        val updatedList = getState().pickList.animalList2.map {
            if (it.ps) it.copy(presentException = equalShare) else it.copy(presentException = 0.0)
        }

        updateState {
            it.copy(
                pickList = it.pickList.copy(
                    animalList2 = updatedList
                ),
                error = it.error.copy(
                    isErrorAnimal = false
                )
            )
        }
    }

    // Хелпер: Обновить ps у одного животного
    private fun updateAnimalSelectionById(animalId: Long) {
        val updatedList = getState().pickList.animalList2.map {
            if (it.id == animalId) it.copy(ps = !it.ps) else it
        }

        updateState {
            it.copy(
                pickList = it.pickList.copy(
                    animalList2 = updatedList
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
        val selectedAnimals = getState().pickList.animalList2.filter { it.ps }
        val remainingShare = (totalShare - newValue).coerceAtLeast(0.0)
        val othersCount = selectedAnimals.size - 1

        val updatedList = getState().pickList.animalList2.map {
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
                pickList = it.pickList.copy(
                    animalList2 = updatedList
                )
            )
        }
    }

    private fun updateSettingDay() {
        val dailyExpensesFoodAndCount = getState().isShowFoodHand

        val countTable = getState().count.toConvertZeroDouble()
        val weight = getState().weight.toConvertZeroDouble()

        val countAnimal = if (dailyExpensesFoodAndCount)
            getState().countAnimalInput.toConvertZeroDouble() else getState().countAnimalChip.toConvertZeroDouble()
        val dailyExpensesFood = if (dailyExpensesFoodAndCount)
            getState().feedFoodInput.toConvertZeroDouble() else getState().feedFoodChip.toConvertZeroDouble()

        val countProduct =
            if (getState().isAutoWeight) {
                (countTable * weight)
                    .convertWeight(
                        getState().weightSuffix,
                        getState().feedFoodChipSuffix
                    )
            } else countTable.convertWeight(
                getState().countSuffix,
                getState().feedFoodChipSuffix
            )

        val foodDay =
            if (dailyExpensesFoodAndCount) dailyExpensesFood
                .convertWeight(
                    getState().feedFoodInputSuffix,
                    getState().feedFoodChipSuffix
                )
            else dailyExpensesFood

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dateLocal = LocalDate.parse(getState().date, formatter)

        var days =
            (if (dailyExpensesFoodAndCount) countProduct / (countAnimal * foodDay)
            else countProduct / foodDay).toLong()

        if (days > 1000) days = 1000
        val newDate = dateLocal.plusDays(days)

        updateState {
            it.copy(
                daysFood = days.toInt(), dateEndFood = newDate.format(formatter)
            )
        }
    }

    private fun updateForSaveAnimalList() {
        updateState {
            it.copy(
                pickList = it.pickList.copy(
                    animalList2 = if (it.isShowFoodHand && it.isShowFood) emptyList()
                    else it.pickList.animalList2
                )
            )
        }
    }
}

sealed class ExpensesEntryIntent : BaseIntent {
    data class TitleChanged(val value: String) : ExpensesEntryIntent()
    data class TitleAndSuffixClicked(val title: String, val suffix: Suffix) :
        ExpensesEntryIntent()

    data class CountChanged(val value: String) : ExpensesEntryIntent()
    data class SuffixClicked(val value: Suffix) : ExpensesEntryIntent()
    data class WeightChanged(val value: String) : ExpensesEntryIntent()
    data class WeightSuffixChanged(val value: Suffix) : ExpensesEntryIntent()
    data class AutoWeightClicked(val value: Boolean) : ExpensesEntryIntent()
    data class PriceChanged(val value: String) : ExpensesEntryIntent()
    data class AutoPriceClicked(val value: Boolean) : ExpensesEntryIntent()
    data class CategoryChanged(val value: String) : ExpensesEntryIntent()
    data class DateClicked(val value: String) : ExpensesEntryIntent()
    data class NoteChanged(val value: String) : ExpensesEntryIntent()

    data class ShowFoodClicked(val value: Boolean) : ExpensesEntryIntent()
    data class ShowFoodHandClicked(val value: Boolean) : ExpensesEntryIntent()
    data class AnimalChipClicked(val value: AnimalExpensesDomain) : ExpensesEntryIntent()
    data class FeedFoodChanged(val value: String) : ExpensesEntryIntent()
    data class FeedFoodSuffixClicked(val value: Suffix) : ExpensesEntryIntent()
    data class CountAnimalChanged(val value: String) : ExpensesEntryIntent()
    data class ShowWarehouseClicked(val value: Boolean) : ExpensesEntryIntent()
    data class ShowAnimalClicked(val value: Boolean) : ExpensesEntryIntent()
    data class AnimalChipByIdClicked(val value: Long) : ExpensesEntryIntent()
    data class AnimalSliderClicked(val animal: Long, val newValue: Double) :
        ExpensesEntryIntent()


    data object Insert : ExpensesEntryIntent()
    data object Update : ExpensesEntryIntent()
    data object Delete : ExpensesEntryIntent()
}
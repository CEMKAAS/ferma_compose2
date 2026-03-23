package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.white
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expensesAnimalRepository: ExpensesAnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
) : EntryNewViewModel2<ExpensesListState, ExpensesListIntent, ExpensesListReduce>(
    initialState = ExpensesListState(),
    ExpensesListReduce(resourceProvider)
) {

    val itemIdPT: Long = checkNotNull(savedStateHandle[ExpensesDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: ExpensesListIntent) {
        sendIntent(intent)
        when (intent) {
            is ExpensesListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(
                openBottomSheetGroup = intent.value,
                currentBriefly = intent.currentBriefly
            )

            is ExpensesListIntent.OpenEntryBottomSheetByItem -> loadDataForEntryOrEdit(
                intent.value,
                intent.item,
                intent.isSaveStateForBottomSheet
            )

            is ExpensesListIntent.TitleChanged -> updateWarehouseUiState(intent.value)
            is ExpensesListIntent.TitleAndSuffixClicked -> updateWarehouseUiState(intent.title)
            ExpensesListIntent.Insert -> insert()
            ExpensesListIntent.Update -> update()
            is ExpensesListIntent.Delete -> delete(intent.value)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                expensesRepository.getAllExpensesItems(itemIdPT),
                expensesRepository.getBrieflyItemExpenses(itemIdPT),
                settingsRepository.getSettings(itemIdPT)
            ) { addList, briefly, settings ->
                Triple(addList, briefly, settings)
            }.collectLatest { (addList, briefly, settings) ->
                val expensesList = addList.map { it.toUi() }
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = expensesList,
                        searchList = expensesList,
                        briefly = briefly,
                        searchBrieflyList = briefly,
                        priceSuffix = settings.currencySuffix,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainExpensesTable> {
        return expensesRepository.getBrieflyDetailsItemExpenses(itemIdPT, name).first()
    }

    private fun openBottomSheetGroup(
        openBottomSheetGroup: Boolean,
        currentBriefly: BrieflyExpensesDomain
    ) {
        viewModelScope.launch {
            val listBriefly = getDetailsName(name = currentBriefly.title).map { it.toUi() }
            updateState {
                it.copy(
                    isOpenGroupBottomSheet = openBottomSheetGroup,
                    currentBriefly = currentBriefly,
                    brieflyList = listBriefly
                )
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: ExpensesTableUi?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else ExpensesEntryState2()
                onIntent(
                    ExpensesListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForEntry || domain != null) {
                val titleDeferred =
                    async { expensesRepository.getItemsTitleExpensesList(itemIdPT).first() }
                val categoryDeferred =
                    async { expensesRepository.getItemsCategoryExpensesList(itemIdPT).first() }
                val animalDeferred = async {
                    updateAnimalList(domain?.id)
                }

                val baseState = ExpensesEntryState2(
                    itemIdPT = itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    pickList = PickExpensesList(
                        titleList = titleDeferred.await(),
                        categoryList = categoryDeferred.await(),
                        animalList2 = animalDeferred.await().map { it.toUi() }
                    )
                )
                domain?.let { baseState.toUiMap(it) } ?: baseState
            } else getState().currentProduct
            onIntent(
                ExpensesListIntent.RefreshEntryBottomSheetState(
                    true, newState
                )
            )
            updateWarehouseUiState(newState.title)
        }
    }

    //TODO разделить на две функции
    private fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            val pair = warehouseRepository
                .getCurrentExpensesProductList(name, itemIdPT)
                .firstOrNull()
            pair?.let {
                onIntent(ExpensesListIntent.RefreshWarehouseCount(pair))
            }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            val id = expensesRepository.insertExpenses(getState().currentProduct.toDomainMap())
            setExpensesAnimal(id)
//                metricalExpenses() TODO Нужно придумать, как грамотно сохранять
            loadDataForEntryOrEdit(false, null)
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

    override fun update() {
        viewModelScope.launch {
            expensesRepository.updateExpenses(getState().currentProduct.toDomainMap())
            saveExpensesAnimal()
            loadDataForEntryOrEdit(false, null)
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

    override fun delete(id: Long) {
        viewModelScope.launch {
            expensesRepository.deleteExpensesById(id)
            deleteExpensesAnimalById(id) //TODO удаление через id
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
                it.ps && it.idExpensesAnimal == 0L ->
                    expensesAnimalRepository.insertExpensesAnimal(table)

                it.ps -> expensesAnimalRepository.updateExpensesAnimal(table)
                else -> expensesAnimalRepository.deleteExpensesAnimal(table)
            }
        }
    }

    private suspend fun deleteExpensesAnimalById(id: Long) {
        expensesAnimalRepository.deleteExpensesAnimalById(id)
    }

    fun ExpensesEntryState2.toUiMap(
        domain: ExpensesTableUi
    ): ExpensesEntryState2 {
        val isIndicatorsValue =
            setOf(domain.animalId, domain.animalVaccinationId, domain.animalCountId)
                .any { it != null }
        val isAutoWeight = domain.weight != null
        val weightAll = when {
            domain.isShowFood && isAutoWeight -> (domain.weight * domain.count).formatNumber()
            domain.isShowFood && !isAutoWeight -> domain.count.formatNumber()
            else -> ""
        }

        val weightSuffix = when (domain.countSuffix) {
            Suffix.LITERS -> Suffix.KILOGRAM_TO_LITERS
            Suffix.CUBIC_METERS -> Suffix.KILOGRAM_TO_CUBIC_METERS
            else -> domain.weightSuffix
        }

        val weightAllSuffix = when (domain.countSuffix) {
            Suffix.LITERS, Suffix.CUBIC_METERS -> Suffix.KILOGRAM
            else -> weightSuffix
        }

        return copy(
            itemId = domain.id,
            title = domain.title,
            count = domain.count.formatNumber(false),
            countSuffix = domain.countSuffix,
            price = domain.price.formatNumber(false),
            isAutoPrice = domain.priceAll != null,
            priceAll = domain.priceAll?.formatNumber() ?: "",
            date = formatDateToString(
                domain.day,
                domain.month,
                domain.year
            ),
            category = domain.category,
            note = domain.note,
            isFood = domain.isShowFood,
            feedFood = domain.feedFood?.formatNumber() ?: "",
            feedFoodSuffix = domain.feedFoodSuffix ?: Suffix.GRAM,
            countAnimalFood = domain.countAnimal?.formatNumber() ?: "",
            daysFood = domain.foodDesignedDay ?: 0,
            dateEndFood = domain.lastDayFood ?: "",
            isShowAutoWeightCheckbox = isAutoWeight,
            isAutoWeight = isAutoWeight,
            weight = domain.weight?.formatNumber(false) ?: "",
            weightSuffix = weightSuffix ?: Suffix.KILOGRAM,
            weightAll = weightAll,
            weightAllSuffix = weightAllSuffix ?: Suffix.KILOGRAM,
            isIndicatorsValue = isIndicatorsValue,
            isEntry = false,
            itemIdPT = domain.idPT
        )
    }

    private fun ExpensesEntryState2.toDomainMap(): DomainExpensesTable {
        val dateList = date.split(".")
        val (weight, weightSuffix) = if (isFood) {
            if (isAutoWeight) weight.toConvertDbDouble() to weightSuffix else null to null
        } else null to null
        return DomainExpensesTable(
            id = itemId,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            countSuffix = countSuffix,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            price = price.toConvertDbDouble(),
            priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
            category = category.trim(),
            note = note.trim(),
            isShowFood = isFood,
            isShowFoodHand = true, // TODO
            isShowWarehouse = true, //TODO
            isShowAnimals = true, // TODO
            countAnimal = if (isFood) countAnimalFood.toConvertDbOnlyInt() else null,
            feedFood = if (isFood) feedFood.toConvertDbDouble() else null,
            feedFoodSuffix = if (isFood) feedFoodSuffix else null,
            foodDesignedDay = if (isFood) daysFood else null,
            lastDayFood = if (isFood) dateEndFood else null,
            weight = weight,
            weightSuffix = weightSuffix,
            idPT = itemIdPT,
        )
    }

    private fun AnimalExpensesDomain.toUi(): AnimalExpensesUi {
        return AnimalExpensesUi(
            id = id,
            name = name,
            type = type,
            foodDay = foodDay,
            foodDaySuffix = foodDaySuffix,
            countAnimal = countAnimal,
            idExpensesAnimal = idExpensesAnimal,
            ps = ps,
            presentException = presentException,
            price = 0.0
        )
    }

    private suspend fun DomainExpensesTable.toUi(): ExpensesTableUi {
        val colors = when {
            isShowFood -> listOf(white, orang_8)
            animalVaccinationId != null -> listOf(white, violet_5)
            animalId != null -> listOf(white, green_g_4)
            else -> null
        }

        return ExpensesTableUi(
            id = id,
            title = title,
            count = count,
            day = day,
            month = month,
            year = year,
            price = price,
            countSuffix = countSuffix,
            category = category,
            note = note,
            isShowFood = isShowFood,
            isShowWarehouse = isShowWarehouse,
            isShowAnimals = isShowAnimals,
            isShowFoodHand = isShowFoodHand,
            feedFood = feedFood,
            countAnimal = countAnimal,
            foodDesignedDay = foodDesignedDay,
            lastDayFood = lastDayFood,
            idPT = idPT,
            animalId = animalId,
            animalVaccinationId = animalVaccinationId,
            animalCountId = animalCountId,
            priceAll = priceAll,
            feedFoodSuffix = feedFoodSuffix,
            weight = weight,
            weightSuffix = weightSuffix,
            food = this.food(),
            colors = colors
        )
    }

    private suspend fun DomainExpensesTable.food(): Food? {
        if (!isShowFood) return null

        val animals = updateAnimalList(id).filter { it.ps }.map { it.name }

        val startDate = formatDateToString(day, month, year)

        val weightAll = weight?.let { it * count } ?: count
        val feedPerDay = feedFood ?: 0.0

        val (percent, daysLeft) = percent(
            startDate = startDate,
            weight = weightAll,
            feedPerDay = feedPerDay
        )

        return Food(
            feedFood = feedPerDay,
            feedFoodSuffix = feedFoodSuffix ?: Suffix.KILOGRAM_DAY,
            weightAll = weightAll,
            weightSuffix = weightSuffix ?: Suffix.KILOGRAM,
            percentFloat = percent,
            animalList = animals,
            daysEnd = daysLeft
        )
    }

    //TODO нейронка предлогает в общий поток зафигачить, а не каждый раз запрашивать я даже хзх
    private suspend fun updateAnimalList(id: Long?): List<AnimalExpensesDomain> {
        return expensesRepository.getItemsAnimalExpensesList2(
            itemIdPT,
            id ?: 0
        ).first()
    }

    private fun percent(
        startDate: String,
        weight: Double,
        feedPerDay: Double
    ): Pair<Float, Int> {

        if (feedPerDay == 0.0) return 1f to 0

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val start = LocalDate.parse(startDate, formatter)
        val now = LocalDate.now()

        val totalDays = weight / feedPerDay
        val passedDays = ChronoUnit.DAYS.between(start, now).toDouble()

        val daysLeft = (totalDays - passedDays)
            .coerceAtLeast(0.0)

        val percentLeft = (daysLeft / totalDays)
            .coerceIn(0.0, 1.0)

        return percentLeft.toFloat() to daysLeft.toInt()
    }
}
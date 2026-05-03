package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expensesAnimalRepository: ExpensesAnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
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
            is ExpensesListIntent.OpenBottomSheetGroup ->
                openBottomSheetGroup(intent.title)

            is ExpensesListIntent.OpenEntryBottomSheetByItem -> loadDataForEntryOrEdit(
                intent.value,
                intent.item,
                intent.isSaveStateForBottomSheet
            )

            is ExpensesListIntent.TitleChanged -> updateWarehouseUiState(intent.value)
            is ExpensesListIntent.TitleAndSuffixClicked -> updateWarehouseUiState(intent.title)
            ExpensesListIntent.Insert -> insert()
            ExpensesListIntent.Update -> update()
            is ExpensesListIntent.Delete -> delete(0)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            combine(
                expensesRepository.getAllExpensesItems(itemIdPT),
                settingsRepository.getSettings(itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                Triple(addList, brieflyList, settings)
            }.collectLatest { (addList, briefly, settings) ->
                val expensesList = addList.map { it.toUi() }
                val currentDetail = getState().currentDetail
                updateState { state ->
                    state.copy(
                        idPT = itemIdPT,
                        list = expensesList,
                        searchList = expensesList,
                        briefly = briefly,
                        searchBrieflyList = briefly,
                        settings = settings,
                        currentDetail = currentDetail?.let { detail ->
                            expensesList.find { it.id == detail.id }
                        },
                        isLoading = false,
                        isArchive = isArchive
                    )
                }
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainExpensesTable> {
        return expensesRepository.getBrieflyDetailsItemExpenses(itemIdPT, name).first()
    }

    private fun brieflyList(
        list: List<DomainExpensesTable>,
        settings: DomainSettings
    ): List<BrieflyItem> {
        return list
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings = settings)
            }
    }

    private fun openBottomSheetGroup(title: String?) {
        viewModelScope.launch {
            if (title == null) {
                updateState { state ->
                    state.copy(isOpenGroupBottomSheet = false)
                }
                return@launch
            }
            val listBriefly = getDetailsName(name = title)
            val listBrieflyMap = listBriefly.map { it.toUi() }
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, settings = getState().settings)
            updateState {
                it.copy(
                    isOpenGroupBottomSheet = true,
                    currentBriefly = currentBriefly,
                    brieflyList = listBrieflyMap
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
            updateWarehouseUiStateSync(newState.title)
        }
    }

    private fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = warehouseRepository
            .getCurrentExpensesProductList(name, itemIdPT).first().build(getState().settings)

        onIntent(ExpensesListIntent.RefreshWarehouseCount(pair))
    }

    private fun List<DomainCountSuffix>.build(
        settings: DomainSettings
    ): List<DomainCountSuffix> {
        return this.groupBy { it.suffix.conversation4(settings) }
            .map { (suffix, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation3(it.suffix, settings)
                }
                DomainCountSuffix(
                    count = totalCount,
                    suffix = suffix
                )
            }
    }

    override fun insert() {
        viewModelScope.launch {
            val id = expensesRepository.insertExpenses(getState().currentProduct.toDomainMap(true))
            setExpensesAnimal(id)
            yandexMetricRepository.metricalExpenses(getState().currentProduct)
            showSnackbar(ProductOperation.ADD)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            expensesRepository.updateExpenses(getState().currentProduct.toDomainMap(false))
            saveExpensesAnimal()
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().currentDetail?.let { product ->
                expensesRepository.deleteExpensesById(product.id)
                showSnackbar(ProductOperation.DELETE)
                deleteExpensesAnimalById(product.id)
                sendIntent(ExpensesListIntent.OpenBottomSheetDelete(null))
            }
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


    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, count) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().currentDetail ?: ExpensesTableUi()
                product.title to product.count.formatNumber()
            } else {
                val product = getState().currentProduct
                product.title to product.count
            }
        val suffix = resourceProvider.getString(getState().settings.currencySuffix.toResId())
        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_expense_add)
                    .format(title, count, suffix)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_expense_update)
                    .format(title, count, suffix)

                else -> resourceProvider.getString(R.string.snackbar_expense_delete)
                    .format(title, count, suffix)
            }
        )
    }

    fun ExpensesEntryState2.toUiMap(
        domain: ExpensesTableUi
    ): ExpensesEntryState2 {
        val isIndicatorsValue =
            setOf(domain.animalId, domain.animalVaccinationId, domain.animalCountId)
                .any { it != null }
        val isAutoWeight = domain.weight != null
        val weightAll = when {
            domain.isFood && isAutoWeight -> (domain.weight * domain.count).formatNumber()
            domain.isFood && !isAutoWeight -> domain.count.formatNumber()
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
            isFood = domain.isFood,
            isShowFood = domain.isShowFood,
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
            itemIdPT = domain.idPT,
            animalId = domain.animalId,
            animalVaccinationId = domain.animalVaccinationId,
            animalCountId = domain.animalCountId,
        )
    }

    private fun ExpensesEntryState2.toDomainMap(isEntry: Boolean): DomainExpensesTable {
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
            priceSuffix = getState().settings.currencySuffix,
            category = category.trim().ifEmpty { null },
            note = note.trim(),
            isShowFood = if (isFood && isEntry) true else isShowFood,
            isFood = isFood,
            countAnimal = if (isFood) countAnimalFood.toConvertDbOnlyInt() else null,
            feedFood = if (isFood) feedFood.toConvertDbDouble() else null,
            feedFoodSuffix = if (isFood) feedFoodSuffix else null,
            foodDesignedDay = if (isFood) daysFood else null,
            lastDayFood = if (isFood) dateEndFood else null,
            weight = weight,
            weightSuffix = weightSuffix,
            idPT = itemIdPT,
            animalId = animalId,
            animalVaccinationId = animalVaccinationId,
            animalCountId = animalCountId,
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
        val typeProduct = when {
            isFood -> TypeProduct.FOOD
            animalVaccinationId != null -> TypeProduct.VACCINATION
            animalId != null -> TypeProduct.ANIMAL
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
            category = category ?: resourceProvider.getString(R.string.support_text_no_category),
            note = note,
            isFood = isFood,
            isShowFood = isShowFood,
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
            typeProduct = typeProduct,
        )
    }

    private suspend fun DomainExpensesTable.food(): Food? {
        if (!isFood) return null

        val animals = updateAnimalList(id).filter { it.ps }

        val startDate = formatDateToString(day, month, year)

        val weightAll = weight?.let { it * count } ?: count
        val feedPerDay = feedFood ?: 0.0

        val (percent, daysLeft, remainingFood) = percent(
            startDate = startDate,
            weight = weightAll,
            feedPerDay = feedPerDay
        )

        return Food(
            feedFood = feedPerDay,
            feedFoodSuffix = feedFoodSuffix ?: Suffix.KILOGRAM_DAY,
            weightAll = weightAll,
            weightSuffix = weightSuffix ?: Suffix.KILOGRAM,
            remainingFood = remainingFood,
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
    ): Triple<Float, Int, Double> {

        if (feedPerDay == 0.0) return Triple(1f, 0, weight)

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val start = LocalDate.parse(startDate, formatter)
        val now = LocalDate.now()

        val totalDays = weight / feedPerDay
        val passedDays = ChronoUnit.DAYS.between(start, now).toDouble()

        val daysLeft = (totalDays - passedDays)
            .coerceAtLeast(0.0)

        val remainingFood = daysLeft * feedPerDay
            .coerceAtLeast(0.0)

        val percentLeft = (daysLeft / totalDays)
            .coerceIn(0.0, 1.0)

        return Triple(percentLeft.toFloat(), daysLeft.toInt(), remainingFood)
    }
}
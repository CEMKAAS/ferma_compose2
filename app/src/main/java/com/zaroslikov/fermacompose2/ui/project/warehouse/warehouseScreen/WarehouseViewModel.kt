package com.zaroslikov.fermacompose2.ui.warehouse

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.FoodListUi
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.LoadDataWarehouse
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseIntent
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseReduce
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseState
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val addRepository: AddRepository,
    private val settingsRepository: SettingsRepository,
    private val writeOffRepository: WriteOffRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : BaseViewModel2<WarehouseState, WarehouseIntent, WarehouseReduce>(
    WarehouseState(),
    WarehouseReduce()
) {

    private val itemId: Long = checkNotNull(savedStateHandle[WarehouseDestination.itemIdArg])

    init {
        loadData()
    }

    fun onIntent(intent: WarehouseIntent) {
        sendIntent(intent)
        when (intent) {
            is WarehouseIntent.FastAddClicked -> fastAddSave(intent.value)
            WarehouseIntent.FoodOnWriteOffChange -> foodOnWriteOffChange()
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val isArchive = projectRepository.getIsArchiveProject(itemId).first()
            combine(
                warehouseRepository.getCurrentBalanceWarehouse(itemId),
                expensesRepository.getCurrentFoodWarehouse(itemId),
                warehouseRepository.getCurrentExpensesWarehouse(itemId),
                addRepository.getFastAddProduct(itemId),
                settingsRepository.getSettings(itemId)
            ) { warehous, foodList, expenses, fastAddProducts, settings ->
                buildUiState(warehous, foodList, expenses, fastAddProducts, settings)
            }.collect { newState ->
                updateState {
                    it.copy(
                        isLoading = false,
                        productList = newState.productList,
                        expensesList = newState.expensesList,
                        fastAddList = newState.fastAddList,
                        foodList = newState.foodList,
                        idPT = itemId,
                        isArchive = isArchive,
                        settings = newState.settings
                    )
                }
            }
        }
    }

    private fun foodOnWriteOffChange() {
        viewModelScope.launch {
            val currentFoodOnWriteOff = getState().currentFoodOnWriteOff ?: return@launch
            expensesRepository.updateFoodOnWriteOffWarehouse(currentFoodOnWriteOff.id)
            writeOffRepository.insertWriteOff(currentFoodOnWriteOff.toUi())
            showMessage(resourceProvider.getString(R.string.warehouse_screen_write_off))
            yandexMetricRepository.metricalWriteOffFood(currentFoodOnWriteOff)
            sendIntent(WarehouseIntent.OpenWarningWriteOffAlterDialogClicked(null))
        }
    }

    private fun fastAddSave(domain: DomainFastAddProduct) {
        viewModelScope.launch {
            val dateList = dateToday().split(".")
            addRepository.insertAdd(
                DomainAddTable(
                    title = domain.title,
                    count = domain.count,
                    countSuffix = domain.suffix,
                    day = dateList[0].toInt(),
                    month = dateList[1].toInt(),
                    year = dateList[2].toInt(),
                    price = 0.0,
                    priceSuffix = getState().settings.currencySuffix,
                    category = domain.category,
                    animalId = domain.idAnimal,
                    note = resourceProvider.getString(R.string.warehouse_screen_fast_add_note),
                    idPT = itemId
                )
            )
            yandexMetricRepository.metricalFastAdd(domain)
            showMessage(
                resourceProvider.getString(R.string.snackbar_product_add)
                    .format(
                        domain.title,
                        domain.count.formatNumber(),
                        resourceProvider.getString(domain.suffix.toResId())
                    )
            )
            sendIntent(WarehouseIntent.ShowFastAddClicked(true))
        }
    }

    private suspend fun buildUiState(
        productList: List<DomainTitleCountSuffix>,
        foodList: List<DomainExpensesTable>,
        expensesList: List<DomainTitleCountSuffix>,
        fastAddList: List<DomainFastAddProduct>,
        settings: DomainSettings
    ): LoadDataWarehouse {
        return LoadDataWarehouse(
            productList = build(productList, settings),
            expensesList = build(expensesList, settings),
            fastAddList = fastAddList,
            foodList = foodWriteOff(foodList),
            settings = settings
        )
    }

    private fun build(
        productList: List<DomainTitleCountSuffix>,
        settings: DomainSettings
    ): List<DomainTitleCountSuffix> {
        return productList
            .groupBy { it.title to it.suffix.conversation4(settings) }
            .map { (title, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation3(it.suffix, settings)
                }
                DomainTitleCountSuffix(
                    title = title.first,
                    count = totalCount,
                    suffix = title.second
                )
            }.filter { it.count > 0 }
    }

    private fun FoodListUi.toUi(): DomainWriteOffTable {
        val date = dateTodayArray()
        return DomainWriteOffTable(
            title = title,
            count = weightAll,
            countSuffix = weightSuffix,
            priceSuffix = getState().settings.currencySuffix,
            price = price,
            category = resourceProvider.getString(R.string.warehouse_screen_warehouse_food_write_off),
            day = date[0],
            month = date[1],
            year = date[2],
            status = false,
            note = resourceProvider.getString(R.string.warehouse_screen_warehouse_food_write_off),
            idPT = itemId,
            productOrigin = null
        )
    }

    private suspend fun foodWriteOff(foodList: List<DomainExpensesTable>): List<FoodListUi> {
        return foodList.mapNotNull { food ->
            val startDate = formatDateToString(food.day, food.month, food.year)
            val weightAll = food.weight?.let { it * food.count } ?: food.count
            val feedPerDay = food.feedFood ?: 0.0

            val (percent, daysLeft, remainingFood) = percent(
                startDate = startDate,
                weight = weightAll,
                feedPerDay = feedPerDay,
            )
            val remainingPrice = calculateRemainingPrice(
                price = food.priceAll ?: food.price,
                percent = percent
            )
            Log.i("food", "foodWriteOff: $remainingPrice")

            if (percent <= 0f) {
                expensesRepository.updateFoodOnWriteOffWarehouse(food.id)
                showMessage("Корм ${food.title} закончился")
                null
            } else
                FoodListUi(
                    title = food.title,
                    weightAll = remainingFood,
                    weightSuffix = food.weightSuffix ?: Suffix.KILOGRAM,
                    percentFloat = percent,
                    daysEnd = daysLeft,
                    id = food.id,
                    price = remainingPrice,
                )
        }
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

    fun calculateRemainingPrice(
        price: Double,
        percent: Float
    ): Double {
        return price * percent
    }
}
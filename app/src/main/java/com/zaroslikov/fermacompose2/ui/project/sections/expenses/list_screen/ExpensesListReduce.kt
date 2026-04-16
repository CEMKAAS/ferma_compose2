package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen


import androidx.compose.ui.util.fastAny
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixFoodList
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.convertWeightDay
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListState
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.text.lowercase

class ExpensesListReduce(private val resourceProvider: ResourceProvider) :
    BaseReducer<ExpensesListState, ExpensesListIntent>() {
    override fun reducer(
        state: ExpensesListState,
        intent: ExpensesListIntent
    ): ExpensesListState {
        val newState = when (intent) {
            is ExpensesListIntent.SearchChanged -> state.updateSearch(intent.value)
            is ExpensesListIntent.GroupClicked -> state.updateGroup(intent.value)

            //Добавление и редактирование купленной продукции
            is ExpensesListIntent.RefreshEntryBottomSheetState -> state
                .updateEntryBottomSheet(
                    isOpenEntryBottomSheet = intent.isOpen,
                    isSaveStateForEntry = intent.isSaveStateForBottomSheet,
                    entryState2 = intent.state
                )
                .helperSlider()

            is ExpensesListIntent.FoodClicked -> {
                val new = state.updateIsFood(intent.value).updateFeedFoodSuffix(Suffix.KILOGRAM)
                if (!new.currentProduct.isFood && new.currentProduct.pickList.animalList2.fastAny { it.ps })
                    new.helperSlider() else new
            }

            is ExpensesListIntent.TitleChanged -> state.updateTitle(intent.value)
            is ExpensesListIntent.TitleAndSuffixClicked -> state.updateTitleAndSuffix(
                intent.title,
                intent.suffix
            )

            is ExpensesListIntent.RefreshWarehouseCount -> state.updateWarehouseList(intent.value)

            is ExpensesListIntent.CountChanged -> {
                var new = state.updateCount(intent.value)
                if (new.currentProduct.isAutoWeight) new = new.updateWeightAll()
                if (new.currentProduct.isFood) new = new.calculateFoodDays()
                if (new.currentProduct.isAutoPrice) new = new.updatePriceAll()
                if (!new.currentProduct.isFood && new.currentProduct.pickList.animalList2.fastAny { it.ps })
                    new = new.helperSlider()

                new
            }

            is ExpensesListIntent.SuffixClicked -> {
                var new = state.updateCountSuffix(intent.value)
                if (state.currentProduct.isFood) {
                    new = new.updateFeedFoodSuffix(intent.value).calculateFeedFood()
                        .calculateFoodDays()
                }
                new
            }

            is ExpensesListIntent.AutoWeightClicked -> state.updateAutoWeight(intent.value)
            is ExpensesListIntent.WeightChanged ->
                state.updateWeight(intent.value)
                    .updateWeightAll()
                    .calculateFeedFood()
                    .calculateFoodDays()


            is ExpensesListIntent.WeightSuffixChanged ->
                state.updateWeightSuffix(intent.value)
                    .updateFeedFoodSuffix(intent.value)
                    .calculateFeedFood()
                    .calculateFoodDays()

            is ExpensesListIntent.PriceChanged -> {
                val new = state.updatePrice(intent.value).updatePriceAll()
                if (!state.currentProduct.isFood
                    && state.currentProduct.pickList.animalList2.fastAny { it.ps }
                ) new.helperSlider() else new
            }

            is ExpensesListIntent.AutoPriceClicked -> {
                val new = state.updateAutoPrice(intent.value).updatePriceAll()
                if (!state.currentProduct.isFood
                    && state.currentProduct.pickList.animalList2.fastAny { it.ps }
                ) new.helperSlider() else new
            }

            is ExpensesListIntent.CategoryChanged -> state.updateCategory(intent.value)
            is ExpensesListIntent.DateClicked -> {
                val new = state.updateDate(intent.value)
                if (state.currentProduct.isFood) new.calculateFoodDays() else new
            }

            is ExpensesListIntent.NoteChanged -> state.updateNote(intent.value)
            //Отвечает за еду
            is ExpensesListIntent.AnimalChipByIdFoodClicked ->
                state.updateAnimalChipByIdFood(intent.value)
                    .calculateFeedFood()
                    .calculateFoodDays()

            //Отвечает за распределение продукции
            is ExpensesListIntent.AnimalChipByIdClicked -> state.updateAnimalSelectionById(intent.value)
            is ExpensesListIntent.PercentClicked -> state.updateIsPercent(intent.value)
            ExpensesListIntent.EquallyClicked -> state.updateEqually()
            is ExpensesListIntent.AnimalSliderClicked -> state.updateAnimalSlider(
                intent.animal,
                intent.newValue
            ).helperSlider(intent.animal)

            is ExpensesListIntent.AnimalValueChanged -> state.updateAnimalValue(
                intent.animal,
                intent.newValue
            )

            is ExpensesListIntent.OpenBottomSheetDetail -> state.updateOpenBottomSheetDetail(intent.value)
            is ExpensesListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)
            else -> state
        }
        return newState.updateValid()
    }

    private fun ExpensesListState.updateOpenBottomSheetDetail(
        id: Long?
    ): ExpensesListState {
        return if (id == null)
            copy(
                isOpenBottomSheetDetail = false,
                currentDetail = null
            )
        else {
            val domain = list.find { it.id == id }
            copy(
                isOpenBottomSheetDetail = domain?.let { true } ?: false,
                currentDetail = domain
            )
        }
    }

    private fun ExpensesListState.updateOpenBottomSheetDelete(id: Long?): ExpensesListState {
        return if (id == null)
            copy(
                isOpenBottomSheetDelete = false,
                currentDetail = null
            )
        else {
            val domain = list.find { it.id == id }
            copy(
                isOpenBottomSheetDelete = domain?.let { true } ?: false,
                currentDetail = domain
            )
        }
    }

    private fun ExpensesListState.updateSearch(search: String): ExpensesListState {
        val query = search.trim().lowercase()
        val searchList = if (query.isBlank() && !isGroup) list
        else
            list.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.note.lowercase().contains(query) ||
                        item.category.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) ||
                        resourceProvider.getString(item.countSuffix.toResId()).lowercase()
                            .contains(query) ||
                        "${item.day} ${resourceProvider.getString(monthToResString(item.month))} ${item.year}".lowercase()
                            .contains(query) ||
                        (item.priceAll ?: item.price).toString().lowercase().contains(query)
            }

        val searchBrieflyList = if (query.isBlank() && isGroup) briefly
        else
            briefly.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.weight?.value.toString().lowercase().contains(query) ||
                        (item.price).toString().lowercase().contains(query)
            }
        return copy(
            textSearch = search,
            searchBrieflyList = searchBrieflyList,
            searchList = searchList
        )
    }

    private fun ExpensesListState.updateGroup(isGroup: Boolean): ExpensesListState {
        return copy(isGroup = isGroup)
    }

    private fun ExpensesListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: ExpensesEntryState2,
        isSaveStateForEntry: Boolean
    ): ExpensesListState {
        return copy(
            isOpenEntryBottomSheet = isOpenEntryBottomSheet,
            currentProduct = entryState2,
            isSaveStateForEntry = isSaveStateForEntry
        )
    }

    private fun ExpensesListState.updateIsFood(isFood: Boolean): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                isFood = isFood,
                suffixList = if (isFood) suffixFoodList else suffixAllList,
                countSuffix = if (isFood) Suffix.KILOGRAM else Suffix.PIECES,
                isShowAutoWeightCheckbox = false,
            )
        )
    }

    private fun ExpensesListState.updateFeedFoodSuffix(suffix: Suffix): ExpensesListState {
        val feedFoodChipSuffix = when (suffix) {
            Suffix.GRAM -> Suffix.GRAM_DAY
            Suffix.KILOGRAM -> Suffix.KILOGRAM_DAY
            Suffix.TONS -> Suffix.TONS_DAY
            else -> Suffix.KILOGRAM_DAY
        }
        return this.copy(
            currentProduct = this.currentProduct.copy(
                feedFoodSuffix = feedFoodChipSuffix
            )
        )
    }

    private fun ExpensesListState.updateTitle(title: String): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                title = title,
                error = this.currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        )
    }

    private fun ExpensesListState.updateTitleAndSuffix(
        title: String,
        suffix: Suffix
    ): ExpensesListState {
        val weightSuffix = when (suffix) {
            Suffix.LITERS -> Suffix.KILOGRAM_TO_LITERS
            Suffix.CUBIC_METERS -> Suffix.KILOGRAM_TO_CUBIC_METERS
            else -> Suffix.KILOGRAM
        }

        val weightAllSuffix = when (suffix) {
            Suffix.LITERS, Suffix.CUBIC_METERS -> Suffix.KILOGRAM
            else -> weightSuffix
        }
        return this.copy(
            currentProduct = this.currentProduct.copy(
                title = title,
                countSuffix = suffix,
                isShowAutoWeightCheckbox = currentProduct.isFood && suffix !in currentProduct.suffixSet,
                isAutoWeight = if (currentProduct.isFood && suffix !in currentProduct.suffixSet) currentProduct.isAutoWeight else false,
                weightSuffix = weightSuffix,
                weightAllSuffix = weightAllSuffix,
                error = this.currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        )
    }

    private fun ExpensesListState.updateWarehouseList(warehouseList: List<DomainCountSuffix>): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    warehouseList = warehouseList
                )
            )
        )
    }

    private fun ExpensesListState.updateCount(count: String): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                count = count,
                error = this.currentProduct.error.copy(
                    isErrorCount = count.isBlank()
                )
            )
        )
    }

    private fun ExpensesListState.updateCountSuffix(suffix: Suffix): ExpensesListState {
        val weightSuffix = when (suffix) {
            Suffix.LITERS -> Suffix.KILOGRAM_TO_LITERS
            Suffix.CUBIC_METERS -> Suffix.KILOGRAM_TO_CUBIC_METERS
            else -> Suffix.KILOGRAM
        }

        val weightAllSuffix = when (suffix) {
            Suffix.LITERS, Suffix.CUBIC_METERS -> Suffix.KILOGRAM
            else -> weightSuffix
        }

        return this.copy(
            currentProduct = currentProduct.copy(
                countSuffix = suffix,
                isShowAutoWeightCheckbox = currentProduct.isFood && suffix !in currentProduct.suffixSet,
                isAutoWeight = if (currentProduct.isFood && suffix !in currentProduct.suffixSet) currentProduct.isAutoWeight else false,
                weightSuffix = weightSuffix,
                weightAllSuffix = weightAllSuffix
            )
        )
    }

    private fun ExpensesListState.updateAutoWeight(isAutoWeight: Boolean): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                isAutoWeight = isAutoWeight
            )
        )
    }

    private fun ExpensesListState.updateWeight(weight: String): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                weight = weight
            )
        )
    }

    private fun ExpensesListState.updateWeightSuffix(weightSuffix: Suffix): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                weightSuffix = weightSuffix,
                weightAllSuffix = weightSuffix
            )
        )
    }

    private fun ExpensesListState.updatePrice(price: String): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                price = price,
                error = currentProduct.error.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        )
    }


    private fun ExpensesListState.updateAutoPrice(isAutoCalculate: Boolean): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(
                isAutoPrice = isAutoCalculate
            )
        )
    }


    private fun ExpensesListState.updatePriceAll(): ExpensesListState {
        val price = this.currentProduct.price.toConvertZeroDouble()
        val count = this.currentProduct.count.toConvertZeroDouble()
        return this.copy(
            currentProduct = this.currentProduct.copy(
                priceAll = (price * count).formatNumber()
            )
        )

    }


    private fun ExpensesListState.updateCategory(category: String): ExpensesListState {
        return this.copy(
            currentProduct = this.currentProduct.copy(category = category)
        )
    }

    private fun ExpensesListState.updateDate(date: String): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(date = date)
        )
    }

    private fun ExpensesListState.updateNote(note: String): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(note = note)
        )
    }

    private fun ExpensesListState.updateAnimalChipByIdFood(id: Long): ExpensesListState {
        val updatedAnimals = currentProduct.pickList.animalList2.map { animal ->
            if (animal.id == id) {
                val convertedFood =
                    animal.foodDay.convertWeightDay(
                        animal.foodDaySuffix,
                        currentProduct.feedFoodSuffix
                    )
                val dailyFood = convertedFood * animal.countAnimal
                animal.copy(
                    ps = !animal.ps,
                    presentException = if (!animal.ps) (animal.foodDay / dailyFood) * 100.0 else 0.0
                )
            } else animal
        }
        return this.copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(animalList2 = updatedAnimals)
            )
        )
    }

    private fun ExpensesListState.updateAnimalSelectionById(
        animalId: Long,
        totalShare: Double = 100.0
    ): ExpensesListState {
        val list = currentProduct.pickList.animalList2
        val totalPrice = currentPrice()

        // переключаем выбранный элемент
        val toggledList = list.map {
            if (it.id == animalId) it.copy(ps = !it.ps) else it
        }

        val count = toggledList.filter { it.ps }.size

        val share = if (count > 0) totalShare / count else 0.0
        val pricePerAnimal = if (count > 0) totalPrice / count else 0.0

        val updatedList = toggledList.map {
            if (it.ps) it.copy(
                presentException = share,
                price = pricePerAnimal
            )
            else it.copy(
                presentException = 0.0,
                price = 0.0
            )
        }
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    animalList2 = updatedList
                )
            )
        )
    }

    private fun ExpensesListState.updateIsPercent(isPercent: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                isPercent = isPercent
            )
        )
    }

    private fun ExpensesListState.updateEqually(totalShare: Double = 100.0): ExpensesListState {
        val animals = currentProduct.pickList.animalList2

        val selectedAnimals = animals.filter { it.ps }
        if (selectedAnimals.isEmpty()) return this

        val equalShare = totalShare / selectedAnimals.size

        val price = currentPrice()

        val updatedList = animals.map {
            if (it.ps) it.copy(
                presentException = equalShare,
                price = equalShare * price / 100
            ) else it.copy(
                presentException = 0.0,
                price = 0.0
            )
        }

        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    animalList2 = updatedList
                )
            )
        )
    }

    private fun ExpensesListState.updateAnimalSlider(
        animalId: Long,
        newValue: Double,
    ): ExpensesListState {
        val updatedList = currentProduct.pickList.animalList2.map {
            if (it.id == animalId) it.copy(
                presentException = newValue,
            ) else it
        }
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    animalList2 = updatedList
                )
            )
        )
    }

    private fun ExpensesListState.helperSlider(
        changedId: Long? = null,
        totalShare: Double = 100.0
    ): ExpensesListState {
        val animals = currentProduct.pickList.animalList2
        val price = currentPrice()
        val selectedAnimals = animals.filter { it.ps }

        val updatedList = when {
            // 🟢 1. одно животное — пользователь управляет сам
            selectedAnimals.size <= 1 -> {
                animals.map {
                    if (it.ps) it.copy(price = it.presentException * price / 100)
                    else it.copy(presentException = 0.0, price = 0.0)
                }
            }

            // 🟢 2. changedId == null → нормализация (старый алгоритм)
            changedId == null -> {
                val totalCurrentShare = selectedAnimals.sumOf { it.presentException }
                animals.map {
                    if (it.ps && totalCurrentShare > 0) {
                        val normalized =
                            (it.presentException / totalCurrentShare * totalShare)
                                .roundToInt()
                                .toDouble()
                        it.copy(
                            presentException = normalized,
                            price = normalized * price / 100
                        )

                    } else it.copy(
                        presentException = 0.0,
                        price = 0.0
                    )
                }
            }

            // 🟢 3. changedId != null → перераспределяем остальные
            else -> {
                val changedAnimal = animals.find { it.id == changedId }
                if (changedAnimal == null) return this
                val remainingShare =
                    (totalShare - changedAnimal.presentException).coerceAtLeast(0.0)
                val others = selectedAnimals.filter { it.id != changedId }
                val shareForOthers =
                    if (others.isNotEmpty())
                        (remainingShare / others.size).roundToInt().toDouble()
                    else 0.0

                animals.map {
                    when {
                        !it.ps -> it.copy(presentException = 0.0, price = 0.0)
                        it.id == changedId -> it.copy(price = it.presentException * price / 100)
                        else ->
                            it.copy(
                                presentException = shareForOthers,
                                price = shareForOthers * price / 100
                            )
                    }
                }
            }
        }

        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    animalList2 = updatedList
                )
            )
        )
    }

    private fun ExpensesListState.updateAnimalValue(
        animalId: Long,
        newValue: String
    ): ExpensesListState {
        val newPrice = newValue.toConvertZeroDouble()
        val animals = currentProduct.pickList.animalList2

        val totalPrice = currentPrice()

        if (totalPrice == 0.0) return this

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
                        presentException = changedPercent,
                        error = animal.error.copy(
                            isErrorPrice = newPrice > totalPrice
                        )
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
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    animalList2 = updatedList
                )
            )
        )
    }

    private fun ExpensesListState.currentPrice(): Double {
        return (if (currentProduct.isAutoPrice) currentProduct.priceAll
        else currentProduct.price).toConvertZeroDouble()
    }

    private fun ExpensesListState.updateWeightAll(): ExpensesListState {
        val weight = currentProduct.weight.toConvertZeroDouble()
        val count = currentProduct.count.toConvertZeroDouble()
        return copy(
            currentProduct = currentProduct.copy(
                weightAll = (weight * count).formatNumber()
            )
        )
    }

    private fun ExpensesListState.calculateFeedFood(): ExpensesListState {
        val animalList = currentProduct.pickList.animalList2.filter { it.ps }
        val updatedCountAnimal = animalList
            .sumOf { it.countAnimal }

        val updatedDailyFood = animalList
            .sumOf {
                it.foodDay.convertWeightDay(
                    it.foodDaySuffix,
                    currentProduct.feedFoodSuffix
                ) * it.countAnimal.toDouble()
            }

        return copy(
            currentProduct = currentProduct.copy(
                feedFood = updatedDailyFood.formatNumber(),
                countAnimalFood = updatedCountAnimal.formatNumber(),
            )
        )
    }

    private fun ExpensesListState.calculateFoodDays(): ExpensesListState {
        val suffix = currentProduct.countSuffix
        val isWeightSuffix = when (suffix) {
            Suffix.KILOGRAM, Suffix.GRAM, Suffix.TONS -> true
            else -> false
        }

        val weight =
            (if (isWeightSuffix) currentProduct.count else currentProduct.weightAll).toConvertZeroDouble()

        val feedFood = currentProduct.feedFood.toConvertZeroDouble()
        val day = (weight / feedFood).toInt()
        val dateEnd = endDay(currentProduct.date, day)
        return copy(
            currentProduct = currentProduct.copy(
                daysFood = day,
                dateEndFood = dateEnd
            )
        )
    }

    private fun ExpensesListState.updateValid(): ExpensesListState {
        val baseValid =
            currentProduct.title.isNotBlank() && currentProduct.count.isNotBlank() && currentProduct.price.isNotBlank() && !currentProduct.title.isSlash()
        val hasAnyError = if (currentProduct.isFood) {
            (currentProduct.countSuffix in currentProduct.suffixSet ||
                    (currentProduct.isShowAutoWeightCheckbox && currentProduct.isAutoWeight)) &&
                    currentProduct.pickList.animalList2.fastAny { it.ps } &&
                    baseValid
        } else baseValid
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = hasAnyError
            )
        )
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
}
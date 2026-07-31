package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen


import androidx.compose.ui.util.fastAny
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixAllList
import com.zaroslikov.domain.models.list.suffixFoodList
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.reduce.SectionReducer
import com.zaroslikov.fermacompose2.supportFun.convertWeightDay
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.text.lowercase

class ExpensesListReduce(private val resourceProvider: ResourceProvider) :
    SectionReducer<ExpensesListState, ExpensesListIntent>() {
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
                    entryState2 = intent.state, isTemplate = intent.isTemplate
                )
                .helperSlider()

            is ExpensesListIntent.FoodClicked -> {
                val new = state.updateIsFood(intent.value).updateFeedFoodSuffix(Suffix.KILOGRAM)
                if (!new.currentProduct.product.isFood && new.currentProduct.pickList.animalList2.fastAny { it.ps })
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
                if (new.currentProduct.product.isAutoWeight) new = new.updateWeightAll()
                if (new.currentProduct.product.isFood) new = new.calculateFoodDays()
                if (new.currentProduct.product.isAutoPrice) new = new.updatePriceAll()
                if (!new.currentProduct.product.isFood && new.currentProduct.pickList.animalList2.fastAny { it.ps })
                    new = new.helperSlider()

                new
            }

            is ExpensesListIntent.SuffixClicked -> {
                var new = state.updateCountSuffix(intent.value)
                if (state.currentProduct.product.isFood) {
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
                if (!state.currentProduct.product.isFood
                    && state.currentProduct.pickList.animalList2.fastAny { it.ps }
                ) new.helperSlider() else new
            }

            is ExpensesListIntent.AutoPriceClicked -> {
                val new = state.updateAutoPrice(intent.value).updatePriceAll()
                if (!state.currentProduct.product.isFood
                    && state.currentProduct.pickList.animalList2.fastAny { it.ps }
                ) new.helperSlider() else new
            }

            is ExpensesListIntent.CategoryChanged -> state.updateCategory(intent.value)
            is ExpensesListIntent.DateClicked -> {
                val new = state.updateDate(intent.value)
                if (state.currentProduct.product.isFood) new.calculateFoodDays() else new
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

            //Template
            is ExpensesListIntent.NameTemplateChanged -> state.updateNameTemplate(intent.value)
                .updateValid()

            is ExpensesListIntent.TitleTemplateChanged -> state.updateTitleTemplate(intent.value)
                .updateValid()

            is ExpensesListIntent.CountTemplateChanged -> state.updateCountTemplate(intent.value)
                .updateValid()

            is ExpensesListIntent.PriceTemplateClicked -> state.updatePriceTemplate(intent.value)
                .updateValid()

            is ExpensesListIntent.SuffixTemplateClicked -> state.updateSuffixTemplate(intent.value)
            is ExpensesListIntent.CategoryTemplateChanged -> state.updateCategoryTemplate(intent.value)
            is ExpensesListIntent.DateTemplateChanged -> state.updateDateTemplate(intent.value)
            is ExpensesListIntent.NoteTemplateChanged -> state.updateNoteTemplate(intent.value)
            is ExpensesListIntent.MultiProjectTemplateChanged -> state.updateMultiProjectTemplate(
                intent.value
            )

            //Templates
            is ExpensesListIntent.OpenTemplateBottomSheetClick ->
                state.updateOpenEntryInTemplate(intent.value, intent.toUiMap23).updateValid()

            else -> state
        }
        return newState.updateValid()
    }

    override fun qrReducer(
        state: ExpensesListState,
        intent: QrCodeIntent
    ): ExpensesListState {
        return when (intent) {
            is QrCodeIntent.OpenScannerQrCodeBottomSheetClick ->
                state.updateOpenScannerQrCode(intent.value)

            is QrCodeIntent.OpenWarningQrCodeBottomSheetClick ->
                state.updateOpenWarningQrCode(
                    intent.value,
                    intent.qrCodeWarningType,
                    intent.backupData
                )

            is QrCodeIntent.OpenQrCodeBottomSheetClick ->
                state.updateOpenQrCodeBottomSheet(intent.value, intent.qrCode)

            else -> state
        }
    }

    override fun templateReducer(
        state: ExpensesListState,
        intent: TemplateIntent
    ): ExpensesListState {
        return when (intent) {
            is TemplateIntent.OpenTemplateDeleteBottomSheet ->
                state.updateOpenTemplateDeleteBottomSheet(intent.value)

            is TemplateIntent.OpenPatternsBottomSheetClick ->
                state.updateOpenPatternBottomSheet(intent.value)

            is TemplateIntent.LoadDataForTemplate ->
                state.updateLoadDataForTemplate(intent.value)

            else -> state
        }
    }

    private fun ExpensesListState.updateOpenBottomSheetDetail(
        id: Long?
    ): ExpensesListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenDetail = false,
                ),
                productDetail = null
            )
        else {
            val domain = mainList.items.find { it.id == id }
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenDetail = domain?.let { true } ?: false,
                ),
                productDetail = domain
            )
        }
    }

    private fun ExpensesListState.updateOpenBottomSheetDelete(id: Long?): ExpensesListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenProductDelete = false
                ),
                productDetail = null
            )
        else {
            val domain = mainList.items.find { it.id == id }
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenProductDelete = domain?.let { true } ?: false),
                productDetail = domain
            )
        }
    }

    private fun ExpensesListState.updateSearch(textSearch: String): ExpensesListState {
        val query = textSearch.trim().lowercase()

        val searchResults = if (query.isBlank() && !mainList.isGroupMode) mainList.items
        else
            mainList.items.filter { item ->
                val category =
                    item.category ?: resourceProvider.getString(R.string.support_text_no_category)
                item.title.lowercase().contains(query) ||
                        item.note.lowercase().contains(query) ||
                        category.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) ||
                        resourceProvider.getString(item.countSuffix.toResId()).lowercase()
                            .contains(query) ||
                        "${item.day} ${resourceProvider.getString(monthToResString(item.month))} ${item.year}".lowercase()
                            .contains(query) ||
                        (item.priceAll ?: item.price).toString().lowercase().contains(query)
            }

        val searchBrieflyResults =
            if (query.isBlank() && mainList.isGroupMode) mainList.brieflyItems
            else
                mainList.brieflyItems.filter { item ->
                    item.title.lowercase().contains(query) ||
                            item.weight?.value.toString().lowercase().contains(query) ||
                            (item.price).toString().lowercase().contains(query)
                }
        return copy(
            searchState = searchState.copy(
                searchQuery = textSearch,
                searchResults = searchResults,
                searchBrieflyResults = searchBrieflyResults
            )
        )
    }

    private fun ExpensesListState.updateGroup(isGroupMode: Boolean): ExpensesListState {
        return copy(
            mainList = mainList.copy(
                isGroupMode = isGroupMode
            )
        )
    }

    private fun ExpensesListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: ExpensesProductState,
        isSaveStateForEntry: Boolean,
        isTemplate: Boolean
    ): ExpensesListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntry = isOpenEntryBottomSheet,
                isSaveStateForBottomSheet = isSaveStateForEntry
            ),
            currentProduct = entryState2.copy(
                template = entryState2.template.copy(
                    isTemplate = isTemplate
                )
            )
        )
    }

    private fun ExpensesListState.updateIsFood(isFood: Boolean): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    isFood = isFood,
                    suffixList = if (isFood) suffixFoodList else suffixAllList,
                    countSuffix = if (isFood) Suffix.KILOGRAM else Suffix.PIECES,
                    isShowAutoWeightCheckbox = false,
                )
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
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    feedFoodSuffix = feedFoodChipSuffix
                )
            )
        )
    }

    private fun ExpensesListState.updateTitle(title: String): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    title = title
                ),
                errors = currentProduct.errors.copy(
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
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    title = title,
                    countSuffix = suffix,
                    isShowAutoWeightCheckbox = currentProduct.product.isFood && suffix !in currentProduct.product.suffixSet,
                    isAutoWeight = if (currentProduct.product.isFood && suffix !in currentProduct.product.suffixSet) currentProduct.product.isAutoWeight else false,
                    weightSuffix = weightSuffix,
                    weightAllSuffix = weightAllSuffix
                ),
                errors = currentProduct.errors.copy(
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
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    count = count
                ),
                errors = currentProduct.errors.copy(
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
                product = currentProduct.product.copy(
                    countSuffix = suffix,
                    isShowAutoWeightCheckbox = currentProduct.product.isFood && suffix !in currentProduct.product.suffixSet,
                    isAutoWeight = if (currentProduct.product.isFood && suffix !in currentProduct.product.suffixSet) currentProduct.product.isAutoWeight else false,
                    weightSuffix = weightSuffix,
                    weightAllSuffix = weightAllSuffix
                )
            )
        )
    }

    private fun ExpensesListState.updateAutoWeight(isAutoWeight: Boolean): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    isAutoWeight = isAutoWeight
                )
            )
        )
    }

    private fun ExpensesListState.updateWeight(weight: String): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    weight = weight
                )
            )
        )
    }

    private fun ExpensesListState.updateWeightSuffix(weightSuffix: Suffix): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    weightSuffix = weightSuffix,
                    weightAllSuffix = weightSuffix
                )
            )
        )
    }

    private fun ExpensesListState.updatePrice(price: String): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    price = price,

                    ),
                errors = currentProduct.errors.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        )
    }


    private fun ExpensesListState.updateAutoPrice(isAutoCalculate: Boolean): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    isAutoPrice = isAutoCalculate
                )
            )
        )
    }


    private fun ExpensesListState.updatePriceAll(): ExpensesListState {
        val price = this.currentProduct.product.price.toConvertZeroDouble()
        val count = this.currentProduct.product.count.toConvertZeroDouble()
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    priceAll = (price * count).formatNumber()
                )
            )
        )
    }


    private fun ExpensesListState.updateCategory(category: String): ExpensesListState {
        return this.copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(category = category)
            )
        )
    }

    private fun ExpensesListState.updateDate(date: String): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    date = date
                )
            )
        )
    }

    private fun ExpensesListState.updateNote(note: String): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(note = note)
            )
        )
    }

    private fun ExpensesListState.updateAnimalChipByIdFood(id: Long): ExpensesListState {
        val updatedAnimals = currentProduct.pickList.animalList2.map { animal ->
            if (animal.id == id) {
                val convertedFood =
                    animal.foodDay.convertWeightDay(
                        animal.foodDaySuffix,
                        currentProduct.product.feedFoodSuffix
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
                product = currentProduct.product.copy(
                    isPercent = isPercent
                )
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
        return (if (currentProduct.product.isAutoPrice) currentProduct.product.priceAll
        else currentProduct.product.price).toConvertZeroDouble()
    }

    private fun ExpensesListState.updateWeightAll(): ExpensesListState {
        val weight = currentProduct.product.weight.toConvertZeroDouble()
        val count = currentProduct.product.count.toConvertZeroDouble()
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    weightAll = (weight * count).formatNumber()
                )
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
                    currentProduct.product.feedFoodSuffix
                ) * it.countAnimal.toDouble()
            }

        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    feedFood = updatedDailyFood.formatNumber(),
                    countAnimalFood = updatedCountAnimal.formatNumber(),
                )
            )
        )
    }

    private fun ExpensesListState.calculateFoodDays(): ExpensesListState {
        val suffix = currentProduct.product.countSuffix
        val isWeightSuffix = when (suffix) {
            Suffix.KILOGRAM, Suffix.GRAM, Suffix.TONS -> true
            else -> false
        }

        val weight =
            (if (isWeightSuffix) currentProduct.product.count else currentProduct.product.weightAll).toConvertZeroDouble()

        val feedFood = currentProduct.product.feedFood.toConvertZeroDouble()
        val day = (weight / feedFood).toInt()
        val dateEnd = endDay(currentProduct.product.date, day)
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    daysFood = day,
                    dateEndFood = dateEnd
                )
            )
        )
    }

    private fun ExpensesListState.updateValid(): ExpensesListState {
        val product = currentProduct.product
        val template = currentProduct.template
        val activeField = template.activeField

        val baseValid = product.title.isNotBlank() && !product.title.isSlash()
                && product.count.isNotBlank() && product.price.isNotBlank()

        val hasAnyError =
            when {
                template.isTemplate -> template.name.isNotBlank() &&
                        (activeField.isTitle || product.title.isNotBlank()
                                || !product.title.isSlash()) &&
                        (activeField.isCount || product.count.isNotBlank()) &&
                        (activeField.isPrice || product.price.isNotBlank())

                product.isFood -> (product.countSuffix in product.suffixSet ||
                        (product.isShowAutoWeightCheckbox && product.isAutoWeight)) &&
                        currentProduct.pickList.animalList2.fastAny { it.ps } && baseValid

                else -> baseValid
            }

        return copy(
            currentProduct = currentProduct.copy(
                errors = currentProduct.errors.copy(
                    hasAnyError = hasAnyError
                )
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

    private fun ExpensesListState.updateNameTemplate(nameTemplate: String): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    name = nameTemplate
                ),
                errors = currentProduct.errors.copy(
                    isErrorNameTemplate = nameTemplate.isBlank()
                )
            )
        )
    }

    private fun ExpensesListState.updateTitleTemplate(isTitle: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isTitle = isTitle
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateCountTemplate(isCount: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isCount = isCount
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updatePriceTemplate(isPrice: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isPrice = isPrice
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateSuffixTemplate(isSuffix: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isSuffix = isSuffix
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateCategoryTemplate(isCategory: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isCategory = isCategory
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateDateTemplate(isDate: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isDate = isDate
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateNoteTemplate(isNote: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isNote = isNote
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateMultiProjectTemplate(isMultiProjectTemplate: Boolean): ExpensesListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isMultiProjectTemplate = isMultiProjectTemplate
                    )
                )
            )
        )
    }

    private fun ExpensesListState.updateOpenEntryInTemplate(
        isOpenEntryInTemplateBottomSheet: Boolean,
        value: ExpensesProductState,
    ): ExpensesListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntryInTemplate = isOpenEntryInTemplateBottomSheet
            ),
            currentProduct = value,
        )
    }

    private fun ExpensesListState.updateOpenWarningQrCode(
        bool: Boolean,
        warning: QrCodeWarningType,
        backupData: DomainTemplateTable?
    ): ExpensesListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenWarningQrCode = bool,
                isOpenScannerQrCode = false
            ),
            qrCodeWarning = qrCodeWarning.copy(
                warningType = warning,
                templateBackup = backupData
            )
        )
    }

    private fun ExpensesListState.updateOpenScannerQrCode(
        bool: Boolean,
    ): ExpensesListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenScannerQrCode = bool
            )
        )
    }

    private fun ExpensesListState.updateOpenTemplateDeleteBottomSheet(id: Long?): ExpensesListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenTemplateDelete = false
                ),
                productDetail = null
            )
        else {
            val templateItem = templatesState.templatesList.find { it.id == id }
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenTemplateDelete = templateItem?.let { true } ?: false),
                templatesState = templatesState.copy(
                    templateToDelete = templateItem
                )
            )
        }
    }

    private fun ExpensesListState.updateOpenPatternBottomSheet(isOpenTemplates: Boolean): ExpensesListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenTemplates = isOpenTemplates
            )
        )
    }

    private fun ExpensesListState.updateLoadDataForTemplate(templateItems: List<TemplateItem>): ExpensesListState {
        return copy(
            templatesState = templatesState.copy(
                templatesList = templateItems
            )
        )
    }

    private fun ExpensesListState.updateOpenQrCodeBottomSheet(
        isOpenCreateQrCode: Boolean,
        qrCodeState: QrCodeData?
    ): ExpensesListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenCreateQrCode = isOpenCreateQrCode,
            ),
            qrCodeState = qrCodeState,
        )
    }
}
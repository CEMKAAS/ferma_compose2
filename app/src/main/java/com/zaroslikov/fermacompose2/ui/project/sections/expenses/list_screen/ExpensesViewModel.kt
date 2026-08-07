package com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.template.DomainExpensesTemplateDto
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.template.TemplateRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel3
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateFieldsState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.QrGenerator
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
import kotlin.text.contains
import kotlin.text.isNotBlank
import kotlin.text.trim

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val qrGenerator: QrGenerator,
    private val qrNavigationManager: QrNavigationManager,
    private val expensesAnimalRepository: ExpensesAnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository,
    private val templateRepository: TemplateRepository,
    private val appSettingsRepository: AppSettingsRepository
) : EntryNewViewModel3<ExpensesListState, ExpensesListIntent, ExpensesListReduce>(
    initialState = ExpensesListState(),
    ExpensesListReduce(resourceProvider),
    qrGenerator = qrGenerator,
    resourceProvider = resourceProvider,
    projectRepository = projectRepository,
    qrNavigationManager = qrNavigationManager,
    templateRepository = templateRepository,
    appSettingsRepository = appSettingsRepository
) {

    private val _itemIdPT: Long = checkNotNull(savedStateHandle[ExpensesDestination.itemIdArg])

    init {
        loadData()
        loadTemplate(TemplateType.EXPENSES)
    }

    override fun onIntent(intent: ExpensesListIntent) {
        sendIntent(intent)
        when (intent) {
            is ExpensesListIntent.OpenBottomSheetGroup ->
                openBottomSheetGroup(intent.title)

            is ExpensesListIntent.OpenEntryBottomSheetByItem -> loadDataForEntryOrEdit(
                intent.isOpen,
                intent.id,
                intent.isSaveStateForBottomSheet,
                intent.isTemplate
            )

            is ExpensesListIntent.TitleChanged -> updateWarehouseUiState(intent.value)
            is ExpensesListIntent.TitleAndSuffixClicked -> updateWarehouseUiState(intent.title)
            ExpensesListIntent.Insert -> insert()
            ExpensesListIntent.Update -> update()
            is ExpensesListIntent.Delete -> delete()
            else -> Unit
        }
    }

    override fun onQrCodeIntent(intent: QrCodeIntent) {
        sendQrCodeIntent(intent)
        when (intent) {
            is QrCodeIntent.CreateQrCodeClick -> generateQrCode(intent.value)
            is QrCodeIntent.RecoverClick -> recover(intent.value)
            is QrCodeIntent.QrDetected -> qrScanner(intent.value, TemplateType.EXPENSES, _itemIdPT)
            else -> Unit
        }
    }

    override suspend fun loadDataForPickList(id: Long?): ExpensesProductState {
        return coroutineScope {
            val titleDeferred =
                async { expensesRepository.getItemsTitleExpensesList(_itemIdPT).first() }
            val categoryDeferred =
                async { expensesRepository.getItemsCategoryExpensesList(_itemIdPT).first() }
            //TODO что-то пошло не поплану, нужно подумать как это сделать красиво
            val animalDeferred = async {
                updateAnimalList(id)
            }
            val animalList = animalDeferred.await().map { it.toUi() }
            ExpensesProductState(
                product = ExpensesProduct(
                    projectId = _itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    priceSuffix = getState().settings.currencySuffix
                ),
                pickList = ExpensesPickList(
                    titles = titleDeferred.await(),
                    categories = categoryDeferred.await(),
                    animalList2 = animalList
                )
            )
        }
    }

    override fun recover(domainTemplateTable: DomainTemplateTable) {
        viewModelScope.launch {
            val baseState = loadDataForPickList(null)
            val currentProduct = baseState.toUiMap23(
                domainTemplateTable,
                isTemplateEntry = true,
                domainTemplateTable.isMultiProjectTemplate
            )
            sendIntent(
                ExpensesListIntent.OpenTemplateBottomSheetClick(true, currentProduct)
            )
            updateWarehouseUiStateSync(
                currentProduct.product.title
            ) //TODO
        }
    }

    override fun loadDataForTemplatesBottomSheet(isOpen: Boolean) {
        viewModelScope.launch {
            if (!isOpen) return@launch
            templateRepository.getAllExpensesTemplateItems(_itemIdPT)
                .collectLatest { it ->
                    sendTemplateIntent(TemplateIntent.LoadDataForTemplate(it.map { it.toTemplateItemUi() }))
                }
        }
    }

    override fun loadDataForTemplateBottomSheet(
        id: Long,
        qrPayload: QrPayload?
    ) {
        viewModelScope.launch {
            val template =
                templateRepository.getTemplateItem(qrPayload?.itemId ?: id).first()
                    ?: return@launch sendQrCodeIntent(
                        QrCodeIntent.OpenWarningQrCodeBottomSheetClick(
                            true,
                            QrCodeWarningType.LOCAL,
                            qrPayload?.backupData
                        )
                    )

            val baseState = loadDataForPickList(qrPayload?.itemId ?: id)
            val currentProduct = baseState.toUiMap23(template, isTemplateEntry = true)
            sendIntent(ExpensesListIntent.OpenTemplateBottomSheetClick(true, currentProduct))
            updateWarehouseUiStateSync(currentProduct.product.title)
        }
    }

    override fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(_itemIdPT).first()
            combine(
                expensesRepository.getAllExpensesItems(_itemIdPT),
                settingsRepository.getSettings(_itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                val expensesList = addList.map { it.toUi() }
                Triple(expensesList, brieflyList, settings)
            }.collectLatest { (expensesItems, brieflyBrieflyItems, settings) ->
                val currentDetail = getState().productDetail
                updateState { state ->
                    state.copy(
                        idPT = _itemIdPT,
                        mainList = state.mainList.copy(
                            items = expensesItems,
                            brieflyItems = brieflyBrieflyItems,
                        ),
                        searchState = state.searchState.copy(
                            searchResults = expensesItems,
                            searchBrieflyResults = brieflyBrieflyItems
                        ),
                        settings = settings,
                        productDetail = currentDetail?.let { detail ->
                            expensesItems.find { it.id == detail.id }
                        },
                        isLoading = false,
                        isArchive = isArchive
                    )
                }
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainExpensesTable> {
        return expensesRepository.getBrieflyDetailsItemExpenses(_itemIdPT, name).first()
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
                    state.copy(
                        bottomSheetState = state.bottomSheetState.copy(
                            isOpenGroup = false
                        )
                    )
                }
                return@launch
            }
            val listBriefly = getDetailsName(name = title)
            val listBrieflyMap = listBriefly.map { it.toUi() }
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, settings = getState().settings)
            updateState {
                it.copy(
                    bottomSheetState = it.bottomSheetState.copy(
                        isOpenGroup = true
                    ),
                    detailNomenclatura = it.detailNomenclatura.copy(
                        detail = currentBriefly,
                        productItems = listBrieflyMap
                    )
                )
            }
        }
    }

    override fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        id: Long?,
        isSaveStateForBottomSheet: Boolean,
        isTemplate: Boolean
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else ExpensesProductState()
                sendIntent(
                    ExpensesListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet, false
                    )
                )
                return@launch
            }
            val newState =
                if (!getState().bottomSheetState.isSaveStateForBottomSheet || id != null) {
                    val baseState = loadDataForPickList(id)
                    when {
                        isTemplate && id == null -> baseState
                        isTemplate && id != null -> {
                            val template = templateRepository.getTemplateItem(id).first()
                                ?: return@launch
                            baseState.toUiMap23(template)
                        }

                        id == null -> baseState

                        else -> {
                            val expensesItem = expensesRepository.getItemExpenses(id).first()
                            baseState.toUiMap(expensesItem)
                        }
                    }
                } else getState().currentProduct

            updateWarehouseUiStateSync(newState.product.title)
            sendIntent(
                ExpensesListIntent.RefreshEntryBottomSheetState(
                    true, newState, isTemplate = isTemplate
                )
            )
        }
    }

    private fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = warehouseRepository
            .getCurrentExpensesProductList(name, _itemIdPT).first().build(getState().settings)

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
            val currentProduct = getState().currentProduct
            val id =
                expensesRepository.insertExpenses(currentProduct.product.toDomainMap(true))
            setExpensesAnimal(id)
            yandexMetricRepository.metricalExpenses(currentProduct)
            if (currentProduct.template.isTemplateEntry)
                sendIntent(ExpensesListIntent.OpenTemplateBottomSheetClick(false))
            else {
                showSnackbar(ProductOperation.ADD)
                loadDataForEntryOrEdit(false, null)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            expensesRepository.updateExpenses(getState().currentProduct.product.toDomainMap(false))
            saveExpensesAnimal()
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete() {
        viewModelScope.launch {
            getState().productDetail?.let { product ->
                expensesRepository.deleteExpensesById(product.id)
                showSnackbar(ProductOperation.DELETE)
                deleteExpensesAnimalById(product.id)
                sendIntent(ExpensesListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    override fun insertTemplate() {
        viewModelScope.launch {
            val domainTemplate = getState().currentProduct.toDomainTemplate()
            templateRepository.insert(domainTemplate)
            yandexMetricRepository.metricalTemplate(domainTemplate)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun updateTemplate() {
        viewModelScope.launch {
            templateRepository.update(getState().currentProduct.toDomainTemplate())
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun deleteTemplate() {
        viewModelScope.launch {
            getState().templatesState.templateToDelete?.let { template ->
                templateRepository.deleteAddTemplateItemById(template.id)
                sendTemplateIntent(TemplateIntent.OpenTemplateDeleteBottomSheet(null))
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
                idPT = _itemIdPT
            )
        }.forEach {
            expensesAnimalRepository.insertExpensesAnimal(it)
        }
    }

    private suspend fun saveExpensesAnimal() {
        getState().currentProduct.pickList.animalList2.forEach {
            val table = DomainExpensesAnimal(
                id = it.idExpensesAnimal,
                idExpenses = getState().currentProduct.product.itemId,
                idAnimal = it.id,
                percentExpenses = it.presentException,
                idPT = _itemIdPT
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
                val product = getState().productDetail ?: ExpensesTableUi()
                product.title to product.count.formatNumber()
            } else {
                val product = getState().currentProduct.product
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

    fun ExpensesProductState.toUiMap(
        domain: DomainExpensesTable
    ): ExpensesProductState {
        val isIndicatorsValue =
            setOf(domain.animalId, domain.animalVaccinationId, domain.animalCountId)
                .any { it != null }
        val isAutoWeight = domain.weight != null
        val weightAll = when {
            domain.isFood && isAutoWeight -> ((domain.weight ?: 0.0) * domain.count).formatNumber()
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
            product = product.copy(
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
                category = domain.category
                    ?: resourceProvider.getString(R.string.support_text_no_category),
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
                hasIndicators = isIndicatorsValue,
                isEntry = false,
                projectId = domain.idPT,
                animalId = domain.animalId,
                animalVaccinationId = domain.animalVaccinationId,
                animalCountId = domain.animalCountId
            ),
            errors = ExpensesError()
        )
    }

    private fun ExpensesProduct.toDomainMap(isEntry: Boolean): DomainExpensesTable {
        val dateList = date.split(".")
        val (weight, weightSuffix) = if (isFood) {
            if (isAutoWeight) weight.toConvertDbDouble() to weightSuffix else null to null
        } else null to null
        val category = category.trim()
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
            priceSuffix = priceSuffix,
            category = if (category.contains(resourceProvider.getString(R.string.support_text_no_category)) || category.isEmpty())
                null else category,
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
            idPT = _itemIdPT,
            animalId = animalId,
            animalVaccinationId = animalVaccinationId,
            animalCountId = animalCountId,
        )
    }

    private fun ExpensesProductState.toUiMap23(
        domain: DomainTemplateTable,
        isTemplateEntry: Boolean = false,
        isMultiProjectTemplate: Boolean = false
    ): ExpensesProductState {
        val category =
            domain.category?.ifBlank { resourceProvider.getString(R.string.support_text_no_category) }
                ?: ""
        return copy(
            product = product.copy(
                itemId = if (isTemplateEntry) product.itemId else domain.id,
                title = domain.title ?: "",
                count = domain.count?.formatNumber(false) ?: "",
                countSuffix = domain.countSuffix ?: Suffix.NO,
                isAutoPrice = domain.priceAll != null,
                price = domain.price?.formatNumber(false) ?: "",
                priceAll = domain.priceAll?.formatNumber() ?: "",
                category = category,
                note = domain.note ?: "",
                isEntry = false,
                projectId = if (isMultiProjectTemplate) _itemIdPT else domain.idPT,
            ),
            template = template.copy(
                name = domain.nameTemplate,
                isTemplate = false,
                isTemplateEntry = isTemplateEntry,
                pin = domain.isPinned,
                activeField = TemplateFieldsState(
                    isTitle = domain.title == null,
                    isCount = domain.count == null,
                    isPrice = domain.price == null,
                    isPriceAll = domain.priceAll == null,
                    isSuffix = domain.countSuffix == null,
                    isCategory = domain.category == null,
                    isDate = domain.isDate,
                    isNote = domain.note == null,
                    isMultiProjectTemplate = domain.isMultiProjectTemplate
                )
            ),
            errors = ExpensesError(),
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
            category = category,
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


    private fun ExpensesProductState.toDomainTemplate(): DomainTemplateTable {
        val activeField = template.activeField
        return DomainTemplateTable(
            id = product.itemId,
            templateType = TemplateType.EXPENSES,
            nameTemplate = template.name.trim(),
            title = if (activeField.isTitle) null else product.title.trim(),
            count = if (activeField.isCount) null else product.count.toConvertDbDouble(),
            countSuffix = if (activeField.isSuffix) null else product.countSuffix,
            price = if (activeField.isPrice) null else product.price.toConvertDbDouble(),
            priceAll = when {
                !activeField.isPrice && product.isAutoPrice ->
                    if (activeField.isCount) 0.0 else product.priceAll.toConvertDbDouble()

                else -> null
            },
            priceSuffix = product.priceSuffix,
            category = if (activeField.isCategory) null else product.category.trim(),
            isDate = activeField.isDate,
            note = if (activeField.isNote) null else product.note.trim(),
            idPT = _itemIdPT,
            isPinned = template.pin,
            isMultiProjectTemplate = activeField.isMultiProjectTemplate
        )
    }

    private fun DomainExpensesTemplateDto.toTemplateItemUi(): TemplateItem {
        val suffix = priceSuffix?.let { resourceProvider.getString(it.toResId()) }
        val description = listOfNotNull(
            title?.takeIf { it.isNotBlank() },
            count?.formatNumber(),
            countSuffix?.let { resourceProvider.getString(it.toResId()) },
            price?.let { "${it.formatNumber()} $suffix".trim() },
            priceAll?.let { if (it != 0.0) "${it.formatNumber()} $suffix".trim() else null },
            category?.takeIf { !it.contains(resourceProvider.getString(R.string.support_text_no_category)) && it.isNotBlank() },
            note?.takeIf { it.isNotBlank() }
        ).joinToString(" · ")
        return TemplateItem(
            id = id,
            name = nameTemplate,
            description = description,
            isPinned = isPinned,
            isMultiProject = isMultiProject
        )
    }

    //TODO нейронка предлогает в общий поток зафигачить, а не каждый раз запрашивать я даже хзх
    private suspend fun updateAnimalList(id: Long?): List<AnimalExpensesDomain> {
        return expensesRepository.getItemsAnimalExpensesList2(
            _itemIdPT,
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
package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

@HiltViewModel
class WriteOffViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val writeOffRepository: WriteOffRepository,
    private val warehouseRepository: WarehouseRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel2<WriteOffListState, WriteOffListIntent, WriteOffListReduce>(
    WriteOffListState(),
    WriteOffListReduce(resourceProvider)
) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[WriteOffDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: WriteOffListIntent) {
        sendIntent(intent)
        when (intent) {
            is WriteOffListIntent.OpenBottomSheetEntry ->
                loadDataForEntryOrEdit(intent.isOpen, intent.item, intent.isSaveStateForBottomSheet)

            is WriteOffListIntent.OpenBottomSheetGroup ->
                openBottomSheetGroup(intent.value)

            is WriteOffListIntent.TitleAndSuffix ->
                updateWarehouseUiState(intent.title, intent.writeOffCategory)

            WriteOffListIntent.Insert -> insert()
            WriteOffListIntent.Update -> update()
            is WriteOffListIntent.Delete -> delete(0)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            combine(
                writeOffRepository.getAllWriteOffItems(itemIdPT),
                addRepository.getItemsTitleAddList(itemIdPT),
                settingsRepository.getSettings(itemIdPT)
            ) { addList, titleList, settings ->
                val brieflyList = brieflyList(addList, settings)
                LoadDataWriteOffList(addList, brieflyList, titleList, settings)
            }.collectLatest { (addList, briefly, titleList, setting) ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = addList,
                        briefly = briefly,
                        searchList = addList,
                        searchBrieflyList = briefly,
                        writeOffBoolean = titleList.isNotEmpty(),
                        settings = setting,
                        isLoading = false,
                        isArchive = isArchive
                    )
                }
            }
        }
    }

    private fun brieflyList(
        list: List<DomainWriteOffTable>,
        settings: DomainSettings
    ): List<BrieflyItem> {
        return list
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings = settings)
            }
    }


    private suspend fun getDetailsName(name: String): List<DomainWriteOffTable> {
        return writeOffRepository.getBrieflyDetailsItemWriteOff(itemIdPT, name).first()
    }

    override fun insert() {
        viewModelScope.launch {
            writeOffRepository.insertWriteOff(getState().currentProduct.updateForSave())
            yandexMetricRepository.metricalWriteOff(getState().currentProduct)
            showSnackbar(ProductOperation.ADD)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            writeOffRepository.updateWriteOff(getState().currentProduct.updateForSave())
            if (getState().currentDetail != null)
                updateState { it.copy(currentDetail = getState().currentProduct.updateForSave()) }
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().currentDetail?.let { product ->
                writeOffRepository.deleteWriteOff(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(WriteOffListIntent.OpenBottomSheetDelete(null))
            }
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
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, settings = getState().settings)

            updateState {
                it.copy(
                    isOpenGroupBottomSheet = true,
                    currentBriefly = currentBriefly,
                    listBriefly = listBriefly
                )
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: DomainWriteOffTable?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else WriteOffEntryState2()
                onIntent(
                    WriteOffListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForBottomSheet || domain != null) {
                val titleList = writeOffRepository.getItemsWriteOffList(itemIdPT).first()

                val baseState = WriteOffEntryState2(
                    itemIdPT = itemIdPT,
                    pickList = PickWriteOffList(
                        titleList = titleList
                    )
                )
                if (domain == null) baseState
                else {
                    val saleCategory = baseState.pickList.titleList
                        .firstOrNull { it.title == domain.title }
                        ?.category
                    baseState.updateFromDomain(domain).copy(writeOffCategory = saleCategory)
                }
            } else getState().currentProduct
            onIntent(WriteOffListIntent.RefreshEntryBottomSheetState(true, newState))
            newState.writeOffCategory?.let { updateWarehouseUiStateSync(newState.title, it) }
        }
    }

    private fun updateWarehouseUiState(name: String, category: Category) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, category)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES)
            warehouseRepository.getCurrentExpensesProductList(name, itemIdPT).first()
                .build(getState().settings)
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, itemIdPT).first().build(getState().settings)

        onIntent(WriteOffListIntent.RefreshWarehouseCount(pair))
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

    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, count, countSuffix) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().currentDetail ?: DomainAddItemDto()
                Triple(product.title, product.count.formatNumber(), product.countSuffix)
            } else {
                val product = getState().currentProduct
                Triple(product.title, product.count, product.countSuffix)
            }
        val suffix = resourceProvider.getString(countSuffix.toResId())
        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_write_off_add)
                    .format(title, count, suffix)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_write_off_update)
                    .format(title, count, suffix)

                else -> resourceProvider.getString(R.string.snackbar_write_off_delete)
                    .format(title, count, suffix)
            }
        )
    }

    private fun WriteOffEntryState2.updateFromDomain(domain: DomainWriteOffTable): WriteOffEntryState2 {
        val isIndicatorsValue =
            setOf(domain.animalCountId)
                .any { it != null }
        return copy(
            itemId = domain.id,
            title = domain.title,
            count = domain.count.formatNumber(false),
            countSuffix = domain.countSuffix,
            isAutoPrice = domain.priceAll != null,
            price = domain.price?.formatNumber(false) ?: "",
            priceAll = domain.priceAll?.formatNumber() ?: "",
            date = formatDateToString(
                domain.day,
                domain.month,
                domain.year
            ),
            status = domain.status,
            note = domain.note,
            animalCountId = domain.animalCountId,
            isIndicatorsValue = isIndicatorsValue,
            isEntry = false
        )
    }

    private fun WriteOffEntryState2.updateForSave(): DomainWriteOffTable {
        val dateList = date.split(".")
        return DomainWriteOffTable(
            id = itemId,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            countSuffix = countSuffix,
            price = if (price.isBlank()) null else price.toConvertDbDouble(),
            priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            note = note.trim(),
            status = status,
            animalCountId = animalCountId,
            idPT = itemIdPT,
        )
    }
}
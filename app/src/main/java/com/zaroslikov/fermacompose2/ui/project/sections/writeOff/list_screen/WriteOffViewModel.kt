package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteOffViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val writeOffRepository: WriteOffRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider
) : EntryNewViewModel<WriteOffListState, WriteOffListIntent>(WriteOffListState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[WriteOffDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: WriteOffListIntent) {
        when (intent) {
            is WriteOffListIntent.OpenBottomSheetEntry -> openBottomSheetEntry(
                intent.value,
                intent.item
            )

            is WriteOffListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(
                intent.value,
                intent.currentBriefly
            )

            is WriteOffListIntent.TitleAndSuffix -> updateTitleAndSuffix(
                intent.title,
                intent.suffix
            )

            is WriteOffListIntent.CountChanged -> updateCount(intent.value)
            is WriteOffListIntent.PriceChanged -> updatePrice(intent.value)
            is WriteOffListIntent.AutoPriceClicked -> updateIsAutoPrice(intent.value)
            is WriteOffListIntent.DateClicked -> updateDate(intent.value)
            is WriteOffListIntent.NoteChanged -> updateNote(intent.value)
            is WriteOffListIntent.StatusClicked -> updateStatus(intent.value)

            WriteOffListIntent.Insert -> insert()
            WriteOffListIntent.Update -> update()
            is WriteOffListIntent.Delete -> delete(intent.value)

            is WriteOffListIntent.SearchChanged -> updateSearch(intent.value)
            is WriteOffListIntent.CountWarehouse -> {/*updateCountWarehouse()*/
            }

            is WriteOffListIntent.GroupClicked -> updateGroup(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                writeOffRepository.getAllWriteOffItems(itemIdPT),
                writeOffRepository.getBrieflyItemWriteOff(itemIdPT),
                addRepository.getItemsTitleAddList(itemIdPT)
            ) { addList, briefly, titleList ->
                Triple(addList, briefly, titleList)
            }.collectLatest { (addList, briefly, titleList) ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = addList,
                        briefly = briefly,
                        writeOffBoolean = titleList.isNotEmpty(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainWriteOffTable> {
        return writeOffRepository.getBrieflyDetailsItemWriteOff(itemIdPT, name).first()
    }

    private fun updateTitleAndSuffix(title: String, suffix: Suffix) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    title = title,
                    countSuffix = suffix,
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

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    price = price
                )
            )
        }
        updatePriceAll()
    }

    private fun updateIsAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    priceAll = if (it.currentProduct.isAutoPrice) (it.currentProduct.price.toConvertZeroDouble() * it.currentProduct.count.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        }
    }

    private fun updateCountWarehouse(domainCountSuffix: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    warehouseList = domainCountSuffix
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    date = date,
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    note = note
                )
            )
        }
    }

    private fun updateStatus(status: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(status = status)
            )
        }
    }

    private fun updateWarehouse(warehouseList: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(warehouseList = warehouseList)
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

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                writeOffRepository.insertWriteOff(getState().currentProduct.updateForSave(itemIdPT))
//            metricaWriteOff(writeOffUiState.copy(priceAll = autoCalculate()))
                openBottomSheetEntry(false, null)
                showMessage(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                writeOffRepository.updateWriteOff(getState().currentProduct.updateForSave(itemIdPT))
                openBottomSheetEntry(false, null)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            writeOffRepository.deleteWriteOff(id)
            openBottomSheetEntry(false, null)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    ) //Todo Обновить название
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank(),
                        isErrorSlash = state.currentProduct.title.isSlash(),
                        isErrorCount = state.currentProduct.count.isBlank()
                    )
                )
            )
        }
    }

    private fun openBottomSheetGroup(
        openBottomSheetGroup: Boolean,
        currentBriefly: BrieflyWriteOffDomain
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
        domainWriteOffTable: DomainWriteOffTable?
    ) {
        if (openBottomSheetEntry)
            viewModelScope.launch {
                val titleList = writeOffRepository.getItemsWriteOffList(itemIdPT).first()

                updateState {
                    it.copy(
                        openBottomSheetEntry = true,
                        currentProduct = WriteOffEntryState2(
                            itemIdPT = itemIdPT,
                            countSuffix = Suffix.PIECES,
                            error = ErrorWriteOff(),
                            titleList = titleList
                        )
                    )
                }
                domainWriteOffTable?.let {
                    val writeOffTable = writeOffRepository.getItemWriteOff(domainWriteOffTable.id)
                        .filterNotNull()
                        .first()

                    updateState {
                        it.copy(
                            currentProduct = it.currentProduct.updateFromDomain(writeOffTable)
                        )
                    }

                    val suffix = getState().currentProduct.titleList
                        .firstOrNull { it.title == getState().currentProduct.title }
                        ?.category

                    suffix?.let {
                        updateWarehouseUiStateSync(getState().currentProduct.title, it)
                    }
                }
            }
        else updateState { it.copy(openBottomSheetEntry = false) }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES)
            warehouseRepository.getCurrentExpensesProductList(name, itemIdPT)
                .filterNotNull()
                .firstOrNull()
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, itemIdPT)
                .filterNotNull()
                .firstOrNull()

        if (pair != null)
            updateCountWarehouse(pair)
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

    private fun WriteOffEntryState2.updateForSave(itemIdPT: Long): DomainWriteOffTable {
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
            idPT = itemIdPT,
            animalCountId = animalCountId,
        )
    }
}

sealed class WriteOffListIntent : BaseIntent {
    data class OpenBottomSheetGroup(
        val value: Boolean,
        val currentBriefly: BrieflyWriteOffDomain = BrieflyWriteOffDomain()
    ) : WriteOffListIntent()

    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainWriteOffTable? = null
    ) : WriteOffListIntent()

    data class TitleAndSuffix(val title: String, val suffix: Suffix) : WriteOffListIntent()
    data class CountChanged(val value: String) : WriteOffListIntent()
    data class PriceChanged(val value: String) : WriteOffListIntent()
    data class AutoPriceClicked(val value: Boolean) : WriteOffListIntent()
    data class DateClicked(val value: String) : WriteOffListIntent()
    data class NoteChanged(val value: String) : WriteOffListIntent()
    data class StatusClicked(val value: Boolean) : WriteOffListIntent()
    data object Insert : WriteOffListIntent()
    data object Update : WriteOffListIntent()
    data class Delete(val value: Long) : WriteOffListIntent()

    data class CountWarehouse(val value: List<DomainCountSuffix>) : WriteOffListIntent()
    data class GroupClicked(val value: Boolean) : WriteOffListIntent()
    data class SearchChanged(val value: String) : WriteOffListIntent()
}
package com.zaroslikov.fermacompose2.ui.sections.writeOff.entry


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dto.PairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.firstOrNull

@HiltViewModel
class WriteOffEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val writeOffRepository: WriteOffRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<WriteOffEntryState, WriteOffIntent>(WriteOffEntryState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1L

    override fun onIntent(intent: WriteOffIntent) {
        when (intent) {
            is WriteOffIntent.TitleAndSuffix -> updateTitleAndSuffix(intent.title, intent.suffix)
            is WriteOffIntent.CountChanged -> updateCount(intent.value)
            is WriteOffIntent.PriceChanged -> updatePrice(intent.value)
            is WriteOffIntent.AutoPriceClicked -> updateIsAutoPrice(intent.value)
            is WriteOffIntent.DateClicked -> updateDate(intent.value)
            is WriteOffIntent.NoteChanged -> updateNote(intent.value)
            is WriteOffIntent.StatusClicked -> updateStatus(intent.value)
            WriteOffIntent.Insert -> insert()
            WriteOffIntent.Update -> update()
            WriteOffIntent.Delete -> delete()
        }
    }

    init {
        viewModelScope.launch {
            if (!isEntry) {
                val writeOffTable = writeOffRepository.getItemWriteOff(itemId)
                    .filterNotNull()
                    .first()
                updateState { it.updateFromDomain(writeOffTable) }
            }

            val titleList = writeOffRepository.getItemsWriteOffList(itemIdPT).first()
            updateState {
                it.copy(
                    isEntry = isEntry,
                    countSuffix = resourceProvider.getString(R.string.suffix_pieces),
                    titleList = titleList
                )
            }

            val suffix = getState().titleList
                .firstOrNull { it.title == getState().title }
                ?.category

            suffix?.let {
                if (!isEntry) updateWarehouseUiStateSync(getState().title, it)
            }
        }
    }

    fun updateWarehouseUiState(name: String, category: Category) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, category)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES)
            warehouseRepository.getCurrentExpensesProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()

        if (pair != null)
            updateCountWarehouse(pair)
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                writeOffRepository.insertWriteOff(
                    getState().updateForSave(itemIdPT = itemIdPT)
                )
//            metricaWriteOff(writeOffUiState.copy(priceAll = autoCalculate()))
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                writeOffRepository.updateWriteOff(
                    getState().updateForSave(itemId, itemIdPT)
                )
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            writeOffRepository.deleteWriteOff(itemId)
            navigateTo(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().title,
                        getState().count,
                        getState().countSuffix
                    ) //Todo Обновить название
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorTitle = state.title.isBlank(),
                    isErrorSlash = state.title.isSlash(),
                    isErrorCount = state.count.isBlank()
                )
            )
        }
    }

    private fun updateTitleAndSuffix(title: String, suffix: String) {
        updateState {
            it.copy(
                title = title,
                countSuffix = suffix,
                error = it.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        }
    }

    private fun updateCount(count: String) {
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

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                price = price
            )
        }
        updatePriceAll()
    }

    private fun updateIsAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                isAutoPrice = isAutoPrice
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                priceAll = if (it.isAutoPrice) (it.price.toConvertZeroDouble() * it.count.toConvertZeroDouble()).formatNumber()
                else "0"
            )
        }
    }

    private fun updateCountWarehouse(domainCountSuffix: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                warehouseList = domainCountSuffix
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                date = date,
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(note = note)
        }
    }

    private fun updateStatus(status: Boolean) {
        updateState {
            it.copy(status = status)
        }
    }
}

sealed class WriteOffIntent : BaseIntent {
    data class TitleAndSuffix(val title: String, val suffix: String) : WriteOffIntent()
    data class CountChanged(val value: String) : WriteOffIntent()
    data class PriceChanged(val value: String) : WriteOffIntent()
    data class AutoPriceClicked(val value: Boolean) : WriteOffIntent()
    data class DateClicked(val value: String) : WriteOffIntent()
    data class NoteChanged(val value: String) : WriteOffIntent()
    data class StatusClicked(val value: Boolean) : WriteOffIntent()
    data object Insert : WriteOffIntent()
    data object Update : WriteOffIntent()
    data object Delete : WriteOffIntent()
}
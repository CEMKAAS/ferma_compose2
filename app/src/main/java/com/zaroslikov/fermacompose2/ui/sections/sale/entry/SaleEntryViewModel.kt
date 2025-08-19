package com.zaroslikov.fermacompose2.ui.sections.sale.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.toRoomMap
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.ui.composeElement.Category
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
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

@HiltViewModel
class SaleEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1

    var saleUiState by mutableStateOf(
        SaleEntryState().copy(
            isEntry = isEntry,
            category = resourceProvider.getString(R.string.support_text_no_category),
            countSuffix = resourceProvider.getString(R.string.suffix_pieces),
            buyer = resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer)
        )
    )
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            if (!isEntry) {
                val domainSaleTable = itemsRepository.getItemSale(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()

                saleUiState = saleUiState.updateFromDomain(domainSaleTable)
            }

            val titleList = itemsRepository.getItemsTitleSaleList(itemIdPT).first()
            val categoryList = itemsRepository.getItemsCategorySaleList(itemIdPT).first()
            val buyerList = itemsRepository.getItemsBuyerSaleList(itemIdPT).first()
            saleUiState = saleUiState.updateList(
                titleList, categoryList, buyerList
            )

            val suffix = saleUiState.titleList
                .firstOrNull { it.first == saleUiState.title }
                ?.third

           suffix?.let {
               if (!isEntry) updateWarehouseUiStateSync(saleUiState.title, it)
           }
        }
    }

    fun updateUiState(state: SaleEntryState) {
        saleUiState =
            state
    }

    fun updateWarehouseUiState(name: String, category: Category) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, category)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES) {
            itemsRepository.getCurrentExpensesProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()
        } else {
            itemsRepository
                .getCurrentBalanceProductList(name,  itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()
        }

        if (pair != null) {
            saleUiState = saleUiState.updateCountWarehouse(pair)
        }
    }

    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.insertSale(
                    saleUiState.updateForSave(itemIdPT = itemIdPT.toLong()).toRoomMap()
                )
//                metricaSale(saleUiState.copy(priceAll = autoCalculate()))
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            saleUiState.title,
                            saleUiState.count,
                            saleUiState.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.updateSale(
                    saleUiState.updateForSave(id = itemId.toLong(), itemIdPT = itemIdPT.toLong())
                        .toRoomMap()
                )
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            saleUiState.title,
                            saleUiState.count,
                            saleUiState.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteSaleById(itemId.toLong())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        saleUiState.title,
                        saleUiState.count,
                        saleUiState.countSuffix
                    ) //Todo Обновить название
            )
        }
    }

    fun showMessage(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }

    fun isError(): Boolean {
        updateUiState(saleUiState.validate())
        return saleUiState.error.hasAnyError
    }
}
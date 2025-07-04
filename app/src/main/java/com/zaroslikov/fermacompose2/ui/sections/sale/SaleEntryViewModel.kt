package com.zaroslikov.fermacompose2.ui.sections.sale

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.supportFun.metricaSale
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    var isIndicatorsValue = false

    val isAutoCalculate = mutableStateOf(false)

    var saleUiState by mutableStateOf(
        DomainSaleTable().copy(
            category = resourceProvider.getString(R.string.support_text_no_category),
            suffix = resourceProvider.getString(R.string.suffix_pieces),
            buyer = resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer)
        )
    )
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (!isEntry)
            viewModelScope.launch {
                saleUiState = itemsRepository.getItemSale(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()
                isIndicatorsValue = saleUiState.animalCountId != null
            }
    }

    fun updateUiState(domainAddTable: DomainSaleTable) {
        saleUiState =
            domainAddTable
    }


    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleSaleList(itemIdPT).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategorySaleList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


    val buyerUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsBuyerSaleList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    var itemUiState by mutableStateOf(DomainPairDataDoubleSting())
        private set

    fun updateWarehouseUiState(pair: Pair<String, String>) {
        viewModelScope.launch {
            itemUiState = when (pair.second) {
                "Моя Продукция" -> {
                    itemsRepository.getCurrentBalanceProduct(pair.first, itemId.toLong())
                        .filterNotNull()
                        .first()
                        .toDomainMap()
                }
                //todo
//                "Купленный товар" -> {
//                    itemsRepository.getCurrentExpensesProduct(pair.first, itemId.toLong())
//                        .filterNotNull()
//                        .first()
//                        .toDouble()
//                }
                else -> DomainPairDataDoubleSting()
            }
        }
    }

    fun insertItem() {
        viewModelScope.launch {
            itemsRepository.insertSale(
                saleUiState.copy(
                    priceAll = autoCalculate(),
                    idPT = itemIdPT.toLong()
                ).toRoomMap()
            )
            metricaSale(saleUiState.copy(priceAll = autoCalculate()))
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_sale_s)
                    .format(
                        saleUiState.title,
                        saleUiState.count,
                        saleUiState.suffix
                    ) //Todo Обновить название
            )
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            autoCalculate()
            itemsRepository.updateSale(saleUiState.copy(priceAll = autoCalculate()).toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_refresh_s_s)
                    .format(
                        saleUiState.title,
                        saleUiState.count,
                        saleUiState.suffix
                    ) //Todo Обновить название
            )
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteSale(saleUiState.toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        saleUiState.title,
                        saleUiState.count,
                        saleUiState.suffix
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

    fun autoCalculate(): String = if (isAutoCalculate.value) calculatePriceAll(
        saleUiState.priceAll,
        saleUiState.count
    ) else saleUiState.priceAll

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
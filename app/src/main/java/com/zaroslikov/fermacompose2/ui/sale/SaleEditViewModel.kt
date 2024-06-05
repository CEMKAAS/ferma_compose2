package com.zaroslikov.fermacompose2.ui.sale

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.ui.home.AddEditDestination
import com.zaroslikov.fermacompose2.ui.home.AnimalUiState
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.home.toAddTableUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SaleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[SaleEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEditDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(SaleTableUiState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemSale(itemId)
                .filterNotNull()
                .first()
                .toSaleTableUiState()
        }
    }

    fun updateUiState(itemDetails: SaleTableUiState) {
        itemUiState =
            itemDetails
    }

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleSaleList(itemIdPT).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategorySaleList(itemIdPT).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsAnimalSaleList(itemIdPT).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )
    val buyerUiState: StateFlow<BuyerUiState> =
        itemsRepository.getItemsAnimalSaleList(itemIdPT).map {  BuyerUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue =  BuyerUiState()
            )

    suspend fun saveItem() {
        itemsRepository.updateSale(itemUiState.toSaleTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteSale(itemUiState.toSaleTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class SaleTableUiState(
    val id: Int = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = 0,  // день
    val mount: Int = 0, // месяц
    val year: Int = 0, // время
    val priceAll: String = "",
    val idPT: Int = 0,
    var suffix: String = "",
    var category: String = "",
    var animal: String = "",
    var buyer: String = ""
)

fun SaleTable.toSaleTableUiState(): SaleTableUiState = SaleTableUiState(
    id, title, count.toString(), day, mount, year, priceAll.toString(), idPT, suffix, category, animal, buyer
)

fun SaleTableUiState.toSaleTable(): SaleTable = SaleTable(
    id = id,
    title = title,
    count = count.toDouble(),
    day = day,
    mount, year, priceAll.toDouble(), suffix, category, animal, buyer, idPT
)

data class BuyerUiState(val buyerList: List<String> = listOf())

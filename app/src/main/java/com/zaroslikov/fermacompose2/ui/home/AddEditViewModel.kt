//package com.zaroslikov.fermacompose2.ui.home
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableDoubleStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
//import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
//import com.zaroslikov.fermacompose2.data.ItemsRepository
//import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
//import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
//import com.zaroslikov.fermacompose2.supportFun.DataStringListState
//import com.zaroslikov.fermacompose2.supportFun.DataTripleListState
//import com.zaroslikov.fermacompose2.supportFun.PairDataDoubleSting
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.filterNotNull
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//
//class AddEditViewModel(
//    savedStateHandle: SavedStateHandle,
//    private val itemsRepository: ItemsRepository
//) : ViewModel() {
//
//    private val itemId: Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArg])
//    private val itemIdPT: Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArgTwo])
//
//    var itemUiState by mutableStateOf(DomainAddTable())
//        private set
//
//    init {
//        viewModelScope.launch {
//            itemUiState = itemsRepository.getItemAdd(itemId)
//                .filterNotNull()
//                .first()
//                .toDomainMap()
//        }
//    }
//
//    val titleUiState: StateFlow<DataStringListState> =
//        itemsRepository.getItemsTitleAddList(itemIdPT).map { DataStringListState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = DataStringListState()
//            )
//
//
//    val categoryUiState: StateFlow<DataStringListState> =
//        itemsRepository.getItemsCategoryAddList(itemIdPT).map { DataStringListState(it) }
//            .filterNotNull()
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = DataStringListState()
//            )
//
//    val animalUiState: StateFlow<DataTripleListState> =
//        itemsRepository.getItemsAnimalAddList(itemIdPT).map { DataTripleListState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = DataTripleListState()
//            )
//
//    var countWarehouseUiState by mutableStateOf(DomainPairDataDoubleSting())
//        private set
//
//    fun updatecountWarehouseUiState(name: String) {
//        viewModelScope.launch {
//            countWarehouseUiState = itemsRepository.getCurrentBalanceProduct(name, itemId.toLong())
//                .filterNotNull()
//                .first()
//                .toDomainMap()
//        }
//    }
//
//
//    fun updateUiState(itemDetails: DomainAddTable) {
//        itemUiState =
//            itemDetails
//    }
//
//    suspend fun saveItem() {
//        itemsRepository.updateItem(itemUiState.toRoomMap())
//    }
//
//    suspend fun deleteItem() {
//        itemsRepository.deleteItem(itemUiState.toRoomMap())
//    }
//
//    companion object {
//        private const val TIMEOUT_MILLIS = 5_000L
//    }
//
//}

package com.zaroslikov.fermacompose2.ui.sections.add.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
) : ListViewModel<AddListState, AddListIntent>(AddListState()) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])

    init {
        viewModelScope.launch {
            combine(
                addRepository.getAllItems(itemIdPT),
                addRepository.getBrieflyItemAdd(itemIdPT)
            ) { addList, briefly ->
                addList to briefly
            }.collectLatest { (addList, briefly) ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = addList,
                        briefly = briefly,
                        isLoading = false
                    )
                }
            }
        }
    }


    fun getDetailsName(name: String): Flow<List<DomainAddTable>> {
        return addRepository.getBrieflyDetailsItemAdd(itemIdPT, name)
    }


    /*    private var _isLoading = MutableStateFlow(true)
        val isLoading: StateFlow<Boolean> = _isLoading

        val homeUiState: StateFlow<HomeUiState> =
            addRepository.getAllItems(itemId).map { HomeUiState(it) }
                .onStart {
                    // Устанавливаем состояние загрузки перед началом загрузки данных
                    _isLoading.value = true
                }.onEach {
                    // Отключаем состояние загрузки после завершения загрузки данных
                    _isLoading.value = false
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = HomeUiState()
                )

        val brieflyUiState: StateFlow<BrieflyUiState> =
            addRepository.getBrieflyItemAdd(itemId).map { BrieflyUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = BrieflyUiState()
                )
    */


}


sealed class AddListIntent() : BaseIntent
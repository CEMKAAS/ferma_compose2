package com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.dto.WriteOffUiState
import com.zaroslikov.data.room.dto.DataPairListState
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteOffViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val writeOffRepository: WriteOffRepository
) : ListViewModel<WriteOffListState, WriteOffListIntent>(WriteOffListState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[WriteOffDestination.itemIdArg])


    init {
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
                        listBriefly = briefly,
                        writeOffBoolean = titleList.isNotEmpty(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getDetailsName(name: String): Flow<List<DomainWriteOffTable>> {
        return writeOffRepository.getBrieflyDetailsItemWriteOff(itemIdPT.toLong(), name)
    }
}

sealed class WriteOffListIntent() : BaseIntent
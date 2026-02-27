package com.zaroslikov.fermacompose2.ui.incubator_project.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorMain
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.ui.incubator_project.journal.JournalDestination
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceIncubatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val incubatorTableRepository: IncubatorTableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<FinanceIncubatorState, FinanceIncubatorIntent>(
    FinanceIncubatorState()
) {
    private val itemIdPT: Long =
        checkNotNull(savedStateHandle[FinanceIncubatorDestination.itemIdPT])

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { state -> state.copy(isLoading = true) }
            val incubator = incubatorTableRepository.getIncubatorByIdPT(itemIdPT).first()
            combine(
                incubatorTableRepository.getIncubatorByIdPT(itemIdPT),
                bookmarkRepository.getFinanceIncubator(incubator.id),
                bookmarkRepository.getFinanceIncubatorList(incubator.id)
            ) { incubator, financeIncubatorMain, financeIncubatorList ->
                Triple(financeIncubatorMain, financeIncubatorList, incubator)
            }.collect { combine ->
                updateState { state ->
                    state.copy(
                        isLoading = false,
                        domainFinanceIncubatorMain = combine.first ?: DomainFinanceIncubatorMain(),
                        domainFinanceIncubatorHistory = combine.second,
                        priceSuffix = combine.third.currencySuffix
                    )
                }
            }
        }
    }


    fun onIntent(intent: FinanceIncubatorIntent) {
        when (intent) {
            else -> {}
        }
    }

}

sealed class FinanceIncubatorIntent() : BaseIntent
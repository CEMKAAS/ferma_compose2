package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository
) : ListViewModel<SaleListState, SaleListIntent>(SaleListState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[SaleDestination.itemIdArg])

    init {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val addList = saleRepository.getAllSaleItems(itemIdPT).first()
            val briefly = saleRepository.getBrieflyItemSale(itemIdPT).first()
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

    fun getDetailsName(name: String): Flow<List<DomainSaleTable>> {
        return saleRepository.getBrieflyDetailsItemSale(itemIdPT, name)
    }
}

sealed class SaleListIntent() : BaseIntent
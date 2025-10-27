package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.AddEntryState2

data class SaleListState(
    val textSearch: String = "",
    val isGroup: Boolean = false,
    val idPT: Long = 0,
    val openBottomSheetGroup: Boolean = false,
    val openBottomSheetEntry: Boolean = false,
    val currentBriefly: BrieflySaleDomain = BrieflySaleDomain(),
    val list: List<DomainSaleTable> = emptyList(),
    val briefly: List<BrieflySaleDomain> = emptyList(),
    val listBriefly: List<DomainAddTable> = emptyList(),
    override val isEntry: Boolean = false,
    override val currentProduct: AddEntryState2 = AddEntryState2(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
) : EntryNewState()

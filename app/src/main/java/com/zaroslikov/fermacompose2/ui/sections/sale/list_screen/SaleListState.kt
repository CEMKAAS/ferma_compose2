package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class SaleListState(
    val textSearch: String = "",
    val isGroup: Boolean = true,
    val idPT: Long = 0,
    val openBottomSheetGroup: Boolean = false,
    val openBottomSheetEntry: Boolean = false,
    val currentBriefly: BrieflySaleDomain = BrieflySaleDomain(),
    val list: List<DomainSaleTable> = emptyList(),
    val briefly: List<BrieflySaleDomain> = emptyList(),
    val listBriefly: List<DomainSaleTable> = emptyList(),
    override val isEntry: Boolean = false,
    override val currentProduct: SaleEntryState2 = SaleEntryState2(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
) : EntryNewState()

data class SaleEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val count: String = "",
    val countSuffix: Suffix = Suffix.PIECES,
    val date: String = dateToday(),
    val category: String = "",
    val isAutoPrice: Boolean = false,
    val price: String = "",
    val priceAll: String = "",
    val buyer: String = "",
    val selectedAnimalIndex: Long = 0,
    val animalCountId: Long? = null,
    val animalId: Long? = null,
    val animal: String = "",
    val note: String = "",
    val itemIdPT: Long = 0,
    val isIndicatorsValue: Boolean = false,
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val pickList: PickSaleList = PickSaleList(),
    val isEntry: Boolean = true,
    val error: ErrorSale = ErrorSale(),
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled =
            title.isNotBlank() && count.isNotBlank() && price.isNotBlank() && !hasAnyError
        return !isEnabled
    }
}

data class PickSaleList(
    val titleList: List<DomainTitleSuffixCategory> = emptyList(),
    val categoryList: List<String> = emptyList(),
    val buyerList: List<String> = emptyList(),
)

data class ErrorSale(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorPrice: Boolean = false
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorTitle || isErrorSlash || isErrorCount || isErrorPrice
}
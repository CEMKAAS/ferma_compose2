package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

enum class Transaction {
    INSERT, UPDATE, DELETE
}

enum class WarningAnimalCount {
    DELETE, DELETE_MINUS, DELETE_KILL, DELETE_MINUS_KILL, UPDATE_MINUS, UPDATE_KILL, UPDATE_MINUS_KILL
}

data class AnimalCountState(

    val openWarningDialog: Boolean = false,
    val openWarningDeleteDialog: Boolean = false,
    val statusWarningDialog: WarningAnimalCount = WarningAnimalCount.UPDATE_KILL,
    val textWarning: String = "",

    val openArchiveDialog: Boolean = false,
    val openSoloDialog: Boolean = false,
    val isOpenEntryBottomSheet: Boolean = false,

    val ignoreWarning: Boolean = false,
    val weight: DomainAnimalWeight? = null,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val oldCount: String = "",

    val countAnimal: String? = "",
    val countAnimalSuffix: Suffix = Suffix.PIECES,

    val countList: List<DomainAnimalCountPriceUi> = emptyList(),
    val isSaveStateForEntry: Boolean = false,
    val saveAnimalCountVersion: AnimalCountVersion? = null,
    val idPT: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val currentProduct: CountItem = CountItem(),
    val isArchive: Boolean = false
) : EntryNewState()

data class CountItem(
    val id: Long = 0,
    val count: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = dateToday(),
    val animalId: Long = 0,
    val note: String = "",
    val version: AnimalCountVersion = AnimalCountVersion.ADD,
    val price: String = "",
    val priceAll: String = "",
    val isAutoCalculate: Boolean = false,
    val buyer: String = "",
    val tableId: Long = 0,
    val itemIdPT: Long = 0,
    val isEntry: Boolean = true,
    val error: ErrorCount = ErrorCount(),
    val indexProductKill: Int = 0,
    val isOpenWeightDialog: Boolean = false,
    val currentProduct: ProductKill = ProductKill(),
    val productKillList: List<ProductKill> = emptyList(),
    val buyerList: List<String> = emptyList(),
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
    val oldCount: String = "",
    val totalWeight: Double? = null,
    val finalWeight: Double = 0.0,
    val weightSuffix: Suffix = Suffix.KILOGRAM,
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class ProductKill(
    val idProduct: Long = 0,
    val title: String = "",
    val countProduct: String = "",
    val suffixProduct: Suffix = Suffix.KILOGRAM,
    val countWarehouse: Double = 0.0,
    val suffixWarehouse: String = "",
    val warehouseList: List<DomainCountSuffix> = emptyList(),
    val error: ErrorKill = ErrorKill(),
    val isEntry: Boolean = true,
    val isVisibility: Boolean = true,
    val hasAnyError: Boolean = false
)

data class ErrorKill(
    val isError: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
)

data class ErrorCount(
    val isErrorPrice: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorCountZero: Boolean = false,
) : BaseError

data class DomainAnimalCountPriceUi(
    val id: Long = 0,
    val count: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = "",
    val animalId: Long = 0,
    val note: String = "",
    val version: AnimalCountVersion? = AnimalCountVersion.ADD, //TODO
    val price: Double? = null,
    val priceAll: Double? = null,
    val buyer: String? = null,
    val tableId: Long? = null,
    val idPT: Long? = null,
    val productKill: List<ProductKill> = emptyList()
)
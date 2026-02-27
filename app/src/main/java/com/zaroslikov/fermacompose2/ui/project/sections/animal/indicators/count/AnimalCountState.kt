package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import android.app.Dialog
import android.util.Log
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
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

data class AnimalCountState(
    val openWarningDialog: Boolean = false,
    val openWarningDeleteDialog: Boolean = false,
    val openWarningDeleteAllDialog: WarningAnimalCount = WarningAnimalCount.MINUS,
    val openArchiveDialog: Boolean = false,
    val openSoloDialog: Boolean = false,
    val isOpenDialog: Boolean = false,

    val ignoreWarning: Boolean = false,
    val weight: DomainAnimalWeight? = null,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val oldCount: String = "",
    val currentAnimal: DomainAnimalCount = DomainAnimalCount(),

    val countList: List<DomainAnimalCountPriceUi> = emptyList(),

    val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val currentProduct: CountItem = CountItem(),
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
    val idPT: Long = 0,
    val isEntry: Boolean = false,
    val error: ErrorCount = ErrorCount(),
    val indexProductKill: Int = 0,
    val isOpenWeightDialog: Boolean = false,
    val currentProduct: ProductKill = ProductKill(),
    val productKillList: List<ProductKill> = emptyList(),
    val buyerList: List<String> = emptyList(),
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = !error.hasAnyError(version)

    fun enabledButton(): Boolean {
        val isEnabled = when (version) {
            AnimalCountVersion.SALE, AnimalCountVersion.EXPENSES ->
                count.isNotBlank() && price.isNotBlank() &&/* isErrorCountZero*/ hasAnyError

            AnimalCountVersion.KILL, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.ADD ->
                count.isNotBlank() && hasAnyError

            AnimalCountVersion.INCUBATOR -> TODO()
        }
        return isEnabled
    }
}


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
    val isVisibility: Boolean = true
) {
    val hasAnyError: Boolean
        get() = !error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled =
            title.isNotBlank() && countProduct.isNotBlank() && hasAnyError /* isErrorCountZero*/
        return isEnabled
    }
}

data class ErrorKill(
    val isError: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorCount: Boolean = false,
) {
    val hasAnyError: Boolean
        get() = isError || isErrorSlash || isErrorCount
}

data class ErrorCount(
    val isErrorPrice: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorCountZero: Boolean = false,
) : BaseError {
    fun hasAnyError(animalCountVersion: AnimalCountVersion): Boolean {
        Log.i(
            "count23",
            "hasAnyError: isErrorPrice: ${isErrorPrice}, isErrorCount: $isErrorCount, isErrorCountZero: $isErrorCountZero"
        )
        return when (animalCountVersion) {
            AnimalCountVersion.SALE, AnimalCountVersion.EXPENSES ->
                isErrorPrice || isErrorCount || isErrorCountZero

            AnimalCountVersion.KILL, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.ADD ->
                isErrorCount || isErrorCountZero

            else -> TODO()
        }
    }
}

data class DomainAnimalCountPriceUi(
    val id: Long = 0,
    val count: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val date: String = "",
    val animalId: Long = 0,
    val note: String = "",
    val version: AnimalCountVersion? = AnimalCountVersion.ADD,
    val price: Double? = null,
    val priceAll: Double? = null,
    val buyer: String? = null,
    val tableId: Long? = null,
    val idPT: Long? = null,
    val productKill: List<ProductKill> = emptyList()
)
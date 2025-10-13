package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.opengl.Visibility
import android.util.Log
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalCountState(
    val openWarningDialog: Boolean = false,
    val openWarningDeleteDialog: Boolean = false,
    val openWarningDeleteAllDialog: WarningAnimalCount = WarningAnimalCount.MINUS,
    val openArchiveDialog: Boolean = false,
    val openSoloDialog: Boolean = false,
    val ignoreWarning: Boolean = false,
    val weight: DomainAnimalWeight? = null,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val oldCount: String = "",
    val currentAnimal: DomainAnimalCount = DomainAnimalCount(),
    val domainAnimalCountPrice: DomainAnimalCountPrice = DomainAnimalCountPrice(date = dateToday()),
    val countList: List<DomainAnimalCountPrice> = emptyList(),
    val buyerList: List<String> = emptyList(),
    val productKill: List<ProductKill> = listOf(ProductKill()),
    val titleList: List<TitleAndSuffixDomain> = emptyList(),
    val price: String = "",
    val priceAll: String = "",
    val isAutoPrice: Boolean = false,
    val isOpenDialog: Boolean = false,
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val isEntry: Boolean = false,
    val error: Error = Error(),
) : ListState() {
    val hasAnyError: Boolean
        get() = !error.hasAnyError(domainAnimalCountPrice.version)

    val hasFieldError: Boolean
        get() = !productKill.filter { it.isVisibility == true }.any { it.hasError }

    data class Error(
        val isErrorPrice: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorCountMore: Boolean = false,
        val isErrorCountZero: Boolean = false,
        val isErrorCountDifference: Boolean = false,
    ) : BaseError {
        fun hasAnyError(animalCountVersion: AnimalCountVersion?): Boolean {
            Log.i(
                "count23",
                "hasAnyError: isErrorPrice: ${isErrorPrice}, isErrorCount: $isErrorCount, isErrorCountMore: $isErrorCountMore, isErrorCountZero: $isErrorCountZero, isErrorCountDifference: $isErrorCountDifference"
            )
            return when (animalCountVersion) {
                AnimalCountVersion.SALE -> isErrorPrice || isErrorCount || isErrorCountMore || isErrorCountZero
                AnimalCountVersion.EXPENSES -> isErrorPrice || isErrorCount || isErrorCountMore || isErrorCountZero
                AnimalCountVersion.KILL -> isErrorCount || isErrorCountMore || isErrorCountZero
                AnimalCountVersion.WRITE_OFF -> isErrorCount || isErrorCountMore || isErrorCountZero || isErrorCountDifference
                AnimalCountVersion.ADD -> isErrorCount || isErrorCountDifference
                AnimalCountVersion.INCUBATOR -> TODO()
                null -> TODO()
            }
        }
    }

    data class ProductKill(
        val idProduct: Long = 0,
        val title: String = "",
        val countProduct: String = "",
        val suffixProduct: Suffix = Suffix.PIECES,
        val countWarehouse: Double = 0.0,
        val suffixWarehouse: String = "",
        val warehouseList: List<DomainCountSuffix> = emptyList(),
        val error: Error = Error(),
        val isVisibility: Boolean = true
    ) {
        val hasError: Boolean
            get() = error.isError || error.isErrorSlash || error.isErrorCount

        data class Error(
            val isError: Boolean = false,
            val isErrorSlash: Boolean = false,
            val isErrorCount: Boolean = false,
        )
    }
}
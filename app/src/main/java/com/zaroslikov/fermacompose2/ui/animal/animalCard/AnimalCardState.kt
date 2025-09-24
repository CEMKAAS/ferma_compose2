package com.zaroslikov.fermacompose2.ui.animal.animalCard

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalCardState(
    val openSaleDialog: Boolean = false,
    val openKillDialog: Boolean = false,
    val openAddDialog: Boolean = false,
    val openWriteOffDialog: Boolean = false,
    val openArchiveDialog: Boolean = false,
    val openSoloDialog: Boolean = false,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val weight: DomainAnimalWeight? = DomainAnimalWeight(),
    val size: DomainAnimalSize? = DomainAnimalSize(),
    val countAnimal: DomainAnimalCount = DomainAnimalCount(),
    val vaccination: DomainAnimalVaccination? = DomainAnimalVaccination(),
    val age: String = "",
    val price: Double? = 0.0,
    val buyerList: List<String> = emptyList(),
    val actionAnimal: CountAnimal = CountAnimal(),
    val itemIdPT: Long = 0,
    val itemId: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState {

    data class CountAnimal(
        val countAnimal: String = "",
        val suffixAnimal: Suffix = Suffix.PIECES,
        val isAutoPrice: Boolean = false,
        val price: String = "",
        val priceAll: String = "",
        val buyer: String = "",
        val note: String = "",
        val actionWithAnimal: ActionWithAnimal = ActionWithAnimal.ADD_ANIMAL,
        val productKill: List<ProductKill> = listOf(ProductKill()),
        val titleList: List<TitleAndSuffixDomain> = emptyList(),
        val error: Error = Error(),
    ) {
        val hasAnyError: Boolean
            get() = !error.hasAnyError(actionWithAnimal)

        val hasFieldError: Boolean
            get() = !productKill.any { it.hasError }

        data class Error(
            val isErrorPrice: Boolean = false,
            val isErrorCount: Boolean = false,
            val isErrorCountMore: Boolean = false,
            val isErrorCountZero: Boolean = false
        ) {
            fun hasAnyError(actionWithAnimal: ActionWithAnimal): Boolean {
                return when (actionWithAnimal) {
                    ActionWithAnimal.ADD_ANIMAL -> isErrorCount
                    ActionWithAnimal.SALE_ANIMAL -> isErrorPrice || isErrorCount || isErrorCountMore || isErrorCountZero
                    ActionWithAnimal.WRITE_OFF_ANIMAL -> isErrorCount || isErrorCountMore || isErrorCountZero
                    ActionWithAnimal.KILL_ANIMAL -> isErrorCount || isErrorCountMore || isErrorCountZero
                }
            }
        }

        data class ProductKill(
            val title: String = "",
            val countProduct: String = "",
            val suffixProduct: Suffix = Suffix.PIECES,
            val countWarehouse: Double = 0.0,
            val suffixWarehouse: String = "",
            val warehouseList: List<DomainCountSuffix> = emptyList(),
            val error: Error = Error()
        ) {
            val hasError: Boolean
                get() = error.isError || error.isErrorSlash || error.isErrorCount

            data class Error(
                val isError: Boolean = false,
                val isErrorSlash: Boolean = false,
                val isErrorCount: Boolean = false,
            )
        }

        fun reset(
            titleList: List<TitleAndSuffixDomain> = emptyList(),
            suffixProduct: Suffix = Suffix.PIECES
        ): CountAnimal {
            return this.copy(
                countAnimal = this.countAnimal,
                suffixAnimal = this.suffixAnimal,
                isAutoPrice = false,
                price = "",
                priceAll = "",
                buyer = "",
                note = "",
                titleList = titleList,
                productKill = listOf(ProductKill(suffixProduct = suffixProduct)),
                error = Error()
            )
        }
    }
}

enum class ActionWithAnimal {
    ADD_ANIMAL, SALE_ANIMAL, WRITE_OFF_ANIMAL, KILL_ANIMAL
}

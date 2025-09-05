package com.zaroslikov.fermacompose2.ui.animal.animalCard

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalCard
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalCardState(
    val openSaleDialog: Boolean = false,
    val openKillDialog: Boolean = false,
    val openAddDialog: Boolean = false,
    val openWriteOffDialog: Boolean = false,
    val openArchiveDialog: Boolean = false,
    val openSoloDialog: Boolean = false,
    val animal: DomainAnimalTable = DomainAnimalTable(),
    val weight: DomainAnimalWeight = DomainAnimalWeight(),
    val size: DomainAnimalSize = DomainAnimalSize(),
    val countAnimal: DomainAnimalCount = DomainAnimalCount(),
    val vaccination: DomainAnimalVaccination = DomainAnimalVaccination(),
    val age: String = "",
    val price: Double? = 0.0,
    val addAnimal: AddAnimal = AddAnimal(),
    val saleAnimal: SaleAnimal = SaleAnimal(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState {
    data class AddAnimal(
        val count: String = "",
        val countSuffix: String = "",
        val isAutoPrice: Boolean = false,
        val price: String = "",
        val priceAll: String = "",
        val note: String = "",
        val isErrorCount: Boolean = false
    )

    data class SaleAnimal(
        val countAnimal: String = " ",
        val countSuffix: String = "",
        val price: String = "",
        val priceAll: String = "",
        val isAutoPrice: Boolean = false,
        val buyer: String = "",
        val error: Error = Error()
    ) {
        val hasAnyError: Boolean
            get() = error.hasAnyError

        data class Error(
            val isErrorPrice: Boolean = false,
            val isErrorCount: Boolean = false,
            val isErrorCountMore: Boolean = false,
            val isErrorCountZero: Boolean = false
        ) {
            val hasAnyError: Boolean
                get() = isErrorPrice || isErrorCountMore || isErrorCount || isErrorCountZero
        }
    }

}


/*fun AnimalCardState.saveAddAnimal() {
    if (isErrorAddAnimal(count = countAnimal, isErrorCount = { isErrorCount = it })
    ) {
        val count =
            (countAnimal.toConvertOnlyInt().toInt() + countAll.toInt()).toString()
                .toConvertOnlyInt()
        onSaveClick(
            Pair(
                first = DomainIndicatorsVM(
//                                    weight = count,
                    weight = countAnimal,
                    suffix = countSuffix,
                    date = dateToday(),
                    idAnimal = idPT,
                    note = reasonNote,
                    version = if (priceInDB.isBlank() || priceInDB == "0") 4 else 1
                ),
                second = if (priceInDB.isBlank() || priceInDB == "0") null else {
                    ExpensesTable(
                        title = title,
                        count = countAnimal.toConvertDbDouble(),
                        day = dateTodayArray()[0],
                        mount = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        price = priceInDB.toConvertDbDouble(),
                        countSuffix = countSuffix,
                        category = category,
                        note = reasonNote,
                        isShowFood = false,
                        isShowWarehouse = false,
                        isShowAnimals = false,
                        isShowFoodHand = false,
                        feedFood = 0.0,
                        countAnimal = 0,
                        foodDesignedDay = 0,
                        lastDayFood = "",
                        idPT = idPT.toLong(),
                        priceAll = 0.0,
                        feedFoodSuffix = "",
                        weight = 0.0,
                        weightSuffix = "",
                    )
                }
            )
        )
    }
}*/
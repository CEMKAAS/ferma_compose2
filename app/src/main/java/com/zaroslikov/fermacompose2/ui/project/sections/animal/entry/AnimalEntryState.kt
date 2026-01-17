package com.zaroslikov.fermacompose2.ui.project.sections.animal.entry

import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber


data class AnimalEntryState(
    val title: String = "",
    val type: String = "",
    val sex: Boolean = true,
    val isAnimalGroup: Boolean = false, // true group
    val count: String = "",

    val countSuffix: Suffix = Suffix.PIECES,
    val isAutoPrice: Boolean = false,

    val price: String = "",
    val priceAll: String = "",
    val isDateFactory: Boolean = true,

    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),
    val foodDay: String = "",

    val foodDaySuffix: Suffix = Suffix.GRAM,
    val note: String = "",

    val category: String = "",

    val typeList: List<String> = emptyList(),
    val archive: Boolean = false,
    override val error: Error = Error(),
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null

) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError(isAnimalGroup, isEntry)

    data class Error(
        val isErrorTitle: Boolean = false,
        val isErrorSlash: Boolean = false,
        val isErrorType: Boolean = false,
        val isErrorCount: Boolean = false,
    ) : BaseError {
        fun hasAnyError(isAnimalGroup: Boolean, isEntry: Boolean): Boolean {
            return when {
                isAnimalGroup && isEntry -> isErrorTitle || isErrorCount || isErrorType
                else -> isErrorTitle || isErrorType
            }
        }
    }
}

fun AnimalEntryState.updateFromDomain(domain: DomainAnimalTable): AnimalEntryState {
    return copy(
        title = domain.name,
        type = domain.type,
        isDateFactory = domain.dateFactory == null,
        dateBorn = domain.date,
        dateFactory = domain.dateFactory ?: domain.date,
        isAnimalGroup = domain.group,
        sex = domain.sex,
        note = domain.note,
        foodDay = domain.foodDay.formatNumber(),
        foodDaySuffix = domain.foodDaySuffix
    )
}

fun AnimalEntryState.saveAnimal(
    id: Long = 0,
    itemIdPT: Long
): DomainAnimalTable {
    return DomainAnimalTable(
        id = id,
        name = title,
        type = type,
        date = dateBorn,
        dateFactory = if (isDateFactory) null else dateFactory,
        group = isAnimalGroup,
        sex = sex,
        note = note,
        image = null,
        archive = archive,
        foodDay = if (foodDay.isBlank()) 0.0 else foodDay.toConvertDbDouble(),
        foodDaySuffix = foodDaySuffix,
        idPT = itemIdPT
    )
}

fun AnimalEntryState.updateForSave(
    idAnimal: Long,
    itemIdPT: Long
): Pair<DomainAnimalCount, DomainExpensesTable?> {
    val dateFactory2 = if (isDateFactory) dateBorn else dateFactory
    val dateList = dateFactory2.split(".")
    val countAnimal = if (count == "") "1" else count

    return DomainAnimalCount(
        id = 0,
        count = countAnimal,
        suffix = countSuffix,
        date = if (!isDateFactory) dateFactory else dateBorn,
        idAnimal = idAnimal,
        note = "",
        version = AnimalCountVersion.ADD
    ) to
            if (price.isNotBlank())
                DomainExpensesTable(
                    id = 0,
                    title = title,
                    count = count.toConvertZeroDouble(),
                    day = dateList[0].toInt(),
                    month = dateList[0].toInt(),
                    year = dateList[0].toInt(),
                    price = price.toConvertDbDouble(),
                    priceAll = if (isAutoPrice && isAnimalGroup) priceAll.toConvertDbDouble() else null,
                    countSuffix = countSuffix,
                    category = category,
                    note = "",
                    isShowFood = false,
                    isShowFoodHand = false,
                    isShowWarehouse = false,
                    isShowAnimals = false,
                    feedFood = null,
                    feedFoodSuffix = null,
                    countAnimal = countAnimal.toInt(),
                    foodDesignedDay = null,
                    lastDayFood = null,
                    weight = null,
                    weightSuffix = null,
                    idPT = itemIdPT,
                    animalId = idAnimal,
                    animalVaccinationId = null,
                    animalCountId = null,
                ) else null
}
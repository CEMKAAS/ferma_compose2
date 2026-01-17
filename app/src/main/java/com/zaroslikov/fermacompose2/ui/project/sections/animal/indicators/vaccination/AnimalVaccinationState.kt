package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination

import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayNextYear
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalVaccinationState(
    val vaccinationList: List<AnimalVaccinationExpensesDomain> = emptyList(),
    val titleVaccinationList: List<String> = emptyList(),
    val idPT: Long = 0,
    val isOpenDialog: Boolean = false,
    val isAnimalGroup: Boolean = false,
    val countAnimalAll: String = "",
    val suffixAnimal: Suffix = Suffix.PIECES,
    override val isEntry: Boolean = false,
    override val currentProduct: Vaccination = Vaccination(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : EntryNewState()

data class Vaccination(
    val id: Long = 0,
    val vaccination: String = "",
    val countVaccination: String = "",
    val date: String = dateToday(),
    val nextDate: String = dateTodayNextYear(),
    val note: String = "",
    val idAnimal: Long = 0,
    //Expenses
    val price: String = "",
    val priceAll: String = "",
    val isAutoCalculate: Boolean = false,
    val isDateFactory: Boolean = true,
    val isEntry: Boolean = true,
    val idExpenses: Long? = null,
    val error: ErrorVaccination = ErrorVaccination()
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled =
            vaccination.isNotBlank() && countVaccination.isNotBlank() && !hasAnyError
        return !isEnabled
    }
}

data class ErrorVaccination(
    val isErrorVaccination: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorCountZero: Boolean = false
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorVaccination || isErrorCount || isErrorCountZero
}
package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination

import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayNextYear
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import okhttp3.internal.http2.Settings

data class AnimalVaccinationState(
    val vaccinationList: List<AnimalVaccinationExpensesDomain> = emptyList(),
    val deleteVaccination: AnimalVaccinationExpensesDomain? = null,
    val titleVaccinationList: List<String> = emptyList(),
    val settings: DomainSettings = DomainSettings(),
    val idPT: Long = 0,
    val isOpenDialog: Boolean = false,
    val isOpenBottomSheetDelete: Boolean = false,
    val isSaveStateForBottomSheet: Boolean = false,
    override val isEntry: Boolean = false,
    override val currentProduct: Vaccination = Vaccination(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false
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

    //Animal
    val isAnimalGroup: Boolean = false,
    val countAnimalAll: String? = "",
    val animalSuffix: Suffix = Suffix.PIECES,
    val priceSuffix: Suffix = Suffix.RUBLE,

    val error: ErrorVaccination = ErrorVaccination(),
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class ErrorVaccination(
    val isErrorVaccination: Boolean = false,
    val isErrorCount: Boolean = false,
    val isErrorCountZero: Boolean = false
) : BaseError
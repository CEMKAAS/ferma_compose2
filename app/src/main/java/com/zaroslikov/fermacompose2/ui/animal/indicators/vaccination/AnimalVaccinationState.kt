package com.zaroslikov.fermacompose2.ui.animal.indicators.vaccination

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalVaccinationState(
    val vaccination: Vaccination = Vaccination(),
    val vaccinationList: List<DomainAnimalVaccination> = emptyList(),
    val titleVaccinationList: List<String> = emptyList(),
    val idPT: Long = 0,
    val isOpenDialog: Boolean = false,
    val isAnimalGroup: Boolean = false,
    val countAnimalAll: String = "",
    val suffixAnimal: Suffix = Suffix.PIECES,
    override val isEntry: Boolean = false,
    override val error: Error = Error(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError(isAnimalGroup)

    data class Vaccination(
        val domainAnimalVaccination: DomainAnimalVaccination = DomainAnimalVaccination(),
        val price: String = "",
        val priceAll: String = "",
        val isAutoCalculate: Boolean = false,
        val countAnimal: String = "",
        val error: Error = Error()
    )

    data class Error(
        val isErrorVaccination: Boolean = false,
        val isErrorCount: Boolean = false,
        val isErrorCountZero: Boolean = false
    ) : BaseError {
        fun hasAnyError(isAnimalGroup: Boolean): Boolean {
            return when {
                isAnimalGroup -> isErrorVaccination || isErrorCount || isErrorCountZero
                else -> isErrorVaccination
            }
        }
    }
}

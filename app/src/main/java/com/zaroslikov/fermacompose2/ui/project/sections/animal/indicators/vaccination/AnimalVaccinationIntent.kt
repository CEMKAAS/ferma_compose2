package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination

import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent

sealed class AnimalVaccinationIntent : BaseIntent {
    data class OpenEntryBottomSheetByItem(
        val isOpen: Boolean,
        val domainAnimalVaccination: AnimalVaccinationExpensesDomain? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalVaccinationIntent()


    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: Vaccination,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalVaccinationIntent()

    data class OpenBottomSheetDelete(val value: Long? = null) : AnimalVaccinationIntent()

    data class VaccinationChanged(val value: String) : AnimalVaccinationIntent()
    data class CountChanged(val value: String) : AnimalVaccinationIntent()
    data class PriceChanged(val value: String) : AnimalVaccinationIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalVaccinationIntent()
    data class DateClicked(val value: String) : AnimalVaccinationIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalVaccinationIntent()
    data class DateNextClicked(val value: String) : AnimalVaccinationIntent()
    data class NoteChanged(val value: String) : AnimalVaccinationIntent()
    data object InsertPressed : AnimalVaccinationIntent()
    data object UpdatePressed : AnimalVaccinationIntent()
    data object DeletePressed : AnimalVaccinationIntent()
}

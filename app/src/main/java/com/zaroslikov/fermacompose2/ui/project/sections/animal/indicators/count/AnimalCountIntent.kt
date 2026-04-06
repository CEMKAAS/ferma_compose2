package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AnimalCountIntent : BaseIntent {

    // Animal Card
    data class OpenSoloDialogClicked(val value: Boolean) : AnimalCountIntent()
    data class SexClicked(val value: Boolean) : AnimalCountIntent()
    data object SaveGroupPressed : AnimalCountIntent()

    // Open Bottom Sheet Animal
    data class DialogClicked(
        val isOpen: Boolean,
        val version: AnimalCountVersion = AnimalCountVersion.ADD,
        val item: DomainAnimalCountPriceUi? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : AnimalCountIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: CountItem,
        val isSaveStateForBottomSheet: Boolean = false,
        val saveAnimalCountVersion: AnimalCountVersion? = null
    ) :
        AnimalCountIntent()

    // Warning Alert Dialog
    data class WarningEndDialogClicked(val wait: Boolean) : AnimalCountIntent()

    // Update Action Animal
    data class CountChanged(val value: String) : AnimalCountIntent()
    data class DateClicked(val value: String) : AnimalCountIntent()
    data class NoteChanged(val value: String) : AnimalCountIntent()
    data class PriceChanged(val value: String) : AnimalCountIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalCountIntent()

    //Insert Update Delete
    data class DeleteCountPressed(val value: Long) : AnimalCountIntent()

    // Add Count Animal
    data object InsertAddPressed : AnimalCountIntent()
    data object UpdateAddPressed : AnimalCountIntent()

    // Sale Count Animal
    data class BuyerSaleChanged(val value: String) : AnimalCountIntent()
    data object InsertSalePressed : AnimalCountIntent()
    data object UpdateSalePressed : AnimalCountIntent()

    // WriteOff Count Animal
    data object InsertWriteOffPressed : AnimalCountIntent()
    data object UpdateWriteOffPressed : AnimalCountIntent()

    // Expenses Count Animal
    data object InsertExpensesPressed : AnimalCountIntent()
    data object UpdateExpensesPressed : AnimalCountIntent()

    // Kill Count Animal
    data class OpenWeightAlertDialogClicked(val value: Boolean) : AnimalCountIntent()
    data class WeightChanged(val value: Double) : AnimalCountIntent()
    data class TitleProductKillChanged(val value: String) : AnimalCountIntent()
    data class TitleAndSuffixKillClicked(val pair: Pair<String, Suffix>) :
        AnimalCountIntent()

    data class CountProductKillChanged(val value: String) : AnimalCountIntent()
    data class SuffixProductKillChanged(val value: Suffix) : AnimalCountIntent()
    data object AddProductKillChanged : AnimalCountIntent()
    data class ChoiceProductKillChanged(val index: Int) : AnimalCountIntent()
    data object CancelProductKillChanged : AnimalCountIntent()
    data object EditProductKillChanged : AnimalCountIntent()
    data class RemoveProductKillChanged(val index: Int) : AnimalCountIntent()
    data object InsertKillChanged : AnimalCountIntent()
    data object EditKillChanged : AnimalCountIntent()
}
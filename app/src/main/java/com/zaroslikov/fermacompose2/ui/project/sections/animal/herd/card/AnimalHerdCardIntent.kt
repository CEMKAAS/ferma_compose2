package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdUi
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem

sealed class AnimalHerdCardIntent : BaseIntent {
    data class Loaded(
        val herd: AnimalHerdUi,
        val isArchive: Boolean,
        val weight: HerdWeight?,
        val products: List<BrieflyItem>,
        val productsByMember: Map<String, List<HerdMemberProduct>>
    ) : AnimalHerdCardIntent()
    data class ProductSelected(val title: String) : AnimalHerdCardIntent()
    data class NoteChanged(val value: String) : AnimalHerdCardIntent()

    // Действия с группой
    data class OpenDepersonalizeDialog(val value: Boolean) : AnimalHerdCardIntent()
    data object DepersonalizeConfirmed : AnimalHerdCardIntent()
    data class OpenDisbandDialog(val value: Boolean) : AnimalHerdCardIntent()
    data object DisbandConfirmed : AnimalHerdCardIntent()
    data class OperationRunningChanged(val value: Boolean) : AnimalHerdCardIntent()
    data object Closed : AnimalHerdCardIntent()
}

package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy.StartFormEvent

sealed class AnimalCardIntent : BaseIntent {
    data class NoteChanged(val value: String) : AnimalCardIntent()
    data class OpenArchiveDialogClicked(val value: Boolean) : AnimalCardIntent()
    data object ArchiveAnimalPressed : AnimalCardIntent()

    // Архивация беременной самки: прервать беременность или только убрать в архив
    data class OpenArchivePregnancyDialog(val value: Boolean) : AnimalCardIntent()
    data class ArchivePregnancyConfirmed(val interrupt: Boolean) : AnimalCardIntent()

    // Случка прямо из карточки
    data object OpenPregnancyStart : AnimalCardIntent()
    data class PregnancyStartEvent(val event: StartFormEvent) : AnimalCardIntent()
    data class PregnancySavingChanged(val value: Boolean) : AnimalCardIntent()

    // Лист «Мать»/«Отец»/«Потомок» в родословной; null — закрыть
    data class OpenPedigreeSheet(val sheet: PedigreeSheet?) : AnimalCardIntent()
    data class PedigreeSheetLoaded(val animal: DomainAnimalTable?, val childrenCount: Int) : AnimalCardIntent()

    // Обезличенная группа: выделить особей из счётчика (они попадут в её сборную группу)
    data class OpenSplitDialog(val value: Boolean) : AnimalCardIntent()
    data class SplitCountChanged(val value: String) : AnimalCardIntent()
    data class SplitPrefixChanged(val value: String) : AnimalCardIntent()
    data class SplitSexChanged(val value: Boolean) : AnimalCardIntent()
    data object SplitConfirmed : AnimalCardIntent()

    // Член сборной группы: вывести из неё
    data class OpenLeaveDialog(val value: Boolean) : AnimalCardIntent()
    data object LeaveConfirmed : AnimalCardIntent()
}

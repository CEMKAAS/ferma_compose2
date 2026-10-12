package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.LitterMode
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import java.time.LocalDate

sealed class AnimalPregnancyIntent : BaseIntent {
    // Случка / правка записи
    data class OpenStart(val value: Boolean) : AnimalPregnancyIntent()
    data class OpenEdit(val id: Long) : AnimalPregnancyIntent()
    /** Правка полей листа случки (дата, отец, заметка). */
    data class StartFormChanged(val event: StartFormEvent) : AnimalPregnancyIntent()
    data object StartConfirmed : AnimalPregnancyIntent()

    // Лист «Отец»
    data class OpenSireSheet(val value: Boolean) : AnimalPregnancyIntent()
    data class SireDetailsLoaded(val animal: DomainAnimalTable?, val childrenCount: Int) : AnimalPregnancyIntent()

    // Роды
    data class OpenBirth(val value: Boolean) : AnimalPregnancyIntent()
    data class BirthDateChanged(val value: String) : AnimalPregnancyIntent()
    data class FemaleChanged(val value: String) : AnimalPregnancyIntent()
    data class MaleChanged(val value: String) : AnimalPregnancyIntent()
    data class StillbornChanged(val value: String) : AnimalPregnancyIntent()
    data class LitterModeChanged(val value: LitterMode) : AnimalPregnancyIntent()
    data class PrefixChanged(val value: String) : AnimalPregnancyIntent()
    data class GroupNameChanged(val value: String) : AnimalPregnancyIntent()
    data class BirthNoteChanged(val value: String) : AnimalPregnancyIntent()
    data object BirthConfirmed : AnimalPregnancyIntent()

    // Прерывание
    data class OpenInterrupt(val value: Boolean) : AnimalPregnancyIntent()
    data class InterruptDateChanged(val value: String) : AnimalPregnancyIntent()
    data class ReasonChanged(val value: PregnancyInterruptReason) : AnimalPregnancyIntent()
    data class InterruptNoteChanged(val value: String) : AnimalPregnancyIntent()
    data object InterruptConfirmed : AnimalPregnancyIntent()

    // Заметка к записи истории
    data class OpenEditNote(val id: Long?) : AnimalPregnancyIntent()
    data class EditNoteChanged(val value: String) : AnimalPregnancyIntent()
    data object EditNoteConfirmed : AnimalPregnancyIntent()

    // Заметка за день (FAB — новая к активной беременности; клик по заметке — правка)
    data class OpenDayNote(val value: Boolean) : AnimalPregnancyIntent()
    data class OpenEditDayNote(val id: Long) : AnimalPregnancyIntent()
    data class DayNoteDateChanged(val value: String) : AnimalPregnancyIntent()
    /** Пустая строка — время сброшено. */
    data class DayNoteTimeChanged(val value: String) : AnimalPregnancyIntent()
    data class DayNoteTextChanged(val value: String) : AnimalPregnancyIntent()
    data object DayNoteConfirmed : AnimalPregnancyIntent()
    data class OpenDayNoteDelete(val value: Boolean) : AnimalPregnancyIntent()
    data object DayNoteDeleted : AnimalPregnancyIntent()

    // Удаление
    data class OpenDelete(val id: Long?) : AnimalPregnancyIntent()
    data object DeleteConfirmed : AnimalPregnancyIntent()

    // Внутренние
    data class RefreshData(
        val animal: DomainAnimalTable,
        val pregnancies: List<DomainAnimalPregnancy>,
        val dayNotes: List<DomainAnimalPregnancyNote>,
        val projectAnimalNames: List<String>,
        val sireCandidates: List<DomainAnimalTable>,
        val isProjectArchive: Boolean,
        val today: LocalDate
    ) : AnimalPregnancyIntent()

    data class SavingChanged(val value: Boolean) : AnimalPregnancyIntent()
}

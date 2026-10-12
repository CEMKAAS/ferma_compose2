package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyDates
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyRules
import java.time.LocalDate

/**
 * Правила формы случки — общие для экрана беременности и карточки животного,
 * чтобы оба места проверяли и сохраняли одинаково.
 */

/** События листа случки; каждый экран переводит их в свои интенты. */
sealed interface StartFormEvent {
    data class MatingDateChanged(val value: String) : StartFormEvent
    data class SireModeChanged(val value: SireMode) : StartFormEvent
    data class SireChosen(val id: Long) : StartFormEvent
    data class SireNameChanged(val value: String) : StartFormEvent
    data class NoteChanged(val value: String) : StartFormEvent
    data object Confirm : StartFormEvent
    data object Dismiss : StartFormEvent
    data class Delete(val id: Long) : StartFormEvent
}

/** Новая случка: сегодняшняя дата, отец не указан. */
fun newStartForm(today: LocalDate): StartForm = StartForm(
    isOpen = true,
    matingDate = PregnancyDates.format(today),
    sireMode = SireMode.NONE
)

/** Поля формы меняются одинаково на обоих экранах; Confirm/Dismiss/Delete — забота экрана. */
fun StartForm.reduce(event: StartFormEvent): StartForm = when (event) {
    is StartFormEvent.MatingDateChanged -> copy(matingDate = event.value)
    is StartFormEvent.SireModeChanged -> copy(sireMode = event.value)
    is StartFormEvent.SireChosen -> copy(sireId = event.id)
    is StartFormEvent.SireNameChanged -> copy(sireName = event.value)
    is StartFormEvent.NoteChanged -> copy(note = event.value)
    StartFormEvent.Confirm, StartFormEvent.Dismiss, is StartFormEvent.Delete -> this
}

fun StartForm.matingDateError(animal: DomainAnimalTable, today: LocalDate): PregnancyRules.DateError? {
    val mating = PregnancyDates.parseOrNull(matingDate) ?: return PregnancyRules.DateError.INVALID
    return PregnancyRules.matingDateError(mating, PregnancyDates.parseOrNull(animal.date), today)
}

/**
 * [editingSireId] — самец редактируемой записи: допустим, даже если выпал из кандидатов
 * (перевели в группу, сменили пол/вид).
 */
fun StartForm.isSireError(candidates: List<DomainAnimalTable>, editingSireId: Long? = null): Boolean =
    when (sireMode) {
        SireMode.FARM -> sireId == null || (candidates.none { it.id == sireId } && sireId != editingSireId)
        SireMode.EXTERNAL -> sireName.isBlank()
        SireMode.NONE -> false
    }

/** Отец для сохранения: (id, имя). null — выбранный самец больше недоступен. */
fun StartForm.resolveSire(
    candidates: List<DomainAnimalTable>,
    editingSireId: Long? = null,
    editingSireName: String? = null
): Pair<Long?, String?>? = when (sireMode) {
    SireMode.FARM -> candidates.firstOrNull { it.id == sireId }?.let { it.id to it.name }
        ?: editingSireId?.takeIf { it == sireId }?.let { it to editingSireName }
    SireMode.EXTERNAL -> null to sireName.trim()
    SireMode.NONE -> null to null
}

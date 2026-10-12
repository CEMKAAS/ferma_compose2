package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.count

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdUi
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.CountItem
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListIntent

sealed class AnimalHerdCountIntent : BaseIntent {
    data class Loaded(
        val herd: AnimalHerdUi,
        val history: List<HerdHistoryItem>,
        val settings: DomainSettings,
        val isArchive: Boolean
    ) : AnimalHerdCountIntent()

    // Лист операции: поля — интенты счётчика животного
    /** [picks] — члены, с которыми открыт лист (отмеченные в составе при убыли). */
    data class EntryOpened(val item: CountItem, val picks: Map<Long, String> = emptyMap()) : AnimalHerdCountIntent()
    data object EntryClosed : AnimalHerdCountIntent()
    data class Entry(val intent: AnimalCountIntent) : AnimalHerdCountIntent()
    data class PickCountChanged(val id: Long, val value: String) : AnimalHerdCountIntent()

    // «Добавить»: форма добавления животного (интенты списка животных)
    data class AnimalEntry(val intent: AnimalListIntent) : AnimalHerdCountIntent()

    // «Добавить в группу»
    data class OpenAddMembersSheet(val value: Boolean) : AnimalHerdCountIntent()
    data class RefreshMergeState(val state: MergeGroupState) : AnimalHerdCountIntent()
    data class MergeCandidateToggled(val id: Long) : AnimalHerdCountIntent()
    data object MergeConfirmed : AnimalHerdCountIntent()

    // Отмеченные в составе: продажа, списание, забой, вывод из группы
    data class MemberSelected(val id: Long) : AnimalHerdCountIntent()
    data object SelectionCleared : AnimalHerdCountIntent()
    data class SelectionActionClicked(val version: AnimalCountVersion) : AnimalHerdCountIntent()
    data class PickRemoved(val id: Long) : AnimalHerdCountIntent()
    data class PickSexChanged(val id: Long, val females: String, val males: String) : AnimalHerdCountIntent()
    data class OpenLeaveDialog(val value: Boolean) : AnimalHerdCountIntent()
    data object LeaveConfirmed : AnimalHerdCountIntent()

    // Действия с группой
    data class OperationRunningChanged(val value: Boolean) : AnimalHerdCountIntent()
    data object Closed : AnimalHerdCountIntent()
}

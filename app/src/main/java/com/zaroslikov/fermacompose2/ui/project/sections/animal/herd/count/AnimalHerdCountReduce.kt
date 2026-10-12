package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.count

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountIntent
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountReduce
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.CountItem
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListReduce
import com.zaroslikov.fermacompose2.utils.ResourceProvider

class AnimalHerdCountReduce(resourceProvider: ResourceProvider) :
    BaseReducer<AnimalHerdCountState, AnimalHerdCountIntent>() {
    private val countReduce = AnimalCountReduce()
    private val animalEntryReduce = AnimalListReduce(resourceProvider)

    override fun reducer(state: AnimalHerdCountState, intent: AnimalHerdCountIntent): AnimalHerdCountState {
        return when (intent) {
            is AnimalHerdCountIntent.Loaded -> state.copy(
                herd = intent.herd,
                history = intent.history,
                settings = intent.settings,
                isArchive = intent.isArchive,
                isLoading = false,
                // Отмеченные и выбранные члены, которых больше нет в составе, убираем
                selected = state.selected.filterTo(mutableSetOf()) { id -> intent.herd.members.any { it.id == id } }
            ).withPicks(state.picks.filterKeys { id -> intent.herd.members.any { it.id == id } })

            is AnimalHerdCountIntent.EntryOpened -> state.copy(
                entry = countReduce.reducer(
                    state.entry,
                    AnimalCountIntent.RefreshEntryBottomSheetState(true, intent.item)
                ),
                pickSex = emptyMap()
            ).withPicks(intent.picks)

            AnimalHerdCountIntent.EntryClosed -> state.copy(
                entry = state.entry.copy(isOpenEntryBottomSheet = false, currentProduct = CountItem()),
                picks = emptyMap(),
                pickSex = emptyMap()
            )

            is AnimalHerdCountIntent.Entry -> state.copy(entry = countReduce.reducer(state.entry, intent.intent))

            is AnimalHerdCountIntent.PickCountChanged -> state.withPicks(
                // Строка остаётся и пустой (ошибка) — убирается только крестиком
                state.picks + (intent.id to intent.value)
            )

            is AnimalHerdCountIntent.AnimalEntry ->
                state.copy(animalEntry = animalEntryReduce.reducer(state.animalEntry, intent.intent))

            is AnimalHerdCountIntent.OpenAddMembersSheet ->
                if (intent.value) state else state.copy(merge = MergeGroupState())

            is AnimalHerdCountIntent.RefreshMergeState -> state.copy(merge = intent.state)
            is AnimalHerdCountIntent.MergeCandidateToggled -> state.copy(merge = state.merge.toggle(intent.id))

            is AnimalHerdCountIntent.MemberSelected -> state.copy(
                selected = if (intent.id in state.selected) state.selected - intent.id else state.selected + intent.id
            )

            AnimalHerdCountIntent.SelectionCleared -> state.copy(selected = emptySet())
            is AnimalHerdCountIntent.PickRemoved ->
                state.copy(pickSex = state.pickSex - intent.id).withPicks(state.picks - intent.id)

            is AnimalHerdCountIntent.PickSexChanged ->
                state.copy(pickSex = state.pickSex + (intent.id to (intent.females to intent.males)))
            is AnimalHerdCountIntent.OpenLeaveDialog -> state.copy(isOpenLeaveDialog = intent.value)
            is AnimalHerdCountIntent.OperationRunningChanged -> state.copy(isOperationRunning = intent.value)
            AnimalHerdCountIntent.Closed -> state.copy(isClosed = true)

            AnimalHerdCountIntent.MergeConfirmed,
            AnimalHerdCountIntent.LeaveConfirmed,
            is AnimalHerdCountIntent.SelectionActionClicked -> state
        }
    }

    /**
     * Новый выбор членов: количество в листе — их сумма. Через CountChanged счётчика животного,
     * чтобы пересчитались общая цена, проверка полей и вес забоя.
     */
    private fun AnimalHerdCountState.withPicks(picks: Map<Long, String>): AnimalHerdCountState {
        val withPicks = copy(picks = picks)
        if (!entry.isOpenEntryBottomSheet) return withPicks
        val total = withPicks.pickedTotal
        return withPicks.copy(
            entry = countReduce.reducer(
                entry,
                AnimalCountIntent.CountChanged(if (total > 0) total.toCountText() else "")
            )
        )
    }
}

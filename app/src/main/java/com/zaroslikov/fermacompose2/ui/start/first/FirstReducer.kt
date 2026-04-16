package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class FirstReducer : BaseReducer<FirstState, FirstIntent>() {
    override fun reducer(
        state: FirstState,
        intent: FirstIntent
    ): FirstState {
        return when (intent) {
            is FirstIntent.LoadingClicked -> state.copy(isLoading = intent.value)
            is FirstIntent.ArchiveModeClicked -> state.copy(isArchive = !state.isArchive)
            is FirstIntent.OpenArchiveIncubatorBottomSheetClicked ->
                state.updateOpenArchiveIncubatorBottomSheet(intent.value, intent.domainProjectTable)

            is FirstIntent.OpenDeleteBottomSheetClicked -> state.copy(
                isOpenDeleteBottomSheet = intent.value,
                currentProjectTable = intent.domainProjectTable
            )

            is FirstIntent.SkipTrainingClicked -> state.updateSkipTraining()

            else -> state
        }
    }

    private fun FirstState.updateOpenArchiveIncubatorBottomSheet(
        isOpenArchiveIncubatorBottomSheet: Boolean,
        domainProjectTable: DomainProjectTable?
    ): FirstState {
        return copy(
            isOpenArchiveIncubatorBottomSheet = isOpenArchiveIncubatorBottomSheet,
            currentProjectTable = if (isOpenArchiveIncubatorBottomSheet) domainProjectTable else null
        )
    }

    private fun FirstState.updateSkipTraining(): FirstState {
        return copy(
            appSettings = appSettings.copy(
                isFirstLaunch = false
            ),
            isNotificationAsked = true
        )
    }
}
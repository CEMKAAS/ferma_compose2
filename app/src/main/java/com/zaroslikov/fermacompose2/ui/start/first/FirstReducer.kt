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

            is FirstIntent.ShowDownloadingUpdate -> state.copy(isOpenDownloadingUpdate = intent.value)
            is FirstIntent.OpenWarningQrCodeClick -> state.copy(isOpenWaringQrCode = intent.value)
            is FirstIntent.OpenQrCodeScanner -> state.copy(isOpenQrScannerBottomSheet = intent.value)
            is FirstIntent.OpenMultiProjectBottomSheetClick ->
                state.updateOpenChoiceProjectForTemplate(intent.value, intent.projectList)

            is FirstIntent.IncubatorChicksLoaded ->
                state.copy(incubatorChicks = intent.chicks, chicksProjects = intent.projects)

            // Любой ответ закрывает шторку сразу: запись идёт в фоне, а второе нажатие
            // по уже закрытой шторке не запишет птенцов дважды.
            FirstIntent.IncubatorChicksDismiss,
            FirstIntent.IncubatorChicksToNewProject,
            is FirstIntent.IncubatorChicksToProject -> state.copy(incubatorChicks = null)

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
    private fun FirstState.updateOpenChoiceProjectForTemplate(
        value: Boolean,
        projectList: List<DomainProjectTable>
    ): FirstState {
        return copy(
            isOpenChoiceProjectBottomSheet = value,
            projectListForTemplate = projectList,
            isOpenQrScannerBottomSheet = false
        )

    }
}
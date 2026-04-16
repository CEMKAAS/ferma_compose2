package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class FirstIntent() : BaseIntent {
    data object DeleteClicked : FirstIntent()
    data class ArchiveClicked(val value: DomainProjectTable?) : FirstIntent()
    data class UnarchiveClicked(val value: DomainProjectTable) : FirstIntent()

    data object SkipTrainingClicked : FirstIntent()
    data class OpenArchiveIncubatorBottomSheetClicked(
        val value: Boolean,
        val domainProjectTable: DomainProjectTable? = null
    ) : FirstIntent()

    data class OpenDeleteBottomSheetClicked(
        val value: Boolean,
        val domainProjectTable: DomainProjectTable? = null
    ) : FirstIntent()

    data object ArchiveModeClicked : FirstIntent()
    data class LoadingClicked(val value: Boolean) : FirstIntent()
}
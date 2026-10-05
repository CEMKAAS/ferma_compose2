package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.utils.IncubatorChicks

sealed class FirstIntent() : BaseIntent {
    data object DeleteClicked : FirstIntent()
    data class ArchiveClicked(val value: DomainProjectTable?) : FirstIntent()
    data class UnarchiveClicked(val value: DomainProjectTable) : FirstIntent()

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

    data class ShowDownloadingUpdate(val value: Boolean) : FirstIntent()
    data class QrCodeScanner(val value: String) : FirstIntent()
    data class OpenQrCodeScanner(val value: Boolean) : FirstIntent()
    data class OpenWarningQrCodeClick(val value: Boolean) : FirstIntent()
    data class OpenMultiProjectBottomSheetClick(
        val value: Boolean,
        val projectList: List<DomainProjectTable> = emptyList()
    ) : FirstIntent()

    data class ChoiceProjectForTemplateClick(
        val value: Long,
    ) : FirstIntent()

    // Птенцы из «Инкубатора»: шторка «куда добавить» и два ответа на неё.
    data class IncubatorChicksLoaded(
        val chicks: IncubatorChicks,
        val projects: List<DomainProjectTable>,
    ) : FirstIntent()
    data object IncubatorChicksDismiss : FirstIntent()
    data object IncubatorChicksToNewProject : FirstIntent()
    data class IncubatorChicksToProject(val id: Long) : FirstIntent()
}
package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.IncubatorChicks

data class FirstState(
    val list: List<DomainProjectTable> = emptyList(),
    val archiveList: List<DomainProjectTable> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false,
    val isOpenArchiveIncubatorBottomSheet: Boolean = false,
    val isOpenQrScannerBottomSheet: Boolean = false,
    val isOpenDeleteBottomSheet: Boolean = false,
    val isOpenDownloadingUpdate: Boolean = false,
    val isOpenWaringQrCode: Boolean = false,
    val isOpenChoiceProjectBottomSheet: Boolean = false,
    val projectListForTemplate: List<DomainProjectTable> = emptyList(),
    val currentProjectTable: DomainProjectTable? = null,
    /** Птенцы из «Инкубатора», ждущие выбора проекта; не `null` — шторка открыта. */
    val incubatorChicks: IncubatorChicks? = null,
    /** Активные проекты-фермы, в которые их можно добавить. */
    val chicksProjects: List<DomainProjectTable> = emptyList(),
) : ListState()
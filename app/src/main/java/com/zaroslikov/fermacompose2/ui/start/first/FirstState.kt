package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FirstState(
    val list: List<DomainProjectTable> = emptyList(),
    val archiveList: List<DomainProjectTable> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false,
    val isOpenArchiveIncubatorBottomSheet: Boolean = false,
    val isOpenDeleteBottomSheet: Boolean = false,
    val currentProjectTable: DomainProjectTable? = null,
    val appSettings: DomainAppSettings = DomainAppSettings(),
    val isFirstLaunch : Boolean = false,
    val isNotificationAsked: Boolean = false //todo
) : ListState()
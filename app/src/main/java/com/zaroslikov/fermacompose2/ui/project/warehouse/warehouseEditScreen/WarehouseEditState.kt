package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen

import androidx.compose.ui.graphics.ImageBitmap
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WarehouseEditState(
    val currentIcon: Int = R.drawable.livestock,
    val imageData: ImageBitmap? = null,
    val nameProject: String = "",
    val dateProject: String = dateToday(),
    val isInsertProject: Boolean = true,
    val currentProject: DomainProjectTable = DomainProjectTable(data = dateToday()),
    val currentSettings: DomainSettings = DomainSettings(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState() {

    fun enabledButton(): Boolean {
        val isEnabled = currentProject.titleProject.isNotBlank()
        return !isEnabled
    }
}
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
    val imagePath: String? = null,
    val nameProject: String = "",
    val dateProject: String = dateToday(),
    val isInsertProject: Boolean = true,
    val currentProject: DomainProjectTable = DomainProjectTable(date = dateToday()),
    val currentSettings: DomainSettings = DomainSettings(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val iconList: List<Int> = listOf(
        R.drawable.livestock,
        R.drawable.icons_chicken_s,
        R.drawable.icons_goat,
        R.drawable.icons_cow,
        R.drawable.icons_pig,
        R.drawable.icons_sheep,
        R.drawable.icons_hourse,
        R.drawable.icons_rabbit,
        R.drawable.icons_farming_pets,
        R.drawable.icons_pets,
        R.drawable.icons_plant,
        R.drawable.icons_farming_1,
        R.drawable.icons_farming_2,
        R.drawable.baseline_add_photo_alternate_24
    )
) : ListState() {

    fun enabledButton(): Boolean {
        val isEnabled = currentProject.title.isNotBlank()
        return isEnabled
    }
}
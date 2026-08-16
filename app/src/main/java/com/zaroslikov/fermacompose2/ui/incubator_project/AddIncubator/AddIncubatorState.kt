package com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.elements.icon.INCUBATOR_ICONS
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AddIncubatorState(
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val currentProduct: AddIncubator = AddIncubator(),
    val currentProject: DomainProjectTable = DomainProjectTable(),
    val iconList: List<AppIcon> = INCUBATOR_ICONS
) : EntryNewState()


data class AddIncubator(
    val id: Long = 0,
    val title: String = "",
    val brand: String = "",
    val model: String = "",
    val capacity: String = "",
    val date: String = dateToday(),
    val price: String = "",
    val note: String = "",
    val imagePath: String? = null,
    val currentIcon: AppIcon = AppIcon.EGG,
    val isAutoRotation: Boolean = false,
    val isAutoVentilation: Boolean = false,
    val currencySuffix: Suffix = Suffix.RUBLE,
    val brandList: List<String> = emptyList(),
    val modelList: List<String> = emptyList(),
    val error: ErrorIncubator = ErrorIncubator()
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled = title.isNotBlank() && capacity.isNotBlank() && !hasAnyError
        return isEnabled
    }
}

data class ErrorIncubator(
    val isErrorTitle: Boolean = false,
    val isErrorCapacity: Boolean = false
) : BaseError {
    override val hasAnyError: Boolean
        get() = isErrorTitle || isErrorCapacity
}
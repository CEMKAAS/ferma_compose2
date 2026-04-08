package com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AddIncubatorState(
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val currentProduct: AddIncubator = AddIncubator(),
    val currentProject: DomainProjectTable = DomainProjectTable(),
    val iconList: List<Int> = listOf(
        R.drawable.chicken,
        R.drawable.duck,
        R.drawable.external_goose_birds_icongeek26_outline_icongeek26,
        R.drawable.quail,
        R.drawable.turkeycock,
        R.drawable.outline_egg_24,
        R.drawable.baseline_add_photo_alternate_24
    )
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
    val currentIcon: Int = R.drawable.outline_egg_24,
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
    val hasAnyError: Boolean
        get() = isErrorTitle || isErrorCapacity
}
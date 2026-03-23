package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


data class AnimalEditState(
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val currentProduct: AnimalUi = AnimalUi()
) : EntryNewState()

data class AnimalUi(
    val id: Long = 0,
    val isAnimalGroup: Boolean = false, // true group

    val name: String = "",
    val type: String = "",

    val isDateFactory: Boolean = true,
    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),

    val sex: Boolean = true,

    val note: String = "",
    val image: String? = null,
    val archive: Boolean = false,

    val foodDay: String = "",
    val foodDaySuffix: Suffix = Suffix.GRAM,
    val idPT: Long = 0,
    val error: Error = Error(),
    val pickList: PickAnimalEditList = PickAnimalEditList(),
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class PickAnimalEditList(
    val typeList: List<String> = emptyList()
)

data class Error(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorType: Boolean = false
) : BaseError
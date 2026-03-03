package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


data class AnimalEditState(
    val animalUi: AnimalUi = AnimalUi(),
    val typeList: List<String> = emptyList(),
    override val error: Error = Error(),
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null

) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError()

}

data class Error(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorType: Boolean = false
) : BaseError {
    fun hasAnyError(): Boolean {
        return isErrorTitle || isErrorType
    }
}

data class AnimalUi(
    val id: Long = 0,
    val name: String = "",
    val type: String = "",
    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),

    val isAnimalGroup: Boolean = false, // true group
    val sex: Boolean = true,

    val note: String = "",
    val image: String? = null,
    val archive: Boolean = false,

    val isDateFactory: Boolean = true,

    val foodDay: String = "",
    val foodDaySuffix: Suffix = Suffix.GRAM,
    val idPT: Long = 0
)
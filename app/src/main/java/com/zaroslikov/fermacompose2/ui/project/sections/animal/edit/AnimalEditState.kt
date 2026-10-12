package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit

import com.zaroslikov.domain.models.animal.breed.DomainAnimalBreed
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.elements.icon.ANIMAL_ICONS
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


data class AnimalEditState(
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val currentProduct: AnimalUi = AnimalUi(),
) : EntryNewState()

data class AnimalUi(
    val id: Long = 0,
    val isAnimalGroup: Boolean = false, // true group

    val name: String = "",
    val type: String = "",
    val breed: String = "",

    val isDateFactory: Boolean = true,
    val dateBorn: String = dateToday(),
    val dateFactory: String = dateToday(),

    val sex: Boolean = true,

    val note: String = "",
    val archive: Boolean = false,

    val foodDay: String = "",
    val foodDaySuffix: Suffix = Suffix.GRAM,
    val idPT: Long = 0,
    val imagePath: String? = null,
    val currentIcon: AppIcon = AppIcon.PETS,
    val iconList: List<AppIcon> = ANIMAL_ICONS,
    val error: Error = Error(),
    val pickList: PickAnimalEditList = PickAnimalEditList(),
    /**
     * Вид активной беременности самки. Не null — пол менять нельзя, тип — только в пределах
     * этого вида («Сначала завершите беременность»).
     */
    val pregnancyLockedSpecies: AnimalSpecies? = null,
    /** Обезличенная группа: сколько самок и самцов (необязательно) и её поголовье — для проверки. */
    val femalesCount: String = "",
    val malesCount: String = "",
    val headCount: Double = 0.0,
    override val hasAnyError: Boolean = false
) : BaseProduct() {
    val isPregnancyLocked: Boolean get() = pregnancyLockedSpecies != null
}

data class PickAnimalEditList(
    val typeList: List<String> = emptyList(),
    /** Породы неархивных животных проекта; в списке — только породы вида из поля «Тип». */
    val breedList: List<DomainAnimalBreed> = emptyList()
)

data class Error(
    val isErrorTitle: Boolean = false,
    val isErrorSlash: Boolean = false,
    val isErrorType: Boolean = false,
    /** Тип другого вида у беременной самки. */
    val isErrorPregnancyType: Boolean = false,
    /** Самок и самцов вместе больше, чем голов в группе. */
    val isErrorSexCount: Boolean = false,
    override val hasAnyError: Boolean = false
) : BaseError
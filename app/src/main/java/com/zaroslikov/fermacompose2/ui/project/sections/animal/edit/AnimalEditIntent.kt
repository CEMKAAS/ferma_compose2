package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AnimalEditIntent : BaseIntent {
    data class LoadDate(val value: AnimalUi) : AnimalEditIntent()
    data class LoadingChanged(val value: Boolean) : AnimalEditIntent()
    data class IconClicked(val value: Int) : AnimalEditIntent()
    data class ImagePathClicked(val value: String?) : AnimalEditIntent()

    data class TitleChanged(val value: String) : AnimalEditIntent()
    data class TypeChanged(val value: String) : AnimalEditIntent()
    data class SexClicked(val value: Boolean) : AnimalEditIntent()

    data class DateClicked(val value: String) : AnimalEditIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalEditIntent()
    data class DateFactoryChanged(val value: String) : AnimalEditIntent()
    data class FoodDayChanged(val value: String) : AnimalEditIntent()
    data class FoodDaySuffixClicked(val value: Suffix) : AnimalEditIntent()
    data object Update : AnimalEditIntent()
}

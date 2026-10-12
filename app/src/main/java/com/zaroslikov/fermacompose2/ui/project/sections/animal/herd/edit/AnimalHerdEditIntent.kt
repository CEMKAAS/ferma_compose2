package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.edit

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class AnimalHerdEditIntent : BaseIntent {
    data class Loaded(val herd: DomainAnimalHerd) : AnimalHerdEditIntent()
    data class NameChanged(val value: String) : AnimalHerdEditIntent()
    data class ImagePathClicked(val value: String?) : AnimalHerdEditIntent()
    data class IconClicked(val value: AppIcon) : AnimalHerdEditIntent()
    data class SavingChanged(val value: Boolean) : AnimalHerdEditIntent()
    data object Save : AnimalHerdEditIntent()
}

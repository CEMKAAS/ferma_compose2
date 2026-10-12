package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.edit

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.elements.icon.ANIMAL_ICONS
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

/** Редактирование сборной группы: только название и фото/иконка (примечание — в карточке). */
data class AnimalHerdEditState(
    val herd: DomainAnimalHerd = DomainAnimalHerd(),
    val name: String = "",
    val imagePath: String? = null,
    /** Иконка, выбранная пользователем; null — не выбрана, в списке показываются иконки членов. */
    val currentIcon: AppIcon? = null,
    val iconList: List<AppIcon> = ANIMAL_ICONS,
    val isSaving: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState {
    val canSave: Boolean get() = !isSaving && name.isNotBlank()
}

package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.edit

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class AnimalHerdEditReduce : BaseReducer<AnimalHerdEditState, AnimalHerdEditIntent>() {
    override fun reducer(state: AnimalHerdEditState, intent: AnimalHerdEditIntent): AnimalHerdEditState {
        return when (intent) {
            is AnimalHerdEditIntent.Loaded -> state.copy(
                herd = intent.herd,
                name = intent.herd.name,
                imagePath = intent.herd.imagePath,
                currentIcon = intent.herd.currentIcon,
                isLoading = false
            )

            is AnimalHerdEditIntent.NameChanged -> state.copy(name = intent.value)
            is AnimalHerdEditIntent.ImagePathClicked -> state.copy(imagePath = intent.value)
            is AnimalHerdEditIntent.IconClicked -> state.copy(currentIcon = intent.value)
            is AnimalHerdEditIntent.SavingChanged -> state.copy(isSaving = intent.value)
            AnimalHerdEditIntent.Save -> state
        }
    }
}

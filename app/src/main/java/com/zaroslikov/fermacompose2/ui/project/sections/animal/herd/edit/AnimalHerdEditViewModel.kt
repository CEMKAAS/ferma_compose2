package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AnimalHerdRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalHerdEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalHerdRepository: AnimalHerdRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel2<AnimalHerdEditState, AnimalHerdEditIntent, AnimalHerdEditReduce>(
    AnimalHerdEditState(), AnimalHerdEditReduce()
) {
    private val herdId: Long = checkNotNull(savedStateHandle[AnimalHerdEditDestination.itemId])

    init {
        viewModelScope.launch {
            val herd = animalHerdRepository.getHerd(herdId).first()
            if (herd == null) navigateTo(UiEvent.NavigateBack)
            else sendIntent(AnimalHerdEditIntent.Loaded(herd))
        }
    }

    fun onIntent(intent: AnimalHerdEditIntent) {
        if (intent == AnimalHerdEditIntent.Save) save() else sendIntent(intent)
    }

    private fun save() {
        val state = getState()
        if (!state.canSave) return
        sendIntent(AnimalHerdEditIntent.SavingChanged(true))
        viewModelScope.launch {
            try {
                animalHerdRepository.updateHerd(
                    state.herd.copy(
                        name = state.name.trim(),
                        imagePath = state.imagePath,
                        // Фото важнее иконки — как у животных
                        currentIcon = if (state.imagePath != null) null else state.currentIcon
                    )
                )
                showMessage(resourceProvider.getString(R.string.snackbar_animal_herd_updated))
                navigateTo(UiEvent.NavigateBack)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(resourceProvider.getString(R.string.snackbar_animal_group_error))
                sendIntent(AnimalHerdEditIntent.SavingChanged(false))
            }
        }
    }
}

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.IncubatorRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryBookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val incubatorRepository: IncubatorRepository
) : BaseViewModel<EntryBookmarkState, EntryBookmarkIntent>(EntryBookmarkState()) {


    init {
    }

    private fun loadData() {
        viewModelScope.launch {
//            val incubatorList = incubatorRepository.getIncubatorList(id)
        }
    }


}

sealed class EntryBookmarkIntent() : BaseIntent {
    data class TitleChanged(val value: String) : EntryBookmarkIntent()
    data class BrandChanged(val value: String) : EntryBookmarkIntent()
    data class ModelChanged(val value: String) : EntryBookmarkIntent()
    data class CapacityChanged(val value: String) : EntryBookmarkIntent()
    data class PriceChanged(val value: String) : EntryBookmarkIntent()
    data class DateClicked(val value: String) : EntryBookmarkIntent()
    data class NoteClicked(val value: String) : EntryBookmarkIntent()
    data class AutoRotationClicked(val value: Boolean) : EntryBookmarkIntent()
    data class AutoVentilationClicked(val value: Boolean) :EntryBookmarkIntent()
    data class CurrencyClicked(val value: Suffix) : EntryBookmarkIntent()
    data object Insert : EntryBookmarkIntent()
    data object Update : EntryBookmarkIntent()
}
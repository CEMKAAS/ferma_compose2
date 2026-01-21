package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.IncubatorRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubatorIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val incubatorRepository: IncubatorRepository
) : BaseViewModel<BookmarkState, BookmarkIntent>(BookmarkState()) {


    init {
    }

    private fun loadData() {
        viewModelScope.launch {
//            val incubatorList = incubatorRepository.getIncubatorList(id)
        }
    }


}

sealed class BookmarkIntent() : BaseIntent {
    data class TempChanged(val value: String) : BookmarkIntent()
    data class DampChanged(val value: String) : BookmarkIntent()
    data class OverChanged(val value: String) : BookmarkIntent()
    data class AiringChanged(val value: String) : BookmarkIntent()
}
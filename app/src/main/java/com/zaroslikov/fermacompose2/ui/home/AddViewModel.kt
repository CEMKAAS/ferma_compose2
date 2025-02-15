/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaroslikov.fermacompose2.ui.home


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.water.BrieflyItemCount
import com.zaroslikov.fermacompose2.data.water.BrieflyUiState
import com.zaroslikov.fermacompose2.data.water.HomeUiState
import com.zaroslikov.fermacompose2.ui.finance.Fin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AddViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Int = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])


    val homeUiState: StateFlow<HomeUiState> =
        itemsRepository.getAllItemsStream(itemId).map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    val brieflyUiState: StateFlow<BrieflyUiState> =
        itemsRepository.getBrieflyItemAdd(itemId).map { BrieflyUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BrieflyUiState()
            )

    fun getDetailsName(name: String): Flow<List<AddTable>> {
        return itemsRepository.getBrieflyDetailsItemAdd(itemId.toLong(), name)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}



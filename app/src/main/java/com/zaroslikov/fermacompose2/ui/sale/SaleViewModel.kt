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

package com.zaroslikov.fermacompose2.ui.sale

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.water.BrieflyPriceUiState
import com.zaroslikov.fermacompose2.data.water.BrieflyUiState
import com.zaroslikov.fermacompose2.data.water.SaleUiState
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.home.AddViewModel.Companion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class SaleViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {



    val itemId: Int = checkNotNull(savedStateHandle[SaleDestination.itemIdArg])


    val saleUiState: StateFlow<SaleUiState> =
        itemsRepository.getAllSaleItems(itemId).map { SaleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SaleUiState()
            )


    val brieflyUiState: StateFlow<BrieflyPriceUiState> =
        itemsRepository.getBrieflyItemSale(itemId).map { BrieflyPriceUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BrieflyPriceUiState()
            )

    fun getDetailsName(name: String): Flow<List<SaleTable>> {
        return itemsRepository.getBrieflyDetailsItemSale(itemId.toLong(), name)
    }



    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }




}


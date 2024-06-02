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

package com.zaroslikov.fermacompose2.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zaroslikov.fermacompose2.InventoryApplication
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEditViewModel
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryViewModel
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesViewModel
import com.zaroslikov.fermacompose2.ui.home.AddEditViewModel
import com.zaroslikov.fermacompose2.ui.home.AddEntryViewModel
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleEditViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleEntryViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleViewModel
import com.zaroslikov.fermacompose2.ui.start.StartScreenViewModel
import com.zaroslikov.fermacompose2.ui.start.add.ProjectAddViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            StartScreenViewModel(inventoryApplication().container.itemsRepository)
        }

        initializer {
            ProjectAddViewModel(inventoryApplication().container.itemsRepository)
        }

        // Initializer for HomeViewModel
        initializer {
            AddViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        initializer {
            AddEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        initializer {
            AddEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        //Sale
        initializer {
            SaleViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        initializer {
            SaleEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        initializer {
            SaleEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        //Expenses
        initializer {
            ExpensesViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        initializer {
           ExpensesEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

        initializer {
            ExpensesEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository)
        }

    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)

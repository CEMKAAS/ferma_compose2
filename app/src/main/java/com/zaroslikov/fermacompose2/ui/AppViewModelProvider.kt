package com.zaroslikov.fermacompose2.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zaroslikov.fermacompose2.InventoryApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {


//        initializer {
//            ExpensesEntryViewModel(
//                this.createSavedStateHandle(),
//                inventoryApplication().container.itemsRepository
//            )
//        }
//
//        initializer {
//            ExpensesEditViewModel(
//                this.createSavedStateHandle(),
//                inventoryApplication().container.itemsRepository
//            )
//        }


    }
}

fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)

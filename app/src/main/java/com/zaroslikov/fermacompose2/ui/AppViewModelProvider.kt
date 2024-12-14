package com.zaroslikov.fermacompose2.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zaroslikov.fermacompose2.InventoryApplication
import com.zaroslikov.fermacompose2.ui.animal.AnimalCardViewModel
import com.zaroslikov.fermacompose2.ui.animal.AnimalEditViewModel
import com.zaroslikov.fermacompose2.ui.animal.AnimalEntryViewModel
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsViewModel
import com.zaroslikov.fermacompose2.ui.animal.AnimalViewModel
import com.zaroslikov.fermacompose2.ui.arhiv.FinanceArhivViewModel
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEditViewModel
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryViewModel
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesViewModel
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisViewModel
import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryViewModel
import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesViewModel
import com.zaroslikov.fermacompose2.ui.finance.FinanceMountViewModel
import com.zaroslikov.fermacompose2.ui.finance.FinanceViewModel
import com.zaroslikov.fermacompose2.ui.home.AddEditViewModel
import com.zaroslikov.fermacompose2.ui.home.AddEntryViewModel
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorEditDayViewModel
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorOvoscopViewModel
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditViewModel
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorViewModel
import com.zaroslikov.fermacompose2.ui.new_year.NewYearViewModel
import com.zaroslikov.fermacompose2.ui.note.NoteEditViewModel
import com.zaroslikov.fermacompose2.ui.note.NoteEntryViewModel
import com.zaroslikov.fermacompose2.ui.note.NoteViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleEditViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleEntryViewModel
import com.zaroslikov.fermacompose2.ui.sale.SaleViewModel
import com.zaroslikov.fermacompose2.ui.start.StartScreenViewModel
import com.zaroslikov.fermacompose2.ui.start.add.ProjectAddViewModel
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AddIncubatorViewModel
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditViewModel
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseViewModel
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEditViewModel
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEntryViewModel
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            StartScreenViewModel(
                inventoryApplication().container.itemsRepository,
                inventoryApplication().container.waterRepository
            )
        }

        initializer {
            ProjectAddViewModel(inventoryApplication().container.itemsRepository)
        }

        initializer {
            AddIncubatorViewModel(
                inventoryApplication().container.itemsRepository,
                inventoryApplication().container.waterRepository
            )
        }

        initializer {
            IncubatorProjectEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository,
                inventoryApplication().container.waterRepository
            )
        }

        //Incubator
        initializer {
            IncubatorViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository,
                inventoryApplication().container.waterRepository
            )
        }

        initializer {
            IncubatorOvoscopViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            IncubatorEditDayViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        //Warehouse
        initializer {
            WarehouseViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            WarehouseEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        // Finance
        initializer {
            FinanceViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            FinanceMountViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            FinanceCategoryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            FinanceIncomeExpensesViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            FinanceAnalysisViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        // HomeViewModel
        initializer {
            AddViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            AddEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            AddEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        //Sale
        initializer {
            SaleViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            SaleEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            SaleEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        //Expenses
        initializer {
            ExpensesViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            ExpensesEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            ExpensesEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }


        // WriteOff
        initializer {
            WriteOffViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            WriteOffEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            WriteOffEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        //Animal
        initializer {
            AnimalViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            AnimalEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            AnimalCardViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            AnimalIndicatorsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }
        initializer {
            AnimalEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        //Note
        initializer {
            NoteEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }
        initializer {
            NoteEntryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }
        initializer {
            NoteViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            FinanceArhivViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }

        initializer {
            NewYearViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository
            )
        }
    }
}

fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)

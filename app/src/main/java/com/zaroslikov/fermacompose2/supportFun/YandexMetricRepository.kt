package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubator
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmark
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size.CurrentAnimalSize
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination.Vaccination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight.CurrentAnimalWeight
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen.ExpensesEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen.SaleEntryState2
import com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.WriteOffEntryState2
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen.WarehouseEditState
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.FoodListUi

interface YandexMetricRepository {
    fun metricalProject(state: WarehouseEditState)
    fun metricalIncubator(state: AddIncubator)
    fun metricalBookmark(state: EntryBookmark)
    fun metricalFastAdd(state: DomainFastAddProduct)
    fun metricalWriteOffFood(state: FoodListUi)
    fun metricAdd(domainAddTable: AddEntryState2)
    fun metricSale(domainSaleTable: SaleEntryState2)
    fun metricalExpenses(domainExpensesTable: ExpensesEntryState2)
    fun metricalWriteOff(currentProduct: WriteOffEntryState2)
    fun metricalAnimalVaccination(state: Vaccination)
    fun metricalNote(title: String)
    fun metricalAnimal(state: AnimalEntryState2, )
    fun metricalAnimalSize(state: CurrentAnimalSize, )
    fun metricalAnimalWeight(state: CurrentAnimalWeight)
    fun metricalAnimalCount(state: DomainAnimalCount, )
    fun metricalProfile(name: String)
}
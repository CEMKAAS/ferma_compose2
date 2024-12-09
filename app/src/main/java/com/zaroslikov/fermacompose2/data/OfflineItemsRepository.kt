package com.zaroslikov.fermacompose2.data

import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsVM
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.expenses.AnimalExpensesList
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTime
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.home.PairString
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(id: Int): Flow<List<AddTable>> = itemDao.getAllItems(id)
    override fun getItemStream(id: Int): Flow<AddTable?> = itemDao.getItem(id)
    override suspend fun getIncubatorListArh4(idPT: Int): List<Incubator> =
        itemDao.getIncubatorListArh4(idPT)

    override suspend fun getIncubatorListArh6(type: String): List<ProjectTable> =
        itemDao.getIncubatorListArh6(type)

    override fun getAllProject(): Flow<List<ProjectTable>> = itemDao.getAllProject()
    override fun getAllProjectArh(): Flow<List<ProjectTable>> = itemDao.getAllProjectArh()
    override fun getAllProjectAct(): Flow<List<ProjectTable>> = itemDao.getAllProjectAct()
    override fun getProject(id: Int): Flow<ProjectTable> = itemDao.getProject(id)
    override suspend fun updateProject(item: ProjectTable) = itemDao.updateProject(item)
    override suspend fun deleteProject(item: ProjectTable) = itemDao.deleteProject(item)
    override fun getLastProject(): Flow<Int> = itemDao.getLastProject()
    override fun getCountRowIncubator(): Flow<Int> = itemDao.getCountRowIncubator()
    override fun getCountRowProject(): Flow<Int> = itemDao.getCountRowProject()
    override fun getProjectListAct(): Flow<List<ProjectTable>> = itemDao.getProjectListAct()
    override fun getIncubatorList(id: Int): Flow<List<Incubator>> = itemDao.getIncubatorList(id)
    override suspend fun getIncubatorList2(id: Int): List<Incubator> = itemDao.getIncubatorList2(id)
    override fun getIncubator(id: Int): Flow<Incubator> = itemDao.getIncubator(id)
    override fun getIncubatorEditDay(id: Int, day: Int): Flow<Incubator> =
        itemDao.getIncubatorEditDay(id, day)

    override fun getItemAdd(id: Int): Flow<AddTable> = itemDao.getItemAdd(id)
    override fun getItemsTitleAddList(id: Int): Flow<List<String>> =
        itemDao.getItemsTitleAddList(id)

    override fun getItemsWriteoffList(id: Int): Flow<List<PairString>> =
        itemDao.getItemsWriteoffList(id)

    override fun getItemsCategoryAddList(id: Int): Flow<List<String>> =
        itemDao.getItemsCategoryAddList(id)

    override fun getItemsAnimalAddList(id: Int): Flow<List<PairString>> =
        itemDao.getItemsAnimalAddList(id)

    override suspend fun insertProject(projectTable: ProjectTable) =
        itemDao.insertProject(projectTable)

    override suspend fun insertProjectLong(projectTable: ProjectTable): Long =
        itemDao.insertProjectLong(projectTable)

    override suspend fun insertItem(item: AddTable) = itemDao.insert(item)

    override suspend fun deleteItem(item: AddTable) = itemDao.delete(item)

    override suspend fun updateItem(item: AddTable) = itemDao.update(item)

    //Sale
    override fun getAllSaleItems(id: Int): Flow<List<SaleTable>> = itemDao.getAllSaleItems(id)

    override fun getItemSale(id: Int): Flow<SaleTable> = itemDao.getItemSale(id)

    override fun getItemsTitleSaleList(id: Int): Flow<List<String>> =
        itemDao.getItemsTitleSaleList(id)

    override fun getItemsCategorySaleList(id: Int): Flow<List<String>> =
        itemDao.getItemsCategorySaleList(id)

    override fun getItemsBuyerSaleList(id: Int): Flow<List<String>> =
        itemDao.getItemsBuyerSaleList(id)

    override suspend fun insertSale(item: SaleTable) = itemDao.insertSale(item)

    override suspend fun updateSale(item: SaleTable) = itemDao.updateSale(item)

    override suspend fun deleteSale(item: SaleTable) = itemDao.deleteSale(item)

    //Expenses
    override fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>> =
        itemDao.getAllExpensesItems(id)

    override fun getItemExpenses(id: Int): Flow<ExpensesTable> = itemDao.getItemExpenses(id)
    override suspend fun getItemExpensesAnimal(id: Int): List<Long> = itemDao.getItemExpensesAnimal(id)

    override fun getItemsTitleExpensesList(id: Int): Flow<List<String>> =
        itemDao.getItemsTitleExpensesList(id)

    override fun getItemsCategoryExpensesList(id: Int): Flow<List<String>> =
        itemDao.getItemsCategoryExpensesList(id)

    override fun getItemsAnimalExpensesList(id: Int): Flow<List<AnimalExpensesList>> =
        itemDao.getItemsAnimalExpensesList(id)

    override suspend fun insertExpenses(item: ExpensesTable) = itemDao.insertExpenses(item)
    override suspend fun updateExpenses(item: ExpensesTable) = itemDao.updateExpenses(item)
    override suspend fun deleteExpenses(item: ExpensesTable) = itemDao.deleteExpenses(item)

    override suspend fun insertExpensesAnimal(item: ExpensesAnimalTable) = itemDao.insertExpensesAnimal(item)
    override suspend fun updateExpensesAnimal(item: ExpensesAnimalTable) = itemDao.updateExpensesAnimal(item)
    override suspend fun deleteExpensesAnimal(item: ExpensesAnimalTable) = itemDao.deleteExpensesAnimal(item)

    //WriteOff
    override fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>> =
        itemDao.getAllWriteOffItems(id)

    override fun getItemWriteOff(id: Int): Flow<WriteOffTable> = itemDao.getItemWriteOff(id)

    override suspend fun insertWriteOff(item: WriteOffTable) = itemDao.insertWriteOff(item)
    override suspend fun updateWriteOff(item: WriteOffTable) = itemDao.updateWriteOff(item)
    override suspend fun deleteWriteOff(item: WriteOffTable) = itemDao.deleteWriteOff(item)

    //Finance
    override fun getCurrentBalance(id: Int): Flow<Double> = itemDao.getCurrentBalance(id)
    override fun getIncome(id: Int): Flow<Double> = itemDao.getIncome(id)
    override fun getExpenses(id: Int): Flow<Double> = itemDao.getExpenses(id)
    override fun getOwnNeed(id: Int): Flow<Double> = itemDao.getOwnNeed(id)
    override fun getScrap(id: Int): Flow<Double> = itemDao.getScrap(id)

    override fun getIncomeMountFin(id: Int, mount: Int, year: Int): Flow<Double> =
        itemDao.getIncomeMountFin(id, mount, year)

    override fun getExpensesMountFin(
        id: Int,
        mount: Int,
        year: Int,
        yearMonth: String
    ): Flow<Double> =
        itemDao.getExpensesMountFin(id, mount, year, yearMonth)


    override fun getIncomeMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getIncomeMount(id, dateBegin, dateEnd)

    override fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getExpensesMount(id, dateBegin, dateEnd)

    override fun getOwnNeedMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getOwnNeedMonth(id, dateBegin, dateEnd)

    override fun getScrapMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getScrapMonth(id, dateBegin, dateEnd)

    override fun getCategoryIncomeCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>> =
        itemDao.getCategoryIncomeCurrentMonth(id, dateBegin, dateEnd)

    override fun getCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>> =
        itemDao.getCategoryExpensesCurrentMonth(id, dateBegin, dateEnd)

    override fun getIncomeExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDetails>> =
        itemDao.getIncomeExpensesCurrentMonth(id, dateBegin, dateEnd)


    //FinanceTap
    override fun getIncomeAllList(id: Int): Flow<List<Fin>> = itemDao.getIncomeAllList(id)
    override fun getExpensesAllList(id: Int): Flow<List<Fin>> = itemDao.getExpensesAllList(id)
    override fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>> =
        itemDao.getExpensesAnimalAllList(id)

    override fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>> =
        itemDao.getIncomeCategoryAllList(id)

    override fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>> =
        itemDao.getExpensesCategoryAllList(id)

    override fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>> =
        itemDao.getProductListCategoryIncomeCurrentMonth(id, dateBegin, dateEnd, category)

    override fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>> =
        itemDao.getProductLisCategoryExpensesCurrentMonth(id, dateBegin, dateEnd, category)

    override fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>> =
        itemDao.getProductLisCategoryExpensesAnimalCurrentMonth(id, dateBegin, dateEnd)

    override fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>> =
        itemDao.getCurrentBalanceWarehouse(id)

    override fun getCurrentFoodWarehouse(id: Int): Flow<List<ExpensesTable>> =
        itemDao.getCurrentFoodWarehouse(id)

    override fun getCurrentExpensesWarehouse(id: Int): Flow<List<WarehouseData>> =
        itemDao.getCurrentExpensesWarehouse(id)

    override fun getAnalysisAddAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisAddAllTime(id, name)

    override fun getAnalysisSaleAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisSaleAllTime(id, name)

    override fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisWriteOffAllTime(id, name)

    override fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisWriteOffOwnNeedsAllTime(id, name)

    override fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisWriteOffScrapAllTime(id, name)

    override fun getAnalysisSaleSoldAllTime(id: Int, name: String): Flow<Double> =
        itemDao.getAnalysisSaleSoldAllTime(id, name)

    override fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double> =
        itemDao.getAnalysisWriteOffOwnNeedsMoneyAllTime(id, name)

    override fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double> =
        itemDao.getAnalysisWriteOffScrapMoneyAllTime(id, name)

    override fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisAddAverageValueAllTime(id, name)

    override fun getAnalysisAddAnimalAllTime(id: Int, name: String): Flow<List<AnimalTitSuff>> =
        itemDao.getAnalysisAddAnimalAllTime(id, name)

    override fun getAnalysisSaleBuyerAllTime(
        id: Int,
        name: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> = itemDao.getAnalysisSaleBuyerAllTime(id, name)

    //analysis Range
    override fun getAnalysisAddAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisAddAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisSaleAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisSaleAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisWriteOffAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisWriteOffAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisWriteOffOwnNeedsAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisWriteOffScrapAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisWriteOffScrapAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisSaleSoldAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> = itemDao.getAnalysisSaleSoldAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> =
        itemDao.getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> =
        itemDao.getAnalysisWriteOffScrapMoneyAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisAddAverageValueAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisAddAverageValueAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisAddAnimalAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnimalTitSuff>> =
        itemDao.getAnalysisAddAnimalAllTimeRange(id, name, dateBegin, dateEnd)

    override fun getAnalysisSaleBuyerAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> =
        itemDao.getAnalysisSaleBuyerAllTimeRange(id, name, dateBegin, dateEnd)

    override suspend fun insertIncubator(item: Incubator) =
        itemDao.insertIncubator(item)

    override suspend fun updateIncubator(item: Incubator) =
        itemDao.updateIncubator(item)

    override fun getAllAnimal(id: Int): Flow<List<AnimalTable>> = itemDao.getAllAnimal(id)
    override fun getAnimal(id: Int): Flow<AnimalTable> = itemDao.getAnimal(id)
    override fun getTypeAnimal(id: Int): Flow<List<String>> = itemDao.getTypeAnimal(id)
    override suspend fun insertAnimalTable(animalTable: AnimalTable): Long =
        itemDao.insertAnimalTable(animalTable)

    override suspend fun insertAnimalCountTable(animalCountTable: AnimalCountTable) =
        itemDao.insertAnimalCountTable(animalCountTable)

    override suspend fun insertAnimalSizeTable(animalSizeTable: AnimalSizeTable) =
        itemDao.insertAnimalSizeTable(animalSizeTable)

    override suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable) =
        itemDao.insertAnimalVaccinationTable(animalVaccinationTable)

    override suspend fun insertAnimalWeightTable(animalWeightTable: AnimalWeightTable) =
        itemDao.insertAnimalWeightTable(animalWeightTable)

    override suspend fun updateAnimalCountTable(animalCountTable: AnimalCountTable) =
        itemDao.updateAnimalCountTable(animalCountTable)

    override suspend fun updateAnimalSizeTable(animalSizeTable: AnimalSizeTable) =
        itemDao.updateAnimalSizeTable(animalSizeTable)

    override suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable) =
        itemDao.updateAnimalVaccinationTable(animalVaccinationTable)

    override suspend fun updateAnimalWeightTable(animalWeightTable: AnimalWeightTable) =
        itemDao.updateAnimalWeightTable(animalWeightTable)

    override suspend fun deleteAnimalCountTable(animalCountTable: AnimalCountTable) =
        itemDao.deleteAnimalCountTable(animalCountTable)

    override suspend fun deleteAnimalSizeTable(animalSizeTable: AnimalSizeTable) =
        itemDao.deleteAnimalSizeTable(animalSizeTable)

    override suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable) =
        itemDao.insertAnimalVaccinationTable(animalVaccinationTable)

    override suspend fun deleteAnimalWeightTable(animalWeightTable: AnimalWeightTable) =
        itemDao.deleteAnimalWeightTable(animalWeightTable)

    override suspend fun updateAnimalTable(animalTable: AnimalTable) =
        itemDao.updateAnimalTable(animalTable)

    override suspend fun deleteAnimalTable(animalTable: AnimalTable) =
        itemDao.deleteAnimalTable(animalTable)

    override fun getCountAnimalLimit(id: Int): Flow<List<AnimalCountTable>> =
        itemDao.getCountAnimalLimit(id)

    override fun getSizeAnimalLimit(id: Int): Flow<List<AnimalSizeTable>> =
        itemDao.getSizeAnimalLimit(id)

    override fun getVaccinationtAnimalLimit(id: Int): Flow<List<AnimalVaccinationTable>> =
        itemDao.getVaccinationtAnimalLimit(id)

    override fun getWeightAnimalLimit(id: Int): Flow<List<AnimalWeightTable>> =
        itemDao.getWeightAnimalLimit(id)

    override fun getCountAnimal(id: Int): Flow<List<AnimalIndicatorsVM>> =
        itemDao.getCountAnimal(id)

    override fun getSizeAnimal(id: Int): Flow<List<AnimalIndicatorsVM>> = itemDao.getSizeAnimal(id)

    override fun getVaccinationtAnimal(id: Int): Flow<List<AnimalVaccinationTable>> =
        itemDao.getVaccinationtAnimal(id)

    override fun getWeightAnimal(id: Int): Flow<List<AnimalIndicatorsVM>> =
        itemDao.getWeightAnimal(id)

    override fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>> =
        itemDao.getProductAnimal(name)

    /**Note**/
    override fun getAllNote(id: Int): Flow<List<NoteTable>> = itemDao.getAllNote(id)
    override fun getNote(id: Int): Flow<NoteTable> = itemDao.getNote(id)

    override suspend fun insertNote(item: NoteTable) = itemDao.insertNote(item)
    override suspend fun updateNote(item: NoteTable) = itemDao.updateNote(item)
    override suspend fun deleteNote(item: NoteTable) = itemDao.deleteNote(item)

}

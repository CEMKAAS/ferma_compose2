package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalCard
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.DomainIndicatorsVM
import java.util.concurrent.Flow

interface ItemsRepository {
    fun getAllItemsStream(id: Int): Flow<List<AddTable>>
    fun getItemStream(id: Int): Flow<AddTable?>

    suspend fun getIncubatorListArh4(idPT: Int): List<Incubator>
    suspend fun getIncubatorListArh6(type: String): List<ProjectTable>

    fun getAllProject(): Flow<List<ProjectTable>>

    fun getProject(id: Int): Flow<ProjectTable>
    suspend fun updateProject(item: ProjectTable)
    suspend fun deleteProject(item: ProjectTable)

    fun getLastProject(): Flow<Int>

    fun getCountRowIncubator(): Flow<Int>
    fun getCountRowProject(): Flow<Int>

    fun getProjectListAct(): Flow<List<ProjectTable>>
    fun getIncubatorList(id: Int): Flow<List<Incubator>>

    suspend fun getIncubatorList2(id: Int): List<Incubator>
    fun getIncubator(id: Int): Flow<Incubator>
    fun getIncubatorEditDay(id: Int, day: Int): Flow<Incubator>

    fun getItemAdd(id: Long): Flow<DomainAddTable>

    fun getItemsTitleAddList(id: Long): Flow<List<PairData>>

    fun getItemsWriteoffList(id: Int): Flow<List<SaleTitleData>>

    fun getItemsCategoryAddList(id: Long): Flow<List<String>>
    fun getItemsAnimalAddList(id: Long): Flow<List<TripleData>>

    suspend fun insertProject(projectTable: ProjectTable)
    suspend fun insertProjectLong(projectTable: ProjectTable): Long

    //==================== Add ====================
    suspend fun insertItem(item: DomainAddTable)

    suspend fun deleteAddById(id: Long)
    suspend fun updateItem(item: DomainAddTable)

    fun getBrieflyItemAdd(id: Int): Flow<List<BrieflyItemCount>>
    fun getBrieflyDetailsItemAdd(id: Long, name: String): Flow<List<AddTable>>

    fun getAnimalById(id: Long): Flow<String>

    //==================== Sale ====================
    fun getAllSaleItems(id: Int): Flow<List<SaleTable>>
    fun getItemSale(id: Int): Flow<SaleTable>
    fun getItemSaleIdCountAnimal(id: Int): Flow<SaleTable>
    fun getBrieflyItemSale(id: Int): Flow<List<BrieflyItemPrice>>
    fun getBrieflyDetailsItemSale(id: Long, name: String): Flow<List<SaleTable>>
    fun getItemsTitleSaleList(id: Int): Flow<List<SaleTitleData>>
    fun getItemsCategorySaleList(id: Int): Flow<List<String>>
    fun getItemsBuyerSaleList(id: Int): Flow<List<String>>

    suspend fun insertSale(item: SaleTable)
    suspend fun updateSale(item: SaleTable)
    suspend fun deleteSaleById(id: Long)

    //==================== Expenses ====================
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>
    fun getItemExpenses(id: Int): Flow<ExpensesTable>
    fun getItemExpensesIdAnimalCount(id: Int): Flow<ExpensesTable>
    fun getBrieflyItemExpenses(id: Int): Flow<List<BrieflyItemPrice>>
    fun getBrieflyDetailsItemExpenses(id: Long, name: String): Flow<List<ExpensesTable>>

    fun getItemsTitleExpensesList(id: Int): Flow<List<PairData>>
    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>

    suspend fun getItemExpensesAnimal(id: Int): List<Long>
    suspend fun getItemsAnimalExpensesList2(id: Int, idExpenses: Long): List<AnimalExpensesList2>
    suspend fun insertExpenses(item: ExpensesTable): Long
    suspend fun updateExpenses(item: ExpensesTable)
    suspend fun deleteExpenses(item: ExpensesTable)


    suspend fun insertExpensesAnimal(item: ExpensesAnimalTable)
    suspend fun updateExpensesAnimal(item: ExpensesAnimalTable)
    suspend fun deleteExpensesAnimal(item: ExpensesAnimalTable)

    //==================== WriteOff ====================
    fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>>
    fun getItemWriteOff(id: Int): Flow<WriteOffTable>
    fun getItemWriteOffIdCountAnimal(id: Int): Flow<WriteOffTable>
    fun getBrieflyItemWriteOff(id: Int): Flow<List<BrieflyItemCount>>
    fun getBrieflyDetailsItemWriteOff(id: Long, name: String): Flow<List<WriteOffTable>>

    suspend fun insertWriteOff(item: WriteOffTable)
    suspend fun updateWriteOff(item: WriteOffTable)
    suspend fun deleteWriteOff(id: Long)

    //==================== Finance ====================
    fun getCurrentBalance(id: Int): Flow<Double>
    fun getIncome(id: Int): Flow<Double>
    fun getExpenses(id: Int): Flow<Double>
    fun getItemExpensesForVaccination(id: Long): Flow<ExpensesTable>
    fun getOwnNeed(id: Int): Flow<Double>
    fun getScrap(id: Int): Flow<Double>

    fun getIncomeMountFin(id: Int, mount: Int, year: Int): Flow<Double>
    fun getExpensesMountFin(id: Int, mount: Int, year: Int, yearMonth: String): Flow<Double>


    fun getIncomeMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>
    fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>
    fun getOwnNeedMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double>
    fun getScrapMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    fun getCategoryIncomeCurrentMonth(id: Int, dateBegin: String, dateEnd: String): Flow<List<Fin>>
    fun getCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>

    fun getIncomeExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDetails>>

    fun getIncomeAllList(id: Int): Flow<List<Fin>>
    fun getExpensesAllList(id: Int): Flow<List<Fin>>
    fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>>

    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>

    fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>

    //Warehouse
    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>
    fun getCurrentFoodWarehouse(id: Int): Flow<List<ExpensesTable>>

    fun getCurrentExpensesWarehouse(id: Int): Flow<List<WarehouseData>>

    fun getCurrentBalanceProduct(name: String, id: Long): Flow<PairDataDoubleSting>
    fun getCurrentBalanceProductList(name: String, id: Long): Flow<List<PairDataDoubleSting>>

    //    fun getCurrentExpensesProduct(name: String, id: Long): Flow<Double>
    fun getCurrentExpensesProductList(name: String, id: Long): Flow<List<PairDataDoubleSting>>
    fun getFastAddProduct(id: Long): Flow<List<FastAdd>>

    // Analysis
    fun getAnalysisAddAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisSaleAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisSaleSoldAllTime(id: Int, name: String): Flow<Double>
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double>
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double>
    fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisAddAnimalAllTime(id: Int, name: String): Flow<List<AnimalTitSuff>>
    fun getAnalysisSaleBuyerAllTime(id: Int, name: String): Flow<List<AnalysisSaleBuyerAllTime>>
    fun getAnalysisCostPriceAllTime(id: Int, name: String): Flow<List<Fin>>

    //AnalisisRange
    fun getAnalysisAddAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisSaleAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisWriteOffAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisSaleSoldAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisAddAverageValueAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisAddAnimalAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnimalTitSuff>>

    fun getAnalysisSaleBuyerAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisCostPriceAllTimeRange(
        id: Int, name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>


    suspend fun insertIncubator(item: Incubator)
    suspend fun updateIncubator(item: Incubator)
    fun getAllAnimal(id: Long): Flow<List<DomainAnimalWithCount>>
    fun getAnimal(id: Long): Flow<AnimalTable>
    fun getAnimalCard(id: Int): Flow<DomainAnimalCard>
    fun getTypeAnimal(id: Long): Flow<List<String>>

    //AnimalScreen
    suspend fun insertAnimalTable(animalTable: AnimalTable): Long
    suspend fun updateAnimalTable(animalTable: DomainAnimalTable)
    suspend fun deleteAnimalTable(id: Long)

    suspend fun insertAnimalCountTable(animalCountTable: DomainAnimalCount): Long
    suspend fun insertAnimalSizeTable(animalSizeTable: AnimalSizeTable)
    suspend fun insertAnimalWeightTable(animalWeightTable: AnimalWeightTable)
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable): Long

    suspend fun updateAnimalCountTable(animalCountTable: AnimalCountTable)
    suspend fun updateAnimalSizeTable(animalSizeTable: AnimalSizeTable)
    suspend fun updateAnimalWeightTable(animalWeightTable: AnimalWeightTable)
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    suspend fun deleteAnimalCountTable(animalCountTable: AnimalCountTable)
    suspend fun deleteAnimalSizeTable(animalSizeTable: AnimalSizeTable)
    suspend fun deleteAnimalWeightTable(animalWeightTable: AnimalWeightTable)
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    fun getCountAnimalLimit(id: Int): Flow<AnimalCountTable>
    fun getSizeAnimalLimit(id: Int): Flow<AnimalSizeTable>
    fun getVaccinationAnimalLimit(id: Int): Flow<AnimalVaccinationTable>
    fun getWeightAnimalLimit(id: Int): Flow<AnimalWeightTable>

    fun getCountAnimal(id: Int): Flow<List<DomainIndicatorsVM>>
    fun getSizeAnimal(id: Int): Flow<List<DomainIndicatorsVM>>
    fun getVaccinationAnimal(id: Int): Flow<List<AnimalVaccinationTable>>
    fun getWeightAnimal(id: Int): Flow<List<DomainIndicatorsVM>>
    fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>>


    //Note
    fun getAllNote(id: Int): Flow<List<NoteTable>>
    fun getNote(id: Int): Flow<NoteTable>
    suspend fun insertNote(item: NoteTable)
    suspend fun updateNote(item: NoteTable)
    suspend fun deleteNote(item: NoteTable)

    // NewYear Project
    fun getAnalysisSaleNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisExpensesNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisCountAnimalNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getAnalysisSaleBuyerNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisAddProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisSaleProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisExpensesProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>


    // NewYear
    fun getAnalysisSaleNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisExpensesNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisSaleBuyerNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisAddProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisSaleProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    fun getAnalysisExpensesProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>


    fun getAnalysisCountAnimalNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getChikenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String>

    fun getBestProjectNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

}
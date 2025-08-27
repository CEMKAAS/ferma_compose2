package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.ItemDao
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalSizeTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.animal.AnimalVaccinationTable
import com.zaroslikov.data.room.table.animal.AnimalWeightTable
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.Incubator
import com.zaroslikov.data.room.table.ferma.NoteTable
import com.zaroslikov.data.room.table.ferma.ProjectTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.domain.repository.ItemsRepository
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {

    //==================== Finance ====================
    override fun getCurrentBalance(id: Int): Flow<Double> = itemDao.getCurrentBalance(id)
    override fun getIncome(id: Int): Flow<Double> =
    override fun getExpenses(id: Int): Flow<Double> = itemDao.getExpenses(id)

    override fun getOwnNeed(id: Int): Flow<Double> = itemDao.getOwnNeed(id)
    override fun getScrap(id: Int): Flow<Double> = itemDao.getScrap(id)

    override fun getExpensesMountFin(
        id: Int,
        mount: Int,
        year: Int,
        yearMonth: String
    ): Flow<Double> =
        itemDao.getExpensesMountFin(id, mount, year, yearMonth)


    override fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getExpensesMount(id, dateBegin, dateEnd)

    override fun getOwnNeedMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getOwnNeedMonth(id, dateBegin, dateEnd)

    override fun getScrapMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getScrapMonth(id, dateBegin, dateEnd)

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

    override fun getExpensesAllList(id: Int): Flow<List<Fin>> = itemDao.getExpensesAllList(id)
    override fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>> =
        itemDao.getExpensesAnimalAllList(id)


    override fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>> =
        itemDao.getExpensesCategoryAllList(id)


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

    override fun getCurrentBalanceProduct(name: String, id: Long): Flow<PairDataDoubleSting> =
        itemDao.getCurrentBalanceProduct(name, id)

    override fun getCurrentBalanceProductList(
        name: String,
        id: Long
    ): Flow<List<PairDataDoubleSting>> = itemDao.getCurrentBalanceProductList(name, id)

//    override fun getCurrentExpensesProduct(name: String, id: Long): Flow<Double> =
//        itemDao.getCurrentExpensesProduct(name, id)

    override fun getCurrentExpensesProductList(
        name: String,
        id: Long
    ): Flow<List<PairDataDoubleSting>> = itemDao.getCurrentExpensesProductList(name, id)

    override fun getFastAddProduct(id: Long): Flow<List<FastAdd>> = itemDao.getFastAddProduct(id)

    override fun getAnalysisAddAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisAddAllTime(id, name)

    override fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisWriteOffAllTime(id, name)

    override fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisWriteOffOwnNeedsAllTime(id, name)

    override fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisWriteOffScrapAllTime(id, name)

    override fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double> =
        itemDao.getAnalysisWriteOffOwnNeedsMoneyAllTime(id, name)

    override fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double> =
        itemDao.getAnalysisWriteOffScrapMoneyAllTime(id, name)

    override fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin> =
        itemDao.getAnalysisAddAverageValueAllTime(id, name)

    override fun getAnalysisAddAnimalAllTime(id: Int, name: String): Flow<List<AnimalTitSuff>> =
        itemDao.getAnalysisAddAnimalAllTime(id, name)

    override fun getAnalysisCostPriceAllTime(id: Int, name: String): Flow<List<Fin>> =
        itemDao.getAnalysisCostPriceAllTime(id, name)

    //analysis Range
    override fun getAnalysisAddAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin> = itemDao.getAnalysisAddAllTimeRange(id, name, dateBegin, dateEnd)

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

    override fun getAnalysisCostPriceAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>> =
        itemDao.getAnalysisCostPriceAllTimeRange(id, name, dateBegin, dateEnd)

    override suspend fun insertIncubator(item: Incubator) =
        itemDao.insertIncubator(item)

    override suspend fun updateIncubator(item: Incubator) =
        itemDao.updateIncubator(item)


    override fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>> =
        itemDao.getProductAnimal(name)

    override fun getAnalysisExpensesNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> = itemDao.getAnalysisExpensesNewYearProject(id, dateBegin, dateEnd)

    override fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> = itemDao.getAnalysisWriteOffOwnNeedsNewYearProject(id, dateBegin, dateEnd)

    override fun getAnalysisWriteOffScrapNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> = itemDao.getAnalysisWriteOffScrapNewYearProject(id, dateBegin, dateEnd)

    override fun getAnalysisCountAnimalNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> = itemDao.getAnalysisCountAnimalNewYearProject(id, dateBegin, dateEnd)

    override fun getAnalysisAddProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> =
        itemDao.getAnalysisAddProductNewYearProject(id, dateBegin, dateEnd)

    override fun getAnalysisExpensesProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> =
        itemDao.getAnalysisExpensesProductNewYearProject(id, dateBegin, dateEnd)

    override fun getAnalysisSaleNewYear(dateBegin: String, dateEnd: String): Flow<Double> =


    override fun getAnalysisExpensesNewYear(dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getAnalysisExpensesNewYear(dateBegin, dateEnd)

    override fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> = itemDao.getAnalysisWriteOffOwnNeedsNewYear(dateBegin, dateEnd)

    override fun getAnalysisWriteOffScrapNewYear(dateBegin: String, dateEnd: String): Flow<Double> =
        itemDao.getAnalysisWriteOffScrapNewYear(dateBegin, dateEnd)

    override fun getAnalysisAddProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> =
        itemDao.getAnalysisAddProductNewYear(dateBegin, dateEnd)

    override fun getAnalysisExpensesProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> =
        itemDao.getAnalysisExpensesProductNewYear(dateBegin, dateEnd)

    override fun getAnalysisCountAnimalNewYear(dateBegin: String, dateEnd: String): Flow<Int> =
        itemDao.getAnalysisCountAnimalNewYear(dateBegin, dateEnd)

    override fun getIncubatorCountNewYear(dateBegin: String, dateEnd: String): Flow<Int> =
        itemDao.getIncubatorCountNewYear(dateBegin, dateEnd)

    override fun getEggInIncubatorNewYear(dateBegin: String, dateEnd: String): Flow<Int> =
        itemDao.getEggInIncubatorNewYear(dateBegin, dateEnd)

    override fun getChikenInIncubatorNewYear(dateBegin: String, dateEnd: String): Flow<Int> =
        itemDao.getChikenInIncubatorNewYear(dateBegin, dateEnd)

    override fun getTypeIncubatorNewYear(dateBegin: String, dateEnd: String): Flow<String> =
        itemDao.getTypeIncubatorNewYear(dateBegin, dateEnd)

    override fun getBestProjectNewYear(dateBegin: String, dateEnd: String): Flow<Fin> =
        itemDao.getBestProjectNewYear(dateBegin, dateEnd)

}
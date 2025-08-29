package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.SaleDao
import com.zaroslikov.data.room.mapper.dto.sale.toBrieflySaleDomain
import com.zaroslikov.data.room.mapper.dto.sale.toDomainBuyerPrice
import com.zaroslikov.data.room.mapper.dto.sale.toTitleSaleDomain
import com.zaroslikov.data.room.mapper.dto.shared.toDomainCategoryPrice
import com.zaroslikov.data.room.mapper.dto.shared.toDomainTitleSuffixPrice
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.dto.sale.TitleSaleDomain
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map


class SaleRepositoryImpl @Inject constructor(private val saleDao: SaleDao) : SaleRepository {

    override fun getAllSaleItems(id: Long): Flow<List<DomainSaleTable>> {
        return saleDao.getAllSaleItems(id).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemSale(id: Long): Flow<DomainSaleTable> {
        return saleDao.getItemSale(id).map { it.toDomainMap() }
    }

    override fun getItemSaleIdCountAnimal(id: Long): Flow<DomainSaleTable> {
        return saleDao.getItemSaleIdCountAnimal(id).map { it.toDomainMap() }
    }

    override fun getBrieflyItemSale(id: Long): Flow<List<BrieflySaleDomain>> {
        return saleDao.getBrieflyItemSale(id).map { it -> it.map { it.toBrieflySaleDomain() } }
    }

    override fun getBrieflyDetailsItemSale(
        id: Long,
        name: String
    ): Flow<List<DomainSaleTable>> {
        return saleDao.getBrieflyDetailsItemSale(id, name).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemsTitleSaleList(id: Long): Flow<List<TitleSaleDomain>> {
        return saleDao.getItemsTitleSaleList(id).map { it -> it.map { it.toTitleSaleDomain() } }
    }

    override fun getItemsCategorySaleList(id: Long): Flow<List<String>> {
        return saleDao.getItemsCategorySaleList(id)
    }

    override fun getItemsBuyerSaleList(id: Long): Flow<List<String>> {
        return saleDao.getItemsBuyerSaleList(id)
    }

    override suspend fun insertSale(item: DomainSaleTable) {
        return saleDao.insertSale(item.toRoomMap())
    }

    override suspend fun updateSale(item: DomainSaleTable) {
        return saleDao.updateSale(item.toRoomMap())
    }

    override suspend fun deleteSaleById(id: Long) {
        return saleDao.deleteSaleById(id)
    }

    override fun getIncome(id: Long): Flow<Double> {
        return saleDao.getIncome(id)
    }

    override fun getIncomeMountFin(
        id: Long,
        month: Int,
        year: Int
    ): Flow<Double> {
        return saleDao.getIncomeMountFin(id, month, year)
    }

    override fun getIncomeMount(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return saleDao.getIncomeMount(id, dateBegin, dateEnd)
    }

    override fun getCategoryIncomeCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCategoryPrice>> {
        return saleDao.getCategoryIncomeCurrentMonth(id, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainCategoryPrice() } }
    }

    override fun getIncomeAllList(id: Long): Flow<List<DomainTitleSuffixPrice>> {
        return saleDao.getIncomeAllList(id).map { it -> it.map { it.toDomainTitleSuffixPrice() } }
    }

    override fun getIncomeCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>> {
        return saleDao.getIncomeCategoryAllList(id)
            .map { it -> it.map { it.toDomainCategoryPrice() } }
    }

    override fun getProductListCategoryIncomeCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<DomainTitleSuffixPrice>> {
        return saleDao.getProductListCategoryIncomeCurrentMonth(id, dateBegin, dateEnd, category)
            .map { it -> it.map { it.toDomainTitleSuffixPrice() } }
    }

    override fun getAnalysisSaleAllTime(
        id: Long,
        name: String
    ): Flow<DomainTitleSuffixPrice> {
        return saleDao.getAnalysisSaleAllTime(id, name).map { it.toDomainTitleSuffixPrice() }
    }

    override fun getAnalysisSaleSoldAllTime(
        id: Long,
        name: String
    ): Flow<Double> {
        return saleDao.getAnalysisSaleSoldAllTime(id, name)
    }

    override fun getAnalysisSaleBuyerAllTime(
        id: Long,
        name: String
    ): Flow<List<DomainBuyerPrice>> {
        return saleDao.getAnalysisSaleBuyerAllTime(id, name)
            .map { it -> it.map { it.toDomainBuyerPrice() } }
    }

    override fun getAnalysisSaleAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainTitleSuffixPrice> {
        return saleDao.getAnalysisSaleAllTimeRange(id, name, dateBegin, dateEnd)
            .map { it.toDomainTitleSuffixPrice() }
    }

    override fun getAnalysisSaleSoldAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return saleDao.getAnalysisSaleSoldAllTimeRange(id, name, dateBegin, dateEnd)
    }

    override fun getAnalysisSaleBuyerAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>> {
        return saleDao.getAnalysisSaleBuyerAllTimeRange(id, name, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainBuyerPrice() } }
    }

    override fun getAnalysisSaleNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return saleDao.getAnalysisSaleNewYearProject(id, dateBegin, dateEnd)
    }

    override fun getAnalysisSaleBuyerNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>> {
        return saleDao.getAnalysisSaleBuyerNewYearProject(id, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainBuyerPrice() } }
    }

    override fun getAnalysisSaleProductNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>> {
        return saleDao.getAnalysisSaleProductNewYearProject(id, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainBuyerPrice() } }
    }

    override fun getAnalysisSaleNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return saleDao.getAnalysisSaleNewYear(dateBegin, dateEnd)
    }

    override fun getAnalysisSaleBuyerNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>> {
        return saleDao.getAnalysisSaleBuyerNewYear(dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainBuyerPrice() } }
    }

    override fun getAnalysisSaleProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>> {
        return saleDao.getAnalysisSaleProductNewYear(dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainBuyerPrice() } }
    }

}
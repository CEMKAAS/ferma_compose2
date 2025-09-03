package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import kotlinx.coroutines.flow.Flow

interface SaleRepository {
    fun getAllSaleItems(id: Long): Flow<List<DomainSaleTable>>
    fun getItemSale(id: Long): Flow<DomainSaleTable>
    fun getItemSaleIdCountAnimal(id: Long): Flow<DomainSaleTable>
    fun getBrieflyItemSale(id: Long): Flow<List<BrieflySaleDomain>>
    fun getBrieflyDetailsItemSale(id: Long, name: String): Flow<List<DomainSaleTable>>
    fun getItemsTitleSaleList(id: Long): Flow<List<DomainTitleSuffixCategory>>
    fun getItemsCategorySaleList(id: Long): Flow<List<String>>
    fun getItemsBuyerSaleList(id: Long): Flow<List<String>>
    suspend fun insertSale(item: DomainSaleTable)
    suspend fun updateSale(item: DomainSaleTable)
    suspend fun deleteSaleById(id: Long)

    fun getIncome(id: Long): Flow<Double>
    fun getIncomeMountFin(id: Long, month: Int, year: Int): Flow<Double>
    fun getIncomeMount(id: Long, dateBegin: String, dateEnd: String): Flow<Double>
    fun getCategoryIncomeCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCategoryPrice>>

    fun getIncomeAllList(id: Long): Flow<List<DomainTitleSuffixPrice>>
    fun getIncomeCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>>
    fun getProductListCategoryIncomeCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<DomainTitleSuffixPrice>> //maybe

    fun getAnalysisSaleAllTime(id: Long, name: String): Flow<DomainTitleSuffixPrice>
    fun getAnalysisSaleSoldAllTime(id: Long, name: String): Flow<Double>
    fun getAnalysisSaleBuyerAllTime(id: Long, name: String): Flow<List<DomainBuyerPrice>>
    fun getAnalysisSaleAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainTitleSuffixPrice>

    fun getAnalysisSaleSoldAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisSaleBuyerAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>>

    fun getAnalysisSaleNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisSaleBuyerNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>>

    fun getAnalysisSaleProductNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>>

    fun getAnalysisSaleNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisSaleBuyerNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>>

    fun getAnalysisSaleProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainBuyerPrice>> // TODO Может нужно заменить?
}
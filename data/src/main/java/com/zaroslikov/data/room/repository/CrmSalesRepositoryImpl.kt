package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.CrmSaleDao
import com.zaroslikov.data.room.mapper.dto.crm.toDomainCrmBuyerProduct
import com.zaroslikov.data.room.mapper.dto.crm.toDomainCrmBuyerStats
import com.zaroslikov.data.room.mapper.dto.crm.toDomainCrmMonthRevenue
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerProduct
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import com.zaroslikov.domain.repository.crm.CrmSalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CrmSalesRepositoryImpl @Inject constructor(private val crmSaleDao: CrmSaleDao) :
    CrmSalesRepository {

    override fun getBuyers(idPT: Long): Flow<List<String>> {
        return crmSaleDao.getBuyers(idPT)
    }

    override fun getBuyerStats(idPT: Long): Flow<List<DomainCrmBuyerStats>> {
        return crmSaleDao.getBuyerStats(idPT).map { list -> list.map { it.toDomainCrmBuyerStats() } }
    }

    override fun getBuyerSales(idPT: Long, buyer: String): Flow<List<DomainSaleTable>> {
        return crmSaleDao.getBuyerSales(idPT, buyer).map { list -> list.map { it.toDomainMap() } }
    }

    override fun getBuyerProducts(idPT: Long, buyer: String): Flow<List<DomainCrmBuyerProduct>> {
        return crmSaleDao.getBuyerProducts(idPT, buyer)
            .map { list -> list.map { it.toDomainCrmBuyerProduct() } }
    }

    override fun getMonthlyRevenue(
        idPT: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCrmMonthRevenue>> {
        return crmSaleDao.getMonthlyRevenue(idPT, dateBegin, dateEnd)
            .map { list -> list.map { it.toDomainCrmMonthRevenue() } }
    }

    override fun getMonthlyRevenueByBuyer(
        idPT: Long,
        buyer: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCrmMonthRevenue>> {
        return crmSaleDao.getMonthlyRevenueByBuyer(idPT, buyer, dateBegin, dateEnd)
            .map { list -> list.map { it.toDomainCrmMonthRevenue() } }
    }

    override fun getSalesWithoutBuyerCount(idPT: Long): Flow<Int> {
        return crmSaleDao.getSalesWithoutBuyerCount(idPT)
    }
}

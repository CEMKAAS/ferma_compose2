package com.zaroslikov.domain.repository.crm

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerProduct
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import kotlinx.coroutines.flow.Flow

/** Агрегаты по продажам для CRM. Покупатель нормализуется через TRIM. */
interface CrmSalesRepository {
    fun getBuyers(idPT: Long): Flow<List<String>>
    fun getBuyerStats(idPT: Long): Flow<List<DomainCrmBuyerStats>>
    fun getBuyerSales(idPT: Long, buyer: String): Flow<List<DomainSaleTable>>
    fun getBuyerProducts(idPT: Long, buyer: String): Flow<List<DomainCrmBuyerProduct>>
    fun getMonthlyRevenue(idPT: Long, dateBegin: String, dateEnd: String): Flow<List<DomainCrmMonthRevenue>>
    fun getMonthlyRevenueByBuyer(idPT: Long, buyer: String, dateBegin: String, dateEnd: String): Flow<List<DomainCrmMonthRevenue>>
    fun getSalesWithoutBuyerCount(idPT: Long): Flow<Int>
}

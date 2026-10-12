package com.zaroslikov.crm.domain.usecase

import com.zaroslikov.crm.domain.model.crmToday
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.domain.repository.crm.CrmSalesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Заводит клиентов CRM по покупателям из продаж проекта. */
class CrmSyncClientsUseCase @Inject constructor(
    private val crmSalesRepository: CrmSalesRepository,
    private val crmClientRepository: CrmClientRepository
) {
    suspend operator fun invoke(idPT: Long) {
        val stats = crmSalesRepository.getBuyerStats(idPT).first()
        crmClientRepository.syncFromSales(idPT, stats, crmToday())
    }
}

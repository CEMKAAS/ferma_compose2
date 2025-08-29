package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import kotlinx.coroutines.flow.Flow

interface WarehouseRepository {
    fun getCurrentBalanceWarehouse(id: Long): Flow<List<DomainTitleCountSuffix>>
    fun getCurrentExpensesWarehouse(id: Long): Flow<List<DomainTitleCountSuffix>>
    fun getCurrentBalanceProduct(name: String, id: Long): Flow<DomainCountSuffix>
    fun getCurrentBalanceProductList(name: String, id: Long): Flow<List<DomainCountSuffix>>
    fun getCurrentExpensesProductList(name: String, id: Long): Flow<List<DomainCountSuffix>>
}
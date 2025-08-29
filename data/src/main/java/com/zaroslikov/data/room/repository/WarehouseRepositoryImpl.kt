package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.WarehouseDao
import com.zaroslikov.data.room.mapper.dto.shared.toDomainCountSuffix
import com.zaroslikov.data.room.mapper.dto.shared.toDomainTitleCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.repository.WarehouseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WarehouseRepositoryImpl @Inject constructor(private val warehouseDao: WarehouseDao) :
    WarehouseRepository {
    override fun getCurrentBalanceWarehouse(id: Long): Flow<List<DomainTitleCountSuffix>> {
        return warehouseDao.getCurrentBalanceWarehouse(id)
            .map { it -> it.map { it.toDomainTitleCountSuffix() } }
    }

    override fun getCurrentExpensesWarehouse(id: Long): Flow<List<DomainTitleCountSuffix>> {
        return warehouseDao.getCurrentExpensesWarehouse(id)
            .map { it -> it.map { it.toDomainTitleCountSuffix() } }
    }

    override fun getCurrentBalanceProduct(
        name: String,
        id: Long
    ): Flow<DomainCountSuffix> {
        return warehouseDao.getCurrentBalanceProduct(name, id).map { it.toDomainCountSuffix() }
    }

    override fun getCurrentBalanceProductList(
        name: String,
        id: Long
    ): Flow<List<DomainCountSuffix>> {
        return warehouseDao.getCurrentBalanceProductList(name, id)
            .map { it -> it.map { it.toDomainCountSuffix() } }
    }

    override fun getCurrentExpensesProductList(
        name: String,
        id: Long
    ): Flow<List<DomainCountSuffix>> {
        return warehouseDao.getCurrentExpensesProductList(name, id)
            .map { it -> it.map { it.toDomainCountSuffix() } }
    }
}
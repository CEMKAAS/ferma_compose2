package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AddDao
import com.zaroslikov.data.room.dao.RecyclingDao
import com.zaroslikov.data.room.dao.SaleDao
import com.zaroslikov.data.room.dao.WriteOffDao
import com.zaroslikov.data.room.mapper.dto.trace.toDomainMap
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.table.ferma.RecyclingTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.dto.trace.TraceProductionDto
import com.zaroslikov.domain.models.dto.trace.DomainProductTrace
import com.zaroslikov.domain.repository.ProductTraceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Собирает все операции продукта из четырёх таблиц. Схема БД не меняется —
 * только запросы на чтение, поэтому отдельный DAO не нужен.
 */
class ProductTraceRepositoryImpl @Inject constructor(
    private val addDao: AddDao,
    private val saleDao: SaleDao,
    private val writeOffDao: WriteOffDao,
    private val recyclingDao: RecyclingDao
) : ProductTraceRepository {

    override fun getProductTrace(idPT: Long, title: String): Flow<DomainProductTrace> =
        combine(
            addDao.getTraceAddList(idPT, title),
            saleDao.getTraceSaleList(idPT, title),
            writeOffDao.getTraceWriteOffList(idPT, title),
            recyclingDao.getRecyclingUsingProduct(idPT, title),
            recyclingDao.getRecyclingProducingProduct(idPT, title),
            recyclingDao.getIngredientsForProduct(idPT, title)
        ) { values: Array<Any?> ->
            @Suppress("UNCHECKED_CAST")
            DomainProductTrace(
                production = (values[0] as List<TraceProductionDto>).map { it.toDomainMap() },
                sales = (values[1] as List<SaleTable>).map { it.toDomainMap() },
                writeOffs = (values[2] as List<WriteOffTable>).map { it.toDomainMap() },
                recyclingUsingProduct = (values[3] as List<RecyclingTable>).map { it.toDomainMap() },
                recyclingProducingProduct = (values[4] as List<RecyclingTable>).map { it.toDomainMap() },
                producingIngredients = (values[5] as List<WriteOffTable>).map { it.toDomainMap() }
            )
        }
}

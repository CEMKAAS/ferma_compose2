package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.TemplateDao
import com.zaroslikov.data.room.mapper.dto.template.toDomainAddTemplateDto
import com.zaroslikov.data.room.mapper.dto.template.toDomainExpensesTemplateDto
import com.zaroslikov.data.room.mapper.dto.template.toDomainSaleTemplateDto
import com.zaroslikov.data.room.mapper.dto.template.toDomainWriteOffTemplateDto
import com.zaroslikov.data.room.mapper.table.template.toAddTemplateTable
import com.zaroslikov.data.room.mapper.table.template.toDomainAddTemplateTable
import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainExpensesTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainSaleTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainWriteOffTemplateDto
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.template.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(private val templateDao: TemplateDao) :
    TemplateRepository {
    override fun getAllAddTemplateTableForExport(): Flow<List<DomainTemplateTable>> {
        return templateDao.getAllAddTemplateTableForExport()
            .map { it -> it.map { it.toDomainAddTemplateTable() } }
    }

    override suspend fun insertAllAddTemplateTable(addTemplateTable: List<DomainTemplateTable>) {
        return templateDao.insertAllAddTemplateTable(addTemplateTable.map { it.toAddTemplateTable() })
    }

    override suspend fun deleteAllAddTemplateTable() {
        return templateDao.deleteAllAddTemplateTable()
    }

    override suspend fun clearAndInsertAddTemplateTableForImport(addTemplateTable: List<DomainTemplateTable>) {
        return templateDao.clearAndInsertAddTemplateTableForImport(addTemplateTable.map { it.toAddTemplateTable() })
    }

    override suspend fun setPinById(pin: Boolean, id: Long) {
        return templateDao.setPinById(pin, id)
    }

    override fun getTemplateItem(id: Long): Flow<DomainTemplateTable?> {
        return templateDao.getAddTemplateItem(id).map { it?.toDomainAddTemplateTable() }
    }

    override fun getAllAddTemplateItems(id: Long): Flow<List<DomainAddTemplateDto>> {
        return templateDao.getAllAddTemplateItems(id)
            .map { it -> it.map { it.toDomainAddTemplateDto() } }
    }

    override fun getAllSaleTemplateItems(id: Long): Flow<List<DomainSaleTemplateDto>> {
        return templateDao.getAllSaleTemplateItems(id)
            .map { it -> it.map { it.toDomainSaleTemplateDto() } }
    }

    override fun getAllWriteOffTemplateItems(id: Long): Flow<List<DomainWriteOffTemplateDto>> {
        return templateDao.getAllWriteOffTemplateItems(id)
            .map { it -> it.map { it.toDomainWriteOffTemplateDto() } }
    }

    override fun getAllExpensesTemplateItems(id: Long): Flow<List<DomainExpensesTemplateDto>> {
        return templateDao.getAllExpensesTemplateItems(id).map { it ->
            it.map { it.toDomainExpensesTemplateDto() }
        }
    }

    override suspend fun insert(item: DomainTemplateTable) {
        return templateDao.insert(item.toAddTemplateTable())
    }

    override suspend fun update(item: DomainTemplateTable) {
        return templateDao.update(item.toAddTemplateTable())
    }

    override suspend fun deleteAddTemplateItemById(id: Long) {
        return templateDao.deleteTemplateItemById(id)
    }

}
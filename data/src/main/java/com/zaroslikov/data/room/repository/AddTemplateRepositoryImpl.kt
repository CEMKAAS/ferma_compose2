package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.template.TemplateDao
import com.zaroslikov.data.room.mapper.dto.add.toDomainAddTemplateDto
import com.zaroslikov.data.room.mapper.table.template.toAddTemplateTable
import com.zaroslikov.data.room.mapper.table.template.toDomainAddTemplateTable
import com.zaroslikov.domain.models.dto.add.DomainAddTemplateDto
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.template.AddTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddTemplateRepositoryImpl @Inject constructor(private val templateDao: TemplateDao) :
    AddTemplateRepository {
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

    override fun getAddTemplateItem(id: Long): Flow<DomainTemplateTable> {
        return templateDao.getAddTemplateItem(id).map { it.toDomainAddTemplateTable() }
    }

    override fun getAllAddTemplateItems(id: Long): Flow<List<DomainAddTemplateDto>> {
        return templateDao.getAllAddTemplateItems(id)
            .map { it -> it.map { it.toDomainAddTemplateDto() } }
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
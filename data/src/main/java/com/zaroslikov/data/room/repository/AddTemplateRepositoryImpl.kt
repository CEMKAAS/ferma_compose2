package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.template.AddTemplateDao
import com.zaroslikov.data.room.mapper.dto.add.toDomainAddTemplateDto
import com.zaroslikov.data.room.mapper.table.template.toAddTemplateTable
import com.zaroslikov.data.room.mapper.table.template.toDomainAddTemplateTable
import com.zaroslikov.domain.models.dto.add.DomainAddTemplateDto
import com.zaroslikov.domain.models.table.template.DomainAddTemplateTable
import com.zaroslikov.domain.repository.template.AddTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddTemplateRepositoryImpl @Inject constructor(private val addTemplateDao: AddTemplateDao) :
    AddTemplateRepository {
    override fun getAllAddTemplateTableForExport(): Flow<List<DomainAddTemplateTable>> {
        return addTemplateDao.getAllAddTemplateTableForExport()
            .map { it -> it.map { it.toDomainAddTemplateTable() } }
    }

    override suspend fun insertAllAddTemplateTable(addTemplateTable: List<DomainAddTemplateTable>) {
        return addTemplateDao.insertAllAddTemplateTable(addTemplateTable.map { it.toAddTemplateTable() })
    }

    override suspend fun deleteAllAddTemplateTable() {
        return addTemplateDao.deleteAllAddTemplateTable()
    }

    override suspend fun clearAndInsertAddTemplateTableForImport(addTemplateTable: List<DomainAddTemplateTable>) {
        return addTemplateDao.clearAndInsertAddTemplateTableForImport(addTemplateTable.map { it.toAddTemplateTable() })
    }

    override fun getAddTemplateItem(id: Long): Flow<DomainAddTemplateTable> {
        return addTemplateDao.getAddTemplateItem(id).map { it.toDomainAddTemplateTable() }
    }

    override fun getAllAddTemplateItems(id: Long): Flow<List<DomainAddTemplateDto>> {
        return addTemplateDao.getAllAddTemplateItems(id)
            .map { it -> it.map { it.toDomainAddTemplateDto() } }
    }

    override suspend fun insert(item: DomainAddTemplateTable) {
        return addTemplateDao.insert(item.toAddTemplateTable())
    }

    override suspend fun update(item: DomainAddTemplateTable) {
        return addTemplateDao.update(item.toAddTemplateTable())
    }

    override suspend fun deleteAddTemplateItemById(id: Long) {
        return addTemplateDao.deleteAddTemplateItemById(id)
    }

}
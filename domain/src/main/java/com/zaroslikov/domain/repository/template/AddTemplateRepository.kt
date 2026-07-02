package com.zaroslikov.domain.repository.template

import com.zaroslikov.domain.models.dto.add.DomainAddTemplateDto
import com.zaroslikov.domain.models.table.template.DomainAddTemplateTable
import kotlinx.coroutines.flow.Flow

interface AddTemplateRepository {
    fun getAllAddTemplateTableForExport(): Flow<List<DomainAddTemplateTable>>

    suspend fun insertAllAddTemplateTable(addTemplateTable: List<DomainAddTemplateTable>)

    suspend fun deleteAllAddTemplateTable()

    suspend fun clearAndInsertAddTemplateTableForImport(addTemplateTable: List<DomainAddTemplateTable>) {
        deleteAllAddTemplateTable()
        insertAllAddTemplateTable(addTemplateTable)
    }

    fun getAddTemplateItem(id: Long): Flow<DomainAddTemplateTable>


    fun getAllAddTemplateItems(id: Long): Flow<List<DomainAddTemplateDto>>


    suspend fun insert(item: DomainAddTemplateTable)

    suspend fun update(item: DomainAddTemplateTable)

    suspend fun deleteAddTemplateItemById(id: Long)
}
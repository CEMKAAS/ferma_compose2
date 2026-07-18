package com.zaroslikov.domain.repository.template

import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import kotlinx.coroutines.flow.Flow

interface AddTemplateRepository {
    fun getAllAddTemplateTableForExport(): Flow<List<DomainTemplateTable>>

    suspend fun insertAllAddTemplateTable(addTemplateTable: List<DomainTemplateTable>)

    suspend fun deleteAllAddTemplateTable()

    suspend fun clearAndInsertAddTemplateTableForImport(addTemplateTable: List<DomainTemplateTable>) {
        deleteAllAddTemplateTable()
        insertAllAddTemplateTable(addTemplateTable)
    }

   suspend fun setPinById(pin: Boolean, id: Long)
    fun getAddTemplateItem(id: Long): Flow<DomainTemplateTable?>

    fun getAllAddTemplateItems(id: Long): Flow<List<DomainAddTemplateDto>>


    suspend fun insert(item: DomainTemplateTable)

    suspend fun update(item: DomainTemplateTable)

    suspend fun deleteAddTemplateItemById(id: Long)
}
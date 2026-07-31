package com.zaroslikov.domain.repository.template

import com.zaroslikov.domain.models.dto.template.DomainAddTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainExpensesTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainSaleTemplateDto
import com.zaroslikov.domain.models.dto.template.DomainWriteOffTemplateDto
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getAllAddTemplateTableForExport(): Flow<List<DomainTemplateTable>>

    suspend fun insertAllAddTemplateTable(addTemplateTable: List<DomainTemplateTable>)

    suspend fun deleteAllAddTemplateTable()

    suspend fun clearAndInsertAddTemplateTableForImport(addTemplateTable: List<DomainTemplateTable>) {
        deleteAllAddTemplateTable()
        insertAllAddTemplateTable(addTemplateTable)
    }

    suspend fun setPinById(pin: Boolean, id: Long)
    fun getTemplateItem(id: Long): Flow<DomainTemplateTable?>

    fun getAllAddTemplateItems(id: Long): Flow<List<DomainAddTemplateDto>>
    fun getAllSaleTemplateItems(id: Long): Flow<List<DomainSaleTemplateDto>>
    fun getAllWriteOffTemplateItems(id: Long): Flow<List<DomainWriteOffTemplateDto>>
    fun getAllExpensesTemplateItems(id: Long): Flow<List<DomainExpensesTemplateDto>>

    suspend fun insert(item: DomainTemplateTable)

    suspend fun update(item: DomainTemplateTable)

    suspend fun deleteAddTemplateItemById(id: Long)
}
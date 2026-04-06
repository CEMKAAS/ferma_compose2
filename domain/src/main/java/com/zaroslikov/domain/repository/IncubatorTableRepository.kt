package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainIncubatorTable
import com.zaroslikov.domain.models.table.DomainProjectTable
import kotlinx.coroutines.flow.Flow

interface IncubatorTableRepository {
    fun getAllIncubatorTableForExport(): Flow<List<DomainIncubatorTable>>
    suspend fun clearAndInsertIncubatorTableForImport(domainIncubatorTable: List<DomainIncubatorTable>)
    fun getAllIncubator(): Flow<List<DomainIncubatorTable>>
    fun getIncubatorByIdPT(idPT: Long): Flow<DomainIncubatorTable>
    fun getIncubatorById(id: Long): Flow<DomainIncubatorTable>
    suspend fun insertIncubator(domainIncubatorTable: DomainIncubatorTable)
    suspend fun insertIncubatorLong(domainIncubatorTable: DomainIncubatorTable): Long
    suspend fun updateIncubator(domainIncubatorTable: DomainIncubatorTable)
    suspend fun deleteIncubator(domainIncubatorTable: DomainIncubatorTable)
    fun getBrandIncubatorList(): Flow<List<String>>
    fun getModelIncubatorList(): Flow<List<String>>
}
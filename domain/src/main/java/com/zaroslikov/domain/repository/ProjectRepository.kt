package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainProjectTable
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProject(): Flow<List<DomainProjectTable>>
    fun getProject(id: Long): Flow<DomainProjectTable>
    suspend fun getIncubatorListArh6(type: String): Flow<List<DomainProjectTable>>
    fun getProjectListAct(): Flow<List<DomainProjectTable>>
    fun getCountRowIncubator(): Flow<Int>
    fun getCountRowProject(): Flow<Int>
    fun getLastProject(): Flow<Int>
    suspend fun insertProject(projectTable: DomainProjectTable)
    suspend fun insertProjectLong(projectTable: DomainProjectTable): Long
    suspend fun updateProject(item: DomainProjectTable)
    suspend fun deleteProject(item: DomainProjectTable)
}
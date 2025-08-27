package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.ProjectDao
import com.zaroslikov.data.room.mapper.table.toDomainProjectTable
import com.zaroslikov.data.room.mapper.table.toProjectTable
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(private val projectDao: ProjectDao) : ProjectRepository {
    override fun getAllProject(): Flow<List<DomainProjectTable>> {
        return projectDao.getAllProject().map { it -> it.map { it.toDomainProjectTable() } }
    }

    override fun getProject(id: Long): Flow<DomainProjectTable> {
        return projectDao.getProject(id).map { it.toDomainProjectTable() }
    }

    override suspend fun getIncubatorListArh6(type: String): Flow<List<DomainProjectTable>> {
        return projectDao.getIncubatorListArh6(type)
            .map { it -> it.map { it.toDomainProjectTable() } }
    }

    override fun getProjectListAct(): Flow<List<DomainProjectTable>> {
        return projectDao.getProjectListAct().map { it -> it.map { it.toDomainProjectTable() } }
    }

    override fun getCountRowIncubator(): Flow<Int> {
        return projectDao.getCountRowIncubator()
    }

    override fun getCountRowProject(): Flow<Int> {
        return projectDao.getCountRowProject()
    }

    override fun getLastProject(): Flow<Int> {
        return projectDao.getLastProject()
    }

    override suspend fun insertProject(projectTable: DomainProjectTable) {
        return projectDao.insertProject(projectTable.toProjectTable())
    }

    override suspend fun insertProjectLong(projectTable: DomainProjectTable): Long {
        return projectDao.insertProjectLong(projectTable.toProjectTable())
    }

    override suspend fun updateProject(item: DomainProjectTable) {
        return projectDao.updateProject(item.toProjectTable())
    }

    override suspend fun deleteProject(item: DomainProjectTable) {
        return projectDao.deleteProject(item.toProjectTable())
    }
}
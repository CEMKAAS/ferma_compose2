package com.zaroslikov.crm.data.room.repository

import com.zaroslikov.crm.data.room.dao.CrmContactDao
import com.zaroslikov.crm.data.room.mapper.toDomain
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.repository.CrmContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CrmContactRepositoryImpl @Inject constructor(
    private val crmContactDao: CrmContactDao
) : CrmContactRepository {

    override fun getContacts(clientId: Long): Flow<List<DomainCrmContact>> {
        return crmContactDao.getContacts(clientId).map { list -> list.map { it.toDomain() } }
    }

    override fun getContactsForProject(idPT: Long): Flow<List<DomainCrmContact>> {
        return crmContactDao.getContactsForProject(idPT).map { list -> list.map { it.toDomain() } }
    }
}

package com.zaroslikov.crm.domain.repository

import com.zaroslikov.crm.domain.model.DomainCrmContact
import kotlinx.coroutines.flow.Flow

/** Только чтение: контакты записываются вместе с клиентом через [CrmClientRepository]. */
interface CrmContactRepository {

    fun getContacts(clientId: Long): Flow<List<DomainCrmContact>>

    /** Все контакты клиентов проекта — для поиска по списку клиентов. */
    fun getContactsForProject(idPT: Long): Flow<List<DomainCrmContact>>
}

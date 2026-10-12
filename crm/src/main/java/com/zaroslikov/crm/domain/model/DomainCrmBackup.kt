package com.zaroslikov.crm.domain.model

import kotlinx.serialization.Serializable

/** Содержимое `crm_database` для общего бэкапа приложения. */
@Serializable
data class DomainCrmBackup(
    val clients: List<DomainCrmClient> = emptyList(),
    val contacts: List<DomainCrmContact> = emptyList(),
    val interactions: List<DomainCrmInteraction> = emptyList(),
    val tasks: List<DomainCrmTask> = emptyList()
)

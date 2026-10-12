package com.zaroslikov.crm.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DomainCrmContact(
    val id: Long = 0,
    val clientId: Long,
    val type: CrmContactType,
    val value: String,
    val isPrimary: Boolean = false
)

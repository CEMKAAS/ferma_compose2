package com.zaroslikov.crm.domain.model

import kotlinx.serialization.Serializable

/** Взаимодействие с клиентом; [date] в формате `yyyy-MM-dd`. */
@Serializable
data class DomainCrmInteraction(
    val id: Long = 0,
    val clientId: Long,
    val type: CrmInteractionType,
    val date: String,
    val note: String
)

package com.zaroslikov.domain.models.table.project

import kotlinx.serialization.Serializable

@Serializable
data class DomainTimeNotificationProject(
    val id: Long = 0,
    val time: String,
    val note: String?,
    val projectId: Long = 0
)

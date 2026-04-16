package com.zaroslikov.domain.models.dto.project

data class DomainTimeNotificationProjectDto(
    val nameProject: String,
    val time: String,
    val note: String?,
    val projectId: Long
)

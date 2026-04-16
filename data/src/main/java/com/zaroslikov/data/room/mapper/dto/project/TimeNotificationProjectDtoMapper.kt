package com.zaroslikov.data.room.mapper.dto.project

import com.zaroslikov.data.room.dto.project.TimeNotificationProjectDto
import com.zaroslikov.domain.models.dto.project.DomainTimeNotificationProjectDto

fun TimeNotificationProjectDto.toDomainTimeNotificationProjectDto(): DomainTimeNotificationProjectDto {
    return DomainTimeNotificationProjectDto(
        nameProject = this.nameProject,
        time = this.time,
        note = this.note,
        projectId = this.projectId,
    )
}
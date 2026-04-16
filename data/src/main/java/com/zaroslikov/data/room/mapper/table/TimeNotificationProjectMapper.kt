package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.project.TimeNotificationProjectTable
import com.zaroslikov.domain.models.table.project.DomainTimeNotificationProject

fun TimeNotificationProjectTable.toDomainTimeNotificationProject(): DomainTimeNotificationProject {
    return DomainTimeNotificationProject(
        id = this.id,
        time = this.time,
        note = this.note,
        projectId = this.projectId,
    )
}

fun DomainTimeNotificationProject.toTimeNotificationProjectTable(): TimeNotificationProjectTable {
    return TimeNotificationProjectTable(
        id = this.id,
        time = this.time,
        note = this.note,
        projectId = this.projectId,
    )
}
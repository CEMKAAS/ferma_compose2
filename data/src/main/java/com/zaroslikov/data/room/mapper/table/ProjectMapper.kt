package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.table.DomainProjectTable

fun ProjectTable.toDomainProjectTable(): DomainProjectTable {
    return DomainProjectTable(
        id = id,
        title = title,
        date = date,
        dateEnd = dateEnd,
        mode = mode,
        archive = archive
    )
}

fun DomainProjectTable.toProjectTable(): ProjectTable {
    return ProjectTable(
        id = id,
        title = title,
        date = date,
        dateEnd = dateEnd,
        mode = mode,
        archive = archive
    )
}
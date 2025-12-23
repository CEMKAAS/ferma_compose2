package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.table.DomainProjectTable

fun ProjectTable.toDomainProjectTable(): DomainProjectTable {
    return DomainProjectTable(
        id = this.id,
        titleProject = this.titleProject,
        type = this.type,
        data = this.data,
        eggAll = this.eggAll,
        eggAllEND = this.eggAllEND,
        airing = this.airing,
        over = this.over,
        arhive = this.arhive,
        dateEnd = this.dateEnd,
        time1 = this.time1,
        time2 = this.time2,
        time3 = this.time3,
        mode = this.mode,
        imageData = this.imageData,
    )
}

fun DomainProjectTable.toProjectTable(): ProjectTable {
    return ProjectTable(
        id = this.id,
        eggAll = this.eggAll,
        dateEnd = this.dateEnd,
        imageData = this.imageData,
        airing = this.airing,
        mode = this.mode,
        arhive = this.arhive,
        time1 = this.time1,
        titleProject = this.titleProject,
        time3 = this.time3,
        type = this.type,
        eggAllEND = this.eggAllEND,
        time2 = this.time2,
        data = this.data,
        over = this.over,
    )
}
package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.profile.ProfileTable
import com.zaroslikov.domain.models.table.profile.DomainProfileTable


fun DomainProfileTable.toProfileTable(): ProfileTable {
    return ProfileTable(id = this.id, name = this.name,)
}

fun ProfileTable.toDomainProfileTable(): DomainProfileTable {
    return DomainProfileTable(id = this.id, name = this.name,)
}
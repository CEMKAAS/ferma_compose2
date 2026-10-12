package com.zaroslikov.crm.data.room.mapper

import com.zaroslikov.crm.data.room.table.CrmClientTable
import com.zaroslikov.crm.data.room.table.CrmContactTable
import com.zaroslikov.crm.data.room.table.CrmInteractionTable
import com.zaroslikov.crm.data.room.table.CrmTaskTable
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask

fun CrmClientTable.toDomain() = DomainCrmClient(
    id = id,
    idPT = idPT,
    name = name,
    salesKey = salesKey,
    source = source,
    isVip = isVip,
    note = note,
    isFavorite = isFavorite,
    createdDate = createdDate,
    isArchived = isArchived
)

fun DomainCrmClient.toTable() = CrmClientTable(
    id = id,
    idPT = idPT,
    name = name,
    salesKey = salesKey,
    source = source,
    isVip = isVip,
    note = note,
    isFavorite = isFavorite,
    createdDate = createdDate,
    isArchived = isArchived
)

fun CrmContactTable.toDomain() = DomainCrmContact(
    id = id,
    clientId = clientId,
    type = type,
    value = value,
    isPrimary = isPrimary
)

fun DomainCrmContact.toTable() = CrmContactTable(
    id = id,
    clientId = clientId,
    type = type,
    value = value,
    isPrimary = isPrimary
)

fun CrmInteractionTable.toDomain() = DomainCrmInteraction(
    id = id,
    clientId = clientId,
    type = type,
    date = date,
    note = note
)

fun DomainCrmInteraction.toTable() = CrmInteractionTable(
    id = id,
    clientId = clientId,
    type = type,
    date = date,
    note = note
)

fun CrmTaskTable.toDomain() = DomainCrmTask(
    id = id,
    idPT = idPT,
    clientId = clientId,
    title = title,
    dueDate = dueDate,
    isDone = isDone,
    note = note
)

fun DomainCrmTask.toTable() = CrmTaskTable(
    id = id,
    idPT = idPT,
    clientId = clientId,
    title = title,
    dueDate = dueDate,
    isDone = isDone,
    note = note
)

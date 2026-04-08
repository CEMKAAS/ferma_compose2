package com.zaroslikov.domain.models.table.incubator

import kotlinx.serialization.Serializable

@Serializable
data class DomainTimeNotification(
    val id: Long = 0,
    val time: String,
    val note: String?,
    val bookmarkId: Long = 0
)
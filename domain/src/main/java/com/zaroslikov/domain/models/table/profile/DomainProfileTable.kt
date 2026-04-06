package com.zaroslikov.domain.models.table.profile

import kotlinx.serialization.Serializable

@Serializable
data class DomainProfileTable(
    val id: Long = 0,
    val name: String
)
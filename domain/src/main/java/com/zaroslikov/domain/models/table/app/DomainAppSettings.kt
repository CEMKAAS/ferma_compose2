package com.zaroslikov.domain.models.table.app

import kotlinx.serialization.Serializable

@Serializable
data class DomainAppSettings(
    val id: Long = 0,
    val lastVersionApp: String? = null,
    val currentVersionApp: String,
    val isFirstLaunch: Boolean = true
)

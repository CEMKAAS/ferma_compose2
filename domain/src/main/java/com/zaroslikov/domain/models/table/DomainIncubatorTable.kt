package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.serialization.Serializable

@Serializable
data class DomainIncubatorTable(
    val id: Long = 0,
    val title: String = "",
    val brand: String? = null,
    val model: String? = null,
    val capacity: Int = 0,
    val price: Double? = null,
    val note: String = "",
    val isAutoRotation: Boolean = false,
    val isAutoVentilation: Boolean = false,
    val currencySuffix: Suffix = Suffix.RUBLE,
    val idPT: Long = 0
)
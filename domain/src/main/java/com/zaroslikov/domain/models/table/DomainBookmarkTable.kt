package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.TypeEgg
import kotlinx.serialization.Serializable

@Serializable
data class DomainBookmark(
    val id: Long = 0,
    val title: String = "",
    val type: TypeEgg = TypeEgg.CHICKENS,
    val breed: String? = null,
    val count: Int = 0,
    val rejectedCount: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val isEarlyCompletionStatus: Boolean = true,
    val time: String = "",
    val price: Double? = null,
    val priceAll: Double? = null,
    val chickPrice: Double? = null,
    val note: String = "",
    val isAutoRotation: Boolean = false,
    val isAutoVentilation: Boolean = false,
    val isActivityBookmark: Boolean = false,
    val idPT: Long = 0
)